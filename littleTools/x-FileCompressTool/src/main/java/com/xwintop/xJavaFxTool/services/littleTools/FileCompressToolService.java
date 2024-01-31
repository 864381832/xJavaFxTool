package com.xwintop.xJavaFxTool.services.littleTools;

import com.github.junrar.Archive;
import com.github.junrar.Junrar;
import com.github.junrar.rarfile.FileHeader;
import com.xwintop.xJavaFxTool.controller.littleTools.FileCompressToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: FileCompressToolService
 * @Description: 文件解压缩工具
 * @author: xufeng
 * @date: 2019/10/26 0026 19:17
 */

@Getter
@Setter
@Slf4j
public class FileCompressToolService {
    private FileCompressToolController fileCompressToolController;

    public FileCompressToolService(FileCompressToolController fileCompressToolController) {
        this.fileCompressToolController = fileCompressToolController;
    }

    public void compressAction() throws Exception {
        String zipType = fileCompressToolController.getFileTypeChoiceBox().getValue();
        String filePath = fileCompressToolController.getSelectFileTextField().getText();
        String saveFilePath = fileCompressToolController.getSaveFilePathTextField().getText();
        if (fileCompressToolController.getCompressCheckBox().isSelected()) {
            byte[] zippedData = null;
            if (new ArchiveStreamFactory().getOutputStreamArchiveNames().contains(zipType.toLowerCase())) {
                zippedData = this.zip(filePath, zipType);
            } else if (CompressorStreamFactory.getSingleton().getOutputStreamCompressorNames().contains(zipType.toLowerCase())) {
                zippedData = this.gzip(filePath, zipType);
            } else {
                log.warn("未支持该压缩格式：" + zipType + " fileName:" + filePath);
                return;
            }
            String[] filePaths = filePath.split("\\|");
            List<File> fileList = new ArrayList<>();
            for (String path : filePaths) {
                File file = new File(path);
                if (file.isDirectory()) {
                    fileList.addAll(Arrays.asList(file.listFiles()));
                } else {
                    fileList.add(file);
                }
            }
            if (StringUtils.isEmpty(saveFilePath)) {
                saveFilePath = FilenameUtils.getFullPath(fileList.get(0).getPath());
            }
            FileUtils.writeByteArrayToFile(new File(saveFilePath, fileList.get(0).getName() + "." + zipType), zippedData);
        } else {
            if ("AUTO".equalsIgnoreCase(zipType)) {
                try {
                    zipType = ArchiveStreamFactory.detect(new FileInputStream(new File(filePath)));
                } catch (Exception e) {
                    try {
                        zipType = CompressorStreamFactory.detect(new FileInputStream(new File(filePath)));
                    } catch (Exception e1) {
                        log.warn("未识别出压缩类型:" + " fileName：" + filePath);
                    }
                }
            }
            if (new ArchiveStreamFactory().getInputStreamArchiveNames().contains(zipType.toLowerCase())) {
                this.unzip(filePath, zipType);
            } else if (CompressorStreamFactory.getSingleton().getInputStreamCompressorNames().contains(zipType.toLowerCase())) {
                this.ungzip(filePath, zipType);
            } else if ("rar".equals(zipType.toLowerCase())) {
                this.unRar(filePath);
            } else {
                log.warn("未支持该压缩格式：" + zipType + " fileName:" + filePath);
            }
        }
    }

