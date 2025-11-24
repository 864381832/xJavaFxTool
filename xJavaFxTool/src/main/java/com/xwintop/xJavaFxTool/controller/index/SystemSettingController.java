package com.xwintop.xJavaFxTool.controller.index;

import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xJavaFxTool.utils.Config.Keys;
import com.xwintop.xJavaFxTool.view.index.SystemSettingView;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 设置页面
 *
 * @author xufeng
 */

@Getter
@Setter
@Slf4j
public class SystemSettingController extends SystemSettingView {

    private Stage newStage = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
    }

    private void initView() {
        try {
            exitShowAlertCheckBox.setSelected(Config.getBoolean(Keys.ConfirmExit, true));
            addNotepadCheckBox.setSelected(Config.getBoolean(Keys.NotepadEnabled, true));
            saveStageBoundCheckBox.setSelected(Config.getBoolean(Keys.RememberWindowLocation, true));
        } catch (Exception e) {
            log.error("加载配置失败：", e);
        }
    }

    public void applySettings() {
        try {
            Config.set(Keys.ConfirmExit, exitShowAlertCheckBox.isSelected());
            Config.set(Keys.NotepadEnabled, addNotepadCheckBox.isSelected());
            Config.set(Keys.RememberWindowLocation, saveStageBoundCheckBox.isSelected());

            if (newStage != null) {
                newStage.close();
            }
        } catch (Exception e) {
            log.error("保存配置失败：", e);
        }
    }
}
