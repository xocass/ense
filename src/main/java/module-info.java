module ShareCloud.main {
    requires com.fasterxml.jackson.databind;
    //requires json.patch;
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
    requires spring.core;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires spring.security.crypto;
    requires spring.security.core;
    requires spring.security.web;
    requires spring.security.config;
    requires spring.webmvc;
    requires jjwt.api;
    requires jakarta.persistence;
    requires spring.data.redis;
    requires java.management;
    requires spring.data.jpa;
    requires org.apache.tomcat.embed.core;
    requires spring.hateoas;
    requires javafx.graphics;
    requires java.net.http;
    requires org.json;
    requires spring.boot.actuator;
    requires java.desktop;
    requires jdk.xml.dom;
    //requires ShareCloud.main;

    opens gal.usc.etse.sharecloud to javafx.fxml;
    opens gal.usc.etse.sharecloud.gui_controller to javafx.fxml;
    //opens gal.usc.etse.sharecloud.configuration to spring.core, spring.beans;
    //exports gal.usc.etse.sharecloud.configuration;
    //opens gal.usc.etse.sharecloud.repository to spring.core, spring.beans;
    //exports gal.usc.etse.sharecloud.repository;
    opens gal.usc.etse.sharecloud.configuration;
    opens gal.usc.etse.sharecloud.controller;
    opens gal.usc.etse.sharecloud.exception;
    opens gal.usc.etse.sharecloud.filter;
    opens gal.usc.etse.sharecloud.model;
    opens gal.usc.etse.sharecloud.repository;
    opens gal.usc.etse.sharecloud.service;
    exports gal.usc.etse.sharecloud;
    opens gal.usc.etse.sharecloud.model.entity;

}