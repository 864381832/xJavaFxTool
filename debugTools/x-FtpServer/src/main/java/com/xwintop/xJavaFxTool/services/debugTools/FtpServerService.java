package com.xwintop.xJavaFxTool.services.debugTools;

import com.xwintop.xJavaFxTool.controller.debugTools.FtpServerController;
import com.xwintop.xJavaFxTool.model.FtpServerTableBean;
import com.xwintop.xcore.util.ConfigureUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.impl.DefaultConnectionConfig;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
public class FtpServerService {
	private FtpServerController ftpServerController;
	private FtpServer server = null;

	public FtpServerService(FtpServerController ftpServerController) {
		this.ftpServerController = ftpServerController;
	}

	public boolean runFtpServerAction() throws Exception {
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory factory = new ListenerFactory();
		// set the port of the listener
//		factory.setPort(21);
		factory.setPort(Integer.parseInt(ftpServerController.getPortTextField().getText()));
		// replace the default listener
		serverFactory.addListener("default", factory.createListener());

		ConnectionConfig connectionConfig = new DefaultConnectionConfig(ftpServerController.getAnonymousLoginEnabledCheckBox().isSelected() , 500, ftpServerController.getMaxConnectCountSpinner().getValue(), ftpServerController.getMaxConnectCountSpinner().getValue(), 3, 0);
		serverFactory.setConnectionConfig(connectionConfig);

		if(ftpServerController.getAnonymousLoginEnabledCheckBox().isSelected()){
			BaseUser user = new BaseUser();
			user.setName("anonymous");
			user.setHomeDirectory(ftpServerController.getAnonymousLoginEnabledTextField().getText());
			List<Authority> authorities = new ArrayList<Authority>();
			authorities.add(new WritePermission());// 添加用户读写权限
			user.setAuthorities(authorities);
			serverFactory.getUserManager().save(user);
		}

		for (FtpServerTableBean ftpServerTableBean : ftpServerController.getTableData()) {
			if(ftpServerTableBean.getIsEnabled()) {
				BaseUser user = new BaseUser();
				user.setName(ftpServerTableBean.getUserName());
				user.setPassword(ftpServerTableBean.getPassword());
				user.setHomeDirectory(ftpServerTableBean.getHomeDirectory());
//				user.setEnabled(ftpServerTableBean.getIsEnabled());
				List<Authority> authorities = new ArrayList<Authority>();
				authorities.add(new TransferRatePermission(ftpServerTableBean.getDownFIle() ? Integer.MAX_VALUE : 0, ftpServerTableBean.getUpFile() ? Integer.MAX_VALUE : 0));
				if (ftpServerTableBean.getDeleteFile()) {
					authorities.add(new WritePermission());// 添加用户读写权限
				}
				user.setAuthorities(authorities);
				serverFactory.getUserManager().save(user);
			}
		}
		
		server = serverFactory.createServer();
		// start the server
		server.start();
		return true;
	}

	public boolean stopFtpServerAction() {
		if (server != null) {
			server.stop();
			server = null;
		}
		return true;
	}

	public void saveConfigure() throws Exception {
		saveConfigure(ConfigureUtil.getConfigureFile("ftpServerConfigure.json"));
	}

	public void saveConfigure(File file) throws Exception {
        ConfigureUtil.getConfig(file).clear();
		for (int i = 0; i < ftpServerController.getTableData().size(); i++) {
            ConfigureUtil.set(file, "tableBean" + i, ftpServerController.getTableData().get(i).getPropertys());
		}
		TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
	}

	public void otherSaveConfigureAction() throws Exception {
		String fileName = "ftpServerConfigure.properties";
		File file = FileChooserUtil.chooseSaveFile(fileName, new FileChooser.ExtensionFilter("All File", "*.*"),
				new FileChooser.ExtensionFilter("Properties", "*.json"));
		if (file != null) {
			saveConfigure(file);
			TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
		}
	}

	public void loadingConfigure() {
		loadingConfigure(ConfigureUtil.getConfigureFile("ftpServerConfigure.json"));
	}

	public void loadingConfigure(File file) {
		try {
			ftpServerController.getTableData().clear();
            Map xmlConfigure = ConfigureUtil.getConfig(file);
            for (Object key : xmlConfigure.keySet()) {
                ftpServerController.getTableData().add(new FtpServerTableBean((String) xmlConfigure.get(key)));
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
