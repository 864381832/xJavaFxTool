package com.xwintop.xTransfer.sender.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.sender.bean.SenderConfigRocketMq;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.service.Sender;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: SenderRocketMqImpl
 * @Description: RocketMq发送类
 * @author: xufeng
 * @date: 2018/12/28 17:04
 */

@Service("senderRocketMq")
@Scope("prototype")
@Getter
@Setter
@Slf4j
public class SenderRocketMqImpl implements Sender {
    private SenderConfigRocketMq senderConfigRocketMq;

    private DefaultMQProducer producer = null;

    @Override
    public Boolean send(IMessage msg, Map params) throws Exception {
        log.debug("SenderRocketMq,taskName:" + params.get(TaskQuartzJob.JOBID));
        if (producer == null) {
            producer = new DefaultMQProducer(senderConfigRocketMq.getGroupName());
            producer.setNamesrvAddr(senderConfigRocketMq.getNamesrvAddr());
            producer.setVipChannelEnabled(false);
            producer.setRetryTimesWhenSendAsyncFailed(10);
            producer.start();
        }
        log.debug("发送RocketMq消息：" + ArrayUtils.getLength(msg.getMessage()));
        try {
            Message message = new Message(senderConfigRocketMq.getTopic(), senderConfigRocketMq.getTags(), msg.getId(), msg.getMessage());
            if (senderConfigRocketMq.getArgs() != null && !senderConfigRocketMq.getArgs().isEmpty()) {
                message.getProperties().putAll(senderConfigRocketMq.getArgs());
            }
            if (StringUtils.isNotEmpty(senderConfigRocketMq.getFileNameField())) {
                message.getProperties().put(senderConfigRocketMq.getFileNameField(), msg.getFileName());
            }
            producer.send(message);

            Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_SENDED, msg.getId(), null);
            msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
            msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
            msgLogInfo.put(LOGKEYS.CHANNEL_OUT_TYPE, LOGVALUES.CHANNEL_TYPE_ROCKET_MQ);
            msgLogInfo.put(LOGKEYS.CHANNEL_OUT, senderConfigRocketMq.getNamesrvAddr() + "/" + senderConfigRocketMq.getTopic());
            msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
            msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
            msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
            msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
            msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, msg.getProperty(LOGKEYS.RECEIVER_TYPE));
            msgLogInfo.put(LOGKEYS.RECEIVER_ID, msg.getProperty(LOGKEYS.RECEIVER_ID));
            MsgLogger.info(msgLogInfo.toMap());
        } catch (Exception e) {
            log.error("SendRocketMq失败：", e);
            this.destroy();
            throw e;
        }
        return true;
    }

    @Override
    public void setSenderConfig(SenderConfig senderConfig) throws Exception {
        this.senderConfigRocketMq = (SenderConfigRocketMq) senderConfig;
    }

    @Override
    public void destroy() throws Exception {
        if (producer != null) {
            producer.shutdown();
            producer = null;
        }
    }
}
