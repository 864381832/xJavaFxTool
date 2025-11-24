package com.xwintop.xJavaFxTool.view.debugTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public abstract class KeyToolView implements Initializable {
    @FXML
    protected TextField info1TextField;
    @FXML
    protected TextField info2TextField;
    @FXML
    protected TextField info3TextField;
    @FXML
    protected TextField info4TextField;
    @FXML
    protected TextField info5TextField;
    @FXML
    protected TextField info6TextField;
    @FXML
    protected TextField aliasTextField;
    @FXML
    protected TextField keystoreTextField;
    @FXML
    protected PasswordField storepassTextField;
    @FXML
    protected PasswordField keypassTextField;
    @FXML
    protected TextField publicAliasTextField;
    @FXML
    protected Spinner<Integer> keysizeSpinner;
    @FXML
    protected Spinner<Integer> validitySpinner;
    @FXML
    protected TextField publicFileTextField;
    @FXML
    protected TextField publicCertsTextField;
    @FXML
    protected TextField subjectTextField;
    @FXML
    protected TextField licenseInfoTextField;
    @FXML
    protected DatePicker notBeforeDatePicker;
    @FXML
    protected DatePicker issuedTimeDatePicker;
    @FXML
    protected DatePicker notAfterDatePicker;
    @FXML
    protected TextField licPathTextField;
    @FXML
    protected ChoiceBox<String> consumerTypeChoiceBox;
    @FXML
    protected Spinner<Integer> consumerAmountSpinner;
    @FXML
    protected TableView<Map<String, String>> propertiesTableView;
    @FXML
    protected TableColumn<Map<String, String>, String> propertiesKeyTableColumn;
    @FXML
    protected TableColumn<Map<String, String>, String> propertiesValueTableColumn;
}