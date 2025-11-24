package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.CharacterConverterController;
import com.xwintop.xJavaFxTool.utils.RadixUtils;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;

/**
 * @ClassName: CharacterConverterService
 * @Description: 编码转换工具
 * @author: xufeng
 * @date: 2018/1/25 14:49
 */

public class CharacterConverterService {
    private CharacterConverterController characterConverterController;

    public CharacterConverterService(CharacterConverterController characterConverterController) {
        this.characterConverterController = characterConverterController;
    }

    /**
     * 字符编码进制前缀字符填充 - 16进制.
     */
    public String encode16RadixAddPrefix(String input, String charset, String prefix)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            // ExCodec.encodeHex 转16进制
            sb.append(prefix).append(Hex.encodeHexString(input.substring(i, i + 1).getBytes(charset)));
        }
        return sb.toString();
    }

    /**
     * 字符编码进制前缀字符填充 - 10进制.
     */
    public String encode10RadixAddPrefix(String input, String charset, String prefix)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            sb.append(prefix).append(RadixUtils.convertRadixString16To10(Hex.encodeHexString(input.substring(i, i + 1).getBytes(charset))));
        }
        return sb.toString();
    }

    /**
     * 字符编码进制前缀字符填充 - 8进制.
     */
    public String encode8RadixAddPrefix(String input, String charset, String prefix)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            sb.append(prefix).append(RadixUtils.convertRadixString16To8(Hex.encodeHexString(input.substring(i, i + 1).getBytes(charset))));
        }
        return sb.toString();
    }

    /**
     * 字符编码进制前缀字符填充 - 2进制.
     */
    public String encode2RadixAddPrefix(String input, String charset, String prefix)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            sb.append(prefix).append(RadixUtils.convertRadixString16To2(Hex.encodeHexString(input.substring(i, i + 1).getBytes(charset))));
        }
        return sb.toString();
    }
}
