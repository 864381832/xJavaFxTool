package com.xwintop.xJavaFxTool.controller.debugTools;

import cn.hutool.core.thread.NamedThreadFactory;
import com.xwintop.xJavaFxTool.services.debugTools.SwitchHostsToolService;
import com.xwintop.xJavaFxTool.view.debugTools.SwitchHostsToolView;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.fxmisc.flowless.VirtualizedScrollPane;
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
    private CodeArea hostTextArea;
    private static final String KEYWORD_PATTERN =
            "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}\\b";

    private static final String COMMENT_PATTERN = "#[^\n]*";

    private static final Pattern PATTERN = Pattern
            .compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")");

    private ExecutorService executor;

    private static String getResourceUrl(String resourcePath) {
        URL resource = SwitchHostsToolController.class.getResource(resourcePath);
        return resource == null ? null : resource.toExternalForm();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("HIGHLIGHTING", true));
            initView();
            initEvent();
            initService();
        } catch (Exception e) {
            log.error("加载报错", e);
        }
    }

    private void initView() {
        hostTextArea = new CodeArea();
        VirtualizedScrollPane virtualizedScrollPane = new VirtualizedScrollPane(hostTextArea);
        hostsToolBorderPane.setCenter(virtualizedScrollPane);
        hostTextArea.getStylesheets().add(getResourceUrl("/css/debugTools/hosts-keywords.css"));
        hostTextArea.setParagraphGraphicFactory(LineNumberFactory.get(hostTextArea));
        hostTextArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(hostTextArea.richChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);
        hostTextArea.setOnKeyPressed(event -> {
            if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN).match(event)) {
                switchHostsToolService.editAction();
            }

        });
        TreeItem<String> treeItem = new TreeItem<String>("Hosts");
        treeItem.setExpanded(true);
        hostFileTreeView.setRoot(treeItem);
        TreeItem<String> systemHostTreeItem = new TreeItem<String>("系统当前Host");
        treeItem.getChildren().add(systemHostTreeItem);
        treeItem.getChildren().add(new TreeItem<String>("获取GitHub地址"));
        treeItem.getChildren().add(new TreeItem<String>("更新GitHub地址"));
    }

    private void initEvent() {
        hostFileTreeView.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = hostFileTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                return;
            }
            if (event.getButton() == MouseButton.PRIMARY) {
                try {
                    if ("系统当前Host".equals(selectedItem.getValue())) {
                        switchHostsToolService.reloadSystemHosts();
                    } else if ("获取GitHub地址".equals(selectedItem.getValue())) {
                        switchHostsToolService.reloadGithubHosts();
                    } else if ("更新GitHub地址".equals(selectedItem.getValue())) {
                        switchHostsToolService.updateGithubHosts();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
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
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("COMMENT") != null ? "comment" :
                                    null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}