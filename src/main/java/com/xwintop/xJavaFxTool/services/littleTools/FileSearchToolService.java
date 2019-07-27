package com.xwintop.xJavaFxTool.services.littleTools;

import cn.hutool.core.thread.ThreadUtil;
import com.xwintop.xJavaFxTool.controller.littleTools.FileSearchToolController;
import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    private static final String searchIndexDir = ConfigureUtil.getConfigurePath("searchIndexDir/");

    private static IndexWriter indexWriter = null;
//    private static IndexSearcher indexSearcher = null;

    static {
        File searchIndexDirFile = new File(searchIndexDir);
        try {
            FileUtils.forceMkdir(searchIndexDirFile);
            Directory directory = FSDirectory.open(searchIndexDirFile.toPath());
//            Analyzer analyzer = new StandardAnalyzer(); // 标准分词器，适用于英文
//            //创建索引写入配置
//            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
//            //创建索引写入对象
//            indexWriter = new IndexWriter(directory, indexWriterConfig);
            getIndexWriter(directory);
            if (!DirectoryReader.indexExists(directory)) {
                indexWriter.close();
                getIndexWriter(directory);
            }
        } catch (Exception e) {
            log.error("创建文件失败！", e);
        }
    }

    public static void getIndexWriter(Directory directory) throws IOException {
        Analyzer analyzer = new StandardAnalyzer(); // 标准分词器，适用于英文
        //创建索引写入配置
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        //创建索引写入对象
        indexWriter = new IndexWriter(directory, indexWriterConfig);
    }

    public IndexSearcher getIndexSearcher() {
        IndexSearcher indexSearcher = null;
//        if (indexSearcher == null) {
        try {
            Directory directory = FSDirectory.open(Paths.get(searchIndexDir));
            // 创建索引的读取器
            IndexReader indexReader = DirectoryReader.open(directory);
            // 创建一个索引的查找器，来检索索引库
            indexSearcher = new IndexSearcher(indexReader);
        } catch (Exception e) {
            log.error("创建索引读取器失败：", e);
        }
//        }
        return indexSearcher;
    }

    public FileSearchToolService(FileSearchToolController fileSearchToolController) {
        this.fileSearchToolController = fileSearchToolController;
    }

    public void searchContentAction() throws Exception {
        String queryText = fileSearchToolController.getSearchContentTextField().getText().trim();
        String path = fileSearchToolController.getSearchDirectoryTextField().getText().trim();
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        String fileName = "fileName";
        if (fileSearchToolController.getMatchCaseCheckBox().isSelected()) {
            fileName = "fileNameLowerCase";
            queryText = queryText.toLowerCase();
        }
        if (fileSearchToolController.getRegularCheckBox().isSelected()) {
            Query fileNameQuery = new RegexpQuery(new Term(fileName, queryText));//正则搜索
            builder.add(fileNameQuery, BooleanClause.Occur.MUST);
        } else {
            if (fileSearchToolController.getFullTextMatchingCheckBox().isSelected()) {
                Query fileNameQuery = new TermQuery(new Term(fileName, queryText));
                builder.add(fileNameQuery, BooleanClause.Occur.MUST);
            } else {
                Query fileNameQuery = new WildcardQuery(new Term(fileName, "*" + queryText + "*"));// 通配符
                builder.add(fileNameQuery, BooleanClause.Occur.MUST);
            }
        }
        if (fileSearchToolController.getShowHideFileChoice().getSelectionModel().getSelectedIndex() == 1) {
            TermQuery isHiddenQuery = new TermQuery(new Term("isHidden", "false"));
            builder.add(isHiddenQuery, BooleanClause.Occur.MUST);
        } else if (fileSearchToolController.getShowHideFileChoice().getSelectionModel().getSelectedIndex() == 2) {
            TermQuery isHiddenQuery = new TermQuery(new Term("isHidden", "true"));
            builder.add(isHiddenQuery, BooleanClause.Occur.MUST);
        }
        if (fileSearchToolController.getFileTypeChoiceBox().getSelectionModel().getSelectedIndex() == 1) {
            TermQuery isDirectoryQuery = new TermQuery(new Term("isDirectory", "false"));
            builder.add(isDirectoryQuery, BooleanClause.Occur.MUST);
        } else if (fileSearchToolController.getFileTypeChoiceBox().getSelectionModel().getSelectedIndex() == 2) {
            TermQuery isDirectoryQuery = new TermQuery(new Term("isDirectory", "true"));
            builder.add(isDirectoryQuery, BooleanClause.Occur.MUST);
        }
        if (StringUtils.isNotEmpty(path)) {
            PrefixQuery pathQuery = new PrefixQuery(new Term("absolutePath", path));// 字段搜索 Field:Keyword，自动在结尾添加 *
            builder.add(pathQuery, BooleanClause.Occur.MUST);
        }
        BooleanQuery query = builder.build();
        IndexSearcher indexSearcher = getIndexSearcher();
        TopDocs topDocs = indexSearcher.search(query, Integer.MAX_VALUE);
        fileSearchToolController.getSearchResultTableData().clear();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            //取得对应的文档对象
            Document document = indexSearcher.doc(scoreDoc.doc);
//            System.out.println("fileName：" + document.get("fileName"));
//            System.out.println("absolutePath：" + document.get("absolutePath"));
            Map map = new HashMap();
            map.put("fileName", document.get("fileName"));
            map.put("absolutePath", document.get("absolutePath"));
            map.put("fileSize", document.get("fileSize"));
            map.put("lastModified", document.get("lastModified"));
            fileSearchToolController.getSearchResultTableData().add(map);
        }
        fileSearchToolController.getSearchTextLabel().setText("总共查询到" + topDocs.totalHits + "个文档");
    }

    public void refreshIndexAction() throws Exception {
        String path = fileSearchToolController.getSearchDirectoryTextField().getText();
        addSearchIndexFile(path);
    }

    public void addSearchIndexFile(String path) {
        ThreadUtil.execute(() -> {
            try {
                DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path));
                Iterator<Path> pathIterator = stream.iterator();
                while (pathIterator.hasNext()) {
                    Path curPath = pathIterator.next();
                    addIndexDocument(curPath.toFile());
                    if (Files.isDirectory(curPath)) {
                        addSearchIndexFile(curPath.toString());
                    }
                }
            } catch (Exception e) {
                log.warn("获取失败：", e);
            }
        });
    }

    public void addIndexDocument(File file) throws Exception {
        Document doc = new Document();
        doc.add(new StringField("fileName", file.getName(), Field.Store.YES));
        doc.add(new StringField("fileNameLowerCase", file.getName().toLowerCase(), Field.Store.YES));
        doc.add(new StringField("absolutePath", file.getAbsolutePath(), Field.Store.YES));
//        doc.add(new DoubleDocValuesField("fileSize", file.length()));
//        doc.add(new DoubleDocValuesField("lastModified", file.lastModified()));
        doc.add(new StringField("fileSize", String.valueOf(file.length()), Field.Store.YES));
        doc.add(new StringField("lastModified", DateTools.timeToString(file.lastModified(), DateTools.Resolution.MILLISECOND), Field.Store.YES));
        doc.add(new StringField("isHidden", String.valueOf(file.isHidden()), Field.Store.YES));
        doc.add(new StringField("isDirectory", String.valueOf(file.isDirectory()), Field.Store.YES));
        indexWriter.updateDocument(new Term("absolutePath", file.getAbsolutePath()), doc);
//        indexWriter.addDocument(doc);
        indexWriter.commit();
    }
}