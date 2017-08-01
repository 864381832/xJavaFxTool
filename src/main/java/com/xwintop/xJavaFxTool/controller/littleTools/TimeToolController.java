package com.xwintop.xJavaFxTool.controller.littleTools;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class TimeToolController implements Initializable {
	/**
     * 时间格式.
     */
    private String[] timeFormatter = new String[]{TimeUtils.Formatter, TimeUtils.Formatter_Millisecond, TimeUtils.Formatter_zh, TimeUtils.Formatter_year, TimeUtils.Formatter_zh_year};

    /**
     * 当前时间格式.
     */
    private String curTimeFormatter = timeFormatter[0];
	@FXML
	private TextField textFileldTimeStr;
	@FXML
	private ChoiceBox<String> choiceBoxTimeFormatter;
	@FXML
	private Button buttonCopy;
	@FXML
	private Button buttonConvert;
	@FXML
	private TextField textFileldTimeStr2;
	@FXML
	private Button buttonCopy2;
	@FXML
	private Button buttonRevert;
	@FXML
	private TextArea textAreaResult;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	private void initView() {
		textFileldTimeStr.setText(new SimpleDateFormat(curTimeFormatter).format(new Date()));
		textFileldTimeStr2.setText(Long.toString(System.currentTimeMillis()));
	}

	private void initEvent() {
	}

	@FXML
	private void copyTimeStr(ActionEvent event) {
        StringSelection selection = new StringSelection(textFileldTimeStr.getText());
        // 获取系统剪切板，复制时间戳
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}

	@FXML
	private void convert(ActionEvent event) {
		curTimeFormatter = choiceBoxTimeFormatter.getValue();
		String timeStr = textFileldTimeStr.getText().trim();
        if (timeStr.length() == 0) {
//            showMessage("没有输入时间字符！", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }
        textFileldTimeStr2.setText("");
        try {
        	textFileldTimeStr2.setText(Long.toString(new SimpleDateFormat(curTimeFormatter).parse(timeStr).getTime()));
        } catch (Exception e) {
//            showExceptionMessage(e);
        }
	}

	@FXML
	private void copyTimeStr2(ActionEvent event) {
		StringSelection selection = new StringSelection(textFileldTimeStr2.getText());
        // 获取系统剪切板，复制时间戳
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}

	@FXML
	private void revert(ActionEvent event) {
		curTimeFormatter = choiceBoxTimeFormatter.getValue();
		String timestamp = textFileldTimeStr2.getText().trim();
        if (timestamp.length() == 0) {
//            showMessage("没有输入时间戳！", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }
        textFileldTimeStr.setText("");
        try {
        	textFileldTimeStr.setText(new SimpleDateFormat(curTimeFormatter).format(new Date(Long.parseLong(timestamp))));
        } catch (Exception e) {
//            showExceptionMessage(e);
        }
	}

	/**
	 * 时间工具类.
	 */
	public class TimeUtils {

		/**
		 * yyyy-MM-dd HH:mm:ss.
		 */
		public static final String Formatter = "yyyy-MM-dd HH:mm:ss";

		/**
		 * yyyy-MM-dd HH:mm:ss.SSS.
		 */
		public static final String Formatter_Millisecond = "yyyy-MM-dd HH:mm:ss.SSS";

		/**
		 * yyyy年MM月dd日HH时mm分ss秒.
		 */
		public static final String Formatter_zh = "yyyy年MM月dd日HH时mm分ss秒";

		/**
		 * yyyy-MM-dd.
		 */
		public static final String Formatter_year = "yyyy-MM-dd";

		/**
		 * yyyy年MM月dd日.
		 */
		public static final String Formatter_zh_year = "yyyy年MM月dd日";

	}

}
