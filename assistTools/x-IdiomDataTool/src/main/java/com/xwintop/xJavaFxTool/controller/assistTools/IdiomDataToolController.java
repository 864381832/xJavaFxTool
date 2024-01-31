package com.xwintop.xJavaFxTool.controller.assistTools;

import cn.hutool.core.map.MapUtil;
import com.xwintop.xJavaFxTool.services.assistTools.IdiomDataToolService;
import com.xwintop.xJavaFxTool.view.assistTools.IdiomDataToolView;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @ClassName: IdiomDataToolController
 * @Description: 成语字典工具
 * @author: xufeng
 * @date: 2019/11/10 0010 22:06
 */

@Getter
@Setter
@Slf4j
public class IdiomDataToolController extends IdiomDataToolView {
    private IdiomDataToolService idiomDataToolService = new IdiomDataToolService(this);
    private ObservableList<Map<String, String>> idiomDataTableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        JavaFxViewUtil.setTableColumnMapValueFactory(wordTableColumn, "word");
        JavaFxViewUtil.setTableColumnMapValueFactory(pinyinTableColumn, "pinyin");
        JavaFxViewUtil.setTableColumnMapValueFactory(explanationTableColumn, "explanation");
        JavaFxViewUtil.setTableColumnMapValueFactory(derivationTableColumn, "derivation");
        JavaFxViewUtil.setTableColumnMapValueFactory(exampleTableColumn, "example");
        idiomDataTableView.setItems(idiomDataTableData);
    }

    private void initEvent() {
        idiomDataTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Map<String, String> map = idiomDataTableView.getSelectionModel().getSelectedItem();
                if (MapUtil.isNotEmpty(map)) {
                    String message = "发音:" + map.get("pinyin") + "\n\n释义：" + map.get("explanation") + "\n\n出处：" + map.get("derivation");
                    AlertUtil.showInfoAlert(map.get("word"), message);
                }
            }
        });
    }

    private void initService() {
        idiomDataToolService.initIdiomData();
    }

    @FXML
    private void selectAction() {
        try {
            idiomDataToolService.selectAction();
        } catch (Exception e) {
            log.error("查询错误：", e);
            TooltipUtil.showToast("查询发生错误：" + e.getMessage());
        }
    }

    @FXML
    private void clearAction(ActionEvent event) {
        idiomDataToolService.clearAction();
    }

    public void onCloseRequest(Event event) throws Exception {
        idiomDataToolService.destroy();
    }
}