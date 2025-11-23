package group_5.banking_system_application.Ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class BeneficiaryController {

    private String currentUserId;

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
        beneficiaries.addAll(
                new Beneficiary("B001", "John Doe", "ACC12345", "john@mail.com"),
                new Beneficiary("B002", "Sarah Khan", "ACC98765", "sarah@gmail.com"),
                new Beneficiary("B003", "Steve R", "ACC23145", "xyz@yahoo.com")
        );

        beneficiaryTable.setItems(beneficiaries);
        NotificationDialog.initialize(root);
        AddBeneficiaryDialog.initialize(root);
    }
}
