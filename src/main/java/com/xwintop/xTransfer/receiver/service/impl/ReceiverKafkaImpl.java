package com.xwintop.xTransfer.receiver.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfigKafka;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xcore.util.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;

/**
 * @ClassName: ReceiverKafkaImpl
 * @Description: Kafka接收器实现类
 * @author: xufeng
 * @date: 2018/4/9 11:27
 */
@Service("receiverKafka")
@Scope("prototype")
@Slf4j
public class ReceiverKafkaImpl implements Receiver {
    private ReceiverConfigKafka receiverConfigKafka;
    private MessageHandler messageHandler;

    private KafkaConsumer<String, byte[]> consumer = null;

    private boolean isReceive = false;

    @Override
    public void receive(Map params) throws Exception {
        log.debug("ReceiverKafkaImpl开始收取");
        if (consumer == null) {
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, receiverConfigKafka.getServers());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, receiverConfigKafka.getClientId());
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, receiverConfigKafka.getSessionTimeout());
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
            props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, receiverConfigKafka.getMaxRequestSize());
            props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, receiverConfigKafka.getRequestTimeout());
            props.put("pollTimeoutms", receiverConfigKafka.getPollTimeout());
            props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, receiverConfigKafka.getMaxPollRecords());
            consumer = new KafkaConsumer(props);
            consumer.subscribe(receiverConfigKafka.getTopics());
            log.debug("poll start...");
        }
        if (isReceive) {
            return;
        }
        isReceive = true;
        ConsumerRecords<String, byte[]> records = consumer.poll(receiverConfigKafka.getPollTimeout());
//        Map topics = consumer.listTopics();
//        log.debug("topics = " + topics);
        int count = records.count();
        log.debug("the numbers of topic:" + count);
        while (count > 0) {
            for (ConsumerRecord<String, byte[]> record : records) {
//            log.info("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
//                log.debug("msgValue" + record.value());
                IMessage msg = new DefaultMessage();
                msg.setRawData(record.value());
                record.headers().forEach(header -> {
                    msg.setProperty(header.key(), new String(header.value()));
                });
                if(StringUtils.isEmpty(msg.getFileName())){
                    msg.setFileName(UuidUtil.get32UUID());
                }
                IContext ctx = new DefaultContext();
                ctx.setMessage(msg);

                Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, msg.getId(), null);
                msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, "KAFKA");
                msgLogInfo.put(LOGKEYS.CHANNEL_IN, receiverConfigKafka.getServers() + ":" +receiverConfigKafka.getTopics().toString());
                msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
                msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
                msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
                msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
                msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, "kafkaReceiver");
                msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigKafka.getId());

                MsgLogger.info(msgLogInfo.toMap());

                messageHandler.handle(ctx);
            }
            consumer.commitSync();
            records = consumer.poll(receiverConfigKafka.getPollTimeout());
            count = records.count();
        }
        log.debug("收取kafka结束");
        isReceive = false;
    }

    @Override
    public void setReceiverConfig(ReceiverConfig receiverConfig) {
        this.receiverConfigKafka = (ReceiverConfigKafka) receiverConfig;
    }

    @Override
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void destroy() {
        if (consumer != null) {
            consumer.close();
            consumer = null;
        }
    }
}
