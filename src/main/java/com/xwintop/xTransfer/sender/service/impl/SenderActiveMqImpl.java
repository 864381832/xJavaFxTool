package com.xwintop.xTransfer.sender.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.bean.SenderConfigActiveMq;
import com.xwintop.xTransfer.sender.service.Sender;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.BytesMessage;
import java.util.Map;

/**
 * @ClassName: SenderActiveMqImpl
 * @Description: ActiveMq发送类
 * @author: xufeng
 * @date: 2019/5/8 13:21
 */

@Service("senderActiveMq")
@Scope("prototype")
@Getter
@Setter
@Slf4j
public class SenderActiveMqImpl implements Sender {
    private SenderConfigActiveMq senderConfigActiveMq;

    private JmsTemplate jmsTemplate = null;

    @Override
    public Boolean send(IMessage msg, Map params) throws Exception {
        this.checkInit();
        try {
            jmsTemplate.send(session -> {
                BytesMessage message = session.createBytesMessage();
                message.writeBytes(msg.getMessage());
                if (StringUtils.isNotEmpty(senderConfigActiveMq.getFileNameField())) {
                    message.setStringProperty(senderConfigActiveMq.getFileNameField(), msg.getFileName());
                }
                if (senderConfigActiveMq.getArgs() != null && !senderConfigActiveMq.getArgs().isEmpty()) {
                    senderConfigActiveMq.getArgs().forEach((key, value) -> {
                        try {
                            message.setStringProperty(key.toString(), value.toString());
                        } catch (Exception e) {
                        }
                    });
                }
                return message;
            });
        } catch (Exception e) {
            log.error("ActiveMq发送失败：", e);
            this.destroy();
            throw e;
        }

        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_SENDED, msg.getId(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT_TYPE, LOGVALUES.CHANNEL_TYPE_ACTIVE_MQ);
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT, senderConfigActiveMq.getHostName() + "/" + senderConfigActiveMq.getQueueName());
        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, msg.getProperty(LOGKEYS.RECEIVER_TYPE));
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, msg.getProperty(LOGKEYS.RECEIVER_ID));
        MsgLogger.info(msgLogInfo.toMap());

        log.info("SenderActiveMq完成,msgId:" + msg.getId() + " fileName:" + msg.getFileName());
        return true;
    }

    @Override
    public void setSenderConfig(SenderConfig senderConfig) throws Exception {
        this.senderConfigActiveMq = (SenderConfigActiveMq) senderConfig;
    }

    @Override
    public void destroy() throws Exception {
        if (jmsTemplate != null) {
            ((CachingConnectionFactory) jmsTemplate.getConnectionFactory()).destroy();
            jmsTemplate = null;
        }
    }

    private synchronized void checkInit() throws Exception {
        if (jmsTemplate == null) {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            connectionFactory.setBrokerURL("tcp://" + senderConfigActiveMq.getHostName() + ":" + senderConfigActiveMq.getPort());
            if (StringUtils.isEmpty(senderConfigActiveMq.getUsername())) {
                connectionFactory.setUserName(senderConfigActiveMq.getUsername());
                connectionFactory.setPassword(senderConfigActiveMq.getPassword());
            }
            CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
            cachingConnectionFactory.setTargetConnectionFactory(connectionFactory);
            cachingConnectionFactory.setSessionCacheSize(500);
            cachingConnectionFactory.setReconnectOnException(true);

            jmsTemplate = new JmsTemplate(cachingConnectionFactory);
            jmsTemplate.setDefaultDestinationName(senderConfigActiveMq.getQueueName());
        }
    }
}
