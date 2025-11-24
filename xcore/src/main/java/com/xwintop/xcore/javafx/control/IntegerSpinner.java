package com.xwintop.xcore.javafx.control;

import javafx.beans.NamedArg;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

/**
 * 只接受整数的 Spinner，增强方面：可以直接赋值，可以获取最小值最大值
 */
public class IntegerSpinner extends Spinner<Integer> {

    public IntegerSpinner(
        @NamedArg(value = "min", defaultValue = "0") int min,
        @NamedArg(value = "max", defaultValue = "100") int max,
        @NamedArg(value = "initialValue", defaultValue = "0") int initialValue,
        @NamedArg(value = "amountToStepBy", defaultValue = "1") int amountToStepBy
    ) {
        super(min, max, initialValue, amountToStepBy);
    }

    /**
     * 赋值，当 value 超出最大最小值范围时抛出异常
     */
    public void setValue(int value) {
        IntegerSpinnerValueFactory valueFactory = getSpinnerValueFactory();
        if (value < valueFactory.getMin() || value > valueFactory.getMax()) {
            throw new IllegalArgumentException(
                "Value out of bounds(" + valueFactory.getMin() + "~" + valueFactory.getMax() + ")."
            );
        }
        valueFactory.setValue(value);
    }

    public IntegerSpinnerValueFactory getSpinnerValueFactory() {
        return (IntegerSpinnerValueFactory) getValueFactory();
    }

    public int getMin() {
        return getSpinnerValueFactory().getMin();
    }

    public int getMax() {
        return getSpinnerValueFactory().getMax();
    }

    public void setMax(int max) {
        getSpinnerValueFactory().setMax(max);
    }

    public void setMin(int min) {
        getSpinnerValueFactory().setMin(min);
    }
}
