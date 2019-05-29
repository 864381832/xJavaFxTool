package com.xwintop.xJavaFxTool.controller.debugTools;

import com.xwintop.xJavaFxTool.services.debugTools.SwitchHostsToolService;
import com.xwintop.xJavaFxTool.view.debugTools.SwitchHostsToolView;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.net.URL;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: SwitchHostsToolController
 * @Description: 切换Hosts工具
 * @author: xufeng
 * @date: 2018/1/31 15:18
 */

@Getter
@Setter
@Slf4j
public class SwitchHostsToolController extends SwitchHostsToolView {
    private SwitchHostsToolService switchHostsToolService = new SwitchHostsToolService(this);

    private static final String KEYWORD_PATTERN = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}\\b";
    private static final String COMMENT_PATTERN = "#[^\n]*";
    private static final Pattern PATTERN = Pattern.compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")");
    private ExecutorService executor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initView();
            initEvent();
            initService();
        } catch (Exception e) {
            log.error("加载报错", e);
        }
    }

    private void initView() {
        executor = Executors.newSingleThreadExecutor();
        hostTextArea = new CodeArea();
        hostTextArea.setParagraphGraphicFactory(LineNumberFactory.get(hostTextArea));
        hostTextArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(hostTextArea.richChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);

        TreeItem<String> treeItem = new TreeItem<String>("Hosts");
        treeItem.setExpanded(true);
        hostFileTreeView.setRoot(treeItem);
        TreeItem<String> commonHostTreeItem = new TreeItem<String>("公共Host");
        TreeItem<String> systemHostTreeItem = new TreeItem<String>("系统当前Host");
        TreeItem<String> localHostTreeItem = new TreeItem<String>("本地方案");
        localHostTreeItem.setExpanded(true);
        TreeItem<String> localHostTreeItem1 = new TreeItem<String>("方案一");
        TreeItem<String> localHostTreeItem2 = new TreeItem<String>("方案二");
        localHostTreeItem.getChildren().add(localHostTreeItem1);
        localHostTreeItem.getChildren().add(localHostTreeItem2);
        TreeItem<String> webTreeItem = new TreeItem<String>("在线方案");
        webTreeItem.setExpanded(true);
        treeItem.getChildren().add(commonHostTreeItem);
        treeItem.getChildren().add(systemHostTreeItem);
        treeItem.getChildren().add(localHostTreeItem);
        treeItem.getChildren().add(webTreeItem);
    }

    private void initEvent() {
    }

    private void initService() throws Exception {
        switchHostsToolService.reloadSystemHosts();
    }

    @FXML
    private void addAction(ActionEvent event) {
    }

    @FXML
    private void reloadAction(ActionEvent event) throws Exception {
        switchHostsToolService.reloadSystemHosts();
    }

    @FXML
    private void editAction(ActionEvent event) throws Exception {
        switchHostsToolService.editAction();
    }

    @FXML
    private void deleteAction(ActionEvent event) {
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = hostTextArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        hostTextArea.setStyleSpans(0, highlighting);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("COMMENT") != null ? "comment" :
                                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}