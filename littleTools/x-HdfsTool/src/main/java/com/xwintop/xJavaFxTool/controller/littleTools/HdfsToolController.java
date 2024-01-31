package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.HdfsToolService;
import com.xwintop.xJavaFxTool.view.littleTools.HdfsToolView;
import com.xwintop.xcore.javafx.helper.DropContentHelper;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
public class HdfsToolController extends HdfsToolView {
    private HdfsToolService hdfsToolService = new HdfsToolService(this);
    private ObservableList<Map<String, String>> searchResultTableData = FXCollections.observableArrayList();
    private ObservableList<Map<String, String>> hadoopConfTableData = FXCollections.observableArrayList();
    private ObservableList<Map<String, String>> systemConfTableData = FXCollections.observableArrayList();
    Node directorySvgGlyph = null;
    Node fileSvgGlyph = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        try {
            directorySvgGlyph = HdfsToolService.getFileIconImage(FileSystemView.getFileSystemView().getHomeDirectory());
            File file = File.createTempFile("icon", ".txt");
            fileSvgGlyph = HdfsToolService.getFileIconImage(file);
            file.deleteOnExit();
            directorySvgGlyph.setId("folder");
            fileSvgGlyph.setId("file");
        } catch (Exception e) {
            log.error("图标初始化失败！", e);
        }
//        hdfsUrlTextField.setText("192.168.130.142:9000");
        JavaFxViewUtil.setTableColumnMapValueFactory(hadoopConfKeyTableColumn, "hadoopConfKey", true);
        JavaFxViewUtil.setTableColumnMapValueFactory(hadoopConfValueTableColumn, "hadoopConfValue", true);
        hadoopConfTableView.setItems(hadoopConfTableData);
        JavaFxViewUtil.setTableColumnMapValueFactory(systemConfKeyTableColumn, "systemConfKey", true);
        JavaFxViewUtil.setTableColumnMapValueFactory(systemConfValueTableColumn, "systemConfValue", true);
        systemConfTableView.setItems(systemConfTableData);
        JavaFxViewUtil.setSpinnerValueFactory(fileSizeFromSpinner, 0, Integer.MAX_VALUE, 0);
        JavaFxViewUtil.setSpinnerValueFactory(fileSizeToSpinner, 0, Integer.MAX_VALUE, 0);
        try {
            hdfsListTreeView.setRoot(new TreeItem<>(HdfsToolService.getTreeItemMap("/"), (Node) BeanUtils.cloneBean(directorySvgGlyph)));
        } catch (Exception e) {
            log.error("设置图标失败", e);
        }
        hdfsListTreeView.getRoot().setExpanded(true);
        hdfsListTreeView.setShowRoot(true);
        JavaFxViewUtil.setTableColumnMapValueFactory(fileNameTableColumn, "fileName", false);
        fileNameTableColumn.setCellFactory(new Callback<TableColumn<Map<String, String>, String>, TableCell<Map<String, String>, String>>() {
            @Override
            public TableCell<Map<String, String>, String> call(TableColumn<Map<String, String>, String> param) {
                TableCell<Map<String, String>, String> cell = new TableCell<Map<String, String>, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        this.setText(item);
                        if (item != null) {
                            try {
                                if ("true".equals(searchResultTableData.get(this.getIndex()).get("isDirectory"))) {
                                    this.setGraphic((Node) BeanUtils.cloneBean(directorySvgGlyph));
                                } else {
                                    this.setGraphic((Node) BeanUtils.cloneBean(fileSvgGlyph));
                                }
                            } catch (Exception e) {
                                log.warn("设置图标失败：" + item, e);
                                this.setGraphic(null);
                            }
                        } else {
                            this.setGraphic(null);
                        }
                    }
                };
                return cell;
            }
        });
        JavaFxViewUtil.setTableColumnMapValueFactory(absolutePathTableColumn, "absolutePath", false);
        JavaFxViewUtil.setTableColumnMapValueFactory(fileSizeTableColumn, "fileSize", false);
        JavaFxViewUtil.setTableColumnMapValueFactory(lastModifiedTableColumn, "lastModified", false);
        searchResultTableView.setItems(searchResultTableData);
    }

    private void initEvent() {
        JavaFxViewUtil.addTableViewOnMouseRightClickMenu(hadoopConfTableView);
        JavaFxViewUtil.addTableViewOnMouseRightClickMenu(systemConfTableView);
        hdfsListTreeView.setOnMouseClicked(event -> {
            TreeItem<Map<String, Object>> selectedItem = hdfsListTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                return;
            }
            if (event.getButton() == MouseButton.PRIMARY) {
                try {
                    hdfsToolService.nodeSelectionChanged(selectedItem);
                } catch (Exception e) {
                    log.error("加载节点报错！", e);
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_UnfoldAll = new MenuItem("展开所有");
                menu_UnfoldAll.setOnAction(event1 -> {
                    hdfsListTreeView.getRoot().setExpanded(true);
                    hdfsListTreeView.getRoot().getChildren().forEach(stringTreeItem -> {
                        stringTreeItem.setExpanded(true);
                    });
                });
                MenuItem menu_FoldAll = new MenuItem("折叠所有");
                menu_FoldAll.setOnAction(event1 -> {
                    hdfsListTreeView.getRoot().setExpanded(false);
                    hdfsListTreeView.getRoot().getChildren().forEach(stringTreeItem -> {
                        stringTreeItem.setExpanded(false);
                    });
                });
                ContextMenu contextMenu = new ContextMenu(menu_UnfoldAll, menu_FoldAll);
                JavaFxViewUtil.addMenuItem(contextMenu, "新建文件夹", event1 -> {
                    hdfsToolService.addDirOnAction(selectedItem);
                });
                JavaFxViewUtil.addMenuItem(contextMenu, "添加文件", event1 -> {
                    hdfsToolService.addFileOnAction(selectedItem);
                });
                JavaFxViewUtil.addMenuItem(contextMenu, "重命名", event1 -> {
                    hdfsToolService.renameNodeOnAction(selectedItem);
                });
                JavaFxViewUtil.addMenuItem(contextMenu, "下载", event1 -> {
                    hdfsToolService.downloadFileOnAction(selectedItem);
                });
                JavaFxViewUtil.addMenuItem(contextMenu, "删除", event1 -> {
                    hdfsToolService.deleteNodeOnAction(selectedItem);
                });
                hdfsListTreeView.setContextMenu(contextMenu);
            }
        });
        JavaFxViewUtil.addTableViewOnMouseRightClickMenu(searchResultTableView);
        DropContentHelper.accept(hdfsListTreeView,
                dragboard -> dragboard.hasFiles(),
                (__, dragboard) -> {
                    TreeItem<Map<String, Object>> selectedItem = hdfsListTreeView.getSelectionModel().getSelectedItem();
                    hdfsToolService.addFileOnAction(dragboard.getFiles(), selectedItem);
                }
        );
        DropContentHelper.accept(searchResultTableView,
                dragboard -> dragboard.hasFiles(),
                (__, dragboard) -> {
                    hdfsToolService.addFileOnAction(dragboard.getFiles());
                }
        );
        searchResultTableView.setOnDragDetected(event -> {
            hdfsToolService.dragDownloadFile();
            event.consume();
        });
    }

    private void initService() {
    }

    @FXML
    private void connectAction(ActionEvent event) {
        try {
            hdfsToolService.searchContentAction();
        } catch (Exception e) {
            log.error("搜索出问题：", e);
        }
    }

    @FXML
    private void searchContentAction(ActionEvent event) {

    }

    @FXML
    private void searchDirectoryAction(ActionEvent event) {
    }

    /**
     * 父控件被移除前调用
     */
    public void onCloseRequest(Event event) throws Exception {
        hdfsToolService.closeHdfs();
    }


}