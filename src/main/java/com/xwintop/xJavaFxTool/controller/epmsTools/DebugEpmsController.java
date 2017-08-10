package com.xwintop.xJavaFxTool.controller.epmsTools;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;

import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class DebugEpmsController implements Initializable {
	@FXML
	private TextField textFieldEpmsStart;
	@FXML
	private Button buttonEpmsStartPathChoose;
	@FXML
	private Button buttonEpmsStart;
	@FXML
	private TextField textFieldCopyFile1;
	@FXML
	private Button buttonCopyFile1Choose;
	@FXML
	private TextField textFieldCopyFile2;
	@FXML
	private Button buttonCopyFile2Choose;
	@FXML
	private Button buttonCopyFile;
	@FXML
	private TextField textFieldCopyFolder1;
	@FXML
	private Button buttonCopyFolder1Choose;
	@FXML
	private TextField textFieldCopyFolder2;
	@FXML
	private Button buttonCopyFolder2Choose;
	@FXML
	private Button buttonCopyFolder;
	@FXML
	private TextField textFieldEpmsCompile;
	@FXML
	private Button buttonEpmsCompileChoose;
	@FXML
	private Button buttonEpmsCompile;
	@FXML
	private TextField textFieldCopyJar1;
	@FXML
	private TextField textFieldCopyJar2;
	@FXML
	private Button buttonCopyJar2Choose;
	@FXML
	private Button buttonCopyJar;
	@FXML
	private Button buttonCopyJar1Choose;
	@FXML
	private Button buttonEpmsRestart;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initEvent();
		initData();
	}

	private void initEvent() {
		FileChooserUtil.setOnDrag(textFieldEpmsStart, FileChooserUtil.FileType.FILE);
		FileChooserUtil.setOnDrag(textFieldCopyFile1, FileChooserUtil.FileType.FILE);
		FileChooserUtil.setOnDrag(textFieldCopyFile2, FileChooserUtil.FileType.FOLDER);
		FileChooserUtil.setOnDrag(textFieldCopyFolder1, FileChooserUtil.FileType.FOLDER);
		FileChooserUtil.setOnDrag(textFieldCopyFolder2, FileChooserUtil.FileType.FOLDER);
		FileChooserUtil.setOnDrag(textFieldEpmsCompile, FileChooserUtil.FileType.FOLDER);
		FileChooserUtil.setOnDrag(textFieldCopyJar1, FileChooserUtil.FileType.FILE);
		FileChooserUtil.setOnDrag(textFieldCopyJar2, FileChooserUtil.FileType.FOLDER);
	}

	private void initData(){
		try {
			Configuration configuration = new PropertiesConfiguration(ConfigureUtil.getConfigurePath("debugEpmsConfigure.properties"));
			textFieldEpmsStart.setText(configuration.getString("textFieldEpmsStart"));
			textFieldCopyFile1.setText(configuration.getString("textFieldCopyFile1"));
			textFieldCopyFile2.setText(configuration.getString("textFieldCopyFile2"));
			textFieldCopyFolder1.setText(configuration.getString("textFieldCopyFolder1"));
			textFieldCopyFolder2.setText(configuration.getString("textFieldCopyFolder2"));
			textFieldEpmsCompile.setText(configuration.getString("textFieldEpmsCompile"));
			textFieldCopyJar1.setText(configuration.getString("textFieldCopyJar1"));
			textFieldCopyJar2.setText(configuration.getString("textFieldCopyJar2"));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@FXML
	private void epmsStartPathChoose(ActionEvent event) {
		File file = FileChooserUtil.chooseFile();
		if (file != null) {
			textFieldEpmsStart.setText(file.getPath());
		}
	}

	@FXML
	private void epmsStart(ActionEvent event) throws Exception {
		// Runtime.getRuntime().exec(textFieldEpmsStart.getText());
		File file = new File(textFieldEpmsStart.getText());
		// Runtime.getRuntime().exec("cmd /c start java -jar
		// org.eclipse.osgi_3.3.0.v20070530.jar -console", null, new
		// File("C:/osgi"));
		Runtime.getRuntime().exec("cmd /c start java -jar " + file.getName() + " -console", null, file.getParentFile());
	}

	@FXML
	private void copyFile1Choose(ActionEvent event) {
		File file = FileChooserUtil.chooseFile();
		if (file != null) {
			textFieldCopyFile1.setText(file.getPath());
		}
	}

	@FXML
	private void copyFile2Choose(ActionEvent event) {
		File file = FileChooserUtil.chooseDirectory();
		if (file != null) {
			textFieldCopyFile2.setText(file.getPath());
		}
	}

	@FXML
	private void copyFile(ActionEvent event) throws Exception {
		FileUtils.copyFileToDirectory(new File(textFieldCopyFile1.getText()), new File(textFieldCopyFile2.getText()));
	}

	@FXML
	private void copyFolder1Choose(ActionEvent event) {
		File file = FileChooserUtil.chooseDirectory();
		if (file != null) {
			textFieldCopyFolder1.setText(file.getPath());
		}
	}

	@FXML
	private void copyFolder2Choose(ActionEvent event) {
		File file = FileChooserUtil.chooseDirectory();
		if (file != null) {
			textFieldCopyFolder2.setText(file.getPath());
		}
	}

	@FXML
	private void copyFolder(ActionEvent event) throws Exception {
		FileUtils.copyDirectory(new File(textFieldCopyFolder1.getText()), new File(textFieldCopyFolder2.getText()));
	}

	@FXML
	private void epmsCompileChoose(ActionEvent event) {
		File file = FileChooserUtil.chooseDirectory();
		if (file != null) {
			textFieldEpmsCompile.setText(file.getPath());
		}
	}

	@FXML
	private void epmsCompile(ActionEvent event) throws Exception {
		File file = new File(textFieldEpmsCompile.getText());
		Runtime.getRuntime().exec("cmd /c start ant", null, file);
	}

	@FXML
	private void copyJar1Choose(ActionEvent event) {
		File file = FileChooserUtil.chooseFile();
		if (file != null) {
			textFieldCopyJar1.setText(file.getPath());
		}
	}

	@FXML
	private void copyJar2Choose(ActionEvent event) {
		File file = FileChooserUtil.chooseDirectory();
		if (file != null) {
			textFieldCopyJar2.setText(file.getPath());
		}
	}

	@FXML
	private void copyJar(ActionEvent event) throws Exception {
		FileUtils.copyFileToDirectory(new File(textFieldCopyJar1.getText()), new File(textFieldCopyJar2.getText()));
	}

	@FXML
	private void epmsRestart(ActionEvent event) throws Exception {
	}

	@FXML
	private void saveDebugEpmsConfigure(ActionEvent event) throws Exception {
		FileUtils.touch(ConfigureUtil.getConfigureFile("debugEpmsConfigure.properties"));
		PropertiesConfiguration prop = new PropertiesConfiguration(ConfigureUtil.getConfigurePath("debugEpmsConfigure.properties"));
//		Properties prop = new Properties();
		// 保存属性到b.properties文件
//		FileOutputStream oFile = new FileOutputStream("debugEpmsConfigure.properties", false);// true表示追加打开
		prop.setProperty("textFieldEpmsStart", textFieldEpmsStart.getText());
		prop.setProperty("textFieldCopyFile1", textFieldCopyFile1.getText());
		prop.setProperty("textFieldCopyFile2", textFieldCopyFile2.getText());
		prop.setProperty("textFieldCopyFolder1", textFieldCopyFolder1.getText());
		prop.setProperty("textFieldCopyFolder2", textFieldCopyFolder2.getText());
		prop.setProperty("textFieldEpmsCompile", textFieldEpmsCompile.getText());
		prop.setProperty("textFieldCopyJar1", textFieldCopyJar1.getText());
		prop.setProperty("textFieldCopyJar2", textFieldCopyJar2.getText());
//		prop.store(oFile, "The New properties file");
//		oFile.close();
		prop.save();
	}

}
