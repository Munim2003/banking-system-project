package group_5.banking_system_application.Ui;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

final class BeneficiaryService {

    private static final Firestore db = FirestoreClient.getFirestore();

    private BeneficiaryService() {}

    static List<String> loadSavedBeneficiaryEmails() throws ExecutionException, InterruptedException {
        if (LoginController.globalUserEmail == null || LoginController.globalUserEmail.isBlank()) {
            return Collections.emptyList();
        }

        ApiFuture<QuerySnapshot> userFuture = db.collection("users")
                .whereEqualTo("email", LoginController.globalUserEmail)
                .limit(1)
                .get();

        List<QueryDocumentSnapshot> userDoc = userFuture.get().getDocuments();
        if (userDoc.isEmpty()) {
            return Collections.emptyList();
        }

        Object raw = userDoc.getFirst().get("beneficiaries");
        if (!(raw instanceof List<?> list)) {
            return Collections.emptyList();
        }

        List<String> emails = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof String email && !email.isBlank()) {
                emails.add(email.trim());
            }
        }
        return emails;
    }

    static boolean isSavedBeneficiary(String email, List<String> saved) {
        if (email == null || email.isBlank()) {
            return false;
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        for (String savedEmail : saved) {
            if (savedEmail != null && savedEmail.trim().toLowerCase(Locale.ROOT).equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    static boolean emailExistsInDatabase(String email) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("users")
                .whereEqualTo("email", email.trim())
                .limit(1)
                .get();
        return !future.get().getDocuments().isEmpty();
    }
}
