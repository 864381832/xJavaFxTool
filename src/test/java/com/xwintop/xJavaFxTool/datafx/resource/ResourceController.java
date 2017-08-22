package com.xwintop.xJavaFxTool.datafx.resource;

import java.net.URL;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

@FXMLController("/fxml/simpleView.fxml")
public class ResourceController implements Initializable{
	private ResourceBundle bundle;
    @FXML
    private Label resultLabel;
 
    @FXML
    @ActionTrigger("myAction")
    private Button actionButton;
 
    private int clickCount = 0;
 
    @PostConstruct
    public void init() {
        resultLabel.setText("Button was clicked " + clickCount + " times");
    }
 
    @ActionMethod("myAction")
    public void onAction() {
        clickCount++;
        resultLabel.setText("Button was clicked " + clickCount + " times");
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		System.out.println(bundle.getString("Title"));
	}
}
