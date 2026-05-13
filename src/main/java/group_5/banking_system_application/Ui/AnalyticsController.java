package group_5.banking_system_application.Ui;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AnalyticsController {

    private static final Firestore db = FirestoreClient.getFirestore();

    @FXML
    private ScrollPane root;

    @FXML
    private StackPane growthChart;

    @FXML
    private Label receivedMonthLabel;

    @FXML
    private Label sentMonthLabel;

    @FXML
    private Label netMonthLabel;

    @FXML
    private Label spendTrendLabel;

    @FXML
    private Label balanceTrendLabel;

    @FXML
    private Label insightLabel;

    private LineChart<String, Number> balanceChart;

    @FXML
    public void initialize() {
        balanceChart = BalanceChartHelper.createChart();
        balanceChart.setMinSize(0, 0);
        balanceChart.prefWidthProperty().bind(growthChart.widthProperty());
        balanceChart.prefHeightProperty().bind(growthChart.heightProperty());
        growthChart.getChildren().add(balanceChart);
        refreshAnalytics();
    }

    private void refreshAnalytics() {
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
                                .limit(200)
                                .get();

                List<QueryDocumentSnapshot> txDocs = new ArrayList<>(txFuture.get().getDocuments());
                List<QueryDocumentSnapshot> chronological = new ArrayList<>(txDocs);
                chronological.sort(Comparator.comparing(d -> FirestoreMoney.toDate(d, "createdAt")));

                ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
                double received = AccountMetrics.sumIncomingInMonth(chronological, now.getYear(), now.getMonthValue());
                double sent = AccountMetrics.sumOutgoingInMonth(chronological, now.getYear(), now.getMonthValue());
                double net = received - sent;

                AccountMetrics.Trend balanceTrend = AccountMetrics.balanceWeekTrend(balance, chronological);
                AccountMetrics.Trend spendTrend = AccountMetrics.spendingMonthTrend(chronological);
                String insight = buildInsight(received, sent, net, chronological.size());

                Platform.runLater(() -> {
                    receivedMonthLabel.setText(String.format(Locale.US, "$%,.2f", received));
                    sentMonthLabel.setText(String.format(Locale.US, "$%,.2f", sent));
                    netMonthLabel.setText(String.format(Locale.US, "$%,.2f", net));
                    applyTrendLabel(balanceTrendLabel, balanceTrend);
                    applyTrendLabel(spendTrendLabel, spendTrend);
                    insightLabel.setText(insight);
                    BalanceChartHelper.populate(balanceChart, chronological, balance);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String buildInsight(double received, double sent, double net, int txCount) {
        if (txCount == 0) {
            return "Start with a transfer or your welcome bonus to unlock balance trends.";
        }
        if (sent == 0 && received > 0) {
            return "You have not sent money this month. Incoming activity is building your balance.";
        }
        if (net >= 0) {
            return String.format(Locale.US,
                    "Net cash flow this month is positive by $%,.2f. Keep transfers aligned with your goals.",
                    net);
        }
        return String.format(Locale.US,
                "Outgoing transfers exceed incoming by $%,.2f this month. Review recent activity on Transactions.",
                -net);
    }

    private void applyTrendLabel(Label label, AccountMetrics.Trend trend) {
        if (label == null || trend == null) {
            return;
        }
        label.setText(trend.text);
        label.getStyleClass().removeAll("trend-positive", "trend-negative", "trend-neutral");
        if (trend.text.contains("No ") || trend.text.contains("Not enough") || trend.text.contains("First spending")) {
            label.getStyleClass().add("trend-neutral");
        } else if (trend.positive) {
            label.getStyleClass().add("trend-positive");
        } else {
            label.getStyleClass().add("trend-negative");
        }
    }
}
