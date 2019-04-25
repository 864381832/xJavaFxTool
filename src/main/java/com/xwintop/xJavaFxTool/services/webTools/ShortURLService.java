package com.xwintop.xJavaFxTool.services.webTools;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xwintop.xcore.util.HttpClientUtil;

/**
 * @ClassName: ShortURLService
 * @Description: 网址缩短
 * @author: xufeng
 * @date: 2019/4/25 0025 23:36
 */

public class ShortURLService {
	/**
	 * 百度转换为短网址.
	 */
	public static String baiduToShort(String longURL,String alias) {
		String shortURL = "转换错误";
		try {
			String url= "http://dwz.cn/create.php";
			String refererUrl = "http://dwz.cn/";
			Map<String, String> postMethod = new HashMap<String, String>();
			postMethod.put("url", longURL);
			postMethod.put("alias", alias);
			postMethod.put("access_type", "web");
			String data = HttpClientUtil.getHttpDataByPost(url, refererUrl, postMethod);
			JSONObject jsonObject = JSON.parseObject(data);
			if(jsonObject.getIntValue("status") == 0) {
				shortURL = jsonObject.getString("tinyurl");
			}else {
				shortURL = jsonObject.getString("err_msg");
			}
			System.out.println(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shortURL;
	}
	/**
	 * 百度还原为长网址.
	 */
	public static String baiduToLongURL(String shortURL) {
		String longURL = "转换错误";
		try {
			String url = "http://dwz.cn/query.php";
			String refererUrl = "http://dwz.cn/";
			Map<String, String> postMethod = new HashMap<String, String>();
			postMethod.put("tinyurl", shortURL);
			postMethod.put("access_type", "web");
			String data = HttpClientUtil.getHttpDataByPost(url, refererUrl, postMethod);
			JSONObject jsonObject = JSON.parseObject(data);
			if(jsonObject.getIntValue("status") == 0) {
				longURL = jsonObject.getString("longurl");
			}else {
				longURL = jsonObject.getString("err_msg");
			}
			System.out.println(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return longURL;
	}
	
	/**
	 * 新浪转换为短网址.
	 */
	public static String sinaToShort(String longURL,String site) {
		String shortURL = "转换错误";
		try {
			StringBuffer url= new StringBuffer("http://dwz.wailian.work/api.php?");
			url.append("url=").append(Base64.encodeBase64String(longURL.getBytes()));
			url.append("&site=").append(site);
			System.out.println(url.toString());
			String refererUrl = "http://dwz.wailian.work/";
			String data = HttpClientUtil.getHttpDataAsUTF_8(url.toString(), refererUrl);
			JSONObject jsonObject = JSON.parseObject(data);
			if("ok".equals(jsonObject.getString("result"))) {
				shortURL = jsonObject.getJSONObject("data").getString("short_url");
			}else {
				shortURL = jsonObject.getString("data");
			}
			System.out.println(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shortURL;
	}
	
	/**
	 * 新浪短网址还原.
	 */
	public static String sinaToLongURL(String longURL) {
		String shortURL = "转换错误";
		try {
			StringBuffer url= new StringBuffer("http://dwz.wailian.work/api.php?");
			url.append("url=").append(Base64.encodeBase64String(longURL.getBytes()));
			url.append("&action=restore");
			System.out.println(url.toString());
			String refererUrl = "http://dwz.wailian.work/restore.php";
			String data = HttpClientUtil.getHttpDataAsUTF_8(url.toString(), refererUrl);
			JSONObject jsonObject = JSON.parseObject(data);
			if("ok".equals(jsonObject.getString("result"))) {
				shortURL = jsonObject.getJSONObject("data").getString("short_url");
			}else {
				shortURL = jsonObject.getString("data");
			}
			System.out.println(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shortURL;
	}
	
	/**
	 * 缩我转换为短网址http://www.suo.im
	 */
	public static String suoImToShort(String longURL) {
		String shortURL = "转换错误";
		try {
			String url= "http://suo.im/api.php?format=json&url="+longURL;
			String refererUrl = "http://www.suo.im/";
			String data = HttpClientUtil.getHttpDataAsUTF_8(url, refererUrl);
			JSONObject jsonObject = JSON.parseObject(data);
			if(StringUtils.isEmpty(jsonObject.getString("err"))) {
				shortURL = jsonObject.getString("url");
			}else {
				shortURL = jsonObject.getString("err");
			}
			System.out.println(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shortURL;
	}
}
