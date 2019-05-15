package com.xwintop.xTransfer.sender.service.impl;

import com.xwintop.xTransfer.common.MsgLogger;
import com.xwintop.xTransfer.common.model.LOGKEYS;
import com.xwintop.xTransfer.common.model.LOGVALUES;
import com.xwintop.xTransfer.common.model.Msg;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.task.quartz.TaskQuartzJob;
import com.xwintop.xTransfer.sender.bean.SenderConfigHttp;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.sender.service.Sender;
import com.xwintop.xTransfer.util.HttpsClientRequestFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

/**
 * @ClassName: SenderHttpImpl
 * @Description: Http发送实现类
 * @author: xufeng
 * @date: 2019/2/25 13:49
 */

@Service("senderHttp")
@Scope("prototype")
@Getter
@Setter
@Slf4j
public class SenderHttpImpl implements Sender {
    private SenderConfigHttp senderConfigHttp;

    private RestTemplate restTemplate = null;

    @Override
    public Boolean send(IMessage msg, Map params) throws Exception {
        log.debug("SenderHttp:" + msg.getId());

        if (restTemplate==null){

            if (senderConfigHttp.getUrl().toLowerCase().startsWith("https")) {
                restTemplate = new RestTemplate(new HttpsClientRequestFactory());
            }else {
                restTemplate = new RestTemplate();
            }

        }

        String url = senderConfigHttp.getUrl();
        // 发生4XX错误时抛出异常，让框架可以重新发送报文
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                switch (response.getRawStatusCode()){
                    case 400:
                        throw new RuntimeException("Network can't connect to :"+ url);
                    case 401:
                        throw new RuntimeException("Unauthorized: "+ url);
                    case 403:
                        throw new RuntimeException("Forbidden :" + url);
                    case 404:
                        throw new RuntimeException("Resource Not Found :" + url);
                }
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {

            }
        });


        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, senderConfigHttp.getContentType());
        senderConfigHttp.getArgs().forEach((key, value) -> {
            headers.add(String.valueOf(key), String.valueOf(value));
        });
        HttpEntity<String> entity = new HttpEntity<>(msg.getMessageByString(), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.valueOf(senderConfigHttp.getHttpMethod()), entity, String.class);

        Msg msgLogInfo = new Msg(LOGVALUES.EVENT_MSG_SENDED, msg.getId(), null);
        msgLogInfo.put(LOGKEYS.CHANNEL_IN_TYPE, msg.getProperty(LOGKEYS.CHANNEL_IN_TYPE));
        msgLogInfo.put(LOGKEYS.CHANNEL_IN, msg.getProperty(LOGKEYS.CHANNEL_IN));
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT_TYPE, LOGVALUES.CHANNEL_TYPE_HTTP);
        msgLogInfo.put(LOGKEYS.CHANNEL_OUT, senderConfigHttp.getUrl());
        msgLogInfo.put(LOGKEYS.MSG_TAG, msg.getFileName());
        msgLogInfo.put(LOGKEYS.MSG_LENGTH, ArrayUtils.getLength(msg.getMessage()));
        msgLogInfo.put(LOGKEYS.JOB_ID, params.get(TaskQuartzJob.JOBID));
        msgLogInfo.put(LOGKEYS.JOB_SEQ, params.get(TaskQuartzJob.JOBSEQ));
        msgLogInfo.put(LOGKEYS.RECEIVER_TYPE, msg.getProperty(LOGKEYS.RECEIVER_TYPE));
        msgLogInfo.put(LOGKEYS.RECEIVER_ID, msg.getProperty(LOGKEYS.RECEIVER_ID));
        MsgLogger.info(msgLogInfo.toMap());

        if (senderConfigHttp.isUpdateMsgWithHttpResp()) {
            String resBody = responseEntity.getBody();
            String encoding = "UTF-8" ;
            msg.setMessage(resBody.getBytes(encoding));
            msg.setEncoding(encoding);
        }

        return true;
    }

    @Override
    public void setSenderConfig(SenderConfig senderConfig) throws Exception {
        this.senderConfigHttp = (SenderConfigHttp) senderConfig;
    }

    @Override
    public void destroy() throws Exception {

    }
}
