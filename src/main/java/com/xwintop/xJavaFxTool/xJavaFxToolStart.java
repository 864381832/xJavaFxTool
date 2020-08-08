package com.xwintop.xJavaFxTool;

import com.xwintop.xJavaFxTool.utils.XJavaFxSystemUtil;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: Main
 * @Description: 启动类
 * @author: xufeng
 * @date: 2017年11月10日 下午4:34:11
 */
@Slf4j
public class xJavaFxToolStart {

    public static void main(String[] args) {

        XJavaFxSystemUtil.initSystemLocal();    // 初始化本地语言
        XJavaFxSystemUtil.addJarByLibs();       // 添加外部jar包

        Application.launch(Main.class, args);
    }
}
