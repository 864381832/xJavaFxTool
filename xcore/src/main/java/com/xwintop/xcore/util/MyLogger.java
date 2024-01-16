package com.xwintop.xcore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * @ClassName: MyLogger 
 * @Description: 日志记录工具
 * @author: Xufeng
 * @date: 2017年5月10日 下午9:22:38  
 */
public class MyLogger {
	private Logger LOGGER;

	public MyLogger(Class<?> c) {
		this.LOGGER = LoggerFactory.getLogger(c);
	}

	public void error(Exception message) {
		this.LOGGER.error(message.getMessage());
	}
	
	public void error(String message) {
		this.LOGGER.error(message);
	}

	public void error(String message, Throwable throwable) {
		this.LOGGER.error(message, throwable);
	}

	public void debug(String message) {
		this.LOGGER.debug(message);
	}

	public void info(String message) {
		this.LOGGER.info(message);
	}
}