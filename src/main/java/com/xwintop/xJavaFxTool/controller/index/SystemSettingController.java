package com.xwintop.xJavaFxTool.controller.index;

import com.xwintop.xJavaFxTool.services.index.SystemSettingService;
import com.xwintop.xJavaFxTool.utils.XJavaFxSystemUtil;
import com.xwintop.xJavaFxTool.view.index.SystemSettingView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.PropertiesConfiguration;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
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

    public void applySettings() {
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
}
