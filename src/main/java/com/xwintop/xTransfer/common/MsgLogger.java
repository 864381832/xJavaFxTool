package com.xwintop.xTransfer.common;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class MsgLogger {
    public static void info(Map map) {
        String logString = JSON.toJSONString(map);
        log.info(logString);
    }
}
