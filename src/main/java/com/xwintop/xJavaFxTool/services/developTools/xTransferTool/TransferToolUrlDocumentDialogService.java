package com.xwintop.xJavaFxTool.services.developTools.xTransferTool;

import com.xwintop.xJavaFxTool.controller.developTools.xTransferTool.TransferToolUrlDocumentDialogController;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: TransferToolUrlDocumentDialogService
 * @Description: 历史连接编辑
 * @author: xufeng
 * @date: 2019/5/29 16:08
 */

@Getter
@Setter
@Slf4j
public class TransferToolUrlDocumentDialogService {
    private TransferToolUrlDocumentDialogController transferToolUrlDocumentDialogController;

    private static final File CONFIG_FILE = new File("./javaFxConfigure/urlDocumentConfigure.yml");

    public static List<Map<String, String>> getConfig() {
        try {
            if (!CONFIG_FILE.exists()) {
                FileUtils.touch(CONFIG_FILE);
            }
            Yaml yaml = new Yaml();
            List<Map<String, String>> list = yaml.load(FileUtils.readFileToString(CONFIG_FILE, "UTF-8"));
            return list;
        } catch (Exception e) {
            log.error("加载配置失败", e);
        }
        return null;
    }

    public static void addConfig(String host, String port, String userName, String password, String path) {
        try {
            if (!CONFIG_FILE.exists()) {
                FileUtils.touch(CONFIG_FILE);
            }
            Map<String, String> map = new HashMap<>();
            map.put("name", host);
            map.put("host", host);
            map.put("port", port);
            map.put("userName", userName);
            map.put("password", password);
            map.put("path", path);
            Yaml yaml = new Yaml();
            List<Map<String, String>> list = yaml.load(FileUtils.readFileToString(CONFIG_FILE, "UTF-8"));
            if (list == null) {
                list = new ArrayList<>();
            }
            for (Map<String, String> smap : list) {
                if (StringUtils.equals(smap.get("host"), host) && StringUtils.equals(smap.get("port"), port)
                        && StringUtils.equals(smap.get("userName"), userName) && StringUtils.equals(smap.get("password"), password)
                        && StringUtils.equals(smap.get("path"), path)) {
                    return;
                }
            }
            list.add(map);
            Writer writer = new FileWriter(CONFIG_FILE);
            yaml.dump(list, writer);
            writer.close();
        } catch (Exception e) {
            log.error("保存配置失败", e);
        }
    }

    public TransferToolUrlDocumentDialogService(TransferToolUrlDocumentDialogController transferToolUrlDocumentDialogController) {
        this.transferToolUrlDocumentDialogController = transferToolUrlDocumentDialogController;
    }

    public void saveConfigure() throws Exception {
        Yaml yaml = new Yaml();
        Writer writer = new FileWriter(CONFIG_FILE);
        yaml.dump(transferToolUrlDocumentDialogController.getTableData(), writer);
        writer.close();
        TooltipUtil.showToast("保存配置成功,保存在：" + CONFIG_FILE.getPath());
    }

    public void loadingConfigure() throws Exception {
        try {
            transferToolUrlDocumentDialogController.getTableData().clear();
            List<Map<String, String>> list = TransferToolUrlDocumentDialogService.getConfig();
            if (list != null) {
                transferToolUrlDocumentDialogController.getTableData().addAll(list);
            }
        } catch (Exception e) {
            log.error("加载配置失败：", e);
        }
    }
}
