package com.xwintop.xJavaFxTool.services.littleTools;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.py.Pinyin;

import java.util.List;

/**
 * @ClassName: ZHConverterService
 * @Description: 字符串转换
 * @author: xufeng
 * @date: 2019/4/25 0025 23:36
 */

public class ZHConverterService {
	private String[] codeTypes;
	private String[] pinyinTypes;

	/** 
	 * @Title: changeAction 
	 * @Description: 转换字符串
	 */
	public String changeAction(String simplifiedString, String codeTypeString, String pinyinTypeString,String pinyinTypeSpaceString) {
		String traditionalString = null;
		if (codeTypes[0].equals(codeTypeString)) {
			List<Pinyin> pinyinList = HanLP.convertToPinyinList(simplifiedString);
			StringBuffer stringBuffer = new StringBuffer();
			int i = 1;
			for (Pinyin pinyin : pinyinList) {
				if (pinyin == Pinyin.none5) {
					stringBuffer.append(simplifiedString.charAt(i - 1));
				} else {
					if (pinyinTypes[0].equals(pinyinTypeString)) {
						stringBuffer.append(pinyin.getPinyinWithoutTone());
						// traditionalString = HanLP.convertToPinyinString(simplifiedString, pinyinTypeSpaceString, false);
					} else if (pinyinTypes[1].equals(pinyinTypeString)) {
						stringBuffer.append(pinyin);
					} else if (pinyinTypes[2].equals(pinyinTypeString)) {
						stringBuffer.append(pinyin.getPinyinWithToneMark());
					} else if (pinyinTypes[3].equals(pinyinTypeString)) {
						stringBuffer.append(pinyin.getShengmu());
					} else if (pinyinTypes[4].equals(pinyinTypeString)) {
						stringBuffer.append(pinyin.getYunmu());
					} else if (pinyinTypes[4].equals(pinyinTypeString)) {
						stringBuffer.append(pinyin.getHead());
					}
					if (i < pinyinList.size()) {
						stringBuffer.append(pinyinTypeSpaceString);
					}
				}
				++i;
			}
			traditionalString = stringBuffer.toString();
		} else if (codeTypes[1].equals(codeTypeString)) {
			traditionalString = HanLP.s2t(simplifiedString);
		} else if (codeTypes[2].equals(codeTypeString)) {
			traditionalString = HanLP.s2tw(simplifiedString);
		} else if (codeTypes[3].equals(codeTypeString)) {
			traditionalString = HanLP.s2hk(simplifiedString);
		} else if (codeTypes[4].equals(codeTypeString)) {
			traditionalString = HanLP.t2tw(simplifiedString);
		} else if (codeTypes[5].equals(codeTypeString)) {
			traditionalString = HanLP.t2hk(simplifiedString);
		} else if (codeTypes[6].equals(codeTypeString)) {
			traditionalString = HanLP.hk2tw(simplifiedString);
		} else if (codeTypes[7].equals(codeTypeString)) {
//			traditionalString = MoneyToChineseUtil.toChinese(simplifiedString);
			traditionalString = Convert.digitToChinese(NumberUtil.parseDouble(simplifiedString.replaceAll(",", "")));
		}
		return traditionalString;
	}

	/** 
	 * @Title: restoreAction 
	 * @Description: 还原字符串
	 */
	public String restoreAction(String traditionalString, String codeTypeString) {
		String simplifiedString = null;
		if (codeTypes[0].equals(codeTypeString)) {
			simplifiedString = "拼音不支持还原。";
		} else if (codeTypes[1].equals(codeTypeString)) {
			simplifiedString = HanLP.t2s(traditionalString);
		} else if (codeTypes[2].equals(codeTypeString)) {
			simplifiedString = HanLP.tw2s(traditionalString);
		} else if (codeTypes[3].equals(codeTypeString)) {
			simplifiedString = HanLP.hk2s(traditionalString);
		} else if (codeTypes[4].equals(codeTypeString)) {
			simplifiedString = HanLP.tw2t(traditionalString);
		} else if (codeTypes[5].equals(codeTypeString)) {
			simplifiedString = HanLP.hk2t(traditionalString);
		} else if (codeTypes[6].equals(codeTypeString)) {
			simplifiedString = HanLP.tw2hk(traditionalString);
		} else if (codeTypes[7].equals(codeTypeString)) {
//			simplifiedString = ""+ MoneyToChineseUtil.cnNumericToArabic(traditionalString);
			simplifiedString = ""+ Convert.chineseToNumber(traditionalString);
		}
		return simplifiedString;
	}

	public void setCodeTypes(String[] codeTypes) {
		this.codeTypes = codeTypes;
	}

	public void setPinyinTypes(String[] pinyinTypes) {
		this.pinyinTypes = pinyinTypes;
	}
}
