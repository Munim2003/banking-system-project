package group_5.banking_system_application.Ui;

import com.google.cloud.firestore.DocumentReference;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import group_5.banking_system_application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class RegistrationController {
    @FXML
    private TextField tfFirst;
    @FXML
    private TextField tfLast;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField pfPassword;


    @FXML
    private Button btnRegister;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnCancel;

    @FXML
    public void handleBackToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/group_5/banking_system_application/FXML Layouts/login-page.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
        stage.show();
    }


    @FXML
    public void initialize()
    {
        NotificationDialog.initialize(btnRegister);
    }

    @FXML
    public void handleRegistration(ActionEvent event) {

        String first = tfFirst.getText().trim();
        String last = tfLast.getText().trim();
        String email = tfEmail.getText().trim();
        String password = pfPassword.getText().trim();

        if(first.isEmpty() || last.isEmpty() || email.isEmpty() || password.isEmpty()){
            NotificationDialog.show("Missing Info", "Please fill all fields", false);
            NotificationDialog.shake(btnRegister);
//            btnRegister.setDisable(false);
        }


//        btnRegister.setDisable(true); // to avoid double clicking

        new Thread(() -> {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(tfEmail.getText())
                    .setPassword(pfPassword.getText())
                    .setEmailVerified(false)
                    .setDisplayName(tfFirst.getText() + " " + tfLast.getText());

            try {
                UserRecord userRecord = Application.auth.createUser(request);
                System.out.println("Auth user created");

                //not needed really since ive already tested that this application has been successfully connected to my firestore database
                if (Application.firestore == null) {
                    System.out.println("❌ Application.firestore is NULL – did you initialize it?");
                    return;
                }
                DocumentReference docRef = Application.firestore.collection("users").document(userRecord.getUid());
                Map<String, Object> map = new HashMap<>();
                map.put("firstName", tfFirst.getText());
                map.put("lastName", tfLast.getText());
                map.put("email", tfEmail.getText());
                map.put("hashedPassword", PasswordAuthUtil.hashPassword(pfPassword.getText()));
                docRef.set(map).get();

                Platform.runLater(() -> {
//                    btnRegister.setDisable(false);
                    NotificationDialog.show("Success", "Account created!", true);
                });

            } catch (FirebaseAuthException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    btnRegister.setDisable(false);
                    NotificationDialog.show("Failed", e.getMessage(), false);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    btnRegister.setDisable(false);
                    NotificationDialog.show("Failed", e.getMessage(), false);
                });
            }

        }).start();
    }
    }