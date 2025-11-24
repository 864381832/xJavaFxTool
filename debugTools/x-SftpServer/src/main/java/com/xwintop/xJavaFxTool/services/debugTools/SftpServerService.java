package com.xwintop.xJavaFxTool.services.debugTools;

import com.xwintop.xJavaFxTool.controller.debugTools.SftpServerController;
import com.xwintop.xJavaFxTool.model.SftpServerTableBean;
import com.xwintop.xcore.util.ConfigureUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.session.SessionContext;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

/**
 * @ClassName: FtpServerService
 * @Description: Ftp服务器
 * @author: xufeng
 * @date: 2019/4/25 0025 23:29
 */

@Getter
@Setter
@Slf4j
public class SftpServerService {
    private SftpServerController sftpServerController;

    private SshServer server = null;

    public SftpServerService(SftpServerController sftpServerController) {
        this.sftpServerController = sftpServerController;
    }

    public boolean runFtpServerAction() throws Exception {
        SshServer sshd = SshServer.setUpDefaultServer();
        // set the port of the listener
        sshd.setPort(Integer.parseInt(sftpServerController.getPortTextField().getText()));
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("/Users/xufeng/TestXf/sftp/key")));
        sshd.setPasswordAuthenticator((username, password, serverSession) -> {
            //假定用户名：usera 密码：apass，
            //这里可以增加逻辑从数据库或其他方式校验用户名和密码
            //返回true则校验成功否则失败
            if (sftpServerController.getAnonymousLoginEnabledCheckBox().isSelected()) {
                if ("anonymous".equals(username) && "".equals(password)) {
                    return true;
                }
            }
            for (SftpServerTableBean ftpServerTableBean : sftpServerController.getTableData()) {
                if (ftpServerTableBean.getIsEnabled()) {
                    if (ftpServerTableBean.getUserName().equals(username) && ftpServerTableBean.getPassword().equals(password)) {
                        return true;
                    }
                }
            }
            return false;
        });
        sshd.setFileSystemFactory(new VirtualFileSystemFactory(null) {
            @Override
            public Path getUserHomeDir(SessionContext session) throws IOException {
                String username = session.getUsername();
                Path homeDir = getUserHomeDir(username);
                if (homeDir == null) {
                    for (SftpServerTableBean ftpServerTableBean : sftpServerController.getTableData()) {
                        if (ftpServerTableBean.getIsEnabled()) {
                            if (ftpServerTableBean.getUserName().equals(username)) {
                                setUserHomeDir(username, Paths.get(ftpServerTableBean.getHomeDirectory()));
                            }
                        }
                    }
                }
                return homeDir;
            }
        });
        //设置sftp子系统
        sshd.setSubsystemFactories(Arrays.asList(new SftpSubsystemFactory()));
        sshd.setShellFactory(InteractiveProcessShellFactory.INSTANCE);
        // start the server
        sshd.start();
        server = sshd;
        return true;
    }

    public boolean stopFtpServerAction() throws Exception {
        if (server != null) {
            server.stop();
            server = null;
        }
        return true;
    }

    public void saveConfigure() throws Exception {
        saveConfigure(ConfigureUtil.getConfigureFile("sftpServerConfigure.json"));
    }

    public void saveConfigure(File file) throws Exception {
        ConfigureUtil.getConfig(file).clear();
        for (int i = 0; i < sftpServerController.getTableData().size(); i++) {
            ConfigureUtil.set(file, "tableBean" + i, sftpServerController.getTableData().get(i).getPropertys());
        }
        TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
    }

    public void otherSaveConfigureAction() throws Exception {
        String fileName = "sftpServerConfigure.properties";
        File file = FileChooserUtil.chooseSaveFile(fileName, new FileChooser.ExtensionFilter("All File", "*.*"),
            new FileChooser.ExtensionFilter("Properties", "*.json"));
        if (file != null) {
            saveConfigure(file);
            TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
        }
    }

    public void loadingConfigure() {
        loadingConfigure(ConfigureUtil.getConfigureFile("sftpServerConfigure.json"));
    }

    public void loadingConfigure(File file) {
        try {
            sftpServerController.getTableData().clear();
            Map xmlConfigure = ConfigureUtil.getConfig(file);
            for (Object key : xmlConfigure.keySet()) {
                sftpServerController.getTableData().add(new SftpServerTableBean((String) xmlConfigure.get(key)));
            }
        } catch (Exception e) {
            try {
                log.error("加载配置失败：" + e.getMessage());
                TooltipUtil.showToast("加载配置失败：" + e.getMessage());
            } catch (Exception e2) {
            }
        }
    }

    public void loadingConfigureAction() {
        File file = FileChooserUtil.chooseFile(new FileChooser.ExtensionFilter("All File", "*.*"),
            new FileChooser.ExtensionFilter("Properties", "*.json"));
        if (file != null) {
            loadingConfigure(file);
        }
    }
}
