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
    requires transitive static lombok;
    requires transitive org.slf4j;
    requires org.yaml.snakeyaml;
    requires quartz;
    requires transitive javafx.swing;
    requires org.apache.commons.imaging;
    requires org.controlsfx.controls;
    requires transitive cn.hutool;
    requires org.dom4j;
}
