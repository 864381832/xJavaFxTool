package com.xwintop.xJavaFxTool.services.codeTools;

import com.xwintop.xJavaFxTool.controller.codeTools.FileUnicodeTransformationToolController;
import com.xwintop.xJavaFxTool.utils.DirectoryTreeUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @ClassName: FileUnicodeTransformationToolService
 * @Description: 文件编码转换工具
 * @author: xufeng
 * @date: 2019/10/9 15:00
 */

@Getter
@Setter
@Slf4j
public class FileUnicodeTransformationToolService {
    private FileUnicodeTransformationToolController fileUnicodeTransformationToolController;

    public FileUnicodeTransformationToolService(FileUnicodeTransformationToolController fileUnicodeTransformationToolController) {
        this.fileUnicodeTransformationToolController = fileUnicodeTransformationToolController;
    }

    public void transformationAction() throws Exception {
        String watchPath = fileUnicodeTransformationToolController.getDetectPathTextField().getText();
        if (StringUtils.isEmpty(watchPath)) {
            TooltipUtil.showToast("检测目录不能为空！");
            return;
        }
        File file = new File(watchPath);
        if (file.isDirectory()) {
            Path path = file.toPath();
            Iterator<Path> iterator = null;
            AutoCloseable autoCloseable = null;
            if (fileUnicodeTransformationToolController.getIncludeSubdirectoryCheckBox().isSelected()) {
                Stream<Path> stream = Files.walk(path);
                autoCloseable = stream;
                iterator = stream.iterator();
            } else {
                DirectoryStream<Path> stream = Files.newDirectoryStream(path);
                autoCloseable = stream;
                iterator = stream.iterator();
            }
            try {
                boolean sRegex = fileUnicodeTransformationToolController.getFileNameSupportRegexCheckBox().isSelected();
                String fileNameContains = fileUnicodeTransformationToolController.getFileNameContainsTextField().getText();
                String fileNameNotContains = fileUnicodeTransformationToolController.getFileNameNotContainsTextField().getText();
                Pattern fileNameCsPattern = null;
                Pattern fileNameNCsPattern = null;
                if (sRegex) {
                    fileNameCsPattern = Pattern.compile(fileNameContains, Pattern.CASE_INSENSITIVE);
                    fileNameNCsPattern = Pattern.compile(fileNameNotContains, Pattern.CASE_INSENSITIVE);
                }
                while (iterator.hasNext()) {
                    Path nextPath = iterator.next();
                    if (Files.isRegularFile(nextPath) && DirectoryTreeUtil.ifMatchText(nextPath.getFileName().toString(), fileNameContains, fileNameNotContains, sRegex, fileNameCsPattern, fileNameNCsPattern)) {
                        File newFile = nextPath.toFile();
                        String showHideFile = fileUnicodeTransformationToolController.getShowHideFileChoice().getValue();
                        if ("非隐藏".equals(showHideFile) && file.isHidden()) {
                            return;
                        } else if ("隐藏文件".equals(showHideFile) && !file.isHidden()) {
                            return;
                        }
                        fileUnicodeTransformation(newFile);
                    }
                }
            } finally {
                if (autoCloseable != null) {
                    autoCloseable.close();
                }
            }
        } else if (file.isFile()) {
            fileUnicodeTransformation(file);
        }
    }

    private void fileUnicodeTransformation(File file) throws Exception {
        String oldFileUnicode = fileUnicodeTransformationToolController.getOldFileUnicodeComboBox().getValue();
        if ("自动检测".equals(oldFileUnicode)) {
            oldFileUnicode = FileUnicodeTransformationToolService.detectFileCharset(file, 51200);
        }
        String newFileUnicode = fileUnicodeTransformationToolController.getNewFileUnicodeComboBox().getValue();
        String newFilePath = fileUnicodeTransformationToolController.getNewFilePathTextField().getText();
        String fileConcel = FileUtils.readFileToString(file, oldFileUnicode);
        if (StringUtils.isNotEmpty(newFilePath)) {
            newFilePath = StringUtils.removeEnd(newFilePath, "/");
            newFilePath = StringUtils.removeEnd(newFilePath, "\\");
            String watchPath = fileUnicodeTransformationToolController.getDetectPathTextField().getText();
            watchPath = StringUtils.removeEnd(watchPath, "/");
            watchPath = StringUtils.removeEnd(watchPath, "\\");
            File watchPathFile = new File(watchPath);
            String subPath = "";
            if (watchPathFile.isDirectory()) {
                subPath = file.getParent().replace(watchPath, "");
            }
            file = new File(newFilePath + "/" + subPath, file.getName());
        }
        FileUtils.writeByteArrayToFile(file, fileConcel.getBytes(newFileUnicode));
        fileUnicodeTransformationToolController.getResultTextArea().appendText(file.getAbsolutePath() + "         转换完成\n");
    }

    public static String detectFileCharset(File file, int detectLength) throws Exception {
        return detectFileCharset(new FileInputStream(file), detectLength);
    }

    public static String detectFileCharset(InputStream inputStream, int detectLength) throws Exception {
        String charset = null;
        byte[] buf = new byte[detectLength];
        UniversalDetector detector = new UniversalDetector(null);
        int nread;
        while ((nread = inputStream.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.handleData(buf, 0, buf.length);
        detector.dataEnd();
        charset = detector.getDetectedCharset();
        detector.reset();
        if (inputStream != null) {
            inputStream.close();
        }
        return charset;
    }
}