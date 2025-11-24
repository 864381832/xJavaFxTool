/**
 *    Copyright (c) [2019] [xufeng]
 *    [x-RegexTester] is licensed under Mulan PSL v2.
 *    You can use this software according to the terms and conditions of the Mulan PSL v2.
 *    You may obtain a copy of Mulan PSL v2 at:
 *             http://license.coscl.org.cn/MulanPSL2
 *    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *    See the Mulan PSL v2 for more details.
 */
package com.xwintop.xJavaFxTool.view.codeTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public abstract class RegexTesterView implements Initializable {
    @FXML
    protected TextField regexTextField;
    @FXML
    protected Button regulatButton;
    @FXML
    protected TableView<Map<String, String>> examplesTableView;
    @FXML
    protected TableColumn<Map<String, String>, String> examplesTableColumn0;
    @FXML
    protected TableColumn<Map<String, String>, String> examplesTableColumn1;
    @FXML
    protected TextArea sourceTextArea;
    @FXML
    protected TextArea matchTextArea;
    @FXML
    protected TableView<Map<String, String>> matchTableView;
    @FXML
    protected TableColumn<Map<String, String>, String> matchTableColumn0;
    @FXML
    protected TableColumn<Map<String, String>, String> matchTableColumn1;
    @FXML
    protected TableColumn<Map<String, String>, String> matchTableColumn2;
    @FXML
    protected TableColumn<Map<String, String>, String> matchTableColumn3;
    @FXML
    protected TableColumn<Map<String, String>, String> matchTableColumn4;
    @FXML
    protected Button resetButton;
    @FXML
    protected Button aboutRegularButton;
    @FXML
    protected CheckBox ignoreCaseCheckBox;
    @FXML
    protected TextField replaceTextField;
    @FXML
    protected CheckBox isReplaceCheckBox;
}
