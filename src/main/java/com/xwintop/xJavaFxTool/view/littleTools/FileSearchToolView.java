package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @ClassName: FileSearchToolView
 * @Description: 文件搜索工具
 * @author: xufeng
 * @date: 2019/7/18 10:21
 */

@Getter
@Setter
public abstract class FileSearchToolView implements Initializable {
    @FXML
    protected TextField searchContentTextField;
    @FXML
    protected CheckBox autoRefreshIndexCheckBox;
    @FXML
    protected Button refreshIndexButton;
    @FXML
    protected CheckBox regularCheckBox;
    @FXML
    protected CheckBox matchCaseCheckBox;
    @FXML
    protected CheckBox fullTextMatchingCheckBox;
    @FXML
    protected ChoiceBox showHideFileChoice;
    @FXML
    protected ChoiceBox fileTypeChoiceBox;
    @FXML
    protected TextField searchDirectoryTextField;
    @FXML
    protected Button searchDirectoryButton;
    @FXML
    protected TableView<Map<String, String>> searchResultTableVIew;
    @FXML
    protected TableColumn<Map<String, String>, String> fileNameTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> absolutePathTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> fileSizeTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> lastModifiedTableColumn;
    @FXML
    protected Label searchTextLabel;

}