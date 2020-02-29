package com.xwintop.xJavaFxTool.newui;

import com.xwintop.xJavaFxTool.services.index.SystemSettingService;
import javafx.scene.layout.VBox;

public class NewLauncherController {

    public VBox pluginCategories;

    public void openConfigDialog() {
        SystemSettingService.openSystemSettings("设置");
    }

    public void initialize() {

        for (int i = 0; i < 5; i++) {
            PluginCategoryController category =
                PluginCategoryController.newInstance("最近使用");

            addCategory(category);

            for (int j = 0; j < 10; j++) {
                PluginItemController item =
                    PluginItemController.newInstance("临时记事本");

                category.addItem(item);
            }

        }
    }

    private void addCategory(PluginCategoryController category) {
        this.pluginCategories.getChildren().add(category.root);
    }

}
