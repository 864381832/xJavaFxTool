package com.xwintop.xJavaFxTool.utils;

import com.sun.javafx.tk.TKStage;
import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import javafx.stage.Stage;

import java.lang.reflect.Method;
/**
 * @ClassName: StageUtils
 * @Description: 更新场景工具类（解决点击任务栏图标无法最小化问题）
 * @author: xufeng
 * @date: 2020/1/2 15:56
 */

public class StageUtils {

    static interface ExtUser32 extends StdCallLibrary, User32 {
        ExtUser32 INSTANCE = (ExtUser32) Native.loadLibrary("user32", ExtUser32.class, W32APIOptions.DEFAULT_OPTIONS);
        WinDef.LRESULT CallWindowProcW(Pointer lpWndProc, Pointer hWnd, int msg, WinDef.WPARAM wParam, WinDef.LPARAM lParam);
        int SetWindowLong(HWND hWnd, int nIndex, com.sun.jna.Callback wndProc) throws LastErrorException;
    }

    // update default javafx stage style
    public static void updateStageStyle(Stage stage) {
        Pointer pointer = getWindowPointer(stage);
        WinDef.HWND hwnd = new WinDef.HWND(pointer);

        final User32 user32 = User32.INSTANCE;
        int oldStyle = user32.GetWindowLong(hwnd, WinUser.GWL_STYLE);
        int newStyle = oldStyle | 0x00020000; // WS_MINIMIZEBOX
        user32.SetWindowLong(hwnd, WinUser.GWL_STYLE, newStyle);
    }

    private static Pointer getWindowPointer(Stage stage) {
        try {
            TKStage tkStage = stage.impl_getPeer();
            Method getPlatformWindow = tkStage.getClass().getDeclaredMethod("getPlatformWindow");
            getPlatformWindow.setAccessible(true);
            Object platformWindow = getPlatformWindow.invoke(tkStage);
            Method getNativeHandle = platformWindow.getClass().getMethod("getNativeHandle");
            getNativeHandle.setAccessible(true);
            Object nativeHandle = getNativeHandle.invoke(platformWindow);
            return new Pointer((Long) nativeHandle);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}

