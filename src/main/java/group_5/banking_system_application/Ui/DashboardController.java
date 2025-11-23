package group_5.banking_system_application.Ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

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
    public void initialize() {
        LineChart<String, Number> chart = createGrowthChart();

        chart.setMinSize(0, 0);
        chart.prefWidthProperty().bind(growthChart.widthProperty());
        chart.prefHeightProperty().bind(growthChart.heightProperty());

        growthChart.getChildren().add(chart);

        NotificationDialog.initialize(root);
        AddBeneficiaryDialog.initialize(root);

    }

//    public void handleSignOut(ActionEvent e) throws IOException {
//        Parent root = FXMLLoader.load(getClass().getResource("/group_5/banking_system_application/FxmlLayouts/login-page.fxml"));
//        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
//        double width = stage.getWidth();
//        double height = stage.getHeight();
//        Scene scene = new Scene(root,width,height);
//        stage.setScene(scene);
//        stage.show();
//    }

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

    private String currentUserId; // login ke baad set hota hai

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
