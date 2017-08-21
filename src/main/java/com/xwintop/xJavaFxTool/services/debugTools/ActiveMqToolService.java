package com.xwintop.xJavaFxTool.services.debugTools;

import java.io.File;
import java.util.Map;
import java.util.function.Consumer;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.StreamMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.google.gson.Gson;
import com.xwintop.xJavaFxTool.job.ActiveMqToolJob;
import com.xwintop.xJavaFxTool.model.ActiveMqToolTableBean;
import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class ActiveMqToolService {
	private TextField urlTextField;
	private TextField userNameTextField;
	private TextField passwordTextField;
	private ObservableList<ActiveMqToolTableBean> tableData;
	private String[] messageTypeStrings;
	private SchedulerFactory sf = new StdSchedulerFactory();
	private String schedulerKeyGroup = "runFileCopy";
	private String schedulerKeyName = "runFileCopy" + System.currentTimeMillis();

	public void saveConfigure() throws Exception {
		saveConfigure(ConfigureUtil.getConfigureFile("ActiveMqToolConfigure.properties"));
	}

	public void saveConfigure(File file) throws Exception {
		FileUtils.touch(file);
		PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
		xmlConfigure.clear();
		for (int i = 0; i < tableData.size(); i++) {
			xmlConfigure.setProperty("tableBean" + i, tableData.get(i).getPropertys());
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
			tableData.clear();
			PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
			xmlConfigure.getKeys().forEachRemaining(new Consumer<String>() {
				@Override
				public void accept(String t) {
					tableData.add(new ActiveMqToolTableBean(xmlConfigure.getString(t)));
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
		for (ActiveMqToolTableBean tableBean : tableData) {
			if (tableBean.getIsSend()) {
				int sendNumber = Integer.parseInt(tableBean.getSendNumber());
				// Connection ：JMS 客户端到JMS Provider 的连接
				Connection connection = null;
				// TextMessage message;
				// ConnectionFactory ：连接工厂，JMS 用它创建连接
				// 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar
				ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(userNameTextField.getText(),
						passwordTextField.getText(), "tcp://" + urlTextField.getText().trim());
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
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean runQuartzAction(String quartzType, String cronText, int interval, int repeatCount) throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(ActiveMqToolJob.class).withIdentity(schedulerKeyName, schedulerKeyGroup)
				.build();
		jobDetail.getJobDataMap().put("activeMqToolService", this);
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

	public ObservableList<ActiveMqToolTableBean> getTableData() {
		return tableData;
	}

	public void setTableData(ObservableList<ActiveMqToolTableBean> tableData) {
		this.tableData = tableData;
	}

	public void setMessageTypeStrings(String[] messageTypeStrings) {
		this.messageTypeStrings = messageTypeStrings;
	}

	public void setUrlTextField(TextField urlTextField) {
		this.urlTextField = urlTextField;
	}

	public void setUserNameTextField(TextField userNameTextField) {
		this.userNameTextField = userNameTextField;
	}

	public void setPasswordTextField(TextField passwordTextField) {
		this.passwordTextField = passwordTextField;
	}

}
