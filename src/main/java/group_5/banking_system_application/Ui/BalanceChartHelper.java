package group_5.banking_system_application.Ui;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

final class BalanceChartHelper {

    private static final SimpleDateFormat POINT_FMT = new SimpleDateFormat("MMM d", Locale.US);

    private BalanceChartHelper() {}

    static LineChart<String, Number> createChart() {
        LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setHorizontalGridLinesVisible(true);
        chart.setVerticalGridLinesVisible(false);
        chart.getStyleClass().add("balance-growth-chart");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        chart.getData().add(series);
        return chart;
    }

    static void populate(LineChart<String, Number> chart, List<QueryDocumentSnapshot> chronological, double currentBalance) {
        XYChart.Series<String, Number> series = chart.getData().getFirst();
        series.getData().clear();

        if (chronological.isEmpty()) {
            series.getData().add(new XYChart.Data<>("Today", currentBalance));
            return;
        }

        for (QueryDocumentSnapshot d : chronological) {
            String label = POINT_FMT.format(FirestoreMoney.toDate(d, "createdAt"));
            double bal = FirestoreMoney.toDouble(d.get("balanceAfter"));
            series.getData().add(new XYChart.Data<>(label, bal));
        }

        QueryDocumentSnapshot last = chronological.get(chronological.size() - 1);
        double lastRecorded = FirestoreMoney.toDouble(last.get("balanceAfter"));
        if (Math.abs(lastRecorded - currentBalance) > 0.009) {
            series.getData().add(new XYChart.Data<>("Now", currentBalance));
        }
    }
}
