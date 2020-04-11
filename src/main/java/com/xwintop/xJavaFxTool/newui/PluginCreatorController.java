package com.xwintop.xJavaFxTool.newui;

import com.xwintop.xJavaFxTool.newui.creator.PluginProjectInfo;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import java.io.File;
import java.net.MalformedURLException;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

public class PluginCreatorController {

    public static final String WELCOME = "你想创建自己的插件项目吗？\n\n"
        + "这个工具帮你搭建一个插件的脚手架项目，帮助你尽快开始功能开发。\n\n"
        + "完成插件开发需要以下知识："
        + "\n1、会使用 Java 开发工具；"
        + "\n2、会使用 Maven；"
        + "\n3、熟悉 JavaFX 桌面框架。\n\n"
        + "若想开始创建插件项目，请点击“下一步”。";

    public TextField txtLocation;

    public StackPane stackPane;

    public Label txtWelcome;

    public ImageView imgPluginLogo;

    public TextField txtGroupId;

    public TextField txtArtifactId;

    public TextField txtVersion;

    public TextField txtPluginName;

    public TextField txtPluginTitle;

    private boolean startCreation;

    private void showStack(int index) {
        for (int i = 0; i < stackPane.getChildren().size(); i++) {
            Node node = stackPane.getChildren().get(i);
            node.setVisible(i == index);
        }
    }

    public void initialize() {
        this.txtWelcome.setText(WELCOME);
    }

    public void showCreatorForm() {
        showStack(1);
        this.startCreation = true;
    }

    public boolean isStartCreation() {
        return startCreation;
    }

    public void choosePluginLogo() {
        File imgFile = FileChooserUtil.chooseFile(
            new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg")
        );

        if (imgFile != null) {
            try {
                this.imgPluginLogo.setImage(new Image(imgFile.toURI().toURL().toExternalForm()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public void choosePluginLocation() {
        File pluginLocation = FileChooserUtil.chooseDirectory();
        if (pluginLocation != null) {
            this.txtLocation.setText(pluginLocation.getAbsolutePath());
        }
    }

    public PluginProjectInfo getPluginProjectInfo() {
        PluginProjectInfo pluginProjectInfo = new PluginProjectInfo();
        pluginProjectInfo.setLocation(this.txtLocation.getText());
        pluginProjectInfo.setGroupId(this.txtGroupId.getText());
        pluginProjectInfo.setArtifactId(this.txtArtifactId.getText());
        pluginProjectInfo.setVersion(this.txtVersion.getText());
        pluginProjectInfo.setPluginName(this.txtPluginName.getText());
        pluginProjectInfo.setPluginTitle(this.txtPluginTitle.getText());
        pluginProjectInfo.setPluginLogo(this.imgPluginLogo.getImage());
        return pluginProjectInfo;
    }
}
