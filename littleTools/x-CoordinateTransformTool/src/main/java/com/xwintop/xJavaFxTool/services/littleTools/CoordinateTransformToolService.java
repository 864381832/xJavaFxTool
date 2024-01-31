package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.CoordinateTransformToolController;
import com.xwintop.xJavaFxTool.services.littleTools.geo.*;
import com.xwintop.xJavaFxTool.utils.ThreadPoolUtil;
import com.xwintop.xJavaFxTool.utils.func.Future;
import com.xwintop.xJavaFxTool.utils.func.HandleFunc;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.locationtech.jts.geom.Point;
import org.opengis.referencing.FactoryException;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: CoordinateTransformToolService
 * @Description: 坐标系转换工具
 * @author: xufeng
 * @date: 2021/1/18 13:37
 */

@Getter
@Setter
@Slf4j
public class CoordinateTransformToolService {
    private CoordinateTransformToolController coordinateTransformToolController;

    public CoordinateTransformToolService(CoordinateTransformToolController coordinateTransformToolController) {
        this.coordinateTransformToolController = coordinateTransformToolController;
    }

    private CoordinateTransform getTransform(String type, String crs) throws FactoryException{
        switch (type) {
            case "BD09":
                return new BD09CoordinateTransform(crs);
            case "GCJ02":
                return new GCJ02CoordinateTransform(crs);
            case "EPSG":
                return new EPSGCoordinateTransform(crs);
            case "WKT":
                return new WKTCoordinateTransform(crs);
            default:
                return new WGS84CoordinateTransform(crs);
        }
    }

    //转换坐标系
    public void transformAction(HandleFunc<Boolean> callback) {
        String sourceType = coordinateTransformToolController.getSourceCrsTypeChoiceBox().getSelectionModel().getSelectedItem();
        String targetType = coordinateTransformToolController.getTargetCrsTypeChoiceBox().getSelectionModel().getSelectedItem();
        String sourceCrsDesc = coordinateTransformToolController.getSourceCrsDesc().getText();
        String targetCrsDesc = coordinateTransformToolController.getTargetCrsDesc().getText();
        String sourceWkt = coordinateTransformToolController.getSourceWkt().getText();
        String targetWkt = coordinateTransformToolController.getTargetWkt().getText();
        String sourceText = coordinateTransformToolController.getSourceCoordinateTextArea().getText();

        double offsetX = parseTextToDouble(coordinateTransformToolController.getSourceOffsetX().getText());
        double offsetY = parseTextToDouble(coordinateTransformToolController.getSourceOffsetY().getText());

        double targetOffsetX = parseTextToDouble(coordinateTransformToolController.getTargetOffsetX().getText());
        double targetOffsetY = parseTextToDouble(coordinateTransformToolController.getTargetOffsetY().getText());

        ThreadPoolUtil.run((HandleFunc<Future<List<Point>>>) future -> {
            try {
                String sourceCrs = sourceCrsDesc;
                if (StringUtils.isNotBlank(sourceWkt)) {
                    sourceCrs = sourceWkt;
                }
                CoordinateTransform sourceTransform = getTransform(sourceType, sourceCrs);

                String targetCrs = targetCrsDesc;
                if (StringUtils.isNotBlank(targetWkt)) {
                    targetCrs = targetWkt;
                }
                CoordinateTransform targetTransform = getTransform(targetType, targetCrs);
                // 有些系统回车换行可能包含 '\r' ？ '' 这里兼容一下
                String[] sourcePointTextArr = sourceText
                        .replaceAll("\r", "\n")
                        .replaceAll("\n\n", "\n")
                        .split("\n");

                if (sourcePointTextArr.length == 0) {
                    future.setMessage("请输入源坐标");
                    future.setSuccess(false);
                    return;
                }

                List<Point> result = new ArrayList<>(sourcePointTextArr.length);
                for (String s : sourcePointTextArr) {
                    String pointText = s.trim();
                    double x;
                    double y;
                    if (pointText.contains(",")) {
                        String[] split = pointText.split(",");
                        x = Double.parseDouble(split[0].trim());
                        y = Double.parseDouble(split[1].trim());
                    } else if (pointText.contains("，")) {
                        String[] split = pointText.split("，");
                        x = Double.parseDouble(split[0].trim());
                        y = Double.parseDouble(split[1].trim());
                    } else if (pointText.contains("\t")) {
                        String[] split = pointText.split("\t");
                        x = Double.parseDouble(split[0].trim());
                        y = Double.parseDouble(split[1].trim());
                    } else {
                        String[] split = pointText.split(" ");
                        x = Double.parseDouble(split[0].trim());
                        y = Double.parseDouble(split[1].trim());
                    }
                    Point toWgs84 = sourceTransform.transformToWgs84(x, y, offsetX, offsetY);
                    Point point = targetTransform.transform(toWgs84, targetOffsetX, targetOffsetY);
                    result.add(point);
                }

                future.setResult(result);
            } catch (FactoryException e) {
                e.printStackTrace();
                future.setThrowable(e);
                future.setMessage("当前坐标系统转换不支持");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                future.setThrowable(e);
                future.setMessage("请检查源坐标输入是否错误");
            } catch (Exception e) {
                e.printStackTrace();
                future.setThrowable(e);
                future.setMessage("坐标系统转换出错");
            }
        }, asyncResult -> {
            if (asyncResult.isSuccess()) {
                coordinateTransformToolController.getTargetCoordinateTextArea().clear();
                List<Point> result = asyncResult.result();
                StringBuilder resultBuilder = new StringBuilder();
                for (Point point : result) {
                    resultBuilder.append(point.getX() + targetOffsetX).append(",").append(point.getY() + targetOffsetY).append('\n');
                }
                coordinateTransformToolController.getTargetCoordinateTextArea().setText(resultBuilder.toString());
            } else {
                coordinateTransformToolController.showTip(Alert.AlertType.WARNING, asyncResult.message());
            }
            callback.handle(asyncResult.isSuccess());
        });
    }

    private double parseTextToDouble(String text) {
        try {
            return Double.parseDouble(text);
        } catch (Exception ignored) {
        }
        return 0;
    }

}