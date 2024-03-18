package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.model.FileCopyTableBean;
import com.xwintop.xJavaFxTool.services.littleTools.FileCopyService;
import com.xwintop.xJavaFxTool.utils.ActionScheduleUtil;
import com.xwintop.xJavaFxTool.view.littleTools.FileCopyView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.JavaFxSystemUtil;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: FileCopyController
 * @Description: 文件复制工具
 * @author: xufeng
 * @date: 2018/1/21 0021 1:08
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
public class FileCopyController extends FileCopyView {
    private FileCopyService fileCopyService = new FileCopyService(this);
    private ObservableList<FileCopyTableBean> tableData = FXCollections.observableArrayList();
    private ActionScheduleUtil actionScheduleUtil;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
    }

    private void initView() {
        fileCopyService.setTableData(tableData);
        fileCopyService.loadingConfigure();
        JavaFxViewUtil.setSpinnerValueFactory(spinnerCopyNumber, 1, Integer.MAX_VALUE);
        tableColumnCopyFileOriginalPath.setCellValueFactory(new PropertyValueFactory<>("copyFileOriginalPath"));
        tableColumnCopyFileOriginalPath.setCellFactory(TextFieldTableCell.forTableColumn());
        tableColumnCopyFileOriginalPath.setOnEditCommit((CellEditEvent<FileCopyTableBean, String> t) -> {
            t.getRowValue().setCopyFileOriginalPath(t.getNewValue());
            // ((FileCopyTableBean) t.getTableView().getItems().get(t.getTablePosition().getRow())).setCopyFileOriginalPath(t.getNewValue());
        });

        JavaFxViewUtil.setTableColumnButonFactory(tableColumnViewCopyFileOriginalPath, "查看", (me, index) -> {
            JavaFxSystemUtil.openDirectory(tableData.get(index).getCopyFileOriginalPath());
        });

        tableColumnCopyFileTargetPath.setCellValueFactory(new PropertyValueFactory<>("copyFileTargetPath"));
        tableColumnCopyFileTargetPath.setCellFactory(TextFieldTableCell.forTableColumn());
        tableColumnCopyFileTargetPath.setOnEditCommit((CellEditEvent<FileCopyTableBean, String> t) -> {
            t.getRowValue().setCopyFileTargetPath(t.getNewValue());
        });

        JavaFxViewUtil.setTableColumnButonFactory(tableColumnViewCopyFileTargetPath, "查看", (me, index) -> {
            JavaFxSystemUtil.openDirectory(tableData.get(index).getCopyFileTargetPath());
        });

        tableColumnCopyNumber.setCellValueFactory(new PropertyValueFactory<>("copyNumber"));
        tableColumnCopyNumber.setCellFactory(TextFieldTableCell.<FileCopyTableBean>forTableColumn());
        tableColumnCopyNumber.setOnEditCommit((CellEditEvent<FileCopyTableBean, String> t) -> {
            t.getRowValue().setCopyNumber(t.getNewValue());
        });

        tableColumnIsCopy.setCellValueFactory(new PropertyValueFactory<>("isCopy"));
        tableColumnIsCopy.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumnIsCopy));
        tableColumnIsRename.setCellValueFactory(new PropertyValueFactory<>("isRename"));
        tableColumnIsRename.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumnIsRename));
        tableColumnIsDelete.setCellValueFactory(new PropertyValueFactory<FileCopyTableBean, Boolean>("isDelete"));
        tableColumnIsDelete.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumnIsDelete));
        tableColumnIsDeleteSourceFile.setCellValueFactory(new PropertyValueFactory<>("isDeleteSourceFile"));
        tableColumnIsDeleteSourceFile.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumnIsDeleteSourceFile));
        JavaFxViewUtil.setTableColumnButonFactory(tableColumnRun, "运行", (me, index) -> {
            try {
                fileCopyService.copyAction(tableData.get(index));
            } catch (Exception e) {
                log.error("拷贝文件失败：" + e.getMessage());
                TooltipUtil.showToast("拷贝文件失败：" + e.getMessage());
            }
        });
        tableColumnRemarks.setCellValueFactory(new PropertyValueFactory<FileCopyTableBean, String>("remarks"));
        tableColumnRemarks.setCellFactory(TextFieldTableCell.<FileCopyTableBean>forTableColumn());
        tableColumnRemarks.setOnEditCommit((CellEditEvent<FileCopyTableBean, String> t) -> {
            t.getRowValue().setRemarks(t.getNewValue());
        });
        tableViewMain.setItems(tableData);

        actionScheduleUtil = new ActionScheduleUtil();
        actionScheduleUtil.setScheduleNode(actionScheduleHBox);
        actionScheduleUtil.setJobAction(() -> {
            try {
                fileCopyService.copyAction();
            } catch (Exception e) {
                log.error("拷贝失败:", e);
            }
        });
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(textFieldCopyFileOriginalPath, FileChooserUtil.FileType.FILE);
        FileChooserUtil.setOnDrag(textFieldCopyFileTargetPath, FileChooserUtil.FileType.FOLDER);
        tableData.addListener((Change<? extends FileCopyTableBean> tableBean) -> {
            try {
                saveConfigure(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        tableViewMain.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    FileCopyTableBean tableBean = tableViewMain.getSelectionModel().getSelectedItem();
                    FileCopyTableBean tableBean2 = new FileCopyTableBean(tableBean.getPropertys());
                    tableData.add(tableViewMain.getSelectionModel().getSelectedIndex(), tableBean2);
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    deleteSelectRowAction(null);
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    tableData.clear();
                });
                tableViewMain.setContextMenu(new ContextMenu(menu_Copy, menu_Remove, menu_RemoveAll));
            }
        });
    }

    @FXML
    private void chooseOriginalPathAction(ActionEvent event) {
        File file = FileChooserUtil.chooseFile();
        if (file != null) {
            textFieldCopyFileOriginalPath.setText(file.getPath());
        }
    }

    @FXML
    private void chooseTargetPathAction(ActionEvent event) {
        File file = FileChooserUtil.chooseDirectory();
        if (file != null) {
            textFieldCopyFileTargetPath.setText(file.getPath());
        }
    }

    @FXML
    private void addItemAction(ActionEvent event) {
        tableData.add(new FileCopyTableBean(textFieldCopyFileOriginalPath.getText(),
                textFieldCopyFileTargetPath.getText(), spinnerCopyNumber.getValue().toString(),
                checkBoxIsCopy.isSelected(), checkBoxIsRename.isSelected(), checkBoxIsDelete.isSelected(), true, ""));
    }

    @FXML
    private void deleteSelectRowAction(ActionEvent event) {
        tableData.remove(tableViewMain.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void saveConfigure(ActionEvent event) throws Exception {
        fileCopyService.saveConfigure();
    }

    @FXML
    private void copyAction(ActionEvent event) throws Exception {
        fileCopyService.copyAction();
    }

    @FXML
    private void otherSaveConfigureAction(ActionEvent event) throws Exception {
        fileCopyService.otherSaveConfigureAction();
    }

    @FXML
    private void loadingConfigureAction(ActionEvent event) {
        fileCopyService.loadingConfigureAction();
    }

    /**
     * 父控件被移除前调用
     */
    public void onCloseRequest(Event event) throws Exception {
        fileCopyService.stopQuartzAction();
    }

}
