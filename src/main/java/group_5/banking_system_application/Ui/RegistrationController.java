package group_5.banking_system_application.Ui;

import com.google.cloud.firestore.DocumentReference;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import group_5.banking_system_application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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
    private Button btnCancel;

    @FXML
    public void handleRegistration(ActionEvent event)
    {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(tfEmail.getText())
                .setPassword(pfPassword.getText())
                .setEmailVerified(false)
                .setDisplayName(tfFirst.getText()+" "+tfLast.getText());

            try {
                UserRecord userRecord = Application.auth.createUser(request);
                System.out.println("Auth user created");

                // 2) Sanity check Firestore instance
                if (Application.firestore == null) {
                    System.out.println("❌ Application.firestore is NULL – did you initialize it?");
                    return;
                }
                DocumentReference docRef = Application.firestore.collection("users").document(userRecord.getUid());
                Map<String,Object> map = new HashMap<>();
                map.put("firstName",tfFirst.getText());
                map.put("lastName",tfLast.getText());
                map.put("email",tfEmail.getText());
                docRef.set(map).get();

            } catch (FirebaseAuthException e) {
                e.printStackTrace();
                System.out.println("❌ Error creating user in Firebase Auth");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("❌ Error writing user data to Firestore");
            }

    }
}
