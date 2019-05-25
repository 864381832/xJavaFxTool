package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.TimeToolController;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @ClassName: TimeToolService
 * @Description: Time转换工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:36
 */

@Getter
@Setter
@Slf4j
public class TimeToolService {
    public static final String 按毫秒计算相差 = "按毫秒计算相差:";
    private TimeToolController timeToolController;

    public TimeToolService(TimeToolController timeToolController) {
        this.timeToolController = timeToolController;
    }

    //时间字符转字符戳
    public void convert() {
        String curTimeFormatter = timeToolController.getChoiceBoxTimeFormatter().getValue();
        TimeZone curTimeZone = TimeZone.getTimeZone(timeToolController.getChoiceBoxTimeZone().getValue());
        String timeStr = timeToolController.getTextFileldTimeStr().getText().trim();
        if (StringUtils.isBlank(timeStr)) {
            timeToolController.getTextAreaResult().setText("没有输入时间字符！");
            return;
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(curTimeFormatter);
            simpleDateFormat.setTimeZone(curTimeZone);
            timeToolController.getTextFileldTimeStr2().setText(Long.toString(simpleDateFormat.parse(timeStr).getTime()));
//            timeToolController.getTextFileldTimeStr2().setText(Long.toString(DateUtils.parseDate(timeStr, curTimeFormatter).getTime()));
            timeToolController.getTextAreaResult().setText("转换成功！");
            TooltipUtil.showToast("转换成功");
        } catch (Exception e) {
            timeToolController.getTextFileldTimeStr2().setText("");
            timeToolController.getTextAreaResult().setText(e.getMessage());
        }
    }

    //字符戳转时间字符
    public void revert() {
        String curTimeFormatter = timeToolController.getChoiceBoxTimeFormatter().getValue();
        TimeZone curTimeZone = TimeZone.getTimeZone(timeToolController.getChoiceBoxTimeZone().getValue());
        String timestamp = timeToolController.getTextFileldTimeStr2().getText().trim();
        if (StringUtils.isBlank(timestamp)) {
            timeToolController.getTextAreaResult().setText("没有输入时间戳！");
            return;
        }
        try {
            timeToolController.getTextFileldTimeStr()
                    .setText(DateFormatUtils.format(Long.parseLong(timestamp), curTimeFormatter, curTimeZone));
            timeToolController.getTextAreaResult().setText("转换成功！");
            TooltipUtil.showToast("转换成功");
        } catch (Exception e) {
            timeToolController.getTextFileldTimeStr().setText("");
            timeToolController.getTextAreaResult().setText(e.getMessage());
        }
    }

    //计算时间差
    public void calculatePoorAction() {
        try {
            long startTime = Long.parseLong(timeToolController.getStartTimeTextField().getText());
            long endTime = Long.parseLong(timeToolController.getEndTimeTextField().getText());
            if (startTime > endTime) {
                long time = startTime;
                startTime = endTime;
                endTime = time;
            }
            long l = Math.abs(endTime - startTime);
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            long ms = (l - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);

            long second1 = l / 1000;//除以1000是为了转换成秒
            long minute1 = second1 / (60);
            long hour1 = minute1 / (60);
            long day1 = hour1 / (24);
            long weeks = day1 / 7;
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append("\n起始时间:" + DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss.SSS"));
            stringBuffer.append("\n结束时间:" + DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss.SSS"));
            stringBuffer.append("\n两个时间相差:" + day + "天" + hour + "小时" + min + "分" + s + "秒" + ms + "毫秒");
            stringBuffer.append("\n按周计算相差:" + weeks + "周");
            stringBuffer.append("\n按天计算相差:" + day1 + "天");
            stringBuffer.append("\n按时计算相差:" + hour1 + "时");
            stringBuffer.append("\n按分计算相差:" + minute1 + "分");
            stringBuffer.append("\n按秒计算相差:" + second1 + "秒");
            stringBuffer.append("\n按毫秒计算相差" + l + "毫秒");
            timeToolController.getTextAreaResult().setText("转换成功！\n" + stringBuffer.toString());
            TooltipUtil.showToast("转换成功");
        } catch (Exception e) {
            timeToolController.getTextAreaResult().setText(e.getMessage());
        }
    }

    //时间叠加
    public void addTimeAction() {
        try {
            Date startTime = new Date(Long.parseLong(timeToolController.getStartTime2TextField().getText()));
            int addTime = Integer.parseInt(timeToolController.getAddTimeTextField().getText());
            String addTimeChoiceBoxString = timeToolController.getAddTimeChoiceBox().getValue();
            Date endTime = null;
            if (timeToolController.getTimeSuffixFormatter()[0].equals(addTimeChoiceBoxString)) {
                endTime = DateUtils.addDays(startTime, addTime);
            } else if (timeToolController.getTimeSuffixFormatter()[1].equals(addTimeChoiceBoxString)) {
                endTime = DateUtils.addWeeks(startTime, addTime);
            } else if (timeToolController.getTimeSuffixFormatter()[2].equals(addTimeChoiceBoxString)) {
                endTime = DateUtils.addMonths(startTime, addTime);
            } else if (timeToolController.getTimeSuffixFormatter()[3].equals(addTimeChoiceBoxString)) {
                endTime = DateUtils.addYears(startTime, addTime);
            } else if (timeToolController.getTimeSuffixFormatter()[4].equals(addTimeChoiceBoxString)) {
                endTime = DateUtils.addHours(startTime, addTime);
            } else if (timeToolController.getTimeSuffixFormatter()[5].equals(addTimeChoiceBoxString)) {
                endTime = DateUtils.addMinutes(startTime, addTime);
            } else if (timeToolController.getTimeSuffixFormatter()[6].equals(addTimeChoiceBoxString)) {
                endTime = DateUtils.addSeconds(startTime, addTime);
            } else if (timeToolController.getTimeSuffixFormatter()[7].equals(addTimeChoiceBoxString)) {
                endTime = DateUtils.addMilliseconds(startTime, addTime);
            } else if (timeToolController.getTimeSuffixFormatter()[8].equals(addTimeChoiceBoxString)) {
                endTime = new Date(startTime.getTime() + addTime);
            }
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append("\n起始时间: " + DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss.SSS"));
            stringBuffer.append("\n结果时间戳: " + endTime.getTime());
            stringBuffer.append("\n结果时间: " + DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss.SSS"));
            timeToolController.getTextAreaResult().setText("转换成功！\n" + stringBuffer.toString());
            TooltipUtil.showToast("转换成功");
        } catch (Exception e) {
            timeToolController.getTextAreaResult().setText(e.getMessage());
        }
    }
}