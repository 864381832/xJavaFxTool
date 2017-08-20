package com.xwintop.xJavaFxTool.tools;

import java.util.List;

import org.junit.Test;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import com.hankcs.hanlp.dictionary.py.PinyinDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

public class HanLPTest {
	@Test
	public void test() {
		System.out.println("首次编译运行时，HanLP会自动构建词典缓存，请稍候……");
		// HanLP.Config.enableDebug(); // 为了避免你等得无聊，开启调试模式说点什么:-)
		System.out.println(HanLP.segment("商品和服务"));

		List<Term> termList = StandardTokenizer.segment("商品和服务");
		System.out.println(termList);

		System.out.println(HanLP.convertToPinyinString("重载，爱", "", false));;
	}
	
	@Test
	public void testPinyin()
    {
        String text = "重载，不是重任！";
        List<Pinyin> pinyinList = HanLP.convertToPinyinList(text);
        System.out.print("原文,");
        for (char c : text.toCharArray())
        {
            System.out.printf("%c,", c);
        }
        System.out.println();

        System.out.print("拼音（数字音调）,");
        for (Pinyin pinyin : pinyinList)
        {
            System.out.printf("%s,", pinyin);
        }
        System.out.println();

        System.out.print("拼音（符号音调）,");
        for (Pinyin pinyin : pinyinList)
        {
            System.out.printf("%s,", pinyin.getPinyinWithToneMark());
        }
        System.out.println();

        System.out.print("拼音（无音调）,");
        for (Pinyin pinyin : pinyinList)
        {
            System.out.printf("%s,", pinyin.getPinyinWithoutTone());
        }
        System.out.println();

        System.out.print("声调,");
        for (Pinyin pinyin : pinyinList)
        {
            System.out.printf("%s,", pinyin.getTone());
        }
        System.out.println();

        System.out.print("声母,");
        for (Pinyin pinyin : pinyinList)
        {
            System.out.printf("%s,", pinyin.getShengmu());
        }
        System.out.println();

        System.out.print("韵母,");
        for (Pinyin pinyin : pinyinList)
        {
            System.out.printf("%s,", pinyin.getYunmu());
        }
        System.out.println();

        System.out.print("输入法头,");
        for (Pinyin pinyin : pinyinList)
        {
            System.out.printf("%s,", pinyin.getHead());
        }
        System.out.println();

        // 拼音转换可选保留无拼音的原字符
        System.out.println(HanLP.convertToPinyinString("截至2012年，", " ", true));
        System.out.println(HanLP.convertToPinyinString("截至2012年，", " ", false));
    }
}
