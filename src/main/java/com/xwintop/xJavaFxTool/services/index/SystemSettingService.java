package com.xwintop.xJavaFxTool.services.index;

import com.xwintop.xJavaFxTool.controller.index.SystemSettingController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: SystemSettingService
 * @Description: 设置页面
 * @author: xufeng
 * @date: 2020/2/25 0025 16:44
 */

@Getter
@Setter
@Slf4j
public class SystemSettingService {
    private SystemSettingController systemSettingController;

    public SystemSettingService(SystemSettingController systemSettingController) {
        this.systemSettingController = systemSettingController;
    }
}
