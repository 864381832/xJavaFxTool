package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.FileCopyController;
import com.xwintop.xJavaFxTool.model.FileCopyTableBean;
import com.xwintop.xcore.util.ConfigureUtil;
import com.xwintop.xcore.util.FileUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

/**
 * @ClassName: FileCopyService
 * @Description: 文件复制工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:34
 */

@Getter
@Setter
@Slf4j
public class FileCopyService {
    private FileCopyController fileCopyController;
    private ObservableList<FileCopyTableBean> tableData;

    public FileCopyService(FileCopyController fileCopyController) {
        this.fileCopyController = fileCopyController;
    }

    public void saveConfigure() throws Exception {
        saveConfigure(ConfigureUtil.getConfigureFile("fileCopyConfigure.properties"));
    }

    public void saveConfigure(File file) throws Exception {
//        FileUtils.touch(file);
//        PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
//        xmlConfigure.clear();
//        for (int i = 0; i < tableData.size(); i++) {
//            xmlConfigure.setProperty("tableBean" + i, tableData.get(i).getPropertys());
//        }
//        xmlConfigure.save();
        TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
    }

    public void otherSaveConfigureAction() throws Exception {
        String fileName = "fileCopyConfigure.properties";
        File file = FileChooserUtil.chooseSaveFile(fileName, new FileChooser.ExtensionFilter("All File", "*.*"),
                new FileChooser.ExtensionFilter("Properties", "*.properties"));
        if (file != null) {
            saveConfigure(file);
            TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
        }
    }

    public void loadingConfigure() {
        loadingConfigure(ConfigureUtil.getConfigureFile("fileCopyConfigure.properties"));
    }

    public void loadingConfigure(File file) {
        try {
            tableData.clear();
//            PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
//            xmlConfigure.getKeys().forEachRemaining(t -> tableData.add(new FileCopyTableBean(xmlConfigure.getString(t))));
        } catch (Exception e) {
            try {
                TooltipUtil.showToast("加载配置失败：" + e.getMessage());
            } catch (Exception e2) {
            }
        }
    }

    public void loadingConfigureAction() {
        File file = FileChooserUtil.chooseFile(new FileChooser.ExtensionFilter("All File", "*.*"),
                new FileChooser.ExtensionFilter("Properties", "*.properties"));
        if (file != null) {
            loadingConfigure(file);
        }
    }

    public void copyAction() throws Exception {
        for (FileCopyTableBean tableBean : tableData) {
            if (tableBean.getIsCopy()) {
                copyAction(tableBean);
            }
        }
    }

    public void copyAction(FileCopyTableBean tableBean) throws Exception {
        int number = Integer.parseInt(tableBean.getCopyNumber());
        File fileOriginal = new File(tableBean.getCopyFileOriginalPath());
        File fileTarget = new File(tableBean.getCopyFileTargetPath());
        FileUtils.forceMkdir(fileTarget);
        for (int i = 0; i < number; i++) {
            if (fileOriginal.isDirectory()) {
                if (tableBean.getIsRename()) {
                    DirectoryStream<Path> stream = Files.newDirectoryStream(fileOriginal.toPath());
                    Iterator<Path> pathIterator = stream.iterator();
                    while (pathIterator.hasNext()) {
                        Path curPath = pathIterator.next();
                        File file = curPath.toFile();
                        String fileName = FileUtil.getRandomFileName(file);
                        if (i != 0) {
                            fileName = i + fileName;
                        }
                        FileUtils.copyFile(file, new File(fileTarget.getPath(), fileName));
                    }
                } else {
                    if (number == 1) {
                        if (tableBean.getIsDeleteSourceFile()) {
                            FileUtils.cleanDirectory(fileTarget);
                        }
                        FileUtils.copyDirectory(fileOriginal, fileTarget, false);
                    } else {
                        Collection<File> files = FileUtils.listFiles(fileOriginal, null, false);
                        for (File file : files) {
                            FileUtils.copyFile(file, new File(fileTarget.getPath(), (i == 0 ? "" : i) + file.getName()));
                        }
                    }
                }
            } else {
                if (tableBean.getIsRename()) {
                    String fileName = FileUtil.getRandomFileName(fileOriginal);
                    if (i != 0) {
                        fileName = i + fileName;
                    }
                    File file = new File(fileTarget.getPath(), fileName);
                    FileUtils.copyFile(fileOriginal, file);
                } else {
                    if (i == 0) {
                        if (tableBean.getIsDeleteSourceFile()) {
                            FileUtils.deleteQuietly(new File(fileTarget, fileOriginal.getName()));
                        }
                        FileUtils.copyFileToDirectory(fileOriginal, fileTarget);
                    } else {
                        FileUtils.copyFile(fileOriginal, new File(fileTarget.getPath(), i + fileOriginal.getName()));
                    }
                }
            }
        }
        if (tableBean.getIsDelete()) {
            if (fileOriginal.isDirectory()) {
                FileUtils.deleteDirectory(fileOriginal);
            } else {
                FileUtils.deleteQuietly(fileOriginal);
            }
        }
    }

    public boolean runQuartzAction() throws Exception {
        fileCopyController.getActionScheduleUtil().runQuartzAction();
        return true;
    }

    public boolean stopQuartzAction() throws Exception {
        fileCopyController.getActionScheduleUtil().stopQuartzAction();
        return true;
    }

    public ObservableList<FileCopyTableBean> getTableData() {
        return tableData;
    }

    public void setTableData(ObservableList<FileCopyTableBean> tableData) {
        this.tableData = tableData;
    }

}
