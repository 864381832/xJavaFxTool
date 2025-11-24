package com.xwintop.xJavaFxTool.utils;

import com.xwintop.xJavaFxTool.XJavaFxToolApplication;
import com.xwintop.xcore.util.VersionChecker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @ClassName: XJavaFxSystemUtil
 * @Description: javafx系统层工具类
 * @author: xufeng
 * @date: 2017年11月10日 下午4:35:17
 */
@Slf4j
public class XJavaFxSystemUtil {

    /**
     * @Title: initSystemLocal
     * @Description: 初始化本地语言
     */
    public static void initSystemLocal() {
        try {
            String localeString = Config.get(Config.Keys.Locale, "");

            if (StringUtils.isNotEmpty(localeString)) {
                String[] locale1 = localeString.split("_");
                Config.defaultLocale = new Locale(locale1[0], locale1[1]);
            }

            XJavaFxToolApplication.RESOURCE_BUNDLE = ResourceBundle.getBundle("locale.Menu", Config.defaultLocale);
        } catch (Exception e) {
            log.error("初始化本地语言失败", e);
        }
    }

    public static void checkerVersion(){
        VersionChecker.checkerVersion("https://gitee.com/api/v5/repos/xwintop/xJavaFxTool/releases/latest",
            "https://gitee.com/xwintop/xJavaFxTool/releases",
            Config.xJavaFxToolVersions.substring(1));
    }
}
