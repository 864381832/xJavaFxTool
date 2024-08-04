package com.xwintop.xJavaFxTool.view.liteappcode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public abstract class LiteappCodeView implements Initializable {
	@FXML
	protected TextField txtPath;
	
	@FXML
	protected TextField txtWidth;
	
	@FXML
	protected TextField txtAppid;
	@FXML
	protected TextField txtAppSecret;
	@FXML
	protected Button btnGen;
	@FXML
	protected ImageView imgQrCode;
	@FXML
	protected CheckBox chkIsHyaline;
	@FXML
	protected ColorPicker pickColor;
	@FXML
	protected TextField txtScene;

}