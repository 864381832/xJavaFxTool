package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.CronExpBuilderController;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.CronExpression;

/**
 * @ClassName: CronExpBuilderService
 * @Description: Cron表达式生成工具
 * @author: xufeng
 * @date: 2020/1/2 16:34
 */

@Slf4j
@Getter
@Setter
public class CronExpBuilderService {
    private CronExpBuilderController cronExpBuilderController;

    public CronExpBuilderService(CronExpBuilderController cronExpBuilderController) {
        this.cronExpBuilderController = cronExpBuilderController;
    }

    public void parseActionPerformed() throws Exception {
        String cronExpString = cronExpBuilderController.getJTF_Cron_Exp().getText().trim();
        if (StringUtils.isEmpty(cronExpString)) {
            return;
        }
        String[] regs = cronExpString.split(" ");
        for (int i = 0; i < cronExpBuilderController.getTypeNameString().length - 1; i++) {
            this.initObj(regs[i], cronExpBuilderController.getTypeNameString()[i]);
        }
        this.initYear(regs.length > 6 ? regs[6] : "");
    }

    public void parseActionPerformedCronTab() throws Exception {
        String cronString = CronExpBuilderService.getCronTabToCron(cronExpBuilderController.getJTF_Cron_ExpCronTab().getText());
        cronExpBuilderController.getJTF_Cron_Exp().setText(cronString);
        this.parseActionPerformed();
    }

    public void runCronExpressionAction() {
        String cronExpString = cronExpBuilderController.getJTF_Cron_Exp().getText().trim();
        if (StringUtils.isEmpty(cronExpString)) {
            return;
        }
        cronExpBuilderController.getJTA_Schedule_Next().setText("");
        try {
            CronExpression exp = new CronExpression(cronExpString);
            java.util.Date dd = new java.util.Date();
            cronExpBuilderController.getJTF_Schedule_Start().setText(DateFormatUtils.format(dd, "yyyy-MM-dd HH:mm:ss"));
            for (int i = 1; i <= 6; i++) {
                dd = exp.getNextValidTimeAfter(dd);
                cronExpBuilderController.getJTA_Schedule_Next().appendText(i + ": " + DateFormatUtils.format(dd, "yyyy-MM-dd HH:mm:ss") + "\t\t");
                if (i % 2 == 0) {
                    cronExpBuilderController.getJTA_Schedule_Next().appendText("\n");
                }
                dd = new java.util.Date(dd.getTime() + 1000);
            }
        } catch (Exception e) {
            cronExpBuilderController.getJTA_Schedule_Next().setText(e.getMessage());
        }
    }

    private void initObj(String strVal, String checkType) throws Exception {
        String checkTypeLowerCase = checkType.toLowerCase();
        ToggleGroup toggleGroup = (ToggleGroup) FieldUtils.readField(cronExpBuilderController, "toggleGroup" + checkType, true);
        if ("*".equals(strVal)) {
            toggleGroup.selectToggle(toggleGroup.getToggles().get(0));
        } else if (strVal.contains("-")) {
            String[] ary = strVal.split("-");
            ((Spinner<Integer>) FieldUtils.readField(cronExpBuilderController, checkTypeLowerCase + "Start_0", true)).getEditor()
                    .setText(ary[0]);
            ((Spinner<Integer>) FieldUtils.readField(cronExpBuilderController, checkTypeLowerCase + "End_0", true)).getEditor()
                    .setText(ary[1]);
        } else if (strVal.contains("/")) {
            String[] ary = strVal.split("/");
            ((Spinner<Integer>) FieldUtils.readField(cronExpBuilderController, checkTypeLowerCase + "Start_1", true)).getEditor()
                    .setText(ary[0]);
            ((Spinner<Integer>) FieldUtils.readField(cronExpBuilderController, checkTypeLowerCase + "End_1", true)).getEditor()
                    .setText(ary[1]);
        } else if (("Day".equals(checkType) || "Month".equals(checkType) || "Week".equals(checkType))
                && "?".equals(strVal)) {
            toggleGroup.selectToggle(toggleGroup.getToggles().get(3));
        } else if ("Day".equals(checkType) && strVal.contains("W")) {
            String[] ary = strVal.split("W");
            ((Spinner<Integer>) FieldUtils.readField(cronExpBuilderController, checkTypeLowerCase + "Start_2", true)).getEditor()
                    .setText(ary[0]);
        } else if ("Day".equals(checkType) && "L".equals(strVal)) {
            toggleGroup.selectToggle(toggleGroup.getToggles().get(5));
        } else if ("Week".equals(checkType) && strVal.contains("L")) {
            String[] ary = strVal.split("L");
            ((Spinner<Integer>) FieldUtils.readField(cronExpBuilderController, checkTypeLowerCase + "Start_2", true)).getEditor()
                    .setText(ary[0]);
        } else {
            if (!"?".equals(strVal)) {
                String[] ary = strVal.split(",");
                CheckBox[] checkBox = (CheckBox[]) FieldUtils.readField(cronExpBuilderController, checkTypeLowerCase + "CheckBox", true);
                int addInt = "00".equals(checkBox[0].getText()) ? 0 : 1;
                for (int i = 0; i < checkBox.length; i++) {
                    checkBox[i].setSelected(false);
                }
                for (int i = 0; i < ary.length; i++) {
                    checkBox[Integer.parseInt(ary[i].trim()) - addInt].setSelected(true);
                }
            }
        }
    }

    private void initYear(String strVal) throws Exception {
        if (StringUtils.isEmpty(strVal)) {
            cronExpBuilderController.getRadioButtonYear1().setSelected(true);
        } else if ("*".equals(strVal)) {
            cronExpBuilderController.getRadioButtonYear2().setSelected(true);
        } else if (strVal.contains("-")) {
            String[] ary = strVal.split("-");
            cronExpBuilderController.getYearStart_0().getEditor().setText(ary[0]);
            cronExpBuilderController.getYearEnd_0().getEditor().setText(ary[1]);
        } else {
            cronExpBuilderController.getJTF_Cron_Year().setText(null);
            cronExpBuilderController.getRadioButtonYear1().setSelected(true);
        }
    }

    public static String getCronTabToCron(String cronTab) {
        String[] cronTabs = cronTab.split(" ");
        if (cronTabs.length > 5) {
            return cronTab;
        }
        if (!"?".equals(cronTabs[2]) && !"?".equals(cronTabs[4])) {
            cronTabs[4] = "?";
        }
        return "0 " + String.join(" ", cronTabs);
    }

    public static String getCronToCronTab(String cron) {
        String[] crons = cron.split(" ");
        crons = ArrayUtils.remove(crons, 0);
        if ("?".equals(crons[2])) {
            crons[2] = "*";
        }
        if ("?".equals(crons[4])) {
            crons[4] = "*";
        }
        return String.join(" ", crons);
    }
}
