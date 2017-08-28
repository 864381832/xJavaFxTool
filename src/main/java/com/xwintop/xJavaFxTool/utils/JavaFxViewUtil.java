package com.xwintop.xJavaFxTool.utils;

import java.text.DecimalFormat;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

public class JavaFxViewUtil {
	//
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
//		tableColumn.setOnEditCommit((CellEditEvent<Map<String, String>, String> t)-> {
//			t.getRowValue().put(name, t.getNewValue());
//		});
	}

}
