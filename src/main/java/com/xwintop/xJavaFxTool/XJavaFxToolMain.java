package com.xwintop.xJavaFxTool;

import com.xwintop.xJavaFxTool.utils.XJavaFxSystemUtil;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

/**
 * 程序入口
 */
@Slf4j
public class XJavaFxToolMain {

    public static void main(String[] args) {
        XJavaFxSystemUtil.initSystemLocal();    // 初始化本地语言
        Application.launch(XJavaFxToolApplication.class, args);
    }
}
