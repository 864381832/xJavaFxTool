package com.xwintop.xJavaFxTool.controller.codeTools;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import com.xwintop.xJavaFxTool.services.codeTools.IdCardGenerator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class IdCardGeneratorController {
    @FXML
    private Button generator;
    @FXML
    private Button name_copy;
    @FXML
    private Button id_copy;
    @FXML
    private Button birthday_copy;
    @FXML
    private TextField name_text;
    @FXML
    private TextField id_text;
    @FXML
    private TextField birthday_text;
    @FXML
    private Label name_tip;
    @FXML
    private Label id_tip;
    @FXML
    private Label birthday_tip;


    @FXML
    protected void buttonClick() {
        clearData();
        IdCardGenerator generator = new IdCardGenerator();
        String id = generator.generate();
        name_text.appendText("张三");
        id_text.appendText(id);
        birthday_text.appendText(IdCardGenerator.getBirthday(id));
        name_tip.setVisible(false);
        id_tip.setVisible(false);
        birthday_tip.setVisible(false);
    }

    @FXML
    protected void copyName() {
        StringSelection stsel = new StringSelection(name_text.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
        name_tip.setVisible(true);
        id_tip.setVisible(false);
        birthday_tip.setVisible(false);
    }

    @FXML
    protected void copyId() {
        StringSelection stsel = new StringSelection(id_text.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
        name_tip.setVisible(false);
        id_tip.setVisible(true);
        birthday_tip.setVisible(false);
    }

    @FXML
    protected void copyBirthday() {
        StringSelection stsel = new StringSelection(birthday_text.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
        name_tip.setVisible(false);
        id_tip.setVisible(false);
        birthday_tip.setVisible(true);
    }

    private void clearData() {
        name_text.clear();
        id_text.clear();
        birthday_text.clear();
    }
}
