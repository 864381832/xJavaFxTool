package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.CoordinateTransformToolService;
import com.xwintop.xJavaFxTool.view.littleTools.CoordinateTransformToolView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: CoordinateTransformToolController
 * @Description: 坐标系转换工具
 * @author: xufeng
 * @date: 2021/1/18 13:37
 */

@Getter
@Setter
@Slf4j
public class CoordinateTransformToolController extends CoordinateTransformToolView {
    private CoordinateTransformToolService coordinateTransformToolService = new CoordinateTransformToolService(this);
    private String[] coordinateTypeStrings = new String[]{"WGS84", "GCJ02", "BD09", "EPSG", "WKT"};

    /**
     * 转换开始标志位
     */
    private boolean transformDoing = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        sourceCoordinateTextArea.setText("113.413296,23.056206\n113.395919,23.060414\n113.385537,23.041085");

        sourceCrsTypeChoiceBox.getItems().addAll(coordinateTypeStrings);
        sourceCrsTypeChoiceBox.getSelectionModel().select(0);
        sourceCrsDesc.setDisable(true);
        sourceWkt.setDisable(true);
        sourceOffsetX.setText("0");
        sourceOffsetY.setText("0");

        targetCrsTypeChoiceBox.getItems().addAll(coordinateTypeStrings);
        targetCrsTypeChoiceBox.getSelectionModel().select(3);
        targetCrsDesc.setText("4326");
        targetWkt.setDisable(true);
        targetOffsetX.setText("0");
        targetOffsetY.setText("0");

    }

    private void initEvent() {
        sourceCrsTypeChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleCrsTypeChange(true, newValue));

        targetCrsTypeChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleCrsTypeChange(false, newValue));

    }

    private void handleCrsTypeChange(boolean isSource, String newValue) {
        TextField crsDescField;
        TextField offsetXField;
        TextField offsetYField;
        TextArea wktTextArea;
        if (isSource) {
            crsDescField = sourceCrsDesc;
            offsetXField = sourceOffsetX;
            offsetYField = sourceOffsetY;
            wktTextArea = sourceWkt;
        } else {
            crsDescField = targetCrsDesc;
            offsetXField = targetOffsetX;
            offsetYField = targetOffsetY;
            wktTextArea = targetWkt;
        }

        offsetXField.setText("0");
        offsetYField.setText("0");
        wktTextArea.clear();

        switch (newValue) {
            case "WGS84":
            case "GCJ02":
            case "BD09":
                crsDescField.clear();
                crsDescField.setDisable(true);
                wktTextArea.setDisable(true);
                break;
            case "EPSG":
                crsDescField.setText("4326");
                crsDescField.setDisable(false);
                wktTextArea.setDisable(true);
                break;
            case "WKT":
                crsDescField.setText("");
                crsDescField.setDisable(true);
                wktTextArea.setDisable(false);
                // 以下相当于EPSG:4326 WGS84
                wktTextArea.setText("GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\"," +
                        "SPHEROID[\"WGS_1984\",6378137,298.257223563]],PRIMEM[\"Greenwich\",0],UNIT[\"Degree\",0.017453292519943295]]");
                break;
        }
    }

    private void initService() {
        // 刚开始初始化坐标转换时需要1s作用，所以这里先后台初始化
//        ThreadPoolUtil.run(() -> {
//            try {
//                new EPSGCoordinateTransform("4326");
//            } catch (FactoryException e) {
//                e.printStackTrace();
//            }
//        });
    }

    @FXML
    private void transformAction() {
        if (this.transformDoing) {
            showTip(Alert.AlertType.INFORMATION, "坐标正在转换中，请稍后再试...");
            return;
        }

        if (checkParams()) {
            this.transformDoing = true;
            this.transformButton.setText("转换中...");
            // 转换完成后回调
            coordinateTransformToolService.transformAction(res -> {
                this.transformDoing = false;
                this.transformButton.setText("开始转换");
            });
        }
    }

    /**
     * 检查参数是否正常
     */
    private boolean checkParams() {
        return true;
    }

    public void showTip(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.titleProperty().set("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}