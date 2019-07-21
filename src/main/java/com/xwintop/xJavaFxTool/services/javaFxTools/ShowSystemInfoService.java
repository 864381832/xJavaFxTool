package com.xwintop.xJavaFxTool.services.javaFxTools;

import com.alibaba.fastjson.JSON;
import com.xwintop.xJavaFxTool.controller.javaFxTools.ShowSystemInfoController;
import com.xwintop.xcore.util.FileUtil;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.hardware.CentralProcessor.TickType;
import oshi.software.os.*;
import oshi.util.FormatUtil;
import oshi.util.Util;

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
@Slf4j
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
            log.error("cpu使用率获取失败：", e);
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
            log.error("显示内存失败", e);
        }
    }

    public void showOverviewDiskLineChart() {
        try {
            HWDiskStore[] diskStores = hal.getDiskStores();
            XYChart.Series[] series = new XYChart.Series[diskStores.length * 2];
            for (int i = 0; i < diskStores.length; i++) {
                series[i * 2 + 0] = new XYChart.Series();
                series[i * 2 + 0].setName("磁盘：" + i + "reads");
                series[i * 2 + 1] = new XYChart.Series();
                series[i * 2 + 1].setName("磁盘：" + i + "writes");
            }
            showSystemInfoController.getOverviewDiskLineChart().getData().addAll(series);
            showSystemInfoController.getOverviewDiskLineChart().getXAxis().setTickLabelsVisible(false);
            Timer timer = new Timer();
            timerList.add(timer);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        String xValue = "" + System.currentTimeMillis();
                        for (int i = 0; i < diskStores.length; i++) {
                            final int ii = i;
                            Platform.runLater(() -> {
                                if (series[ii * 2 + 0].getData().size() > 60) {
                                    series[ii * 2 + 0].getData().remove(0);
                                }
                                if (series[ii * 2 + 1].getData().size() > 60) {
                                    series[ii * 2 + 1].getData().remove(0);
                                }
                                if (diskStores[ii].getTransferTime() > 0) {
                                    series[ii * 2 + 0].getData().add(new XYChart.Data(xValue, diskStores[ii].getReadBytes() * 1000 / diskStores[ii].getTransferTime()));
                                    series[ii * 2 + 1].getData().add(new XYChart.Data(xValue, diskStores[ii].getWriteBytes() * 1000 / diskStores[ii].getTransferTime()));
                                }
                            });
                        }
                    } catch (Exception e) {
                        log.error("磁盘使用率获取失败：", e);
                    }
                }
            }, 0, 1000);
        } catch (Exception e) {
            log.error("磁盘使用率获取失败：", e);
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
            log.error("", e);
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
                map.put("showValue", FileUtil.formetFileSize(fs.getTotalSpace()));

                Map map1 = new HashMap();
                map1.put("id", "" + i + '1');
                map1.put("name", "已用");
                map1.put("parent", "" + i);
                map1.put("value", fs.getTotalSpace() - fs.getUsableSpace());
                map1.put("showValue", FileUtil.formetFileSize((fs.getTotalSpace() - fs.getUsableSpace())));

                Map map2 = new HashMap();
                map2.put("id", "" + i + '2');
                map2.put("name", "剩余");
                map2.put("parent", "" + i);
                map2.put("value", fs.getUsableSpace());
                map2.put("showValue", FileUtil.formetFileSize(fs.getUsableSpace()));
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
     * 显示系统信息
     */
    public void showSystemInfo() {
        showSystemInfoController.getSystemInfoTextArea().setText(this.getSystemInfoString());
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
            log.error("获取jvm信息失败", e);
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


    private String getSystemInfoString() {
        OperatingSystem os = si.getOperatingSystem();

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(os.toString());
        //LOG.info("Checking computer system...");
        stringBuffer.append(printComputerSystem(hal.getComputerSystem()));

        //LOG.info("Checking Processor...");
        stringBuffer.append(printProcessor(hal.getProcessor()));

        //LOG.info("Checking Memory...");
        stringBuffer.append(printMemory(hal.getMemory()));

        //LOG.info("Checking CPU...");
        stringBuffer.append(printCpu(hal.getProcessor()));

        //LOG.info("Checking Processes...");
        stringBuffer.append(printProcesses(os, hal.getMemory()));

        //LOG.info("Checking Sensors...");
        stringBuffer.append(printSensors(hal.getSensors()));

        //LOG.info("Checking Power sources...");
        stringBuffer.append(printPowerSources(hal.getPowerSources()));

        //LOG.info("Checking Disks...");
        stringBuffer.append(printDisks(hal.getDiskStores()));

        //LOG.info("Checking File System...");
        stringBuffer.append(printFileSystem(os.getFileSystem()));

        //LOG.info("Checking Network interfaces...");
        stringBuffer.append(printNetworkInterfaces(hal.getNetworkIFs()));

        //LOG.info("Checking Network parameterss...");
        stringBuffer.append(printNetworkParameters(os.getNetworkParams()));

        // hardware: displays
        //LOG.info("Checking Displays...");
        stringBuffer.append(printDisplays(hal.getDisplays()));

        // hardware: USB devices
        //LOG.info("Checking USB Devices...");
        stringBuffer.append(printUsbDevices(hal.getUsbDevices(true)));

        //LOG.info("Checking Sound Cards...");
        stringBuffer.append(printSoundCards(hal.getSoundCards()));
        return stringBuffer.toString();
    }

    private static String printComputerSystem(final ComputerSystem computerSystem) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nmanufacturer: " + computerSystem.getManufacturer());
        stringBuffer.append("\nmodel: " + computerSystem.getModel());
        stringBuffer.append("\nserialnumber: " + computerSystem.getSerialNumber());
        final Firmware firmware = computerSystem.getFirmware();
        stringBuffer.append("\nfirmware:");
        stringBuffer.append("\n  manufacturer: " + firmware.getManufacturer());
        stringBuffer.append("\n  name: " + firmware.getName());
        stringBuffer.append("\n  description: " + firmware.getDescription());
        stringBuffer.append("\n  version: " + firmware.getVersion());
        stringBuffer.append("\n  release date: " + (firmware.getReleaseDate() == null ? "unknown" : firmware.getReleaseDate() == null ? "unknown" : firmware.getReleaseDate()));
        final Baseboard baseboard = computerSystem.getBaseboard();
        stringBuffer.append("\nbaseboard:");
        stringBuffer.append("\n  manufacturer: " + baseboard.getManufacturer());
        stringBuffer.append("\n  model: " + baseboard.getModel());
        stringBuffer.append("\n  version: " + baseboard.getVersion());
        stringBuffer.append("\n  serialnumber: " + baseboard.getSerialNumber());
        return stringBuffer.toString();
    }

    private static String printProcessor(CentralProcessor processor) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\n " + processor.getPhysicalPackageCount() + " physical CPU package(s)");
        stringBuffer.append("\n " + processor.getPhysicalProcessorCount() + " physical CPU core(s)");
        stringBuffer.append("\n " + processor.getLogicalProcessorCount() + " logical CPU(s)");

        stringBuffer.append("\nIdentifier: " + processor.getIdentifier());
        stringBuffer.append("\nProcessorID: " + processor.getProcessorID());
        return stringBuffer.toString();
    }

    private static String printMemory(GlobalMemory memory) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nMemory: " + FormatUtil.formatBytes(memory.getAvailable()) + "/" + FormatUtil.formatBytes(memory.getTotal()));
        stringBuffer.append("\nSwap used: " + FormatUtil.formatBytes(memory.getSwapUsed()) + "/" + FormatUtil.formatBytes(memory.getSwapTotal()));
        return stringBuffer.toString();
    }

    private static String printCpu(CentralProcessor processor) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nUptime: " + FormatUtil.formatElapsedSecs(processor.getSystemUptime()));
        stringBuffer.append("\nContext Switches/Interrupts: " + processor.getContextSwitches() + " / " + processor.getInterrupts());
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        stringBuffer.append("\nCPU, IOWait, and IRQ ticks @ 0 sec:" + Arrays.toString(prevTicks));
        // Wait a second...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        stringBuffer.append("\nCPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks));
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;

        stringBuffer.append(String.format("\nUser: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%% Steal: %.1f%%%n",
                100d * user / totalCpu, 100d * nice / totalCpu, 100d * sys / totalCpu, 100d * idle / totalCpu,
                100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu, 100d * steal / totalCpu));
        stringBuffer.append(String.format("CPU load: %.1f%% (counting ticks)%n", processor.getSystemCpuLoadBetweenTicks() * 100));
        stringBuffer.append(String.format("CPU load: %.1f%% (OS MXBean)%n", processor.getSystemCpuLoad() * 100));
        double[] loadAverage = processor.getSystemLoadAverage(3);
        stringBuffer.append("\nCPU load averages:" + (loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0]))
                + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2])));
        // per core CPU
        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
        double[] load = processor.getProcessorCpuLoadBetweenTicks();
        for (double avg : load) {
            procCpu.append(String.format(" %.1f%%", avg * 100));
        }
        stringBuffer.append("\n" + procCpu.toString());
        return stringBuffer.toString();
    }

    private static String printProcesses(OperatingSystem os, GlobalMemory memory) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nProcesses: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
        // Sort by highest CPU
        List<OSProcess> procs = Arrays.asList(os.getProcesses(5, OperatingSystem.ProcessSort.CPU));

        stringBuffer.append("\n   PID  %CPU %MEM       VSZ       RSS Name");
        for (int i = 0; i < procs.size() && i < 5; i++) {
            OSProcess p = procs.get(i);
            stringBuffer.append(String.format(" %5d %5.1f %4.1f %9s %9s %s%n", p.getProcessID(),
                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
                    100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName()));
        }
        return stringBuffer.toString();
    }

    private static String printSensors(Sensors sensors) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nSensors:");
        stringBuffer.append(String.format("\n CPU Temperature: %.1f°C%n", sensors.getCpuTemperature()));
