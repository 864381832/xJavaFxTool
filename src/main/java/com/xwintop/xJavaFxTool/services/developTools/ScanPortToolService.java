package com.xwintop.xJavaFxTool.services.developTools;

import com.xwintop.xJavaFxTool.controller.developTools.ScanPortToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class ScanPortToolService {
    private ScanPortToolController scanPortToolController;

    public ScanPortToolService(ScanPortToolController scanPortToolController) {
        this.scanPortToolController = scanPortToolController;
    }

    public void scanAction(){

    }
}