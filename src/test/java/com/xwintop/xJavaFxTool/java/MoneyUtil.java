package com.xwintop.xJavaFxTool.java;

import org.junit.Test;

import com.xwintop.xcore.util.little.MoneyCnNumericToArabicUtil;
import com.xwintop.xcore.util.little.MoneyToChineseUtil;

public class MoneyUtil {
	@Test
	public void testToChinese() {
		String number = "1.23";
		System.out.println(number + " " + MoneyToChineseUtil.toChinese(number));
		number = "1234567890123456.123";
		System.out.println(number + " " + MoneyToChineseUtil.toChinese(number));
		number = "0.0798";
		System.out.println(number + " " + MoneyToChineseUtil.toChinese(number));
		number = "10,001,000.09";
		System.out.println(number + " " + MoneyToChineseUtil.toChinese(number));
		number = "01.107700";
		System.out.println(number + " " + MoneyToChineseUtil.toChinese(number));
	}

	@Test
	public void testCnNumericToArabic() {
		int val = 0;
		long s = System.nanoTime();
		val = MoneyCnNumericToArabicUtil.cnNumericToArabic("三亿二千零六万七千五百六", true);
		System.out.println(val);
		val = MoneyCnNumericToArabicUtil.cnNumericToArabic("一九九八", true);
		System.out.println(val);
		long e = System.nanoTime();
		System.out.format("Done[" + val + "], cost: %.5fsec\n", ((float) (e - s)) / 1E9);
	}
}
