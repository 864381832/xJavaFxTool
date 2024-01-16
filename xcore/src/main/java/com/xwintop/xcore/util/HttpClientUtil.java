package com.xwintop.xcore.util;

import okhttp3.*;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 使用方法参见单元测试
 */
public class HttpClientUtil {

    public static final Duration DEFAULT_CALL_TIMEOUT = Duration.ofMinutes(2);

    public static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofMinutes(2);

    public static final Duration DEFAULT_READ_TIMEOUT = Duration.ofMinutes(2);

    public static final Duration DEFAULT_WRITE_TIMEOUT = Duration.ofMinutes(2);

    /**
     * 一个应用当中只需要创建一个 OkHttpClient 对象
     */
    private static OkHttpClient okHttpClient = null;

    static {
        reinitialize(
            DEFAULT_CONNECT_TIMEOUT,
            DEFAULT_READ_TIMEOUT,
            DEFAULT_WRITE_TIMEOUT,
            DEFAULT_CALL_TIMEOUT
        );
    }

    /**
     * 重新初始化 OkHttpClient
     */
    public static void reinitialize(
        Duration connectTimeout, Duration readTimeout,
        Duration writeTimeout, Duration callTimeout
    ) {
        HttpClientUtil.okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(connectTimeout)
            .readTimeout(readTimeout)
            .writeTimeout(writeTimeout)
            .callTimeout(callTimeout)
            .build();
    }

    public static String executeGet(
        HttpUrl url, Headers headers, Charset charset) throws IOException {
        return execute(url, headers, charset, Request.Builder::get);
    }

    public static String executePost(
        HttpUrl url, Headers headers, Charset charset, RequestBody body) throws IOException {
        return execute(url, headers, charset, builder -> builder.post(body));
    }

    public static String execute(
        HttpUrl url, Headers headers, Charset charset, Consumer<Request.Builder> builderConsumer
    ) throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(url).headers(headers);
        builderConsumer.accept(requestBuilder);
        Request request = requestBuilder.build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() != null) {
                return new String(response.body().bytes(), charset);
            } else {
                return null;
            }
        }
    }

    ///////////////////////////////////////////////////////////////
    // 下面标记为 @Deprecated 的方法都存在设计或使用方式上的问题，不建议使用

    @Deprecated
    public static String getHttpDataAsUTF_8(String url, String refererUrl) {
        try {
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Referer", refererUrl);
            Request request = new Request.Builder().url(url).headers(Headers.of(headerMap)).build();
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Deprecated
    public static String getHttpDataByPost(String url, String refererUrl, Map<String, String> map) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            map.forEach((String key, String value) -> {
                if (value != null) {
                    builder.add(key, value);
                }
            });
            RequestBody body = builder.build();
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Referer", refererUrl);
            Request request = new Request.Builder().url(url).post(body).headers(Headers.of(headerMap)).build();
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Deprecated
    public static String getHttpDataByPost(String url, String refererUrl, String string) {
        return getHttpDataByPost(url, refererUrl, string, "text/x-markdown; charset=utf-8");
    }

    @Deprecated
    public static String getHttpDataByPost(String url, String refererUrl, String string, String header) {
        try {
            RequestBody body = RequestBody.create(MediaType.parse(header), string);
            Map<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Referer", refererUrl);
            Request request = new Request.Builder().url(url).post(body).headers(Headers.of(headerMap)).build();
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void openBrowseURL(String url) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(url));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void openBrowseURLThrowsException(String url) throws IOException, URISyntaxException {
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(new URI(url));
    }
}
