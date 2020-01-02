package com.xwintop.xJavaFxTool.utils;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.lang.Singleton;
import com.jfoenix.controls.JFXDecorator;
import com.xwintop.xJavaFxTool.controller.IndexController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.awt.*;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class JavaFxViewUtil {

    /**
     * 获取JFoenix面板
     *
     * @param stage
     * @param title   标题
     * @param iconUrl 图标Url
     * @param root    显示的面板
     */
    public static JFXDecorator getJFXDecorator(Stage stage, String title, String iconUrl, Parent root) {
        JFXDecorator decorator = new JFXDecorator(stage, root);
        decorator.setCustomMaximize(true);
        decorator.setText(title);
        if (StringUtils.isNotEmpty(iconUrl)) {
            ImageView imageView = new ImageView(new Image(iconUrl));
            imageView.setFitWidth(24);
            imageView.setFitHeight(24);
            decorator.setGraphic(imageView);
        }
        return decorator;
    }

    /**
     * 获取JFoenix窗口
     *
     * @param stage
     * @param title   标题
     * @param iconUrl 图标Url
     * @param root    显示的面板
     */
    public static Scene getJFXDecoratorScene(Stage stage, String title, String iconUrl, Parent root) {
        JFXDecorator decorator = getJFXDecorator(stage, title, iconUrl, root);
        return getJFXDecoratorScene(decorator);
    }

    /**
     * 获取JFoenix窗口
     *
     * @param decorator 显示的decorator面板
     */
    public static Scene getJFXDecoratorScene(JFXDecorator decorator) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.width / 1.35;
        double height = screenSize.height / 1.2;
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        if (width > bounds.getWidth() || height > bounds.getHeight()) {//解决屏幕缩放问题
            width = bounds.getWidth();
            height = bounds.getHeight();
        }
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
        Stage newStage = new Stage();
        newStage.setTitle(title);
        newStage.initModality(Modality.NONE);
        newStage.setResizable(true);//可调整大小

        if (StringUtils.isEmpty(iconUrl)) {
            iconUrl = "/images/icon.jpg";
        }
        Scene scene = JavaFxViewUtil.getJFXDecoratorScene(newStage, title, iconUrl, root);
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
            log.error("加载新窗口失败", e);
        }
        return newStage;
    }

    //打开一个等待窗口
    public static void openNewWindow(String title, Parent root) {
        Stage window = new Stage();
        window.setTitle(title);
        window.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.showAndWait();
    }

    public static void openUrlOnWebView(String url, String title, String iconUrl) {
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        if (url.startsWith("http")) {
            webEngine.load(url);
        } else {
            webEngine.load(IndexController.class.getResource(url).toExternalForm());
        }
        JavaFxViewUtil.getNewStage(title, iconUrl, new BorderPane(browser));
    }

    //设置窗口移除前回调
    public static void setControllerOnCloseRequest(Object controller, Event event) {
        try {
            Method method = MethodUtils.getAccessibleMethod(controller.getClass(), "onCloseRequest", Event.class);
            if (method != null) {
                MethodUtils.invokeMethod(controller, "onCloseRequest", event);
            }
        } catch (Exception e) {
            log.error("执行onCloseRequest方法失败", e);
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

    public static void setSpinnerValueFactory(Spinner<Double> spinner, double min, double max) {
        setSpinnerValueFactory(spinner, min, max, min, 1d);
    }

    public static void setSpinnerValueFactory(Spinner<Double> spinner, double min, double max, double initialValue) {
        setSpinnerValueFactory(spinner, min, max, initialValue, 1d);
    }

    public static void setSpinnerValueFactory(Spinner spinner, Number min, Number max, Number initialValue, Number amountToStepBy) {
        if (min instanceof Integer) {
            IntegerSpinnerValueFactory secondStart_0svf = new IntegerSpinnerValueFactory((int) min, (int) max, (int) initialValue, (int) amountToStepBy);
            spinner.setValueFactory(secondStart_0svf);
            spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    spinner.getValueFactory().setValue(Integer.parseInt(newValue));
                } catch (Exception e) {
                    log.warn("数字int转换异常 newValue:" + newValue);
                    spinner.getEditor().setText(oldValue);
                }
            });
        } else if (min instanceof Double) {
            DoubleSpinnerValueFactory secondStart_0svf = new DoubleSpinnerValueFactory((double) min, (double) max, (double) initialValue, (double) amountToStepBy);
            spinner.setValueFactory(secondStart_0svf);
            spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    spinner.getValueFactory().setValue(Double.parseDouble(newValue));
                } catch (Exception e) {
                    log.warn("数字double转换异常 newValue:" + newValue);
                    spinner.getEditor().setText(oldValue);
                }
            });
        }
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
     * @Title: addTableViewOnMouseRightClickMenu
     * @Description: 添加TableView右键菜单
     */
    public static void addTableViewOnMouseRightClickMenu(TableView<Map<String, String>> tableView) {
        tableView.setEditable(true);
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menuAdd = new MenuItem("添加行");
                menuAdd.setOnAction(event1 -> {
                    tableView.getItems().add(new HashMap<String, String>());
                });
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    Map<String, String> map = tableView.getSelectionModel().getSelectedItem();
                    Map<String, String> map2 = new HashMap<String, String>(map);
                    tableView.getItems().add(tableView.getSelectionModel().getSelectedIndex(), map2);
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    tableView.getItems().remove(tableView.getSelectionModel().getSelectedIndex());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    tableView.getItems().clear();
                });
                tableView.setContextMenu(new ContextMenu(menuAdd, menu_Copy, menu_Remove, menu_RemoveAll));
            }
        });
    }

    /**
     * @Title: addListViewOnMouseRightClickMenu
     * @Description: 添加ListView右键菜单
     */
    public static void addListViewOnMouseRightClickMenu(ListView<String> listView) {
        listView.setEditable(true);
        listView.setCellFactory(TextFieldListCell.forListView());
        listView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menuAdd = new MenuItem("添加行");
                menuAdd.setOnAction(event1 -> {
                    listView.getItems().add("");
                });
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    listView.getItems().add(listView.getSelectionModel().getSelectedIndex(), listView.getSelectionModel().getSelectedItem());
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    listView.getItems().remove(listView.getSelectionModel().getSelectedIndex());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    listView.getItems().clear();
                });
                listView.setContextMenu(new ContextMenu(menuAdd, menu_Copy, menu_Remove, menu_RemoveAll));
            }
        });
    }

    /**
     * @Title: setSpinnerValueFactory
     * @Description: 初始化表格属性
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setTableColumnMapValueFactory(TableColumn tableColumn, String name) {
        setTableColumnMapValueFactory(tableColumn, name, true, null);
    }

    public static void setTableColumnMapValueFactory(TableColumn tableColumn, String name, boolean isEdit) {
        setTableColumnMapValueFactory(tableColumn, name, isEdit, null);
    }

    public static void setTableColumnMapValueFactory(TableColumn tableColumn, String name, boolean isEdit, Runnable onEditCommitHandle) {
        tableColumn.setCellValueFactory(new MapValueFactory(name));
        tableColumn.setCellFactory(TextFieldTableCell.<Map<String, String>>forTableColumn());
        if (isEdit) {
            tableColumn.setOnEditCommit(new EventHandler<CellEditEvent<Map<String, String>, String>>() {
                @Override
                public void handle(CellEditEvent<Map<String, String>, String> t) {
                    t.getRowValue().put(name, t.getNewValue());
                    if (onEditCommitHandle != null) {
                        onEditCommitHandle.run();
                    }
                }
            });
        }
    }

    public static void setTableColumnButonFactory(TableColumn tableColumn, String name, EventHandler<? super MouseEvent> value) {
        setTableColumnButonFactory(tableColumn, name, (mouseEvent, index) -> {
            value.handle(mouseEvent);
        });
    }

    public static void setTableColumnButonFactory(TableColumn tableColumn, String name, MouseEventCallFunc mouseEventCallFunc) {
        tableColumn.setCellFactory((col) -> {
            TableCell<Object, Boolean> cell = new TableCell<Object, Boolean>() {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);
                    if (!empty) {
                        Button delBtn = new Button(name);
                        this.setContentDisplay(ContentDisplay.CENTER);
                        this.setGraphic(delBtn);
                        delBtn.setOnMouseClicked((me) -> {
                            mouseEventCallFunc.callFun(me, this.getIndex());
                        });
                    }
                }
            };
            return cell;
        });
    }

    public static interface MouseEventCallFunc {
        void callFun(MouseEvent mouseEvent, Integer index);
    }

    /**
     * @Title: setTableColumnMapValueFactoryAsChoiceBox
     * @Description: 初始化下拉选择表格属性
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setTableColumnMapAsChoiceBoxValueFactory(TableColumn tableColumn, String name, String[] choiceBoxStrings) {
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
                                    ObservableList<Map<String, String>> tableData = tableColumn.getTableView().getItems();
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

    /**
     * 初始化选择框表格属性
     */
    public static void setTableColumnMapAsCheckBoxValueFactory(TableColumn tableColumn, String name) {
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
                                    ObservableList<Map<String, String>> tableData = tableColumn.getTableView().getItems();
                                    CheckBox checkBox = new CheckBox();
                                    checkBox.setSelected(Boolean.valueOf(tableData.get(this.getIndex()).get(name)));
                                    checkBox.selectedProperty().addListener((obVal, oldVal, newVal) -> {
                                        tableData.get(this.getIndex()).put(name, newVal.toString());
                                    });
                                    this.setGraphic(checkBox);
                                }
                            }
                        };
                        return cell;
                    }
                });
    }

    /**
     * 设置改变事件监听操作
     */
    public static void setPropertyAddChangeListener(TextInputControl inputControl, Consumer<ActionEvent> consumer) {
        inputControl.textProperty().addListener((observable, oldValue, newValue) -> {
            if (Singleton.get(TimedCache.class, (long) 2000).get("initiativeChange") != null) {
                return;
            }
            Singleton.get(TimedCache.class, (long) 2000).put("initiativeChange", true);
            consumer.accept(null);
            Singleton.get(TimedCache.class, (long) 2000).remove("initiativeChange");
        });
    }

    /**
     * 设置改变事件监听操作
     */
    public static void setPropertyAddChangeListener(TextInputControl inputControl, Runnable runnable) {
        inputControl.textProperty().addListener((observable, oldValue, newValue) -> {
            setPropertyChangeRun(runnable);
        });
    }

    /**
     * 设置改变事件监听防重复操作
     */
    public static void setPropertyChangeRun(Runnable runnable) {
        if (Singleton.get(TimedCache.class, (long) 2000).get("initiativeChange") != null) {
            return;
        }
        Singleton.get(TimedCache.class, (long) 2000).put("initiativeChange", true);
        runnable.run();
        Singleton.get(TimedCache.class, (long) 2000).remove("initiativeChange");
    }

    //设置密码框可预览密码
    public static void setPasswordTextFieldFactory(PasswordField passwordTextField) {
        TextField password2TextField = new TextField(passwordTextField.getText());
        password2TextField.setVisible(false);
        RadioButton passwordRadioButton = new RadioButton();
        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            password2TextField.setText(newValue);
        });
        password2TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            passwordTextField.setText(newValue);
        });
        passwordRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            passwordTextField.setVisible(!newValue);
            password2TextField.setVisible(newValue);
        });
        StackPane stackPane = (StackPane) passwordTextField.getParent();
        stackPane.getChildren().add(password2TextField);
        stackPane.getChildren().add(passwordRadioButton);
        stackPane.setAlignment(passwordRadioButton, Pos.CENTER_RIGHT);
    }
}
