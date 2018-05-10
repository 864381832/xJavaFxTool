package com.xwintop.xJavaFxTool.controller.littleTools;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

import com.jfoenix.controls.JFXComboBox;
import com.xwintop.xJavaFxTool.services.littleTools.TimeToolService;
import com.xwintop.xJavaFxTool.view.littleTools.TimeToolView;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.time.DateUtils;

/**
 * @ClassName: TimeToolController
 * @Description: 时间转换工具
 * @author: xufeng
 * @date: 2017/12/14 16:50
 */

@Getter
@Setter
@Log4j
public class TimeToolController extends TimeToolView {
    private TimeToolService timeToolService = new TimeToolService(this);
    /**
     * 时间格式.
     */
    private String[] timeFormatter = new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy年MM月dd日HH时mm分ss秒", "yyyy-MM-dd", "yyyy年MM月dd日"};
    //时间后缀格式
    private String[] timeSuffixFormatter = new String[]{"天", "周", "月", "年", "时", "分", "秒", "毫秒", "时间戳"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        choiceBoxTimeZone.getItems().addAll(TimeZone.getAvailableIDs());
        choiceBoxTimeZone.setValue(TimeZone.getDefault().getID());
        choiceBoxTimeFormatter.getItems().addAll(timeFormatter);
        choiceBoxTimeFormatter.getSelectionModel().select(0);
        textFileldTimeStr.setText(new SimpleDateFormat(choiceBoxTimeFormatter.getValue()).format(new Date()));
        textFileldTimeStr2.setText(Long.toString(System.currentTimeMillis()));
        addTimeChoiceBox.getItems().addAll(timeSuffixFormatter);
        addTimeChoiceBox.getSelectionModel().select(0);
    }

    private void initEvent() {
    }

    private void initService() {

    }

    @FXML
    private void copyTimeStr(ActionEvent event) {
        StringSelection selection = new StringSelection(textFileldTimeStr.getText());
        // 获取系统剪切板，复制时间戳
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
    }

    @FXML
    private void convert(ActionEvent event) {
        timeToolService.convert();
    }

    @FXML
    private void copyTimeStr2(ActionEvent event) {
        StringSelection selection = new StringSelection(textFileldTimeStr2.getText());
        // 获取系统剪切板，复制时间戳
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
    }

    @FXML
    private void revert(ActionEvent event) {
        timeToolService.revert();
    }

    @FXML
    private void calculatePoorAction(ActionEvent event) {
        timeToolService.calculatePoorAction();
    }

    @FXML
    private void addTimeAction(ActionEvent event) {
        timeToolService.addTimeAction();
    }

}