    private byte[] zip(String filePath, String zipType) throws Exception {
        String[] filePaths = filePath.split("\\|");
        List<File> fileList = new ArrayList<>();
        for (String path : filePaths) {
            File file = new File(path);
            if (file.isDirectory()) {
                fileList.addAll(Arrays.asList(file.listFiles()));
            } else {
                fileList.add(file);
            }
        }
        if (ArchiveStreamFactory.SEVEN_Z.equalsIgnoreCase(zipType)) {
            SeekableInMemoryByteChannel seekableByteChannel = new SeekableInMemoryByteChannel();
            SevenZOutputFile sevenZOutput = new SevenZOutputFile(seekableByteChannel);
            for (File file : fileList) {
                SevenZArchiveEntry entry = sevenZOutput.createArchiveEntry(file, file.getName());
                sevenZOutput.putArchiveEntry(entry);
                sevenZOutput.write(FileUtils.readFileToByteArray(file));
                sevenZOutput.closeArchiveEntry();
            }
            sevenZOutput.close();
            return seekableByteChannel.array();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ArchiveOutputStream archiveOutputStream = new ArchiveStreamFactory().createArchiveOutputStream(zipType, outputStream);
        for (File file : fileList) {
            if (archiveOutputStream instanceof ArArchiveOutputStream) {
                ((ArArchiveOutputStream) archiveOutputStream).setLongFileMode(ArArchiveOutputStream.LONGFILE_BSD);
            }
            ArchiveEntry entry = archiveOutputStream.createArchiveEntry(file, file.getName());
            archiveOutputStream.putArchiveEntry(entry);
            archiveOutputStream.write(FileUtils.readFileToByteArray(file));
            archiveOutputStream.closeArchiveEntry();
        }
        archiveOutputStream.close();
        return outputStream.toByteArray();
    }

    private byte[] gzip(String filePath, String zipType) throws Exception {
        File file = new File(filePath);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CompressorOutputStream compressorOutputStream;
        if (CompressorStreamFactory.GZIP.equalsIgnoreCase(zipType)) {
            GzipParameters gzipParameters = new GzipParameters();
            gzipParameters.setFilename(file.getName());
            compressorOutputStream = new GzipCompressorOutputStream(outputStream, gzipParameters);
        } else {
            compressorOutputStream = CompressorStreamFactory.getSingleton().createCompressorOutputStream(zipType, outputStream);
        }
        compressorOutputStream.write(FileUtils.readFileToByteArray(file));
        compressorOutputStream.close();
        return outputStream.toByteArray();
    }

    private void unzip(String filePath, String zipType) throws Exception {
        String saveFilePath = fileCompressToolController.getSaveFilePathTextField().getText();
        if (StringUtils.isEmpty(saveFilePath)) {
            saveFilePath = FilenameUtils.getFullPath(filePath);
        }
        if (ArchiveStreamFactory.SEVEN_Z.equalsIgnoreCase(zipType)) {
            SevenZFile sevenZFile = new SevenZFile(new File(filePath));
            SevenZArchiveEntry entry = null;
            while ((entry = sevenZFile.getNextEntry()) != null) {
                byte[] bytes = new byte[(int) entry.getSize()];
                sevenZFile.read(bytes);  // read current entry's data
                FileUtils.writeByteArrayToFile(new File(saveFilePath, entry.getName()), bytes);
            }
            sevenZFile.close();
        }
        ArchiveInputStream archiveInputStream = new ArchiveStreamFactory().createArchiveInputStream(zipType, new FileInputStream(filePath));
        ArchiveEntry entry = null;
        while ((entry = archiveInputStream.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                IOUtils.copy(archiveInputStream, outputStream);
                FileUtils.writeByteArrayToFile(new File(saveFilePath, entry.getName()), outputStream.toByteArray());
            }
        }
        archiveInputStream.close();
    }

    private void ungzip(String filePath, String zipType) throws Exception {
        String saveFilePath = fileCompressToolController.getSaveFilePathTextField().getText();
        CompressorInputStream compressorInputStream = CompressorStreamFactory.getSingleton().createCompressorInputStream(zipType, new FileInputStream(filePath));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(compressorInputStream, outputStream);
        compressorInputStream.close();
        String entryFileName = null;
        if (compressorInputStream instanceof GzipCompressorInputStream) {
            entryFileName = ((GzipCompressorInputStream) compressorInputStream).getMetaData().getFilename();
        }
        if (StringUtils.isEmpty(entryFileName)) {
            entryFileName = FilenameUtils.getBaseName(filePath);
        }
        if (StringUtils.isEmpty(saveFilePath)) {
            saveFilePath = FilenameUtils.getFullPath(filePath);
        }
        FileUtils.writeByteArrayToFile(new File(saveFilePath, entryFileName), outputStream.toByteArray());
    }

    private void unRar(String filePath) throws Exception {
        String saveFilePath = fileCompressToolController.getSaveFilePathTextField().getText();
        Junrar.extract(filePath, saveFilePath);
    }
}