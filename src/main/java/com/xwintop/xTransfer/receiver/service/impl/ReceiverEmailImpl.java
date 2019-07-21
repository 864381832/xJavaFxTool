package com.xwintop.xTransfer.receiver.service.impl;

import com.sun.mail.util.BASE64DecoderStream;
import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfigEmail;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.xwintop.xTransfer.sender.enums.EmailMethod;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xcore.util.UuidUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * @ClassName: ReceiverEmailImpl
 * @Description: Email接收器实现类
 * @author: xufeng
 * @date: 2018/4/9 11:27
 */
@Service("receiverEmail")
@Scope("prototype")
@Slf4j
@Data
public class ReceiverEmailImpl implements Receiver {
    private ReceiverConfigEmail receiverConfigEmail;
    private MessageHandler messageHandler;

    @Override
    public void receive(Map params) throws Exception {
        log.debug("ReceiverEmailImpl开始收取");
        URLName urln = new URLName(receiverConfigEmail.getProcotol(), receiverConfigEmail.getHost(), receiverConfigEmail.getPort(), "inbox", receiverConfigEmail.getUser(), receiverConfigEmail.getPassword());
        Folder folder = Session.getInstance(new Properties()).getFolder(urln);
        folder.open(Folder.READ_WRITE);
        Message[] messages = folder.getMessages();
        log.info("邮件数量：" + messages.length);
        int mailSize = messages.length;
        if (receiverConfigEmail.getMax() > 0 && receiverConfigEmail.getMax() < mailSize) {
            mailSize = receiverConfigEmail.getMax();
        }
        for (int i = 0; i < mailSize; i++) {
            String from = null;
            String to = null;
            String subject = "no subject";
            Message message = messages[i];
            try {
                Date sendDt = message.getSentDate();
                String sendTime = "";
                if (sendDt != null) {
                    sendTime = DateFormatUtils.format(sendDt, "yyyyMMddHHmmss");
                }
                String s = message.getSubject();
                if (s != null) {
                    subject = MimeUtility.decodeText(s);
                }
                Address[] fromAddrList = message.getFrom();
                if (fromAddrList != null) {
                    from = ((InternetAddress) fromAddrList[0]).getAddress();
                }
                Address[] toAddrList = message.getRecipients(RecipientType.TO);
                if (toAddrList != null) {
                    to = ((InternetAddress) toAddrList[0]).getAddress();
                }
                List msgList = null;
                switch (EmailMethod.getEnum(receiverConfigEmail.getReceiveType())) {
                    case attachment:
                        msgList = this.getAttachFile(message);
                        break;
                    case raw:
                        msgList = this.getRawMessage(message);
                        break;
                    default:
                        msgList = this.getBodyMessage(message);
                        break;
                }
                if (msgList != null && msgList.size() > 0) {
                    this.sendMsgToListeners(msgList, from, to, subject, sendTime, params);
                } else {
                    backupMail(message, receiverConfigEmail.getExcPath());
                    log.error("this mail has no message,from:" + from + ",to:" + to + ",subject:" + subject);
                }
            } catch (Exception exc) {
                backupMail(message, receiverConfigEmail.getExcPath());
                log.error("send msg to queue:", exc);
            } finally {
                // set delete mail
//                message.setFlag(Flags.Flag.DELETED, true);
            }
        }
        folder.close(true);
        log.debug("ReceiverEmailImpl收取完成");
    }

