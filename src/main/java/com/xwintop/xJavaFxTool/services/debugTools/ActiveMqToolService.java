package com.xwintop.xJavaFxTool.services.debugTools;

import com.google.gson.Gson;
import com.xwintop.xJavaFxTool.controller.debugTools.ActiveMqToolController;
import com.xwintop.xJavaFxTool.job.ActiveMqToolJob;
import com.xwintop.xJavaFxTool.manager.ScheduleManager;
import com.xwintop.xJavaFxTool.model.ActiveMqToolReceiverTableBean;
import com.xwintop.xJavaFxTool.model.ActiveMqToolTableBean;
import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import javax.jms.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @ClassName: ActiveMqToolService
 * @Description: ActiveMq调试工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:28
 */

@Getter
@Setter
@Slf4j
public class ActiveMqToolService {
	private ActiveMqToolController activeMqToolController;
	private ScheduleManager scheduleManager = new ScheduleManager();
	private Map<String, Message> receiverMessageMap = new HashMap<String, Message>();
	// ConnectionFactory ：连接工厂，JMS 用它创建连接
	private ConnectionFactory connectionFactory = null;
	// Connection ：JMS 客户端到JMS Provider 的连接
	private Connection connection = null;

	public void saveConfigure() throws Exception {
		saveConfigure(ConfigureUtil.getConfigureFile("ActiveMqToolConfigure.properties"));
	}

	public void saveConfigure(File file) throws Exception {
		FileUtils.touch(file);
		PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
		xmlConfigure.clear();
		for (int i = 0; i < activeMqToolController.getTableData().size(); i++) {
			xmlConfigure.setProperty("tableBean" + i, activeMqToolController.getTableData().get(i).getPropertys());
		}
		xmlConfigure.save();
		Platform.runLater(() -> {
			TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
		});
	}

	public void otherSaveConfigureAction() throws Exception {
		String fileName = "ActiveMqToolConfigure.properties";
		File file = FileChooserUtil.chooseSaveFile(fileName, new FileChooser.ExtensionFilter("All File", "*.*"),
				new FileChooser.ExtensionFilter("Properties", "*.properties"));
		if (file != null) {
			saveConfigure(file);
			TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
		}
	}

	public void loadingConfigure() {
		loadingConfigure(ConfigureUtil.getConfigureFile("ActiveMqToolConfigure.properties"));
	}

