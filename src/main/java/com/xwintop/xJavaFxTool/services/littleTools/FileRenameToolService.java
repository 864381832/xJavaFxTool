package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.FileRenameToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
}