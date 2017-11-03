package com.xwintop.xJavaFxTool.view.littleTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

/** 
 * @ClassName: PdfConvertToolView 
 * @Description: pdf转换工具
 * @author: xufeng
 * @date: 2017年11月3日 下午6:13:04  
 */
@Getter
@Setter
public abstract class PdfConvertToolView implements Initializable {
	@FXML
	protected TextField fileOriginalPathTextField;
	@FXML
	protected Button fileOriginalPathButton;
	@FXML
	protected TextField fileTargetPathTextField;
	@FXML
	protected Button fileTargetPathButton;
	@FXML
	protected Button pdfToImageButton;
	@FXML
	protected Button pdfToTxtButton;
	@FXML
	protected Button pdfToWordButton;
	@FXML
	protected Button wordToPdfButton;
}