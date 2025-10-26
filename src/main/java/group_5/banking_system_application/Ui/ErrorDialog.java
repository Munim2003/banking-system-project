package group_5.banking_system_application.Ui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class ErrorDialog {
    private static Node lastAnchor = null;

    private ErrorDialog() {}

    public static void initialize(Node anyNode) {
        lastAnchor = anyNode;
    }

    public static void show(String title, String message) {
        if (lastAnchor == null) {
            System.err.println("ErrorDialog.initialize() must be called first!");
            return;
        }
        showInternal(lastAnchor, title, message);
    }

    public static void show(String message) {
        show("Error", message);
    }

    public static void showWithAnchor(Node anchor, String title, String message) {
        showInternal(anchor, title, message);
    }

    private static void showInternal(Node anchor, String title, String message) {
        if (anchor == null || anchor.getScene() == null) {
            System.out.println("ErrorDialog: anchor or scene is null");
            return;
        }

        Platform.runLater(() -> {
            Scene scene = anchor.getScene();
            Stage stage = (Stage) scene.getWindow();

            // Get actual window dimensions
            double width = stage.getWidth();
            double height = stage.getHeight();

            System.out.println("Window dimensions: " + width + " x " + height);

            // Create a NEW StackPane to overlay everything
            // Create a NEW StackPane to overlay everything
            StackPane overlayRoot = new StackPane();
            // slightly darker/warmer overlay to match gradient bg
            overlayRoot.setStyle("-fx-background-color: rgba(12, 8, 24, 0.55);");
            overlayRoot.setAlignment(Pos.CENTER);

            /* ---------- CARD (shorter + dark theme) ---------- */
            VBox card = new VBox(10); // smaller spacing
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(14, 20, 14, 20)); // less padding
            card.setMaxWidth(260);
            card.setMaxHeight(200);
            card.setStyle(
                    // glassy dark surface with subtle border & purple glow
                    "-fx-background-color: linear-gradient(to bottom, #1C1430, #2B0B3A);" +
                            "-fx-background-radius: 14;" +
                            "-fx-border-color: rgba(255,255,255,0.08);" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 14;" +
                            "-fx-effect: dropshadow(gaussian, rgba(108,92,231,0.18), 18, 0.25, 0, 4);"
            );

            /* ---------- ICON ---------- */
            StackPane iconContainer = new StackPane();
            iconContainer.setPrefSize(48, 48);
            iconContainer.setStyle(
                    "-fx-background-color: rgba(239,68,68,0.16);" +  // soft red fill
                            "-fx-background-radius: 24;" +
                            "-fx-border-color: #ef4444;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 24;"
            );
            Label iconLabel = new Label("✕");
            iconLabel.setStyle(
                    "-fx-font-size: 22px;" +
                            "-fx-text-fill: #ef4444;" +
                            "-fx-font-weight: bold;"
            );
            iconContainer.getChildren().add(iconLabel);

            /* ---------- TITLE ---------- */
            Label titleLabel = new Label(title);
            titleLabel.setStyle(
                    "-fx-font-size: 16px;" +
                            "-fx-font-weight: 700;" +
                            "-fx-text-fill: #F5F6FA;"
            );

            /* ---------- MESSAGE ---------- */
            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);
            messageLabel.setAlignment(Pos.CENTER);
            messageLabel.setStyle(
                    "-fx-font-size: 13px;" +
                            "-fx-text-fill: #B8B9C2;"
            );

            /* ---------- OK BUTTON (Vaultiq purple) ---------- */
            Button okButton = new Button("OK");
            okButton.setPrefWidth(100);
            okButton.setStyle(
                    "-fx-background-color: #6C5CE7;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: 700;" +
                            "-fx-padding: 8 14;" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;"
            );
            okButton.setOnMouseEntered(e ->
                    okButton.setStyle(
                            "-fx-background-color: #7A6CF1;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 13px;" +
                                    "-fx-font-weight: 700;" +
                                    "-fx-padding: 8 14;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-cursor: hand;"
                    )
            );
            okButton.setOnMouseExited(e ->
                    okButton.setStyle(
                            "-fx-background-color: #6C5CE7;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 13px;" +
                                    "-fx-font-weight: 700;" +
                                    "-fx-padding: 8 14;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-cursor: hand;"
                    )
            );

// Assemble
            card.getChildren().addAll(iconContainer, titleLabel, messageLabel, okButton);

            // Make overlay + card invisible and place card off-screen bottom
            double preOffset = stage.getHeight() * 0.6 + 80; // safe off-screen start

            overlayRoot.setOpacity(0);  // overlay hidden initially
            card.setOpacity(0);         // card hidden initially
            card.setTranslateY(preOffset); // start below the viewport
            card.setUserData(preOffset);   // store for close animation

            overlayRoot.getChildren().add(card);


            // Store the OLD root
            Parent oldRoot = scene.getRoot();

            // Create a container with old root + overlay
            StackPane container = new StackPane();
            container.getChildren().addAll(oldRoot, overlayRoot);

            // Replace scene root temporarily
            scene.setRoot(container);

            // Close action - restore old root
            Runnable closeAction = () -> {
                closeDialog(overlayRoot, card, () -> scene.setRoot(oldRoot));
            };

            okButton.setOnAction(e -> closeAction.run());
            overlayRoot.setOnMouseClicked(e -> {
                if (e.getTarget() == overlayRoot) {
                    closeAction.run();
                }
            });

            // Animate entrance
            // Animate entrance (slide up from bottom to CENTER)
            Platform.runLater(() -> {
                // ensure sizes are computed
                overlayRoot.applyCss();
                overlayRoot.layout();

                // start below the center (off-screen-ish)
                double startOffset = overlayRoot.getHeight() / 2
                        + card.getBoundsInParent().getHeight() / 2
                        + 40; // extra margin

                card.setTranslateY(startOffset);
                card.setOpacity(0);
                card.setUserData(startOffset); // store for close animation

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

    private static void animateShow(StackPane overlay, VBox card) {
        // Optional: fade in overlay slightly
        FadeTransition overlayFade = new FadeTransition(Duration.millis(150), overlay);
        overlayFade.setFromValue(0);
        overlayFade.setToValue(1);

        // Card enters from below like a toast
        card.setOpacity(0);
        card.setTranslateY(60); // 60px below final spot

        FadeTransition cardFade = new FadeTransition(Duration.millis(180), card);
        cardFade.setFromValue(0);
        cardFade.setToValue(1);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(240), card);
        slideUp.setFromY(60);
        slideUp.setToY(0);
        slideUp.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition show = new ParallelTransition(overlayFade, cardFade, slideUp);
        show.play();
    }


    private static void closeDialog(StackPane overlay, VBox card, Runnable onComplete) {
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(180), card);
        slideDown.setToY(60); // slide back down
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