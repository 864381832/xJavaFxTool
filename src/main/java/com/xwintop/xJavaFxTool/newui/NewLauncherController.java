package com.xwintop.xJavaFxTool.newui;

import com.xwintop.xJavaFxTool.services.index.SystemSettingService;

public class NewLauncherController {

    public void openConfigDialog() {
        SystemSettingService.openSystemSettings("设置");
    }
}
