package com.xwintop.xTransfer.filter.service.impl;

import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.bean.FilterConfigDecompress;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @ClassName: FilterDecompressImpl
 * @Description: 消息解压操作
 * @author: xufeng
 * @date: 2018/5/29 16:40
 */
@Service("filterDecompress")
@Scope("prototype")
@Data
@Slf4j
public class FilterDecompressImpl implements Filter {
    private FilterConfigDecompress filterConfigDecompress;

    @Override
    public void doFilter(IContext ctx, Map params) throws Exception {
        IMessage msg = ctx.getMessages().get(0);
        doFilter(msg, ctx);
    }

    private void doFilter(IMessage msg, IContext ctx) throws Exception {
        Map args = filterConfigDecompress.getArgs();
        log.debug("执行了解压动作");
        String zipType = filterConfigDecompress.getMethod();
        String zipEntryEncoding = msg.getProperties().getProperty("ZIP_ENTRY_ENCODING");
        if ((zipEntryEncoding == null || "".equals(zipEntryEncoding.trim())) && StringUtils.isNotBlank(filterConfigDecompress.getEncoding())) {
            zipEntryEncoding = filterConfigDecompress.getEncoding();
        }
        if (StringUtils.isBlank(zipType)) {
            zipType = StringUtils.defaultIfBlank(msg.getProperties().getProperty("COMPRESS_METHOD"), "zip");
        }
        log.debug("begin unzip data,zip type is:" + zipType);
        if ("zip".equalsIgnoreCase(zipType)) {
            List list = this.unzip(msg, zipEntryEncoding);
            log.debug("list size is:" + list.size());
//            if (args.get("oneMessage") != null && args.get("oneMessage").equals("true") && list.size() == 1) {
//                ctx.setMessage((IMessage) list.get(0));
                ctx.setMessages(list);
//            } else {
//                ctx.setMessages(list);
//            }
        } else {
            byte[] zippedData = this.ungzip(msg);
            if (zipEntryEncoding != null && !"".equals(zipEntryEncoding.trim())) {
                msg.setEncoding(zipEntryEncoding);
            }
            msg.getProperties().put("COMPRESS_METHOD", zipType);
            log.debug("success unzip data.");
            msg.setMessage(zippedData);
            ctx.setMessage(msg);
        }
    }

    private List unzip(IMessage msg, String zipEntryEncoding) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(msg.getMessage());
        ZipInputStream zin = new ZipInputStream(bais);
        ZipEntry zipEntry;
        List unzipList = new ArrayList();
        ByteArrayOutputStream baos = null;
        while ((zipEntry = zin.getNextEntry()) != null) {
            log.debug("unziping :" + zipEntry.getName());
            if (!zipEntry.isDirectory()) {
                String fileName = zipEntry.getName();
                byte bb[] = new byte[1024];
                int curLen;
                baos = new ByteArrayOutputStream();
                while ((curLen = zin.read(bb)) != -1) {
                    baos.write(bb, 0, curLen);
                }
                IMessage unzipMsg = (IMessage) msg.clone();
                if (zipEntryEncoding != null && !"".equals(zipEntryEncoding.trim())) {
                    unzipMsg.setEncoding(zipEntryEncoding);
                }
                unzipMsg.getProperties().put("ZIP_ENTRY_PATH", fileName);
                unzipMsg.getProperties().put("TAG", fileName);
                unzipMsg.setFileName(fileName);
                byte bt[] = baos.toByteArray();
                unzipMsg.setMessage(bt);
                baos.close();
                baos = null;
                unzipList.add(unzipMsg);
            }
        }
        zin.close();
        zin = null;
        return unzipList;
    }

    private byte[] ungzip(IMessage msg) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(msg.getMessage());
        GZIPInputStream gin = new GZIPInputStream(bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int num;
        while ((num = gin.read(buf, 0, buf.length)) != -1) {
            baos.write(buf, 0, num);
        }
        gin.close();
        bais.close();
        bais = null;
        gin = null;
        byte[] unzipData = baos.toByteArray();
        baos.close();
        baos = null;
        return unzipData;
    }

    @Override
    public void setFilterConfig(FilterConfig filterConfig) throws Exception {
        this.filterConfigDecompress = (FilterConfigDecompress) filterConfig;
    }

    @Override
    public void destroy() {

    }
}
