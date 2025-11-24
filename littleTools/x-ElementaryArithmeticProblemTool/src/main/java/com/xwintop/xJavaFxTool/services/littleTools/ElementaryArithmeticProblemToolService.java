package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.ElementaryArithmeticProblemToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@Slf4j
public class ElementaryArithmeticProblemToolService {
    private ElementaryArithmeticProblemToolController elementaryArithmeticProblemToolController;

    public ElementaryArithmeticProblemToolService(ElementaryArithmeticProblemToolController elementaryArithmeticProblemToolController) {
        this.elementaryArithmeticProblemToolController = elementaryArithmeticProblemToolController;
    }

    public void startBuildAction() throws Exception {
        int maxNumber = 10;
        if (elementaryArithmeticProblemToolController.getMaxNumber1RadioButton().isSelected()) {
            maxNumber = 10;
        } else if (elementaryArithmeticProblemToolController.getMaxNumber2RadioButton().isSelected()) {
            maxNumber = 100;
        } else {
            maxNumber = elementaryArithmeticProblemToolController.getMaxNumberSpinner().getValue();
        }
        int zuiduoshuzi = elementaryArithmeticProblemToolController.getBuildNumberSpinner2().getValue();
        elementaryArithmeticProblemToolController.getJieguoTextArea().clear();
        Map<String, String> jiSuangMap = new HashMap<>();
        for (Integer i = 0; i < elementaryArithmeticProblemToolController.getBuildNumberSpinner1().getValue(); i++) {
            String[] jiSuangString = getJiSuang(maxNumber, zuiduoshuzi);
            while (jiSuangMap.containsKey(jiSuangString[0])) {
                jiSuangString = getJiSuang(maxNumber, zuiduoshuzi);
            }
            jiSuangMap.put(jiSuangString[0], jiSuangString[1]);
        }
        AtomicInteger index = new AtomicInteger();
        StringBuilder keyString = new StringBuilder();
        StringBuilder valueString = new StringBuilder();
        jiSuangMap.forEach((key, value) -> {
            index.getAndIncrement();
            if (elementaryArithmeticProblemToolController.getBuildTypeRadioButton1().isSelected()) {
                keyString.append(index.get() + "、 " + key + " = \n");
            } else if (elementaryArithmeticProblemToolController.getBuildTypeRadioButton2().isSelected()) {
                keyString.append(index.get() + "、 " + key + " =    （答案：" + value + " ）\n");
            } else if (elementaryArithmeticProblemToolController.getBuildTypeRadioButton3().isSelected()) {
                keyString.append(index.get() + "、 " + key + " = \n");
                valueString.append(index.get() + "、 " + value + "\n");
            }
        });
        elementaryArithmeticProblemToolController.getJieguoTextArea().appendText(keyString.toString());
        elementaryArithmeticProblemToolController.getJieguoTextArea().appendText("\n\n\n");
        elementaryArithmeticProblemToolController.getJieguoTextArea().appendText(valueString.toString());
    }

    public String[] getJiSuang(int maxNumber, int zuiduoshuzi) throws Exception {
        List<Character> operList = new ArrayList<>();
        if (elementaryArithmeticProblemToolController.getFuHao1CheckBox().isSelected()) {
            operList.add('+');
        }
        if (elementaryArithmeticProblemToolController.getFuHao2CheckBox().isSelected()) {
            operList.add('-');
        }
        if (elementaryArithmeticProblemToolController.getFuHao3CheckBox().isSelected()) {
            operList.add('*');
        }
        if (elementaryArithmeticProblemToolController.getFuHao4CheckBox().isSelected()) {
            operList.add('/');
        }
        if (operList.isEmpty()) {
            throw new Exception("未选择任何运算符号");
        }
        Random random = new Random();
        int zuiDuoShuZi = 2 + random.nextInt(zuiduoshuzi - 1);
        StringBuffer stringBuffer = new StringBuffer();
        int start = -1;
        int ent = -1;
        if (elementaryArithmeticProblemToolController.getFuHao5CheckBox().isSelected()) {
            start = random.nextInt(zuiDuoShuZi - 1);
            ent = start + 1 + random.nextInt(zuiDuoShuZi - start - 1);
        }
        if (start == 0) {
            stringBuffer.append("(");
        }
        stringBuffer.append(random.nextInt(maxNumber));
        for (int i = 1; i < zuiDuoShuZi; i++) {
            stringBuffer.append(operList.get(random.nextInt(operList.size())));
            if (i == start) {
                stringBuffer.append("(");
            }
            stringBuffer.append(random.nextInt(maxNumber));
            if (i == ent) {
                stringBuffer.append(")");
            }
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine se = manager.getEngineByName("js");
        String operatorString = stringBuffer.toString();
        Double d = Double.parseDouble(se.eval(operatorString).toString());
        if (!elementaryArithmeticProblemToolController.getFushuCheckBox().isSelected()) {
            if (d < 0) {
                d = 0.1;
            }
        }
        if (d == Math.round(d) && d <= maxNumber) {
            return new String[]{operatorString.replaceAll("\\*", "×").replaceAll("/", "÷"), Integer.toString(d.intValue())};
        } else {
            return getJiSuang(maxNumber, zuiduoshuzi);
        }
    }
}