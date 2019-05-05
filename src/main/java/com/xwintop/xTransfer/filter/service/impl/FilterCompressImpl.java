package com.xwintop.xTransfer.filter.service.impl;

import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.bean.FilterConfigCompress;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName: FilterCompressImpl
 * @Description: 消息压缩操作
 * @author: xufeng
 * @date: 2018/6/26 15:03
 */

@Service("filterCompress")
@Scope("prototype")
@Data
@Slf4j
public class FilterCompressImpl implements Filter {
    private FilterConfigCompress filterConfigCompress;

    @Override
    public void doFilter(IContext ctx, Map params) throws Exception {
        String zipType = null;
        IMessage msg = ctx.getMessages().get(0);
        Map args = filterConfigCompress.getArgs();
        log.debug("执行了压缩动作");
        if (msg != null) {
            zipType = msg.getProperties().getProperty("COMPRESS_METHOD");
            if (StringUtils.isBlank(zipType)) {
                zipType = StringUtils.defaultIfBlank(filterConfigCompress.getMethod(), "zip");
            }
        }
        if (StringUtils.isBlank(zipType)) {
            zipType = "zip";
        }
        log.debug("begin unzip data,zip type is:" + zipType);
        byte[] zippedData;
        if (zipType.equalsIgnoreCase("zip")) {
            List rawList = ctx.getMessages();
            zippedData = this.zip(rawList);
        } else {
            zippedData = this.gzip(msg);
        }
        msg.getProperties().put("COMPRESS_METHOD", zipType);
        if (filterConfigCompress.isAddPostfixName()) {
            msg.setFileName(msg.getFileName() + "." + zipType);
        }
        log.debug("success zip data.");
        msg.setMessage(zippedData);
        ctx.setMessage(msg);
    }

    private byte[] zip(List list) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (list != null && list.size() > 0) {
            ZipOutputStream zout = new ZipOutputStream(baos);
            for (Iterator it = list.iterator(); it.hasNext(); ) {
                IMessage msg = (IMessage) it.next();
                this.zip(msg, zout);
            }
            zout.close();
            zout = null;
        } else {
            log.warn("no message for compress...");
        }
        byte zippedData[] = baos.toByteArray();
        baos.close();
        baos = null;
        return zippedData;
    }


    private void zip(IMessage msg, ZipOutputStream zout) throws Exception {
        zout.putNextEntry(new ZipEntry(msg.getFileName()));
        zout.write(msg.getMessage());
    }

    private byte[] gzip(IMessage msg) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gout = new GZIPOutputStream(baos);
        gout.write(msg.getMessage());
        gout.close();
        gout = null;
        byte[] zipData = baos.toByteArray();
        baos.close();
        baos = null;
        return zipData;
    }

    @Override
    public void setFilterConfig(FilterConfig filterConfig) throws Exception {
        this.filterConfigCompress = (FilterConfigCompress) filterConfig;
    }

    @Override
    public void destroy() {

    }
}
