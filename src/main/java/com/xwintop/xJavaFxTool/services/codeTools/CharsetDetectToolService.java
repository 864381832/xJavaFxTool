package com.xwintop.xJavaFxTool.services.codeTools;

import com.xwintop.xJavaFxTool.controller.codeTools.CharsetDetectToolController;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

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
        Path path = Paths.get(watchPath);
        if (!Files.exists(path)) {
            TooltipUtil.showToast("检测目录不存在！");
            return;
        }
        int detectLength = Integer.parseInt(charsetDetectToolController.getDetectSizeTextField().getText().trim());
        if (Files.isDirectory(path)) {
            Iterator<Path> iterator = Files.newDirectoryStream(path).iterator();
            while (iterator.hasNext()) {
                Path nextPath = iterator.next();
                if (Files.isRegularFile(nextPath)) {
                    charsetDetectToolController.getResultTextArea().appendText(nextPath.toString() + "         Charset: " + detectFileCharset(nextPath.toFile(), detectLength) + "\n");
                }
            }
        } else if (Files.isRegularFile(path)) {
            charsetDetectToolController.getResultTextArea().appendText(path.toString() + "         Charset: " + detectFileCharset(path.toFile(), detectLength) + "\n");
        }
    }

    public static String detectFileCharset(File file, int detectLength) throws IOException {
        String charset = null;
        FileInputStream fis = null;
        try {
            byte[] buf = new byte[detectLength];
            fis = new FileInputStream(file);
            UniversalDetector detector = new UniversalDetector(null);
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            charset = detector.getDetectedCharset();
            detector.reset();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return charset;
    }
}