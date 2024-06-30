package com.xwintop.xJavaFxTool.view.debugTools;
import com.xwintop.xJavaFxTool.model.ExpressionParserToolTableBean;
import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * @ClassName: ExpressionParserToolView
 * @Description: 表达式解析器调试工具
 * @author: xufeng
 * @date: 2021/9/12 22:56
 */

@Getter
@Setter
public abstract class ExpressionParserToolView implements Initializable {
    @FXML
    protected CheckBox isEnabledCheckBox;
    @FXML
    protected TextArea scriptTextField;
    @FXML
    protected ChoiceBox<String> typeChoiceBox;
    @FXML
    protected TextArea parameterTextField;
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
    protected TableView<ExpressionParserToolTableBean> tableViewMain;
    @FXML
    protected TableColumn<ExpressionParserToolTableBean,String> orderTableColumn;
    @FXML
    protected TableColumn<ExpressionParserToolTableBean,Boolean> isEnabledTableColumn;
    @FXML
    protected TableColumn<ExpressionParserToolTableBean,String> scriptTableColumn;
    @FXML
    protected TableColumn<ExpressionParserToolTableBean,Boolean> viewScriptTableColumn;
    @FXML
    protected TableColumn<ExpressionParserToolTableBean,String> typeTableColumn;
    @FXML
    protected TableColumn<ExpressionParserToolTableBean,String> parameterTableColumn;
    @FXML
    protected TableColumn<ExpressionParserToolTableBean,Boolean> manualTableColumn;
    @FXML
    protected TableColumn<ExpressionParserToolTableBean, Boolean> isRunAfterActivateTableColumn;
    @FXML
    protected TableColumn<ExpressionParserToolTableBean,String> runAfterActivateTableColumn;
    @FXML
    protected TableColumn<ExpressionParserToolTableBean,String> remarksTableColumn;
    @FXML
    protected TextArea logTextArea;
}