package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.FileSearchToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.*;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * @ClassName: FileSearchToolService
 * @Description: 文件搜索工具
 * @author: xufeng
 * @date: 2019/7/18 10:21
 */

@Getter
@Setter
@Slf4j
public class FileSearchToolService {
    private FileSearchToolController fileSearchToolController;

    public FileSearchToolService(FileSearchToolController fileSearchToolController) {
        this.fileSearchToolController = fileSearchToolController;
    }

    public void searchContentAction() {

    }

    private void refreshIndexAction() {

    }

    private void searchDirectoryAction() {

    }

    public void addSearchIndexFile(String path) throws Exception {
        DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path));
        Iterator<Path> pathIterator = stream.iterator();
        while (pathIterator.hasNext()) {
            Path curPath = pathIterator.next();
//            System.out.println(curPath.toString());
            if (Files.isDirectory(curPath)) {
                addSearchIndexFile(curPath.toString());
            } else {
//                System.out.println(curPath.toString());
            }
        }
    }

    public void addIndexDocument(File file) throws Exception {
        Document doc = new Document();
        doc.add(new TextField("fileName", file.getName(), Field.Store.YES));
        doc.add(new TextField("absolutePath", file.getAbsolutePath(), Field.Store.YES));
        doc.add(new LongPoint("fileSize", file.length()));
        doc.add(new TextField("lastModified", DateTools.timeToString(file.lastModified(), DateTools.Resolution.MILLISECOND), Field.Store.YES));
    }
}