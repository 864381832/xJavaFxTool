package com.xwintop.xJavaFxTool.view.assistTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @ClassName: IdiomDataToolView
 * @Description: 成语字典工具
 * @author: xufeng
 * @date: 2019/11/10 0010 22:06
 */

@Getter
@Setter
public abstract class IdiomDataToolView implements Initializable {
    @FXML
    protected TextField index1TextField;
    @FXML
    protected TextField index2TextField;
    @FXML
    protected TextField index3TextField;
    @FXML
    protected TextField index4TextField;
    @FXML
    protected TextField selectWordTextField;
    @FXML
    protected Button selectButton;
    @FXML
    protected Button clearButton;
    @FXML
    protected TableView<Map<String, String>> idiomDataTableView;
    @FXML
    protected TableColumn<Map<String, String>, String> wordTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> pinyinTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> explanationTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> derivationTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> exampleTableColumn;

}