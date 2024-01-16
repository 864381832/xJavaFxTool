package com.xwintop.xcore.javafx.control;

public class IntegerSpinnerTestController {

    public IntegerSpinner sizeSpinner;

    public void initialize() {
        sizeSpinner.valueProperty().addListener((_ob, _old, _new) -> {
            System.out.println("new value: " + _new);
        });
    }
}
