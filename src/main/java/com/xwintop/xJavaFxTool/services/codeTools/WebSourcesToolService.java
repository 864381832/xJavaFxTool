package com.xwintop.xJavaFxTool.services.codeTools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.xwintop.xJavaFxTool.controller.codeTools.WebSourcesToolController;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @ClassName: WebSourcesToolService
 * @Description: 网页源码下载工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:27
 */

@Getter
@Setter
@Slf4j
public class WebSourcesToolService {
	private WebSourcesToolController webSourcesToolController;
	private final OkHttpClient client = new OkHttpClient();
	private String sourcesFilePathString = "D://TestXf//";
	private static Pattern srcPattern = Pattern.compile("src *= *['\"]*(\\S+)[\"']", Pattern.CASE_INSENSITIVE); // 不区分大小写
	private static Pattern hrefPattern = Pattern.compile("href *= *['\"]*(\\S+)[\"']", Pattern.CASE_INSENSITIVE); // 不区分大小写

	public WebSourcesToolService(WebSourcesToolController webSourcesToolController) {
		this.webSourcesToolController = webSourcesToolController;
	}

	public void downloadHtmlSources() throws Exception {
		OkHttpClient client = new OkHttpClient();
		String urlString = webSourcesToolController.getUrlTextField().getText().trim();
		Request request = new Request.Builder().url(urlString).build();
		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
		String indexHtml = response.body().string();
		new File(sourcesFilePathString).mkdirs();
		File indexHtmlFile = new File(sourcesFilePathString + "index.html");
		// FileUtils.touch(indexHtmlFile);
		FileUtils.writeStringToFile(indexHtmlFile, indexHtml, Charset.defaultCharset());

		// 用Pattern类的matcher()方法生成一个Matcher对象
		Matcher srcMatcher = srcPattern.matcher(indexHtml);
		while (srcMatcher.find()) {
			String str0 = srcMatcher.group();
			String str1 = srcMatcher.group(1); // 捕获的子序列
			saveSourcesFile(urlString,str1);
		}
		FileUtils.writeStringToFile(indexHtmlFile, indexHtml, Charset.defaultCharset());

		Matcher hrefMatcher = hrefPattern.matcher(indexHtml);
		while (hrefMatcher.find()) {
			String str1 = hrefMatcher.group(1); // 捕获的子序列
			saveSourcesFile(urlString,str1);
		}
	}

	private void saveSourcesFile(String url, String sourcesUrl) throws Exception {
		if(sourcesUrl.startsWith(url)){
			
		}else if(sourcesUrl.startsWith("http")){
			log.info(url);
		}else{
			String[] urlStrings = url.split("/");
			String[] sUrlStrings = sourcesUrl.split("\\.\\./");
			File sourcesIndexPathFile = new File(sourcesFilePathString);
			if(sUrlStrings.length > 1){
				for(int i=0;i<sUrlStrings.length-1;i++){
					sourcesIndexPathFile = sourcesIndexPathFile.getParentFile();
				}
				url = StringUtils.join(urlStrings,"/",0,urlStrings.length-sUrlStrings.length);
			}
			Request request = new Request.Builder().url(url+"/"+sUrlStrings[sUrlStrings.length-1]).build();
			Response response = client.newCall(request).execute();
			if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
			File sourcesFile = new File(sourcesIndexPathFile, "/" + sUrlStrings[sUrlStrings.length-1]);
			FileUtils.touch(sourcesFile);
			FileUtils.writeByteArrayToFile(sourcesFile, response.body().bytes());
		}
	}

}
