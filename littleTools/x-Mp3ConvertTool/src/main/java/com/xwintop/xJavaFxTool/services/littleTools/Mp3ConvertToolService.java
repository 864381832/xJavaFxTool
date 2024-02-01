package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.Mp3ConvertToolController;
import com.xwintop.xJavaFxTool.utils.NcmDump;
import com.xwintop.xcore.util.FileUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: Mp3ConvertToolService
 * @Description: mp3格式转换工具
 * @author: xufeng
 * @date: 2019/8/8 0008 20:41
 */

@Getter
@Setter
@Slf4j
public class Mp3ConvertToolService {
    private Mp3ConvertToolController mp3ConvertToolController;

    public Mp3ConvertToolService(Mp3ConvertToolController mp3ConvertToolController) {
        this.mp3ConvertToolController = mp3ConvertToolController;
    }

    public void addTableData(File file) {
        Map<String, String> rowValue = new HashMap<>();
        rowValue.put("fileName", file.getName());
        rowValue.put("absolutePath", file.getAbsolutePath());
        rowValue.put("fileSize", FileUtil.formatFileSize(file.length()));
        rowValue.put("convertStatus", "待转换");
        mp3ConvertToolController.getTableData().add(rowValue);
    }

    public void convertAction() {
        for (Map<String, String> tableDatum : mp3ConvertToolController.getTableData()) {
            String absolutePath = tableDatum.get("absolutePath");
            if (StringUtils.endsWithIgnoreCase(absolutePath, ".qmcflac")) {
                if (convertQmc(absolutePath)) {
                    tableDatum.put("convertStatus", "转换成功");
                } else {
                    tableDatum.put("convertStatus", "转换失败");
                }
            } else if (StringUtils.endsWithIgnoreCase(absolutePath, ".ncm")) {
                if (convertNcm(absolutePath)) {
                    tableDatum.put("convertStatus", "转换成功");
                } else {
                    tableDatum.put("convertStatus", "转换失败");
                }
            }
        }
        mp3ConvertToolController.getTableViewMain().refresh();
    }

    //QQ音乐格式转换
    private boolean convertQmc(String absolutePath) {
        try {
            byte[] buffer = FileUtils.readFileToByteArray(new File(absolutePath));
            QmcDecode dc = new QmcDecode();
            for (int i = 0; i < buffer.length; ++i) {
                buffer[i] = (byte) (dc.NextMask() ^ buffer[i]);
            }
            String file = StringUtils.removeEndIgnoreCase(absolutePath, ".qmcflac");
            if (StringUtils.isEmpty(mp3ConvertToolController.getOutputFolderTextField().getText())) {
                FileUtils.writeByteArrayToFile(new File(file + ".mp3"), buffer);
            } else {
                FileUtils.writeByteArrayToFile(new File(mp3ConvertToolController.getOutputFolderTextField().getText(), new File(file + ".mp3").getName()), buffer);
            }
            return true;
        } catch (Exception e) {
            log.error("转换异常", e);
            return false;
        }
    }

    //网易云音乐格式转换
    private boolean convertNcm(String absolutePath) {
        if (StringUtils.isEmpty(mp3ConvertToolController.getOutputFolderTextField().getText())) {
            return NcmDump.dump(new File(absolutePath), new File(absolutePath).getParentFile());
        } else {
            return NcmDump.dump(new File(absolutePath), new File(mp3ConvertToolController.getOutputFolderTextField().getText()));
        }
    }
}

class QmcDecode {
    private int x = -1;
    private int y = 8;
    private int dx = 1;
    private int index = -1;
    private int[][] seedMap = {
            {0x4a, 0xd6, 0xca, 0x90, 0x67, 0xf7, 0x52},
            {0x5e, 0x95, 0x23, 0x9f, 0x13, 0x11, 0x7e},
            {0x47, 0x74, 0x3d, 0x90, 0xaa, 0x3f, 0x51},
            {0xc6, 0x09, 0xd5, 0x9f, 0xfa, 0x66, 0xf9},
            {0xf3, 0xd6, 0xa1, 0x90, 0xa0, 0xf7, 0xf0},
            {0x1d, 0x95, 0xde, 0x9f, 0x84, 0x11, 0xf4},
            {0x0e, 0x74, 0xbb, 0x90, 0xbc, 0x3f, 0x92},
            {0x00, 0x09, 0x5b, 0x9f, 0x62, 0x66, 0xa1}
    };

    public int NextMask() {
        int ret;
        index++;
        if (x < 0) {
            dx = 1;
            y = ((8 - y) % 8);
            ret = ((8 - y) % 8);
            ret = 0xc3;
        } else if (x > 6) {
            dx = -1;
            y = 7 - y;
            ret = 0xd8;
        } else {
            ret = seedMap[y][x];
        }

        x += dx;
        if (index == 0x8000 || (index > 0x8000 && (index + 1) % 0x8000 == 0)) {
            return NextMask();
        }
        return ret;
    }
}