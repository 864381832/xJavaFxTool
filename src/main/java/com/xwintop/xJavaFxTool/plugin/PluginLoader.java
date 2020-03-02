package com.xwintop.xJavaFxTool.plugin;

import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class PluginLoader {

    public static void loadPluginAsWindow(PluginJarInfo plugin) {
        try {
            FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(PluginLoader.class.getResource(plugin.getFxmlPath()));

            if (StringUtils.isNotEmpty(plugin.getBundleName())) {
                ResourceBundle resourceBundle = ResourceBundle.getBundle(plugin.getBundleName(), Config.defaultLocale);
                generatingCodeFXMLLoader.setResources(resourceBundle);
            }

            JavaFxViewUtil.getNewStage(plugin.getTitle(), plugin.getIconPath(), generatingCodeFXMLLoader);
        } catch (Exception e) {
            log.error("加载插件失败", e);
        }
    }


    public static void loadPluginAsTab(PluginJarInfo plugin, TabPane tabPane) {
        try {
            FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(PluginLoader.class.getResource(plugin.getFxmlPath()));

            if (StringUtils.isNotEmpty(plugin.getBundleName())) {
                ResourceBundle resourceBundle = ResourceBundle.getBundle(plugin.getBundleName(), Config.defaultLocale);
                generatingCodeFXMLLoader.setResources(resourceBundle);
            }

            Tab tab = new Tab(plugin.getTitle());

            if (StringUtils.isNotEmpty(plugin.getIconPath())) {
                ImageView imageView = new ImageView(new Image(plugin.getIconPath()));
                imageView.setFitHeight(18);
                imageView.setFitWidth(18);
                tab.setGraphic(imageView);
            }

            tab.setContent(generatingCodeFXMLLoader.load());
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            tab.setOnCloseRequest(
                event -> JavaFxViewUtil.setControllerOnCloseRequest(generatingCodeFXMLLoader.getController(), event)
            );
        } catch (Exception e) {
            log.error("加载插件失败", e);
        }
    }

}
