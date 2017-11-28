package com.xwintop.xJavaFxTool.utils;

import org.hyperic.sigar.Sigar;

/**
 * @ClassName: SigarUtil
 * @Description: 获取系统信息工具类
 * @author: xufeng
 * @date: 2017/11/28 14:28  
 */

public class SigarUtil {
    public final static Sigar sigar = initSigar();
    private static Sigar initSigar() {//初始化Sigar
        try {
            String classPath = SigarUtil.class.getResource("/data/sigar").getPath();
            String path = System.getProperty("java.library.path");
            if (isOSWin()) {
                path += ";" + classPath;
            } else {
                path += ":" + classPath;
            }
            System.setProperty("java.library.path", path);
            return new Sigar();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isOSWin(){//OS 版本判断
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("win") >= 0) {
            return true;
        } else return false;
    }
}
