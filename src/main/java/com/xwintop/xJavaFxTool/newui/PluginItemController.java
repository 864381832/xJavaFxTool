package com.xwintop.xJavaFxTool.newui;

import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.utils.ResourceUtils;
import com.xwintop.xcore.javafx.helper.FxmlHelper;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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

    private ContextMenu contextMenu;

    public Label pluginName;

    public VBox root;

    public ImageView imgLogo;

    public void initialize() {
        // 当元素不可见时也从布局流中去掉
        this.root.managedProperty().bind(this.root.visibleProperty());

        this.root.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                onMouseLeftClicked(event);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                onMouseRightClicked(event);
            }
        });
    }

    private void onMouseRightClicked(MouseEvent event) {
        NewLauncherService.getInstance().setCurrentPluginItem(this);
        CheckMenuItem chkFavorite = (CheckMenuItem) this.contextMenu.getItems().get(0);
        chkFavorite.setSelected(this.pluginJarInfo.getIsFavorite());
        this.contextMenu.show(this.root, event.getScreenX(), event.getScreenY());
    }

    private void onMouseLeftClicked(MouseEvent event) {
        NewLauncherService.getInstance().loadPlugin(this.pluginJarInfo);
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

    public void setContextMenu(ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }

    private void setPluginInfo(PluginJarInfo pluginJarInfo) {
        this.pluginJarInfo = pluginJarInfo;
        this.pluginName.setText(pluginJarInfo.getName());
        updateIcon();
    }

    public PluginJarInfo getPluginJarInfo() {
        return pluginJarInfo;
    }

    public boolean matchKeyword(String keyword) {
        return this.pluginJarInfo.getName().toLowerCase().contains(keyword.toLowerCase());
    }

    public void setVisible(boolean visible) {
        this.root.setVisible(visible);
    }
}
