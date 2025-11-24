package com.xwintop.xcore.javafx.dialog;

import javafx.scene.control.TextField;

public class FxDialogTestController {

    public TextField nameField;

    public void initialize() {
        // 如何从父窗体获得要初始化的内容
    }

    public void initName(String name) {
        this.nameField.setText(name);
    }

    public String getName() {
        return this.nameField.getText();
    }
}
