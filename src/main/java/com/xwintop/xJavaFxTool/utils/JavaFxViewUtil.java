package com.xwintop.xJavaFxTool.utils;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

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
}
