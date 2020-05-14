package com.xwintop.xJavaFxTool.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 用于i伪造请求的UserAgent
 * @Author LiFaXin
 * @Date 2020/5/14 17:24
 * @Version
 **/
public class UserAgentUtils {

    private static List<String> userAgents = new ArrayList<>(3);
    static{
        // Firefox
        userAgents.add("Mozilla/5.0 (Windows NT 6.1; rv:51.0) Gecko/20100101 Firefox/51.0");
        // Chrome
        userAgents.add("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.0 Safari/537.36");
        // IE
        userAgents.add("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko");
    }

    /**
     * 获取UserAgent
     * @param index
     * @return
     */
    public static String getUserAgent(int index){
        index = index > userAgents.size()-1 ? userAgents.size()-1 : index;
        return userAgents.get(index);
    }

    /**
     * 获取UserAgent配置数量
     * @return
     */
    public static int getUserAgentSize(){
        return userAgents.size();
    }
}
