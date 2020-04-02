package com.xwintop.xJavaFxTool.newui;

import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.utils.ResourceUtils;
import com.xwintop.xcore.javafx.helper.FxmlHelper;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PluginItemController {

    public static final String FXML_PATH = "/com/xwintop/xJavaFxTool/fxmlView/newui/plugin-item.fxml";

    public static PluginItemController newInstance(PluginJarInfo pluginJarInfo) {
        FXMLLoader fxmlLoader = FxmlHelper.loadFromResource(FXML_PATH);
        PluginItemController controller = fxmlLoader.getController();
        controller.setPluginInfo(pluginJarInfo);
        return controller;
    }

    ///////////////////////////////////////////////////////////////

    private PluginJarInfo pluginJarInfo;

    public Label pluginName;

    public VBox root;

    public ImageView imgLogo;

    public void initialize() {
        // 当元素不可见时也从布局流中去掉
        this.root.managedProperty().bind(this.root.visibleProperty());
    }

    private void updateIcon() {
        URL iconUrl = ResourceUtils.getResource(
            this.pluginJarInfo.getIconPath(),
            this.pluginJarInfo.getDefaultIconPath(),
            "/logo/plugin.png"
        );

        if (iconUrl != null) {
            String url = iconUrl.toExternalForm();
            if (url.endsWith("plugin.png")) {
                log.info("please add logo to " + this.pluginJarInfo.getDefaultIconPath());
            }
            imgLogo.setImage(new Image(url));
        }
    }

    private void setPluginInfo(PluginJarInfo pluginJarInfo) {
        this.pluginJarInfo = pluginJarInfo;
        this.pluginName.setText(pluginJarInfo.getName());
        updateIcon();
    }
}
