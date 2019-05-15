package com.xwintop.xTransfer.receiver.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfigJms;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xcore.util.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Service;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;
import java.util.Map;

/**
 * @ClassName: ReceiverJmsImpl
 * @Description: Jms接收器
 * @author: xufeng
 * @date: 2019/1/17 17:06
 */

@Service("receiverJms")
@Scope("prototype")
@Slf4j
public class ReceiverJmsImpl implements Receiver {
    private ReceiverConfigJms receiverConfigJms;
    private MessageHandler messageHandler;
    private DefaultMessageListenerContainer defaultMessageListenerContainer = null;
    private ConnectionFactory connectionFactory;

    @Override
    public void receive(Map params) throws Exception {
        if (defaultMessageListenerContainer == null) {
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, receiverConfigJms.getJmsJndiFactory());
            env.put(Context.PROVIDER_URL, receiverConfigJms.getJmsUrl());
            InitialContext initialContext = new InitialContext(env);
            ConnectionFactory orginFactory = (ConnectionFactory) initialContext.lookup(receiverConfigJms.getJmsFactory());
            if (receiverConfigJms.isUseJms102()) {
                SingleConnectionFactory scf = new SingleConnectionFactory(orginFactory);
                scf.setReconnectOnException(true);
                connectionFactory = scf;
            } else {
                CachingConnectionFactory ccf = new CachingConnectionFactory();
                ccf.setTargetConnectionFactory(orginFactory);
                ccf.setReconnectOnException(true);
                if (receiverConfigJms.getSessionSize() > 0) {
                    ccf.setSessionCacheSize(receiverConfigJms.getSessionSize());
                }
                connectionFactory = ccf;
            }
            defaultMessageListenerContainer = new DefaultMessageListenerContainer();

            Queue queue = (Queue) initialContext.lookup(receiverConfigJms.getJmsQueue());
            defaultMessageListenerContainer.setConnectionFactory(connectionFactory);
            defaultMessageListenerContainer.setDestination(queue);
            defaultMessageListenerContainer.setSessionTransacted(true);
            defaultMessageListenerContainer.setConcurrentConsumers(1);//消费者个数
            defaultMessageListenerContainer.afterPropertiesSet();
            defaultMessageListenerContainer.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
            defaultMessageListenerContainer.setMessageListener((SessionAwareMessageListener<Message>) (message, session) -> {
                try {
                    IMessage msg = new DefaultMessage();
                    log.debug("Jms接收到消息：" + message);
                    if (message instanceof BytesMessage) {
                        byte[] b = new byte[(int) ((BytesMessage) message).getBodyLength()];
                        ((BytesMessage) message).readBytes(b);
                        msg.setRawData(b);
                    } else if (message instanceof TextMessage) {
                        msg.setRawData(((TextMessage) message).getText().getBytes());
                    } else if (message instanceof ObjectMessage) {
                        Object o = ((ObjectMessage) message).getObject();
                        if (o instanceof IMessage) {
                            msg = (IMessage) o;
                        } else {
                            msg = new DefaultMessage();
                            msg.setRawData((byte[]) o);
                        }
                    } else {
                        msg.setRawData((byte[]) ((ObjectMessage) message).getObject());
                    }
                    if (StringUtils.isBlank(msg.getFileName())) {
                        String fileName = message.getStringProperty(receiverConfigJms.getFileNameField());
                        if (fileName == null) {
                            fileName = UuidUtil.get32UUID();
                        }
                        msg.setFileName(fileName);
                    }
                    log.info("received jms message id is:" + msg.getId());
                    msg.setProperty(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_JMS);
                    msg.setProperty(LOGKEYS.CHANNEL_IN, receiverConfigJms.getJmsUrl() + ":" + receiverConfigJms.getJmsQueue());
                    msg.setProperty(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_JMS);
                    msg.setProperty(LOGKEYS.RECEIVER_ID, this.receiverConfigJms.getId());
                    IContext ctx = new DefaultContext();
                    ctx.setMessage(msg);

                    Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, msg.getId(), null);
                    msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_JMS);
                    msgLogInfo.put(LOGKEYS.CHANNEL_IN, receiverConfigJms.getJmsUrl() + ":" + receiverConfigJms.getJmsQueue());
                    msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
                    msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
                    msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
                    msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
                    msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_JMS);
                    msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigJms.getId());

                    MsgLogger.info(msgLogInfo.toMap());

                    messageHandler.handle(ctx);
                    log.debug("success pull into queue:" + msg.getId());

                    message.acknowledge();
                } catch (Exception e) {
                    log.error("receiverJms收取处理异常:", e);
                    session.rollback();
                    this.destroy();
                }
            });
            defaultMessageListenerContainer.start();
        }
    }

    @Override
    public void setReceiverConfig(ReceiverConfig receiverConfig) {
        this.receiverConfigJms = (ReceiverConfigJms) receiverConfig;
    }

    @Override
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void destroy() {
        if (defaultMessageListenerContainer != null) {
            defaultMessageListenerContainer.stop();
            defaultMessageListenerContainer = null;
        }
    }
}
