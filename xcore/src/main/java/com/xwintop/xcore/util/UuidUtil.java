package com.xwintop.xcore.util;

import java.util.UUID;

/**
 * @ClassName: UuidUtil
 * @Description: Uuid工具类
 * @author: Administrator
 * @date: 2017年5月9日 下午9:44:52
 */
public class UuidUtil {
	public static String get32UUID() {
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}
}
