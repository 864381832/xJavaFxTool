package com.xwintop.xTransfer.receiver.service.impl;

import com.ibm.mq.jmqi.system.JmqiCodepage;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;
import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfigIbmMq;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * @ClassName: ReceiverIbmMqImpl
 * @Description: IbmMq接收器
 * @author: xufeng
 * @date: 2018/6/13 16:11
 */

@Service("receiverIbmMq")
@Scope("prototype")
@Slf4j
public class ReceiverIbmMqImpl implements Receiver {
    private ReceiverConfigIbmMq receiverConfigIbmMq;
    private MessageHandler messageHandler;
    private DefaultMessageListenerContainer defaultMessageListenerContainer = null;

    @Override
    public void receive(Map params) throws Exception {
        this.checkInit(params);
    }

    @Override
    public void setReceiverConfig(ReceiverConfig receiverConfig) {
        this.receiverConfigIbmMq = (ReceiverConfigIbmMq) receiverConfig;
    }

    @Override
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void destroy() {
        new Thread(() -> {
            if (defaultMessageListenerContainer != null) {
                try {
                    defaultMessageListenerContainer.stop();
                    defaultMessageListenerContainer.destroy();
                    ((SingleConnectionFactory) defaultMessageListenerContainer.getConnectionFactory()).destroy();
                } catch (Exception e) {
                    log.error("destroy receiver IbmMq error:", e);
                }
                defaultMessageListenerContainer = null;
            }
        }).start();
    }

