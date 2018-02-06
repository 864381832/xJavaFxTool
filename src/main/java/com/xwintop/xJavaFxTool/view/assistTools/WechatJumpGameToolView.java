package com.xwintop.xJavaFxTool.view.assistTools;

import com.jfoenix.controls.JFXSlider;
import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
/**
 * @ClassName: WechatJumpGameToolView
 * @Description: 微信跳一跳助手
 * @author: xufeng
 * @date: 2018/2/6 10:32
 */

@Getter
@Setter
public abstract class WechatJumpGameToolView implements Initializable {
    @FXML
    protected Label adbConnectHintLabel;
    @FXML
    protected JFXSlider waitTimeSlider;
    @FXML
    protected JFXCheckBox autoRestartCheckBox;
    @FXML
    protected Label errorInfoLabel;
    @FXML
    protected Button adbConnectButton;
    @FXML
    protected Button adbRunButton;

}