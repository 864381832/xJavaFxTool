package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.dao.BookTypeDao;
import com.xwintop.xJavaFxTool.utils.SimpleTools;
import com.xwintop.xJavaFxTool.view.littleTools.BookTypeAddFrameView;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
public class BookTypeAddFrameController extends BookTypeAddFrameView {
    private SimpleTools simpleTools = new SimpleTools();

    public static AnchorPane initBookTypeAddFrame() {
        return SimpleTools.loadByFxml("/com/xwintop/xJavaFxTool/fxmlView/littleTools/bookTypeAddFrame.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化按钮的图标
        simpleTools.setLabeledImage(new Labeled[]{addButton, resetButton}, new String[]{"/BookManageSystem/images/add.png",
                "/BookManageSystem/images/reset.png"});
    }

    // “添加”按钮的事件监听器方法
    public void do_addButton_event(ActionEvent event) {
        // 获取图书类别名称
        String bookTypeName = bookTypeNameTextField.getText();
        // 获取图书类别描述
        String bookTypeDescription = bookTypeDescriptionTextArea.getText();
        // 组装插入SQL语句
        String sql = "insert into tb_booktype (btName, btDescription) values ('" + bookTypeName + "','" + bookTypeDescription + "');";
        // 执行添加操作并返回操作结果
        boolean isOK = new BookTypeDao().dataChange(sql);
        // 对操作结果进行判断
        if (isOK) {
            // 添加成功则弹出提示框并清空文本框内容
            simpleTools.informationDialog(Alert.AlertType.INFORMATION, "提示", "信息", "添加成功！");
            bookTypeNameTextField.setText("");
            bookTypeDescriptionTextArea.setText("");
        } else {
            // 添加失败则弹出提示框
            simpleTools.informationDialog(Alert.AlertType.ERROR, "提示", "错误", "添加失败！");
        }
    }

    // “重置”按钮的事件监听器方法
    public void do_resetButton_event(ActionEvent event) {
        // 重置即清空用户输入的内容
        simpleTools.clearTextField(bookTypeNameTextField, bookTypeDescriptionTextArea);
    }
}
