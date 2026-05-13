package group_5.banking_system_application.Ui;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class TransactionsController {
    private static final Firestore db = FirestoreClient.getFirestore();
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("MMM d, yyyy HH:mm", Locale.US);

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Transactions> transactionsTable;

    @FXML
    private TableColumn<Transactions, java.util.Date> dateColumn;

    @FXML
    private TableColumn<Transactions, String> descriptionColumn;

    @FXML
    private TableColumn<Transactions, Double> amountColumn;

    @FXML
    private TableColumn<Transactions, Double> balanceColumn;

    @FXML
    private TableColumn<Transactions, String> statusColumn;

    @FXML
    private Label monthSpentLabel;

    @FXML
    private Label monthReceivedLabel;

    @FXML
    private Label monthSpentTrendLabel;

    private final ObservableList<Transactions> transactions = FXCollections.observableArrayList();
    private FilteredList<Transactions> filtered;

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(java.util.Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(DATE_FMT.format(item));
                }
            }
        });

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                TableView<Transactions> tv = getTableView();
                int idx = getIndex();
                Transactions row = (tv != null && idx >= 0 && idx < tv.getItems().size())
                        ? tv.getItems().get(idx)
                        : null;
                boolean in = row != null && Transactions.FLOW_IN.equals(row.getFlow());
                setText((in ? "+" : "-") + String.format(Locale.US, "$%.2f", amount));
                setStyle(in
                        ? "-fx-text-fill: #34D399; -fx-font-weight: 700;"
                        : "-fx-text-fill: #F87171; -fx-font-weight: 700;");
            }
        });

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double bal, boolean empty) {
                super.updateItem(bal, empty);
                if (empty || bal == null) {
                    setText(null);
                } else {
                    setText(String.format(Locale.US, "$%.2f", bal));
                }
            }
        });

        filtered = new FilteredList<>(transactions, t -> true);
        transactionsTable.setItems(filtered);

        if (comboBox != null) {
            comboBox.getItems().setAll("All", "Incoming only", "Outgoing only");
            comboBox.getSelectionModel().selectFirst();
            comboBox.valueProperty().addListener((o, oldV, newV) -> applyFilters());
        }
        if (searchField != null) {
            searchField.textProperty().addListener((o, oldV, newV) -> applyFilters());
        }

        loadFromFirestore();
    }

    private void applyFilters() {
        String q = searchField != null ? searchField.getText().trim().toLowerCase(Locale.ROOT) : "";
        String filter = comboBox != null && comboBox.getValue() != null ? comboBox.getValue() : "All";

        filtered.setPredicate(t -> {
            if ("Incoming only".equals(filter) && !Transactions.FLOW_IN.equals(t.getFlow())) {
                return false;
            }
            if ("Outgoing only".equals(filter) && !Transactions.FLOW_OUT.equals(t.getFlow())) {
                return false;
            }
            if (q.isEmpty()) {
                return true;
            }
            String desc = t.getDescription() != null ? t.getDescription().toLowerCase(Locale.ROOT) : "";
            String ben = t.getBeneficiary() != null ? t.getBeneficiary().toLowerCase(Locale.ROOT) : "";
            return desc.contains(q) || ben.contains(q);
        });
    }

    private void loadFromFirestore() {
        String uid = LoginController.globalUserId;
        if (uid == null || uid.isBlank()) {
            return;
        }

        new Thread(() -> {
            try {
                ApiFuture<com.google.cloud.firestore.QuerySnapshot> future =
                        db.collection("users")
                                .document(uid)
                                .collection(TransferDialog.USER_TRANSACTIONS_SUBCOLLECTION)
                                .orderBy("createdAt", Query.Direction.DESCENDING)
                                .limit(200)
                                .get();

                List<QueryDocumentSnapshot> docs = future.get().getDocuments();
                List<Transactions> rows = new ArrayList<>();
                for (QueryDocumentSnapshot d : docs) {
                    rows.add(Transactions.fromFirestore(d));
                }

                List<QueryDocumentSnapshot> chronological = new ArrayList<>(docs);
                chronological.sort((a, b) -> FirestoreMoney.toDate(a, "createdAt")
                        .compareTo(FirestoreMoney.toDate(b, "createdAt")));

                ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
                double spent = AccountMetrics.sumOutgoingInMonth(chronological, now.getYear(), now.getMonthValue());
                double received = AccountMetrics.sumIncomingInMonth(chronological, now.getYear(), now.getMonthValue());
                AccountMetrics.Trend spendTrend = AccountMetrics.spendingMonthTrend(chronological);

                Platform.runLater(() -> {
                    transactions.setAll(rows);
                    applyFilters();
                    if (monthSpentLabel != null) {
                        monthSpentLabel.setText(String.format(Locale.US, "$%,.2f", spent));
                    }
                    if (monthReceivedLabel != null) {
                        monthReceivedLabel.setText(String.format(Locale.US, "$%,.2f", received));
                    }
                    applyTrendLabel(monthSpentTrendLabel, spendTrend);
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void applyTrendLabel(Label label, AccountMetrics.Trend trend) {
        if (label == null || trend == null) {
            return;
        }
        label.setText(trend.text);
        label.getStyleClass().removeAll("trend-positive", "trend-negative", "trend-neutral");
        if (trend.text.contains("No ") || trend.text.contains("Not enough") || trend.text.contains("First spending")) {
            label.getStyleClass().add("trend-neutral");
        } else if (trend.positive) {
            label.getStyleClass().add("trend-positive");
        } else {
            label.getStyleClass().add("trend-negative");
        }
    }
}
