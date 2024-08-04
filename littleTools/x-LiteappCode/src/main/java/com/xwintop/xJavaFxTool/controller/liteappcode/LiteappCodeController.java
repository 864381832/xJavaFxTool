package com.xwintop.xJavaFxTool.controller.liteappcode;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xwintop.xJavaFxTool.service.liteappcode.LiteappCodeService;
import com.xwintop.xJavaFxTool.view.liteappcode.LiteappCodeView;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class LiteappCodeController extends LiteappCodeView {
	private LiteappCodeService liteappCodeService = new LiteappCodeService(this);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
		initService();
	}

	private void initView() {

	}

	private void initEvent() {
	}

	private void initService() {
	}

	Map<String,String> accessTokens = new HashMap<>();

	public InputStream getImageStream(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                return inputStream;
            }
        } catch (IOException e) {
            System.out.println("获取网络图片出现异常，图片路径为：" + url);
            e.printStackTrace();
        }
        return null;
    }

	@FXML
	public void download() {

		 FileChooser fileChooser = new FileChooser();
	       // FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
	     //   fileChooser.getExtensionFilters().add(extFilter);
	        Stage s = new Stage();
	        File file = fileChooser.showSaveDialog(s);
	        if (file == null)
	            return;
	        if(file.exists()){//文件已存在，则删除覆盖文件
	            file.delete();
	        }
	        String exportFilePath = file.getAbsolutePath();
	        System.out.println("导出文件的路径" + exportFilePath);

	        BufferedImage bImage = SwingFXUtils.fromFXImage(imgQrCode.getImage(), null);
	        try {
	        	File outputFile = new File(exportFilePath);
	        	ImageIO.write(bImage, "png", outputFile);
	        } catch (IOException e) {
	        	 TooltipUtil.showToast("到处失败:"+e.getMessage());
	        }
	        //FileWriteUtil.WriteDocument(exportFilePath, sBuilder.toString());
	        //AlertUtil.showConfirmAlert("导出成功!保存路径:\n"+exportFilePath);
	        TooltipUtil.showToast("导出成功!保存路径:\n"+exportFilePath);
	}

	static InputStream in = null;

	@FXML
	public void genCode() {
		String appid = txtAppid.getText();
		String page = txtPath.getText();
		String secret = txtAppSecret.getText();
		String scence = txtScene.getText();
		if(StrUtil.isEmpty(appid) ||StrUtil.isEmpty(secret) ||StrUtil.isEmpty(page) ||StrUtil.isEmpty(scence)) {
			AlertUtil.showConfirmAlert("路径、appid、秘钥,scene都是必填参数");
			return;
		}
		Map<String, Object> requestParam = new HashMap<String,Object>();
		requestParam.put("grant_type", "client_credential");
		requestParam.put("appid", appid);
		requestParam.put("secret", secret);

		String cacheKey = appid+"_"+secret;
		String accessToken = "";
		if(accessTokens.containsKey(cacheKey) && StrUtil.isNotBlank( accessTokens.get(cacheKey))) {
			accessToken = accessTokens.get(cacheKey);
			System.out.println("从缓存中获取"+cacheKey+" accessToken:"+accessToken);
		}else {
			String resp = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token", requestParam);

			JSONObject jsonObject = JSON.parseObject(resp);
			if(!jsonObject.containsKey("access_token")) {
				AlertUtil.showConfirmAlert("获取access_token失败");
				return;
			}
			 accessToken = jsonObject.getString("access_token");
			 accessTokens.put(cacheKey,accessToken);
		}
		// sendPost 为一个请求各种接口而封装的函数，并转换JSON字符串为JSONObject


		 String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+accessToken;

		 Map<String, Object> param = new HashMap<>();
         param.put("page", page); //跳转到查询物流(自定义)
         param.put("auto_color", true);
         param.put("is_hyaline", chkIsHyaline.isSelected());
         String widthStr = txtWidth.getText();
         if(NumberUtil.isInteger(widthStr)) {
        	 param.put("width", Integer.parseInt(widthStr));
         }else {
        	 param.put("width", 430);
         }

    	 if(!StrUtil.isEmpty(scence)) {
			 param.put("scene",scence);
		 }
         Map<String, Object> line_color = new HashMap<>();
         Color  c = pickColor.getValue();

         line_color.put("r", c.getRed());
         line_color.put("g", c.getGreen());
         line_color.put("b", c.getBlue());
         System.out.println("调用生成微信URL接口传参:url"+url+" \r\n param:" + param);
         String codeRes = "";
		try {
			InputStream in = null;
			//{"errcode":41030,"errmsg":"invalid page hint: [AHNvcA08331090]"}
			Object res = send(url, JSONObject.toJSONString(param));
			if(res instanceof String) {
				JSONObject jo =  JSONObject.parseObject(res.toString());
				if(jo.containsKey("errcode") && StrUtil.isNotBlank(jo.getString("errcode"))) {
					int errorCode = jo.getIntValue("errcode");
					switch(errorCode) {
						case 41030:
							AlertUtil.showConfirmAlert("所传page页面不存在，或者小程序没有发布");
							break;
						case 45009:
							AlertUtil.showConfirmAlert("调用分钟频率受限(目前5000次/分钟，会调整)，如需大量小程序码，建议预生成。");
							break;
						default :
							AlertUtil.showConfirmAlert(jo.getString("errmsg"));
					}
				}
			}else{
				in = null;
				in = (InputStream)res;
				Image img = new Image(in);
				imgQrCode.setImage(img);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AlertUtil.showConfirmAlert(e.getMessage());
		}
         System.out.println(codeRes);
         //AlertUtil.showConfirmAlert(access_token);
	}

	private Object send(String urlPath,String param) throws Exception {
		URL url=new URL(urlPath);
        HttpURLConnection httpConn=(HttpURLConnection)url.openConnection();

        //设置参数
        httpConn.setDoOutput(true);     //需要输出
        httpConn.setDoInput(true);      //需要输入
        httpConn.setUseCaches(false);   //不允许缓存
        httpConn.setRequestMethod("POST");      //设置POST方式连接

        //设置请求属性
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
        httpConn.setRequestProperty("Charset", "UTF-8");

        //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
        httpConn.connect();

        //建立输入流，向指向的URL传入参数
        DataOutputStream dos=new DataOutputStream(httpConn.getOutputStream());
        dos.writeBytes(param);
        dos.flush();
        dos.close();

        //获得响应状态
        int resultCode=httpConn.getResponseCode();
        if(HttpURLConnection.HTTP_OK==resultCode){
        	InputStream in1  = httpConn.getInputStream();
        	long size = httpConn.getContentLengthLong();
        	if(size<500) {
        	  BufferedReader in = new BufferedReader(new InputStreamReader(in1, "utf-8"));

              String temp = null;
              StringBuffer sb = new StringBuffer();
              while ((temp = in.readLine()) != null) {
                  sb.append(temp);
              }

              in.close();
              return sb.toString();
        	}else {
        		return in1;
        	}
        } else {
        	return null;
        }
	}

	public static byte[] toByteArray(InputStream input) throws IOException {
		 ByteArrayOutputStream output = new ByteArrayOutputStream();
		 byte[] buffer = new byte[1024*4];
		 int n = 0;
		 while (-1 != (n = input.read(buffer))) {
		  output.write(buffer, 0, n);
		}
		 	return output.toByteArray();
		}
}