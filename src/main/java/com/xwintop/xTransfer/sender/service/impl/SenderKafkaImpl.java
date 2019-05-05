package com.xwintop.xTransfer.sender.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.bean.SenderConfigKafka;
import com.xwintop.xTransfer.sender.service.Sender;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;

/**
 * @ClassName: SenderKafkaImpl
 * @Description: Kafka发送类
 * @author: xufeng
 * @date: 2018/6/13 16:07
 */
@Service("senderKafka")
@Scope("prototype")
@Getter
@Setter
@Slf4j
public class SenderKafkaImpl implements Sender {
    private SenderConfigKafka senderConfigKafka;

    private Producer<String, byte[]> producer = null;

    @Override
    public Boolean send(IMessage msg, Map params) throws Exception {
        log.debug("SenderKafka:" + msg.getId());
        if (producer == null) {
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, senderConfigKafka.getServers());
            props.put(ProducerConfig.ACKS_CONFIG, senderConfigKafka.getAcks());
            props.put(ProducerConfig.RETRIES_CONFIG, senderConfigKafka.getRetries());
            props.put(ProducerConfig.BATCH_SIZE_CONFIG, senderConfigKafka.getBatchSize());
            props.put(ProducerConfig.LINGER_MS_CONFIG, senderConfigKafka.getLinger());
            props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, senderConfigKafka.getBufferMemory());
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
            producer = new KafkaProducer<>(props);
        }
        try {
            ProducerRecord producerRecord = new ProducerRecord(senderConfigKafka.getTopic(), msg.getMessage());
            for (Map.Entry<Object, Object> objectObjectEntry : msg.getProperties().entrySet()) {
                producerRecord.headers().add((String) objectObjectEntry.getKey(), ((String) objectObjectEntry.getValue()).getBytes());
            }
            producer.send(producerRecord);
//            producer.send(new ProducerRecord(senderConfigKafka.getTopic(), JSON.toJSONString(msg)));
            producer.flush();

            Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_SENDED, msg.getId(), null);
            msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
            msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
            msgLogInfo.put(LOGKEYS.CHANNEL_OUT_TYPE, "KAFKA");
            msgLogInfo.put(LOGKEYS.CHANNEL_OUT, senderConfigKafka.getServers() + "/" + senderConfigKafka.getTopic());
            msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
            msgLogInfo.put(LOGKEYS.MSG_LENGTH, msg.getRawData().length);
            msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
            msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
            msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, msg.getProperty(LOGKEYS.RECEIVER_TYPE));
            msgLogInfo.put(LOGKEYS.RECEIVER_ID, msg.getProperty(LOGKEYS.RECEIVER_ID));
            MsgLogger.info(msgLogInfo.toMap());
        } catch (Exception e) {
            log.error("SenderKafka错误:", e);
            this.destroy();
            throw e;
        }
        return true;
    }

    @Override
    public void setSenderConfig(SenderConfig senderConfig) throws Exception {
        this.senderConfigKafka = (SenderConfigKafka) senderConfig;
    }

    @Override
    public void destroy() throws Exception {
        if (producer != null) {
            producer.close();
            producer = null;
        }
    }
}
