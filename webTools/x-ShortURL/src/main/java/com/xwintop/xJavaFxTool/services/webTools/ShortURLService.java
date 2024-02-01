package com.xwintop.xJavaFxTool.services.webTools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xwintop.xJavaFxTool.controller.webTools.ShortURLController;
import com.xwintop.xcore.util.HttpClientUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

/**
 * @ClassName: ShortURLService
 * @Description: 网址缩短
 * @author: xufeng
 * @date: 2019/4/25 0025 23:36
 */

@Getter
@Setter
@Slf4j
public class ShortURLService {
    private ShortURLController shortURLController;

    public ShortURLService(ShortURLController shortURLController) {
        this.shortURLController = shortURLController;
    }

    /**
     * 百度转换为短网址.
     */
    public static String baiduToShort(String longURL, String alias) {
        String shortURL = "转换错误";
        try {
            String params = "{\"url\":\"" + longURL + "\"}";
            String url = "https://dwz.cn/admin/v2/create";
            String Token = "517cfcf7869ce43dec7b8e076f870652";
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), params);
            Request request = new Request.Builder().url(url).post(body).addHeader("Token", Token).build();
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            log.info("返回值：" + data);
            JSONObject jsonObject = JSON.parseObject(data);
            if (jsonObject.getIntValue("Code") == 0) {
                shortURL = jsonObject.getString("ShortUrl");
            } else {
                shortURL = jsonObject.getString("ErrMsg");
            }
        } catch (Exception e) {
            log.error("转换出错：", e);
        }
        return shortURL;
    }

    /**
     * 百度还原为长网址.
     */
    public static String baiduToLongURL(String shortURL) {
        String longURL = "转换错误";
        try {
            String params = "{\"shortUrl\":\"" + shortURL + "\"}";
            String url = "https://dwz.cn/admin/v2/query";
            String Token = "517cfcf7869ce43dec7b8e076f870652";
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), params);
            Request request = new Request.Builder().url(url).post(body).addHeader("Token", Token).build();
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            log.info("返回值：" + data);
            JSONObject jsonObject = JSON.parseObject(data);
            if (jsonObject.getIntValue("Code") == 0) {
                longURL = jsonObject.getString("LongUrl");
            } else {
                longURL = jsonObject.getString("ErrMsg");
            }
        } catch (Exception e) {
            log.error("转换出错：", e);
        }
        return longURL;
    }

    /**
     * 新浪转换为短网址.
     */
    public static String sinaToShort(String longURL, String site) {
        String shortURL = "转换错误";
        try {
            StringBuffer url = new StringBuffer("http://dwz.wailian.work/api.php?");
            url.append("url=").append(Base64.encodeBase64String(longURL.getBytes()));
            url.append("&site=").append(site);
            System.out.println(url.toString());
            String refererUrl = "http://dwz.wailian.work/";
            String data = HttpClientUtil.getHttpDataAsUTF_8(url.toString(), refererUrl);
            JSONObject jsonObject = JSON.parseObject(data);
            if ("ok".equals(jsonObject.getString("result"))) {
                shortURL = jsonObject.getJSONObject("data").getString("short_url");
            } else {
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
            StringBuffer url = new StringBuffer("http://dwz.wailian.work/api.php?");
            url.append("url=").append(Base64.encodeBase64String(longURL.getBytes()));
            url.append("&action=restore");
            System.out.println(url.toString());
            String refererUrl = "http://dwz.wailian.work/restore.php";
            String data = HttpClientUtil.getHttpDataAsUTF_8(url.toString(), refererUrl);
            JSONObject jsonObject = JSON.parseObject(data);
            if ("ok".equals(jsonObject.getString("result"))) {
                shortURL = jsonObject.getJSONObject("data").getString("short_url");
            } else {
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
            String url = "http://suo.im/api.php?format=json&url=" + longURL;
            String refererUrl = "http://www.suo.im/";
            String data = HttpClientUtil.getHttpDataAsUTF_8(url, refererUrl);
            JSONObject jsonObject = JSON.parseObject(data);
            if (StringUtils.isEmpty(jsonObject.getString("err"))) {
                shortURL = jsonObject.getString("url");
            } else {
                shortURL = jsonObject.getString("err");
            }
            System.out.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shortURL;
    }
}
