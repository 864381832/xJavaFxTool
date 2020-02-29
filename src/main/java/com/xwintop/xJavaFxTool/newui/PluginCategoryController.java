package com.xwintop.xJavaFxTool.newui;

import com.xwintop.xcore.javafx.helper.FxmlHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class PluginCategoryController {

    public static PluginCategoryController newInstance(String categoryName) {
        FXMLLoader fxmlLoader = FxmlHelper.loadFromResource(
            "/com/xwintop/xJavaFxTool/fxmlView/newui/plugin-category.fxml"
        );
        PluginCategoryController controller = fxmlLoader.getController();
        controller.lblCategoryName.setText(categoryName);
        return controller;
    }

    ///////////////////////////////////////////////////////////////

    public Label lblCategoryName;

    public FlowPane items;

    public VBox root;

    public void addItem(PluginItemController item) {
        this.items.getChildren().add(item.root);
    }
}
