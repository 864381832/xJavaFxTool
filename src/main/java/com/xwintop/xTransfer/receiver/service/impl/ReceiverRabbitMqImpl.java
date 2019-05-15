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
import com.xwintop.xcore.util.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
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
        if (simpleMessageListenerContainer == null) {
            simpleMessageListenerContainer = new SimpleMessageListenerContainer();
            RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
            if (StringUtils.isNotEmpty(receiverConfigRabbitMq.getHost())) {
                factory.setHost(receiverConfigRabbitMq.getHost());
            }
            factory.setPort(receiverConfigRabbitMq.getPort());
            if (StringUtils.isNotEmpty(receiverConfigRabbitMq.getUsername())) {
                factory.setUsername(receiverConfigRabbitMq.getUsername());
            }
            if (StringUtils.isNotEmpty(receiverConfigRabbitMq.getPassword())) {
                factory.setPassword(receiverConfigRabbitMq.getPassword());
            }
            if (StringUtils.isNotEmpty(receiverConfigRabbitMq.getVirtualHost())) {
                factory.setVirtualHost(receiverConfigRabbitMq.getVirtualHost());
            }
            if (receiverConfigRabbitMq.getRequestedHeartbeat() != null) {
                factory.setRequestedHeartbeat(receiverConfigRabbitMq.getRequestedHeartbeat());
            }
            if (receiverConfigRabbitMq.getConnectionTimeout() != null) {
                factory.setConnectionTimeout(receiverConfigRabbitMq.getConnectionTimeout());
            }
            factory.setAutomaticRecoveryEnabled(true);//如果连接超时，会自动去恢复
            factory.afterPropertiesSet();
            CachingConnectionFactory connectionFactory = new CachingConnectionFactory(factory.getObject());
            if (StringUtils.isNotEmpty(receiverConfigRabbitMq.getAddresses())) {
                //该方法配置多个host，在当前连接host down掉的时候会自动去重连后面的host
                connectionFactory.setAddresses(receiverConfigRabbitMq.getAddresses());
            }
            connectionFactory.setPublisherConfirms(receiverConfigRabbitMq.isPublisherConfirms());
            connectionFactory.setPublisherReturns(receiverConfigRabbitMq.isPublisherReturns());

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
                Map<String, Object> headers = message.getMessageProperties().getHeaders();
                byte[] body = message.getBody();
                if (null != body) {
                    log.debug("RabbitMq接收到消息:" + body.length);
                    try {
                        IMessage msg = new DefaultMessage();
                        msg.setRawData(body);
                        if (StringUtils.isNotEmpty(message.getMessageProperties().getContentEncoding())) {
                            msg.setEncoding(message.getMessageProperties().getContentEncoding());
                        }
                        if (StringUtils.isBlank(msg.getFileName())) {
                            String rabbitMqFileName = ReceiverRabbitMqImpl.this.getFileName(body);
                            if (StringUtils.isEmpty(rabbitMqFileName)) {
                                msg.setFileName(StringUtils.defaultIfEmpty((String) headers.get(receiverConfigRabbitMq.getFileNameField()), UuidUtil.get32UUID()));
                            } else {
                                msg.setFileName(rabbitMqFileName);
                            }
                        }
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
                }
            });
            simpleMessageListenerContainer.start();
        }
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

    public String getFileName(byte[] bytes) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new ByteArrayInputStream(bytes));
            Element bookStore = document.getRootElement();
            Element addInfo = bookStore.element("AddInfo");
            //存放解析节点值得实体
            return addInfo.element("FileName").getStringValue().trim();
        } catch (Exception e) {
            log.debug("从消息中获取文件名失败，非标准客户端生成消息：", e);
        }
        return null;
    }
}
