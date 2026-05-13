package group_5.banking_system_application.Ui;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteBatch;
import com.google.firebase.cloud.FirestoreClient;
import group_5.banking_system_application.Application;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public final class TransferDialog {

    public static final String USER_TRANSACTIONS_SUBCOLLECTION = "transactions";

    private static Node lastAnchor = null;
    private static final Firestore db = FirestoreClient.getFirestore();

    private TransferDialog() {}

    public static void initialize(Node anyNode) {
        lastAnchor = anyNode;
    }

    public static void show(String currentUserId) {
        show(currentUserId, null);
    }

    public static void show(String currentUserId, Runnable onSuccess) {
        if (lastAnchor == null) {
            System.err.println("transferDialog.initialize() must be called first!");
            return;
        }
        showInternal(lastAnchor, currentUserId, onSuccess);
    }

    public static void showWithAnchor(Node anchor, String currentUserId, Runnable onSuccess) {
        showInternal(anchor, currentUserId, onSuccess);
    }

    private static Map<String, Object> balanceUpdate(double newBalance) {
        return Map.of("balance", newBalance);
    }

    private static void showInternal(Node anchor, String currentUserId, Runnable onSuccess) {
        if (anchor == null || anchor.getScene() == null) {
            System.out.println("transferDialog: anchor or scene is null");
            return;
        }

        Platform.runLater(() -> {
            Scene scene = anchor.getScene();
            Stage stage = (Stage) scene.getWindow();

            StackPane overlayRoot = new StackPane();
            overlayRoot.setStyle("-fx-background-color: rgba(12, 8, 24, 0.55);");
            overlayRoot.setAlignment(Pos.CENTER);

            VBox card = new VBox(12);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(18, 22, 18, 22));
            card.setMaxWidth(360);
            card.setMaxHeight(480);
            card.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #1C1430, #2B0B3A);" +
                            "-fx-background-radius: 16;" +
                            "-fx-border-color: rgba(255,255,255,0.08);" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 16;" +
                            "-fx-effect: dropshadow(gaussian, rgba(108,92,231,0.25), 22, 0.3, 0, 6);"
            );

            Label titleLabel = new Label("Transfer funds");
            titleLabel.setStyle(
                    "-fx-font-size: 18px;" +
                            "-fx-font-weight: 700;" +
                            "-fx-text-fill: #F5F6FA;"
            );

            Label subtitle = new Label("Choose a saved beneficiary to transfer funds to.");
            subtitle.setWrapText(true);
            subtitle.setStyle(
                    "-fx-font-size: 13px;" +
                            "-fx-text-fill: #B8B9C2;"
            );

            Label beneficiaryLabel = new Label("Beneficiary");
            beneficiaryLabel.setStyle(
                    "-fx-font-size: 13px;" +
                            "-fx-text-fill: #D3D4DD;"
            );

            ComboBox<String> beneficiarySelect = new ComboBox<>();
            beneficiarySelect.setPromptText("Select beneficiary");
            beneficiarySelect.setMaxWidth(Double.MAX_VALUE);
            beneficiarySelect.setStyle(
                    "-fx-background-color: #120C25;" +
                            "-fx-text-fill: #F5F6FA;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-color: rgba(255,255,255,0.10);" +
                            "-fx-border-width: 1;"
            );
            beneficiarySelect.setPrefWidth(280);

            Label amountLabel = new Label("Amount");
            amountLabel.setStyle(
                    "-fx-font-size: 13px;" +
                            "-fx-text-fill: #D3D4DD;"
            );

            TextField amountField = new TextField();
            amountField.setPromptText("0.00");
            amountField.setStyle(
                    "-fx-background-color: #120C25;" +
                            "-fx-text-fill: #F5F6FA;" +
                            "-fx-prompt-text-fill: #6B6C7A;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-color: rgba(255,255,255,0.10);" +
                            "-fx-border-width: 1;" +
                            "-fx-padding: 8 10;"
            );

            Label errorLabel = new Label();
            errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
            errorLabel.setWrapText(true);

            Button cancelBtn = new Button("Cancel");
            cancelBtn.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #B8B9C2;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: 600;" +
                            "-fx-padding: 8 12;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;"
            );
            cancelBtn.setOnMouseEntered(e ->
                    cancelBtn.setStyle(
                            "-fx-background-color: rgba(255,255,255,0.04);" +
                                    "-fx-text-fill: #E5E6ED;" +
                                    "-fx-font-size: 13px;" +
                                    "-fx-font-weight: 600;" +
                                    "-fx-padding: 8 12;" +
                                    "-fx-background-radius: 8;" +
                                    "-fx-cursor: hand;"
                    )
            );
            cancelBtn.setOnMouseExited(e ->
                    cancelBtn.setStyle(
                            "-fx-background-color: transparent;" +
                                    "-fx-text-fill: #B8B9C2;" +
                                    "-fx-font-size: 13px;" +
                                    "-fx-font-weight: 600;" +
                                    "-fx-padding: 8 12;" +
                                    "-fx-background-radius: 8;" +
                                    "-fx-cursor: hand;"
                    )
            );

            Button sendBtn = new Button("Send");
            sendBtn.setPrefWidth(110);
            sendBtn.setStyle(
                    "-fx-background-color: #6C5CE7;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: 700;" +
                            "-fx-padding: 8 14;" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;"
            );
            sendBtn.setOnMouseEntered(e ->
                    sendBtn.setStyle(
                            "-fx-background-color: #7A6CF1;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 13px;" +
                                    "-fx-font-weight: 700;" +
                                    "-fx-padding: 8 14;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-cursor: hand;"
                    )
            );
            sendBtn.setOnMouseExited(e ->
                    sendBtn.setStyle(
                            "-fx-background-color: #6C5CE7;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 13px;" +
                                    "-fx-font-weight: 700;" +
                                    "-fx-padding: 8 14;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-cursor: hand;"
                    )
            );

            HBox buttonsRow = new HBox(10, cancelBtn, sendBtn);
            buttonsRow.setAlignment(Pos.CENTER_RIGHT);

            card.getChildren().addAll(
                    titleLabel,
                    subtitle,
                    beneficiaryLabel,
                    beneficiarySelect,
                    amountLabel,
                    amountField,
                    errorLabel,
                    buttonsRow
            );

            overlayRoot.getChildren().add(card);

            Parent oldRoot = scene.getRoot();
            StackPane container = new StackPane();
            container.getChildren().addAll(oldRoot, overlayRoot);
            scene.setRoot(container);

            Runnable closeAction = () -> closeDialog(overlayRoot, card, () -> {
                container.getChildren().remove(oldRoot);
                scene.setRoot(oldRoot);
            });

            cancelBtn.setOnAction(e -> closeAction.run());

            new Thread(() -> {
                try {
                    List<String> saved = BeneficiaryService.loadSavedBeneficiaryEmails();
                    Platform.runLater(() -> {
                        beneficiarySelect.getItems().setAll(saved);
                        if (saved.isEmpty()) {
                            errorLabel.setText("Add a beneficiary before transferring funds.");
                            sendBtn.setDisable(true);
                        } else {
                            beneficiarySelect.getSelectionModel().selectFirst();
                            errorLabel.setText("");
                            sendBtn.setDisable(false);
                        }
                    });
                } catch (ExecutionException | InterruptedException ex) {
                    Platform.runLater(() -> errorLabel.setText("Could not load beneficiaries."));
                }
            }).start();

            sendBtn.setOnAction(e -> {
                String recipientEmail = beneficiarySelect.getValue();
                errorLabel.setText("");

                if (recipientEmail == null || recipientEmail.isBlank()) {
                    errorLabel.setText("Select a beneficiary.");
                    NotificationDialog.shake(beneficiarySelect);
                    return;
                }

                if (!BeneficiaryService.isSavedBeneficiary(recipientEmail, beneficiarySelect.getItems())) {
                    errorLabel.setText("Transfers are limited to saved beneficiaries.");
                    NotificationDialog.shake(beneficiarySelect);
                    return;
                }

                if (recipientEmail.equalsIgnoreCase(LoginController.globalUserEmail)) {
                    errorLabel.setText("You cannot transfer money to yourself.");
                    NotificationDialog.shake(beneficiarySelect);
                    return;
                }

                String amountRaw = amountField.getText().trim();
                if (amountRaw.isEmpty()) {
                    errorLabel.setText("Please enter an amount.");
                    NotificationDialog.shake(amountField);
                    return;
                }

                final double sendAmount;
                try {
                    sendAmount = Double.parseDouble(amountRaw);
                } catch (NumberFormatException ex) {
                    errorLabel.setText("Amount must be a valid number.");
                    NotificationDialog.shake(amountField);
                    return;
                }

                if (sendAmount <= 0) {
                    errorLabel.setText("Amount must be greater than zero.");
                    NotificationDialog.shake(amountField);
                    return;
                }

                sendBtn.setDisable(true);
                cancelBtn.setDisable(true);
                errorLabel.setText("Processing transfer…");

                new Thread(() -> {
                    try {
                        ApiFuture<QuerySnapshot> receiverFuture =
                                db.collection("users")
                                        .whereEqualTo("email", recipientEmail)
                                        .limit(1)
                                        .get();

                        List<QueryDocumentSnapshot> receiverDocs = receiverFuture.get().getDocuments();
                        if (receiverDocs.isEmpty()) {
                            Platform.runLater(() -> {
                                errorLabel.setText("Beneficiary account is no longer available.");
                                sendBtn.setDisable(false);
                                cancelBtn.setDisable(false);
                                NotificationDialog.shake(beneficiarySelect);
                            });
                            return;
                        }

                        ApiFuture<QuerySnapshot> senderFuture =
                                db.collection("users")
                                        .whereEqualTo("email", LoginController.globalUserEmail)
                                        .limit(1)
                                        .get();

                        List<QueryDocumentSnapshot> senderDocs = senderFuture.get().getDocuments();
                        if (senderDocs.isEmpty()) {
                            Platform.runLater(() -> {
                                errorLabel.setText("Signed-in user not found. Please log in again.");
                                sendBtn.setDisable(false);
                                cancelBtn.setDisable(false);
                            });
                            return;
                        }

                        QueryDocumentSnapshot senderSnap = senderDocs.getFirst();
                        QueryDocumentSnapshot receiverSnap = receiverDocs.getFirst();

                        double oldSender = FirestoreMoney.toDouble(senderSnap.get("balance"));
                        double oldReceiver = FirestoreMoney.toDouble(receiverSnap.get("balance"));

                        if (oldSender < sendAmount) {
                            Platform.runLater(() -> {
                                errorLabel.setText("Insufficient balance.");
                                sendBtn.setDisable(false);
                                cancelBtn.setDisable(false);
                                NotificationDialog.shake(amountField);
                            });
                            return;
                        }

                        double newSenderBalance = oldSender - sendAmount;
                        double newReceiverBalance = oldReceiver + sendAmount;

                        String senderUid = senderSnap.getString("userID");
                        if (senderUid == null || senderUid.isBlank()) {
                            senderUid = senderSnap.getId();
                        }
                        String receiverUid = receiverSnap.getString("userID");
                        if (receiverUid == null || receiverUid.isBlank()) {
                            receiverUid = receiverSnap.getId();
                        }

                        String senderEmail = senderSnap.getString("email");
                        String txId = UUID.randomUUID().toString();
                        Timestamp now = Timestamp.now();

                        DocumentReference senderRef = Application.firestore.collection("users").document(senderUid);
                        DocumentReference receiverRef = Application.firestore.collection("users").document(receiverUid);
                        DocumentReference outTx = senderRef.collection(USER_TRANSACTIONS_SUBCOLLECTION).document(txId);
                        DocumentReference inTx = receiverRef.collection(USER_TRANSACTIONS_SUBCOLLECTION).document(txId);

                        Map<String, Object> outRecord = new HashMap<>();
                        outRecord.put("amount", sendAmount);
                        outRecord.put("flow", Transactions.FLOW_OUT);
                        outRecord.put("counterpartyEmail", recipientEmail);
                        outRecord.put("balanceAfter", newSenderBalance);
                        outRecord.put("createdAt", now);
                        outRecord.put("status", "Completed");
                        outRecord.put("description", "Transfer to " + recipientEmail);

                        Map<String, Object> inRecord = new HashMap<>();
                        inRecord.put("amount", sendAmount);
                        inRecord.put("flow", Transactions.FLOW_IN);
                        inRecord.put("counterpartyEmail", senderEmail != null ? senderEmail : "");
                        inRecord.put("balanceAfter", newReceiverBalance);
                        inRecord.put("createdAt", now);
                        inRecord.put("status", "Completed");
                        inRecord.put("description", "Transfer from " + (senderEmail != null ? senderEmail : "Vaultiq user"));

                        WriteBatch batch = db.batch();
                        batch.set(senderRef, balanceUpdate(newSenderBalance), SetOptions.merge());
                        batch.set(receiverRef, balanceUpdate(newReceiverBalance), SetOptions.merge());
                        batch.set(outTx, outRecord);
                        batch.set(inTx, inRecord);
                        batch.commit().get();

                        Platform.runLater(() -> {
                            if (onSuccess != null) {
                                onSuccess.run();
                            }
                            NotificationDialog.show("Success", "Transfer completed.", true);
                            sendBtn.setDisable(false);
                            cancelBtn.setDisable(false);
                            errorLabel.setText("");
                            beneficiarySelect.getSelectionModel().clearSelection();
                            amountField.clear();
                            closeAction.run();
                        });

                    } catch (InterruptedException | ExecutionException ex) {
                        ex.printStackTrace();
                        Platform.runLater(() -> {
                            errorLabel.setText("Transfer failed. Please try again.");
                            sendBtn.setDisable(false);
                            cancelBtn.setDisable(false);
                        });
                    }
                }).start();
            });

            Platform.runLater(() -> {
                overlayRoot.applyCss();
                overlayRoot.layout();

                double startOffset = overlayRoot.getHeight() / 2
                        + card.getBoundsInParent().getHeight() / 2
                        + 40;

                card.setTranslateY(startOffset);
                card.setOpacity(0);

                FadeTransition overlayFade = new FadeTransition(Duration.millis(150), overlayRoot);
                overlayFade.setFromValue(0);
                overlayFade.setToValue(1);

                TranslateTransition slideUp = new TranslateTransition(Duration.millis(260), card);
                slideUp.setFromY(startOffset);
                slideUp.setToY(0);
                slideUp.setInterpolator(Interpolator.EASE_OUT);

                FadeTransition cardFade = new FadeTransition(Duration.millis(200), card);
                cardFade.setFromValue(0);
                cardFade.setToValue(1);

                new ParallelTransition(overlayFade, slideUp, cardFade).play();
            });
        });
    }

    private static void closeDialog(StackPane overlay, VBox card, Runnable onComplete) {
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(180), card);
        slideDown.setToY(60);
        slideDown.setInterpolator(Interpolator.EASE_IN);

        FadeTransition cardFade = new FadeTransition(Duration.millis(160), card);
        cardFade.setToValue(0);

        FadeTransition overlayFade = new FadeTransition(Duration.millis(180), overlay);
        overlayFade.setToValue(0);

        ParallelTransition exit = new ParallelTransition(slideDown, cardFade, overlayFade);
        exit.setOnFinished(e -> {
            if (onComplete != null) onComplete.run();
        });
        exit.play();
    }
}
