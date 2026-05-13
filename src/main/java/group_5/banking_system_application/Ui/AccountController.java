package group_5.banking_system_application.Ui;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.firebase.cloud.FirestoreClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AccountController {

    private static final Firestore db = FirestoreClient.getFirestore();

    @FXML
    private ScrollPane root;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private Label emailLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label userIdLabel;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        NotificationDialog.initialize(root);
        loadAccount();
    }

    @FXML
    private void onRefresh() {
        loadAccount();
    }

    @FXML
    private void onSave() {
        String uid = LoginController.globalUserId;
        if (uid == null || uid.isBlank()) {
            NotificationDialog.show("Not signed in", "Log in again to update your profile.", false);
            return;
        }

        String first = firstNameField.getText().trim();
        String last = lastNameField.getText().trim();
        if (first.isEmpty() || last.isEmpty()) {
            NotificationDialog.show("Missing info", "First and last name are required.", false);
            NotificationDialog.shake(firstNameField);
            return;
        }

        statusLabel.setText("Saving profile…");
        new Thread(() -> {
            try {
                DocumentReference ref = db.collection("users").document(uid);
                Map<String, Object> patch = new HashMap<>();
                patch.put("firstName", first);
                patch.put("lastName", last);
                ref.set(patch, SetOptions.merge()).get();

                Platform.runLater(() -> {
                    LoginController.userName = first + " " + last;
                    statusLabel.setText("Profile saved.");
                    NotificationDialog.show("Saved", "Account details updated.", true);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    statusLabel.setText("Could not save changes.");
                    NotificationDialog.show("Save failed", "Try again in a moment.", false);
                });
            }
        }).start();
    }

    private void loadAccount() {
        String uid = LoginController.globalUserId;
        if (uid == null || uid.isBlank()) {
            statusLabel.setText("No signed-in user.");
            return;
        }

        statusLabel.setText("Loading account…");
        new Thread(() -> {
            try {
                DocumentSnapshot user = db.collection("users").document(uid).get().get();
                String first = user.getString("firstName");
                String last = user.getString("lastName");
                String email = user.getString("email");
                double balance = FirestoreMoney.toDouble(user.get("balance"));
                String storedUid = user.getString("userID");
                if (storedUid == null || storedUid.isBlank()) {
                    storedUid = user.getId();
                }

                final String displayUid = storedUid;
                Platform.runLater(() -> {
                    firstNameField.setText(first != null ? first : "");
                    lastNameField.setText(last != null ? last : "");
                    emailLabel.setText(email != null ? email : LoginController.globalUserEmail);
                    balanceLabel.setText(String.format(Locale.US, "$%,.2f", balance));
                    userIdLabel.setText(displayUid);
                    statusLabel.setText("Account details are up to date.");
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> statusLabel.setText("Could not load account details."));
            }
        }).start();
    }
}
