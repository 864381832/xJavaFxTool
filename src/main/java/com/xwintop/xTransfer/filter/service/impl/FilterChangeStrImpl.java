package com.xwintop.xTransfer.filter.service.impl;

import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.bean.FilterConfigChangeStr;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: FilterChangeStrImpl
 * @Description: 改变内容操作
 * @author: xufeng
 * @date: 2019/11/22 16:02
 */

@Service("filterChangeStr")
@Scope("prototype")
@Data
@Slf4j
public class FilterChangeStrImpl implements Filter {
    private FilterConfigChangeStr filterConfigChangeStr;

    @Override
    public void doFilter(IContext ctx, Map params) throws Exception {
        for (IMessage iMessage : ctx.getMessages()) {
            if (StringUtils.isNotBlank(filterConfigChangeStr.getFileNameFilterRegex())) {
                String fileNameFilterRegexGroup = filterConfigChangeStr.getFileNameFilterRegexGroup();
                if (StringUtils.isEmpty(fileNameFilterRegexGroup)) {
                    fileNameFilterRegexGroup = "defaultRegexGroup";
                }
                if ("?!".equals(filterConfigChangeStr.getFileNameFilterRegex())) {
                    if (iMessage.checkFileNameFilterRegexGroup(fileNameFilterRegexGroup)) {
                        log.info("Filter:" + filterConfigChangeStr.getId() + "跳过fileName：" + iMessage.getFileName());
                        continue;
                    }
                } else {
                    if (!iMessage.getFileName().matches(filterConfigChangeStr.getFileNameFilterRegex())) {
                        log.info("Filter:" + filterConfigChangeStr.getId() + "跳过fileName：" + iMessage.getFileName());
                        continue;
                    }
                    iMessage.addFileNameFilterRegexGroup(fileNameFilterRegexGroup);
                }
            }
            doFilter(iMessage);
        }
    }

    public void doFilter(IMessage msg) throws Exception {
        if (filterConfigChangeStr.isHalfChinese()) {
            halfChinese(msg);
        }
        if (StringUtils.isNotEmpty(filterConfigChangeStr.getOrgStr())) {
            String encoding = filterConfigChangeStr.getEncoding();
            if (StringUtils.isEmpty(encoding)) {
                encoding = msg.getEncoding();
            }
            String msgStr = new String(msg.getMessage(), encoding);
            String orgStr = filterConfigChangeStr.getOrgStr();
            String newStr = filterConfigChangeStr.getNewStr();
            if (newStr == null) {
                newStr = "";
            }
            if (filterConfigChangeStr.isUseRegex()) {
                msgStr = msgStr.replaceAll(orgStr, newStr);
            } else {
                msgStr = msgStr.replace(orgStr, newStr);
            }
            msg.setMessage(msgStr.getBytes(encoding));
            log.info(filterConfigChangeStr.getId() + " replacement success! fileName:" + msg.getFileName());
        }
        log.info(filterConfigChangeStr.getId() + " FilterChangeStr完成：" + msg.getFileName());
    }

    @Override
    public void setFilterConfig(FilterConfig filterConfig) throws Exception {
        this.filterConfigChangeStr = (FilterConfigChangeStr) filterConfig;
    }

    @Override
    public void destroy() {
    }

    private void halfChinese(IMessage msg) {
        byte[] bb = msg.getMessage();
        int bbLen = bb.length;
        byte[] btt = new byte[bbLen];
        int ii = 0;
        byte[] DEFAULTVALUE = " ".getBytes();
        byte STARTVALUE = (byte) 0x81;
        byte ENDVALUE = (byte) 0xFE;
        for (int current = 0; current < bbLen; current++) {
            if (bb[current] >= STARTVALUE && bb[current] <= ENDVALUE) {
                ii++;
                if (ii % 2 == 0) {
                    ii = 0;
                }
                btt[current] = bb[current];
            } else {
                if (ii == 1) {
                    ii = 0;
                    btt[current - 1] = DEFAULTVALUE[0];
                }
                btt[current] = bb[current];
            }
        }
        msg.setMessage(btt);
        log.info(filterConfigChangeStr.getId() + " halfChinese success! fileName:" + msg.getFileName());
    }

}
