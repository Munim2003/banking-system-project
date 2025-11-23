package group_5.banking_system_application.Ui;

import group_5.banking_system_application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainPageController {
    @FXML
    private StackPane contentComponent;

    @FXML
    private VBox navbarHost;



    @FXML
    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(Application.class.getResource("/group_5/banking_system_application/FxmlLayouts/navbar.fxml"));
            VBox navRoot = loader.load();
            NavbarController navbarController = loader.getController();
            navbarController.setMainPageController(this);
            navbarHost.getChildren().setAll(navRoot);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setPage("/group_5/banking_system_application/FxmlLayouts/dashboard-page.fxml");
    }

    @FXML
    public void setPage(String fxmlPath){
        try {
            var url = getClass().getResource(fxmlPath);

            if (url == null) {
                throw new IllegalStateException("FXML not found on classpath: " + fxmlPath);
            }

            Parent page = FXMLLoader.load(url);

            // Clear and set new content
            contentComponent.getChildren().clear();
            contentComponent.getChildren().setAll(page);

            // Ensure the page fills the available space
            StackPane.setAlignment(page, javafx.geometry.Pos.TOP_LEFT);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }

    }
}
