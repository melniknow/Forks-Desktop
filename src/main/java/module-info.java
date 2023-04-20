module com.melniknow.fd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.telegram.bot.api;
    requires atlantafx.base;

    opens com.melniknow.fd to javafx.fxml;
    exports com.melniknow.fd;
    exports com.melniknow.fd.UI;
    opens com.melniknow.fd.UI to javafx.fxml;
    exports com.melniknow.fd.UI.panels;
    opens com.melniknow.fd.UI.panels to javafx.fxml;
    exports com.melniknow.fd.UI.panels.impl;
    opens com.melniknow.fd.UI.panels.impl to javafx.fxml;
}