package group_5.banking_system_application.Ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Button bSignout;

    @FXML
    private Button bAccounts;

    @FXML
    private Button bOpen;

// takes user back to login after signout
    public void handleSignOut(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/group_5/banking_system_application/FXML Layouts/login-page.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        double width = stage.getWidth();
        double height = stage.getHeight();
        Scene scene = new Scene(root,width,height);
        stage.setScene(scene);
        stage.show();
    }
}
