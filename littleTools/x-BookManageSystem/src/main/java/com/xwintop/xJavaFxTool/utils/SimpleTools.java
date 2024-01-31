package com.xwintop.xJavaFxTool.utils;

import com.xwintop.xJavaFxTool.beans.BookBean;
import com.xwintop.xJavaFxTool.beans.BookBeanTableData;
import com.xwintop.xJavaFxTool.beans.BookTypeBean;
import com.xwintop.xJavaFxTool.beans.BookTypeBeanTableData;
import com.xwintop.xJavaFxTool.dao.BookDao;
import com.xwintop.xJavaFxTool.dao.BookTypeDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SimpleTools {
    //加载fxml文件
    public static AnchorPane loadByFxml(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Object.class.getResource(fxmlPath));
            AnchorPane root = loader.load();
            return root;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 操作结果：JavaFX设置按钮、标签等组件的图标
     *
     * @param labeleds   需要设置图标的按钮
     * @param imagePaths 图标的路径
     */
    public void setLabeledImage(Labeled[] labeleds, String[] imagePaths) {
        for (int i = 0; i < labeleds.length; i++) {
            labeleds[i].setGraphic(new ImageView(new Image(imagePaths[i])));
        }
    }

    /**
     * 操作结果：清空文本框组件的内容
     *
     * @param inputControls 文本框或文本域组等
     */
    public void clearTextField(TextInputControl... inputControls) {
        for (TextInputControl inputControl : inputControls) {
            inputControl.setText("");
        }
    }

    /**
     * 操作结果：取消所有单选按钮选择
     *
     * @param toggleButtons 单选按钮组
     */
    public void clearSelectedRadioButton(ToggleButton... toggleButtons) {
        for (ToggleButton toggleButton : toggleButtons) {
            toggleButton.setSelected(false);
        }
    }

    /**
     * 操作结果：取消所有下拉列表框选择
     *
     * @param comboBoxes 下拉列表框组
     */
    public void clearSelectedComboBox(ComboBox... comboBoxes) {
        for (ComboBox comboBox : comboBoxes) {
            // 设置选择的索引为-1，就不会选择任何选择选项了。
            comboBox.getSelectionModel().select(-1);
        }
    }

    /**
     * 操作结果：JavaFX设置菜单项组件的图标
     *
     * @param menuItems  菜单项
     * @param imagePaths 图标的路径
     */
    public void setMenuItemImage(MenuItem[] menuItems, String[] imagePaths) {
        for (int i = 0; i < menuItems.length; i++) {
            menuItems[i].setGraphic(new ImageView(new Image(imagePaths[i])));
        }
    }

    /**
     * 操作结果：JavaFX判断是否为空
     *
     * @param str 文本
     * @return boolean 如果不为空返回true，否则返回false
     */
    public boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * 操作结果：自定义一个JavaFX的对话框
     *
     * @param alterType 对话框类型
     * @param title     对话框标题
     * @param header    对话框头信息
     * @param message   对话框显示内容
     * @return boolean 如果点击了”确定“那么就返回true，否则返回false
     */
    public boolean informationDialog(Alert.AlertType alterType, String title, String header, String message) {
        // 按钮部分可以使用预设的也可以像这样自己 new 一个
        Alert alert = new Alert(alterType, message, new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE), new ButtonType("确定", ButtonBar.ButtonData.YES));
        // 设置对话框的标题
        alert.setTitle(title);
        alert.setHeaderText(header);
        // showAndWait() 将在对话框消失以前不会执行之后的代码
        Optional<ButtonType> buttonType = alert.showAndWait();
        // 根据点击结果返回，如果点击了“确定”就返回true
        return buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES);
    }

    /**
     * 操作结果：JavaFX判断是否登录成功
     *
     * @param userNameTextField 用户名文本框
     * @param passwordTextField 密码文本框
     * @param userName          正确用户名
     * @param password          正确密码
     * @return boolean 如果登录成功返回true，否则返回false
     */
    public boolean isLogIn(TextInputControl userNameTextField, TextInputControl passwordTextField, String userName, String password) {
        SimpleTools simpleTools = new SimpleTools();
        if (simpleTools.isEmpty(userNameTextField.getText())) {
            simpleTools.informationDialog(Alert.AlertType.WARNING, "提示", "警告", "用户名不能为空！");
            return false;
        }
        if (simpleTools.isEmpty(passwordTextField.getText())) {
            simpleTools.informationDialog(Alert.AlertType.WARNING, "提示", "警告", "密码不能为空！");
            return false;
        }
        if (!userNameTextField.getText().equals(userName)) {
            simpleTools.informationDialog(Alert.AlertType.WARNING, "提示", "警告", "用户名不正确！");
            return false;
        }
        if (!passwordTextField.getText().equals(password)) {
            simpleTools.informationDialog(Alert.AlertType.WARNING, "提示", "警告", "密码不正确！");
            return false;
        }
        if (!userNameTextField.getText().equals(userName) && !passwordTextField.getText().equals(password)) {
            simpleTools.informationDialog(Alert.AlertType.ERROR, "提示", "错误", "用户名和密码均不正确！");
            return false;
        }
        if (userNameTextField.getText().equals(userName) && passwordTextField.getText().equals(password)) {
//            return simpleTools.informationDialog(Alert.AlertType.INFORMATION, "提示", "信息", "登录成功！");
            return true;
        }
        return false;
    }

    /**
     * 操作结果：向下拉列表框中添加列表项
     *
     * @param comboBox 下拉列表框
     * @param items    列表项
     */
    public void addComboBoxItems(ComboBox comboBox, Object[] items) {
        // 清除下列列表框中的所有选项
        comboBox.getItems().clear();
        ObservableList options = FXCollections.observableArrayList(items);
        // 添加下拉列表项
        comboBox.setItems(options);
    }

    /**
     * 将数据显示在图书类别表格中
     *
     * @param tableView         表格视图控件
     * @param data              要显示要表格上的数据
     * @param idColumn          ID表格列控件
     * @param nameColumn        图书类别名称列控件
     * @param descriptionColumn 图书类别描述列控件
     */
    public void setBookTypeTableViewData(TableView tableView, ObservableList data, TableColumn<BookTypeBeanTableData, String> idColumn, TableColumn<BookTypeBeanTableData, String> nameColumn, TableColumn<BookTypeBeanTableData, String> descriptionColumn) {
        // 设置id列的数据
        idColumn.setCellValueFactory(cellData -> cellData.getValue().bookTypeIdProperty());
        // 设置图书类别名称列的数据
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().bookTypeNameProperty());
        // 设置图书类别描述列的数据
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().bookTypeDesciptionProperty());
        // 将数据添加到表格控件中
        tableView.setItems(data);
    }

    /**
     * 通过SQL从数据库表中查询图书类别数据并进行封装
     *
     * @param sql SQL语句
     * @return 返回ObservableList<BookTypeBeanTableData>类型的数据
     */
    public ObservableList<BookTypeBeanTableData> getBookTypeTableViewData(String sql) {
        // 实例化BookTypeDao
        BookTypeDao bookTypeDao = new BookTypeDao();
        // 查询图书类别表的所有数据
        List list = bookTypeDao.getRecordsDataBySql(sql);
        // 创建ObservableList<BookTypeBeanTableData>对象
        ObservableList<BookTypeBeanTableData> data = FXCollections.observableArrayList();
        // 循环遍历集合中的数据
        for (int i = 0; i < list.size(); i++) {
            BookTypeBean r = (BookTypeBean) list.get(i);
            // 将数据封装到BookTypeBeanTableData中
            BookTypeBeanTableData td = new BookTypeBeanTableData(String.valueOf(r.getBookTypeId()), r.getBookTypeName(), r.getBookTypeDescription());
            // 将BookTypeBeanTableData对象添加到data中
            data.add(td);
        }
        // 返回数据
        return data;
    }

    /**
     * 将数据显示在图书表格中
     *
     * @param tableView         表格视图控件
     * @param data              要显示要表格上的数据
     * @param idColumn          ID表格列控件
     * @param nameColumn        图书名字表格列控件
     * @param authorColumn      图书作者表格列控件
     * @param sexColumn         图书作者性别表格列控件
     * @param priceColumn       图书价格表格列控件
     * @param descriptionColumn 图书描述表格列控件
     * @param typeColumn        图书类别表格列控件
     */
    public void setBookTableViewData(TableView tableView, ObservableList data, TableColumn<BookBeanTableData, String> idColumn, TableColumn<BookBeanTableData, String> nameColumn, TableColumn<BookBeanTableData, String> authorColumn, TableColumn<BookBeanTableData, String> sexColumn, TableColumn<BookBeanTableData, String> priceColumn, TableColumn<BookBeanTableData, String> descriptionColumn, TableColumn<BookBeanTableData, String> typeColumn) {
        // 设置id列的数据
        idColumn.setCellValueFactory(cellData -> cellData.getValue().bookIdProperty());
        // 设置图书名称列的数据
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().bookNameProperty());
        // 设置图书作者列的数据
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().bookAuthorProperty());
        // 设置图书作者性别列的数据
        sexColumn.setCellValueFactory(cellData -> cellData.getValue().bookAuthorSexProperty());
        // 设置图书价格列的数据
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().bookPriceProperty());
        // 设置图书描述列的数据
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().bookDescriptionProperty());
        // 设置图书类别列的数据
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().bookTypeProperty());
        // 将数据设置到表格视图
        tableView.setItems(data);
    }

    /**
     * 通过SQL从数据库表中查询图书数据并进行封装
     *
     * @param sql SQL语句
     * @return 返回ObservableList<BookBeanTableData>类型的数据
     */
    public ObservableList<BookBeanTableData> getBookTableViewData(String sql) {
        BookDao bookDao = new BookDao();
        List list = bookDao.getRecordsDataBySql(sql);
        ObservableList<BookBeanTableData> data = FXCollections.observableArrayList();
        for (int i = 0; i < list.size(); i++) {
            BookBean r = (BookBean) list.get(i);
            BookBeanTableData td = new BookBeanTableData(String.valueOf(r.getBookId()), r.getBookName(), r.getBookAuthor(), r.getBookAuthorSex(), String.valueOf(r.getBookPrice()), r.getBookDescription(), String.valueOf(r.getBookTypeId()));
            data.add(td);
        }
        return data;
    }
}
