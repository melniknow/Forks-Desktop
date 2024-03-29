module com.melniknow.fd {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires java.net.http;
    requires urlbuilder;
    requires com.google.gson;
    requires io.github.bonigarcia.webdrivermanager;
    requires org.seleniumhq.selenium.chrome_driver;
    requires org.seleniumhq.selenium.support;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.commons.codec;
    requires java.sql;

    opens com.melniknow.fd to javafx.fxml;
    exports com.melniknow.fd;
    exports com.melniknow.fd.ui;
    opens com.melniknow.fd.ui to javafx.fxml;
    exports com.melniknow.fd.ui.panels;
    opens com.melniknow.fd.ui.panels to javafx.fxml;
    exports com.melniknow.fd.ui.panels.impl;
    opens com.melniknow.fd.ui.panels.impl to javafx.fxml;
    exports com.melniknow.fd.core;
    exports com.melniknow.fd.betting;
    exports com.melniknow.fd.domain;
    exports com.melniknow.fd.utils;
    exports com.melniknow.fd.betting.bookmakers;
    exports com.melniknow.fd.betting.bookmakers._188bet;
    exports com.melniknow.fd.betting.bookmakers.pinnacle;
    exports com.melniknow.fd.betting.bookmakers.bet365;
    exports com.melniknow.fd.profile;
    exports com.melniknow.fd.advanced;
}