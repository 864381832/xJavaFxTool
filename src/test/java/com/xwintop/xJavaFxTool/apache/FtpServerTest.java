package com.xwintop.xJavaFxTool.apache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.junit.Test;

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
		
		BaseUser user = new BaseUser();  
	    user.setName("admin");  
	    user.setPassword("admin");  
	    user.setHomeDirectory("D:/TestXf");
	    
	    //添加用户权限
	    List<Authority> authorities = new ArrayList<Authority>();  
	    authorities.add(new WritePermission());  
	    user.setAuthorities(authorities);  
	    
	    serverFactory.getUserManager().save(user);
		
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
