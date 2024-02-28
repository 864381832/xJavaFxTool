/**
 * Copyright (c) [2019] [xufeng]
 * [x-RegexTester] is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.xwintop.xJavaFxTool.controller.codeTools;

import cn.hutool.core.swing.clipboard.ClipboardUtil;
import com.xwintop.xJavaFxTool.view.codeTools.RegexTesterView;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: RegexTesterController
 * @Description: 正则表达式生成工具
 * @author: xufeng
 * @date: 2018/1/21 0021 1:02
 */
@Getter
@Setter
@Slf4j
public class RegexTesterController extends RegexTesterView {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initView();
            initEvent();
        } catch (Exception e) {
            log.error("initialize error", e);
        }
    }

    private void initView() throws Exception {
        examplesTableColumn0.setCellValueFactory(new MapValueFactory("column0"));
        examplesTableColumn1.setCellValueFactory(new MapValueFactory("column1"));
        PropertiesConfiguration pcfg = new Configurations().properties(RegexTesterController.class.getResource("/data/regexData.properties"));
        pcfg.getKeys().forEachRemaining((String key) -> {
            Map<String, String> map = new HashMap<String, String>();
            map.put("column0", key);
            map.put("column1", pcfg.getString(key));
            examplesTableView.getItems().add(map);
        });
        matchTableColumn0.setCellValueFactory(new MapValueFactory("column0"));
        matchTableColumn1.setCellValueFactory(new MapValueFactory("column1"));
        matchTableColumn2.setCellValueFactory(new MapValueFactory("column2"));
        matchTableColumn3.setCellValueFactory(new MapValueFactory("column3"));
        matchTableColumn4.setCellValueFactory(new MapValueFactory("column4"));
        matchTableColumn1.setCellFactory(TextFieldTableCell.<Map<String, String>>forTableColumn());
        matchTableColumn2.setCellFactory(TextFieldTableCell.<Map<String, String>>forTableColumn());
    }

    private void initEvent() {
        examplesTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    regexTextField.setText(examplesTableView.getSelectionModel().getSelectedItem().get("column1"));
                }
            }
        });
        ContextMenu contextMenu = new ContextMenu();
        JavaFxViewUtil.addMenuItem(contextMenu, "复制表格内容", event -> {
            StringBuilder strb = new StringBuilder();
            for (int i = -1; i < matchTableView.getItems().size(); i++) {
                for (Object visibleLeafColumn : matchTableView.getVisibleLeafColumns()) {
                    TableColumn tableColumn = (TableColumn) visibleLeafColumn;
                    if (i == -1) {
                        strb.append(tableColumn.getText()).append("\t");
                    } else {
                        strb.append(tableColumn.getCellData(i)).append("\t");
                    }
                }
                strb.append("\n");
            }
            ClipboardUtil.setStr(strb.toString());
        });
        matchTableView.setContextMenu(contextMenu);
        regexTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            regulatAction(null);
        });
        sourceTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            regulatAction(null);
        });
    }

    @FXML
    private void regulatAction(ActionEvent event) {
        String regexText = regexTextField.getText().trim();
        String sourceText = sourceTextArea.getText().trim();
        String replaceText = replaceTextField.getText();
        matchTableView.getItems().clear();
        matchTextArea.setText(null);
        Pattern p = null;
        if (ignoreCaseCheckBox.isSelected()) {
            p = Pattern.compile(regexText, Pattern.CASE_INSENSITIVE); // 不区分大小写
        } else {
            p = Pattern.compile(regexText);
        }
        // 用Pattern类的matcher()方法生成一个Matcher对象
        Matcher m = p.matcher(sourceText);
        StringBuffer sb = new StringBuffer();
        StringBuffer rsb = new StringBuffer(); // 替换匹配
        // 使用find()方法查找第一个匹配的对象
        boolean result = m.find();
        // 使用循环找出模式匹配的内容替换之,再将内容加到sb里
        int cnt = 0; // 匹配总数
        int start = 0;
        int end = 0;
        while (result) {
            m.appendReplacement(rsb, replaceText); // 替换匹配
            cnt++;
            sb.append("\n");
            start = m.start();
            end = m.end();
            String matchText = sourceText.substring(start, end);
            sb.append("Match[").append(cnt).append("]: ");
            sb.append(matchText);
            sb.append(" [start: ").append(start).append(", end: ").append(end).append("]");
            String str0 = m.group();
            String str1 = "";
            try {
                str1 = m.group(1);                                //捕获的子序列
            } catch (Exception e) {
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("column0", Integer.toString(cnt));
            map.put("column1", str0);
            map.put("column2", str1);
            map.put("column3", Integer.toString(start));
            map.put("column4", Integer.toString(end));
            matchTableView.getItems().add(map);
            result = m.find();
        }
        sb.insert(0, "\n匹配总数: " + cnt);
        sb.insert(0, "\t直接匹配判断: " + sourceText.matches(regexText));
        if (isReplaceCheckBox.isSelected() && replaceText.length() != 0) {
            m.appendTail(rsb);
            sb.append("\n\n替换匹配后内容: \n").append(rsb);
        }
        matchTextArea.setText(sb.length() > 0 ? sb.substring(1) : "");
    }

    @FXML
    private void resetAction(ActionEvent event) {
        regexTextField.setText(null);
        sourceTextArea.setText(null);
        matchTextArea.setText(null);
        matchTableView.getItems().clear();
    }

    @FXML
    private void aboutRegularAction(ActionEvent event) {
        String url = RegexTesterController.class.getResource("/com/xwintop/xJavaFxTool/web/littleTools/正则表达式教程.html").toExternalForm();
//        JavaFxViewUtil.openUrlOnWebView(url, "正则表达式教程", null);
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        webEngine.load(url);
        JavaFxViewUtil.getNewStage("正则表达式教程", null, (Parent) (new BorderPane(browser)));
    }
}
