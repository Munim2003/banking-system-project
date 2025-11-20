package group_5.banking_system_application.Ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class NavbarController {


    private MainPageController mainPageController;

    @FXML
    public void openTransactions(ActionEvent actionEvent) {
        if(mainPageController != null){
            mainPageController.setPage("/group_5/banking_system_application/FxmlLayouts/transactions.fxml");
        }
    }

    @FXML
    public void openDashboard(ActionEvent actionEvent) {

            mainPageController.setPage("/group_5/banking_system_application/FxmlLayouts/dashboard-page.fxml");

    }

    @FXML
    public void setMainPageController(MainPageController mainPageController){
        this.mainPageController=mainPageController;
    }

}
