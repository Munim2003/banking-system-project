package group_5.banking_system_application.Ui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

public class DashboardController {

    @FXML
    private Button bAccounts;

    @FXML
    private Button bOpen;

    @FXML
    private StackPane growthChart;

    @FXML
    private VBox root;

    @FXML
    private HBox tickerContainer;

    @FXML
    private Label tickerLabel;

    // bottom navbar tip label (purple card)
    @FXML
    private Label navSuggestionLabel;

    // --- Ticker fields (center card) ---
    private List<String> tickerSuggestions;
    private Timeline tickerTimeline;
    private int currentTickerIndex = 0;

    // --- Nav-tip fields (bottom of sidebar) ---
    private List<String> navSuggestions;
    private int navSuggestionIndex = 0;
    private SequentialTransition navSuggestionTransition;

    private String currentUserId; // login ke baad set hota hai

    @FXML
    public void initialize() {
        // Dynamic welcome message
        for (var c1 : root.getChildren()) {
            if (c1 instanceof HBox) {
                for (var c2 : ((HBox) c1).getChildren()) {
                    if (c2 instanceof Label) {
                        if (((Label) c2).getText().equals("Welcome back, Munim")) {
                            ((Label) c2).setText("Welcome back, " + LoginController.userName);
                            break;
                        }
                    }
                }
            }
        }

        // Chart setup
        LineChart<String, Number> chart = createGrowthChart();
        chart.setMinSize(0, 0);
        chart.prefWidthProperty().bind(growthChart.widthProperty());
        chart.prefHeightProperty().bind(growthChart.heightProperty());
        growthChart.getChildren().add(chart);

        // Dialog initializations
        NotificationDialog.initialize(root);
        AddBeneficiaryDialog.initialize(root);

        // Ticker + nav tip
        initializeTicker();
        initializeNavSuggestion();
    }

    // ========================================================================
    // TICKER (middle of dashboard) – one full message at a time, scrolling
    // ========================================================================
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

        // Clip overflow so text stays inside the purple bar
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(tickerContainer.widthProperty());
        clip.heightProperty().bind(tickerContainer.heightProperty());
        tickerContainer.setClip(clip);

        // Start loop once scene is attached
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

            double pixelsPerSecond = 100.0; // speed
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

    // ========================================================================
    // NAVBAR BOTTOM TIP – fades in/out and cycles suggestions
    // ========================================================================
    private void initializeNavSuggestion() {
        if (navSuggestionLabel == null) {
            // maybe sidebar is a different FXML, then this is simply unused
            return;
        }

        // use same file / messages
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

        // Stop any running transition
        if (navSuggestionTransition != null) {
            navSuggestionTransition.stop();
        }

        String message = navSuggestions.get(navSuggestionIndex);
        navSuggestionIndex = (navSuggestionIndex + 1) % navSuggestions.size();

        navSuggestionLabel.setText(message);

        // Fade in → stay → fade out → then call this again
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), navSuggestionLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        PauseTransition stay = new PauseTransition(Duration.seconds(6)); // visible time

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), navSuggestionLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        navSuggestionTransition = new SequentialTransition(fadeIn, stay, fadeOut);
        navSuggestionTransition.setOnFinished(e -> playNextNavSuggestion());
        navSuggestionTransition.play();
    }

    // ========================================================================
    // Existing chart + other logic
    // ========================================================================
    private LineChart<String, Number> createGrowthChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);

        XYChart.Series<String, Number> data = new XYChart.Series<>();
        data.getData().add(new XYChart.Data<>("Today", 4));
        data.getData().add(new XYChart.Data<>("Apr 22", 8));
        data.getData().add(new XYChart.Data<>("Apr", 6));
        data.getData().add(new XYChart.Data<>("Apr 21", 10));
        data.getData().add(new XYChart.Data<>("Apr", 14));

        chart.getData().add(data);
        return chart;
    }

    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
    }

    @FXML
    private void onAddBeneficiaryClicked() {
        AddBeneficiaryDialog.show(currentUserId, this::reloadBeneficiariesOnDashboard);
    }

    private void reloadBeneficiariesOnDashboard() {
        // Firestore se beneficiaries list fetch karo aur dashboard pe show/update karo
    }
}
