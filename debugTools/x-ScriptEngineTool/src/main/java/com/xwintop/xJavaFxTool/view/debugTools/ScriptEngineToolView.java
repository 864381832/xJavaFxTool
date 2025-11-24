package com.xwintop.xJavaFxTool.view.debugTools;
import com.xwintop.xJavaFxTool.model.ScriptEngineToolTableBean;
import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
/**
 * @ClassName: ScriptEngineToolView
 * @Description: 脚本引擎调试工具类
 * @author: xufeng
 * @date: 2018/1/28 23:00
 */
@Getter
@Setter
public abstract class ScriptEngineToolView implements Initializable {
    @FXML
    protected CheckBox isEnabledCheckBox;
    @FXML
    protected TextArea scriptTextField;
    @FXML
    protected ChoiceBox<String> typeChoiceBox;
    @FXML
    protected TextField parameterTextField;
    @FXML
    protected Button addItemButton;
    @FXML
    protected ChoiceBox<String> quartzChoiceBox;
    @FXML
    protected TextField cronTextField;
    @FXML
    protected AnchorPane simpleScheduleAnchorPane;
    @FXML
    protected Spinner<Integer> intervalSpinner;
    @FXML
    protected Spinner<Integer> repeatCountSpinner;
    @FXML
    protected Button runQuartzButton;
    @FXML
    protected Button runAllButton;
    @FXML
    protected TableView<ScriptEngineToolTableBean> tableViewMain;
    @FXML
    protected TableColumn<ScriptEngineToolTableBean,String> orderTableColumn;
    @FXML
    protected TableColumn<ScriptEngineToolTableBean,Boolean> isEnabledTableColumn;
    @FXML
    protected TableColumn<ScriptEngineToolTableBean,String> scriptTableColumn;
    @FXML
    protected TableColumn<ScriptEngineToolTableBean,Boolean> viewScriptTableColumn;
    @FXML
    protected TableColumn<ScriptEngineToolTableBean,String> typeTableColumn;
    @FXML
    protected TableColumn<ScriptEngineToolTableBean,String> parameterTableColumn;
    @FXML
    protected TableColumn<ScriptEngineToolTableBean,Boolean> manualTableColumn;
    @FXML
    protected TableColumn<ScriptEngineToolTableBean, Boolean> isRunAfterActivateTableColumn;
    @FXML
    protected TableColumn<ScriptEngineToolTableBean,String> runAfterActivateTableColumn;
    @FXML
    protected TableColumn<ScriptEngineToolTableBean,String> remarksTableColumn;
    @FXML
    protected TextArea logTextArea;
}