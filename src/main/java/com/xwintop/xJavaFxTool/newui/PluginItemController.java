package com.xwintop.xJavaFxTool.newui;

import com.xwintop.xcore.javafx.helper.FxmlHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PluginItemController {

    public static PluginItemController newInstance(String pluginName) {
        FXMLLoader fxmlLoader = FxmlHelper.loadFromResource(
            "/com/xwintop/xJavaFxTool/fxmlView/newui/plugin-item.fxml"
        );
        PluginItemController controller = fxmlLoader.getController();
        controller.setPluginName(pluginName);
        return controller;
    }

    ///////////////////////////////////////////////////////////////

    public Label pluginName;

    public VBox root;

    public void initialize() {
        // 当元素不可见时也从布局流中去掉
        this.root.managedProperty().bind(this.root.visibleProperty());
    }

    public void setPluginName(String pluginName) {
        this.pluginName.setText(pluginName);
    }
}
