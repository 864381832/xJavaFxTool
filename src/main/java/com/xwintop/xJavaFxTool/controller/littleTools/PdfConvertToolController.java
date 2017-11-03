package com.xwintop.xJavaFxTool.controller.littleTools;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.xwintop.xJavaFxTool.services.littleTools.PdfConvertToolService;
import com.xwintop.xJavaFxTool.view.littleTools.PdfConvertToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: PdfConvertToolController
 * @Description: pdf转换工具
 * @author: xufeng
 * @date: 2017年11月3日 下午6:12:52
 */
@Getter
@Setter
public class PdfConvertToolController extends PdfConvertToolView {
	private PdfConvertToolService pdfConvertToolService = new PdfConvertToolService(this);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
		initService();
	}

	private void initView() {
	}

	private void initEvent() {
		FileChooserUtil.setOnDrag(fileOriginalPathTextField, FileChooserUtil.FileType.FILE);
		FileChooserUtil.setOnDrag(fileTargetPathTextField, FileChooserUtil.FileType.FOLDER);
	}

	private void initService() {
	}

	@FXML
	private void fileOriginalPathAction(ActionEvent event) {
		File file = FileChooserUtil.chooseFile();
		if (file != null) {
			fileOriginalPathTextField.setText(file.getPath());
		}
	}

	@FXML
	private void fileTargetPathAction(ActionEvent event) {
		File file = FileChooserUtil.chooseDirectory();
		if (file != null) {
			fileTargetPathTextField.setText(file.getPath());
		}
	}

	@FXML
	private void pdfToImageAction(ActionEvent event) {
		pdfConvertToolService.pdfToImageAction();
	}

	@FXML
	private void pdfToTxtAction(ActionEvent event) {
		pdfConvertToolService.pdfToTxtAction();
	}

	@FXML
	private void pdfToWordAction(ActionEvent event) {
		pdfConvertToolService.pdfToWordAction();
	}

	@FXML
	private void wordToPdfAction(ActionEvent event) {
		pdfConvertToolService.wordToPdfAction();
	}
}