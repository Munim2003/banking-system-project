package group_5.banking_system_application.Ui;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import group_5.banking_system_application.Application;
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
import java.util.concurrent.ExecutionException;

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
        NotificationDialog.initialize(loginButton);
    }


    public void handleRegisterLink(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/group_5/banking_system_application/FxmlLayouts/registration-page.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        double width = stage.getWidth();
        double height = stage.getHeight();
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(root);
        stage.show();
    }

    public void handleLogin(ActionEvent event) throws IOException {

        String email =  emailField.getText();
        String password = passwordField.getText();

        if(email.isEmpty() || password.isEmpty()) {
            NotificationDialog.show("Missing information", "Please enter both your email and password", false);
            NotificationDialog.shake(loginButton);
            return;
        }

        Firestore db = FirestoreClient.getFirestore();
        if(db == null) {
            NotificationDialog.show("Configuration Error", "Firestore not initialized", false);
            return;
        }
        ApiFuture<QuerySnapshot> future = db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get();

        QuerySnapshot snapShot = null;
        try {
            snapShot = future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        if(snapShot.isEmpty()) {
            NotificationDialog.show("Login Failed", "Email not found", false);
            return;
        }

        DocumentSnapshot document = snapShot.getDocuments().get(0);

        String storedPassword = (String) document.get("hashedPassword");
        if(PasswordAuthUtil.checkPassword(password,storedPassword)) {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/group_5/banking_system_application/FxmlLayouts/main-page.fxml")
            );

            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);

            scene.getStylesheets().setAll(
                    Application.class
                            .getResource("/group_5/banking_system_application/Styles/style.css")
                            .toExternalForm()
            );

            Stage stage = (Stage) scene.getWindow();
            stage.setTitle("Vaultiq Dashboard");

            stage.show();
            NotificationDialog.showWithAnchor(root,"Success!","Login Successful !", true);
        }
        else{
            NotificationDialog.show("Sorry","Invalid Password", false);
            NotificationDialog.shake(loginButton);
        }

//        String dummyEmail = emailField.getText();
//        String dummyPassword = passwordField.getText();
//        if(dummyEmail.equals("test@gmail.com") && dummyPassword.equals("test123")) {
//            Parent root = FXMLLoader.load(getClass().getResource("/group_5/banking_system_application/FXML Layouts/dashboard-page.fxml"));
//            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
//            stage.setScene(scene);
//
//            stage.show();
//            NotificationDialog.showWithAnchor(root,"Success!","Login Successful !", true);
//        }
//        else{
//            NotificationDialog.show("Sorry","Invalid Email or Password", false);
//            NotificationDialog.shake(loginButton);
//        }

    }
}
