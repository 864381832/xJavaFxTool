package com.xwintop.xJavaFxTool.services.littleTools;
import com.xwintop.xJavaFxTool.controller.littleTools.TimeToolController;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@Log4j
public class TimeToolService{
    private TimeToolController timeToolController;
    public TimeToolService(TimeToolController timeToolController) {
        this.timeToolController = timeToolController;
    }

    //时间字符转字符戳
    public void convert(){
        String curTimeFormatter = timeToolController.getChoiceBoxTimeFormatter().getValue();
        String timeStr = timeToolController.getTextFileldTimeStr().getText().trim();
        if (StringUtils.isBlank(timeStr)) {
            timeToolController.getTextAreaResult().setText("没有输入时间字符！");
            return;
        }
        try {
//            timeToolController.getTextFileldTimeStr2().setText(Long.toString(new SimpleDateFormat(curTimeFormatter).parse(timeStr).getTime()));
            timeToolController.getTextFileldTimeStr2().setText(Long.toString(DateUtils.parseDate(timeStr,curTimeFormatter).getTime()));
            timeToolController.getTextAreaResult().setText("转换成功！");
            TooltipUtil.showToast("转换成功");
        } catch (Exception e) {
            timeToolController.getTextFileldTimeStr2().setText("");
            timeToolController.getTextAreaResult().setText(e.getMessage());
        }
    }

    //字符戳转时间字符
    public void revert(){
        String curTimeFormatter = timeToolController.getChoiceBoxTimeFormatter().getValue();
        String timestamp = timeToolController.getTextFileldTimeStr2().getText().trim();
        if (StringUtils.isBlank(timestamp)) {
            timeToolController.getTextAreaResult().setText("没有输入时间戳！");
            return;
        }
        try {
            timeToolController.getTextFileldTimeStr()
//                    .setText(new SimpleDateFormat(curTimeFormatter).format(new Date(Long.parseLong(timestamp))));
                    .setText(DateFormatUtils.format(Long.parseLong(timestamp),curTimeFormatter));
            timeToolController.getTextAreaResult().setText("转换成功！");
            TooltipUtil.showToast("转换成功");
        } catch (Exception e) {
            timeToolController.getTextFileldTimeStr().setText("");
            timeToolController.getTextAreaResult().setText(e.getMessage());
        }
    }

    //计算时间差
    public void calculatePoorAction(){

    }

    //时间叠加
    public void addTimeAction(){
        Date startTime = new Date(Long.parseLong(timeToolController.getStartTime2TextField().getText()));
        int addTime = Integer.parseInt(timeToolController.getAddTimeTextField().getText());
        String addTimeChoiceBoxString = timeToolController.getAddTimeChoiceBox().getValue();
        if(timeToolController.getTimeSuffixFormatter()[0].equals(addTimeChoiceBoxString)){
            startTime = DateUtils.addDays(startTime,addTime);
        }else if(timeToolController.getTimeSuffixFormatter()[1].equals(addTimeChoiceBoxString)){
            startTime = DateUtils.addWeeks(startTime,addTime);
        }else if(timeToolController.getTimeSuffixFormatter()[2].equals(addTimeChoiceBoxString)){
            startTime = DateUtils.addMonths(startTime,addTime);
        }else if(timeToolController.getTimeSuffixFormatter()[3].equals(addTimeChoiceBoxString)){
            startTime = DateUtils.addYears(startTime,addTime);
        }else if(timeToolController.getTimeSuffixFormatter()[4].equals(addTimeChoiceBoxString)){
            startTime = DateUtils.addHours(startTime,addTime);
        }else if(timeToolController.getTimeSuffixFormatter()[5].equals(addTimeChoiceBoxString)){
            startTime = DateUtils.addMinutes(startTime,addTime);
        }else if(timeToolController.getTimeSuffixFormatter()[6].equals(addTimeChoiceBoxString)){
            startTime = DateUtils.addSeconds(startTime,addTime);
        }else if(timeToolController.getTimeSuffixFormatter()[7].equals(addTimeChoiceBoxString)){
            startTime = DateUtils.addMilliseconds(startTime,addTime);
        }else if(timeToolController.getTimeSuffixFormatter()[8].equals(addTimeChoiceBoxString)){
//            startTime = DateUtils.add(startTime,addTime);
        }
    }
}