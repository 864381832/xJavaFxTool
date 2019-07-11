package com.xwintop.xJavaFxTool.controller.developTools;

import com.xwintop.xJavaFxTool.services.developTools.JsonConvertToolService;
import com.xwintop.xJavaFxTool.view.developTools.JsonConvertToolView;
import com.xwintop.xcore.util.javafx.TooltipUtil;
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
        try {
            jsonConvertToolService.jsonToXmlAction();
        } catch (Exception e) {
            log.error("转换错误：", e);
            TooltipUtil.showToast("转换错误" + e.getMessage());
        }
    }

    @FXML
    private void xmlToJsonAction(ActionEvent event) throws Exception {
        try {
            jsonConvertToolService.xmlToJsonAction();
        } catch (Exception e) {
            log.error("转换错误：", e);
            TooltipUtil.showToast("转换错误" + e.getMessage());
        }
    }

    @FXML
    private void jsonToJavaBeanAction(ActionEvent event) throws Exception {
        try {
            jsonConvertToolService.jsonToJavaBeanAction();
        } catch (Exception e) {
            log.error("转换错误：", e);
            TooltipUtil.showToast("转换错误" + e.getMessage());
        }
    }

    @FXML
    private void jsonToCAction(ActionEvent event) throws Exception {
        try {
            jsonConvertToolService.jsonToCAction();
        } catch (Exception e) {
            log.error("转换错误：", e);
            TooltipUtil.showToast("转换错误" + e.getMessage());
        }
    }

    @FXML
    private void excelToJsonAction(ActionEvent event) throws Exception {
        try {
            jsonConvertToolService.excelToJsonAction();
        } catch (Exception e) {
            log.error("转换错误：", e);
            TooltipUtil.showToast("转换错误" + e.getMessage());
        }
    }

    @FXML
    private void jsonToExcelAction(ActionEvent event) throws Exception {
        try {
            jsonConvertToolService.jsonToExcelAction();
        } catch (Exception e) {
            log.error("转换错误：", e);
            TooltipUtil.showToast("转换错误" + e.getMessage());
        }
    }

    @FXML
    private void jsonToYamlAction(ActionEvent event) throws Exception {
        try {
            jsonConvertToolService.jsonToYamlAction();
        } catch (Exception e) {
            log.error("转换错误：", e);
            TooltipUtil.showToast("转换错误" + e.getMessage());
        }
    }

    @FXML
    private void yamlToJsonAction(ActionEvent event) throws Exception {
        try {
            jsonConvertToolService.yamlToJsonAction();
        } catch (Exception e) {
            log.error("转换错误：", e);
            TooltipUtil.showToast("转换错误" + e.getMessage());
        }
    }

    @FXML
    private void propsToYamlAction(ActionEvent event) throws Exception {
        try {
            jsonConvertToolService.propsToYamlAction();
        } catch (Exception e) {
            log.error("转换错误：", e);
            TooltipUtil.showToast("转换错误" + e.getMessage());
        }
    }

    @FXML
    private void yamlToPropsAction(ActionEvent event) throws Exception {
        try {
            jsonConvertToolService.yamlToPropsAction();
        } catch (Exception e) {
            log.error("转换错误：", e);
            TooltipUtil.showToast("转换错误" + e.getMessage());
        }
    }
}