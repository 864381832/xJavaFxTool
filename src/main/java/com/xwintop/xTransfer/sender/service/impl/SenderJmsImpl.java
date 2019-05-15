package com.xwintop.xTransfer.sender.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.sender.bean.SenderConfigJms;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.service.Sender;
import com.ibm.mq.jms.MQDestination;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.BytesMessage;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;
import java.util.Map;

/**
 * @ClassName: SenderJmsImpl
 * @Description: Jms发送类
 * @author: xufeng
 * @date: 2019/2/13 14:35
 */

@Service("senderJms")
@Scope("prototype")
@Getter
@Setter
@Slf4j
public class SenderJmsImpl implements Sender {
    private SenderConfigJms senderConfigJms;
    private JmsTemplate jmsTemplate = null;

    @Override
    public Boolean send(IMessage msg, Map params) throws Exception {
        this.checkInit();
        try {
            jmsTemplate.send(session -> {
                BytesMessage message = session.createBytesMessage();
                message.writeBytes(msg.getMessage());
                if (StringUtils.isNotEmpty(senderConfigJms.getFileNameField())) {
                    message.setStringProperty(senderConfigJms.getFileNameField(), msg.getFileName());
                }
                if (senderConfigJms.getArgs() != null && !senderConfigJms.getArgs().isEmpty()) {
                    senderConfigJms.getArgs().forEach((key, value) -> {
                        try {
                            message.setStringProperty(key.toString(), value.toString());
                        } catch (Exception e) {
                        }
                    });
                }
                return message;
            });
        } catch (Exception e) {
            log.error("Jms发送失败：", e);
            this.destroy();
            throw e;
        }

        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_SENDED, msg.getId(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT_TYPE, LOGVALUES.CHANNEL_TYPE_MQ);
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT, senderConfigJms.getJmsUrl() + "/" + senderConfigJms.getJmsQueue());
        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, msg.getProperty(LOGKEYS.RECEIVER_TYPE));
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, msg.getProperty(LOGKEYS.RECEIVER_ID));
        MsgLogger.info(msgLogInfo.toMap());

        log.info("SenderJms完成,msgId:" + msg.getId() + " fileName:" + msg.getFileName());
        return true;
    }

    @Override
    public void setSenderConfig(SenderConfig senderConfig) throws Exception {
        this.senderConfigJms = (SenderConfigJms) senderConfig;
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
            jmsTemplate = new JmsTemplate();
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, senderConfigJms.getJmsJndiFactory());
            env.put(Context.PROVIDER_URL, senderConfigJms.getJmsUrl());
            InitialContext context = new InitialContext(env);
            ConnectionFactory orginFactory = (ConnectionFactory) context.lookup(senderConfigJms.getJmsFactory());
            Queue queue = (Queue) context.lookup(senderConfigJms.getJmsQueue());

            CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
            cachingConnectionFactory.setTargetConnectionFactory(orginFactory);
            if(senderConfigJms.getSessionCacheSize() > 0){
                cachingConnectionFactory.setSessionCacheSize(senderConfigJms.getSessionCacheSize());
            }
            cachingConnectionFactory.setReconnectOnException(true);

            jmsTemplate.setConnectionFactory(cachingConnectionFactory);
            if (senderConfigJms.isTargetClient()) {
                if (queue instanceof MQDestination) {
                    MQDestination mqDestination = (MQDestination) queue;
                    mqDestination.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);
                }
            }
            jmsTemplate.setDefaultDestination(queue);
        }
    }
}
