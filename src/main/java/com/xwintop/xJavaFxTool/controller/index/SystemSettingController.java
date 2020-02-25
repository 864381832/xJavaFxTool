package com.xwintop.xJavaFxTool.controller.index;

import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.services.index.SystemSettingService;
import com.xwintop.xJavaFxTool.utils.XJavaFxSystemUtil;
import com.xwintop.xJavaFxTool.view.index.SystemSettingView;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: SystemSettingController
 * @Description: 设置页面
 * @author: xufeng
 * @date: 2020/2/25 0025 16:44
 */

@Getter
@Setter
@Slf4j
public class SystemSettingController extends SystemSettingView {
    private SystemSettingService systemSettingService = new SystemSettingService(this);
    private Stage newStage = null;

    //显示设置界面
    public static void showSystemSetting(String title) throws Exception {
        FXMLLoader fXMLLoader = new FXMLLoader(IndexController.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/index/SystemSetting.fxml"));
        Parent root = fXMLLoader.load();
        SystemSettingController systemSettingController = fXMLLoader.getController();
        Stage newStage = JavaFxViewUtil.getNewStageNull(title, null, root, -1, -1, false, false, false);
        newStage.initModality(Modality.APPLICATION_MODAL);
        systemSettingController.setNewStage(newStage);
        newStage.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        try {
            PropertiesConfiguration xmlConfigure = XJavaFxSystemUtil.getSystemConfigure();
            exitShowAlertCheckBox.setSelected(xmlConfigure.getBoolean("exitShowAlertCheckBox", true));
            addNotepadCheckBox.setSelected(xmlConfigure.getBoolean("addNotepadCheckBox", true));
            saveStageBoundCheckBox.setSelected(xmlConfigure.getBoolean("saveStageBoundCheckBox", true));
        } catch (Exception e) {
            log.error("加载配置失败：", e);
        }

    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void saveAction(ActionEvent event) {
        try {
            PropertiesConfiguration xmlConfigure = XJavaFxSystemUtil.getSystemConfigure();
            xmlConfigure.setProperty("exitShowAlertCheckBox", exitShowAlertCheckBox.isSelected());
            xmlConfigure.setProperty("addNotepadCheckBox", addNotepadCheckBox.isSelected());
            xmlConfigure.setProperty("saveStageBoundCheckBox", saveStageBoundCheckBox.isSelected());
            xmlConfigure.save();
            if (newStage != null) {
                newStage.close();
            }
        } catch (Exception e) {
            log.error("保存配置失败：", e);
        }
    }

    @FXML
    private void cancelAction(ActionEvent event) {
        if (newStage != null) {
            newStage.close();
        }
    }
}
