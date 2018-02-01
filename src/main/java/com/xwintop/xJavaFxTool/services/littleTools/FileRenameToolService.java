package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.FileRenameToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class FileRenameToolService {
    private FileRenameToolController fileRenameToolController;

    public FileRenameToolService(FileRenameToolController fileRenameToolController) {
        this.fileRenameToolController = fileRenameToolController;
    }
}