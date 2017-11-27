package com.xwintop.xJavaFxTool.controller.debugTools.redisTool;

import com.xwintop.xJavaFxTool.services.debugTools.redisTool.RedisToolDataTableService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisToolDialogController implements Initializable
{
    @FXML
    private TextField keyTextField;
    @FXML
    private TextField overdueTimeTextField;
    @FXML
    private TableView<Map<String,String>> dialogTableView;
    @FXML
    private TableColumn<Map<String,String>,String> dialogKeyTableColumn;
    @FXML
    private TableColumn<Map<String,String>,String> dialogValueTableColumn;
    @FXML
    private Button enterButton;
    @FXML
    private Button cancelButton;

    private ObservableList<Map<String, String>> dialogTableData = FXCollections.observableArrayList();
    private RedisToolDataTableService redisToolDataTableService;
    private String title;

    private Stage dialogStage;


    public static FXMLLoader getFXMLLoader()
    {
        URL url = Object.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/debugTools/redisTool/RedisToolDialog.fxml");
        FXMLLoader fXMLLoader = new FXMLLoader(url);
        return fXMLLoader;
    }

    public static RedisToolDialogController getRedisToolDialogController(String title, boolean type)
    {
        FXMLLoader fXMLLoader = RedisToolDialogController.getFXMLLoader();
        Parent loginNode;
        Stage dialogStage = null;
        try
        {
            loginNode = fXMLLoader.load();
            dialogStage = new Stage();
            dialogStage.setTitle("添加"+title+"数据");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
//            dialogStage.initOwner(getPrimaryStage());
            dialogStage.setScene(new Scene(loginNode));
            dialogStage.setMaximized(false);
            dialogStage.setResizable(false);
            dialogStage.show();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        RedisToolDialogController redisToolDialogController = fXMLLoader.getController();
        redisToolDialogController.setDialogType(type);
        redisToolDialogController.setDialogStage(dialogStage);
        redisToolDialogController.setTitle(title);
        return redisToolDialogController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        initView();
        initEvent();
        initService();
    }

    private void initView()
    {
        JavaFxViewUtil.setTableColumnMapValueFactory(dialogKeyTableColumn, "key");
        JavaFxViewUtil.setTableColumnMapValueFactory(dialogValueTableColumn, "value");
        dialogTableView.setItems(dialogTableData);
        for (int i=0;i<10;i++){
            dialogTableData.add(new HashMap<String, String>());
        }
    }

    private void initEvent()
    {
        dialogTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menuAdd = new MenuItem("添加行");
                menuAdd.setOnAction(event1 -> {
                    dialogTableData.add(new HashMap<String,String>());
                });
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    Map<String,String> map = dialogTableView.getSelectionModel().getSelectedItem();
                    Map<String,String> map2 =  new HashMap<String,String>(map);
                    dialogTableData.add(dialogTableView.getSelectionModel().getSelectedIndex(), map2);
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    dialogTableData.remove(dialogTableView.getSelectionModel().getSelectedItem());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    dialogTableData.clear();
                });
                dialogTableView.setContextMenu(new ContextMenu(menuAdd,menu_Copy, menu_Remove, menu_RemoveAll));
            }
        });
    }

    private void initService()
    {
    }

    public void setDialogType(boolean type)
    {
        if (type)
        {
            dialogTableView.getColumns().remove(dialogKeyTableColumn);
        }
    }

    @FXML
    private void enterAction(ActionEvent event)
    {
        redisToolDataTableService.addTableDataByDialog();
        dialogStage.close();
    }

    @FXML
    private void cancelAction(ActionEvent event)
    {
        Platform.runLater(() -> {
            dialogStage.close();
        });
    }
}