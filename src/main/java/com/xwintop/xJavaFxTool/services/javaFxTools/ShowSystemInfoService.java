package com.xwintop.xJavaFxTool.services.javaFxTools;

import com.xwintop.xJavaFxTool.controller.javaFxTools.ShowSystemInfoController;
import com.xwintop.xJavaFxTool.utils.SigarUtil;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;

import java.net.InetAddress;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

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
    private Sigar sigar = SigarUtil.sigar;

    public void showOverviewCpuLineChart() {
        try {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {

                }
            }, 1000);
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Month");
            final LineChart<String, Number> lineChart
                    = new LineChart<>(xAxis, yAxis);

            lineChart.setTitle("Stock Monitoring, 2010");

            lineChart.setCreateSymbols(false);
            lineChart.setAlternativeRowFillVisible(false);
            XYChart.Series series1 = new XYChart.Series();
            series1.setName("Portfolio 1");

            series1.getData().add(new XYChart.Data("Jan", 23));
            series1.getData().add(new XYChart.Data("Feb", 14));
            series1.getData().add(new XYChart.Data("Mar", 15));
            series1.getData().add(new XYChart.Data("Apr", 24));
            series1.getData().add(new XYChart.Data("May", 34));
            series1.getData().add(new XYChart.Data("Jun", 36));
            series1.getData().add(new XYChart.Data("Jul", 22));
            series1.getData().add(new XYChart.Data("Aug", 45));
            series1.getData().add(new XYChart.Data("Sep", 43));
            series1.getData().add(new XYChart.Data("Oct", 17));
            series1.getData().add(new XYChart.Data("Nov", 29));
            series1.getData().add(new XYChart.Data("Dec", 25));

            XYChart.Series series2 = new XYChart.Series();
            series2.setName("Portfolio 2");
            series2.getData().add(new XYChart.Data("Jan", 33));
            series2.getData().add(new XYChart.Data("Feb", 34));
            series2.getData().add(new XYChart.Data("Mar", 25));
            series2.getData().add(new XYChart.Data("Apr", 44));
            series2.getData().add(new XYChart.Data("May", 39));
            series2.getData().add(new XYChart.Data("Jun", 16));
            series2.getData().add(new XYChart.Data("Jul", 55));
            series2.getData().add(new XYChart.Data("Aug", 54));
            series2.getData().add(new XYChart.Data("Sep", 48));
            series2.getData().add(new XYChart.Data("Oct", 27));
            series2.getData().add(new XYChart.Data("Nov", 37));
            series2.getData().add(new XYChart.Data("Dec", 29));

            XYChart.Series series3 = new XYChart.Series();
            series3.setName("Portfolio 3");
            series3.getData().add(new XYChart.Data("Jan", 44));
            series3.getData().add(new XYChart.Data("Feb", 35));
            series3.getData().add(new XYChart.Data("Mar", 36));
            series3.getData().add(new XYChart.Data("Apr", 33));
            series3.getData().add(new XYChart.Data("May", 31));
            series3.getData().add(new XYChart.Data("Jun", 26));
            series3.getData().add(new XYChart.Data("Jul", 22));
            series3.getData().add(new XYChart.Data("Aug", 25));
            series3.getData().add(new XYChart.Data("Sep", 43));
            series3.getData().add(new XYChart.Data("Oct", 44));
            series3.getData().add(new XYChart.Data("Nov", 45));
            series3.getData().add(new XYChart.Data("Dec", 44));
//            lineChart.getData().addAll(series1, series2, series3);
            showSystemInfoController.getOverviewCpuLineChart().getData().addAll(series1, series2, series3);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void showOverviewMemoryLineChart() {
        try {
            LineChart lineChart = showSystemInfoController.getOverviewMemoryLineChart();
            lineChart.setCreateSymbols(false);
            XYChart.Series<String, Integer> series = new XYChart.Series<String, Integer>();
            lineChart.getData().add(series);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        try {
                            if (series.getData().size() > 60) {
                                series.getData().remove(0);
                            }
                            Mem mem = sigar.getMem();
                            series.getData().add(new XYChart.Data(new Date().toLocaleString(), mem.getUsed()/ 1024L));
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
            new Timer().schedule(new TimerTask() {
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
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {

                }
            }, 1000);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

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

    public ShowSystemInfoService(ShowSystemInfoController showSystemInfoController) {
        this.showSystemInfoController = showSystemInfoController;
    }
}