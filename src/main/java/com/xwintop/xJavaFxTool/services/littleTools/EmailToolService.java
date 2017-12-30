package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.EmailToolController;
import com.xwintop.xJavaFxTool.model.EmailToolTableBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

import javax.mail.internet.InternetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * @ClassName: EmailToolService
 * @Description: 邮件发送工具
 * @author: xufeng
 * @date: 2017/12/30 0030 21:04
 */
@Getter
@Setter
@Log4j
public class EmailToolService {
    private EmailToolController emailToolController;

    public void runAllAction() {
        ArrayList<EmailToolTableBean> emailToolTableBeanArrayList = new ArrayList<EmailToolTableBean>();
        for (EmailToolTableBean emailToolTableBean : emailToolController.getTableData()) {
            if(emailToolTableBean.getIsEnabled()){
                emailToolTableBeanArrayList.add(emailToolTableBean);
            }
        }
        if(!emailToolTableBeanArrayList.isEmpty()){
            runAction(emailToolTableBeanArrayList.toArray(new EmailToolTableBean[0]));
        }
    }

    public void runAction(EmailToolTableBean... emailToolTableBeans) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName(emailToolController.getHostNameComboBox().getValue());
            email.setSmtpPort(Integer.parseInt(emailToolController.getPortTextField().getText()));
            email.setAuthentication(emailToolController.getUserNameTextField().getText(), emailToolController.getPasswordPasswordField().getText());
            email.setSSLOnConnect(emailToolController.getSslCheckBox().isSelected());
            email.setFrom(emailToolController.getUserNameTextField().getText());
            email.setSubject(emailToolController.getSubjectTextField().getText());
            email.setHtmlMsg(emailToolController.getMsgHtmlEditor().getHtmlText());
            email.setCharset("utf-8");
            if(emailToolController.getAttachCheckBox().isSelected()){
                for(Map<String,String> map:emailToolController.getAttachPathTableData()){
                    EmailAttachment attachment = new EmailAttachment();
                    attachment.setName(map.get("attachName"));
                    String attachPath = map.get("attachPath");
                    if(attachPath.startsWith("http")){
                        attachment.setURL(new URL(attachPath));//网络文件
                    }else {
                        attachment.setPath(attachPath);//本地文件
                    }
                    attachment.setDescription(map.get("attachDescription"));
                    email.attach(attachment);
                }
            }
            if (emailToolController.getSentSeparatelyCheckBox().isSelected()){
                for (EmailToolTableBean emailToolTableBean : emailToolTableBeans){
                    ArrayList<InternetAddress> toList = new ArrayList<InternetAddress>();
                    toList.add(new InternetAddress(emailToolTableBean.getToEmail(),emailToolTableBean.getToEmailName()));
                    email.setTo(toList);
                    email.send();
                }
            }else {
                for (EmailToolTableBean emailToolTableBean : emailToolTableBeans){
                    email.addTo(emailToolTableBean.getToEmail(),emailToolTableBean.getToEmailName());
                }
                email.send();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public EmailToolService(EmailToolController emailToolController) {
        this.emailToolController = emailToolController;
    }
}