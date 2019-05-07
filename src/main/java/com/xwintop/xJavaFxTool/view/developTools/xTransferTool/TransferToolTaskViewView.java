package com.xwintop.xJavaFxTool.view.developTools.xTransferTool;

import com.jfoenix.controls.JFXCheckBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public abstract class TransferToolTaskViewView implements Initializable {
    @FXML
    protected Button saveTaskConfigButton;
    @FXML
    protected TextField nameTextField;
    @FXML
    protected JFXCheckBox isEnableCheckBox;
    @FXML
    protected TextField taskTypeTextField;
    @FXML
    protected ChoiceBox<String> triggerTypeChoiceBox;
    @FXML
    protected Spinner<Integer> intervalTimeSpinner;
    @FXML
    protected Spinner<Integer> executeTimesSpinner;
    @FXML
    protected TextField triggerCronTextField;
    @FXML
    protected JFXCheckBox isStatefullJobCheckBox;
    @FXML
    protected ListView<String> receiverConfigListView;
    @FXML
    protected ListView<String> filterConfigsListView;
    @FXML
    protected ListView<String> senderConfigListView;
    @FXML
    protected TableView<Map<String, String>> propertiesTableView;
    @FXML
    protected TableColumn<Map<String, String>, String> propertiesKeyTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> propertiesValueTableColumn;
    @FXML
    protected TabPane serviceViewTabPane;
}