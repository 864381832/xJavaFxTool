package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.FileSearchToolService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.littleTools.FileSearchToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
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
                                ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file);
                                Image fxImage = SwingFXUtils.toFXImage((BufferedImage) icon.getImage(), null);
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

        searchDirectoryTextField.setText("D:\\TestXf\\");
    }

    private void initEvent() {
        FileChooserUtil.setOnDrag(searchDirectoryTextField, FileChooserUtil.FileType.FOLDER);
    }

    private void initService() {
    }

    public void searchContentAction() throws Exception {
        fileSearchToolService.searchContentAction();
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
}