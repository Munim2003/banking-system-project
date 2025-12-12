package group_5.banking_system_application.Ui;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BeneficiaryController {

    private String currentUserId;
    private static final Firestore db = FirestoreClient.getFirestore();
    private static int idIndex = 0;

    //dumb function to generate random ids
    private static String genBenID() {
        return "B" + idIndex++;
    }

    @FXML
    private Node root;

    @FXML
    private void onAddBeneficiaryClicked() {
        AddBeneficiaryDialog.show(currentUserId, this::reloadBeneficiaries);
    }

    private void reloadBeneficiaries() {

    }
    @FXML
    private TextField searchField;

    @FXML
    private TableView<Beneficiary> beneficiaryTable;

    @FXML
    private TableColumn<Beneficiary, String> beneficiaryNameColumn;

    @FXML
    private TableColumn<Beneficiary, String> accountNumberColumn;

    @FXML
    private TableColumn<Beneficiary, String> beneficiaryIdColumn;

    @FXML
    private TableColumn<Beneficiary, String> emailColumn;

    private final ObservableList<Beneficiary> beneficiaries = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // mapping columns to Beneficiary class fields
        beneficiaryNameColumn.setCellValueFactory(new PropertyValueFactory<>("beneficiaryName"));
        accountNumberColumn.setCellValueFactory(new PropertyValueFactory<>("accountNum"));
        beneficiaryIdColumn.setCellValueFactory(new PropertyValueFactory<>("beneficiaryId"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Dummy data for now
        /*
        beneficiaries.addAll(
                new Beneficiary("B001", "John Doe", "ACC12345", "john@mail.com"),
                new Beneficiary("B002", "Sarah Khan", "ACC98765", "sarah@gmail.com"),
                new Beneficiary("B003", "Steve R", "ACC23145", "xyz@yahoo.com")
        );

         */
        ApiFuture<QuerySnapshot> userFuture =
                db.collection("users")
                        .whereEqualTo("email", LoginController.globalUserEmail)
                        .get();
        try {
            List<QueryDocumentSnapshot> userDoc = userFuture.get().getDocuments();
            var userQuery = userDoc.getFirst();
            if(userQuery.get("beneficiaries") != null) {
                var bens = (ArrayList<String>) userQuery.get(("beneficiaries"));
                for (var b : bens) {
                    ApiFuture<QuerySnapshot> bFuture =
                            db.collection("users")
                                    .whereEqualTo("email", b)
                                    .get();
                    List<QueryDocumentSnapshot> bDoc = bFuture.get().getDocuments();
                    var bQuery = bDoc.getFirst();
                    beneficiaries.add(new Beneficiary(genBenID(),bQuery.get("firstName") + " "
                    + bQuery.get("lastName"),"",b));
                }
            }
        }catch(ExecutionException | InterruptedException ee) {

        }

        beneficiaryTable.setItems(beneficiaries);
        NotificationDialog.initialize(root);
        AddBeneficiaryDialog.initialize(root);
    }

    // should be search field callback
    public void search(ActionEvent e) {
        if (searchField.getText().trim().equals("")) {
            beneficiaryTable.setItems(beneficiaries);
        } else {
            ObservableList<Beneficiary> otherList = FXCollections.observableArrayList();
            for (var b : beneficiaries) {
                if (b.getBeneficiaryName().toUpperCase().trim().startsWith(searchField.getText().toUpperCase())) {
                    otherList.add(b);
                }
            }
            beneficiaryTable.setItems(otherList);
        }
    }
}
