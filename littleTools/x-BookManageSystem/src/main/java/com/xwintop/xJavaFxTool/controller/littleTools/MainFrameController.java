package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.BookManageSystemMain;
import com.xwintop.xJavaFxTool.utils.SimpleTools;
import com.xwintop.xJavaFxTool.view.littleTools.MainFrameView;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
public class MainFrameController extends MainFrameView {
    private SimpleTools simpleTools = new SimpleTools();

    /**
     * 初始化启动
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 为菜单项添加图标
        simpleTools.setMenuItemImage(new MenuItem[]{bookTypeAddMenuItem, bookTypeManageMenuItem, bookAddMenuItem, bookManageMenuItem, exitMenuItem, aboutSoftMenuItem},
                new String[]{"/BookManageSystem/images/add.png", "/BookManageSystem/images/edit.png", "/BookManageSystem/images/add.png", "/BookManageSystem/images/edit.png", "/BookManageSystem/images/exit.png", "/BookManageSystem/images/about.png"});
        do_bookTypeManageMenuItem_event(null);
    }

    /**
     * “退出”菜单项的事件处理
     *
     * @param event 事件
     */
    public void do_exitMenuItem_vent(ActionEvent event) {
        // 退出菜单项的事件处理
        System.exit(0);
    }

    /**
     * “图书类别添加”菜单项的事件处理
     *
     * @param event 事件
     */
    public void do_bookTypeAddMenuItem_event(ActionEvent event) {
        AnchorPane pane = BookTypeAddFrameController.initBookTypeAddFrame();
        mainFrameAnchorPane.getChildren().clear();
        mainFrameAnchorPane.getChildren().add(pane);
    }

    /**
     * “图书类别维护”菜单项的事件处理
     *
     * @param event 事件
     */
    public void do_bookTypeManageMenuItem_event(ActionEvent event) {
        // 当点击“图书类别维护”菜单项后，加载图书类别维护面板
        AnchorPane pane = BookTypeManageFrameController.initBookTypeManageFrame();
        // 清空界面上原有的控件
        mainFrameAnchorPane.getChildren().clear();
        // 将图书类别维护面板添加到界面上
        mainFrameAnchorPane.getChildren().add(pane);
    }

    /**
     * “图书添加”菜单项的事件处理
     *
     * @param event 事件
     */
    public void do_bookAddMenuItem_event(ActionEvent event) {
        // 当点击“图书添加”菜单项后，加载图书添加面板
        AnchorPane pane = BookAddFrameController.initBookAddFrame();
        // 清空界面上原有的控件
        mainFrameAnchorPane.getChildren().clear();
        // 将图书添加面板添加到界面上
        mainFrameAnchorPane.getChildren().add(pane);
    }

    /**
     * “图书维护”菜单项的事件处理
     *
     * @param event 事件
     */
    public void do_bookManageMenuItem_event(ActionEvent event) {
        // 当点击“图书维护”菜单项后，加载图书维护面板
        AnchorPane pane = BookManageFrameController.initBookManageFrame();
        // 清空界面上原有的控件
        mainFrameAnchorPane.getChildren().clear();
        // 将图书维护面板添加到界面上
        mainFrameAnchorPane.getChildren().add(pane);
    }

    public void do_aboutSoftMenuItem_event(ActionEvent event) {
        // 当点击“关于软件”菜单项后，加载弹出框
        new BookManageSystemMain().initAboutSoftFrame();
    }
}
