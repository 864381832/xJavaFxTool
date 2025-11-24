package com.xwintop.xJavaFxTool.view.littleTools;
import lombok.Getter;
import lombok.Setter;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import java.lang.String;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
@Getter
@Setter
public abstract class HbaseToolView implements Initializable {
@FXML
protected TextField hbaseUrlTextField;
@FXML
protected TextField userNameTextField;
@FXML
protected Button connectButton;
@FXML
protected TextField kerberosTextField;
@FXML
protected TableView hadoopConfTableView;
@FXML
protected TableColumn hadoopConfKeyTableColumn;
@FXML
protected TableColumn hadoopConfValueTableColumn;
@FXML
protected TableView systemConfTableView;
@FXML
protected TableColumn systemConfKeyTableColumn;
@FXML
protected TableColumn systemConfValueTableColumn;
@FXML
protected ChoiceBox fileTypeChoiceBox;
@FXML
protected Spinner fileSizeFromSpinner;
@FXML
protected ChoiceBox fileSizeFromChoiceBox;
@FXML
protected Spinner fileSizeToSpinner;
@FXML
protected ChoiceBox fileSizeToChoiceBox;
@FXML
protected TextField searchDirectoryTextField;
@FXML
protected Button searchDirectoryButton;
@FXML
protected TreeView hbaseListTreeView;
@FXML
protected TableView searchResultTableView;
@FXML
protected TableColumn fileNameTableColumn;
@FXML
protected TableColumn absolutePathTableColumn;
@FXML
protected TableColumn fileSizeTableColumn;
@FXML
protected TableColumn lastModifiedTableColumn;
@FXML
protected TextArea fileContentTextArea;

}