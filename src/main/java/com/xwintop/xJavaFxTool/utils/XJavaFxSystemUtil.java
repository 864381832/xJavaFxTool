package com.xwintop.xJavaFxTool.utils;

import com.xwintop.xJavaFxTool.model.ToolFxmlLoaderConfiguration;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultElement;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/** 
 * @ClassName: XJavaFxSystemUtil 
 * @Description: javafx系统层工具类
 * @author: xufeng
 * @date: 2017年11月10日 下午4:35:17  
 */
public class XJavaFxSystemUtil {
	private static Logger log = Logger.getLogger(XJavaFxSystemUtil.class);

	/**
	 * @Title: initSystemLocal
	 * @Description: 初始化本地语言
	 */
	public static void initSystemLocal() {
		try {
			File file = ConfigureUtil.getConfigureFile("systemConfigure.properties");
			PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
			String[] locale1 = xmlConfigure.getString("Locale").split("_");
			if (locale1 != null) {
				Config.defaultLocale = new Locale(locale1[0], locale1[1]);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * @Title: loaderToolFxmlLoaderConfiguration
	 * @Description: 加载本地工具
	 */
	public static List<ToolFxmlLoaderConfiguration> loaderToolFxmlLoaderConfiguration() {
		List<ToolFxmlLoaderConfiguration> toolList = new ArrayList<ToolFxmlLoaderConfiguration>();
		try {
			XMLConfiguration xml = new XMLConfiguration("config/toolFxmlLoaderConfiguration.xml");
			for (ConfigurationNode configurationNode : xml.getRoot().getChildren("ToolFxmlLoaderConfiguration")) {
				ToolFxmlLoaderConfiguration toolFxmlLoaderConfiguration = new ToolFxmlLoaderConfiguration();
				List<ConfigurationNode> attributes = configurationNode.getAttributes();
				for (ConfigurationNode configuration : attributes) {
					BeanUtils.copyProperty(toolFxmlLoaderConfiguration, configuration.getName(),
							configuration.getValue());
				}
				List<ConfigurationNode> childrenList = configurationNode.getChildren();
				for (ConfigurationNode configuration : childrenList) {
					BeanUtils.copyProperty(toolFxmlLoaderConfiguration, configuration.getName(),
							configuration.getValue());
				}
				if(StringUtils.isEmpty(toolFxmlLoaderConfiguration.getMenuParentId())){
					toolFxmlLoaderConfiguration.setMenuParentId("toolsMenu");
				}
				toolList.add(toolFxmlLoaderConfiguration);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return toolList;
	}

	/**
	 * @Title: loaderToolFxmlLoaderConfiguration
	 * @Description: 加载libs下jar包中工具配置
	 */
	@SuppressWarnings({ "unchecked", "resource" })
	public static List<ToolFxmlLoaderConfiguration> loaderPlugInToolFxmlLoaderConfiguration() {
		List<ToolFxmlLoaderConfiguration> toolList = new ArrayList<ToolFxmlLoaderConfiguration>();
		try {
			File libPath = new File("libs/");
			// 获取所有的.jar和.zip文件
			File[] jarFiles = libPath.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar") || name.endsWith(".zip");
				}
			});
			for (File file : jarFiles) {
				JarFile jarFile = new JarFile(file);
				JarEntry entry = jarFile.getJarEntry("config/toolFxmlLoaderConfiguration.xml");
				InputStream input = jarFile.getInputStream(entry);
				SAXReader saxReader = new SAXReader();
				Document document = saxReader.read(input);
				Element root = document.getRootElement();
				List<Element> elements = root.elements("ToolFxmlLoaderConfiguration");
				for (Element configurationNode : elements) {
					ToolFxmlLoaderConfiguration toolFxmlLoaderConfiguration = new ToolFxmlLoaderConfiguration();
					List<DefaultAttribute> attributes = configurationNode.attributes();
					for (DefaultAttribute configuration : attributes) {
						BeanUtils.copyProperty(toolFxmlLoaderConfiguration, configuration.getName(),
								configuration.getValue());
					}
					List<DefaultElement> childrenList = configurationNode.elements();
					for (DefaultElement configuration : childrenList) {
						BeanUtils.copyProperty(toolFxmlLoaderConfiguration, configuration.getName(),
								configuration.getStringValue());
					}
					if(StringUtils.isEmpty(toolFxmlLoaderConfiguration.getMenuParentId())){
						toolFxmlLoaderConfiguration.setMenuParentId("moreToolsMenu");
					}
					toolList.add(toolFxmlLoaderConfiguration);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return toolList;
	}

	/**
	 * @Title: addJarByLibs
	 * @Description: 添加libs中jar包到系统中
	 */
	public static void addJarByLibs() {
		try {
			// 系统类库路径
			File libPath = new File("libs/");
			// 获取所有的.jar和.zip文件
			File[] jarFiles = libPath.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar") || name.endsWith(".zip");
				}
			});
			if (jarFiles != null) {
				// 从URLClassLoader类中获取类所在文件夹的方法
				// 对于jar文件，可以理解为一个存放class文件的文件夹
				Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
				boolean accessible = method.isAccessible(); // 获取方法的访问权限
				try {
					if (accessible == false) {
						method.setAccessible(true); // 设置方法的访问权限
					}
					// 获取系统类加载器
					URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
					for (File file : jarFiles) {
						URL url = file.toURI().toURL();
						try {
							method.invoke(classLoader, url);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} finally {
					method.setAccessible(accessible);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}
