package com.xwintop.xJavaFxTool.event;

import org.junit.Test;

public class AppEventsTest {

    @Test
    public void testFireEvent() {
        AppEvents.addEventHandler(PluginEvent.PLUGIN_DOWNLOADED, event -> System.out.println("Plugin downloaded: " + event.getPluginJarInfo()));

        AppEvents.fire(new PluginEvent(PluginEvent.PLUGIN_DOWNLOADED, null));
    }
}