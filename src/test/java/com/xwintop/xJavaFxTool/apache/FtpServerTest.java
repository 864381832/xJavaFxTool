package com.xwintop.xJavaFxTool.apache;

import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.impl.DefaultConnectionConfig;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FtpServerTest {
	
	@Test
//	public static void main(String[] args) throws FtpException {
	public void testStart() throws Exception {
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory factory = new ListenerFactory();
		// set the port of the listener
		factory.setPort(21);
		// replace the default listener
		serverFactory.addListener("default", factory.createListener());

		ConnectionConfig connectionConfig = new DefaultConnectionConfig(false , 500, 10, 10, 3, 0);
		serverFactory.setConnectionConfig(connectionConfig);

		BaseUser user = new BaseUser();
	    user.setName("admin1");
	    user.setPassword("admin");  
	    user.setHomeDirectory("D:/TestXf");

		BaseUser user2 = new BaseUser();
		user2.setName("admin2");
		user2.setPassword("admin");
		user2.setHomeDirectory("D:/TestXf");
		user2.setEnabled(false);

		BaseUser user3 = new BaseUser();
		user3.setName("admin3");
		user3.setPassword("admin");
		user3.setHomeDirectory("D:/TestXf");

		BaseUser user4 = new BaseUser();
		user4.setName("anonymous");
		user4.setHomeDirectory("D:/TestXf");

		//添加用户权限
	    List<Authority> authorities = new ArrayList<Authority>();
//		authorities.add(new WritePermission());
	    authorities.add(new TransferRatePermission(0,0));
	    authorities.add(new ConcurrentLoginPermission(0,Integer.MAX_VALUE));
	    user.setAuthorities(authorities);
	    
	    serverFactory.getUserManager().save(user);
//	    serverFactory.getUserManager().save(user2);
//	    serverFactory.getUserManager().save(user3);
	    serverFactory.getUserManager().save(user4);

		FtpServer server = serverFactory.createServer(); 
		// start the server
		server.start();
		System.in.read();//系统等待输入
	}
	
	@Test
	public void mainTest() throws FtpException {
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory factory = new ListenerFactory();
		// set the port of the listener
		factory.setPort(2221);
		// define SSL configuration
		SslConfigurationFactory ssl = new SslConfigurationFactory();
		ssl.setKeystoreFile(new File("src/test/resources/ftpserver.jks"));
		ssl.setKeystorePassword("password");
		// set the SSL configuration for the listener
		factory.setSslConfiguration(ssl.createSslConfiguration());
		factory.setImplicitSsl(true);
		// replace the default listener
		serverFactory.addListener("default", factory.createListener());
		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		userManagerFactory.setFile(new File("myusers.properties"));
		serverFactory.setUserManager(userManagerFactory.createUserManager());
		// start the server
		FtpServer server = serverFactory.createServer(); 
		server.start();
	}
}
