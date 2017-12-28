package com.xwintop.xJavaFxTool.services.debugTools;

import com.google.gson.Gson;
import com.xwintop.xJavaFxTool.controller.debugTools.KafkaToolController;
import com.xwintop.xJavaFxTool.job.KafkaToolJob;
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
import lombok.extern.log4j.Log4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.jms.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

@Getter
@Setter
@Log4j
public class KafkaToolService {
    private KafkaToolController kafkaToolController;
    private String fileName = "KafkaToolConfigure.properties";
    private SchedulerFactory sf = new StdSchedulerFactory();
    private String schedulerKeyGroup = "runFileCopy";
    private String schedulerKeyName = "runFileCopy" + System.currentTimeMillis();
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
        props.put("bootstrap.servers", kafkaToolController.getUrlTextField().getText().trim());
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        for (KafkaToolTableBean tableBean : kafkaToolController.getTableData()) {
            if (tableBean.getIsSend()) {
                int sendNumber = Integer.parseInt(tableBean.getSendNumber());
//                ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
//                        kafkaToolController.getUserNameTextField().getText(),
//                        kafkaToolController.getPasswordTextField().getText(),
//                        "tcp://" + kafkaToolController.getUrlTextField().getText().trim());
                Producer<String, String> producer = new KafkaProducer<>(props);
                try {
                    for (int i = 0; i < sendNumber; i++) {
                        producer.send(new ProducerRecord<>(tableBean.getQueue(), tableBean.getMessageType() , tableBean.getMessage()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    TooltipUtil.showToast("发送失败：" + e.getMessage());
                } finally {
                   producer.close();
                }
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean runQuartzAction(String quartzType, String cronText, int interval, int repeatCount) throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(KafkaToolJob.class).withIdentity(schedulerKeyName, schedulerKeyGroup)
                .build();
        jobDetail.getJobDataMap().put("kafkaToolService", this);
        ScheduleBuilder scheduleBuilder = null;
        if ("简单表达式".equals(quartzType)) {
            scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval)// 时间间隔
                    .withRepeatCount(repeatCount);// 重复次数（将执行6次）
        } else if ("Cron表达式".equals(quartzType)) {
            if (StringUtils.isEmpty(cronText)) {
                TooltipUtil.showToast("cron表达式不能为空。");
                return false;
            }
            scheduleBuilder = CronScheduleBuilder.cronSchedule(cronText);
        }
        // 描叙触发Job执行的时间触发规则,Trigger实例化一个触发器
        Trigger trigger = TriggerBuilder.newTrigger()// 创建一个新的TriggerBuilder来规范一个触发器
                .withIdentity(schedulerKeyName, schedulerKeyGroup)// 给触发器一个名字和组名
                .startNow()// 立即执行
                .withSchedule(scheduleBuilder).build();// 产生触发器

        // 运行容器，使用SchedulerFactory创建Scheduler实例
        Scheduler scheduler = sf.getScheduler();
        // 向Scheduler添加一个job和trigger
        scheduler.scheduleJob(jobDetail, trigger);
        if (!scheduler.isStarted()) {
            scheduler.start();
        }
        return true;
    }

    public boolean stopQuartzAction() throws Exception {
        Scheduler sched = sf.getScheduler();
        sched.unscheduleJob(new TriggerKey(schedulerKeyName, schedulerKeyGroup));
        sched.deleteJob(new JobKey(schedulerKeyName, schedulerKeyGroup));
        return true;
    }

    /**
     * @Title: receiverMessageListenerAction
     * @Description: receiver端监听消息
     */
    public void receiverMessageListenerAction() {
//        // Session： 一个发送或接收消息的线程
//        Session session;
//        // Destination ：消息的目的地;消息发送给谁.
//        Destination destination;
//        // 消费者，消息接收者
//        MessageConsumer consumer;
//        connectionFactory = new ActiveMQConnectionFactory(kafkaToolController.getUserNameTextField().getText(),
//                kafkaToolController.getPasswordTextField().getText(),
//                "tcp://" + kafkaToolController.getUrlTextField().getText().trim());
//        try {
//            if (connection != null) {
//                connection.close();
//            }
//            // 构造从工厂得到连接对象
//            connection = connectionFactory.createConnection();
//            // 启动
//            connection.start();
//            // 获取操作连接
//            session = connection.createSession(Boolean.FALSE, kafkaToolController
//                    .getReceiverAcknowledgeModeChoiceBox().getValue().getBean());
//            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在Kafka的console配置
//            String queue = kafkaToolController.getReceiverQueueTextField().getText();
//            destination = session.createQueue(queue);
//            consumer = session.createConsumer(destination);
//            kafkaToolController.getReceiverTableData().clear();
//            receiverMessageMap.clear();
//            consumer.setMessageListener(new MessageListener() {// 有事务限制
//                @Override
//                public void onMessage(Message message) {
//                    addReceiverTableBean(message);
//                    // try {
//                    // session.commit();
//                    // } catch (JMSException e) {
//                    // e.printStackTrace();
//                    // }
//                }
//            });
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            TooltipUtil.showToast(e.getMessage());
//        }
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

    private void addReceiverTableBean(Message message) {
        String queue = kafkaToolController.getReceiverQueueTextField().getText();
        String messageType = "TextMessage";
        String messageSring = null;
        boolean isAcknowledge = false;
        try {
            if (message instanceof TextMessage) {
                messageType = "TextMessage";
                messageSring = ((TextMessage) message).getText();
            } else if (message instanceof ObjectMessage) {
                messageType = "ObjectMessage";
                messageSring = ((ObjectMessage) message).getObject().toString();
            } else if (message instanceof BytesMessage) {
                messageType = "BytesMessage";
                messageSring = new String(((ActiveMQBytesMessage) message).getContent().getData());
            } else if (message instanceof MapMessage) {
                messageType = "MapMessage";
                messageSring = ((ActiveMQMapMessage) message).getContentMap().toString();
            } else if (message instanceof StreamMessage) {
                messageType = "StreamMessage";
                messageSring = ((StreamMessage) message).readString();
            }
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(message.getJMSTimestamp()));
            KafkaToolReceiverTableBean kafkaToolReceiverTableBean = new KafkaToolReceiverTableBean(
                    message.getJMSMessageID(), queue, messageSring, messageType, timestamp, isAcknowledge);
            kafkaToolController.getReceiverTableData().add(kafkaToolReceiverTableBean);
            receiverMessageMap.put(message.getJMSMessageID(), message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * @Title: receiverPullMessageAction
     * @Description: receiver端拉取消息
     */
    @FXML
    public void receiverPullMessageAction() {
//        // Session： 一个发送或接收消息的线程
//        Session session;
//        // Destination ：消息的目的地;消息发送给谁.
//        Destination destination;
//        // 消费者，消息接收者
//        MessageConsumer consumer;
//        connectionFactory = new ActiveMQConnectionFactory(kafkaToolController.getUserNameTextField().getText(),
//                kafkaToolController.getPasswordTextField().getText(),
//                "tcp://" + kafkaToolController.getUrlTextField().getText().trim());
//        try {
//            if (connection != null) {
//                connection.close();
//            }
//            // 构造从工厂得到连接对象
//            connection = connectionFactory.createConnection();
//            // 启动
//            connection.start();
//            // 获取操作连接
//            // session = connection.createSession(Boolean.FALSE,
//            // Session.AUTO_ACKNOWLEDGE);
//            session = connection.createSession(Boolean.FALSE, kafkaToolController
//                    .getReceiverAcknowledgeModeChoiceBox().getValue().getBean());
//            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在Kafka的console配置
//            String queue = kafkaToolController.getReceiverQueueTextField().getText();
//            destination = session.createQueue(queue);
//            consumer = session.createConsumer(destination);
//            kafkaToolController.getReceiverTableData().clear();
//            receiverMessageMap.clear();
//            while (true) {
//                // 设置接收者接收消息的时间，为了便于测试，这里谁定为100s
//                Message message = consumer.receive(1000);
//                if (null == message) {
//                    break;
//                }
//                addReceiverTableBean(message);
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            TooltipUtil.showToast(e.getMessage());
//        }
    }

    public KafkaToolService(KafkaToolController kafkaToolController) {
        super();
        this.kafkaToolController = kafkaToolController;
    }

}
