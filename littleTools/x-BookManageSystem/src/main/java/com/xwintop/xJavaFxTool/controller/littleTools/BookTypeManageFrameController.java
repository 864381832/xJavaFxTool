package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.beans.BookTypeBeanTableData;
import com.xwintop.xJavaFxTool.dao.BookTypeDao;
import com.xwintop.xJavaFxTool.utils.SimpleTools;
import com.xwintop.xJavaFxTool.view.littleTools.BookTypeManageFrameView;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 图书类别维护控制器
 *
 * @author lck100
 */
@Getter
@Setter
@Slf4j
public class BookTypeManageFrameController extends BookTypeManageFrameView {
    private SimpleTools simpleTools = new SimpleTools();
    private BookTypeDao bookTypeDao = new BookTypeDao();

    public static AnchorPane initBookTypeManageFrame() {
        return SimpleTools.loadByFxml("/com/xwintop/xJavaFxTool/fxmlView/littleTools/bookTypeManageFrame.fxml");
    }

    /**
     * 初始化界面数据
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 批量为按钮添加图标
        simpleTools.setLabeledImage(new Labeled[]{alterButton, deleteButton}, new String[]{"/BookManageSystem/images/edit.png", "/BookManageSystem/images/delete.png"});
        // 设置显示id的文本框不可编辑
        idTextField.setEditable(false);
        // 查询所有的图书类别列的SQL语句
        String sql = "select * from tb_booktype;";
        // 将数据添加到表格控件中
        simpleTools.setBookTypeTableViewData(bookTypeManageTableView
                , simpleTools.getBookTypeTableViewData(sql)
                , idTableColumn
                , bookTypeNameColumn
                , bookTypeDescriptionTableColumn
        );
        // 为表格控件注册事件监听器
        bookTypeManageTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showBookTypeDetails(newValue));
    }

    // 【查询】按钮的事件监听器
    public void do_checkButton_event(ActionEvent event) {
        // 获取用户输入的图书类别
        String bookTypeName = bookTypeNameTextField.getText();
        // 组装查询SQL
        String checkSQL = "";
        // 判断用户是否输入图书类别，如果没有输入即为空字符串，那么就查询所有数据，否则按条件将那些查询
        if (simpleTools.isEmpty(bookTypeName)) {
            checkSQL = "select * from tb_booktype";
        } else {
            checkSQL = "select * from tb_booktype where btName='" + bookTypeName + "';";
        }
        // 重新绘制表格数据
        simpleTools.setBookTypeTableViewData(bookTypeManageTableView
                , simpleTools.getBookTypeTableViewData(checkSQL)
                , idTableColumn
                , bookTypeNameColumn
                , bookTypeDescriptionTableColumn
        );
    }

    // 【修改】按钮的事件监听器
    public void do_alterButton_event(ActionEvent event) {
        // 获取用户输入的内容
        String id = idTextField.getText();
        String name = bookTypeNameTextField2.getText();
        String description = descriptionTextArea.getText();
        // 组装更新SQL
        String alterSQL = "update tb_booktype set btName='" + name + "',btDescription='" + description + "' where btId=" + id + ";";
        // 执行更新操作并获取操作结果
        boolean isOK = bookTypeDao.dataChange(alterSQL);
        // 对操作结果进行判断
        if (isOK) {
            // 更新成功则界面并清空各文本框及弹出提示框
            initialize(null, null);
            simpleTools.clearTextField(idTextField, bookTypeNameTextField2, descriptionTextArea);
            simpleTools.informationDialog(Alert.AlertType.INFORMATION, "提示", "信息", "修改成功！");
        } else {
            // 更新失败弹出提示框
            simpleTools.informationDialog(Alert.AlertType.ERROR, "提示", "错误", "修改失败！");
        }
    }

    // 【删除】按钮的事件监听器
    public void do_deleteButton_event(ActionEvent event) {
        // 获取要删除的id
        String id = idTextField.getText();
        // 组装删除的SQL语句
        String sql1 = "set FOREIGN_KEY_CHECKS=0;";
        String deleteSQL = "delete from tb_booktype where btId=" + id + ";";
        String sql2 = "set FOREIGN_KEY_CHECKS=1;";
        // 弹出确认框获取用户是否确认删除
        boolean is = simpleTools.informationDialog(Alert.AlertType.WARNING, "提示", "警告", "是否删除？");
        // 对删除结果进行判断
        if (is) {
            // 执行删除操作
            bookTypeDao.dataChange(sql1);
            boolean isOK = bookTypeDao.dataChange(deleteSQL);
            bookTypeDao.dataChange(sql2);
            // 对删除结果进行判断
            if (isOK) {
                // 删除成功则初始化表格数据，刷新表格
                initialize(null, null);
                // 清空用户输入
                idTextField.setText("");
                bookTypeNameTextField2.setText("");
                descriptionTextArea.setText("");
                // 弹出删除成功的提示框
                simpleTools.informationDialog(Alert.AlertType.INFORMATION, "提示", "信息", "删除成功！");
            } else {
                // 弹出删除失败的提示框
                simpleTools.informationDialog(Alert.AlertType.ERROR, "提示", "错误", "删除失败！");
            }
        } else {
            return;
        }

    }

    // 选中行后将选中行内容显示在下面的文本框中
    public void showBookTypeDetails(BookTypeBeanTableData bookTypeBeanTableData) {
        // 判断是否选中
        if (bookTypeBeanTableData == null) {
            return;
        } else {
            // 如果表格行被选中，则将数据显示在下面的文本框中
            idTextField.setText(bookTypeBeanTableData.getBookTypeId());
            bookTypeNameTextField2.setText(bookTypeBeanTableData.getBookTypeName());
            descriptionTextArea.setText(bookTypeBeanTableData.getBookTypeDesciption());
        }
    }
}
