open module com.xwintop.xcore {
    exports com.xwintop.xcore;
    exports com.xwintop.xcore.javafx.dialog;
    exports com.xwintop.xcore.plugin;
    exports com.xwintop.xcore.util;
    exports com.xwintop.xcore.javafx;
    exports com.xwintop.xcore.javafx.helper;
    exports com.xwintop.xcore.util.javafx;
    requires transitive java.desktop;
    requires transitive javafx.base;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.web;
    requires transitive org.apache.commons.lang3;
    requires transitive org.apache.commons.io;
    requires com.jfoenix;
    requires static lombok;
    requires transitive org.slf4j;
//    requires logback.classic;
//    requires logback.core;
    requires okhttp3;
    requires org.yaml.snakeyaml;
    requires quartz;
    requires org.apache.commons.collections4;
    requires transitive javafx.swing;
    requires org.apache.commons.imaging;
    requires org.controlsfx.controls;
    requires transitive cn.hutool;
    requires dom4j;
}
