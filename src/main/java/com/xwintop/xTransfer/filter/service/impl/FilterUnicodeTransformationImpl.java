package com.xwintop.xTransfer.filter.service.impl;

import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.bean.FilterConfigUnicodeTransformation;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import com.xwintop.xTransfer.util.Common;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: FilterUnicodeTransformationImpl
 * @Description: 编码转换工具类
 * @author: xufeng
 * @date: 2019/8/21 18:07
 */

@Service("filterUnicodeTransformation")
@Scope("prototype")
@Data
@Slf4j
public class FilterUnicodeTransformationImpl implements Filter {
    private FilterConfigUnicodeTransformation filterConfigUnicodeTransformation;

    @Override
    public void doFilter(IContext ctx, Map params) throws Exception {
        for (IMessage iMessage : ctx.getMessages()) {
            if (StringUtils.isNotBlank(filterConfigUnicodeTransformation.getFileNameFilterRegex())) {
                String fileNameFilterRegexGroup = filterConfigUnicodeTransformation.getFileNameFilterRegexGroup();
                if (StringUtils.isEmpty(fileNameFilterRegexGroup)) {
                    fileNameFilterRegexGroup = "defaultRegexGroup";
                }
                if ("?!".equals(filterConfigUnicodeTransformation.getFileNameFilterRegex())) {
                    if (iMessage.checkFileNameFilterRegexGroup(fileNameFilterRegexGroup)) {
                        log.info("Filter:" + filterConfigUnicodeTransformation.getId() + "跳过fileName：" + iMessage.getFileName());
                        continue;
                    }
                } else {
                    if (!iMessage.getFileName().matches(filterConfigUnicodeTransformation.getFileNameFilterRegex())) {
                        log.info("Filter:" + filterConfigUnicodeTransformation.getId() + "跳过fileName：" + iMessage.getFileName());
                        continue;
                    }
                    iMessage.addFileNameFilterRegexGroup(fileNameFilterRegexGroup);
                }
            }
            doFilter(iMessage);
        }
    }

    public void doFilter(IMessage msg) throws Exception {
        if (StringUtils.isNotEmpty(filterConfigUnicodeTransformation.getOldEncoding())) {
            if ("AUTO".equalsIgnoreCase(filterConfigUnicodeTransformation.getOldEncoding())) {
                String charset = Common.detectFileCharset(msg.getMessage());
                log.info("检测到文件:" + msg.getFileName() + "编码：" + charset);
                msg.setEncoding(charset);
            } else {
                msg.setEncoding(filterConfigUnicodeTransformation.getOldEncoding());
            }
        }
        if (StringUtils.isNotEmpty(filterConfigUnicodeTransformation.getNewEncoding()) && !filterConfigUnicodeTransformation.getNewEncoding().equalsIgnoreCase(msg.getEncoding())) {
            byte[] newBytes = msg.getMessageByString().getBytes(filterConfigUnicodeTransformation.getNewEncoding());
            msg.setMessage(newBytes);
            msg.setEncoding(filterConfigUnicodeTransformation.getNewEncoding());
        }
    }

    @Override
    public void setFilterConfig(FilterConfig filterConfig) throws Exception {
        this.filterConfigUnicodeTransformation = (FilterConfigUnicodeTransformation) filterConfig;
    }

    @Override
    public void destroy() {
    }
}
