package com.xwintop.xJavaFxTool.services.littleTools;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.xwintop.xJavaFxTool.controller.littleTools.FileBuildToolController;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.text.StringSubstitutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: FileBuildToolService
 * @Description: 文件生成工具
 * @author: xufeng
 * @date: 2020/4/18 0018 18:05
 */

@Getter
@Setter
@Slf4j
public class FileBuildToolService {
    private FileBuildToolController fileBuildToolController;

    public FileBuildToolService(FileBuildToolController fileBuildToolController) {
        this.fileBuildToolController = fileBuildToolController;
    }

    public void buildFileAction(boolean isMoreFile) {
        StringBuffer stringBuffer = new StringBuffer();
        String fileTemplateText = fileBuildToolController.getFileTemplateTextArea().getText();
        String fileName = fileBuildToolController.getFileNameTextField().getText();
        int index = -1;
        for (Map<String, String> fieldTableDatum : fileBuildToolController.getFieldTableData()) {
            StringSubstitutor contentStringSubstitutor = new StringSubstitutor(fieldTableDatum, "${", "}");
            contentStringSubstitutor.setEnableSubstitutionInVariables(true);
            String fileName1 = contentStringSubstitutor.replace(fileName);
            String content = contentStringSubstitutor.replace(fileTemplateText);
            if (isMoreFile) {
                if (StringUtils.isEmpty(fileName) || !fileName.contains("${")) {
                    if (index++ != -1) {
                        fileName1 = fileName + "_" + (index);
                    }
                }
                showFileContent(fileName1, content);
            } else {
                stringBuffer.append(content).append(StringUtils.defaultIfEmpty(fileBuildToolController.getOneFileSpaceTextField().getText(), "").replace("\\n", "\n").replace("\\r", "\r"));
            }
        }
        if (!isMoreFile) {
            showFileContent(fileName, stringBuffer.toString());
        }
    }

    public void showFileContent(String fileName, String content) {
        if (StringUtils.isEmpty(fileName)) {
            fileName = IdUtil.fastSimpleUUID();
        }
        if (fileBuildToolController.getIsShowCheckBox().isSelected()) {
            TextArea textArea = new TextArea(content);
            textArea.setWrapText(true);
            JavaFxViewUtil.openNewWindow(fileName, textArea);
        } else {
            try {
                String outputPath = StringUtils.defaultIfBlank(fileBuildToolController.getOutputFilePathTextField().getText(), "./executor");
                File jsonFile = new File(outputPath, fileName);
                FileUtils.writeStringToFile(jsonFile, content, "utf-8");
                TooltipUtil.showToast("生成成功:" + jsonFile.getCanonicalPath());
            } catch (IOException e) {
                log.error("写入文件失败：", e);
                TooltipUtil.showToast("写入文件失败：" + e.getMessage());
            }
        }
    }

    public void saveTemplateAndConfig() throws Exception {
        File saveFile = FileChooserUtil.chooseSaveFile("fileBuildToolData" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".json");
        if (saveFile != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fileName", fileBuildToolController.getFileNameTextField().getText());
            jsonObject.put("oneFileSpace", fileBuildToolController.getOneFileSpaceTextField().getText());
            jsonObject.put("outputFilePath", fileBuildToolController.getOutputFilePathTextField().getText());
            jsonObject.put("fileTemplate", fileBuildToolController.getFileTemplateTextArea().getText());
            jsonObject.put("fieldTableData", fileBuildToolController.getFieldTableData());
            List<String> columns = new ArrayList<>();
            for (TableColumn<Map<String, String>, ?> column : fileBuildToolController.getFieldTableView().getColumns()) {
                columns.add(column.getText());
            }
            jsonObject.put("columns", columns);
            FileUtils.writeStringToFile(saveFile, JSON.toJSONString(jsonObject), "utf-8");
            TooltipUtil.showToast("保存配置成功：" + saveFile.getCanonicalPath());
        }
    }

    public void addTemplateAndConfig() throws Exception {
        File saveFile = FileChooserUtil.chooseFile();
        if (saveFile != null) {
            JSONObject jsonObject = JSON.parseObject(FileUtils.readFileToString(saveFile, "utf-8"));
            fileBuildToolController.getFileNameTextField().setText(jsonObject.getString("fileName"));
            fileBuildToolController.getOneFileSpaceTextField().setText(jsonObject.getString("oneFileSpace"));
            fileBuildToolController.getOutputFilePathTextField().setText(jsonObject.getString("outputFilePath"));
            fileBuildToolController.getFileTemplateTextArea().setText(jsonObject.getString("fileTemplate"));
            fileBuildToolController.getFieldTableData().clear();
            fileBuildToolController.getFieldTableView().getColumns().clear();
            for (Object columns : jsonObject.getJSONArray("columns")) {
                String tableColumnName = columns.toString();
                TableColumn<Map<String, String>, String> tableColumn = new TableColumn<>(tableColumnName);
                JavaFxViewUtil.setTableColumnMapValueFactory(tableColumn, tableColumnName);
                fileBuildToolController.getFieldTableView().getColumns().add(tableColumn);
            }
            List<Map<String, String>> fieldTableDatalist = jsonObject.getObject("fieldTableData", new TypeReference<List<Map<String, String>>>() {
            });
            fileBuildToolController.getFieldTableData().addAll(fieldTableDatalist);
            TooltipUtil.showToast("加载配置成功！");
        }
    }
}
