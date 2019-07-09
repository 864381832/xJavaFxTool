package com.xwintop.xJavaFxTool.controller.developTools.xTransferTool;

import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.services.developTools.xTransferTool.TransferToolUrlDocumentDialogService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.developTools.xTransferTool.TransferToolUrlDocumentDialogView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @ClassName: UrlDocumentDialogController
 * @Description: 历史连接编辑
 * @author: xufeng
 * @date: 2019/5/29 16:09
 */

@Getter
@Setter
@Slf4j
public class TransferToolUrlDocumentDialogController extends TransferToolUrlDocumentDialogView {
    private TransferToolUrlDocumentDialogService transferToolUrlDocumentDialogService = new TransferToolUrlDocumentDialogService(this);
    private ObservableList<Map<String, String>> tableData = FXCollections.observableArrayList();

    public static FXMLLoader getFXMLLoader() {
        FXMLLoader fXMLLoader = new FXMLLoader(IndexController.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/developTools/xTransferTool/UrlDocumentDialog.fxml"));
        return fXMLLoader;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        try {
            transferToolUrlDocumentDialogService.loadingConfigure();
        } catch (Exception e) {
            log.error("读取配置失败", e);
        }
        JavaFxViewUtil.setTableColumnMapValueFactory(nameTableColumn, "name");
        JavaFxViewUtil.setTableColumnMapValueFactory(hostTableColumn, "host");
        JavaFxViewUtil.setTableColumnMapValueFactory(portTableColumn, "port");
        JavaFxViewUtil.setTableColumnMapValueFactory(userNameTableColumn, "userName");
        JavaFxViewUtil.setTableColumnMapValueFactory(passwordTableColumn, "password");
        JavaFxViewUtil.setTableColumnMapValueFactory(pathTableColumn, "path");
        JavaFxViewUtil.setTableColumnMapValueFactory(explainTableColumn, "explain");
        urlDocumentTableView.setItems(tableData);
    }

    private void initEvent() {
        JavaFxViewUtil.addTableViewOnMouseRightClickMenu(urlDocumentTableView);
        urlDocumentTableView.setEditable(true);
        urlDocumentTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menuAdd = new MenuItem("添加行");
                menuAdd.setOnAction(event1 -> {
                    urlDocumentTableView.getItems().add(new HashMap<String, String>());
                });
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    Map<String, String> map = urlDocumentTableView.getSelectionModel().getSelectedItem();
                    Map<String, String> map2 = new HashMap<String, String>(map);
                    urlDocumentTableView.getItems().add(urlDocumentTableView.getSelectionModel().getSelectedIndex(), map2);
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    urlDocumentTableView.getItems().remove(urlDocumentTableView.getSelectionModel().getSelectedIndex());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    urlDocumentTableView.getItems().clear();
                });
                MenuItem menuSave = new MenuItem("保存配置");
                menuSave.setOnAction(event1 -> {
                    try {
                        transferToolUrlDocumentDialogService.saveConfigure();
                    } catch (Exception e) {
                        log.error("保存配置失败", e);
                    }
                });
                urlDocumentTableView.setContextMenu(new ContextMenu(menuAdd, menu_Copy, menu_Remove, menu_RemoveAll, menuSave));
            }
        });
    }

    private void initService() {
    }
}
