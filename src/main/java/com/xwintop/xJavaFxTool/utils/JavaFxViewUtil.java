package com.xwintop.xJavaFxTool.utils;

import com.jfoenix.controls.JFXDecorator;
import javafx.scene.image.ImageView;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import lombok.extern.log4j.Log4j;

@Log4j
public class JavaFxViewUtil {

    /**
     * 获取JFoenix面板
     * @param stage
     * @param title 标题
     * @param iconUrl 图标Url
     * @param root 显示的面板
     */
    public static JFXDecorator getJFXDecorator(Stage stage,String title, String iconUrl, Parent root){
        JFXDecorator decorator = new JFXDecorator(stage, root);
        decorator.setCustomMaximize(true);
        decorator.setText(title);
        if(StringUtils.isNotEmpty(iconUrl)){
            ImageView imageView = new ImageView(new Image(iconUrl));
            imageView.setFitWidth(24);
            imageView.setFitHeight(24);
            decorator.setGraphic(imageView);
        }
        return decorator;
    }
    /**
     * 获取JFoenix窗口
     * @param stage
     * @param title 标题
     * @param iconUrl 图标Url
     * @param root 显示的面板
     */
    public static Scene getJFXDecoratorScene(Stage stage,String title, String iconUrl, Parent root){
        JFXDecorator decorator = getJFXDecorator(stage,title,iconUrl,root);
        return getJFXDecoratorScene(decorator);
    }

    /**
     * 获取JFoenix窗口
     * @param decorator 显示的decorator面板
     */
    public static Scene getJFXDecoratorScene(JFXDecorator decorator){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.width / 1.35;
        double height = screenSize.height / 1.2;
        Scene scene = new Scene(decorator, width, height);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(
//                JavaFxViewUtil.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
//                JavaFxViewUtil.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                JavaFxViewUtil.class.getResource("/css/jfoenix-main.css").toExternalForm());
        return scene;
    }

    /*
     * 获取新窗口
     */
    public static Stage getNewStage(String title, String iconUrl, Parent root) {
        Stage newStage = null;
        newStage = new Stage();
        newStage.setTitle(title);
        newStage.initModality(Modality.NONE);
        newStage.setResizable(true);//可调整大小

        if (StringUtils.isEmpty(iconUrl)) {
            iconUrl = "/images/icon.jpg";
        }
        Scene scene = JavaFxViewUtil.getJFXDecoratorScene(newStage,title,iconUrl,root);
        newStage.setScene(scene);
//        newStage.setScene(new Scene(root));
//		newStage.setMaximized(false);
        newStage.getIcons().add(new Image(iconUrl));
        newStage.show();
        return newStage;
    }

    /*
     * 获取新窗口，并添加关闭回调事件
     */
    public static Stage getNewStage(String title, String iconUrl, FXMLLoader fXMLLoader) {
        Stage newStage = null;
        try {
            newStage = getNewStage(title, iconUrl, fXMLLoader.<Parent>load());
            newStage.setOnCloseRequest((WindowEvent event) -> {
                setControllerOnCloseRequest(fXMLLoader.getController(), event);
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return newStage;
    }

    //设置窗口移除前回调
    public static void setControllerOnCloseRequest(Object controller, Event event) {
        try {
            MethodUtils.invokeMethod(controller, "onCloseRequest", event);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 设置Spinner最大最小值
     */
    public static void setSpinnerValueFactory(Spinner<Integer> spinner, int min, int max) {
        setSpinnerValueFactory(spinner, min, max, min, 1);
    }

    public static void setSpinnerValueFactory(Spinner<Integer> spinner, int min, int max, int initialValue) {
        setSpinnerValueFactory(spinner, min, max, initialValue, 1);
    }

    public static void setSpinnerValueFactory(Spinner<Integer> spinner, int min, int max, int initialValue,
                                              int amountToStepBy) {
        IntegerSpinnerValueFactory secondStart_0svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max,
                initialValue, amountToStepBy);
        spinner.setValueFactory(secondStart_0svf);
    }

    /**
     * @Title: setSliderLabelFormatter
     * @Description: 格式化Slider显示内容
     */
    public static void setSliderLabelFormatter(Slider slider, String formatter) {
        slider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                DecimalFormat decimalFormat = new DecimalFormat(formatter);
                return decimalFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                return Double.valueOf(string);
            }
        });
    }

    /**
     * @Title: setSpinnerValueFactory
     * @Description: 初始化表格属性
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setTableColumnMapValueFactory(TableColumn tableColumn, String name) {
        setTableColumnMapValueFactory(tableColumn,name,true);
        // tableColumn.setOnEditCommit((CellEditEvent<Map<String, String>,
        // String> t)-> {
        // t.getRowValue().put(name, t.getNewValue());
        // });
    }

    public static void setTableColumnMapValueFactory(TableColumn tableColumn, String name, boolean isEdit) {
        tableColumn.setCellValueFactory(new MapValueFactory(name));
        tableColumn.setCellFactory(TextFieldTableCell.<Map<String, String>>forTableColumn());
        if (isEdit) {
            tableColumn.setOnEditCommit(new EventHandler<CellEditEvent<Map<String, String>, String>>() {
                @Override
                public void handle(CellEditEvent<Map<String, String>, String> t) {
                    t.getRowValue().put(name, t.getNewValue());
                }
            });
        }
    }

    /**
     * @Title: setTableColumnMapValueFactoryAsChoiceBox
     * @Description: 初始化下拉选择表格属性
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setTableColumnMapAsChoiceBoxValueFactory(TableColumn tableColumn, String name, String[] choiceBoxStrings,
                                                                ObservableList<Map<String, String>> tableData) {
        tableColumn.setCellValueFactory(new MapValueFactory(name));
        tableColumn.setCellFactory(
                new Callback<TableColumn<Map<String, String>, String>, TableCell<Map<String, String>, String>>() {
                    @Override
                    public TableCell<Map<String, String>, String> call(TableColumn<Map<String, String>, String> param) {
                        TableCell<Map<String, String>, String> cell = new TableCell<Map<String, String>, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                this.setText(null);
                                this.setGraphic(null);
                                if (!empty) {
                                    ChoiceBox<String> choiceBox = new ChoiceBox<String>();
                                    choiceBox.getItems().addAll(choiceBoxStrings);
                                    choiceBox.setValue(tableData.get(this.getIndex()).get(name));
                                    choiceBox.valueProperty().addListener((obVal, oldVal, newVal) -> {
                                        tableData.get(this.getIndex()).put(name, newVal);
                                    });
                                    this.setGraphic(choiceBox);
                                }
                            }
                        };
                        return cell;
                    }
                });
    }

}
