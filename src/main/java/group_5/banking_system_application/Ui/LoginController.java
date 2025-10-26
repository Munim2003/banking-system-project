package group_5.banking_system_application.Ui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    public Button loginButton;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ImageView logoView1;
    @FXML
    private ImageView logoView2;
    @FXML
    private Hyperlink hlCreate;
    @FXML
    public void initialize() {
        ErrorDialog.initialize(loginButton);
    }

    public void handleRegisterLink(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/group_5/banking_system_application/FXML Layouts/registration-page.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        double width = stage.getWidth();
        double height = stage.getHeight();
        Scene scene = new Scene(root,width,height);
        stage.setScene(scene);
        stage.show();
    }

    public void handleLogin(ActionEvent event) throws IOException {

        String dummyEmail = emailField.getText();
        String dummyPassword = passwordField.getText();
        if(dummyEmail.equals("test@gmail.com") && dummyPassword.equals("test123")) {
            Parent root = FXMLLoader.load(getClass().getResource("/group_5/banking_system_application/FXML Layouts/dashboard-page.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.show();
        }
        else{
            ErrorDialog.show("Invalid Email or Password");
            System.out.println("Invalid Email or Password");
        }

    }
}