//        stringBuffer.append("\n Fan Speeds: " + Arrays.toString(sensors.getFanSpeeds()));
        stringBuffer.append(String.format("\n CPU Voltage: %.1fV%n", sensors.getCpuVoltage()));
        return stringBuffer.toString();
    }

    private static String printPowerSources(PowerSource[] powerSources) {
        StringBuilder sb = new StringBuilder("Power: ");
        if (powerSources.length == 0) {
            sb.append("Unknown");
        } else {
            double timeRemaining = powerSources[0].getTimeRemaining();
            if (timeRemaining < -1d) {
                sb.append("Charging");
            } else if (timeRemaining < 0d) {
                sb.append("Calculating time remaining");
            } else {
                sb.append(String.format("%d:%02d remaining", (int) (timeRemaining / 3600),
                        (int) (timeRemaining / 60) % 60));
            }
        }
        for (PowerSource pSource : powerSources) {
            sb.append(String.format("%n %s @ %.1f%%", pSource.getName(), pSource.getRemainingCapacity() * 100d));
        }
        return sb.toString();
    }

    private static String printDisks(HWDiskStore[] diskStores) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nDisks:");
        for (HWDiskStore disk : diskStores) {
            boolean readwrite = disk.getReads() > 0 || disk.getWrites() > 0;
            stringBuffer.append(String.format(" %s: (model: %s - S/N: %s) size: %s, reads: %s (%s), writes: %s (%s), xfer: %s ms%n",
                    disk.getName(), disk.getModel(), disk.getSerial(),
                    disk.getSize() > 0 ? FormatUtil.formatBytesDecimal(disk.getSize()) : "?",
                    readwrite ? disk.getReads() : "?", readwrite ? FormatUtil.formatBytes(disk.getReadBytes()) : "?",
                    readwrite ? disk.getWrites() : "?", readwrite ? FormatUtil.formatBytes(disk.getWriteBytes()) : "?",
                    readwrite ? disk.getTransferTime() : "?"));
            HWPartition[] partitions = disk.getPartitions();
            if (partitions == null) {
                continue;
            }
            for (HWPartition part : partitions) {
                stringBuffer.append(String.format(" |-- %s: %s (%s) Maj:Min=%d:%d, size: %s%s%n", part.getIdentification(),
                        part.getName(), part.getType(), part.getMajor(), part.getMinor(),
                        FormatUtil.formatBytesDecimal(part.getSize()),
                        part.getMountPoint().isEmpty() ? "" : " @ " + part.getMountPoint()));
            }
        }
        return stringBuffer.toString();
    }

    private static String printFileSystem(FileSystem fileSystem) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nFile System:");

        stringBuffer.append(String.format(" File Descriptors: %d/%d%n", fileSystem.getOpenFileDescriptors(),
                fileSystem.getMaxFileDescriptors()));

        OSFileStore[] fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            stringBuffer.append(String.format(
                    " %s (%s) [%s] %s of %s free (%.1f%%) is %s "
                            + (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s")
                            + " and is mounted at %s%n",
                    fs.getName(), fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
                    fs.getVolume(), fs.getLogicalVolume(), fs.getMount()));
        }
        return stringBuffer.toString();
    }

    private static String printNetworkInterfaces(NetworkIF[] networkIFs) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nNetwork interfaces:");
        for (NetworkIF net : networkIFs) {
            stringBuffer.append(String.format(" Name: %s (%s)%n", net.getName(), net.getDisplayName()));
            stringBuffer.append(String.format("   MAC Address: %s %n", net.getMacaddr()));
            stringBuffer.append(String.format("   MTU: %s, Speed: %s %n", net.getMTU(), FormatUtil.formatValue(net.getSpeed(), "bps")));
            stringBuffer.append(String.format("   IPv4: %s %n", Arrays.toString(net.getIPv4addr())));
            stringBuffer.append(String.format("   IPv6: %s %n", Arrays.toString(net.getIPv6addr())));
            boolean hasData = net.getBytesRecv() > 0 || net.getBytesSent() > 0 || net.getPacketsRecv() > 0
                    || net.getPacketsSent() > 0;
            stringBuffer.append(String.format("   Traffic: received %s/%s%s; transmitted %s/%s%s %n",
                    hasData ? net.getPacketsRecv() + " packets" : "?",
                    hasData ? FormatUtil.formatBytes(net.getBytesRecv()) : "?",
                    hasData ? " (" + net.getInErrors() + " err)" : "",
                    hasData ? net.getPacketsSent() + " packets" : "?",
                    hasData ? FormatUtil.formatBytes(net.getBytesSent()) : "?",
                    hasData ? " (" + net.getOutErrors() + " err)" : ""));
        }
        return stringBuffer.toString();
    }

    private static String printNetworkParameters(NetworkParams networkParams) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nNetwork parameters:");
        stringBuffer.append(String.format(" Host name: %s%n", networkParams.getHostName()));
        stringBuffer.append(String.format(" Domain name: %s%n", networkParams.getDomainName()));
        stringBuffer.append(String.format(" DNS servers: %s%n", Arrays.toString(networkParams.getDnsServers())));
        stringBuffer.append(String.format(" IPv4 Gateway: %s%n", networkParams.getIpv4DefaultGateway()));
        stringBuffer.append(String.format(" IPv6 Gateway: %s%n", networkParams.getIpv6DefaultGateway()));
        return stringBuffer.toString();
    }

    private static String printDisplays(Display[] displays) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nDisplays:");
        int i = 0;
        for (Display display : displays) {
            stringBuffer.append("\n Display " + i + ":");
            stringBuffer.append("\n" + display.toString());
            i++;
        }
        return stringBuffer.toString();
    }

    private static String printUsbDevices(UsbDevice[] usbDevices) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nUSB Devices:");
        for (UsbDevice usbDevice : usbDevices) {
            stringBuffer.append("\n" + usbDevice.toString());
        }
        return stringBuffer.toString();
    }

    private static String printSoundCards(SoundCard[] cards) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nSound Cards:");
        for (SoundCard card : cards) {
            stringBuffer.append("\n" + card.toString());
        }
        return stringBuffer.toString();
    }
}