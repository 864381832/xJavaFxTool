module com.xwintop.xJavaFxTool {
    exports com.xwintop.xJavaFxTool;
    opens com.xwintop.xJavaFxTool.controller;
    opens com.xwintop.xJavaFxTool.view;
    opens com.xwintop.xJavaFxTool.model;
    opens com.xwintop.xJavaFxTool.plugin;
    opens com.xwintop.xJavaFxTool.controller.plugin;

    requires com.xwintop.xcore;
    requires java.sql;
    requires java.xml;
    requires io.github.classgraph;
//    requires okio;
    requires commons.configuration;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires com.alibaba.fastjson2;
}