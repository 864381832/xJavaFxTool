package com.xwintop.xJavaFxTool.services.codeTools;

import com.xwintop.xJavaFxTool.controller.codeTools.CharsetDetectToolController;
import com.xwintop.xJavaFxTool.utils.DirectoryTreeUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: CharsetDetectToolService
 * @Description: 编码检测工具
 * @author: xufeng
 * @date: 2019/4/27 0027 18:14
 */

@Getter
@Setter
@Slf4j
public class CharsetDetectToolService {
    private CharsetDetectToolController charsetDetectToolController;

    public CharsetDetectToolService(CharsetDetectToolController charsetDetectToolController) {
        this.charsetDetectToolController = charsetDetectToolController;
    }

    public void detectAction() throws Exception {
        String watchPath = charsetDetectToolController.getDetectPathTextField().getText();
        if (StringUtils.isEmpty(watchPath)) {
            TooltipUtil.showToast("检测目录不能为空！");
            return;
        }
        int detectLength = Integer.parseInt(charsetDetectToolController.getDetectSizeTextField().getText().trim());
        File file = new File(watchPath);
        if (!file.exists()) {
            try {
                String fileCharset = detectFileCharset(new URL(watchPath).openStream(), detectLength);
                charsetDetectToolController.getResultTextArea().appendText(watchPath + "         Charset: " + fileCharset + "\n");
                return;
            } catch (Exception e) {
                log.error("URL检查失败！");
            }
            TooltipUtil.showToast("检测文本/文件夹/URL不存在或错误！");
            return;
        }
        if (file.isDirectory()) {
            Path path = file.toPath();
            Iterator<Path> iterator = null;
            if (charsetDetectToolController.getIncludeSubdirectoryCheckBox().isSelected()) {
                iterator = Files.walk(path).iterator();
            } else {
                iterator = Files.newDirectoryStream(path).iterator();
            }
            boolean sRegex = charsetDetectToolController.getFileNameSupportRegexCheckBox().isSelected();
            String fileNameContains = charsetDetectToolController.getFileNameContainsTextField().getText();
            String fileNameNotContains = charsetDetectToolController.getFileNameNotContainsTextField().getText();
            Pattern fileNameCsPattern = null;
            Pattern fileNameNCsPattern = null;
            if (sRegex) {
                fileNameCsPattern = Pattern.compile(fileNameContains, Pattern.CASE_INSENSITIVE);
                fileNameNCsPattern = Pattern.compile(fileNameNotContains, Pattern.CASE_INSENSITIVE);
            }
            while (iterator.hasNext()) {
                Path nextPath = iterator.next();
                if (Files.isRegularFile(nextPath) && DirectoryTreeUtil.ifMatchText(nextPath.getFileName().toString(), fileNameContains, fileNameNotContains, sRegex, fileNameCsPattern, fileNameNCsPattern)) {
                    charsetDetectToolController.getResultTextArea().appendText(nextPath.toString() + "         Charset: " + detectFileCharset(nextPath.toFile(), detectLength) + "\n");
                }
            }
        } else if (file.isFile()) {
            charsetDetectToolController.getResultTextArea().appendText(file.getAbsolutePath() + "         Charset: " + detectFileCharset(file, detectLength) + "\n");
        }
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