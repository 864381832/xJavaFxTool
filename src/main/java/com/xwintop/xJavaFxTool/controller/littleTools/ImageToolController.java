package com.xwintop.xJavaFxTool.controller.littleTools;
import com.xwintop.xJavaFxTool.services.littleTools.ImageToolService;
import com.xwintop.xJavaFxTool.view.littleTools.ImageToolView;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
@Getter
@Setter
@Log4j
public class ImageToolController extends ImageToolView {
private ImageToolService imageToolService = new ImageToolService(this);

@Override
public void initialize(URL location, ResourceBundle resources) {
initView();
initEvent();
initService();
}
 private void initView() {}
 private void initEvent() {}
 private void initService() {}
@FXML
private void addImageAction(ActionEvent event){
}

@FXML
private void openFolderAction(ActionEvent event){
}

@FXML
private void imageCompressionAction(ActionEvent event){
}

@FXML
private void outputFolderAction(ActionEvent event){
}
}