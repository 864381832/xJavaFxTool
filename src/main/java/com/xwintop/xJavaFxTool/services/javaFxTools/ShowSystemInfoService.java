package com.xwintop.xJavaFxTool.services.javaFxTools;

import com.alibaba.fastjson.JSON;
import com.xwintop.xJavaFxTool.controller.javaFxTools.ShowSystemInfoController;
import com.xwintop.xcore.util.FileUtil;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.net.InetAddress;
import java.util.*;

/**
 * @ClassName: ShowSystemInfoService
 * @Description: 显示系统信息
 * @author: xufeng
 * @date: 2017/11/28 22:17
 */
@Getter
@Setter
@Log4j
public class ShowSystemInfoService {

    private ShowSystemInfoController showSystemInfoController;
    private List<Timer> timerList = new ArrayList<Timer>();//统一管理线程

    private SystemInfo si = new SystemInfo();
    private HardwareAbstractionLayer hal = si.getHardware();

    public void showOverviewCpuLineChart() {
        try {
            CentralProcessor processor = hal.getProcessor();
            XYChart.Series[] series = new XYChart.Series[processor.getLogicalProcessorCount()];
            for (int i = 0; i < series.length; i++) {
                series[i] = new XYChart.Series();
                series[i].setName("第" + (i + 1) + "块CPU信息");
            }
            showSystemInfoController.getOverviewCpuLineChart().getData().addAll(series);
            showSystemInfoController.getOverviewCpuLineChart().getXAxis().setTickLabelsVisible(false);
            showSystemInfoController.getOverviewCpuLineChart().getYAxis().setMaxHeight(1);
            Timer timer = new Timer();
            timerList.add(timer);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        String xValue = "" + System.currentTimeMillis();
                        double[] load = processor.getProcessorCpuLoadBetweenTicks();
                        for (int i = 0; i < series.length; i++) {// 不管是单块CPU还是多CPU都适用
                            final int ii = i;
                            Platform.runLater(() -> {
                                if (series[ii].getData().size() > 60) {
                                    series[ii].getData().remove(0);
                                }
                                series[ii].getData().add(new XYChart.Data(xValue, load[ii]));
                            });
                        }
                    } catch (Exception e) {
                        log.error("cpu使用率获取失败：", e);
                    }
                }
            }, 0, 1000);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void showOverviewMemoryLineChart() {
        try {
            GlobalMemory memory = hal.getMemory();
            LineChart lineChart = showSystemInfoController.getOverviewMemoryLineChart();
//            lineChart.setCreateSymbols(false);
            XYChart.Series<String, Integer> series = new XYChart.Series<String, Integer>();
            lineChart.getData().add(series);
            lineChart.getXAxis().setTickLabelsVisible(false);
            lineChart.getYAxis().setMaxHeight(memory.getTotal() / 1048576L);
            lineChart.setLegendVisible(false);
            Timer timer = new Timer();
            timerList.add(timer);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        try {
                            if (series.getData().size() > 60) {
                                series.getData().remove(0);
                            }
                            series.getData().add(new XYChart.Data("" + System.currentTimeMillis(), (memory.getTotal() - memory.getAvailable()) / 1048576L));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }, 0, 1000);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void showOverviewDiskLineChart() {
        try {
            Timer timer = new Timer();
            timerList.add(timer);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                }
            }, 1000);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void showOverviewNetLineChart() {
        try {
            Timer timer = new Timer();
            timerList.add(timer);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                }
            }, 1000);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void showDiskInfo() {
        try {
            OperatingSystem os = si.getOperatingSystem();
            FileSystem fileSystem = os.getFileSystem();
            OSFileStore[] fsArray = fileSystem.getFileStores();
            List dataList = new ArrayList();
            for (int i = 0; i < fsArray.length; i++) {
                OSFileStore fs = fsArray[i];
                Map map = new HashMap();
                map.put("id", "" + i);
                map.put("name", fs.getName());
                map.put("parent", "");
                map.put("value", fs.getTotalSpace());
                map.put("showValue", FileUtil.FormetFileSize(fs.getTotalSpace()));

                Map map1 = new HashMap();
                map1.put("id", "" + i + '1');
                map1.put("name", "已用");
                map1.put("parent", "" + i);
                map1.put("value", fs.getTotalSpace() - fs.getUsableSpace());
                map1.put("showValue", FileUtil.FormetFileSize((fs.getTotalSpace() - fs.getUsableSpace())));

                Map map2 = new HashMap();
                map2.put("id", "" + i + '2');
                map2.put("name", "剩余");
                map2.put("parent", "" + i);
                map2.put("value", fs.getUsableSpace());
                map2.put("showValue", FileUtil.FormetFileSize(fs.getUsableSpace()));
                dataList.add(map);
                dataList.add(map1);
                dataList.add(map2);
            }
            String s = JSON.toJSONString(dataList);
            showSystemInfoController.getDiskWebView().getEngine().executeScript("updateData(" + s + ")");
        } catch (Exception e) {
            log.error("获取磁盘空间失败", e);
        }
    }


    /**
     * 显示vm信息
     */
    public void showVmInfo() {//显示Vm信息
        try {
            Runtime r = Runtime.getRuntime();
            Properties props = System.getProperties();
            InetAddress addr = InetAddress.getLocalHost();

            String ip = addr.getHostAddress();
            Map<String, String> map = System.getenv();
            String userName = map.get("USERNAME");// 获取用户名
            String computerName = map.get("COMPUTERNAME");// 获取计算机名
            String userDomain = map.get("USERDOMAIN");// 获取计算机域名
            StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append("用户名:    " + userName);
            stringBuffer.append("\n计算机名:    " + computerName);
            stringBuffer.append("\n计算机域名:    " + userDomain);
            stringBuffer.append("\n本地ip地址:    " + ip);
            stringBuffer.append("\n本地主机名:    " + addr.getHostName());
            stringBuffer.append("\nJVM可以使用的总内存:    " + r.totalMemory());
            stringBuffer.append("\nJVM可以使用的剩余内存:    " + r.freeMemory());
            stringBuffer.append("\nJVM可以使用的处理器个数:    " + r.availableProcessors());
            stringBuffer.append("\nJava的运行环境版本：    " + props.getProperty("java.version"));
            stringBuffer.append("\nJava的运行环境供应商：    " + props.getProperty("java.vendor"));
            stringBuffer.append("\nJava供应商的URL：    " + props.getProperty("java.vendor.url"));
            stringBuffer.append("\nJava的安装路径：    " + props.getProperty("java.home"));
            stringBuffer.append("\nJava的虚拟机规范版本：    " + props.getProperty("java.vm.specification.version"));
            stringBuffer.append("\nJava的虚拟机规范供应商：    " + props.getProperty("java.vm.specification.vendor"));
            stringBuffer.append("\nJava的虚拟机规范名称：    " + props.getProperty("java.vm.specification.name"));
            stringBuffer.append("\nJava的虚拟机实现版本：    " + props.getProperty("java.vm.version"));
            stringBuffer.append("\nJava的虚拟机实现供应商：    " + props.getProperty("java.vm.vendor"));
            stringBuffer.append("\nJava的虚拟机实现名称：    " + props.getProperty("java.vm.name"));
            stringBuffer.append("\nJava运行时环境规范版本：    " + props.getProperty("java.specification.version"));
            stringBuffer.append("\nJava运行时环境规范供应商：    " + props.getProperty("java.specification.vender"));
            stringBuffer.append("\nJava运行时环境规范名称：    " + props.getProperty("java.specification.name"));
            stringBuffer.append("\nJava的类格式版本号：    " + props.getProperty("java.class.version"));
            stringBuffer.append("\nJava的类路径：    " + props.getProperty("java.class.path"));
            stringBuffer.append("\n加载库时搜索的路径列表：    " + props.getProperty("java.library.path"));
            stringBuffer.append("\n默认的临时文件路径：    " + props.getProperty("java.io.tmpdir"));
            stringBuffer.append("\n一个或多个扩展目录的路径：    " + props.getProperty("java.ext.dirs"));
            stringBuffer.append("\n操作系统的名称：    " + props.getProperty("os.name"));
            stringBuffer.append("\n操作系统的构架：    " + props.getProperty("os.arch"));
            stringBuffer.append("\n操作系统的版本：    " + props.getProperty("os.version"));
            stringBuffer.append("\n文件分隔符：    " + props.getProperty("file.separator"));
            stringBuffer.append("\n路径分隔符：    " + props.getProperty("path.separator"));
            stringBuffer.append("\n行分隔符：    " + props.getProperty("line.separator"));
            stringBuffer.append("\n用户的账户名称：    " + props.getProperty("user.name"));
            stringBuffer.append("\n用户的主目录：    " + props.getProperty("user.home"));
            stringBuffer.append("\n用户的当前工作目录：    " + props.getProperty("user.dir"));
            showSystemInfoController.getVmTextArea().setText(stringBuffer.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 停止所以线程
     */
    public void stopTimerList() {
        for (Timer timer : timerList) {
            timer.cancel();
            timer = null;
        }
        timerList.clear();
    }

    public ShowSystemInfoService(ShowSystemInfoController showSystemInfoController) {
        this.showSystemInfoController = showSystemInfoController;
    }
}