package com.xwintop.xTransfer.sender.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.sender.bean.SenderConfigEmail;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.enums.EmailMethod;
import com.xwintop.xTransfer.sender.service.Sender;
import com.xwintop.xTransfer.util.ParseVariableCommon;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Map;

/**
 * @ClassName: SenderEmailImpl
 * @Description: 发送Email实现类
 * @author: xufeng
 * @date: 2018/6/13 16:06
 */

@Service("senderEmail")
@Scope("prototype")
@Getter
@Setter
@Slf4j
public class SenderEmailImpl implements Sender {
    private SenderConfigEmail senderConfigEmail;

    private JavaMailSenderImpl mailSender;

    @Override
    public Boolean send(IMessage msg, Map params) throws Exception {
        log.debug("SenderEmail:" + msg.getId());
        if (mailSender == null) {
            mailSender = new JavaMailSenderImpl();
            mailSender.setHost(senderConfigEmail.getHost());
            mailSender.setPort(senderConfigEmail.getPort());
            mailSender.setUsername(senderConfigEmail.getUser());
            mailSender.setPassword(senderConfigEmail.getPassword());
            mailSender.setDefaultEncoding(senderConfigEmail.getEncoding());
            mailSender.setProtocol(senderConfigEmail.getProtocol());
        }
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(ParseVariableCommon.parseVariable(senderConfigEmail.getFrom(), msg, params));
        helper.setTo(ParseVariableCommon.parseVariable(senderConfigEmail.getTo(), msg, params));
        helper.setSubject(ParseVariableCommon.parseVariable(senderConfigEmail.getSubject(), msg, params));

        String fileName = msg.getFileName();
        if (StringUtils.isNotBlank(senderConfigEmail.getFileName())) {
            fileName = ParseVariableCommon.parseVariable(senderConfigEmail.getFileName(), msg, params);
        }
        switch (EmailMethod.getEnum(senderConfigEmail.getMethod())) {
            case attachment:
                helper.setText("");
                helper.addAttachment(fileName, new ByteArrayResource(msg.getMessage()));
                break;
            case plain:
                helper.setText(msg.getMessageByString());
                break;
        }
        mailSender.send(message);

        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_SENDED, msg.getId(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT_TYPE, LOGVALUES.CHANNEL_TYPE_EMAIL);
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT, senderConfigEmail.getHost() + "/" + senderConfigEmail.getPort());
        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, msg.getProperty(LOGKEYS.RECEIVER_TYPE));
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, msg.getProperty(LOGKEYS.RECEIVER_ID));
        MsgLogger.info(msgLogInfo.toMap());
        return true;
    }

    @Override
    public void setSenderConfig(SenderConfig senderConfig) throws Exception {
        this.senderConfigEmail = (SenderConfigEmail) senderConfig;
    }

    @Override
    public void destroy() throws Exception {
        if (mailSender != null) {
            mailSender = null;
        }
    }
}
