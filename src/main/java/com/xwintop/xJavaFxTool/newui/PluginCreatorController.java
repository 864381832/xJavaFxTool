package com.xwintop.xJavaFxTool.newui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class PluginCreatorController {

    public static final String WELCOME = "你想创建自己的插件项目吗？\n\n"
        + "这个工具帮你搭建一个插件的脚手架项目，帮助你尽快开始功能开发。\n\n"
        + "完成插件开发需要以下知识："
        + "\n1、会使用 Java 开发工具；"
        + "\n2、会使用 Maven；"
        + "\n3、熟悉 JavaFX 桌面框架。\n\n"
        + "若想开始创建插件项目，请点击“下一步”。";

    public StackPane stackPane;

    public Label txtWelcome;

    public void initialize() {
        this.txtWelcome.setText(WELCOME);
    }
}
