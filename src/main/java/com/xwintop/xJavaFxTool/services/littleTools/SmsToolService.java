package com.xwintop.xJavaFxTool.services.littleTools;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.xwintop.xJavaFxTool.controller.littleTools.SmsToolController;
import com.xwintop.xJavaFxTool.model.EmailToolTableBean;
import com.xwintop.xJavaFxTool.model.SmsToolTableBean;
import com.xwintop.xcore.util.HttpClientUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.util.*;

@Getter
@Setter
@Log4j
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
        }
    }

    public void runAction(SmsToolTableBean... smsToolTableBeans) {
        int selectedIndex = smsToolController.getMainTabPane().getSelectionModel().getSelectedIndex();
        switch (selectedIndex) {
            case 0://中国移动
                System.out.println("中国移动");
                cmccSendAction(smsToolTableBeans);
                break;
            case 1://中国电信
                System.out.println("中国电信");
                open189SendAction(smsToolTableBeans);
                break;
            case 2://腾讯云
                System.out.println("腾讯云");
                tencentSendAction(smsToolTableBeans);
                break;
            case 3://阿里云
                System.out.println("阿里云");
                aliyunSendAction(smsToolTableBeans);
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
        String destAddr = "18356971618";//目标地址
        String apiKey = smsToolController.getCmccApiKeyTextField().getText();
        String secretKey = smsToolController.getCmccSecretKeyTextField().getText();
        String needReceipt = smsToolController.getCmccNeedReceiptCheckBox().isSelected() ? "1" : "0";//是否回执 1：回执0：不回执
        String message = smsToolController.getCmccMessageTextArea().getText();
        String receiptNotificationURL = smsToolController.getCmccReceiptNotificationUrlTextField().getText();//回调地址
        Map<String, String> map = new HashMap<>();
        map.put("destAddr", destAddr);
        map.put("apiKey", apiKey);
        map.put("secretKey", secretKey);
        map.put("needReceipt", needReceipt);
        map.put("message", message);
        if ("1".equals(needReceipt)) {
            map.put("receiptNotificationURL", receiptNotificationURL);
        }
        String s = HttpClientUtil.getHttpDataByPost(url, "http://www.openservice.com.cn", JSON.toJSONString(map));
        System.out.println(s);
    }

    /**
     * 中国电信短信发送
     */
    private void open189SendAction(SmsToolTableBean... smsToolTableBeans) {
        String url = smsToolController.getOpen189UrlTextField().getText();
        String acceptor_tel = "18356971618";//接收方号码
        String app_id = smsToolController.getOpen189AppIdTextField().getText();
        String access_token = "";
        String app_secret = smsToolController.getOpen189AppSecretTextField().getText();
        String template_id = smsToolController.getOpen189TemplateIdTextField().getText();//短信模板ID
        String template_param = smsToolController.getOpen189TemplateParamTextArea().getText();//模板匹配参数,参数格式为(json对象字符串): {参数名：参数值，参数名：参数值}

        Map<String, String> map = new HashMap<>();
        map.put("app_id", app_id);
        map.put("app_secret", app_secret);
        map.put("grant_type", "client_credentials");
        String resJson = HttpClientUtil.getHttpDataByPost("https://oauth.api.189.cn/emp/oauth2/v3/access_token", "https://oauth.api.189.cn", map);
        System.out.println(resJson);
        access_token = JSON.parseObject(resJson).getString("access_token");

        String timestamp = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");//时间戳
        TreeMap<String, String> map2 = new TreeMap<>();
        map2.put("app_id", app_id);
        map2.put("access_token", access_token);
        map2.put("acceptor_tel", acceptor_tel);
        map2.put("template_id", template_id);
        map2.put("template_param", template_param);
        map2.put("timestamp", timestamp);
        String idertifier = null;
        try {
            resJson = HttpClientUtil.getHttpDataByPost(url, "http://api.189.cn", map2);
            System.err.println(resJson);
            idertifier = JSON.parseObject(resJson).getString("idertifier");
            System.out.println("idertifier" + idertifier);
        } catch (Exception e) {
            System.err.println(resJson);
            e.printStackTrace();
        }
    }

    /**
     * 腾讯云短信发送
     */
    private void tencentSendAction(SmsToolTableBean... smsToolTableBeans) {
        int appid = Integer.parseInt(smsToolController.getTencentAppidTextField().getText());
        String appkey = smsToolController.getTencentAppkeyTextField().getText();
        boolean isParam = smsToolController.getTencentIsParamCheckBox().isSelected();
        String msg = smsToolController.getTencentMsgTextField().getText();
        ArrayList<String> phoneNumbers = new ArrayList<String>();
        phoneNumbers.add("18356971618");
        try {
            SmsMultiSender multiSender = new SmsMultiSender(appid, appkey);
            if (isParam) {
                String paramsStr = smsToolController.getTencentParamsTextArea().getText();
                ArrayList<String> params = JSON.parseObject(paramsStr, ArrayList.class);
                SmsMultiSenderResult multiSenderResult = multiSender.sendWithParam("86", phoneNumbers, 123, params, "", "", "");
                System.out.println(multiSenderResult);
            } else {
                SmsMultiSenderResult result = multiSender.send(0, "86", phoneNumbers, msg, "", "");
                System.out.print(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 阿里云短信发送
     */
    private void aliyunSendAction(SmsToolTableBean... smsToolTableBeans) {
        try {
            //产品名称:云通信短信API产品,开发者无需替换
            String product = "Dysmsapi";
            //产品域名,开发者无需替换
            String domain = "dysmsapi.aliyuncs.com";
            // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
            String accessKeyId = smsToolController.getAliyunAccessKeyIdTextField().getText();
            String accessKeySecret = smsToolController.getAliyunAccessKeySecretTextField().getText();
            String signName = smsToolController.getAliyunSignNameTextField().getText();
            String templateCode = smsToolController.getAliyunTemplateCodeTextField().getText();
            String templateParam = smsToolController.getAliyunTemplateParamTextArea().getText();

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            //必填:待发送手机号
            request.setPhoneNumbers("18356971618");
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(signName);
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            request.setTemplateParam(templateParam);
            //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");
            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//            request.setOutId("yourOutId");
            //hint 此处可能会抛出异常，注意catch
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            System.out.println("短信接口返回的数据----------------");
            System.out.println("Code=" + sendSmsResponse.getCode());
            System.out.println("Message=" + sendSmsResponse.getMessage());
            System.out.println("RequestId=" + sendSmsResponse.getRequestId());
            System.out.println("BizId=" + sendSmsResponse.getBizId());
        } catch (Exception e) {
            log.error("阿里云短信发送失败:"+e.getMessage());
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
}