package com.xwintop.xJavaFxTool.view.index;

import java.util.Map;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: PluginManageView
 * @Description: 插件管理
 * @author: xufeng
 * @date: 2020/1/19 17:41
 */

@Getter
@Setter
public abstract class PluginManageView implements Initializable {

    @FXML
    protected TextField selectPluginTextField;

    @FXML
    protected Button selectPluginButton;

    @FXML
    protected TableView<Map<String, String>> pluginDataTableView;

    @FXML
    protected TableColumn<Map<String, String>, String> nameTableColumn;

    @FXML
    protected TableColumn<Map<String, String>, String> synopsisTableColumn;

    @FXML
    protected TableColumn<Map<String, String>, String> versionTableColumn;

    @FXML
    protected TableColumn<Map<String, String>, String> isDownloadTableColumn;

    @FXML
    protected TableColumn<Map<String, String>, String> isEnableTableColumn;

    @FXML
    protected TableColumn<Map<String, String>, String> downloadTableColumn;

}