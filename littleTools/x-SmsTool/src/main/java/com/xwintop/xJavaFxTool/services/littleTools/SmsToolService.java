package com.xwintop.xJavaFxTool.services.littleTools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xwintop.xJavaFxTool.controller.littleTools.SmsToolController;
import com.xwintop.xJavaFxTool.model.SmsToolTableBean;
import com.xwintop.xcore.util.HttpClientUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.util.*;

/**
 * @ClassName: SmsToolService
 * @Description: 短信群发工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:35
 */

@Getter
@Setter
@Slf4j
public class SmsToolService {
    private SmsToolController smsToolController;

    public void runAllAction() {
        ArrayList<SmsToolTableBean> smsToolTableBeanArrayList = new ArrayList<SmsToolTableBean>();
        for (SmsToolTableBean smsToolTableBean : smsToolController.getTableData()) {
            if (smsToolTableBean.getIsEnabled()) {
                smsToolTableBeanArrayList.add(smsToolTableBean);
            }
        }
        if (!smsToolTableBeanArrayList.isEmpty()) {
            runAction(smsToolTableBeanArrayList.toArray(new SmsToolTableBean[0]));
        } else {
            TooltipUtil.showToast("未选择联系人！！！");
        }
    }

    public void runAction(SmsToolTableBean... smsToolTableBeans) {
        int selectedIndex = smsToolController.getMainTabPane().getSelectionModel().getSelectedIndex();
        switch (selectedIndex) {
            case 0://中国移动
                cmccSendAction(smsToolTableBeans);
                break;
            case 1://中国电信
                open189SendAction(smsToolTableBeans);
                break;
            case 2://腾讯云
                tencentSendAction(smsToolTableBeans);
                break;
            case 3://阿里云
                aliyunSendAction(smsToolTableBeans);
                break;
            case 4://梦网云通讯
                monyunSendAction(smsToolTableBeans);
                break;
            default:
                break;
        }
    }

    /**
     * 中国移动短信发送
     */
    public void cmccSendAction(SmsToolTableBean... smsToolTableBeans) {
        String url = smsToolController.getCmccUrlTextField().getText();
        String apiKey = smsToolController.getCmccApiKeyTextField().getText();
        String secretKey = smsToolController.getCmccSecretKeyTextField().getText();
        String needReceipt = smsToolController.getCmccNeedReceiptCheckBox().isSelected() ? "1" : "0";//是否回执 1：回执0：不回执
        String message = smsToolController.getCmccMessageTextArea().getText();
        String receiptNotificationURL = smsToolController.getCmccReceiptNotificationUrlTextField().getText();//回调地址
        for (SmsToolTableBean smsToolTableBean : smsToolTableBeans) {
//            String destAddr = "18356971618";//目标地址
            String destAddr = smsToolTableBean.getToPhone();//目标地址
            Map<String, String> map = new HashMap<>();
            map.put("destAddr", destAddr);
            map.put("apiKey", apiKey);
            map.put("secretKey", secretKey);
            map.put("needReceipt", needReceipt);
            map.put("message", message);
            if ("1".equals(needReceipt)) {
                map.put("receiptNotificationURL", receiptNotificationURL);
            }
            String resJson = HttpClientUtil.getHttpDataByPost(url, "http://www.openservice.com.cn", JSON.toJSONString(map));
            log.info("中国移动短信发送返回：" + resJson);
            if ("200".equals(JSON.parseObject(resJson).getString("resultCode"))) {
                TooltipUtil.showToast("中国移动发送短信成功！！！");
            } else {
                TooltipUtil.showToast("中国移动发送短信失败：" + resJson);
            }
        }
    }

