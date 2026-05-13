package group_5.banking_system_application;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FirestoreContext {

    private static InputStream openServiceAccountStream() throws IOException {
        String envPath = System.getenv("FIREBASE_KEY_JSON_PATH");
        if (envPath != null && !envPath.isBlank()) {
            return new FileInputStream(envPath);
        }
        InputStream fromClasspath = FirestoreContext.class.getResourceAsStream("/firebase/key.json");
        if (fromClasspath != null) {
            return fromClasspath;
        }
        Path devPath = Path.of("src/main/resources/firebase/key.json");
        if (Files.isReadable(devPath)) {
            return Files.newInputStream(devPath);
        }
        throw new FileNotFoundException(
                "Firebase credentials missing. Add src/main/resources/firebase/key.json (gitignored), "
                        + "or set FIREBASE_KEY_JSON_PATH to your service account JSON file.");
    }

    public Firestore firebase() {
        try (InputStream serviceAccount = openServiceAccountStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId("vaultiq-banking-system")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return FirestoreClient.getFirestore();
    }


}
