package com.xwintop.xJavaFxTool.controller;

import com.xwintop.xJavaFxTool.utils.NodeUtils;
import com.xwintop.xJavaFxTool.view.NotepadView;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotepadController extends NotepadView {

    private int counter = 0;

    public void initialize() {
        tabPane.getSelectionModel().selectedIndexProperty().addListener(((observable, oldValue, newValue) -> {
            int index = newValue.intValue();
            if (index + 1 == tabPane.getTabs().size()) {
                tabPane.getTabs().add(index, createTab());
                tabPane.getSelectionModel().select(index);
            }
        }));

        Tab firstTab = tabPane.getTabs().get(0);
        setupTab(firstTab, firstTab.getContent());
    }

    private Tab createTab() {
        counter += 1;
        Tab tab = new Tab("未命名" + counter);

        try {
            Node content = FXMLLoader.load(NotepadController.class
                .getResource("/com/xwintop/xJavaFxTool/fxmlView/notepad-tab.fxml")
            );

            setupTab(tab, content);

            tab.setContent(content);
        } catch (IOException e) {
            log.error("读取 FXML 失败", e);
        }
        return tab;
    }

    private void setupTab(Tab tab, Node content) {

        TextArea textArea = (TextArea) content.lookup(".text-area");
        if (textArea != null) {
            NodeUtils.setUserData(textArea, "id", counter);
            textArea.textProperty().addListener((_ob, _old, _new) -> {
                if (_new.trim().length() == 0) {
                    tab.setText("未命名" + NodeUtils.getUserData(textArea, "id"));
                } else if (_new.contains("\n")) {
                    tab.setText(_new.substring(0, _new.indexOf("\n")));
                } else {
                    tab.setText(_new);
                }
            });
        }

        tab.setContextMenu(new ContextMenu(
            wrapTextMenuItem(textArea)
        ));
    }

    private CheckMenuItem wrapTextMenuItem(TextArea textArea) {
        CheckMenuItem menuItem = new CheckMenuItem("自动换行");
        if (textArea != null) {
            menuItem.setOnAction(event -> textArea.setWrapText(menuItem.isSelected()));
        }
        return menuItem;
    }
}
