module com.xwintop.xJavaFxTool {
    exports com.xwintop.xJavaFxTool;
    opens com.xwintop.xJavaFxTool.controller;
    opens com.xwintop.xJavaFxTool.view;
    opens com.xwintop.xJavaFxTool.model;
    opens com.xwintop.xJavaFxTool.plugin;
    opens com.xwintop.xJavaFxTool.controller.plugin;

    requires com.xwintop.xcore;
    requires static lombok;
    requires org.slf4j;
    requires org.apache.commons.lang3;
    requires javafx.web;
    requires java.sql;
    requires io.github.classgraph;
    requires okhttp3;
    requires org.apache.commons.io;
//    requires okio;
    requires commons.configuration;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires com.alibaba.fastjson2;
}