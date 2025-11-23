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

public final class NotificationDialog {
    private static Node lastAnchor = null;

    private NotificationDialog() {}

    public static void initialize(Node anyNode) {
        lastAnchor = anyNode;
    }

    // Success methods
    public static void showSuccess(String title, String message) {
        if (lastAnchor == null) {
            System.err.println("NotificationDialog.initialize() must be called first!");
            return;
        }
        showInternal(lastAnchor, title, message, true);
    }

    public static void showSuccess(String message) {
        showSuccess("Success", message);
    }

    // Error methods
    public static void showError(String title, String message) {
        if (lastAnchor == null) {
            System.err.println("NotificationDialog.initialize() must be called first!");
            return;
        }
        showInternal(lastAnchor, title, message, false);
    }

    public static void showError(String message) {
        showError("Error", message);
    }

    // Generic method with boolean parameter
    public static void show(String title, String message, boolean isSuccess) {
        if (lastAnchor == null) {
            System.err.println("NotificationDialog.initialize() must be called first!");
            return;
        }
        showInternal(lastAnchor, title, message, isSuccess);
    }

    // With anchor methods
    public static void showWithAnchor(Node anchor, String title, String message, boolean isSuccess) {
        showInternal(anchor, title, message, isSuccess);
    }

    private static void showInternal(Node anchor, String title, String message, boolean isSuccess) {
        if (anchor == null || anchor.getScene() == null) {
            System.out.println("NotificationDialog: anchor or scene is null");
            return;
        }

        Platform.runLater(() -> {
            Scene scene = anchor.getScene();
            Stage stage = (Stage) scene.getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            System.out.println("Window dimensions: " + width + " x " + height);

            // Create overlay
            StackPane overlayRoot = new StackPane();
            overlayRoot.setStyle("-fx-background-color: rgba(12, 8, 24, 0.55);");
            overlayRoot.setAlignment(Pos.CENTER);

            // Determine colors based on success/error
            String iconBgColor = isSuccess ? "rgba(34,197,94,0.16)" : "rgba(239,68,68,0.16)";
            String iconBorderColor = isSuccess ? "#22c55e" : "#ef4444";
            String iconTextColor = isSuccess ? "#22c55e" : "#ef4444";
            String iconSymbol = isSuccess ? "✓" : "✕";
            String glowColor = isSuccess ? "rgba(34,197,94,0.18)" : "rgba(108,92,231,0.18)";

            /* ---------- CARD ---------- */
            VBox card = new VBox(10);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(14, 20, 14, 20));
            card.setMaxWidth(260);
            card.setMaxHeight(200);
            card.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #1C1430, #2B0B3A);" +
                            "-fx-background-radius: 14;" +
                            "-fx-border-color: rgba(255,255,255,0.08);" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 14;" +
                            "-fx-effect: dropshadow(gaussian, " + glowColor + ", 18, 0.25, 0, 4);"
            );

            /* ---------- ICON ---------- */
            StackPane iconContainer = new StackPane();
            iconContainer.setPrefSize(48, 48);
            iconContainer.setStyle(
                    "-fx-background-color: " + iconBgColor + ";" +
                            "-fx-background-radius: 24;" +
                            "-fx-border-color: " + iconBorderColor + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 24;"
            );
            Label iconLabel = new Label(iconSymbol);
            iconLabel.setStyle(
                    "-fx-font-size: 22px;" +
                            "-fx-text-fill: " + iconTextColor + ";" +
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

            /* ---------- OK BUTTON ---------- */
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

            // Setup initial positions
            double preOffset = stage.getHeight() * 0.6 + 80;
            overlayRoot.setOpacity(0);
            card.setOpacity(0);
            card.setTranslateY(preOffset);
            card.setUserData(preOffset);

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
                closeDialog(overlayRoot, card, () -> {container.getChildren().remove(oldRoot); scene.setRoot(oldRoot);});
            };

            okButton.setOnAction(e -> closeAction.run());
            overlayRoot.setOnMouseClicked(e -> {
                if (e.getTarget() == overlayRoot) {
                    closeAction.run();
                }
            });

            // Animate entrance
            Platform.runLater(() -> {
                overlayRoot.applyCss();
                overlayRoot.layout();

                double startOffset = overlayRoot.getHeight() / 2
                        + card.getBoundsInParent().getHeight() / 2
                        + 40;

                card.setTranslateY(startOffset);
                card.setOpacity(0);
                card.setUserData(startOffset);

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

    public static void shake(Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), node);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
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
