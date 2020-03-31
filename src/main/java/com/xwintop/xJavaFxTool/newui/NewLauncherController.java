package com.xwintop.xJavaFxTool.newui;

import com.xwintop.xJavaFxTool.Main;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.plugin.PluginManager;
import com.xwintop.xJavaFxTool.services.index.SystemSettingService;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class NewLauncherController {

    public VBox pluginCategories;

    public void openConfigDialog() {
        SystemSettingService.openSystemSettings("设置");
    }

    public void initialize() {

        List<PluginJarInfo> pluginList = PluginManager.getInstance().getPluginList();
        ResourceBundle menuResourceBundle = Main.RESOURCE_BUNDLE;

        for (PluginJarInfo jarInfo : pluginList) {
            String parentId = jarInfo.getMenuParentId();
            if (parentId.startsWith("p-")) {
                parentId = parentId.substring(2);
                String categoryName = menuResourceBundle.getString(parentId);
                String pluginName = jarInfo.getName();
                log.info("plugin: {} - {}", categoryName, pluginName);
            }
        }

        for (int i = 0; i < 5; i++) {
            PluginCategoryController category = PluginCategoryController.newInstance("最近使用");
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
