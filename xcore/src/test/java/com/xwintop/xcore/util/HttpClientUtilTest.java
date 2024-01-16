package com.xwintop.xcore.util;

import okhttp3.Headers;
import okhttp3.HttpUrl;

import java.nio.charset.StandardCharsets;

public class HttpClientUtilTest {

    public static void main(String[] args) throws Exception {

        String response = HttpClientUtil.executeGet(
            HttpUrl.parse("https://www.baidu.com"),
            Headers.of(
                "Referer", "https://www.google.com",
                "Accept", "application/json"
            ),
            StandardCharsets.UTF_8
        );

        System.out.println(response);
    }
}