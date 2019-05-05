package com.xwintop.xTransfer.filter.service.impl;

import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.bean.FilterConfigEncryptDecrypt;
import com.xwintop.xTransfer.filter.enums.CryptosEnum;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: FilterEncryptDecryptImpl
 * @Description: 加密解密操作类
 * @author: xufeng
 * @date: 2018/5/30 11:00
 */
@Service("filterEncryptDecrypt")
@Scope("prototype")
@Data
@Slf4j
public class FilterEncryptDecryptImpl implements Filter {
    private FilterConfigEncryptDecrypt filterConfigEncryptDecrypt;

    @Override
    public void doFilter(IContext ctx, Map params) throws Exception {
        for (IMessage iMessage : ctx.getMessages()) {
            if (StringUtils.isNotBlank(filterConfigEncryptDecrypt.getFileNameFilterRegex())) {
                if (!iMessage.getFileName().matches(filterConfigEncryptDecrypt.getFileNameFilterRegex())) {
                    log.info("Filter:" + filterConfigEncryptDecrypt.getId() + "跳过fileName：" + iMessage.getFileName());
                    continue;
                }
            }
            doFilter(iMessage);
        }
    }

    public void doFilter(IMessage msg) throws Exception {
        if (CryptosEnum.getEnum(filterConfigEncryptDecrypt.getCryptos()) == null) {
            log.error("未找到该编码规则");
            return;
        }
        if ("encrypt".equals(filterConfigEncryptDecrypt.getActionType())) {
            this.encrptyAction(msg);
        } else if ("decrypt".equals(filterConfigEncryptDecrypt.getActionType())) {
            this.decrptyAction(msg);
        } else {
            log.error("加密类型未指定");
        }
    }

    /* 加密 */
    private void encrptyAction(IMessage msg) {
        switch (CryptosEnum.getEnum(filterConfigEncryptDecrypt.getCryptos())) {
            case Ascii:
                msg.setMessage(BinaryCodec.toAsciiBytes(msg.getMessage()));
                break;
            case Base32:
                msg.setMessage(new Base32().encode(msg.getMessage()));
                break;
            case Base64:
                msg.setMessage(Base64.encodeBase64(msg.getMessage()));
                break;
        }
    }

    /* 解密 */
    private void decrptyAction(IMessage msg) {
        switch (CryptosEnum.getEnum(filterConfigEncryptDecrypt.getCryptos())) {
            case Ascii:
                msg.setMessage(BinaryCodec.fromAscii(msg.getMessage()));
                break;
            case Base32:
                msg.setMessage(new Base32().decode(msg.getMessage()));
                break;
            case Base64:
                msg.setMessage(Base64.decodeBase64(msg.getMessage()));
                break;
        }
    }

    @Override
    public void setFilterConfig(FilterConfig filterConfig) throws Exception {
        this.filterConfigEncryptDecrypt = (FilterConfigEncryptDecrypt) filterConfig;
    }

    @Override
    public void destroy() {

    }
}
