package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.FileRenameToolController;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: FileRenameToolService
 * @Description: 文件重命名工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:35
 */

@Getter
@Setter
@Slf4j
public class FileRenameToolService {
    private FileRenameToolController fileRenameToolController;

    public FileRenameToolService(FileRenameToolController fileRenameToolController) {
        this.fileRenameToolController = fileRenameToolController;
    }

    public void renameAction() {
        for (Map<String, String> fileInfoTableDatum : fileRenameToolController.getFileInfoTableData()) {
            if ("true".equals(fileInfoTableDatum.get("status"))) {
                File file = new File(fileInfoTableDatum.get("filesPath"));
                File newFile = new File(file.getParent(), fileInfoTableDatum.get("newFileName"));
                file.renameTo(newFile);
                fileInfoTableDatum.put("fileName", newFile.getName());
                fileInfoTableDatum.put("filesPath", newFile.getPath());
            }
        }
        fileRenameToolController.getFileInfoTableView().refresh();
        TooltipUtil.showToast("重命名成功！");
    }

    public void addFileAction(File file) {
        if (file != null) {
            Map<String, String> dataRow = new HashMap<String, String>();
            dataRow.put("status", "true");
            dataRow.put("fileName", file.getName());
            dataRow.put("newFileName", "");
            dataRow.put("errorInfo", "");
            dataRow.put("filesPath", file.getPath());
            fileRenameToolController.getFileInfoTableData().add(dataRow);
        }
    }

    public void generateRenameDestFilesOfFormat() {
        if (!fileRenameToolController.getFileInfoTableData().isEmpty()) {
            int startNumber = fileRenameToolController.getStartNumberOfRenameTab().getValue();
            String fileQueryString = fileRenameToolController.getFileQueryStringOfRenameTab().getText();
            String fileReplaceString = fileRenameToolController.getFileReplaceStringOfRenameTab().getText();
            String filePrefixString = fileRenameToolController.getFilePrefixAddableText().getText();
            String filePostfixString = fileRenameToolController.getFilePostfixAddableText().getText();
            for (Map<String, String> fileInfoTableDatum : fileRenameToolController.getFileInfoTableData()) {
                String fileName = fileInfoTableDatum.get("fileName");
                StringBuffer newFileName = new StringBuffer(fileName);
                newFileName.insert(0, filePrefixString);
                if (StringUtils.isNoneEmpty(fileQueryString, fileReplaceString)) {
                    newFileName.append(fileName.replaceAll(fileQueryString, fileReplaceString));
                }
                if (fileRenameToolController.getTextConvertDxRadioButton().isSelected()) {
                    newFileName.replace(0, newFileName.length(), newFileName.toString().toUpperCase());
                }
                if (fileRenameToolController.getTextConvertXxRadioButton().isSelected()) {
                    newFileName.replace(0, newFileName.length(), newFileName.toString().toLowerCase());
                }
                newFileName.append(filePostfixString);
                if (StringUtils.isNotEmpty(fileRenameToolController.getFileZjAddableTextTextField().getText())) {
                    newFileName.insert(fileRenameToolController.getFileZjAddableTextSpinner().getValue(), fileRenameToolController.getFileZjAddableTextTextField().getText());
                }
                if (fileRenameToolController.getAddXhCheckBox().isSelected()) {
                    if (fileRenameToolController.getAddXhBwSpinner().getValue() > 0) {
                        newFileName.append(String.format("%0" + fileRenameToolController.getAddXhBwSpinner().getValue() + "d", startNumber));
                    } else {
                        newFileName.append(startNumber);
                    }
                    startNumber = startNumber + fileRenameToolController.getAddXhJgSpinner().getValue();
                }
                if (fileRenameToolController.getDeleteTopCheckBox().isSelected()) {
                    newFileName.delete(0, fileRenameToolController.getDeleteTopSpinner().getValue());
                }
                if (fileRenameToolController.getDeleteWbCheckBox().isSelected()) {
                    newFileName.delete(newFileName.length() - fileRenameToolController.getDeleteWbSpinner().getValue(), fileRenameToolController.getDeleteWbSpinner().getValue());
                }
                if (fileRenameToolController.getDeleteZjCheckBox().isSelected()) {
                    newFileName.delete(fileRenameToolController.getDeleteZj1Spinner().getValue(), fileRenameToolController.getDeleteZj2Spinner().getValue());
                }
                if (fileRenameToolController.getKzmConvertDxRadioButton().isSelected() || fileRenameToolController.getKzmConvertXxRadioButton().isSelected() || fileRenameToolController.getKzmConvertSzmdxRadioButton().isSelected() || StringUtils.isNotEmpty(fileRenameToolController.getKzmConvertContentTextField().getText())) {
                    String extensionName = FilenameUtils.getExtension(newFileName.toString());
                    if (StringUtils.isNotEmpty(extensionName)) {
                        if (fileRenameToolController.getKzmConvertDxRadioButton().isSelected()) {
                            extensionName = extensionName.toUpperCase();
                        }
                        if (fileRenameToolController.getKzmConvertXxRadioButton().isSelected()) {
                            extensionName = extensionName.toLowerCase();
                        }
                        if (fileRenameToolController.getKzmConvertSzmdxRadioButton().isSelected()) {
                            extensionName = extensionName.substring(0, 1).toUpperCase() + extensionName.substring(1);
                        }
                        if (StringUtils.isNotEmpty(fileRenameToolController.getKzmConvertContentTextField().getText())) {
                            extensionName = fileRenameToolController.getKzmConvertContentTextField().getText();
                        }
                        String baseName = FilenameUtils.getBaseName(newFileName.toString());
                        newFileName.setLength(0);
                        newFileName.append(baseName).append(".").append(extensionName);
                    }
                }
                fileInfoTableDatum.put("newFileName", newFileName.toString());
            }
            fileRenameToolController.getFileInfoTableView().refresh();
        }
    }

}