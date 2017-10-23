package com.xwintop.xJavaFxTool.services.debugTools;

import com.xwintop.xJavaFxTool.controller.debugTools.SwitchHostsToolController;

import org.apache.commons.io.FileUtils;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class SwitchHostsToolService {
    private SwitchHostsToolController switchHostsToolController;

    private String commonHostString = "# common\n" +
            "# 这儿是公用 hosts，其内容会插入到各个方案最前面";
    private String systemHostString = "";
    private String localHost1String = "# 方案一\n" +
            "# 什么也没有绑定\n" +
            "#\n" +
            "# 在这儿输入你需要绑定的 hosts";
    private String localHost2String = "# 方案二\n";

    public void reloadSystemHosts() throws Exception {
        String fileName = null;
        // 判断系统
        if ("linux".equalsIgnoreCase(System.getProperty("os.name"))) {
            fileName = "/etc/hosts";
        } else {
            fileName = "C://WINDOWS//system32//drivers//etc//hosts";
        }
        log.info("fileName:"+fileName);
        String systemHostString = FileUtils.readFileToString(new File(fileName),"utf-8");
        switchHostsToolController.getHostTextArea().setText(systemHostString);

    }

    public SwitchHostsToolService(SwitchHostsToolController switchHostsToolController) {
        this.switchHostsToolController = switchHostsToolController;
    }
}
