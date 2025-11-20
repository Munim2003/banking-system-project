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
    private VBox navbarHost;   // placeholder from FXML

    @FXML
    public void initialize() {
        // 1) Navbar.fxml load karo
        try {
            FXMLLoader navLoader = new FXMLLoader(
                    Application.class.getResource("/group_5/banking_system_application/FxmlLayouts/navbar.fxml")
            );
            VBox navRoot = navLoader.load();
            NavbarController navController = navLoader.getController();

            // 2) Navbar controller ko reference do
            navController.setMainPageController(this);

            // 3) Navbar ko left container me daalo
            navbarHost.getChildren().setAll(navRoot);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 4) Default page = dashboard
        setPage("/group_5/banking_system_application/FxmlLayouts/dashboard-page.fxml");
    }

    @FXML
    public void setPage(String fxmlPath){
//        Parent page = null;
//        try {
//            page = FXMLLoader.load(getClass().getResource(fxmlPath));
//            contentComponent.getChildren().setAll(page);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        try {
            System.out.println("Loading FXML: " + fxmlPath);
            var url = getClass().getResource(fxmlPath);
            System.out.println("Resolved URL: " + url);

            if (url == null) {
                throw new IllegalStateException("FXML not found on classpath: " + fxmlPath);
            }

            Parent page = FXMLLoader.load(url);

            if (contentComponent.getScene() != null) {
                page.getStylesheets().addAll(contentComponent.getScene().getStylesheets());
            }

            // Clear and set new content
            contentComponent.getChildren().clear();
            contentComponent.getChildren().add(page);

            // Ensure the page fills the available space
            StackPane.setAlignment(page, javafx.geometry.Pos.TOP_LEFT);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }

    }
}
