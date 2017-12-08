package com.xwintop.xJavaFxTool.utils;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.text.DecimalFormat;
import java.util.Map;

@Log4j
public class JavaFxViewUtil {

	/*
	 * 获取新窗口
	 */
	public static Stage getNewStage(String title,String iconUrl, Parent root) {
		Stage newStage = null;
		newStage = new Stage();
		newStage.setTitle(title);
		newStage.initModality(Modality.NONE);
		newStage.setResizable(true);//可调整大小
		newStage.setScene(new Scene(root));
//		newStage.setMaximized(false);
		if(StringUtils.isEmpty(iconUrl)){
			newStage.getIcons().add(new Image("/images/icon.jpg"));
		}else{
			newStage.getIcons().add(new Image(iconUrl));
		}
		newStage.show();
		newStage.setOnCloseRequest((WindowEvent event)->{
			System.out.println("删除前");
			try {
				MethodUtils.invokeMethod(root,"onCloseRequest",event);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		});
		return newStage;
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setTableColumnMapValueFactory(TableColumn tableColumn, String name) {
		tableColumn.setCellValueFactory(new MapValueFactory(name));
		tableColumn.setCellFactory(TextFieldTableCell.<Map<String, String>>forTableColumn());
		tableColumn.setOnEditCommit(new EventHandler<CellEditEvent<Map<String, String>, String>>() {
			@Override
			public void handle(CellEditEvent<Map<String, String>, String> t) {
				t.getRowValue().put(name, t.getNewValue());
			}
		});
		// tableColumn.setOnEditCommit((CellEditEvent<Map<String, String>,
		// String> t)-> {
		// t.getRowValue().put(name, t.getNewValue());
		// });
	}

	/**
	 * @Title: setTableColumnMapValueFactoryAsChoiceBox
	 * @Description: 初始化下拉选择表格属性
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
