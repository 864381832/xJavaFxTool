package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.FileCompressToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: FileCompressToolService
 * @Description: 文件解压缩工具
 * @author: xufeng
 * @date: 2019/10/26 0026 19:17
 */

@Getter
@Setter
@Slf4j
public class FileCompressToolService {
    private FileCompressToolController fileCompressToolController;

    public FileCompressToolService(FileCompressToolController fileCompressToolController) {
        this.fileCompressToolController = fileCompressToolController;
    }

    public void compressAction() {

    }
}