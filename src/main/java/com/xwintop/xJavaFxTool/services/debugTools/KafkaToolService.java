package com.xwintop.xJavaFxTool.services.debugTools;

import com.xwintop.xJavaFxTool.controller.debugTools.KafkaToolController;
import com.xwintop.xJavaFxTool.job.KafkaToolJob;
import com.xwintop.xJavaFxTool.manager.ScheduleManager;
import com.xwintop.xJavaFxTool.model.KafkaToolReceiverTableBean;
import com.xwintop.xJavaFxTool.model.KafkaToolTableBean;
import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.jms.Message;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * @ClassName: KafkaToolService
 * @Description: Kafka调试工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:33
 */

@Getter
@Setter
@Slf4j
public class KafkaToolService {
    private KafkaToolController kafkaToolController;
    private String fileName = "KafkaToolConfigure.properties";
    private ScheduleManager scheduleManager = new ScheduleManager();
    private Map<String, Message> receiverMessageMap = new HashMap<String, Message>();

    public void saveConfigure() throws Exception {
        saveConfigure(ConfigureUtil.getConfigureFile(fileName));
    }

    public void saveConfigure(File file) throws Exception {
        FileUtils.touch(file);
        PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
        xmlConfigure.clear();
        for (int i = 0; i < kafkaToolController.getTableData().size(); i++) {
            xmlConfigure.setProperty("tableBean" + i, kafkaToolController.getTableData().get(i).getPropertys());
        }
        xmlConfigure.save();
        Platform.runLater(() -> {
            TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
        });
    }

    public void otherSaveConfigureAction() throws Exception {
        File file = FileChooserUtil.chooseSaveFile(fileName, new FileChooser.ExtensionFilter("All File", "*.*"),
                new FileChooser.ExtensionFilter("Properties", "*.properties"));
        if (file != null) {
            saveConfigure(file);
            TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
        }
    }

    public void loadingConfigure() {
        loadingConfigure(ConfigureUtil.getConfigureFile(fileName));
    }

    public void loadingConfigure(File file) {
        try {
            kafkaToolController.getTableData().clear();
            PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
            xmlConfigure.getKeys().forEachRemaining(new Consumer<String>() {
                @Override
                public void accept(String t) {
                    kafkaToolController.getTableData().add(new KafkaToolTableBean(xmlConfigure.getString(t)));
                }
            });
        } catch (Exception e) {
            try {
                TooltipUtil.showToast("加载配置失败：" + e.getMessage());
            } catch (Exception e2) {
            }
        }
    }

    public void loadingConfigureAction() {
        File file = FileChooserUtil.chooseFile(new FileChooser.ExtensionFilter("All File", "*.*"),
                new FileChooser.ExtensionFilter("Properties", "*.properties"));
        if (file != null) {
            loadingConfigure(file);
        }
    }

    public void sendAction() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaToolController.getUrlTextField().getText().trim());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        for (KafkaToolTableBean tableBean : kafkaToolController.getTableData()) {
            if (tableBean.getIsSend()) {
                int sendNumber = Integer.parseInt(tableBean.getSendNumber());
                Producer<String, String> producer = new KafkaProducer<>(props);
                try {
                    for (int i = 0; i < sendNumber; i++) {
                        producer.send(new ProducerRecord<>(tableBean.getQueue(), null, tableBean.getMessage()));
                    }
                } catch (Exception e) {
                    log.error("发送失败：", e);
                    TooltipUtil.showToast("发送失败：" + e.getMessage());
                } finally {
                    producer.close();
                }
            }
        }
    }

    public boolean runQuartzAction(String quartzType, String cronText, int interval, int repeatCount) throws Exception {
        if ("简单表达式".equals(quartzType)) {
            scheduleManager.runQuartzAction(KafkaToolJob.class, this, interval, repeatCount);
        } else if ("Cron表达式".equals(quartzType)) {
            if (StringUtils.isEmpty(cronText)) {
                TooltipUtil.showToast("cron表达式不能为空。");
                return false;
            }
            scheduleManager.runQuartzAction(KafkaToolJob.class, this, cronText);
        }
        return true;
    }

    public boolean stopQuartzAction() throws Exception {
        scheduleManager.stopQuartzAction();
        return true;
    }

    /**
     * @Title: receiverMessageListenerAction
     * @Description: receiver端监听消息
     */
    public void receiverMessageListenerAction() {
    }

    public void receiverMessageStopListenerAction() {
//        if (connection != null) {
//            try {
//                connection.close();
//            } catch (JMSException e) {
//                log.error(e.getMessage());
//                TooltipUtil.showToast(e.getMessage());
//            }
//        }
    }

    /**
     * @Title: receiverPullMessageAction
     * @Description: receiver端拉取消息
     */
    @FXML
    public void receiverPullMessageAction() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaToolController.getUrlTextField().getText().trim());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaToolController.getGroupIdTextField().getText().trim());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 180000);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 5242880);
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 305000);
        props.put("pollTimeoutms", 5000);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);
        KafkaConsumer consumer = new KafkaConsumer(props);
        consumer.subscribe(Arrays.asList(kafkaToolController.getReceiverQueueTextField().getText().trim()));
        ConsumerRecords<String, String> records = consumer.poll(0);
        for (ConsumerRecord<String, String> record : records) {
//            log.info("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
            log.info("msgValue" + record.value());
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(record.timestamp()));
            KafkaToolReceiverTableBean kafkaToolReceiverTableBean = new KafkaToolReceiverTableBean(
                    record.key(), record.topic(), record.value(), "String", timestamp, false);
            kafkaToolController.getReceiverTableData().add(kafkaToolReceiverTableBean);
        }
        consumer.commitAsync();
    }

    public KafkaToolService(KafkaToolController kafkaToolController) {
        super();
        this.kafkaToolController = kafkaToolController;
    }

}
