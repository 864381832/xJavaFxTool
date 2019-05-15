package com.xwintop.xTransfer.receiver.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfigRocketMq;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xcore.util.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ReceiverRocketMqImpl
 * @Description: RocketMq接收器实现类
 * @author: xufeng
 * @date: 2018/12/29 11:27
 */
@Service("receiverRocketMq")
@Scope("prototype")
@Slf4j
public class ReceiverRocketMqImpl implements Receiver {
    private ReceiverConfigRocketMq receiverConfigRocketMq;
    private MessageHandler messageHandler;

    private DefaultMQPushConsumer consumer = null;

    @Override
    public void receive(Map params) throws Exception {
        log.debug("ReceiverRocketMqImpl开始收取");
        if (consumer == null) {
            consumer = new DefaultMQPushConsumer(receiverConfigRocketMq.getGroupName());
            consumer.setNamesrvAddr(receiverConfigRocketMq.getNamesrvAddr());
            consumer.subscribe(receiverConfigRocketMq.getTopic(), receiverConfigRocketMq.getTags());
            // 开启内部类实现监听
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    for (MessageExt messageExt : msgs) {
                        try {
                            log.debug("接收到消息：" + messageExt.getBody().length);
                            IMessage msg = new DefaultMessage();
                            msg.setRawData(messageExt.getBody());
                            msg.setProperty(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_ROCKET_MQ);
                            msg.setProperty(LOGKEYS.CHANNEL_IN, receiverConfigRocketMq.getNamesrvAddr() + ":" + receiverConfigRocketMq.getTopic());
                            msg.setProperty(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_MQ);
                            msg.setProperty(LOGKEYS.RECEIVER_ID, receiverConfigRocketMq.getId());
                            messageExt.getProperties().get(receiverConfigRocketMq.getFileNameField());
                            msg.setFileName(StringUtils.defaultIfEmpty(messageExt.getProperties().get(receiverConfigRocketMq.getFileNameField()), UuidUtil.get32UUID()));
                            IContext ctx = new DefaultContext();
                            ctx.setMessage(msg);

                            Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, msg.getId(), null);
                            msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_ROCKET_MQ);
                            msgLogInfo.put(LOGKEYS.CHANNEL_IN, receiverConfigRocketMq.getNamesrvAddr() + ":" + receiverConfigRocketMq.getTopic());
                            msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
                            msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
                            msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
                            msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
                            msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_MQ);
                            msgLogInfo.put(LOGKEYS.RECEIVER_ID, receiverConfigRocketMq.getId());

                            MsgLogger.info(msgLogInfo.toMap());

                            messageHandler.handle(ctx);
                        } catch (Exception e) {
                            log.error("处理Rocket接收消息失败：", e);
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;//失败回滚
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
            log.debug("ReceiverRocketMqImpl poll start...");
        }
    }

    @Override
    public void setReceiverConfig(ReceiverConfig receiverConfig) {
        this.receiverConfigRocketMq = (ReceiverConfigRocketMq) receiverConfig;
    }

    @Override
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void destroy() {
        if (consumer != null) {
            consumer.shutdown();
            consumer = null;
        }
    }
}
