package group_5.banking_system_application.Ui;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public final class TransferDialog {

    private static Node lastAnchor = null;
    private static final Firestore db = FirestoreClient.getFirestore();

    private TransferDialog() {}

    public static void initialize(Node anyNode) {
        lastAnchor = anyNode;
    }

    // basic show, without callback
    public static void show(String currentUserId) {
        show(currentUserId, null);
    }

    // show with callback (e.g. refresh table)
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

            // ---------- CARD ----------
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

            // ---------- TITLE ----------
            Label titleLabel = new Label("Transfer funds");
            titleLabel.setStyle(
                    "-fx-font-size: 18px;" +
                            "-fx-font-weight: 700;" +
                            "-fx-text-fill: #F5F6FA;"
            );

            Label subtitle = new Label("Enter the email of an existing Vaultiq user to transfer funds to.");
            subtitle.setWrapText(true);
            subtitle.setStyle(
                    "-fx-font-size: 13px;" +
                            "-fx-text-fill: #B8B9C2;"
            );

            // ---------- FORM FIELD ----------
            Label emailLabel = new Label("user Email");
            emailLabel.setStyle(
                    "-fx-font-size: 13px;" +
                            "-fx-text-fill: #D3D4DD;"
            );

            TextField emailField = new TextField();
            emailField.setPromptText("email@example.com");
            emailField.setStyle(
                    "-fx-background-color: #120C25;" +
                            "-fx-text-fill: #F5F6FA;" +
                            "-fx-prompt-text-fill: #6B6C7A;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-color: rgba(255,255,255,0.10);" +
                            "-fx-border-width: 1;" +
                            "-fx-padding: 8 10;"
            );
            emailField.setPrefWidth(280);
            // new stuff for balance transfer
            TextField amountField = new TextField();
            amountField.setPromptText("1000.0");
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
        // end new stuff
            Label errorLabel = new Label();
            errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");

            // ---------- BUTTONS ----------
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

            Button addBtn = new Button("Add");
            addBtn.setPrefWidth(110);
            addBtn.setStyle(
                    "-fx-background-color: #6C5CE7;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: 700;" +
                            "-fx-padding: 8 14;" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;"
            );
            addBtn.setOnMouseEntered(e ->
                    addBtn.setStyle(
                            "-fx-background-color: #7A6CF1;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 13px;" +
                                    "-fx-font-weight: 700;" +
                                    "-fx-padding: 8 14;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-cursor: hand;"
                    )
            );
            addBtn.setOnMouseExited(e ->
                    addBtn.setStyle(
                            "-fx-background-color: #6C5CE7;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 13px;" +
                                    "-fx-font-weight: 700;" +
                                    "-fx-padding: 8 14;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-cursor: hand;"
                    )
            );

            HBox buttonsRow = new HBox(10, cancelBtn, addBtn);
            buttonsRow.setAlignment(Pos.CENTER_RIGHT);

            // assemble card
            card.getChildren().addAll(
                    titleLabel,
                    subtitle,
                    emailLabel,
                    emailField,
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

            // NOTE: yahan overlay pe click se close NAAHIN kar raha,
            // taki galti se click karke form band na ho.
            // agar chaho to NotificationDialog jaisa behavior add kar sakte ho.

            // ---------- ADD BUTTON ACTION ----------
            addBtn.setOnAction(e -> {
                String email = emailField.getText().trim();
                errorLabel.setText("");

                if (email.isEmpty()) {
                    errorLabel.setText("Please enter user email.");
                    NotificationDialog.shake(emailField);
                    return;
                }

                if(email.equals(LoginController.globalUserEmail)) {
                    errorLabel.setText("you cannot give money to yourself silly!");
                    NotificationDialog.shake(emailField);
                    return;
                }

                // background thread for Firestore
                addBtn.setDisable(true);
                cancelBtn.setDisable(true);
                errorLabel.setText("Checking user...");

              //  new Thread(() -> {
                    try {
                        //----
                        ApiFuture<QuerySnapshot> future =
                                db.collection("users")
                                        .whereEqualTo("email", email)
                                        .get();

                        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

                        if (docs.isEmpty()) {
                            Platform.runLater(() -> {
                                errorLabel.setText("No user found with this email.");
                                addBtn.setDisable(false);
                                cancelBtn.setDisable(false);
                                NotificationDialog.shake(emailField);
                            });
                            return;
                        }

                        // new code for transfer balances

                        ApiFuture<QuerySnapshot> userFuture =
                                db.collection("users")
                                        .whereEqualTo("email", LoginController.globalUserEmail)
                                        .get();

                        List<QueryDocumentSnapshot> userDoc = userFuture.get().getDocuments();
                        var userQuery = userDoc.getFirst();
                        var recieverQuery = docs.getFirst();
                        double sendAmount;
                        try {
                            sendAmount = Double.parseDouble(amountField.getText());
                        }catch(InputMismatchException inputException) {
                            errorLabel.setText("input must be valid number");
                            NotificationDialog.shake(emailField);
                            return;

                        }

                        Number senderFirebaseNum = (Number)userQuery.get("balance");
                        double oldSenderBalance = senderFirebaseNum.doubleValue();
                        System.out.println(oldSenderBalance);
                        System.out.println(email);
                        System.out.println((Number)recieverQuery.get("balance"));
                        double newSenderBalance = oldSenderBalance-sendAmount;
                        Number recieverFirebaseNum = (Number)recieverQuery.get("balance");
                        double newRecieverBalance = recieverFirebaseNum.doubleValue() +sendAmount;

                        // now to update both sender and reciever
                        {
                            DocumentReference docRef = Application.firestore.collection("users").document((String) userQuery.get("userID"));
                            var map = new HashMap<String, Object>();
                            map.put("userID", userQuery.get("userID")); // to access user more easily
                            map.put("firstName", userQuery.get("firstName"));
                            map.put("lastName", userQuery.get("lastName"));
                            map.put("email", userQuery.get("email"));
                            map.put("hashedPassword", userQuery.get("hashedPassword"));
                            var benArray = new ArrayList<String>();
                            if (userQuery.get("beneficiaries") != null) {
                                for (var i : (ArrayList<String>) userQuery.get("beneficiaries")) {
                                    benArray.add(i);
                                }
                            }
                            map.put("beneficiaries", benArray);
                            map.put("balance", newSenderBalance);
                            docRef.set(map).get();
                        }
                        //update reciever
                        {
                            DocumentReference docRef = Application.firestore.collection("users").document((String)recieverQuery.get("userID"));
                            var map = new HashMap<String, Object>();
                            map.put("userID",recieverQuery.get("userID") ); // to access user more easily
                            map.put("firstName", recieverQuery.get("firstName"));
                            map.put("lastName", recieverQuery.get("lastName"));
                            map.put("email", recieverQuery.get("email"));
                            map.put("hashedPassword", recieverQuery.get("hashedPassword"));
                            var benArray = new ArrayList<String>();
                            if(userQuery.get("beneficiaries") != null) {
                                for (var i : (ArrayList<String>) recieverQuery.get("beneficiaries")) {
                                    benArray.add(i);
                                }
                            }
                            map.put("beneficiaries",benArray);
                            map.put("balance",newRecieverBalance);
                            docRef.set(map).get();
                        }

                        Platform.runLater(() -> {
                           // closeAction.run();
                            if (onSuccess != null) {
                                onSuccess.run();    // e.g. table refresh
                            }
                            NotificationDialog.show("success","Transfer made successfully.",true);
                            addBtn.setDisable(false);
                            cancelBtn.setDisable(false);
                            errorLabel.setText("");
                            //NotificationDialog.showWithAnchor(lastAnchor,"Success!","Login Successful !", true);

                        });

                    } catch (InterruptedException | ExecutionException ex) {
                        ex.printStackTrace();
                        Platform.runLater(() -> {
                            errorLabel.setText("Something went wrong. Please try again.");
                            addBtn.setDisable(false);
                            cancelBtn.setDisable(false);
                        });
                    }
               // }).start();
            });

            // ---------- entrance animation ----------
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