    private void sendMsgToListeners(List msgList, String from, String to, String subject, String sendTime, Map params) throws Exception {
        for (Iterator i = msgList.iterator(); i.hasNext(); ) {
            IMessage msg = (IMessage) i.next();
            if (from != null) {
                msg.getProperties().setProperty("MAIL_FROM", from);
            }
            if (to != null) {
                msg.getProperties().setProperty("MAIL_TO", to);
            }
            if (subject != null) {
                msg.getProperties().setProperty("MAIL_SUBJECT", subject);
            }
            try {
                IContext ctx = new DefaultContext();
                ctx.setMessage(msg);

                Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, msg.getId(), null);
                msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_EMAIL);
                msgLogInfo.put(LOGKEYS.CHANNEL_IN, receiverConfigEmail.getHost() + ":" + receiverConfigEmail.getPort());
                msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
                msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
                msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
                msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
                msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, "emailReceiver");
                msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigEmail.getId());

                MsgLogger.info(msgLogInfo.toMap());
                messageHandler.handle(ctx);
                log.info("put a message to listener:" + msg.getId());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
            }
        }
    }

    private void backupMail(Message message, String backupPath) {
        if (StringUtils.isNotBlank(backupPath)) {
            try {
                File dir = new File(backupPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir.getAbsolutePath() + File.separator + UuidUtil.get32UUID());
                FileOutputStream fo = new FileOutputStream(file);
                message.writeTo(fo);
                fo.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        try {
            String subject = MimeUtility.decodeText(message.getSubject());
            Address[] fromList = message.getFrom();
            String from = "";
            if (fromList != null && fromList.length > 0) {
                from = ((InternetAddress) fromList[0]).getAddress();
            }
            Address[] toList = message.getRecipients(Message.RecipientType.TO);
            String to = "";
            if (toList != null && toList.length > 0) {
                to = ((InternetAddress) toList[0]).getAddress();
            }
            byte[] raw = getRawString(message);
            log.error(new String(raw));
            log.info("finished dump except email,subject is:" + subject + ",from:" + from + ",to:" + to);
        } catch (Exception e) {
            log.error("try to dump a message to log,but have a exception,so we will lose this message.");
            log.error(e.getMessage(), e);
        }
    }

    private byte[] getRawString(Message message) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            message.writeTo(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List getRawMessage(Message message) {
        ArrayList msgList = new ArrayList();
        byte[] raw = getRawString(message);
        if (raw != null) {
            DefaultMessage msg = new DefaultMessage();
            msg.setRawData(raw);
            msgList.add(msg);
        }
        return msgList;
    }

    private List getBodyMessage(Message message) throws Exception {
        String mailMsg = null;
        if (message.isMimeType("text/plain")) {
            mailMsg = ((String) message.getContent());
        } else if (message.isMimeType("text/html")) {
            mailMsg = ((String) message.getContent());
        } else if (message.isMimeType("multipart/alternative")) {
            Multipart mp = (Multipart) message.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++) {
                String contentType = mp.getBodyPart(i).getContentType();
                if (contentType.startsWith("text/plain")) {
                    mailMsg = (String) mp.getBodyPart(i).getContent();
                    break;
                }
            }
        } else {
            return null;
        }
        ArrayList msgList = new ArrayList();
        // added by paulqing
        if (mailMsg != null && !"".equals(mailMsg)) {
            IMessage msg = new DefaultMessage();
            msg.setRawData(mailMsg.getBytes(msg.getEncoding()));
            msgList.add(msg);
        }
        return msgList;
    }

    private List getAttachFile(Part mpart) throws Exception {
        ArrayList attList = new ArrayList();
        log.debug("start get message from attachments ...");
        if (mpart.isMimeType("multipart/*") || mpart.getContentType().startsWith("application")) {
            Multipart mp = null;
            try {
                mp = (Multipart) mpart.getContent();
            } catch (ClassCastException e) {
                if (mpart.getContent() instanceof BASE64DecoderStream) {
                    IMessage msg = new DefaultMessage();
                    msg.setFileName(mpart.getFileName());
                    msg.getProperties().put("tag", mpart.getFileName());
                    msg.setRawData(IOUtils.toByteArray((BASE64DecoderStream) mpart.getContent()));
//                    msg.setRawData(IOUtils.toString((BASE64DecoderStream) mpart.getContent(), "UTF-8").getBytes());
                    attList.add(msg);
                    log.info("mpart.getContentType  catch ClassCastException  " + mpart.getFileName());
                    return attList;
                }
            }

            for (int j = 0, m = mp.getCount(); j < m; j++) {
                Part part = mp.getBodyPart(j);
                String fileName = "";
                String ss = part.getFileName();
                log.debug("part.getFileName():" + ss);
                if (ss != null) {
                    fileName = MimeUtility.decodeText(ss);
                }
                if (j > 1) {
                    fileName += ".epms." + (j - 1);
                }
                if (log.isDebugEnabled()) {
                    log.debug("FileName:" + fileName);
                    log.debug("ContentType:" + part.getContentType());
                    log.debug("Disposition:" + part.getDisposition());
                }
                String disposition = part.getDisposition();
                if ((disposition == null && j > 0) || ((disposition != null && disposition.equals(Part.ATTACHMENT)))) {
                    log.info("the size of attachment:" + part.getSize());
                    log.info("the file name is :" + fileName);
                    IMessage msg = new DefaultMessage();
                    if (fileName != null) {
                        msg.setFileName(fileName);
                        msg.getProperties().put("tag", fileName);
                    }
                    msg.setRawData(IOUtils.toByteArray(part.getInputStream()));
                    attList.add(msg);
                } else if (part.isMimeType("multipart/*")) {
                    List subList = getAttachFile(part);
                    attList.addAll(subList);
                }
            }
        }
        log.debug("end get message from attachments");
        return attList;
    }

    @Override
    public void setReceiverConfig(ReceiverConfig receiverConfig) {
        this.receiverConfigEmail = (ReceiverConfigEmail) receiverConfig;
    }

    @Override
    public void destroy() {

    }
}