    /**
     * 中国电信短信发送
     */
    public void open189SendAction(SmsToolTableBean... smsToolTableBeans) {
        String url = smsToolController.getOpen189UrlTextField().getText();
        String app_id = smsToolController.getOpen189AppIdTextField().getText();
        String app_secret = smsToolController.getOpen189AppSecretTextField().getText();
        String template_id = smsToolController.getOpen189TemplateIdTextField().getText();//短信模板ID
        String template_param = smsToolController.getOpen189TemplateParamTextArea().getText();//模板匹配参数,参数格式为(json对象字符串): {参数名：参数值，参数名：参数值}

        for (SmsToolTableBean smsToolTableBean : smsToolTableBeans) {
//            String acceptor_tel = "18356971618";//接收方号码
            String acceptor_tel = smsToolTableBean.getToPhone();//接收方号码
            String access_token = "";
            Map<String, String> map = new HashMap<>();
            map.put("app_id", app_id);
            map.put("app_secret", app_secret);
            map.put("grant_type", "client_credentials");
            String resJson = HttpClientUtil.getHttpDataByPost("https://oauth.api.189.cn/emp/oauth2/v3/access_token", "https://oauth.api.189.cn", map);
            log.info("中国电信短信获取Token返回：" + resJson);
            access_token = JSON.parseObject(resJson).getString("access_token");

            String timestamp = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");//时间戳
            TreeMap<String, String> map2 = new TreeMap<>();
            map2.put("app_id", app_id);
            map2.put("access_token", access_token);
            map2.put("acceptor_tel", acceptor_tel);
            map2.put("template_id", template_id);
            map2.put("template_param", template_param);
            map2.put("timestamp", timestamp);
            try {
                resJson = HttpClientUtil.getHttpDataByPost(url, "http://api.189.cn", map2);
                log.info("中国电信短信发送返回：" + resJson);
                String res_message = JSON.parseObject(resJson).getString("res_message");
                if ("Success".equals(res_message)) {
                    TooltipUtil.showToast("中国电信发送短信成功！！！");
                } else {
                    TooltipUtil.showToast("中国电信发送短信失败：" + resJson);
                }
            } catch (Exception e) {
                TooltipUtil.showToast("中国电信发送短信失败：" + e.getMessage());
                log.info("电信短信发送失败：" + e.getMessage());
            }
        }
    }

    /**
     * 腾讯云短信发送
     */
    public void tencentSendAction(SmsToolTableBean... smsToolTableBeans) {
        String url = "https://yun.tim.qq.com/v5/tlssmssvr/sendmultisms2";
        int appid = Integer.parseInt(smsToolController.getTencentAppidTextField().getText());
        String appkey = smsToolController.getTencentAppkeyTextField().getText();
        boolean isParam = smsToolController.getTencentIsParamCheckBox().isSelected();
        String msg = smsToolController.getTencentMsgTextField().getText();
        String params = smsToolController.getTencentParamsTextArea().getText();
        ArrayList<String> phoneNumbers = new ArrayList<String>();
//        phoneNumbers.add("18356971618");
        for (SmsToolTableBean smsToolTableBean : smsToolTableBeans) {
            phoneNumbers.add(smsToolTableBean.getToPhone());
        }
        try {
            long random = RandomUtils.nextInt(0, 999999) + 100000;
            long curTime = System.currentTimeMillis() / 1000;

            Map data = new HashMap();
            ArrayList tel = new ArrayList();
            int i = 0;
            do {
                Map telElement = new HashMap();
                telElement.put("nationcode", "86");
                telElement.put("mobile", phoneNumbers.get(i));
                tel.add(telElement);
            } while (++i < phoneNumbers.size());
            data.put("tel", tel);
            data.put("sign", "");
            if (isParam) {
                data.put("tpl_id", msg);
                data.put("params", JSON.parse(params));
            } else {
                data.put("msg", msg);
            }
            String sigStr = String.format(
                    "appkey=%s&random=%d&time=%d&mobile=%s",
                    appkey, random, curTime, StringUtils.join(phoneNumbers, ","));
            data.put("sig", DigestUtils.sha256Hex(sigStr));
            data.put("time", curTime);
            data.put("extend", "");
            data.put("ext", "");

            String wholeUrl = String.format("%s?sdkappid=%d&random=%d", url, appid, random);
            String resultStr = HttpClientUtil.getHttpDataByPost(wholeUrl, url, JSON.toJSONString(data));
            log.info("腾讯云短信发送返回：" + resultStr);
            JSONObject jsonObject = JSON.parseObject(resultStr);
            if (jsonObject.getInteger("result") == 0) {
                TooltipUtil.showToast("腾讯云发送短信成功！！！");
            } else {
                TooltipUtil.showToast("腾讯云发送短信失败：" + resultStr);
            }
        } catch (Exception e) {
            log.error("腾讯云发送短信失败：", e);
            TooltipUtil.showToast("腾讯云发送短信失败：" + e.getMessage());
        }
    }

