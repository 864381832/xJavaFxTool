package com.xwintop.xTransfer.util;

import com.xwintop.xTransfer.messaging.IMessage;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Slf4j
public class ParseVariableCommon {
    private static final char SEPARATOR_BEGIN = '$';
    private static final char SEPARATOR_LEFT = '(';
    private static final char SEPARATOR_RIGHT = ')';
    private static final char SEPARATOR_STR = ':';

    private static final String FILE_NAME = "FILE_NAME";

    private static final String LOGID = "LOGID";
    private static final String ID = "ID";
    private static final String VERSION = "VERSION";
    private static final String DATE = "DATE";
    private static final String IP = "IP";

    public static String parseVariable(String value, IMessage msg, Map extraMap)
            throws Exception {
        if (value == null) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        String key = "";
        boolean isVar = false;
        char next = 0;
        for (int index = 0; index < value.length(); index++) {
            char pos = value.charAt(index);
            // read the variable
            if (isVar) {
                if (pos == SEPARATOR_RIGHT) {
                    result.append(parse(key, msg, extraMap));
                    key = "";
                    isVar = !isVar;
                } else if (index >= value.length() - 1) {
                    throw new Exception("unclosed variable of :" + value);
                } else {
                    key = key + pos;
                }
            } else {
                if (pos == SEPARATOR_BEGIN) {
                    if (index >= value.length() - 1) {
                        result.append(pos);
                    } else {
                        next = value.charAt(++index);
                        if (next == SEPARATOR_BEGIN) {
                            result.append(next);
                        } else if (next == SEPARATOR_LEFT) {
                            if (index >= value.length() - 1) {
                                throw new Exception("unclosed variable of :"
                                        + value);
                            }
                            isVar = !isVar;
                        } else {
                            result.append(next);
                        }
                    }
                } else {
                    result.append(pos);
                }

            }

        }
        return result.toString();
    }

    /**
     * Replace a variable to a value by the message.<br>
     * Variable <b>$(user)</b> would be replace by the receiver of the message.<br>
     * Variable <b>$(msgtype)</b> would be replace by the type of the message.<br>
     * Variable <b>$(logid)</b> would be replace by the id of the message.<br>
     * Variable <b>$(format)</b> would be replace by the format of the message.<br>
     * The variables are case insensitive.
     *
     * @param key the variable name.
     * @param msg the message.
     * @return the value of the variable.
     */
    public static String parse(String key, IMessage msg, Map extraMap) {
        if (key.equalsIgnoreCase(LOGID)) {
            return msg.getId();
        } else if (key.equalsIgnoreCase(ID)) {
            return msg.getId();
        } else if (key.equalsIgnoreCase(VERSION)) {
            return msg.getVersion();
        } else if (key.equalsIgnoreCase(FILE_NAME)) {
            return msg.getFileName();
        } else if (key.equalsIgnoreCase(IP)) {
            try {
                InetAddress inet = InetAddress.getLocalHost();
                return inet.getHostAddress();
            } catch (Exception exc) {
                log.error("get ip occur exception:", exc);
            }
        } else if (key.startsWith(DATE)) {
            String formatStr = key.substring(key.indexOf(SEPARATOR_STR) + 1);
            SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
            Date d = new Date();
            String date = sdf.format(d);
            return date;
        } else {
            String result = msg.getProperties().getProperty(key);
            if (result != null) {
                return result;
            }
        }
        if (extraMap != null) {
            String result = (String) extraMap.get(key);
            if (result != null) {
                return result;
            }
        }
        return key;
    }
}
