package com.xwintop.xJavaFxTool.view.littleTools;

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
 * @ClassName: Mp3ConvertToolView
 * @Description: mp3格式转换工具
 * @author: xufeng
 * @date: 2019/8/8 0008 20:41
 */

@Getter
@Setter
public abstract class Mp3ConvertToolView implements Initializable {
    @FXML
    protected TableView<Map<String, String>> tableViewMain;
    @FXML
    protected TableColumn<Map<String, String>, String> fileNameTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> absolutePathTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> fileSizeTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> convertStatusTableColumn;
    @FXML
    protected Button addFileButton;
    @FXML
    protected Button addFolderButton;
    @FXML
    protected Button convertButton;
    @FXML
    protected TextField outputFolderTextField;
    @FXML
    protected Button outputFolderButton;
    @FXML
    protected Button openOutputFolderButton;

}