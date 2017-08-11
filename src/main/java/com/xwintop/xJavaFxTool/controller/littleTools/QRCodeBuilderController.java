package com.xwintop.xJavaFxTool.controller.littleTools;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

import com.xwintop.xJavaFxTool.utils.QRCodeUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
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
	private TextField contentTextField;
	@FXML
	private ImageView codeImageView;

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
		try {
			BufferedImage bf = QRCodeUtil.toBufferedImage(contentTextField.getText(), (int) codeImageView.getFitWidth(),
					(int) codeImageView.getFitHeight());
			Image image = SwingFXUtils.toFXImage(bf, null);
			codeImageView.setImage(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
