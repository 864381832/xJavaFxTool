package com.xwintop.xJavaFxTool.services.assistTools;

import com.xwintop.xJavaFxTool.controller.assistTools.DecompilerWxApkgToolController;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * @ClassName: DecompilerWxApkgToolService
 * @Description: 微信小程序反编译工具
 * @author: xufeng
 * @date: 2018/7/4 14:44
 */

@Getter
@Setter
@Slf4j
public class DecompilerWxApkgToolService {
    private DecompilerWxApkgToolController decompilerWxApkgToolController;

    private DataInputStream in;//in 流
    private WxFile wxFile;//当前文件
    private WxFile currentWxFile;//当前文件
    private int count;//当前文件

    public void decompileButtonAction() throws Exception {
        String filePath = decompilerWxApkgToolController.getPackageFileTextField().getText();
        if (StringUtils.isBlank(filePath)) {
            TooltipUtil.showToast("原包路径未填写。");
            return;
        }
        String decompilePath = decompilerWxApkgToolController.getDecompilePathTextField().getText();
        if (StringUtils.isBlank(decompilePath)) {
            decompilePath = FilenameUtils.getFullPath(filePath) + FilenameUtils.getBaseName(filePath);
            FileUtils.forceMkdir(new File(decompilePath + "\\"));
        }
        decompilePath = StringUtils.appendIfMissing(decompilePath, "/", "/", "\\");
        this.in = new DataInputStream(new FileInputStream(filePath));
        decodeWxFile(this.in);
        while (currentWxFile != null) {
            String name = currentWxFile.getName();
            File file = new File(decompilePath, name);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            OutputStream o = new FileOutputStream(file);
            this.readWxFile(o);
            o.close();
        }
        if (in != null) {
            in.close();
        }
        TooltipUtil.showToast("反编译成功，文件保存在：" + decompilePath + "目录下。");
    }

    public DecompilerWxApkgToolService(DecompilerWxApkgToolController decompilerWxApkgToolController) {
        this.decompilerWxApkgToolController = decompilerWxApkgToolController;
    }

    /**
     * 解码文件名区域
     *
     * @param in in
     */
    private void decodeWxFile(DataInputStream in) throws IOException {
        in.readByte();  //标识
        in.readInt();   //未知
        in.readInt();   //文件名域长度
        in.readInt();   //内容域长度
        in.readByte();  //未知
        count = in.readInt();   //文件数量
        if (count > 0) {
            WxFile nextWxFile = null;
            for (int i = 0; i < count; i++) {
                byte[] name = new byte[in.readInt()];                //文件名长度
                int j = in.read(name);
                int offset = in.readInt();                           //偏移
                int length = in.readInt();                           //内容长度
                WxFile currentWxFile = new WxFile();
                currentWxFile.setName(new String(name, "UTF-8"));
                currentWxFile.setOffset(offset);
                currentWxFile.setLength(length);
                if (nextWxFile == null) {
                    nextWxFile = currentWxFile;
                    wxFile = nextWxFile;
                } else {
                    nextWxFile.setNext(currentWxFile);
                    nextWxFile = currentWxFile;
                }
            }
        }
        currentWxFile = wxFile;                                     //当前处理的文件
    }

    /**
     * @param out 输出流
     */
    public void readWxFile(OutputStream out) throws IOException {
        if (currentWxFile == null) {
            throw new IOException("no more files");
        }
        int length = currentWxFile.getLength();
        byte[] buffer = new byte[length];
        int readSize;
        while (length > 0 && (readSize = in.read(buffer)) != -1) {
            length -= readSize;
            out.write(buffer, 0, readSize);
        }
        currentWxFile = currentWxFile.getNext();
    }

    @Data
    public class WxFile {
        private String name;//文件名称
        private int offset;//偏移量
        private int length;//文件长度
        private WxFile next;//文件长度
    }
}