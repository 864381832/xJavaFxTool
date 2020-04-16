package com.xwintop.xJavaFxTool.event;

import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import javafx.event.Event;
import javafx.event.EventType;

public class PluginEvent extends AppEvent {

    public static final EventType<PluginEvent> PLUGIN_DOWNLOADED = new EventType<>(Event.ANY, "PLUGIN_DOWNLOADED");

    private final PluginJarInfo pluginJarInfo;

    public PluginEvent(EventType<PluginEvent> eventType, PluginJarInfo pluginJarInfo) {
        super(eventType);
        this.pluginJarInfo = pluginJarInfo;
    }

    public PluginJarInfo getPluginJarInfo() {
        return pluginJarInfo;
    }
}
