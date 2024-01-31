package com.xwintop.xJavaFxTool.controller.littleTools;

import cn.hutool.core.swing.clipboard.ClipboardUtil;
import com.xwintop.xJavaFxTool.services.littleTools.FileSearchToolService;
import com.xwintop.xJavaFxTool.utils.ImgToolUtil;
import com.xwintop.xJavaFxTool.view.littleTools.FileSearchToolView;
import com.xwintop.xcore.util.javafx.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @ClassName: FileSearchToolController
 * @Description: 文件搜索工具
 * @author: xufeng
 * @date: 2019/7/18 10:21
 */

@Getter
@Setter
@Slf4j
public class FileSearchToolController extends FileSearchToolView {
    private FileSearchToolService fileSearchToolService = new FileSearchToolService(this);
    private ObservableList<Map<String, String>> searchResultTableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        JavaFxViewUtil.setSpinnerValueFactory(fileSizeFromSpinner, 0, Integer.MAX_VALUE, 0);
        JavaFxViewUtil.setSpinnerValueFactory(fileSizeToSpinner, 0, Integer.MAX_VALUE, 0);
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
                                String absolutePath = searchResultTableData.get(this.getIndex()).get("absolutePath");
                                File file = new File(absolutePath);
                                if (!file.exists()) {
                                    this.setText(null);
                                    this.setGraphic(null);
                                    Platform.runLater(() -> {
                                        fileSearchToolService.deleteDocument(absolutePath);
                                        searchResultTableData.remove(this.getIndex());
                                    });
                                    return;
                                }
//                                ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file);
//                                Image fxImage = SwingFXUtils.toFXImage((BufferedImage) icon.getImage(), null);
                                Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
                                BufferedImage bufferedImage = new BufferedImage(
                                        icon.getIconWidth(),
                                        icon.getIconHeight(),
                                        BufferedImage.TYPE_INT_ARGB
                                );
                                icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
                                Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                                if (file.isHidden()) {
                                    this.setTextFill(Color.GREY);
                                    fxImage = ImgToolUtil.pixWithImage(8, fxImage);
                                } else {
                                    this.setTextFill(Color.BLACK);
                                }
                                ImageView imageView = new ImageView(fxImage);
                                this.setGraphic(imageView);
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
        searchResultTableVIew.setItems(searchResultTableData);

//        searchDirectoryTextField.setText("D:\\TestXf\\");
        searchDirectoryTextField.setText(StringUtils.removeEnd(new File("./").getAbsolutePath(), "."));
    }

    private void initEvent() {
        fileSizeFromSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            searchContentAction();
        });
        fileSizeToSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            searchContentAction();
        });
        FileChooserUtil.setOnDrag(searchDirectoryTextField, FileChooserUtil.FileType.FOLDER);

        searchResultTableVIew.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String absolutePath = searchResultTableVIew.getSelectionModel().getSelectedItem().get("absolutePath");
                JavaFxSystemUtil.openDirectory(absolutePath);
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menuOpen = new MenuItem("打开");
                menuOpen.setOnAction(event1 -> {
                    String absolutePath = searchResultTableVIew.getSelectionModel().getSelectedItem().get("absolutePath");
                    JavaFxSystemUtil.openDirectory(absolutePath);
                });
                MenuItem menuOpenPath = new MenuItem("打开路径");
                menuOpenPath.setOnAction(event1 -> {
                    String absolutePath = searchResultTableVIew.getSelectionModel().getSelectedItem().get("absolutePath");
                    File file = new File(absolutePath);
                    if (!file.isDirectory()) {
                        absolutePath = file.getParent();
                    }
                    JavaFxSystemUtil.openDirectory(absolutePath);
                });
                MenuItem menuCopyFileName = new MenuItem("复制文件名");
                menuCopyFileName.setOnAction(event1 -> {
                    ClipboardUtil.setStr(searchResultTableVIew.getSelectionModel().getSelectedItem().get("fileName"));
                });
                MenuItem menuCopyFilePath = new MenuItem("复制完整路径");
                menuCopyFilePath.setOnAction(event1 -> {
                    ClipboardUtil.setStr(searchResultTableVIew.getSelectionModel().getSelectedItem().get("absolutePath"));
                });
                MenuItem menuDeleteFile = new MenuItem("删除文件");
                menuDeleteFile.setOnAction(event1 -> {
                    if (AlertUtil.showConfirmAlert("确定要删除吗？")) {
                        String absolutePath = searchResultTableVIew.getSelectionModel().getSelectedItem().get("absolutePath");
                        try {
                            FileUtils.forceDelete(new File(absolutePath));
                            fileSearchToolService.deleteDocument(absolutePath);
                            searchResultTableData.remove(searchResultTableVIew.getSelectionModel().getSelectedIndex());
                        } catch (IOException e) {
                            log.error("删除失败！" + absolutePath, e);
                        }
                        TooltipUtil.showToast("删除文件成功：" + absolutePath);
                    }
                });
                searchResultTableVIew.setContextMenu(new ContextMenu(menuOpen, menuOpenPath, menuCopyFileName, menuCopyFilePath, menuDeleteFile));
            }
        });
    }

    private void initService() {
        if (autoRefreshIndexCheckBox.isSelected()) {
            fileSearchToolService.autoRefreshIndexAction();
        }
    }

    public void autoRefreshIndexAction() throws Exception {
        if (autoRefreshIndexCheckBox.isSelected()) {
            fileSearchToolService.autoRefreshIndexAction();
        } else {
            fileSearchToolService.stopAutoRefreshIndexTimer();
        }
    }

    public void searchContentAction() {
        try {
            fileSearchToolService.searchContentAction();
        } catch (Exception e) {
            log.error("搜索出问题：", e);
        }
    }

    @FXML
    private void refreshIndexAction(ActionEvent event) throws Exception {
        fileSearchToolService.refreshIndexAction();
    }

    @FXML
    private void searchDirectoryAction(ActionEvent event) throws Exception {
        File file = FileChooserUtil.chooseDirectory();
        if (file != null) {
            searchDirectoryTextField.setText(file.getPath());
        }
    }

    /**
     * 父控件被移除前调用
     */
    public void onCloseRequest(Event event) {
        fileSearchToolService.stopAutoRefreshIndexTimer();
    }
}