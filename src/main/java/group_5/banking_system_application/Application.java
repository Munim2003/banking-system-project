package group_5.banking_system_application;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import group_5.banking_system_application.Ui.AddBeneficiaryDialog;
import group_5.banking_system_application.Ui.NotificationDialog;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
        public static Firestore  firestore;
        public static FirebaseAuth auth;

    @Override
    public void start(Stage stage) throws IOException {
        FirestoreContext ctx = new FirestoreContext();
        firestore = ctx.firebase();          // this initializes FirebaseApp internally
        auth = FirebaseAuth.getInstance();
            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/group_5/banking_system_application/FxmlLayouts/login-page.fxml"));
            Parent root = fxmlLoader.load();
        NotificationDialog.initialize(root);
        AddBeneficiaryDialog.initialize(root);
            Scene scene = new Scene(root, 600, 600);

        scene.getStylesheets().add(
                Application.class
                        .getResource("/group_5/banking_system_application/Styles/style.css")
                        .toExternalForm()
        );
        stage.setTitle("Login or Sign Up");
        stage.setScene(scene);
        stage.show();
    }
}
