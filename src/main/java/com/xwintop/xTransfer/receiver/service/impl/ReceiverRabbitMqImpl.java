package com.xwintop.xTransfer.receiver.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfigRabbitMq;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: ReceiverRabbitMqImpl
 * @Description: RabbitMq接收器实现类
 * @author: xufeng
 * @date: 2018/4/9 11:27
 */
@Service("receiverRabbitMq")
@Scope("prototype")
@Slf4j
public class ReceiverRabbitMqImpl implements Receiver {
    private ReceiverConfigRabbitMq receiverConfigRabbitMq;
    private MessageHandler messageHandler;

    SimpleMessageListenerContainer simpleMessageListenerContainer = null;

    @Override
    public void receive(Map params) throws Exception {
        log.debug("ReceiverRabbitMqImpl开始执行");
        this.checkInit(params);
    }

    @Override
    public void setReceiverConfig(ReceiverConfig receiverConfig) {
        this.receiverConfigRabbitMq = (ReceiverConfigRabbitMq) receiverConfig;
    }

    @Override
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void destroy() {
        if (simpleMessageListenerContainer != null && simpleMessageListenerContainer.isRunning()) {
            simpleMessageListenerContainer.stop();
            ((CachingConnectionFactory) simpleMessageListenerContainer.getConnectionFactory()).destroy();
            simpleMessageListenerContainer = null;
        }
    }

    private synchronized void checkInit(Map params) throws Exception {
        if (simpleMessageListenerContainer == null) {
            simpleMessageListenerContainer = new SimpleMessageListenerContainer();
            CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
            if (StringUtils.isNotEmpty(receiverConfigRabbitMq.getHost())) {
                connectionFactory.setHost(receiverConfigRabbitMq.getHost());
            }
            connectionFactory.setPort(receiverConfigRabbitMq.getPort());
            if (StringUtils.isNotEmpty(receiverConfigRabbitMq.getUsername())) {
                connectionFactory.setUsername(receiverConfigRabbitMq.getUsername());
            }
            if (StringUtils.isNotEmpty(receiverConfigRabbitMq.getPassword())) {
                connectionFactory.setPassword(receiverConfigRabbitMq.getPassword());
            }
            if (StringUtils.isNotEmpty(receiverConfigRabbitMq.getVirtualHost())) {
                connectionFactory.setVirtualHost(receiverConfigRabbitMq.getVirtualHost());
            }
            if (receiverConfigRabbitMq.getRequestedHeartbeat() != null) {
                connectionFactory.setRequestedHeartBeat(receiverConfigRabbitMq.getRequestedHeartbeat());
            }
            if (receiverConfigRabbitMq.getConnectionTimeout() != null) {
                connectionFactory.setConnectionTimeout(receiverConfigRabbitMq.getConnectionTimeout());
            }
            if (StringUtils.isNotEmpty(receiverConfigRabbitMq.getAddresses())) {
                //该方法配置多个host，在当前连接host down掉的时候会自动去重连后面的host
                connectionFactory.setAddresses(receiverConfigRabbitMq.getAddresses());
            }
            connectionFactory.setPublisherConfirms(receiverConfigRabbitMq.isPublisherConfirms());
            connectionFactory.setPublisherReturns(receiverConfigRabbitMq.isPublisherReturns());
            connectionFactory.getRabbitConnectionFactory().setAutomaticRecoveryEnabled(true);//如果连接超时，会自动去恢复
            simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
            simpleMessageListenerContainer.setQueueNames(receiverConfigRabbitMq.getTopic());
            simpleMessageListenerContainer.setExposeListenerChannel(true);
            //设置每个消费者获取的最大的消息数量
            simpleMessageListenerContainer.setPrefetchCount(receiverConfigRabbitMq.getPrefetchCount());
            if (receiverConfigRabbitMq.getConcurrentConsumers() != null) {
                //初始化消费者个数
                simpleMessageListenerContainer.setConcurrentConsumers(receiverConfigRabbitMq.getConcurrentConsumers());
            }
            if (receiverConfigRabbitMq.getMaxConcurrentConsumers() != null) {
                //最大消费者数量
                simpleMessageListenerContainer.setMaxConcurrentConsumers(receiverConfigRabbitMq.getMaxConcurrentConsumers());
            }
            simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);//设置确认模式为手工确认
            simpleMessageListenerContainer.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
                byte[] body = message.getBody();
                if (null != body) {
//                    log.debug("RabbitMq接收到消息:" + body.length);
                    try {
                        IMessage msg = new DefaultMessage();
                        msg.setRawData(body);
                        if (StringUtils.isNotEmpty(message.getMessageProperties().getContentEncoding())) {
                            msg.setEncoding(message.getMessageProperties().getContentEncoding());
                        }
                        Map<String, Object> headers = message.getMessageProperties().getHeaders();
                        msg.setFileName(StringUtils.defaultIfEmpty((String) headers.get(receiverConfigRabbitMq.getFileNameField()), msg.getId()));
                        msg.setProperty(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_RABBIT_MQ);
                        msg.setProperty(LOGKEYS.CHANNEL_IN, receiverConfigRabbitMq.getVirtualHost() + ":" + receiverConfigRabbitMq.getTopic());
                        msg.setProperty(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_MQ);
                        msg.setProperty(LOGKEYS.RECEIVER_ID, receiverConfigRabbitMq.getId());
                        IContext ctx = new DefaultContext();
                        ctx.setMessage(msg);

                        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, msg.getId(), null);
                        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_RABBIT_MQ);
                        msgLogInfo.put(LOGKEYS.CHANNEL_IN, receiverConfigRabbitMq.getVirtualHost() + ":" + receiverConfigRabbitMq.getTopic());
                        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
                        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
                        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
                        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
                        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_MQ);
                        msgLogInfo.put(LOGKEYS.RECEIVER_ID, receiverConfigRabbitMq.getId());
                        MsgLogger.info(msgLogInfo.toMap());

                        ReceiverRabbitMqImpl.this.messageHandler.handle(ctx);
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);//确认消息消费成功 
//                            if (false) {
//                                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);//处理失败，扔掉消息
//                            }
                    } catch (Exception e) {
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);//处理失败，仍回队列
                        log.error("RabbitMq处理消息失败：", e);
                        this.destroy();
                    }
                } else {
                    try {
                        log.warn("receiver rabbitMq message body is null:" + message.getMessageProperties().getDeliveryTag());
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);//确认消息消费成功
                    } catch (Exception e) {
                        log.error("receiver rabbitMq message basicAck error", e);
                    }
                }
            });
            simpleMessageListenerContainer.start();
        }
    }
}
