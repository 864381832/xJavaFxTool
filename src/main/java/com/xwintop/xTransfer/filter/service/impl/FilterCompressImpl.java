package com.xwintop.xTransfer.filter.service.impl;

import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.filter.bean.FilterConfigCompress;
import com.xwintop.xTransfer.filter.service.Filter;
import com.xwintop.xTransfer.messaging.IContext;
import com.xwintop.xTransfer.messaging.IMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Map;

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
        IMessage msg = ctx.getMessages().get(0);
        if (StringUtils.isNotBlank(filterConfigCompress.getFileNameFilterRegex())) {
            String fileNameFilterRegexGroup = filterConfigCompress.getFileNameFilterRegexGroup();
            if (StringUtils.isEmpty(fileNameFilterRegexGroup)) {
                fileNameFilterRegexGroup = "defaultRegexGroup";
            }
            if ("?!".equals(filterConfigCompress.getFileNameFilterRegex())) {
                if (msg.checkFileNameFilterRegexGroup(fileNameFilterRegexGroup)) {
                    log.info("Filter:" + filterConfigCompress.getId() + "跳过fileName：" + msg.getFileName());
                    return;
                }
            } else {
                if (!msg.getFileName().matches(filterConfigCompress.getFileNameFilterRegex())) {
                    log.info("Filter:" + filterConfigCompress.getId() + "跳过fileName：" + msg.getFileName());
                    return;
                }
                msg.addFileNameFilterRegexGroup(fileNameFilterRegexGroup);
            }
        }
        //        Map args = filterConfigCompress.getArgs();
        String zipType = msg.getProperties().getProperty("COMPRESS_METHOD");
        if (StringUtils.isBlank(zipType)) {
            zipType = StringUtils.defaultIfBlank(filterConfigCompress.getMethod(), "zip");
        }
        byte[] zippedData = null;
        if (new ArchiveStreamFactory().getOutputStreamArchiveNames().contains(zipType.toLowerCase())) {
            zippedData = this.zip(ctx.getMessages(), zipType);
        } else if (CompressorStreamFactory.getSingleton().getOutputStreamCompressorNames().contains(zipType.toLowerCase())) {
            zippedData = this.gzip(msg, zipType);
        } else {
            log.warn("未支持该压缩格式：" + zipType + " Filter:" + filterConfigCompress.getId() + " fileName:" + msg.getFileName());
            return;
        }
        msg.getProperties().put("COMPRESS_METHOD", zipType);
        if (filterConfigCompress.isAddPostfixName()) {
            msg.getProperties().put("ZIP_FILE_NAME", msg.getFileName());
            msg.setFileName(msg.getFileName() + "." + zipType);
        }
        log.info("Filter:" + filterConfigCompress.getId() + "success zip data." + msg.getFileName());
        msg.setMessage(zippedData);
        ctx.setMessage(msg);
    }

    private byte[] zip(List<IMessage> list, String zipType) throws Exception {
        if (ArchiveStreamFactory.SEVEN_Z.equalsIgnoreCase(zipType)) {
            SeekableInMemoryByteChannel seekableByteChannel = new SeekableInMemoryByteChannel();
            SevenZOutputFile sevenZOutput = new SevenZOutputFile(seekableByteChannel);
            for (IMessage msg : list) {
                SevenZArchiveEntry entry = new SevenZArchiveEntry();
                entry.setName(msg.getFileName());
                sevenZOutput.putArchiveEntry(entry);
                sevenZOutput.write(msg.getMessage());
                sevenZOutput.closeArchiveEntry();
            }
            sevenZOutput.close();
            return seekableByteChannel.array();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ArchiveOutputStream archiveOutputStream = new ArchiveStreamFactory().createArchiveOutputStream(zipType, outputStream);
        for (IMessage msg : list) {
            if (archiveOutputStream instanceof ArArchiveOutputStream) {
                ((ArArchiveOutputStream) archiveOutputStream).setLongFileMode(ArArchiveOutputStream.LONGFILE_BSD);
            }
            ArchiveEntry entry = null;
            if (archiveOutputStream instanceof ArArchiveOutputStream) {
                entry = new ArArchiveEntry(msg.getFileName(), msg.getMessage().length);
            } else if (archiveOutputStream instanceof TarArchiveOutputStream) {
                entry = new TarArchiveEntry(msg.getFileName());
                ((TarArchiveEntry) entry).setSize(msg.getMessage().length);
            } else if (archiveOutputStream instanceof CpioArchiveOutputStream) {
                entry = new CpioArchiveEntry(msg.getFileName());
                ((CpioArchiveEntry) entry).setSize(msg.getMessage().length);
            } else {
                entry = archiveOutputStream.createArchiveEntry(new File(msg.getFileName()), msg.getFileName());
            }
            archiveOutputStream.putArchiveEntry(entry);
            archiveOutputStream.write(msg.getMessage());
            archiveOutputStream.closeArchiveEntry();
        }
        archiveOutputStream.close();
        return outputStream.toByteArray();
    }

    private byte[] gzip(IMessage msg, String zipType) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CompressorOutputStream compressorOutputStream;
        if (CompressorStreamFactory.GZIP.equalsIgnoreCase(zipType)) {
            GzipParameters gzipParameters = new GzipParameters();
            gzipParameters.setFilename(msg.getFileName());
            compressorOutputStream = new GzipCompressorOutputStream(outputStream, gzipParameters);
        } else {
            compressorOutputStream = CompressorStreamFactory.getSingleton().createCompressorOutputStream(zipType, outputStream);
        }
        compressorOutputStream.write(msg.getMessage());
        compressorOutputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public void setFilterConfig(FilterConfig filterConfig) throws Exception {
        this.filterConfigCompress = (FilterConfigCompress) filterConfig;
    }

    @Override
    public void destroy() {

    }
}
