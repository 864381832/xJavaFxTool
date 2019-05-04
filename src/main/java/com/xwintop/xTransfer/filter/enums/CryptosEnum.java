package com.xwintop.xTransfer.filter.enums;

/**
 * @ClassName: CryptosEnum
 * @Description: 加密类型
 * @author: xufeng
 * @date: 2018/5/30 11:01
 */

public enum CryptosEnum {
    Null,//不做任何转换
    Ascii,
    Base32,
    Base64;
    public static CryptosEnum getEnum(String name) {
        for (CryptosEnum cryptosEnum : CryptosEnum.values()) {
            if(cryptosEnum.toString().equalsIgnoreCase(name)){
                return cryptosEnum;
            }
        }
        return null;
    }
}
