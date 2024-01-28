package com.xwintop.xJavaFxTool.services.debugTools;

import cn.hutool.http.HttpUtil;
import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xwintop.xJavaFxTool.controller.debugTools.SwitchHostsToolController;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.nio.charset.Charset;
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
    String lineSeparator = System.lineSeparator();
    private static String sudoPwd = null;

    private String commonHostString = "# common" + lineSeparator +
            "# 这儿是公用 hosts，其内容会插入到各个方案最前面";

    //    private String systemHostString = "";
    private String localHost1String = "# 方案一" + lineSeparator +
            "# 什么也没有绑定" + lineSeparator +
            "#" + lineSeparator +
            "# 在这儿输入你需要绑定的 hosts";

    private String localHost2String = "# 方案二\n";

    public void reloadSystemHosts() {
        switchHostsToolController.getHostTextArea().clear();
        switchHostsToolController.getHostTextArea().replaceText(0, 0, getHostsString());
    }

    public void reloadGithubHosts() {
        switchHostsToolController.getHostTextArea().clear();
        switchHostsToolController.getHostTextArea().replaceText(0, 0, getGithubHost());
    }

    public void updateGithubHosts() {
        StringBuilder stringBuilder = new StringBuilder();
        String hostsString = getHostsString();
        LineIterator lineIterator = new LineIterator(new StringReader(hostsString));
        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            if (!line.contains("github")) {
                stringBuilder.append(line).append(lineSeparator);
            }
        }
        stringBuilder.append(lineSeparator).append(getGithubHost());
        switchHostsToolController.getHostTextArea().clear();
        switchHostsToolController.getHostTextArea().replaceText(0, 0, stringBuilder.toString());
    }

    public void editAction() {
        Boolean ret = this.saveHostsContent(switchHostsToolController.getHostTextArea().getText());
        if (ret) {
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

                    String[] cmds = {"/bin/bash", "-c",
                            "echo \"" + sudoPwd + "\" | " + "sudo" + " -S " + " chmod 777 " + file};
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

    //获取hosts内容
    public static String getHostsString() {
        String fileName = getHostsFilePath();
        String systemHostString = null;
        try {
            systemHostString = FileUtils.readFileToString(new File(fileName), Charset.defaultCharset());
        } catch (IOException e) {
            log.error("获取hosts内容失败：", e);
        }
        return systemHostString;
    }

    //获取hosts文件路径
    public static String getHostsFilePath() {
        String fileName = null;
        if (SystemUtil.getOsInfo().isWindows()) {
            fileName = "C://WINDOWS//system32//drivers//etc//hosts";
        } else {
            fileName = "/etc/hosts";
        }
        return fileName;
    }

    //获取GitHub地址
    public String getGithubHost() {
        StringBuilder stringBuilder = new StringBuilder();
        String ipString = HttpUtil.get("https://ipchaxun.com/domain/read.do?domain=github.com");
        if (JSON.isValid(ipString)) {
            JSONObject jsonObject = JSON.parseObject(ipString);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                String ip = jsonArray.getJSONObject(i).getString("ip");
                String ips = "wintop github.com" + lineSeparator +
                        "wintop gist.github.com" + lineSeparator +
                        "wintop assets-cdn.github.com" + lineSeparator +
                        "wintop raw.githubusercontent.com" + lineSeparator +
                        "wintop gist.githubusercontent.com" + lineSeparator +
                        "wintop cloud.githubusercontent.com" + lineSeparator +
                        "wintop camo.githubusercontent.com" + lineSeparator +
                        "wintop avatars0.githubusercontent.com" + lineSeparator +
                        "wintop avatars1.githubusercontent.com" + lineSeparator +
                        "wintop avatars2.githubusercontent.com" + lineSeparator +
                        "wintop avatars3.githubusercontent.com" + lineSeparator +
                        "wintop avatars4.githubusercontent.com" + lineSeparator +
                        "wintop avatars5.githubusercontent.com" + lineSeparator +
                        "wintop avatars6.githubusercontent.com" + lineSeparator +
                        "wintop avatars7.githubusercontent.com" + lineSeparator +
                        "wintop avatars8.githubusercontent.com ";
                stringBuilder.append(ips.replaceAll("wintop", ip));
                stringBuilder.append(lineSeparator + lineSeparator + lineSeparator);
            }
        }
        return stringBuilder.toString();
    }
}
