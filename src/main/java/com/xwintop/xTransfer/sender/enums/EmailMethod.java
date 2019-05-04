package com.xwintop.xTransfer.sender.enums;

/**
 * @ClassName: EmailMethod
 * @Description: 发送邮件类型
 * @author: xufeng
 * @date: 2018/6/22 15:04
 */

public enum EmailMethod {
    attachment,//附件类型
    raw,  //原始邮件
    plain;//文本类型

    public static EmailMethod getEnum(String name) {
        for (EmailMethod emailMethod : EmailMethod.values()) {
            if (emailMethod.toString().equalsIgnoreCase(name)) {
                return emailMethod;
            }
        }
        return null;
    }
}
