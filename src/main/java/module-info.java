module group_5.banking_system_application {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens group_5.banking_system_application to javafx.fxml;
    exports group_5.banking_system_application;
    exports group_5.banking_system_application.Ui;
    opens group_5.banking_system_application.Ui to javafx.fxml;
}