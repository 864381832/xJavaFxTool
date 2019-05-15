package com.xwintop.xTransfer.sender.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.sender.bean.SenderConfigIbmMq;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.service.Sender;
import com.ibm.mq.jms.MQDestination;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import java.util.Map;

/**
 * @ClassName: SenderIbmMqImpl
 * @Description: IbmMq发送类
 * @author: xufeng
 * @date: 2018/12/10 16:10
 */

@Service("senderIbmMq")
@Scope("prototype")
@Getter
@Setter
@Slf4j
public class SenderIbmMqImpl implements Sender {
    private SenderConfigIbmMq senderConfigIbmMq;

    private JmsTemplate jmsTemplate = null;

    @Override
    public Boolean send(IMessage msg, Map params) throws Exception {
        this.checkInit();
        try {
            jmsTemplate.send(session -> {
                BytesMessage message = session.createBytesMessage();
                message.writeBytes(msg.getMessage());
                if (StringUtils.isNotEmpty(senderConfigIbmMq.getFileNameField())) {
                    message.setStringProperty(senderConfigIbmMq.getFileNameField(), msg.getFileName());
                }
                if (senderConfigIbmMq.getArgs() != null && !senderConfigIbmMq.getArgs().isEmpty()) {
                    senderConfigIbmMq.getArgs().forEach((key, value) -> {
                        try {
                            message.setStringProperty(key.toString(), value.toString());
                        } catch (Exception e) {
                        }
                    });
                }
                return message;
            });
        } catch (Exception e) {
            log.error("ibmMq发送失败：", e);
            this.destroy();
            throw e;
        }

        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_SENDED, msg.getId(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT_TYPE, LOGVALUES.CHANNEL_TYPE_MQ);
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT, senderConfigIbmMq.getQueueManagerName() + "/" + senderConfigIbmMq.getQueueName());
        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, msg.getProperty(LOGKEYS.RECEIVER_TYPE));
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, msg.getProperty(LOGKEYS.RECEIVER_ID));
        MsgLogger.info(msgLogInfo.toMap());

        log.info("SenderIbmMq完成,msgId:" + msg.getId() + " fileName:" + msg.getFileName());
        return true;
    }

    @Override
    public void setSenderConfig(SenderConfig senderConfig) throws Exception {
        this.senderConfigIbmMq = (SenderConfigIbmMq) senderConfig;
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
            MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
            mqQueueConnectionFactory.setQueueManager(senderConfigIbmMq.getQueueManagerName());
            if (StringUtils.isNotBlank(senderConfigIbmMq.getHostName())) {
                mqQueueConnectionFactory.setHostName(senderConfigIbmMq.getHostName());
                mqQueueConnectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
            }
            if (StringUtils.isNotBlank(senderConfigIbmMq.getChannel())) {
                mqQueueConnectionFactory.setChannel(senderConfigIbmMq.getChannel());
            }
            if (senderConfigIbmMq.getPort() != null) {
                mqQueueConnectionFactory.setPort(senderConfigIbmMq.getPort());
            }
            if (senderConfigIbmMq.getCCSID() != null) {
                mqQueueConnectionFactory.setCCSID(senderConfigIbmMq.getCCSID());
            }
//            mqQueueConnectionFactory.setTargetClientMatching(false);
            CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
            if (StringUtils.isEmpty(senderConfigIbmMq.getUsername())) {
                cachingConnectionFactory.setTargetConnectionFactory(mqQueueConnectionFactory);
            } else {
                UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
                userCredentialsConnectionFactoryAdapter.setUsername(senderConfigIbmMq.getUsername());
                userCredentialsConnectionFactoryAdapter.setPassword(senderConfigIbmMq.getPassword());
                userCredentialsConnectionFactoryAdapter.setTargetConnectionFactory(mqQueueConnectionFactory);
                cachingConnectionFactory.setTargetConnectionFactory(userCredentialsConnectionFactoryAdapter);
            }
            cachingConnectionFactory.setSessionCacheSize(500);
            cachingConnectionFactory.setReconnectOnException(true);
            jmsTemplate = new JmsTemplate(cachingConnectionFactory);
            jmsTemplate.setDefaultDestinationName(senderConfigIbmMq.getQueueName());
            if (senderConfigIbmMq.isTargetClient()) {
                jmsTemplate.execute(session -> {
                    Destination destination = jmsTemplate.getDestinationResolver().resolveDestinationName(session, senderConfigIbmMq.getQueueName(), false);
                    if (destination instanceof MQDestination) {
                        MQDestination mqDestination = (MQDestination) destination;
                        mqDestination.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);
                    }
                    jmsTemplate.setDefaultDestination(destination);
                    return null;
                });
            }
        }
    }
}