    /**
     * 阿里云短信发送
     */
    public void aliyunSendAction(SmsToolTableBean... smsToolTableBeans) {
        try {
            ArrayList<String> phoneNumbers = new ArrayList<String>();
            for (SmsToolTableBean smsToolTableBean : smsToolTableBeans) {
                phoneNumbers.add(smsToolTableBean.getToPhone());
            }
            String accessKeyId = smsToolController.getAliyunAccessKeyIdTextField().getText();
            String accessKeySecret = smsToolController.getAliyunAccessKeySecretTextField().getText();
            String signName = smsToolController.getAliyunSignNameTextField().getText();
            String templateCode = smsToolController.getAliyunTemplateCodeTextField().getText();
            String templateParam = smsToolController.getAliyunTemplateParamTextArea().getText();

            java.util.Map<String, String> paras = new java.util.HashMap<String, String>();
            // 1. 系统参数
            paras.put("SignatureMethod", "HMAC-SHA1");
            paras.put("SignatureNonce", java.util.UUID.randomUUID().toString());
            paras.put("AccessKeyId", accessKeyId);
            paras.put("SignatureVersion", "1.0");
            paras.put("Timestamp", DateFormatUtils.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss'Z'", new SimpleTimeZone(0, "GMT")));
            paras.put("Format", "JSON");
            // 2. 业务API参数
            paras.put("Action", "SendSms");
            paras.put("Version", "2017-05-25");
            paras.put("RegionId", "cn-hangzhou");
            paras.put("PhoneNumbers", StringUtils.join(phoneNumbers, ","));
            //必填:短信签名-可在短信控制台中找到
            paras.put("SignName", signName);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时
            paras.put("TemplateParam", templateParam);
            //必填:短信模板-可在短信控制台中找到
            paras.put("TemplateCode", templateCode);
            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//        paras.put("OutId", "123");
            // 4. 参数KEY排序
            java.util.TreeMap<String, String> sortParas = new java.util.TreeMap<String, String>();
            sortParas.putAll(paras);
            // 5. 构造待签名的字符串
            java.util.Iterator<String> it = sortParas.keySet().iterator();
            StringBuilder sortQueryStringTmp = new StringBuilder();
            while (it.hasNext()) {
                String key = it.next();
                sortQueryStringTmp.append("&").append(specialUrlEncode(key)).append("=").append(specialUrlEncode(paras.get(key)));
            }
            String sortedQueryString = sortQueryStringTmp.substring(1);// 去除第一个多余的&符号
            StringBuilder stringToSign = new StringBuilder();
            stringToSign.append("GET").append("&");
            stringToSign.append(specialUrlEncode("/")).append("&");
            stringToSign.append(specialUrlEncode(sortedQueryString));
            String sign = org.apache.commons.codec.binary.Base64.encodeBase64String(HmacUtils.hmacSha1(accessKeySecret + "&", stringToSign.toString()));
            String signature = specialUrlEncode(sign);
            String url = "http://dysmsapi.aliyuncs.com/?Signature=" + signature + sortQueryStringTmp;
            String resJson = HttpClientUtil.getHttpDataAsUTF_8(url, url);
            log.info("阿里云短信发送返回：" + resJson);
            if ("OK".equals(JSON.parseObject(resJson).getString("Code"))) {
                TooltipUtil.showToast("阿里云发送短信成功！！！");
            } else {
                TooltipUtil.showToast("阿里云发送短信失败：" + resJson);
            }
        } catch (Exception e) {
            TooltipUtil.showToast("阿里云发送短信失败：" + e.getMessage());
            log.error("阿里云发送短信失败:" + e.getMessage());
        }
    }

    /**
     * 梦网云通讯短信发送
     */
    public void monyunSendAction(SmsToolTableBean... smsToolTableBeans) {
        try {
            String url = smsToolController.getMonyunUrlTextField().getText();//Url
            String userid = smsToolController.getMonyunUseridTextField().getText();//发送账号
            String pwd = smsToolController.getMonyunPwdTextField().getText();//发送账号密码
            String apikey = smsToolController.getMonyunApikeyTextField().getText();//用户唯一标识
//            String mobile = "18356971618";//短信接收的手机号：多个手机号请用英文逗号分隔
            String content = smsToolController.getMonyunContentTextArea().getText();
            String timestamp = DateFormatUtils.format(new Date(), "MMddHHmmss");
            String svrtype = smsToolController.getMonyunSvrtypeTextField().getText();//业务类型
            String exno = smsToolController.getMonyunExnoTextField().getText();//扩展号
            ArrayList<String> phoneNumbers = new ArrayList<String>();
            for (SmsToolTableBean smsToolTableBean : smsToolTableBeans) {
                phoneNumbers.add(smsToolTableBean.getToPhone());
            }
            Map params = new HashMap<>();
            if (StringUtils.isBlank(apikey)) {
                pwd = DigestUtils.md5Hex(userid.toUpperCase() + "00000000" + pwd + timestamp);
                params.put("userid", userid.toUpperCase());
                params.put("pwd", pwd);
            } else {
                params.put("apikey", apikey);
            }
            params.put("timestamp", timestamp);
            params.put("mobile", StringUtils.join(phoneNumbers, ","));
            params.put("content", java.net.URLEncoder.encode(content, "GBK"));
            params.put("svrtype", svrtype);
            params.put("exno", exno);
            String resultStr = HttpClientUtil.getHttpDataByPost(url + "batch_send", url, JSON.toJSONString(params), "text/json; charset=utf-8");
            log.info("梦网云通讯短信发送返回：" + resultStr);
            if (JSON.parseObject(resultStr).getInteger("result") == 0) {
                TooltipUtil.showToast("梦网云通讯发送短信成功！！！");
            } else {
                TooltipUtil.showToast("梦网云通讯发送短信失败：" + resultStr);
            }
        } catch (Exception e) {
            log.error("梦网云通讯发送短信失败：", e);
            TooltipUtil.showToast("梦网云通讯发送短信失败：" + e.getMessage());
        }
    }

    /**
     * 导入联系人号码
     */
    public void importToPhoneAction() {
        try {
            File file = FileChooserUtil.chooseFile();
            if (file != null) {
                List<String> smsList = FileUtils.readLines(file, "utf-8");
                ObservableList<SmsToolTableBean> tableData = smsToolController.getTableData();
                for (String sms : smsList) {
                    SmsToolTableBean smsToolTableBean = null;
                    if (sms.contains(" ")) {
                        String[] smsStr = sms.split(" ");
                        smsToolTableBean = new SmsToolTableBean(tableData.size() + 1, true, smsStr[0], smsStr[1], "");
                    } else {
                        smsToolTableBean = new SmsToolTableBean(tableData.size() + 1, true, sms, sms, "");
                    }
                    tableData.add(smsToolTableBean);
                }
            }
        } catch (Exception e) {
            log.error("导入联系人号码失败：" + e.getMessage());
            TooltipUtil.showToast("导入联系人号码失败：" + e.getMessage());
        }
    }

    public SmsToolService(SmsToolController smsToolController) {
        this.smsToolController = smsToolController;
    }

    public static String specialUrlEncode(String value) throws Exception {
        return java.net.URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }
}