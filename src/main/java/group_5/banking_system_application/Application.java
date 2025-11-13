package group_5.banking_system_application;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import javafx.fxml.FXMLLoader;
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
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/group_5/banking_system_application/FXML Layouts/login-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        stage.setTitle("Login or Sign Up");
        stage.setScene(scene);
        stage.show();
    }
}