	public void loadingConfigure(File file) {
		try {
			activeMqToolController.getTableData().clear();
			PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
			xmlConfigure.getKeys().forEachRemaining(new Consumer<String>() {
				@Override
				public void accept(String t) {
					activeMqToolController.getTableData().add(new ActiveMqToolTableBean(xmlConfigure.getString(t)));
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
		for (ActiveMqToolTableBean tableBean : activeMqToolController.getTableData()) {
			if (tableBean.getIsSend()) {
				int sendNumber = Integer.parseInt(tableBean.getSendNumber());
				// Connection ：JMS 客户端到JMS Provider 的连接
				Connection connection = null;
				// TextMessage message;
				// ConnectionFactory ：连接工厂，JMS 用它创建连接
				// 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar
				ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
						activeMqToolController.getUserNameTextField().getText(),
						activeMqToolController.getPasswordTextField().getText(),
						"tcp://" + activeMqToolController.getUrlTextField().getText().trim());
				try {
					// 构造从工厂得到连接对象
					connection = connectionFactory.createConnection();
					// 启动
					connection.start();
					// 获取操作连接（一个发送或接收消息的线程）
					Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
					// 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
					Destination destination = session.createQueue(tableBean.getQueue());
					// 得到消息生成者【发送者】
					MessageProducer producer = session.createProducer(destination);
					// 设置不持久化，此处学习，实际根据项目决定
					producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
					// 构造消息，此处写死，项目就是参数，或者方法获取
					for (int i = 0; i < sendNumber; i++) {
						sendMessage(session, producer, tableBean.getMessageType(), tableBean.getMessage());
					}
					session.commit();
				} catch (Exception e) {
					e.printStackTrace();
					TooltipUtil.showToast("发送失败：" + e.getMessage());
				} finally {
					try {
						if (null != connection)
							connection.close();
					} catch (Throwable ignore) {
					}
				}
			}
		}
	}

	private void sendMessage(Session session, MessageProducer producer, String messageType, String messageText)
			throws Exception {
		Message message = null;
		String[] messageTypeStrings = activeMqToolController.getMessageTypeStrings();
		if (messageTypeStrings[0].equals(messageType)) {
			message = session.createTextMessage(messageText);
		} else if (messageTypeStrings[1].equals(messageType)) {
			message = session.createObjectMessage(messageText);
		} else if (messageTypeStrings[2].equals(messageType)) {
			message = session.createBytesMessage();
			((BytesMessage) message).writeBytes(messageText.getBytes());
		} else if (messageTypeStrings[3].equals(messageType)) {
			message = session.createMapMessage();
			@SuppressWarnings("unchecked")
			Map<String, Object> map = new Gson().fromJson(messageText, Map.class);
			final MapMessage mapMessage = (MapMessage) message;
			map.forEach((String key, Object value) -> {
				try {
					mapMessage.setObject(key, value);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			});
		} else if (messageTypeStrings[4].equals(messageType)) {
			message = session.createStreamMessage();
			((StreamMessage) message).writeString(messageText);
		}
		producer.send(message);
		TooltipUtil.showToast("消息发送成功！！！");
	}

	public boolean runQuartzAction(String quartzType, String cronText, int interval, int repeatCount) throws Exception {
		if ("简单表达式".equals(quartzType)) {
			scheduleManager.runQuartzAction(ActiveMqToolJob.class,this,interval,repeatCount);
		} else if ("Cron表达式".equals(quartzType)) {
			if (StringUtils.isEmpty(cronText)) {
				TooltipUtil.showToast("cron表达式不能为空。");
				return false;
			}
			scheduleManager.runQuartzAction(ActiveMqToolJob.class,this,cronText);
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
		// Session： 一个发送或接收消息的线程
		Session session;
		// Destination ：消息的目的地;消息发送给谁.
		Destination destination;
		// 消费者，消息接收者
		MessageConsumer consumer;
		connectionFactory = new ActiveMQConnectionFactory(activeMqToolController.getUserNameTextField().getText(),
				activeMqToolController.getPasswordTextField().getText(),
				"tcp://" + activeMqToolController.getUrlTextField().getText().trim());
		try {
			if (connection != null) {
				connection.close();
			}
			// 构造从工厂得到连接对象
			connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.FALSE, activeMqToolController
					.getReceiverAcknowledgeModeChoiceBox().getValue().getBean());
			// 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
			String queue = activeMqToolController.getReceiverQueueTextField().getText();
			destination = session.createQueue(queue);
			consumer = session.createConsumer(destination);
			activeMqToolController.getReceiverTableData().clear();
			receiverMessageMap.clear();
			consumer.setMessageListener(new MessageListener() {// 有事务限制
				@Override
				public void onMessage(Message message) {
					addReceiverTableBean(message);
					// try {
					// session.commit();
					// } catch (JMSException e) {
					// e.printStackTrace();
					// }
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			TooltipUtil.showToast(e.getMessage());
		}
	}

	public void receiverMessageStopListenerAction() {
		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException e) {
				log.error(e.getMessage());
				TooltipUtil.showToast(e.getMessage());
			}
		}
	}

	private void addReceiverTableBean(Message message) {
		String queue = activeMqToolController.getReceiverQueueTextField().getText();
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
			ActiveMqToolReceiverTableBean activeMqToolReceiverTableBean = new ActiveMqToolReceiverTableBean(
					message.getJMSMessageID(), queue, messageSring, messageType, timestamp, isAcknowledge);
			activeMqToolController.getReceiverTableData().add(activeMqToolReceiverTableBean);
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
		// Session： 一个发送或接收消息的线程
		Session session;
		// Destination ：消息的目的地;消息发送给谁.
		Destination destination;
		// 消费者，消息接收者
		MessageConsumer consumer;
		connectionFactory = new ActiveMQConnectionFactory(activeMqToolController.getUserNameTextField().getText(),
				activeMqToolController.getPasswordTextField().getText(),
				"tcp://" + activeMqToolController.getUrlTextField().getText().trim());
		try {
			if (connection != null) {
				connection.close();
			}
			// 构造从工厂得到连接对象
			connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			// session = connection.createSession(Boolean.FALSE,
			// Session.AUTO_ACKNOWLEDGE);
			session = connection.createSession(Boolean.FALSE, activeMqToolController
					.getReceiverAcknowledgeModeChoiceBox().getValue().getBean());
			// 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
			String queue = activeMqToolController.getReceiverQueueTextField().getText();
			destination = session.createQueue(queue);
			consumer = session.createConsumer(destination);
			activeMqToolController.getReceiverTableData().clear();
			receiverMessageMap.clear();
			while (true) {
				// 设置接收者接收消息的时间，为了便于测试，这里谁定为100s
				Message message = consumer.receive(1000);
				if (null == message) {
					break;
				}
				addReceiverTableBean(message);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			TooltipUtil.showToast(e.getMessage());
		}
	}

	public ActiveMqToolService(ActiveMqToolController activeMqToolController) {
		super();
		this.activeMqToolController = activeMqToolController;
	}

}
