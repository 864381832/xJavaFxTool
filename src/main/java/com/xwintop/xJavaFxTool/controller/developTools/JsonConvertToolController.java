package com.xwintop.xJavaFxTool.controller.developTools;

import com.xwintop.xJavaFxTool.services.developTools.JsonConvertToolService;
import com.xwintop.xJavaFxTool.view.developTools.JsonConvertToolView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * @ClassName: JsonConvertToolController
 * @Description: Json转换工具
 * @author: xufeng
 * @date: 2018/2/5 17:04
 */

@Getter
@Setter
@Slf4j
public class JsonConvertToolController extends JsonConvertToolView {
    private JsonConvertToolService jsonConvertToolService = new JsonConvertToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void jsonToXmlAction(ActionEvent event) throws Exception {
        jsonConvertToolService.jsonToXmlAction();
    }

    @FXML
    private void xmlToJsonAction(ActionEvent event) throws Exception {
        jsonConvertToolService.xmlToJsonAction();
    }

    @FXML
    private void jsonToJavaBeanAction(ActionEvent event) throws Exception {
        jsonConvertToolService.jsonToJavaBeanAction();
    }

    @FXML
    private void jsonToCAction(ActionEvent event) throws Exception {
        jsonConvertToolService.jsonToCAction();
    }

    @FXML
    private void excelToJsonAction(ActionEvent event) throws Exception {
        jsonConvertToolService.excelToJsonAction();
    }

    @FXML
    private void jsonToExcelAction(ActionEvent event) throws Exception {
        jsonConvertToolService.jsonToExcelAction();
    }

    @FXML
    private void jsonToYamlAction(ActionEvent event) throws Exception {
        jsonConvertToolService.jsonToYamlAction();
    }

    @FXML
    private void yamlToJsonAction(ActionEvent event) throws Exception {
        jsonConvertToolService.yamlToJsonAction();
    }
}