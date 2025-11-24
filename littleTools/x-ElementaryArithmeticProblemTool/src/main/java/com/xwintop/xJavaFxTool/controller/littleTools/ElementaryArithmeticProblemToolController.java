package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.view.littleTools.ElementaryArithmeticProblemToolView;
import com.xwintop.xJavaFxTool.services.littleTools.ElementaryArithmeticProblemToolService;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * @ClassName: ElementaryArithmeticProblemToolController
 * @Description: 小学生算数题生成工具
 * @author: xufeng
 * @date: 2021/1/16 23:04
 */

@Getter
@Setter
@Slf4j
public class ElementaryArithmeticProblemToolController extends ElementaryArithmeticProblemToolView {
    private ElementaryArithmeticProblemToolService elementaryArithmeticProblemToolService = new ElementaryArithmeticProblemToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        JavaFxViewUtil.setSpinnerValueFactory(maxNumberSpinner, 1, Integer.MAX_VALUE, 1000);
        JavaFxViewUtil.setSpinnerValueFactory(buildNumberSpinner1, 1, Integer.MAX_VALUE, 100);
        JavaFxViewUtil.setSpinnerValueFactory(buildNumberSpinner2, 2, Integer.MAX_VALUE, 5);
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void startBuildAction(ActionEvent event) throws Exception{
        elementaryArithmeticProblemToolService.startBuildAction();
    }
}