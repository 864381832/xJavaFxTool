package com.xwintop.xJavaFxTool.utils;

import java.text.DecimalFormat;

import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
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
}
