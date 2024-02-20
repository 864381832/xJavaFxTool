module com.xwintop.xJavaFxTool {
    exports com.xwintop.xJavaFxTool;
    exports com.xwintop.xJavaFxTool.controller.index;
    opens com.xwintop.xJavaFxTool.controller;
    opens com.xwintop.xJavaFxTool.view;
    opens com.xwintop.xJavaFxTool.model;
    opens com.xwintop.xJavaFxTool.plugin;
    opens com.xwintop.xJavaFxTool.controller.plugin;
    opens com.xwintop.xJavaFxTool.view.index;

    requires com.xwintop.xcore;
    requires java.sql;
    requires java.xml;
//    requires io.github.classgraph;
//    requires okio;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires com.alibaba.fastjson2;
    requires org.dom4j;
}