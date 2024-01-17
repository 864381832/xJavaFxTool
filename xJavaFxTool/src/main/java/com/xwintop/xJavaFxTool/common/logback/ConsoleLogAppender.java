package com.xwintop.xJavaFxTool.common.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.scene.control.TextArea;
import lombok.Data;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ConsoleLogAppender
 * @Description: 日志打印控制台
 * @author: xufeng
 * @date: 2019/4/25 0025 23:18
 */

@Data
public class ConsoleLogAppender extends OutputStreamAppender<ILoggingEvent> {
    public final static List<TextArea> textAreaList = new ArrayList<>();

    @Override
    public void start() {
        OutputStream targetStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                for (TextArea textArea : textAreaList) {
                    textArea.appendText(b + "\n");
                }
            }

            @Override
            public void write(byte[] b) throws IOException {
                for (TextArea textArea : textAreaList) {
                    textArea.appendText(new String(b) + "\n");
                }
            }
        };
        setOutputStream(targetStream);
        super.start();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (eventObject.getLevel() == Level.ERROR) {
            try {
                TooltipUtil.showToast("发生错误:\n" + eventObject.getFormattedMessage());
            } catch (Exception e) {

            }
        }
        super.append(eventObject);
    }
}
