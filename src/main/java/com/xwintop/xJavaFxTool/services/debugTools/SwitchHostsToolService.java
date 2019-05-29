package com.xwintop.xJavaFxTool.services.debugTools;

import com.sun.jna.Platform;
import com.xwintop.xJavaFxTool.controller.debugTools.SwitchHostsToolController;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

/**
 * @ClassName: SwitchHostsToolService
 * @Description: 切换Hosts工具
 * @author: xufeng
 * @date: 2018/1/31 15:17
 */

@Getter
@Setter
@Slf4j
public class SwitchHostsToolService {
    private SwitchHostsToolController switchHostsToolController;
    private static String sudoPwd = null;

    private String commonHostString = "# common\n" +
            "# 这儿是公用 hosts，其内容会插入到各个方案最前面";
    //    private String systemHostString = "";
    private String localHost1String = "# 方案一\n" +
            "# 什么也没有绑定\n" +
            "#\n" +
            "# 在这儿输入你需要绑定的 hosts";
    private String localHost2String = "# 方案二\n";

    public void reloadSystemHosts() throws Exception {
        String fileName = this.getHostsFilePath();
        String systemHostString = FileUtils.readFileToString(new File(fileName));
//        switchHostsToolController.getHostTextArea().setText(systemHostString);
        switchHostsToolController.getHostTextArea().clear();
        switchHostsToolController.getHostTextArea().replaceText(0, 0, systemHostString);
    }

    public void editAction() {
        Boolean ret = this.saveHostsContent(switchHostsToolController.getHostTextArea().getText());
        if (ret == true) {
            TooltipUtil.showToast("save success!");
            switchHostsToolController.getHostTextArea().setStyle("-fx-border-color: green;");
        } else {
            TooltipUtil.showToast("save error!");
            switchHostsToolController.getHostTextArea().setStyle("-fx-border-color: red;");
        }
    }

    public SwitchHostsToolService(SwitchHostsToolController switchHostsToolController) {
        this.switchHostsToolController = switchHostsToolController;
    }

    public Boolean saveHostsContent(String fileContent) {
        String fileName = this.getHostsFilePath();
        File file = new File(fileName);
        try {
            if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_UNIX) {
                if (sudoPwd == null) {
                    TextInputDialog dialog = new TextInputDialog("");
                    dialog.setTitle("Sudo Dialog");
                    dialog.setHeaderText("Look, a Sudo Input Dialog");
                    dialog.setContentText("Please enter your sudo pwd:");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {

                    }
                    result.ifPresent(pwd -> sudoPwd = pwd);

                    String[] cmds = {"/bin/bash", "-c", "echo \"" + sudoPwd + "\" | " + "sudo" + " -S " + " chmod 777 " + file};
                    Process process = Runtime.getRuntime().exec(cmds);

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line;
                    Boolean isSucc = true;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.contains("try again")) {
                            isSucc = false;
                        }
                    }

                    if (!isSucc) {
                        sudoPwd = null;

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Wrong Password");
                        alert.setHeaderText("Wrong Password");
                        alert.setContentText("Please try agian");
                        alert.showAndWait();

                        return false;
                    }
                }
            }
            FileUtils.writeStringToFile(file, fileContent, "UTF-8");
        } catch (IOException e) {
            log.error("报错hosts文件失败", e);
            return false;
        }
        return true;
    }

    public String getHostsFilePath() {
        String fileName = null;
        if (Platform.isWindows()) {
            fileName = "C://WINDOWS//system32//drivers//etc//hosts";
        } else {
            fileName = "/etc/hosts";
        }
        return fileName;
    }
}
