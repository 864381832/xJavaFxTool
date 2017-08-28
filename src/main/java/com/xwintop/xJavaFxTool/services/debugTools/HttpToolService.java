package com.xwintop.xJavaFxTool.services.debugTools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.xwintop.xJavaFxTool.controller.debugTools.HttpToolController;
import com.xwintop.xcore.util.HttpClientUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Getter
@Setter
@Log4j
public class HttpToolService {
	private HttpToolController httpToolController;

	/**
	 * @Title: sendAction
	 * @Description: 发送请求
	 */
	public void sendAction() {
		String url = httpToolController.getUrlTextField().getText().trim();
		if (StringUtils.isEmpty(url)) {
			TooltipUtil.showToast("请输入网站！！！");
			return;
		}
		Map<String, String> paramsMap = new HashMap<String, String>();
		Map<String, String> headerMap = new HashMap<String, String>();
		Map<String, String> cookieMap = new HashMap<String, String>();
		for (Map<String, String> map : httpToolController.getParamsDatatableData()) {
			paramsMap.put(map.get("name"), map.get("value"));
		}
		for (Map<String, String> map : httpToolController.getParamsHeadertableData()) {
			headerMap.put(map.get("name"), map.get("value"));
		}
		for (Map<String, String> map : httpToolController.getParamsCookietableData()) {
			cookieMap.put(map.get("name"), map.get("value"));
		}
		String methodString = httpToolController.getMethodChoiceBox().getValue();
		if ("GET".equals(methodString)) {
			HttpResponse httpResponse = HttpClientUtil.getHttpResponse(url, paramsMap, headerMap, cookieMap);
			String httpDataString = HttpClientUtil.getHttpDataByHttpResponse(httpResponse);
			httpToolController.getResponseBodyTextArea().setText(httpDataString);
			StringBuffer headerStringBuffer = new StringBuffer();
			for (Header header : httpResponse.getAllHeaders()) {
				headerStringBuffer.append(header.getName()).append(":").append(header.getValue()).append("\n");
			}
			httpToolController.getResponseHeaderTextArea().setText(headerStringBuffer.toString());
			// httpToolController.getResponseImgImageView().setImage(new
			// Image(url));
		} else if ("POST".equals(methodString)) {
			OkHttpClient okHttpClient = new OkHttpClient();
			Builder builder = new FormBody.Builder();
			for (Map<String, String> map : httpToolController.getParamsDatatableData()) {
				builder.add(map.get("name"), map.get("value"));
			}
			RequestBody body = builder.build();
			Request request = new Request.Builder().url(url).post(body).headers(Headers.of(headerMap)).build();
			Call call = okHttpClient.newCall(request);
			try {
				Response response = call.execute();
				StringBuffer headerStringBuffer = new StringBuffer();
				Headers headers = response.headers();
				for(int i=0;i<headers.size();i++){
					headerStringBuffer.append(headers.name(i)).append(":").append(headers.value(i)).append("\n");
				}
				httpToolController.getResponseHeaderTextArea().setText(headerStringBuffer.toString());
				httpToolController.getResponseBodyTextArea().setText(response.body().string());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		httpToolController.getResponseHtmlWebView().getEngine().load(url);
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
