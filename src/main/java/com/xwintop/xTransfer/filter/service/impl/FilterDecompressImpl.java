package com.xwintop.xTransfer.filter.service.impl;

import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.bean.FilterConfigDecompress;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        for (IMessage iMessage : ctx.getMessages()) {
            if (StringUtils.isNotBlank(filterConfigDecompress.getFileNameFilterRegex())) {
                String fileNameFilterRegexGroup = filterConfigDecompress.getFileNameFilterRegexGroup();
                if (StringUtils.isEmpty(fileNameFilterRegexGroup)) {
                    fileNameFilterRegexGroup = "defaultRegexGroup";
                }
                if ("?!".equals(filterConfigDecompress.getFileNameFilterRegex())) {
                    if (iMessage.checkFileNameFilterRegexGroup(fileNameFilterRegexGroup)) {
                        log.info("Filter:" + filterConfigDecompress.getId() + "跳过fileName：" + iMessage.getFileName());
                        continue;
                    }
                } else {
                    if (!iMessage.getFileName().matches(filterConfigDecompress.getFileNameFilterRegex())) {
                        log.info("Filter:" + filterConfigDecompress.getId() + "跳过fileName：" + iMessage.getFileName());
                        continue;
                    }
                    iMessage.addFileNameFilterRegexGroup(fileNameFilterRegexGroup);
                }
            }
            ctx.getMessages().remove(iMessage);
            doFilter(iMessage, ctx);
        }
    }

    private void doFilter(IMessage msg, IContext ctx) throws Exception {
//        Map args = filterConfigDecompress.getArgs();
        String zipType = filterConfigDecompress.getMethod();
        if (StringUtils.isBlank(zipType)) {
            zipType = StringUtils.defaultIfBlank(msg.getProperties().getProperty("COMPRESS_METHOD"), "zip");
        }
        if ("AUTO".equalsIgnoreCase(zipType)) {
            try {
                zipType = ArchiveStreamFactory.detect(new ByteArrayInputStream(msg.getMessage()));
            } catch (Exception e) {
                try {
                    zipType = CompressorStreamFactory.detect(new ByteArrayInputStream(msg.getMessage()));
                } catch (Exception e1) {
                    log.warn("未识别出压缩类型:" + filterConfigDecompress.getId() + " fileName：" + msg.getFileName());
                }
            }
        }
        if (new ArchiveStreamFactory().getInputStreamArchiveNames().contains(zipType.toLowerCase())) {
            List list = this.unzip(msg, zipType);
            ctx.getMessages().addAll(list);
        } else if (CompressorStreamFactory.getSingleton().getInputStreamCompressorNames().contains(zipType.toLowerCase())) {
            msg = this.ungzip(msg, zipType);
            ctx.addMessage(msg);
        } else {
            log.warn("未支持该压缩格式：" + zipType + " Filter:" + filterConfigDecompress.getId());
        }
        log.info("Filter:" + filterConfigDecompress.getId() + " success unzip data." + msg.getFileName());
    }

    private List unzip(IMessage msg, String zipType) throws Exception {
        List unzipList = new ArrayList();
        if (ArchiveStreamFactory.SEVEN_Z.equalsIgnoreCase(zipType)) {
            SeekableByteChannel inMemoryByteChannel = new SeekableInMemoryByteChannel(msg.getMessage());
            SevenZFile sevenZFile = new SevenZFile(inMemoryByteChannel);
            SevenZArchiveEntry entry = null;
            while ((entry = sevenZFile.getNextEntry()) != null) {
                byte[] bytes = new byte[(int) entry.getSize()];
                sevenZFile.read(bytes);  // read current entry's data
                IMessage unzipMsg = this.getUnzipMsg(msg, bytes, entry);
                unzipList.add(unzipMsg);
            }
            sevenZFile.close();
            return unzipList;
        }
        ArchiveInputStream archiveInputStream = new ArchiveStreamFactory().createArchiveInputStream(zipType, new ByteArrayInputStream(msg.getMessage()), filterConfigDecompress.getActualEncoding());
        ArchiveEntry entry = null;
        while ((entry = archiveInputStream.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                IOUtils.copy(archiveInputStream, outputStream);
                IMessage unzipMsg = this.getUnzipMsg(msg, outputStream.toByteArray(), entry);
                unzipList.add(unzipMsg);
            }
        }
        archiveInputStream.close();
        return unzipList;
    }

    private IMessage ungzip(IMessage msg, String zipType) throws Exception {
        CompressorInputStream compressorInputStream = CompressorStreamFactory.getSingleton().createCompressorInputStream(zipType, new ByteArrayInputStream(msg.getMessage()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(compressorInputStream, outputStream);
        compressorInputStream.close();
        msg.setMessage(outputStream.toByteArray());
        String entryFileName = null;
        if (compressorInputStream instanceof GzipCompressorInputStream) {
            entryFileName = ((GzipCompressorInputStream) compressorInputStream).getMetaData().getFilename();
        }
        if (StringUtils.isEmpty(entryFileName)) {
            entryFileName = FilenameUtils.getBaseName(msg.getFileName());
        }
        msg.getProperties().put("ZIP_FILE_NAME", msg.getFileName());
        msg.setFileName(entryFileName);
        if (StringUtils.isNotEmpty(filterConfigDecompress.getEncoding())) {
            msg.setEncoding(filterConfigDecompress.getEncoding());
        }
        return msg;
    }

    private IMessage getUnzipMsg(IMessage msg, byte[] bytes, ArchiveEntry entry) throws Exception {
        IMessage unzipMsg = (IMessage) msg.clone();
        if (StringUtils.isNotEmpty(filterConfigDecompress.getEncoding())) {
            unzipMsg.setEncoding(filterConfigDecompress.getEncoding());
        }
        String zipEntryPath = FilenameUtils.getFullPath(entry.getName());
        if (StringUtils.isNotEmpty(zipEntryPath)) {
            unzipMsg.getProperties().put("ZIP_ENTRY_PATH", zipEntryPath);
        }
        unzipMsg.getProperties().put("ZIP_FILE_NAME", msg.getFileName());
        unzipMsg.setFileName(FilenameUtils.getName(entry.getName()));
        unzipMsg.setMessage(bytes);
        return unzipMsg;
    }

    @Override
    public void setFilterConfig(FilterConfig filterConfig) throws Exception {
        this.filterConfigDecompress = (FilterConfigDecompress) filterConfig;
    }

    @Override
    public void destroy() {

    }
}
