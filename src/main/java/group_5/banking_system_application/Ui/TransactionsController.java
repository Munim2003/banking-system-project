package group_5.banking_system_application.Ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Date;

public class TransactionsController {
    @FXML
    private ComboBox<String> comboBox;


    @FXML
    private TableView<Transactions> transactionsTable;

    @FXML
    private TableColumn<Transactions, Date> dateColumn;

    @FXML
    private TableColumn<Transactions, String> descriptionColumn;

    @FXML
    private TableColumn<Transactions, Double> amountColumn;

    @FXML
    private TableColumn<Transactions, Double> balanceColumn;

    @FXML
    private TableColumn<Transactions, String> statusColumn;

    private final ObservableList<Transactions> transactions = FXCollections.observableArrayList();


    @FXML
    public void initialize(){
//        ObservableList<String> items = FXCollections.observableArrayList("Date","Amount");
//        comboBox.setItems(items);

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("Amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("Status"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("Balance"));

        transactions.addAll(
                new Transactions("Starbucks", 8.75, "Completed", new Date(), "Starbucks Corp", 1291.25),
                new Transactions("Salary", 2000.00, "Completed", new Date(), "Employer Inc", 3291.25),
                new Transactions("Netflix", 15.99, "Completed", new Date(), "Netflix Inc", 3275.26),
        new Transactions("Starbucks", 8.75, "Completed", new Date(), "Starbucks Corp", 1291.25),
                new Transactions("Salary", 2000.00, "Completed", new Date(), "Employer Inc", 3291.25),
                new Transactions("Netflix", 15.99, "Completed", new Date(), "Netflix Inc", 3275.26)
        );

        transactionsTable.setItems(transactions);
    }
}

