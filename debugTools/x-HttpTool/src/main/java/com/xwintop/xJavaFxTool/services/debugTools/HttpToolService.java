package com.xwintop.xJavaFxTool.services.debugTools;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.xwintop.xJavaFxTool.controller.debugTools.HttpToolController;
import com.xwintop.xcore.util.HttpClientUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @ClassName: HttpToolService
 * @Description: Http调试工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:29
 */

@Getter
@Setter
@Slf4j
public class HttpToolService {
    private HttpToolController httpToolController;
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");

    /**
     * @Title: sendAction
     * @Description: 发送请求
     */
    public void sendAction() throws Exception {
        String url = httpToolController.getUrlTextField().getText().trim();
        if (StringUtils.isEmpty(url)) {
            TooltipUtil.showToast("请输入网站！！！");
            return;
        }
        Map<String, String> paramsMap = new HashMap<String, String>();
        Map<String, String> headerMap = new HashMap<String, String>();
        Map<String, String> cookieMap = new HashMap<String, String>();
        if (httpToolController.getParamsDataCheckBox().isSelected()) {
            for (Map<String, String> map : httpToolController.getParamsDatatableData()) {
                if (StringUtils.isNotEmpty(map.get("name")) && StringUtils.isNotEmpty(map.get("value"))) {
                    paramsMap.put(map.get("name"), map.get("value"));
                }
            }
        }
        if (httpToolController.getParamsHeaderCheckBox().isSelected()) {
            for (Map<String, String> map : httpToolController.getParamsHeadertableData()) {
                if (StringUtils.isNotEmpty(map.get("name")) && StringUtils.isNotEmpty(map.get("value"))) {
                    headerMap.put(map.get("name"), map.get("value"));
                }
            }
        }
        if (httpToolController.getParamsCookieCheckBox().isSelected() && MapUtils.isNotEmpty(cookieMap)) {
            StringBuffer paramsCookieBuffer = new StringBuffer();
            for (Map<String, String> map : httpToolController.getParamsCookietableData()) {
                if (StringUtils.isNotEmpty(map.get("name")) && StringUtils.isNotEmpty(map.get("value"))) {
                    cookieMap.put(map.get("name"), map.get("value"));
                    paramsCookieBuffer.append(map.get("name")).append("=").append(map.get("value")).append(";");
                }
            }
            paramsCookieBuffer.deleteCharAt(paramsCookieBuffer.length() - 1);
            headerMap.put("Cookie", paramsCookieBuffer.toString());
        }
        String methodString = httpToolController.getMethodChoiceBox().getValue();
        OkHttpClient client = new OkHttpClient();
        Request request = null;
        if ("GET".equals(methodString)) {
            StringBuffer paramsDataBuffer = new StringBuffer();
            if (MapUtils.isNotEmpty(paramsMap)) {
                if (url.contains("?")) {
                    paramsDataBuffer.append("&");
                } else {
                    paramsDataBuffer.append("?");
                }
                paramsMap.forEach(new BiConsumer<String, String>() {
                    @Override
                    public void accept(String key, String value) {
                        paramsDataBuffer.append(key).append("=").append(value).append("&");
                    }
                });
                paramsDataBuffer.deleteCharAt(paramsDataBuffer.length() - 1);
            }
            url += paramsDataBuffer.toString();
            request = new Request.Builder().url(url).headers(Headers.of(headerMap)).build();
        } else {
            Builder builder = new FormBody.Builder();
            for (Map<String, String> map : httpToolController.getParamsDatatableData()) {
                builder.add(map.get("name"), map.get("value"));
            }
            RequestBody body = builder.build();
            if ("POST".equals(methodString)) {
                if (httpToolController.getParamsDataIsStringCheckBox().isSelected()) {
                    if (httpToolController.getParamsDataCheckBox().isSelected()) {
                        RequestBody rbody = RequestBody.create(MEDIA_TYPE_MARKDOWN, httpToolController.getParamsDataTextArea().getText());
                        request = new Request.Builder().url(url).post(rbody).build();
                    } else {
                        request = new Request.Builder().url(url).post(body).build();
                    }
                } else {
                    request = new Request.Builder().url(url).post(body).headers(Headers.of(headerMap)).build();
                }
            } else if ("HEAD".equals(methodString)) {
                request = new Request.Builder().url(url).head().headers(Headers.of(headerMap)).build();
            } else if ("PUT".equals(methodString)) {
                request = new Request.Builder().url(url).put(body).headers(Headers.of(headerMap)).build();
            } else if ("PATCH".equals(methodString)) {
                request = new Request.Builder().url(url).patch(body).headers(Headers.of(headerMap)).build();
            } else if ("DELETE".equals(methodString)) {
                request = new Request.Builder().url(url).delete(body).headers(Headers.of(headerMap)).build();
            }
        }
        Response response = client.newCall(request).execute();
        Headers headers = response.headers();
        StringBuffer headerStringBuffer = new StringBuffer();
        for (int i = 0; i < headers.size(); i++) {
            headerStringBuffer.append(headers.name(i)).append(":").append(headers.value(i)).append("\n");
        }
        httpToolController.getResponseHeaderTextArea().setText(headerStringBuffer.toString());
        httpToolController.getResponseBodyTextArea().setText(response.body().string());
        httpToolController.getResponseHtmlWebView().getEngine().load(url);
//		httpToolController.getResponseImgImageView().setImage(new Image(url));
    }

    /**
     * @Title: toBrowerAction
     * @Description: 从浏览器中打开
     */
    public void toBrowerAction() {
        if (StringUtils.isEmpty(httpToolController.getUrlTextField().getText())) {
            TooltipUtil.showToast("请输入网站！！！");
            return;
        }
        try {
            HttpClientUtil.openBrowseURLThrowsException(httpToolController.getUrlTextField().getText());
        } catch (Exception e1) {
            TooltipUtil.showToast("输入Url有误！" + e1.getMessage());
            log.error(e1.getMessage());
        }
    }

    public HttpToolService(HttpToolController httpToolController) {
        this.httpToolController = httpToolController;
    }

}
