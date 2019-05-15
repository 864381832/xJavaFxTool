package com.xwintop.xTransfer.receiver.service.impl;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;
import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfigIbmMq;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xcore.util.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.util.Map;

/**
 * @ClassName: ReceiverIbmMqImpl
 * @Description: IbmMq接收器
 * @author: xufeng
 * @date: 2018/6/13 16:11
 */

@Service("receiverIbmMq")
@Scope("prototype")
@Slf4j
public class ReceiverIbmMqImpl implements Receiver {
    private ReceiverConfigIbmMq receiverConfigIbmMq;
    private MessageHandler messageHandler;
    private SimpleMessageListenerContainer simpleMessageListenerContainer = null;

    @Override
    public void receive(Map params) throws Exception {
        if (simpleMessageListenerContainer == null) {
            simpleMessageListenerContainer = new SimpleMessageListenerContainer();
            MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
            mqQueueConnectionFactory.setQueueManager(receiverConfigIbmMq.getQueueManagerName());
            if (StringUtils.isNotBlank(receiverConfigIbmMq.getHostName())) {
                mqQueueConnectionFactory.setHostName(receiverConfigIbmMq.getHostName());
                mqQueueConnectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
            }
            if (StringUtils.isNotBlank(receiverConfigIbmMq.getChannel())) {
                mqQueueConnectionFactory.setChannel(receiverConfigIbmMq.getChannel());
            }
            if (receiverConfigIbmMq.getPort() != null) {
                mqQueueConnectionFactory.setPort(receiverConfigIbmMq.getPort());
            }
            if (receiverConfigIbmMq.getCCSID() != null) {
                mqQueueConnectionFactory.setCCSID(receiverConfigIbmMq.getCCSID());
            }
            CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
            if (StringUtils.isEmpty(receiverConfigIbmMq.getUsername())) {
                cachingConnectionFactory.setTargetConnectionFactory(mqQueueConnectionFactory);
            } else {
                UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
                userCredentialsConnectionFactoryAdapter.setUsername(receiverConfigIbmMq.getUsername());
                userCredentialsConnectionFactoryAdapter.setPassword(receiverConfigIbmMq.getPassword());
                userCredentialsConnectionFactoryAdapter.setTargetConnectionFactory(mqQueueConnectionFactory);
                cachingConnectionFactory.setTargetConnectionFactory(userCredentialsConnectionFactoryAdapter);
            }
            cachingConnectionFactory.setSessionCacheSize(500);
            cachingConnectionFactory.setReconnectOnException(true);

            simpleMessageListenerContainer.setConnectionFactory(cachingConnectionFactory);
            simpleMessageListenerContainer.setDestinationName(receiverConfigIbmMq.getQueueName());
            simpleMessageListenerContainer.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
            simpleMessageListenerContainer.setExceptionListener(exception -> {
                log.error("IbmMq接收器监听异常:", exception);
                this.destroy();
            });
            if (receiverConfigIbmMq.getConcurrentConsumers() != null) {
                //消费者个数
                simpleMessageListenerContainer.setConcurrentConsumers(receiverConfigIbmMq.getConcurrentConsumers());
            }
            simpleMessageListenerContainer.setMessageListener((SessionAwareMessageListener<Message>) (message, session) -> {
                try {
                    IMessage msg = new DefaultMessage();
                    log.debug("IbmMq接收到消息：" + message);
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
                            msg.setRawData((byte[]) o);
                        }
                    } else {
                        msg.setRawData((byte[]) ((ObjectMessage) message).getObject());
                    }
                    if (StringUtils.isBlank(msg.getFileName())) {
                        String fileName = message.getStringProperty(receiverConfigIbmMq.getFileNameField());
                        if (fileName == null) {
                            fileName = UuidUtil.get32UUID();
                        }
                        msg.setFileName(fileName);
                    }
                    log.info("received mq message id is:" + msg.getId());
                    msg.setProperty(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_MQ);
                    msg.setProperty(LOGKEYS.CHANNEL_IN, receiverConfigIbmMq.getQueueManagerName() + ":" + receiverConfigIbmMq.getQueueName());
                    msg.setProperty(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_MQ);
                    msg.setProperty(LOGKEYS.RECEIVER_ID, this.receiverConfigIbmMq.getId());
                    IContext ctx = new DefaultContext();
                    ctx.setMessage(msg);

                    Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, msg.getId(), null);
                    msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_MQ);
                    msgLogInfo.put(LOGKEYS.CHANNEL_IN, receiverConfigIbmMq.getQueueManagerName() + ":" + receiverConfigIbmMq.getQueueName());
                    msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
                    msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
                    msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
                    msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
                    msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_MQ);
                    msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigIbmMq.getId());

                    MsgLogger.info(msgLogInfo.toMap());

                    messageHandler.handle(ctx);
                    log.info("success put into queue:" + msg.getId());

                    message.acknowledge();
                } catch (Exception e) {
                    log.error("receiverIbmMq收取处理异常:", e);
                    session.rollback();
                    this.destroy();
                }
            });
            simpleMessageListenerContainer.start();
        }
    }

    @Override
    public void setReceiverConfig(ReceiverConfig receiverConfig) {
        this.receiverConfigIbmMq = (ReceiverConfigIbmMq) receiverConfig;
    }

    @Override
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void destroy() {
        if (simpleMessageListenerContainer != null) {
            simpleMessageListenerContainer.stop();
            simpleMessageListenerContainer = null;
        }
    }
}
