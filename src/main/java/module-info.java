module group_5.banking_system_application {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;

    requires firebase.admin;
    requires com.google.auth;
    requires com.google.auth.oauth2;
    requires google.cloud.firestore;
    requires google.cloud.core;
    requires com.google.api.apicommon;
    requires jbcrypt;
    requires commons.logging;


    opens group_5.banking_system_application to javafx.fxml;
    exports group_5.banking_system_application;
    exports group_5.banking_system_application.Ui;
    opens group_5.banking_system_application.Ui to javafx.fxml;
}