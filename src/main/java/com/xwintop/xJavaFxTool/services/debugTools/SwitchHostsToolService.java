package com.xwintop.xJavaFxTool.services.debugTools;

import com.xwintop.xJavaFxTool.controller.debugTools.SwitchHostsToolController;
import com.xwintop.xcore.util.SystemInfoUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @ClassName: SwitchHostsToolService
 * @Description: 切换Hosts工具
 * @author: xufeng
 * @date: 2018/1/31 15:17
 */

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
        String fileName = SystemInfoUtil.getHostsFilePath();
        String systemHostString = FileUtils.readFileToString(new File(fileName),"utf-8");
        switchHostsToolController.getHostTextArea().setText(systemHostString);

    }

    public void editAction() throws Exception {
        String fileName = SystemInfoUtil.getHostsFilePath();
        String systemHostString = switchHostsToolController.getHostTextArea().getText();
        FileUtils.writeByteArrayToFile(new File(fileName),systemHostString.getBytes());
        TooltipUtil.showToast("保存配置成功");
    }

    public SwitchHostsToolService(SwitchHostsToolController switchHostsToolController) {
        this.switchHostsToolController = switchHostsToolController;
    }
}
