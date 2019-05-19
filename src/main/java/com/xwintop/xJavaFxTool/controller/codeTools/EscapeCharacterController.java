package com.xwintop.xJavaFxTool.controller.codeTools;

import java.net.URL;
import java.util.ResourceBundle;

import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import org.apache.commons.lang.StringEscapeUtils;

import com.xwintop.xJavaFxTool.view.codeTools.EscapeCharacterView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/** 
 * @ClassName: EscapeCharacterController 
 * @Description: 转义字符
 * @author: xufeng
 * @date: 2017年8月17日 下午9:55:25  
 */
public class EscapeCharacterController extends EscapeCharacterView {
	/**
	 * 字符类型.
	 */
	private String[] characterTypes = new String[] { "HTML", "XML", "JAVA", "JavaScript", "CSV", "Sql" };
	/**
	 * HTML转义说明.
	 */
	private static final String ESCAPE_HELP_HTML = "HTML            See: http://www.w3.org/TR/html4/sgml/entities.html"
			+ "\n"
			+ "\n <          >            &              \"          no-break space       em space      en space          ®             ©             ™"
			+ "\n&lt;      &gt;      &amp;      &quot;            &nbsp;                &emsp;          &ensp;        &reg;      &copy;     &trade;";
	/**
	 * XML转义说明.
	 */
	private static final String ESCAPE_HELP_XML = "XML              See: http://www.xmlnews.org/docs/xml-basics.html"
			+ "\n" + "\n <          >            &               \"               '"
			+ "\n&lt;      &gt;      &amp;      &quot;      &apos;";
	/**
	 * JAVA转义说明.
	 */
	private static final String ESCAPE_HELP_JAVA = "JAVA             See: http://docs.oracle.com/javase/tutorial/java/data/characters.html"
			+ "\n" + "\n回车符     换行符     制表符     单引号     双引号     反斜杠     退格符     换页符"
			+ "\n    \\r             \\n             \\t              \\'             \\\"              \\\\             \\b             \\f";
	/**
	 * JavaScript转义说明.
	 */
	private static final String ESCAPE_HELP_JavaScript = "JavaScript     See: http://www.w3schools.com/js/js_strings.asp"
			+ "\n" + "\n回车符     换行符     制表符     单引号     双引号     反斜杠     退格符     换页符"
			+ "\n    \\r             \\n             \\t              \\'             \\\"              \\\\             \\b             \\f";
	/**
	 * CSV转义说明.
	 */
	private static final String ESCAPE_HELP_CSV = "";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	private void initView() {
		characterTypesChoiceBox.getItems().addAll(characterTypes);
		characterTypesChoiceBox.setValue(characterTypes[0]);
		helpTextArea.setText(ESCAPE_HELP_HTML);
	}

	private void initEvent() {
		characterTypesChoiceBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (characterTypes[0].equals(newValue)) {
					helpTextArea.setText(ESCAPE_HELP_HTML);
				} else if (characterTypes[1].equals(newValue)) {
					helpTextArea.setText(ESCAPE_HELP_XML);
				} else if (characterTypes[2].equals(newValue)) {
					helpTextArea.setText(ESCAPE_HELP_JAVA);
				} else if (characterTypes[3].equals(newValue)) {
					helpTextArea.setText(ESCAPE_HELP_JavaScript);
				} else if (characterTypes[4].equals(newValue)) {
					helpTextArea.setText(ESCAPE_HELP_CSV);
				}
			}
		});
		JavaFxViewUtil.setPropertyAddChangeListener(unescapeTextArea, () -> {
			switchAction(null);
		});
		JavaFxViewUtil.setPropertyAddChangeListener(escapeTextArea, () -> {
			restoreAction(null);
		});
	}

	@FXML // 转换字符
	private void switchAction(ActionEvent event) {
		String string = unescapeTextArea.getText();
		String characterTypesString = characterTypesChoiceBox.getValue();
		String unescape = null;
		if (characterTypes[0].equals(characterTypesString)) {
			unescape = StringEscapeUtils.escapeHtml(string);
		} else if (characterTypes[1].equals(characterTypesString)) {
			unescape = StringEscapeUtils.escapeXml(string);
		} else if (characterTypes[2].equals(characterTypesString)) {
			unescape = StringEscapeUtils.escapeJava(string);
		} else if (characterTypes[3].equals(characterTypesString)) {
			unescape = StringEscapeUtils.escapeJavaScript(string);
		} else if (characterTypes[4].equals(characterTypesString)) {
			unescape = StringEscapeUtils.escapeCsv(string);
		} else if (characterTypes[5].equals(characterTypesString)) {
			unescape = StringEscapeUtils.escapeSql(string);
		}
		escapeTextArea.setText(unescape);
	}

	@FXML // 还原
	private void restoreAction(ActionEvent event) {
		String string = escapeTextArea.getText();
		String characterTypesString = characterTypesChoiceBox.getValue();
		String unescape = null;
		if (characterTypes[0].equals(characterTypesString)) {
			unescape = StringEscapeUtils.unescapeHtml(string);
		} else if (characterTypes[1].equals(characterTypesString)) {
			unescape = StringEscapeUtils.unescapeXml(string);
		} else if (characterTypes[2].equals(characterTypesString)) {
			unescape = StringEscapeUtils.unescapeJava(string);
		} else if (characterTypes[3].equals(characterTypesString)) {
			unescape = StringEscapeUtils.unescapeJavaScript(string);
		} else if (characterTypes[4].equals(characterTypesString)) {
			unescape = StringEscapeUtils.unescapeCsv(string);
		} else if (characterTypes[5].equals(characterTypesString)) {
			unescape = characterTypesString + "转义字符不能进行还原";
		}
		unescapeTextArea.setText(unescape);
	}

	@FXML
	private void cleanAllAction(ActionEvent event) {
		unescapeTextArea.setText(null);
		escapeTextArea.setText(null);
	}
}