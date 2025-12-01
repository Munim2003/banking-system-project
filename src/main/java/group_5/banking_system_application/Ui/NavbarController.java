package group_5.banking_system_application.Ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class NavbarController {
    @FXML private Button btnHome;
    @FXML private Button btnAccount;
    @FXML private Button btnTransactions;
    @FXML private Button btnAnalytics;
    @FXML private Button btnBeneficiaries;

    private Button activeButton;


    private MainPageController mainPageController;


    private void setActiveButton(Button button){
        if(activeButton != null){
            activeButton.getStyleClass().remove("nav-item-active");
        }
        activeButton = button;

        if(!button.getStyleClass().contains("nav-item-active")){
            button.getStyleClass().add("nav-item-active");
        }
        System.out.println("Active button classes: " + button.getText() + " -> " + button.getStyleClass());

    }

    @FXML
    public void openTransactions(ActionEvent actionEvent) {
        if(mainPageController != null){
            mainPageController.setPage("/group_5/banking_system_application/FxmlLayouts/transactions.fxml");
            setActiveButton(btnTransactions);
        }
    }

    @FXML
    public void openAnalytics(ActionEvent actionEvent) {
        if(mainPageController != null){
            mainPageController.setPage("/group_5/banking_system_application/FxmlLayouts/analytics-page.fxml");
            setActiveButton(btnAnalytics);
        }
    }

    @FXML
    public void openDashboard(ActionEvent actionEvent) {

        mainPageController.setPage("/group_5/banking_system_application/FxmlLayouts/dashboard-page.fxml");
        setActiveButton(btnHome);
    }

    @FXML
    public void openAccounts(ActionEvent actionEvent) {

    }

    @FXML void openBeneficiaries(ActionEvent actionEvent) {
        mainPageController.setPage("/group_5/banking_system_application/FxmlLayouts/Beneficiaries.fxml");
        setActiveButton(btnBeneficiaries);
    }

    @FXML
    public void setMainPageController(MainPageController mainPageController){
        this.mainPageController=mainPageController;
        setActiveButton(btnHome);
    }

}
