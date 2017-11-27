package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.ImageToolService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.littleTools.ImageToolView;
import com.xwintop.xcore.util.FileUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import org.apache.commons.io.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * @ClassName: ImageToolController
 * @Description: 图片工具控制类
 * @author: xufeng
 * @date: 2017/11/27 10:05
 */

@Getter
@Setter
@Log4j
public class ImageToolController extends ImageToolView {
    private ImageToolService imageToolService = new ImageToolService(this);
    private ObservableList<Map<String, String>> tableData = FXCollections.observableArrayList();//表格数据
    private String[] formatChoiceBoxStrings = new String[]{"原格式", "jpg", "png"};//输出格式类型
    private String[] sizeTypeChoiceBoxStrings = new String[]{"固定值px", "百分比%"};//图片压缩大小类型

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        JavaFxViewUtil.setTableColumnMapValueFactory(nameTableColumn, "name");
        JavaFxViewUtil.setTableColumnMapValueFactory(sizeTableColumn, "size");
        JavaFxViewUtil.setTableColumnMapValueFactory(resolutionTableColumn, "resolution");
        JavaFxViewUtil.setTableColumnMapValueFactory(fullPathTableColumn, "fullPath");
        tableViewMain.setItems(tableData);

        formatChoiceBox.getItems().addAll(formatChoiceBoxStrings);
        formatChoiceBox.getSelectionModel().selectFirst();
        sizeTypeChoiceBox.getItems().addAll(sizeTypeChoiceBoxStrings);
        sizeTypeChoiceBox.getSelectionModel().selectFirst();
        JavaFxViewUtil.setSpinnerValueFactory(scaleWidthSpinner, 0, Integer.MAX_VALUE);
        JavaFxViewUtil.setSpinnerValueFactory(scaleHeightSpinner, 0, Integer.MAX_VALUE);
    }

    private void initEvent() {
        tableViewMain.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_Remove = new MenuItem("移除选中行");
                menu_Remove.setOnAction(event1 -> {
                    tableData.remove(tableViewMain.getSelectionModel().getSelectedItem());
                });
                MenuItem menu_RemoveAll = new MenuItem("移除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    tableData.clear();
                });
                tableViewMain.setContextMenu(new ContextMenu(menu_Remove, menu_RemoveAll));
            }
            if (event.getButton() == MouseButton.PRIMARY) {
                String imagePath = tableData.get(tableViewMain.getSelectionModel().getSelectedIndex()).get("fullPath");
                showImageView.setImage(new Image("file:" +imagePath));
                showImageView.setFitWidth(showImageView.getImage().getWidth());
                showImageView.setFitHeight(showImageView.getImage().getHeight());

                BufferedImage bufferedImage = imageToolService.getThumbnailsBuilder(imagePath);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                showCompressionImageView.setImage(image);
                showCompressionImageView.setFitWidth(image.getWidth());
                showCompressionImageView.setFitHeight(image.getHeight());
            }
        });
        FileChooserUtil.setOnDrag(outputFolderTextField, FileChooserUtil.FileType.FOLDER);
    }

    private void initService() {
    }

    @FXML
    private void addImageAction(ActionEvent event) {
        File file = FileChooserUtil.chooseFile();
        if (file != null) {
            addTableData(file);
        }
    }

    @FXML
    private void openFolderAction(ActionEvent event) {
        File folder = FileChooserUtil.chooseDirectory();
        if (folder != null) {
            FileUtils.listFiles(folder, null, false).forEach((File file) -> {
                addTableData(file);
            });
        }
    }

    private void addTableData(File file){
        Image image = new Image("file:"+file.getAbsolutePath());
        String resolution = ""+image.getWidth()+"×"+image.getHeight();
        Map<String, String> rowValue = new HashMap<>();
        rowValue.put("name", file.getName());
        rowValue.put("size", "" + FileUtil.FormetFileSize(file.length()));
        rowValue.put("resolution", resolution);
        rowValue.put("fullPath", file.getAbsolutePath());
        tableData.add(rowValue);
    }

    @FXML
    private void imageCompressionAction(ActionEvent event) {
        try {
            imageToolService.imageCompressionAction();
            TooltipUtil.showToast("图片压缩完成：");
        } catch (IOException e) {
            TooltipUtil.showToast("图片压缩失败："+e.getMessage());
        }
    }

    @FXML
    private void outputFolderAction(ActionEvent event) {
        File folder = FileChooserUtil.chooseDirectory();
        if (folder != null) {
            outputFolderTextField.setText(folder.getPath());
        }
    }
}