package com.xwintop.xJavaFxTool.controller.littleTools;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.xwintop.xJavaFxTool.Main;
import com.xwintop.xJavaFxTool.utils.QRCodeUtil;
import com.xwintop.xJavaFxTool.utils.ScreenShoter;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class QRCodeBuilderController implements Initializable {
	@FXML
	private Button builderButton;
	@FXML
	private Button snapshotButton;
	@FXML
	private TextField contentTextField;
	@FXML
	private ImageView codeImageView;
	@FXML
	private ImageView codeImageView1;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	private void initView() {
	}

	private void initEvent() {
		contentTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
				builderAction(null);
			}
		});
	}

	@FXML
	private void builderAction(ActionEvent event) {
		if (StringUtils.isEmpty(contentTextField.getText())) {
			return;
		}
		try {
			Image image = QRCodeUtil.toImage(contentTextField.getText(), (int) codeImageView.getFitWidth(),
					(int) codeImageView.getFitHeight());
			codeImageView.setImage(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void snapshotAction(ActionEvent event) {
		Platform.setImplicitExit(false);
//		Main.getStage().setIconified(true);;
		Main.getStage().hide();
//		new SnapshotRectUtil(this);
		new ScreenShoter(this);
	}
	
	public void snapshotActionCallBack(Image image) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
//				Main.getStage().setIconified(false);
				Main.getStage().show();
			}
		});
		codeImageView1.setImage(image);
		String code = QRCodeUtil.toDecode(image);
		if(StringUtils.isNotEmpty(code)) {
			contentTextField.setText(QRCodeUtil.toDecode(image));
		}
		Platform.setImplicitExit(true);
	}
}
