package com.xwintop.xJavaFxTool.view.developTools;

import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: DirectoryTreeToolView
 * @Description: 文件列表生成器工具
 * @author: xufeng
 * @date: 2017年11月13日 下午4:34:11
 */
@Getter
@Setter
public abstract class DirectoryTreeToolView implements Initializable {

    @FXML
    protected TextField projectPathTextField;
    @FXML
    protected Button projectPathButton;
    @FXML
    protected CheckBox includeSubdirectoryCheckBox;
    @FXML
    protected CheckBox isFileCheckBox;
    @FXML
    protected CheckBox isDirCheckBox;
    @FXML
    protected CheckBox showHideFileCheckBox;
    @FXML
    protected CheckBox notShowEmptyListCheckBox;
    @FXML
    protected Button reloadButton;
    @FXML
    protected TextArea showDirectoryTreeTextArea;
    @FXML
    protected CheckBox showDirDepthCheckBox;
    @FXML
    protected Spinner<Integer> showDirDepthSpinner;
    @FXML
    protected ChoiceBox<String> filtrationTypeChoiceBox;
    @FXML
    protected ChoiceBox<String> scopeTypeChoiceBox;
    @FXML
    protected TextField filtrationConditionTextField;
    @FXML
    protected Button filtrationAddButton;
    @FXML
    protected CheckBox filtrationUsingCheckBox;
    @FXML
    protected TableView<Map<String, String>> tableViewMain;
    @FXML
    protected TableColumn<Map<String, String>, String> filtrationTypeTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> filtrationConditionTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> scopeTypeTableColumn;
    @FXML
    protected CheckBox showFileLengthCheckBox;
    @FXML
    protected CheckBox showModifyCheckBox;
    @FXML
    protected CheckBox showPermissionCheckBox;
    @FXML
    protected Label totalDirNumLabel;
    @FXML
    protected Label totalFileNumLabel;
}