package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.beans.BookBeanTableData;
import com.xwintop.xJavaFxTool.beans.BookTypeBean;
import com.xwintop.xJavaFxTool.dao.BookDao;
import com.xwintop.xJavaFxTool.dao.BookTypeDao;
import com.xwintop.xJavaFxTool.utils.SimpleTools;
import com.xwintop.xJavaFxTool.view.littleTools.BookManageFrameView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
public class BookManageFrameController extends BookManageFrameView {
    public static AnchorPane initBookManageFrame() {
        return SimpleTools.loadByFxml("/com/xwintop/xJavaFxTool/fxmlView/littleTools/bookManageFrame.fxml");
    }

    private SimpleTools simpleTools = new SimpleTools();
    private BookDao bookDao = new BookDao();

    /**
     * 初始化图书维护界面控件
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 批量为按钮添加图标
        simpleTools.setLabeledImage(new Labeled[]{alterButton, delteButton, resetButton2}, new String[]{"/BookManageSystem/images/edit.png", "/BookManageSystem/images/delete.png", "/BookManageSystem/images/reset.png"});
        // 设置显示id号的文本框不可编辑
        idTextField.setDisable(true);
        // 查询图书信息的SQL语句
        String sql = "select bId,bBookName,bAuthor,bSex,bPrice,bBookDescription,btName from tb_book,tb_booktype where tb_book.btId=tb_booktype.btId;";
        // 填充表格数据，初始化表格视图
        simpleTools.setBookTableViewData(bookManageTableView
                , simpleTools.getBookTableViewData(sql)
                , idTableColumn
                , bookNameTableColumn
                , bookAuthorTableColumn
                , authorSexTableColumn
                , bookPriceTableColumn
                , bookDescriptionTableColumn
                , bookTypeTableColumn
        );

        // 查询图书类别的SQL语句
        String getBookTypeSQL = "select * from tb_booktype";
        List bookTypeList = new BookTypeDao().getRecordsDataBySql(getBookTypeSQL);
        String[] typeNames = new String[bookTypeList.size()];
        for (int i = 0; i < bookTypeList.size(); i++) {
            BookTypeBean bookTypeBean = (BookTypeBean) bookTypeList.get(i);
            typeNames[i] = bookTypeBean.getBookTypeName();
        }
        // 为下拉列表框填充选项
        simpleTools.addComboBoxItems(bookTypeComboBox, typeNames);
        simpleTools.addComboBoxItems(bookTypeComboBox2, typeNames);

        // 为表格注册事件监听器
        bookManageTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showBookDetails(newValue));
    }

    // 【查询】按钮的事件监听器
    public void do_checkButton_event(ActionEvent event) {
        // 查询SQL语句
        String sql = "select bId,bBookName,bAuthor,bSex,bPrice,bBookDescription,btName from tb_book,tb_booktype where tb_book.btId=tb_booktype.btId ";
        // 判断用户是否输入图书名称，模糊查询
        if (!simpleTools.isEmpty(bookNameTextField.getText())) {
            sql += " and bBookName like '%" + bookNameTextField.getText() + "%'";
        }
        // 判断用户是否输入作者名称，模糊查询
        if (!simpleTools.isEmpty(bookAuthorTextField.getText())) {
            sql += " and bAuthor like '%" + bookAuthorTextField.getText() + "%'";
        }
        // 判断用户是否选择图书类别
        String booktype = (String) bookTypeComboBox.getSelectionModel().selectedItemProperty().getValue();
        if (!simpleTools.isEmpty(booktype)) {
            sql += " and btName='" + booktype + "';";
        }
        // 通过SQL语句查询到的数据重新填充表格，刷新表格显示的数据
        simpleTools.setBookTableViewData(bookManageTableView
                , simpleTools.getBookTableViewData(sql)
                , idTableColumn
                , bookNameTableColumn
                , bookAuthorTableColumn
                , authorSexTableColumn
                , bookPriceTableColumn
                , bookDescriptionTableColumn
                , bookTypeTableColumn
        );
    }

    // 【修改】按钮的事件监听器
    public void do_alterButton_event(ActionEvent event) {
        // 获取用户输入的修改数据
        String id = idTextField.getText();
        String bookName = bookNameTextField2.getText();
        String authorSex = "";
        if (maleRadioButton.isSelected()) {
            authorSex = maleRadioButton.getText();
        } else if (femaleRadioButton.isSelected()) {
            authorSex = femaleRadioButton.getText();
        }
        String price = priceTextField.getText();
        String bookAuthor = bookAuthorTextField2.getText();
        String bookType = (String) bookTypeComboBox2.getSelectionModel().selectedItemProperty().getValue();
        String description = bookDescriptionTextArea.getText();
        // 组装SQL语句
        String bookTypeSQL = "select * from tb_booktype where btName='" + bookType + "';";
        List bookTypeList = new BookTypeDao().getRecordsDataBySql(bookTypeSQL);
        BookTypeBean bookTypeBean = (BookTypeBean) bookTypeList.get(0);
        // 获取图书类别id
        int bookTypeId = bookTypeBean.getBookTypeId();
        // 组装修改SQL语句
        String alterSQL =
                "update tb_book set bBookName='" + bookName + "',bAuthor='" + bookAuthor + "',bSex='" + authorSex +
                        "',bPrice=" + price + ",bBookDescription='" + description + "',btId=" + bookTypeId + " where " +
                        "bId=" + id + ";";
        // 执行SQL语句并返回结果
        boolean isOK = bookDao.dataChange(alterSQL);
        // 对结果进行判断
        if (isOK) {
            // 修改成功则重新刷新表格，并重置用户输入
            initialize(null, null);
            do_resetButton2_event(null);
            simpleTools.informationDialog(Alert.AlertType.INFORMATION, "提示", "信息", "修改成功！");
        } else {
            // 修改失败则弹出提示框
            simpleTools.informationDialog(Alert.AlertType.ERROR, "提示", "错误", "修改失败！");
        }
    }

    // 【删除】按钮的事件监听器
    public void do_delteButton_event(ActionEvent event) {
        // 获取id文本框的值
        String id = idTextField.getText();
        // 组装SQL语句，通过id来删除记录
        String deleteSQL = "delete from tb_book where bId=" + id + ";";
        // 执行删除操作
        boolean isOK = bookDao.dataChange(deleteSQL);
        // 对结果进行判断处理
        if (isOK) {
            // 删除成功则刷新表格
            initialize(null, null);
            do_resetButton2_event(null);
            simpleTools.informationDialog(Alert.AlertType.INFORMATION, "提示", "信息", "删除成功！");
        } else {
            // 删除失败弹出提示框
            simpleTools.informationDialog(Alert.AlertType.ERROR, "提示", "错误", "删除失败！");
        }
    }

    // 【重置】按钮的事件监听器
    public void do_resetButton2_event(ActionEvent event) {
        // 清空用户输入
        simpleTools.clearTextField(idTextField, bookNameTextField2, priceTextField, bookAuthorTextField2,
                bookDescriptionTextArea);
        simpleTools.clearSelectedRadioButton(femaleRadioButton, maleRadioButton);
        simpleTools.clearSelectedComboBox(bookTypeComboBox2);
    }

    // 【重置】按钮的事件监听器
    public void do_resetButton_event(ActionEvent event) {
        // 清空用户并刷新表格
        simpleTools.clearTextField(bookNameTextField, bookAuthorTextField);
        simpleTools.clearSelectedComboBox(bookTypeComboBox);
        initialize(null, null);
    }

    // 选中某行后行内容显示在下面文本框中
    public void showBookDetails(BookBeanTableData bookBeanTableData) {
        // 判断用户是否选中表格中的某一行
        if (bookManageTableView.getSelectionModel().getSelectedIndex() < 0) {
            return;
        } else {
            // 如果选中表格中的某一行，则将选中行的数据显示在下面的文本框中
            idTextField.setText(bookBeanTableData.getBookId());
            bookNameTextField2.setText(bookBeanTableData.getBookName());
            if (bookBeanTableData.getBookAuthorSex().equals("男")) {
                maleRadioButton.setSelected(true);
            } else if (bookBeanTableData.getBookAuthorSex().equals("女")) {
                femaleRadioButton.setSelected(true);
            }
            priceTextField.setText(bookBeanTableData.getBookPrice());
            bookAuthorTextField2.setText(bookBeanTableData.getBookAuthor());
            // 设置分类
            String str = bookBeanTableData.getBookType();
            int index = 0;
            List inputList = FXCollections.observableArrayList(bookTypeComboBox.getItems());
            for (int i = 0; i < inputList.size(); i++) {
                if (str.equals(inputList.get(i))) {
                    index = i;
                }
            }
            bookTypeComboBox2.getSelectionModel().select(index);
            bookDescriptionTextArea.setText(bookBeanTableData.getBookDescription());
        }
    }
}