    private synchronized void checkInit(Map params) throws Exception {
        if (defaultMessageListenerContainer == null) {
            defaultMessageListenerContainer = new DefaultMessageListenerContainer();
            MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
            mqQueueConnectionFactory.setQueueManager(receiverConfigIbmMq.getQueueManagerName());
            if (StringUtils.isNotBlank(receiverConfigIbmMq.getHostName())) {
                mqQueueConnectionFactory.setHostName(receiverConfigIbmMq.getHostName());
                mqQueueConnectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
                mqQueueConnectionFactory.setClientReconnectTimeout(10);
            }
            if (StringUtils.isNotBlank(receiverConfigIbmMq.getChannel())) {
                mqQueueConnectionFactory.setChannel(receiverConfigIbmMq.getChannel());
            }
            if (receiverConfigIbmMq.getPort() != null) {
                mqQueueConnectionFactory.setPort(receiverConfigIbmMq.getPort());
            }
            if (receiverConfigIbmMq.getCCSID() != null) {
                mqQueueConnectionFactory.setCCSID(receiverConfigIbmMq.getCCSID());
            }
            SingleConnectionFactory cachingConnectionFactory = new SingleConnectionFactory();
            if (StringUtils.isEmpty(receiverConfigIbmMq.getUsername())) {
                cachingConnectionFactory.setTargetConnectionFactory(mqQueueConnectionFactory);
            } else {
                UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
                userCredentialsConnectionFactoryAdapter.setUsername(receiverConfigIbmMq.getUsername());
                userCredentialsConnectionFactoryAdapter.setPassword(receiverConfigIbmMq.getPassword());
                userCredentialsConnectionFactoryAdapter.setTargetConnectionFactory(mqQueueConnectionFactory);
                cachingConnectionFactory.setTargetConnectionFactory(userCredentialsConnectionFactoryAdapter);
            }

            defaultMessageListenerContainer.setConnectionFactory(cachingConnectionFactory);
            defaultMessageListenerContainer.setDestinationName(receiverConfigIbmMq.getQueueName());
            defaultMessageListenerContainer.setSessionTransacted(true);
            defaultMessageListenerContainer.setErrorHandler(exception -> {
                log.error("IbmMq接收器ErrorHandler:", exception);
                this.destroy();
            });
            defaultMessageListenerContainer.setExceptionListener(exception -> {
                log.error("IbmMq接收器监听异常:", exception);
                this.destroy();
            });
            if (receiverConfigIbmMq.getConcurrentConsumers() != null) {
                //消费者个数
                defaultMessageListenerContainer.setConcurrentConsumers(receiverConfigIbmMq.getConcurrentConsumers());
            }
            defaultMessageListenerContainer.setMessageListener((MessageListener) (message) -> {
                try {
                    IMessage msg = new DefaultMessage();
                    log.info("received mq message id is:" + msg.getId() + " message：" + message.getClass());
                    if (message instanceof BytesMessage) {
                        ByteArrayOutputStream fout = new ByteArrayOutputStream();
                        byte[] b = new byte[4096];
                        int len = 0;
                        while ((len = ((BytesMessage) message).readBytes(b)) != -1) {
                            fout.write(b, 0, len);
                        }
                        msg.setRawData(fout.toByteArray());
                    } else if (message instanceof TextMessage) {
                        log.info("received mq TextMessage:" + msg.getId());
                        try {
                            if (receiverConfigIbmMq.getCCSID() != null) {
                                JmqiCodepage jmqiCodepage = JmqiCodepage.getJmqiCodepage(null, receiverConfigIbmMq.getCCSID());
                                if (jmqiCodepage != null && jmqiCodepage.charsetId != null) {
                                    msg.setRawData(((TextMessage) message).getText().getBytes(jmqiCodepage.charsetId));
                                } else {
                                    msg.setRawData(((TextMessage) message).getText().getBytes());
                                }
                            } else {
                                msg.setRawData(((TextMessage) message).getText().getBytes());
                            }
//                        log.info("接收消息：" + new String(new String(msg.getRawData(), "UTF-8").getBytes("ISO-8859-1")));
                        } catch (Exception e) {
                            log.error("转码错误", e);
                        }
                    } else if (message instanceof ObjectMessage) {
                        log.info("received mq ObjectMessage:" + msg.getId());
                        Serializable o = ((ObjectMessage) message).getObject();
                        msg.setRawData(SerializationUtils.serialize(o));
                    } else {
                        log.info("received mq JMSMessage:" + msg.getId());
                        try {
                            msg.setRawData(SerializationUtils.serialize((Serializable) message));
                        } catch (Exception e) {
                            log.error("received IbmMq 接收到异常消息msgId:" + msg.getId(), e);
                        }
                        if (msg.getRawData() == null) {
                            msg.setRawData("".getBytes());
                        }
                    }
                    msg.setFileName(StringUtils.defaultIfEmpty(message.getStringProperty(receiverConfigIbmMq.getFileNameField()), msg.getId()));
                    msg.setProperty(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_MQ);
                    msg.setProperty(LOGKEYS.CHANNEL_IN, receiverConfigIbmMq.getQueueManagerName() + ":" + receiverConfigIbmMq.getQueueName());
                    msg.setProperty(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_MQ);
                    msg.setProperty(LOGKEYS.RECEIVER_ID, this.receiverConfigIbmMq.getId());
                    IContext ctx = new DefaultContext();
                    ctx.setMessage(msg);

                    Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, msg.getId(), null);
                    msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_MQ);
                    msgLogInfo.put(LOGKEYS.CHANNEL_IN, receiverConfigIbmMq.getQueueManagerName() + ":" + receiverConfigIbmMq.getQueueName());
                    msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
                    msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
                    msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
                    msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
                    msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, LOGVALUES.RCV_TYPE_MQ);
                    msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigIbmMq.getId());

                    MsgLogger.info(msgLogInfo.toMap());
                    try {
                        messageHandler.handle(ctx);
                        log.info("success put into queue:" + msg.getId());
                    } catch (Exception e) {
                        throw new JMSException("receiverIbmMq handle Exception:" + e.getMessage());
                    }
                } catch (Exception e) {
                    log.error("receiverIbmMq occur exception:", e);
                    throw new RuntimeException(e);
                }
            });
            defaultMessageListenerContainer.setAutoStartup(false);
            defaultMessageListenerContainer.afterPropertiesSet();
            defaultMessageListenerContainer.start();
        }
    }
}
