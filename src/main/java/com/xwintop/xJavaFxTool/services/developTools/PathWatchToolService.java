package com.xwintop.xJavaFxTool.services.developTools;

import com.xwintop.xJavaFxTool.controller.developTools.PathWatchToolController;
import com.xwintop.xJavaFxTool.utils.DirectoryTreeUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.geometry.Pos;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;

/**
 * @ClassName: PathWatchToolService
 * @Description: 文件夹监控工具
 * @author: xufeng
 * @date: 2019/4/27 0027 1:06
 */

@Getter
@Setter
@Slf4j
public class PathWatchToolService {
    private PathWatchToolController pathWatchToolController;
    Thread thread = null;

    public PathWatchToolService(PathWatchToolController pathWatchToolController) {
        this.pathWatchToolController = pathWatchToolController;
    }

    public void watchAction() throws Exception {
        String watchPath = pathWatchToolController.getWatchPathTextField().getText();
        if (StringUtils.isEmpty(watchPath)) {
            TooltipUtil.showToast("监控目录不能为空！");
            return;
        }
        Path path = Paths.get(watchPath);
        if (!Files.exists(path)) {
            TooltipUtil.showToast("监控目录不存在！");
            return;
        } else if (!Files.isDirectory(path)) {
            TooltipUtil.showToast("只能监控文件夹！");
            return;
        }
        if (thread != null) {
            thread.stop();
        }
        boolean fileNameSRegex = pathWatchToolController.getFileNameSupportRegexCheckBox().isSelected();
        String fileNameContains = pathWatchToolController.getFileNameContainsTextField().getText();
        String fileNameNotContains = pathWatchToolController.getFileNameNotContainsTextField().getText();
        Pattern fileNameCsPattern = Pattern.compile(fileNameContains, Pattern.CASE_INSENSITIVE);
        Pattern fileNameNCsPattern = Pattern.compile(fileNameNotContains, Pattern.CASE_INSENSITIVE);

        String folderPathCsText = pathWatchToolController.getFolderPathContainsTextField().getText();
        String folderPathNCsText = pathWatchToolController.getFolderPathNotContainsTextField().getText();
        boolean folderPathSRegex = pathWatchToolController.getFolderPathSupportRegexCheckBox().isSelected();
        Pattern folderPathCsPattern = Pattern.compile(folderPathCsText, Pattern.CASE_INSENSITIVE);
        Pattern folderPathNCsPattern = Pattern.compile(folderPathNCsText, Pattern.CASE_INSENSITIVE);
        thread = new Thread(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                //给path路径加上文件观察服务
//                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        if (DirectoryTreeUtil.ifMatchText(dir.toString(), folderPathCsText, folderPathNCsText, folderPathSRegex, folderPathCsPattern, folderPathNCsPattern)) {
                            dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                        } else {
                            log.info("跳过监听：" + dir.toString());
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
                while (true) {
                    final WatchKey key = watchService.take();
                    for (WatchEvent<?> watchEvent : key.pollEvents()) {
                        StringBuffer stringBuffer = new StringBuffer();
                        final WatchEvent.Kind<?> kind = watchEvent.kind();
                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }
                        // get the filename for the event
                        final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                        final Path filename = watchEventPath.context();
                        Path watchable = ((Path) key.watchable()).resolve(filename);
                        if (Files.isDirectory(watchable)) {
                            if (!DirectoryTreeUtil.ifMatchText(filename.toString(), folderPathCsText, folderPathNCsText, folderPathSRegex, folderPathCsPattern, folderPathNCsPattern)) {
                                log.info("跳过文件夹监听：" + watchable);
                                continue;
                            }
                        }
                        if (Files.isRegularFile(watchable)) {
                            if (!DirectoryTreeUtil.ifMatchText(filename.toString(), fileNameContains, fileNameNotContains, fileNameSRegex, fileNameCsPattern, fileNameNCsPattern)) {
                                log.info("跳过文件监听：" + watchable);
                                continue;
                            }
                        }
                        //创建事件
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            stringBuffer.append("新建：");
                            pathWatchToolController.getWatchLogTextArea().appendText("新建：");
                            if (Files.isDirectory(watchable)) {
                                watchable.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                            }
                        }
                        //修改事件
                        if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                            stringBuffer.append("修改：");
                            pathWatchToolController.getWatchLogTextArea().appendText("修改：");
                        }
                        //删除事件
                        if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                            stringBuffer.append("删除：");
                            pathWatchToolController.getWatchLogTextArea().appendText("删除：");
                        }
                        stringBuffer.append(kind + " -> " + filename + "\n");
                        log.info(stringBuffer.toString());
                        pathWatchToolController.getWatchLogTextArea().appendText(stringBuffer.toString());
                        if (pathWatchToolController.getIsShowNotificationCheckBox().isSelected()) {
                            TooltipUtil.showToast("文件夹发送变化", stringBuffer.toString(), Pos.BOTTOM_RIGHT);
                        }
                    }
                    boolean valid = key.reset();
//                    if (!valid) {
//                        break;
//                    }
                }
            } catch (IOException | InterruptedException ex) {
                log.error("获取监听异常：", ex);
            }
        });
        thread.start();
    }

    public void stopWatchAction() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
    }
}