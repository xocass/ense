module ShareCloud.main {
    requires com.fasterxml.jackson.databind;
    requires json.patch;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.data.commons;
    requires spring.data.mongodb;
    requires spring.web;
    requires spring.webflux;

    requires javafx.controls;
    requires javafx.fxml;
    requires org.reactivestreams;

    opens gal.usc.etse.sharecloud to javafx.fxml;
    opens gal.usc.etse.sharecloud.model;
    opens gal.usc.etse.sharecloud.service;
    opens gal.usc.etse.sharecloud.controller;
    exports gal.usc.etse.sharecloud;
}