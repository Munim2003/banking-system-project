package group_5.banking_system_application.Ui;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DashboardController {

    private static final Firestore db = FirestoreClient.getFirestore();

    @FXML
    private StackPane growthChart;

    @FXML
    private VBox root;

    @FXML
    private HBox tickerContainer;

    @FXML
    private Label tickerLabel;

    @FXML
    private Label navSuggestionLabel;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label totalBalanceLabel;

    @FXML
    private Label monthlySpendLabel;

    @FXML
    private Label activeAccountsLabel;

    @FXML
    private VBox recentActivityContainer;

    private List<String> tickerSuggestions;
    private Timeline tickerTimeline;
    private int currentTickerIndex = 0;

    private List<String> navSuggestions;
    private int navSuggestionIndex = 0;
    private SequentialTransition navSuggestionTransition;

    private String currentUserId;

    private LineChart<String, Number> balanceChart;
    private final SimpleDateFormat chartDayFormat = new SimpleDateFormat("MMM d", Locale.US);
    private final SimpleDateFormat activityFormat = new SimpleDateFormat("MMM d, HH:mm", Locale.US);

    @FXML
    public void initialize() {
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome back, " + LoginController.userName);
            welcomeLabel.setWrapText(true);
            welcomeLabel.maxWidthProperty().bind(root.widthProperty());
        }
        if (activeAccountsLabel != null) {
            activeAccountsLabel.setText("1");
        }

        balanceChart = createEmptyGrowthChart();
        balanceChart.setMinSize(0, 0);
        balanceChart.prefWidthProperty().bind(growthChart.widthProperty());
        balanceChart.prefHeightProperty().bind(growthChart.heightProperty());
        growthChart.getChildren().add(balanceChart);

        NotificationDialog.initialize(root);
        AddBeneficiaryDialog.initialize(root);
        TransferDialog.initialize(root);

        initializeTicker();
        initializeNavSuggestion();

        refreshDashboardFromFirestore();
    }

    private LineChart<String, Number> createEmptyGrowthChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("—", 0));
        chart.getData().add(series);
        return chart;
    }

    public void refreshDashboardFromFirestore() {
        String uid = LoginController.globalUserId;
        if (uid == null || uid.isBlank()) {
            return;
        }

        new Thread(() -> {
            try {
                DocumentSnapshot user = db.collection("users").document(uid).get().get();
                double balance = FirestoreMoney.toDouble(user.get("balance"));

                ApiFuture<com.google.cloud.firestore.QuerySnapshot> txFuture =
                        db.collection("users")
                                .document(uid)
                                .collection(TransferDialog.USER_TRANSACTIONS_SUBCOLLECTION)
                                .orderBy("createdAt", Query.Direction.DESCENDING)
                                .limit(120)
                                .get();

                List<QueryDocumentSnapshot> txDocs = new ArrayList<>(txFuture.get().getDocuments());

                double monthSpend = computeMonthOutgoingSpend(txDocs);

                List<QueryDocumentSnapshot> forChart = new ArrayList<>(txDocs);
                forChart.sort(Comparator.comparing(d -> FirestoreMoney.toDate(d, "createdAt")));

                List<Transactions> recentRows = new ArrayList<>();
                for (int i = 0; i < Math.min(5, txDocs.size()); i++) {
                    recentRows.add(Transactions.fromFirestore(txDocs.get(i)));
                }

                final String displayName = LoginController.deriveDisplayName(user);

                Platform.runLater(() -> {
                    LoginController.userName = displayName;
                    if (welcomeLabel != null) {
                        welcomeLabel.setText("Welcome back, " + displayName);
                    }
                    totalBalanceLabel.setText(String.format(Locale.US, "$%,.2f", balance));
                    monthlySpendLabel.setText(String.format(Locale.US, "$%,.2f", monthSpend));
                    rebuildChart(forChart, balance);
                    rebuildRecentActivity(recentRows);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private double computeMonthOutgoingSpend(List<QueryDocumentSnapshot> txDocs) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        int y = now.getYear();
        int m = now.getMonthValue();
        double sum = 0;
        for (QueryDocumentSnapshot d : txDocs) {
            if (!Transactions.FLOW_OUT.equals(d.getString("flow"))) {
                continue;
            }
            Instant inst = FirestoreMoney.toDate(d, "createdAt").toInstant();
            ZonedDateTime z = inst.atZone(ZoneId.systemDefault());
            if (z.getYear() == y && z.getMonthValue() == m) {
                sum += FirestoreMoney.toDouble(d.get("amount"));
            }
        }
        return sum;
    }

    private void rebuildChart(List<QueryDocumentSnapshot> chronological, double currentBalance) {
        XYChart.Series<String, Number> series = balanceChart.getData().getFirst();
        series.getData().clear();

        if (chronological.isEmpty()) {
            series.getData().add(new XYChart.Data<>("Today", currentBalance));
            return;
        }

        int maxPoints = 12;
        int from = Math.max(0, chronological.size() - maxPoints);
        for (int i = from; i < chronological.size(); i++) {
            QueryDocumentSnapshot d = chronological.get(i);
            String day = chartDayFormat.format(FirestoreMoney.toDate(d, "createdAt"));
            double bal = FirestoreMoney.toDouble(d.get("balanceAfter"));
            series.getData().add(new XYChart.Data<>(day, bal));
        }
        if (series.getData().isEmpty()) {
            series.getData().add(new XYChart.Data<>("Today", currentBalance));
        }
    }

    private void rebuildRecentActivity(List<Transactions> rows) {
        recentActivityContainer.getChildren().clear();
        if (rows.isEmpty()) {
            Label empty = new Label("No transactions yet. Use Transfer funds to move money between Vaultiq accounts.");
            empty.setWrapText(true);
            empty.setStyle("-fx-text-fill: #B8B9C2; -fx-font-size: 13px;");
            recentActivityContainer.getChildren().add(empty);
            return;
        }
        for (Transactions t : rows) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label when = new Label(activityFormat.format(t.getDate()));
            when.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 12px;");
            when.setMinWidth(130);

            Label desc = new Label(t.getDescription());
            desc.setStyle("-fx-text-fill: #E5E7EB; -fx-font-size: 13px;");
            desc.setWrapText(true);
            HBox.setHgrow(desc, javafx.scene.layout.Priority.ALWAYS);

            String sign = Transactions.FLOW_IN.equals(t.getFlow()) ? "+" : "-";
            Label amt = new Label(sign + String.format(Locale.US, "$%.2f", t.getAmount()));
            boolean in = Transactions.FLOW_IN.equals(t.getFlow());
            amt.setStyle(in
                    ? "-fx-text-fill: #34D399; -fx-font-weight: 700; -fx-font-size: 13px;"
                    : "-fx-text-fill: #F87171; -fx-font-weight: 700; -fx-font-size: 13px;");

            row.getChildren().addAll(when, desc, amt);
            recentActivityContainer.getChildren().add(row);
        }
    }

    private void initializeTicker() {
        if (tickerLabel == null || tickerContainer == null) {
            return;
        }

        tickerSuggestions = BankingSuggestionService.getShuffledSuggestions();

        if (tickerSuggestions == null || tickerSuggestions.isEmpty()) {
            tickerLabel.setText("Welcome to Vaultiq Banking!");
        } else {
            tickerLabel.setText(tickerSuggestions.get(0));
        }

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(tickerContainer.widthProperty());
        clip.heightProperty().bind(tickerContainer.heightProperty());
        tickerContainer.setClip(clip);

        tickerContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(this::startTickerLoop);
            }
        });

        if (tickerContainer.getScene() != null) {
            Platform.runLater(this::startTickerLoop);
        }
    }

    private void startTickerLoop() {
        if (tickerSuggestions == null || tickerSuggestions.isEmpty()) {
            return;
        }
        currentTickerIndex = 0;
        playNextTickerMessage();
    }

    private void playNextTickerMessage() {
        if (tickerSuggestions == null || tickerSuggestions.isEmpty()) {
            return;
        }

        if (tickerTimeline != null) {
            tickerTimeline.stop();
        }

        String message = tickerSuggestions.get(currentTickerIndex);
        tickerLabel.setText(message);

        Platform.runLater(() -> {
            tickerLabel.applyCss();
            tickerLabel.layout();
            tickerContainer.applyCss();
            tickerContainer.layout();

            double textWidth = tickerLabel.getBoundsInLocal().getWidth();
            if (textWidth < 10) {
                textWidth = tickerLabel.getText().length() * 8.0;
            }

            double containerWidth = tickerContainer.getWidth();
            if (containerWidth <= 0) {
                containerWidth = 600.0;
            }

            final double gap = 40.0;
            double startX = containerWidth;
            double endX = -textWidth - gap;
            double distance = startX - endX;

            double pixelsPerSecond = 100.0;
            double durationSeconds = distance / pixelsPerSecond;

            tickerLabel.setTranslateX(startX);

            tickerTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(0),
                            new KeyValue(tickerLabel.translateXProperty(), startX)),
                    new KeyFrame(Duration.seconds(durationSeconds),
                            new KeyValue(tickerLabel.translateXProperty(), endX)));

            tickerTimeline.setCycleCount(1);
            tickerTimeline.setOnFinished(e -> {
                currentTickerIndex = (currentTickerIndex + 1) % tickerSuggestions.size();
                playNextTickerMessage();
            });

            tickerTimeline.play();
        });
    }

    private void initializeNavSuggestion() {
        if (navSuggestionLabel == null) {
            return;
        }

        navSuggestions = BankingSuggestionService.getShuffledSuggestions();

        if (navSuggestions == null || navSuggestions.isEmpty()) {
            navSuggestionLabel.setText("Security: Log out of banking apps when not in use");
            return;
        }

        navSuggestionIndex = 0;
        startNavSuggestionLoop();
    }

    private void startNavSuggestionLoop() {
        if (navSuggestions == null || navSuggestions.isEmpty() || navSuggestionLabel == null) {
            return;
        }
        playNextNavSuggestion();
    }

    private void playNextNavSuggestion() {
        if (navSuggestions == null || navSuggestions.isEmpty() || navSuggestionLabel == null) {
            return;
        }

        if (navSuggestionTransition != null) {
            navSuggestionTransition.stop();
        }

        String message = navSuggestions.get(navSuggestionIndex);
        navSuggestionIndex = (navSuggestionIndex + 1) % navSuggestions.size();

        navSuggestionLabel.setText(message);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), navSuggestionLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        PauseTransition stay = new PauseTransition(Duration.seconds(6));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), navSuggestionLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        navSuggestionTransition = new SequentialTransition(fadeIn, stay, fadeOut);
        navSuggestionTransition.setOnFinished(e -> playNextNavSuggestion());
        navSuggestionTransition.play();
    }

    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
    }

    @FXML
    private void onAddBeneficiaryClicked() {
        String uid = currentUserId != null ? currentUserId : LoginController.globalUserId;
        AddBeneficiaryDialog.show(uid, this::refreshDashboardFromFirestore);
    }

    @FXML
    private void onTransferClicked() {
        String uid = currentUserId != null ? currentUserId : LoginController.globalUserId;
        TransferDialog.show(uid, this::refreshDashboardFromFirestore);
    }
}
