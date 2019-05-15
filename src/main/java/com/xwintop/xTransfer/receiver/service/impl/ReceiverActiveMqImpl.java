package com.xwintop.xTransfer.receiver.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfigActiveMq;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.util.Map;

/**
 * @ClassName: ReceiverActiveMqImpl
 * @Description: ActiveMq接收器实现类
 * @author: xufeng
 * @date: 2019/5/8 9:42
 */

@Service("receiverActiveMq")
@Scope("prototype")
@Slf4j
public class ReceiverActiveMqImpl implements Receiver {
    private ReceiverConfigActiveMq receiverConfigActiveMq;
    private MessageHandler messageHandler;

    private SimpleMessageListenerContainer simpleMessageListenerContainer = null;

    @Override
    public void receive(Map params) throws Exception {
        log.debug("ReceiverActiveMqImpl开始执行");
        if (simpleMessageListenerContainer == null) {
            simpleMessageListenerContainer = new SimpleMessageListenerContainer();

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            connectionFactory.setBrokerURL("tcp://" + receiverConfigActiveMq.getHost() + ":" + receiverConfigActiveMq.getPort());
            if (StringUtils.isEmpty(receiverConfigActiveMq.getUsername())) {
                connectionFactory.setUserName(receiverConfigActiveMq.getUsername());
                connectionFactory.setPassword(receiverConfigActiveMq.getPassword());
            }
            CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
            cachingConnectionFactory.setTargetConnectionFactory(connectionFactory);
            cachingConnectionFactory.setSessionCacheSize(500);
            cachingConnectionFactory.setReconnectOnException(true);

            simpleMessageListenerContainer.setConnectionFactory(cachingConnectionFactory);
            simpleMessageListenerContainer.setDestinationName(receiverConfigActiveMq.getQueueName());
            simpleMessageListenerContainer.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
            simpleMessageListenerContainer.setExceptionListener(exception -> {
                log.error("ActiveMq接收器监听异常:", exception);
                this.destroy();
            });
            if (receiverConfigActiveMq.getConcurrentConsumers() != null) {
                //消费者个数
                simpleMessageListenerContainer.setConcurrentConsumers(receiverConfigActiveMq.getConcurrentConsumers());
            }
            simpleMessageListenerContainer.setMessageListener((SessionAwareMessageListener<Message>) (message, session) -> {
                try {
                    IMessage msg = new DefaultMessage();
                    log.debug("ActiveMq接收到消息：" + message);
                    if (message instanceof BytesMessage) {
                        msg.setRawData(((ActiveMQBytesMessage) message).getContent().getData());
                    } else if (message instanceof TextMessage) {
                        msg.setRawData(((TextMessage) message).getText().getBytes());
                    } else if (message instanceof ObjectMessage) {
                        Object o = ((ObjectMessage) message).getObject();
                        msg.setRawData((byte[]) o);
                    } else if (message instanceof MapMessage) {
                        msg.setRawData(((ActiveMQMapMessage) message).getContentMap().toString().getBytes());
                    } else if (message instanceof StreamMessage) {
                        msg.setRawData(((StreamMessage) message).readString().getBytes());
                    } else {
                        msg.setRawData((byte[]) ((ObjectMessage) message).getObject());
                    }
                    if (StringUtils.isBlank(msg.getFileName())) {
                        msg.setFileName(message.getStringProperty(receiverConfigActiveMq.getFileNameField()));
                        if (StringUtils.isNotEmpty(msg.getFileName())) {
                            msg.setFileName(msg.getId());
                        }
                    }
                    log.info("received activeMq message id is:" + msg.getId());
                    msg.setProperty(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_ACTIVE_MQ);
                    msg.setProperty(LOGKEYS.CHANNEL_IN, receiverConfigActiveMq.getHost() + ":" + receiverConfigActiveMq.getQueueName());
                    msg.setProperty(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_MQ);
                    msg.setProperty(LOGKEYS.RECEIVER_ID, this.receiverConfigActiveMq.getId());
                    IContext ctx = new DefaultContext();
                    ctx.setMessage(msg);

                    Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, msg.getId(), null);
                    msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_ACTIVE_MQ);
                    msgLogInfo.put(LOGKEYS.CHANNEL_IN, receiverConfigActiveMq.getHost() + ":" + receiverConfigActiveMq.getQueueName());
                    msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
                    msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
                    msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
                    msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
                    msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_MQ);
                    msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigActiveMq.getId());

                    MsgLogger.info(msgLogInfo.toMap());

                    messageHandler.handle(ctx);
                    log.info("success received activeMq message id is:" + msg.getId());

                    message.acknowledge();
                } catch (Exception e) {
                    log.error("receiverActiveMq收取处理异常:", e);
                    session.rollback();
                    this.destroy();
                }
            });
            simpleMessageListenerContainer.start();
        }
    }

    @Override
    public void setReceiverConfig(ReceiverConfig receiverConfig) {
        this.receiverConfigActiveMq = (ReceiverConfigActiveMq) receiverConfig;
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
