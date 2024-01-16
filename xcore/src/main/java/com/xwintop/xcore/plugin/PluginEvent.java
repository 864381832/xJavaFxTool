package com.xwintop.xcore.plugin;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * 插件生命周期相关事件，可在插件 root 节点中接收事件
 */
public class PluginEvent extends Event {

    /**
     * 插件在框架中完成初始化，即完成 FXMLLoader.load()
     */
    public static final EventType<PluginEvent> PLUGIN_INITIALIZED = new EventType<>(Event.ANY, "PLUGIN_INITIALIZED");

    /**
     * 插件即将被卸载
     */
    public static final EventType<PluginEvent> PLUGIN_UNLOADING = new EventType<>(Event.ANY, "PLUGIN_UNLOADING");

    public PluginEvent(Object source, EventTarget target, EventType<? extends Event> eventType) {
        super(source, target, eventType);
    }

    public PluginEvent(Object source, EventType<? extends Event> eventType) {
        super(source, null, eventType);
    }
}
