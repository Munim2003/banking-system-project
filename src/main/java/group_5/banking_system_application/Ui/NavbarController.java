package group_5.banking_system_application.Ui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.List;

public class NavbarController {

    @FXML
    private Button btnHome;
    @FXML
    private Button btnAccount;
    @FXML
    private Button btnTransactions;
    @FXML
    private Button btnAnalytics;
    @FXML
    private Button btnBeneficiaries;

    @FXML
    private StackPane navbarTickerContainer;
    @FXML
    private Label navbarTickerLabel;

    private Button activeButton;
    private SequentialTransition tickerAnimation;
    private List<String> suggestions;
    private int currentSuggestionIndex = 0;

    private MainPageController mainPageController;

    // ----------------- NAV BUTTON STATE -----------------

    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("nav-item-active");
        }
        activeButton = button;

        if (!button.getStyleClass().contains("nav-item-active")) {
            button.getStyleClass().add("nav-item-active");
        }
        System.out.println("Active button classes: " + button.getText() + " -> " + button.getStyleClass());
    }

    // ----------------- NAVIGATION HANDLERS -----------------

    @FXML
    public void openTransactions(ActionEvent actionEvent) {
        if (mainPageController != null) {
            mainPageController.setPage("/group_5/banking_system_application/FxmlLayouts/transactions.fxml");
            setActiveButton(btnTransactions);
        }
    }

    @FXML
    public void openAnalytics(ActionEvent actionEvent) {
        if (mainPageController != null) {
            mainPageController.setPage("/group_5/banking_system_application/FxmlLayouts/analytics-page.fxml");
            setActiveButton(btnAnalytics);
        }
    }

    @FXML
    public void openDashboard(ActionEvent actionEvent) {
        if (mainPageController != null) {
            mainPageController.setPage("/group_5/banking_system_application/FxmlLayouts/dashboard-page.fxml");
            setActiveButton(btnHome);
        }
    }

    @FXML
    public void openAccounts(ActionEvent actionEvent) {
        if (mainPageController != null) {
            mainPageController.setPage("/group_5/banking_system_application/FxmlLayouts/accounts-page.fxml");
            setActiveButton(btnAccount);
        }
    }

    @FXML
    public void openBeneficiaries(ActionEvent actionEvent) {
        if (mainPageController != null) {
            mainPageController.setPage("/group_5/banking_system_application/FxmlLayouts/Beneficiaries.fxml");
            setActiveButton(btnBeneficiaries);
        }
    }

    @FXML
    public void setMainPageController(MainPageController mainPageController) {
        this.mainPageController = mainPageController;
        setActiveButton(btnHome);
        initializeNavbarTicker();
    }

    // ----------------- NAVBAR TICKER -----------------

    /**
     * Initializes the navbar bottom ticker:
     * fades suggestions in/out and cycles through the whole list.
     */
    private void initializeNavbarTicker() {
        if (navbarTickerLabel == null || navbarTickerContainer == null) {
            return;
        }

        suggestions = BankingSuggestionService.getShuffledSuggestions();

        if (suggestions == null || suggestions.isEmpty()) {
            navbarTickerLabel.setText("Welcome to Vaultiq!");
            return;
        }

        // Start from the first suggestion
        currentSuggestionIndex = 0;
        navbarTickerLabel.setText(suggestions.get(currentSuggestionIndex));
        navbarTickerLabel.setOpacity(1.0);

        createTickerAnimation();

        // Play when scene is ready
        navbarTickerContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && tickerAnimation != null) {
                tickerAnimation.play();
            }
        });

        // Or immediately if scene already set
        if (navbarTickerContainer.getScene() != null && tickerAnimation != null) {
            tickerAnimation.play();
        }
    }

    /**
     * Creates a repeating animation:
     * - show message for a few seconds
     * - fade out
     * - swap to next suggestion
     * - fade in
     * - repeat for all suggestions
     */
    private void createTickerAnimation() {
        // fade out current text
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), navbarTickerLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // change text when fully faded out
        Timeline changeText = new Timeline(
                new javafx.animation.KeyFrame(Duration.ZERO, e -> {
                    if (suggestions == null || suggestions.isEmpty()) {
                        return;
                    }
                    // go to next suggestion (loop over full list)
                    currentSuggestionIndex = (currentSuggestionIndex + 1) % suggestions.size();
                    String nextSuggestion = suggestions.get(currentSuggestionIndex);
                    navbarTickerLabel.setText(nextSuggestion);
                    navbarTickerLabel.applyCss();
                    navbarTickerLabel.layout();
                }));

        // fade new text in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), navbarTickerLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // show text for some time before changing again
        Timeline displayPause = new Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(5)) // how long each message stays fully visible
        );

        // one full cycle: pause with visible text -> fade out -> swap text -> fade in
        tickerAnimation = new SequentialTransition(
                displayPause,
                fadeOut,
                changeText,
                fadeIn);
        tickerAnimation.setCycleCount(Animation.INDEFINITE);
    }
}
