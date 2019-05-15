package com.xwintop.xTransfer.receiver.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.*;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfigHttp;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.receiver.service.Receiver;
import com.xwintop.xTransfer.util.HttpsClientRequestFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ReceiverHttpImpl
 * @Description: Http接收器实现类
 * @author: xufeng
 * @date: 2019/2/25 10:13
 */

//@RestController
@Service("receiverHttp")
@Scope("prototype")
@Slf4j
@Data
public class ReceiverHttpImpl implements Receiver {
    private ReceiverConfigHttp receiverConfigHttp;
    private MessageHandler messageHandler;

    private RestTemplate restTemplate = null;

    @Autowired
    RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private HttpServletRequest request;

    RequestMappingInfo mapping_info = null;

    @Override
    public void receive(Map params) throws Exception {
        log.debug("ReceiverHttpImpl开始收取");
        if (receiverConfigHttp.isController()) {
            if (mapping_info == null) {
                Method targetMethod = ReflectionUtils.findMethod(ReceiverHttpImpl.class, "registerMappingBy" + receiverConfigHttp.getHttpMethod().toUpperCase(), String.class); // 找到处理该路由的方法

                PatternsRequestCondition patternsRequestCondition = new PatternsRequestCondition(receiverConfigHttp.getUrl());
                RequestMethodsRequestCondition requestMethodsRequestCondition = new RequestMethodsRequestCondition(getRequestMethod(receiverConfigHttp.getHttpMethod()));

                mapping_info = new RequestMappingInfo(patternsRequestCondition, requestMethodsRequestCondition, null, null, null, null, null);
                requestMappingHandlerMapping.registerMapping(mapping_info, ReceiverHttpImpl.this, targetMethod); // 注册映射处理
            }
        } else {
            if (restTemplate == null) {
                if (receiverConfigHttp.getUrl().toLowerCase().startsWith("https")) {
                    restTemplate = new RestTemplate(new HttpsClientRequestFactory());
                } else {
                    restTemplate = new RestTemplate();
                }
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, receiverConfigHttp.getContentType());
            receiverConfigHttp.getArgs().forEach((key, value) -> {
                headers.add(String.valueOf(key), String.valueOf(value));
            });
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(receiverConfigHttp.getUrl(), HttpMethod.valueOf(receiverConfigHttp.getHttpMethod()), entity, String.class);
            try {
                IMessage msg = new DefaultMessage();
                List<String> value = responseEntity.getHeaders().get(receiverConfigHttp.getFileNameField());
                String fileName = (CollectionUtils.isEmpty(value) ? null : value.get(0));
                if (StringUtils.isNotEmpty(fileName)) {
                    msg.setFileName(fileName);
                } else {
                    msg.setFileName(msg.getId());
                }
                msg.setRawData(responseEntity.getBody().getBytes());
                this.handleMsg(msg, params);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.debug("ReceiverHttpImpl收取完成");
    }

    @ResponseBody
    public String registerMappingByPOST(@RequestBody String data) {
        Enumeration<String> d = request.getHeaderNames();
        IMessage msg = new DefaultMessage();
        String encoding = request.getCharacterEncoding();
        String fileName = request.getHeader(receiverConfigHttp.getFileNameField());
        if (StringUtils.isNotEmpty(fileName)) {
            msg.setFileName(fileName);
        } else {
            msg.setFileName(msg.getId());
        }
        try {
            msg.setEncoding(encoding);
            msg.setRawData(data.getBytes(encoding));
            this.handleMsg(msg, new HashMap());
        } catch (Exception e) {
            log.error("ReceiverHttpImpl处理错误：", e);
            return "error:" + e.getMessage();
        }
        return "is success!";
    }

    @ResponseBody
    public String registerMappingByGET(@RequestParam String data) {
        IMessage msg = new DefaultMessage();
        String encoding = request.getCharacterEncoding();
        String fileName = request.getHeader(receiverConfigHttp.getFileNameField());
        if (StringUtils.isNotEmpty(fileName)) {
            msg.setFileName(fileName);
        } else {
            msg.setFileName(msg.getId());
        }
        try {
            msg.setEncoding(encoding);
            msg.setRawData(data.getBytes(encoding));
            this.handleMsg(msg, new HashMap());
        } catch (Exception e) {
            log.error("ReceiverHttpImpl处理错误：", e);
            return "error:" + e.getMessage();
        }
        return "is success!";
    }

    private void handleMsg(IMessage msg, Map params) throws Exception {
        IContext ctx = new DefaultContext();
        ctx.setMessage(msg);

        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_RECEIVED, msg.getId(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, LOGVALUES.CHANNEL_TYPE_HTTP);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, receiverConfigHttp.getUrl());
        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, "httpReceiver");
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, this.receiverConfigHttp.getId());

        MsgLogger.info(msgLogInfo.toMap());
        messageHandler.handle(ctx);
    }

    private RequestMethod getRequestMethod(String method) {
        if ("GET".equalsIgnoreCase(method)) {
            return RequestMethod.GET;
        } else if ("POST".equalsIgnoreCase(method)) {
            return RequestMethod.POST;
        } else {
            return RequestMethod.GET;
        }
    }

    @Override
    public void setReceiverConfig(ReceiverConfig receiverConfig) {
        this.receiverConfigHttp = (ReceiverConfigHttp) receiverConfig;
    }

    @Override
    public void destroy() {
        if (!receiverConfigHttp.isController()) {
            if (mapping_info != null) {
                requestMappingHandlerMapping.unregisterMapping(mapping_info);
            }
        }
    }
}
