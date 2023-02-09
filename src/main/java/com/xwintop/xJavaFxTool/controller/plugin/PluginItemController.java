package com.xwintop.xJavaFxTool.controller.plugin;

import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.plugin.PluginManager;
import com.xwintop.xcore.javafx.helper.FxmlHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class PluginItemController {
    public static final String FXML_PATH = "/com/xwintop/xJavaFxTool/fxmlView/plugin/plugin-item.fxml";

    private PluginJarInfo pluginJarInfo;

    private ContextMenu contextMenu;

    public Label pluginName;

    public VBox root;

    public ImageView imgLogo;

    private IndexController indexController;

    public static PluginItemController newInstance(PluginJarInfo pluginJarInfo) {
        FXMLLoader fxmlLoader = FxmlHelper.loadFromResource(FXML_PATH);
        PluginItemController controller = fxmlLoader.getController();
        controller.setPluginInfo(pluginJarInfo);
        return controller;
    }

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
        CheckMenuItem chkFavorite = new CheckMenuItem(IndexController.FAVORITE_CATEGORY_NAME);
        chkFavorite.setStyle("-fx-padding: 0 35 0 0");
        this.contextMenu = new ContextMenu(chkFavorite);
        chkFavorite.setOnAction(event2 -> {
            this.pluginJarInfo.setIsFavorite(chkFavorite.isSelected());
            PluginManager.getInstance().saveToFileQuietly();
            indexController.loadPlugins();
        });
    }

    private void onMouseRightClicked(MouseEvent event) {
        CheckMenuItem chkFavorite = (CheckMenuItem) this.contextMenu.getItems().get(0);
        chkFavorite.setSelected(this.pluginJarInfo.getIsFavorite());
        this.contextMenu.show(this.root, event.getScreenX(), event.getScreenY());
    }

    private void onMouseLeftClicked(MouseEvent event) {
        indexController.getIndexService().loadPlugin(pluginJarInfo);
    }

    private void updateIcon() {
        if (this.pluginJarInfo.getIconImage() != null) {
            imgLogo.setImage(this.pluginJarInfo.getIconImage());
        } else {
            imgLogo.setImage(new Image("/images/plugin.png"));
        }
    }

    private void setPluginInfo(PluginJarInfo pluginJarInfo) {
        this.pluginJarInfo = pluginJarInfo;
        this.pluginName.setText(pluginJarInfo.getName());
        updateIcon();
    }

    public boolean matchKeyword(String keyword) {
        return this.pluginJarInfo.getName().toLowerCase().contains(keyword.toLowerCase());
    }

    public void setVisible(boolean visible) {
        this.root.setVisible(visible);
    }
}
