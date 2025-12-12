module ShareCloud.client.main {
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jjwt.api;
    requires java.management;
    requires javafx.graphics;
    requires java.net.http;
    requires org.json;
    requires java.desktop;
    requires jdk.xml.dom;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    //requires ShareCloud.main;

    exports gal.usc.etse.sharecloud.model.dto to com.fasterxml.jackson.databind;
    opens gal.usc.etse.sharecloud.model.dto to com.fasterxml.jackson.databind;
    exports gal.usc.etse.sharecloud.model.entity to com.fasterxml.jackson.databind;
    opens gal.usc.etse.sharecloud.model.entity to com.fasterxml.jackson.databind;
    opens gal.usc.etse.sharecloud to javafx.fxml;
    opens gal.usc.etse.sharecloud.guiController to javafx.fxml;
    exports gal.usc.etse.sharecloud;

}