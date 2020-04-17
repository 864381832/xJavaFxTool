package com.xwintop.xJavaFxTool.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javafx.event.EventType;

/**
 * 应用全局事件注册和触发
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class AppEvents {

    private static final AppEvents instance = new AppEvents();

    /**
     * 触发事件
     *
     * @param appEvent 事件对象
     */
    public static void fire(AppEvent appEvent) {
        List<Consumer> handlers = instance.listeners
            .getOrDefault(appEvent.getEventType(), Collections.emptyList());

        for (Consumer handler : handlers) {
            handler.accept(appEvent);
        }
    }

    /**
     * 注册事件侦听
     *
     * @param eventType 事件类型
     * @param handler   侦听器
     * @param <T>       事件对象类型
     */
    public static <T extends AppEvent> void addEventHandler(EventType<T> eventType, Consumer<T> handler) {
        instance.listeners.computeIfAbsent(eventType, __ -> new ArrayList<>()).add(handler);
    }

    private final Map<EventType, List<Consumer>> listeners = new ConcurrentHashMap<>();
}
