package com.xwintop.xTransfer.sender.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.sender.bean.SenderConfigRabbitMq;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.service.Sender;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: SenderRabbitMqImpl
 * @Description: RabbitMq发送类
 * @author: xufeng
 * @date: 2018/6/13 16:07
 */
@Service("senderRabbitMq")
@Scope("prototype")
@Getter
@Setter
@Slf4j
public class SenderRabbitMqImpl implements Sender {
    private SenderConfigRabbitMq senderConfigRabbitMq;

    private RabbitTemplate rabbitTemplate = null;

    @Override
    public Boolean send(IMessage msg, Map params) throws Exception {
        log.debug("SenderRabbitMq,taskName:" + params.get(TaskQuartzJob.JOBID));
        if (rabbitTemplate == null) {
            if (StringUtils.isEmpty(senderConfigRabbitMq.getTopic())) {
                throw new Exception("SenderRabbitMq失败，topic不能为空" + params.get(TaskQuartzJob.JOBID));
            }
            RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
            if (senderConfigRabbitMq.getHost() != null) {
                factory.setHost(senderConfigRabbitMq.getHost());
            }
            factory.setPort(senderConfigRabbitMq.getPort());
            if (senderConfigRabbitMq.getUsername() != null) {
                factory.setUsername(senderConfigRabbitMq.getUsername());
            }
            if (senderConfigRabbitMq.getPassword() != null) {
                factory.setPassword(senderConfigRabbitMq.getPassword());
            }
            if (senderConfigRabbitMq.getVirtualHost() != null) {
                factory.setVirtualHost(senderConfigRabbitMq.getVirtualHost());
            }
            if (senderConfigRabbitMq.getRequestedHeartbeat() != null) {
                factory.setRequestedHeartbeat(senderConfigRabbitMq.getRequestedHeartbeat());
            }

            if (senderConfigRabbitMq.getConnectionTimeout() != null) {
                factory.setConnectionTimeout(senderConfigRabbitMq.getConnectionTimeout());
            }
            factory.afterPropertiesSet();
            CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
                    factory.getObject());
            connectionFactory.setAddresses(senderConfigRabbitMq.getAddresses());
            connectionFactory.setPublisherConfirms(senderConfigRabbitMq.isPublisherConfirms());
            connectionFactory.setPublisherReturns(senderConfigRabbitMq.isPublisherReturns());
            rabbitTemplate = new RabbitTemplate(connectionFactory);
            if (StringUtils.isNotEmpty(senderConfigRabbitMq.getExchange())) {
                rabbitTemplate.setExchange(senderConfigRabbitMq.getExchange());
            }
            rabbitTemplate.setRoutingKey(senderConfigRabbitMq.getTopic());
        }
        log.debug("发送rabbitMq消息：" + ArrayUtils.getLength(msg.getMessage()));
        try {
            MessageProperties messageProperties = new MessageProperties();
            if (senderConfigRabbitMq.getArgs() != null && !senderConfigRabbitMq.getArgs().isEmpty()) {
                messageProperties.getHeaders().putAll(senderConfigRabbitMq.getArgs());
            }
            if(StringUtils.isNotEmpty(senderConfigRabbitMq.getFileNameField())){
                messageProperties.getHeaders().put(senderConfigRabbitMq.getFileNameField(), msg.getFileName());
            }
            messageProperties.setContentType(senderConfigRabbitMq.getContentType());
            messageProperties.setContentLength(ArrayUtils.getLength(msg.getMessage()));
            messageProperties.setContentEncoding(msg.getEncoding());
            Message message = new Message(msg.getMessage(), messageProperties);
            rabbitTemplate.convertAndSend(message);

            Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_SENDED, msg.getId(), null);
            msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
            msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
            msgLogInfo.put(LOGKEYS.CHANNEL_OUT_TYPE, LOGVALUES.CHANNEL_TYPE_RABBIT_MQ);
            msgLogInfo.put(LOGKEYS.CHANNEL_OUT, senderConfigRabbitMq.getHost() + ":" + senderConfigRabbitMq.getPort() + ":" + senderConfigRabbitMq.getVirtualHost() + "/" + senderConfigRabbitMq.getTopic());
            msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
            msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
            msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
            msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
            msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, msg.getProperty(LOGKEYS.RECEIVER_TYPE));
            msgLogInfo.put(LOGKEYS.RECEIVER_ID, msg.getProperty(LOGKEYS.RECEIVER_ID));
            MsgLogger.info(msgLogInfo.toMap());
        } catch (Exception e) {
            log.error("SendRabbitMq失败：", e);
            this.destroy();
            throw e;
        }
        return true;
    }

    @Override
    public void setSenderConfig(SenderConfig senderConfig) throws Exception {
        this.senderConfigRabbitMq = (SenderConfigRabbitMq) senderConfig;
    }

    @Override
    public void destroy() throws Exception {
        if (rabbitTemplate != null) {
            rabbitTemplate.stop();
            ((CachingConnectionFactory) rabbitTemplate.getConnectionFactory()).destroy();
            rabbitTemplate = null;
        }
    }
}
