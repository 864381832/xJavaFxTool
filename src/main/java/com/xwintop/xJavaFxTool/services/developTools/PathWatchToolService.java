package com.xwintop.xJavaFxTool.services.developTools;

import com.xwintop.xJavaFxTool.controller.developTools.PathWatchToolController;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.*;

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
        Thread thread = new Thread(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                //给path路径加上文件观察服务
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                while (true) {
                    final WatchKey key = watchService.take();
                    for (WatchEvent<?> watchEvent : key.pollEvents()) {
                        final WatchEvent.Kind<?> kind = watchEvent.kind();
                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }
                        //创建事件
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            System.out.print("[新建]");
                            pathWatchToolController.getWatchLogTextArea().appendText("新建：");
                        }
                        //修改事件
                        if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                            System.out.print("修改]");
                            pathWatchToolController.getWatchLogTextArea().appendText("修改：");
                        }
                        //删除事件
                        if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                            System.out.print("[删除]");
                            pathWatchToolController.getWatchLogTextArea().appendText("删除：");
                        }
                        // get the filename for the event
                        final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                        final Path filename = watchEventPath.context();
                        // print it out
                        System.out.println(kind + " -> " + filename);
                        pathWatchToolController.getWatchLogTextArea().appendText(kind + " -> " + filename + "\n");
                    }
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            } catch (IOException | InterruptedException ex) {
                System.err.println(ex);
            }
        });
        thread.start();
    }
}