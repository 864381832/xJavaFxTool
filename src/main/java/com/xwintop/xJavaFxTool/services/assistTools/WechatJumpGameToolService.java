package com.xwintop.xJavaFxTool.services.assistTools;

import com.xwintop.xJavaFxTool.controller.assistTools.WechatJumpGameToolController;
import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.application.Platform;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * @ClassName: WechatJumpGameToolService
 * @Description: 微信跳一跳助手
 * @author: xufeng
 * @date: 2018/2/6 10:32
 */

@Getter
@Setter
@Slf4j
public class WechatJumpGameToolService {
    private WechatJumpGameToolController wechatJumpGameToolController;
    private String adb = "";
    private float rate = 1.37f;
    private boolean running = false;
    private boolean restart = true;
    private int step = 0;
    private final String start = "跳一跳", stop = "停止运行";

    private final String path = ConfigureUtil.getConfigurePath("wechatJumpGame/");

    public void init() {
        String ANDROID_HOME = System.getenv("ANDROID_HOME");
        if (ANDROID_HOME == null) {
            ANDROID_HOME = Object.class.getResource("/data").getPath().toString();
        }
        adb = ANDROID_HOME + "/platform-tools/adb";
    }

    public void adbConnectAction() {
        try {
            Process processADB = Runtime.getRuntime().exec(adb + " shell");
            InputStream inputStream = processADB.getInputStream();
            new Thread(new Runnable() {
                byte[] cache = new byte[1024];

                public void run() {
                    try {
                        int i;
                        while ((i = inputStream.read(cache)) != -1) {
                            String s = new String(cache, 0, i);
                            if (s.endsWith(":/ $ ")) {
                                Platform.runLater(() -> {
                                    wechatJumpGameToolController.getAdbConnectHintLabel().setText("已连接");
                                    wechatJumpGameToolController.getAdbRunButton().setDisable(false);
                                });
                            }

                        }
                    } catch (IOException ignored) {
                        stop("连接失败");
                        log.error(ignored.getMessage());
                    }
                }
            }).start();
        } catch (Exception e) {
            TooltipUtil.showToast("Error ADB!!!");
            log.error("连接ADB失败", e);
        }
    }

    public void adbRunAction() {
        if (!running) {
            new Thread(() -> {
                try {
                    rate = (float) wechatJumpGameToolController.getWaitTimeSlider().getValue();
                    run();
                } catch (Exception e) {
                    log.error("分析出错", e);
                    String msg = "分析图片错误，请确认已打开跳一跳";
//                    if (step == 0) msg = "时间系数请设置为1-3之间的数字";
                    if (step == 1) {
                        msg = "adb错误，请结束adb.exe进程后重试";
                    }
                    stop(msg);
                }
            }).start();
        } else {
            stop("=====已停止运行=====");
        }
    }

    private void stop(String msg) {
        running = false;
        Platform.runLater(() -> {
            wechatJumpGameToolController.getAdbRunButton().setText(start);
            wechatJumpGameToolController.getErrorInfoLabel().setText(msg);
        });
    }

    private void run() throws Exception {
        running = true;
        Platform.runLater(() -> {
            wechatJumpGameToolController.getAdbRunButton().setText(stop);
            wechatJumpGameToolController.getErrorInfoLabel().setText("=====正在运行中=====");
        });
        while (running) {
            step = 1;//截屏
            File pic = getScreenPic();
            step = 2;//分析图片
            String res = scanPic(pic);
            step = 3;//跳一跳
            exec(adb + " shell input swipe " + res);
            step = 4;//暂停2-3秒左右
            Thread.sleep(2000 + new Random().nextInt(500));
        }
    }

    private String scanPic(File pic) throws Exception {
        BufferedImage bi = ImageIO.read(pic);
        //获取图像的宽度和高度
        int width = bi.getWidth();
        int height = bi.getHeight();
        int x1 = 0, y1 = 0, x2 = 0, y2 = 0, r = width / 1080;
        //扫描获取黑棋位置
        for (int i = 50; i < width; i++) {
            for (int flag = 0, j = height * 3 / 4; j > height / 3; j -= 5) {
                if (!colorDiff(bi.getRGB(i, j), 55 << 16 | 58 << 8 | 100)) flag++;
                if (flag > 3) {
                    x1 = i + 13 * r;
                    y1 = j + 2 * r;
                    break;
                }
            }
            if (x1 > 0) break;
        }
        Graphics2D g2d = bi.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(3f));
        //扫描目标点
        for (int i = height / 3; i < y1; i++) {
            int p1 = bi.getRGB(99, i);
            for (int j = 100; j < width; j++) {
                if (colorDiff(bi.getRGB(j, i), p1)) {
                    if (Math.abs(j - x1) < 50 * r) {//黑棋比图高
                        j = j + 50 * r;
                    } else {
                        x2 = j;
                        y2 = i;
                        break;
                    }
                }
            }
            if (x2 > 0) {//找到了目标块顶点
                int p2 = bi.getRGB(x2, y2 - 10), j, max = -1;
                for (; i < y1 - 50 * r; i += 5) {
                    for (j = x2; colorDiff(bi.getRGB(j, i), p2) && j < x2 + 200 * r; ) j++;
                    if (max < 0 && j - x2 > 0) x2 = x2 + (j - x2) / 2;//修正顶点横坐标
                    if (max < j - x2) max = j - x2;//找到目标块最长宽度
                    else break;
                }
                g2d.drawLine(x2, y2, x2, i);
                y2 = i - 5;
                g2d.drawLine(x2 - max, y2, x2 + max, y2);
                break;
            }
        }
        g2d.drawLine(x1, y1, x2, y2);
        ImageIO.write(bi, "png", new FileOutputStream(pic));//保存成图片
        double distance = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        if (x1 < 50 || y1 < 50 || x2 < 50 || y2 < 50 || distance < 100) {
            if (!restart) throw new Exception("scan error:" + x1 + "|" + y1 + "|" + x2 + "|" + y2);
            int x = width / 2, y = height * 3 / 4, z = 9 * height / 10, i = y;//获取开始按钮位置，自动重新开始
            while ((i += 20) < z) if (bi.getRGB(x, i) == -1 && bi.getRGB(x + 20, i + 20) == -1) break;
            if (i == y - 20 || i == z) throw new Exception("scan error:game not start");
            return x + " " + i + " " + x + " " + i + " 100";
        }
        if (distance < 150) distance = 150;
        return x1 + " " + y1 + " " + x2 + " " + y2 + " " + (int) (distance * rate);
    }

    private boolean colorDiff(int c1, int c2) {
        int c11 = c1 >> 16 & 0xFF, c12 = c1 >> 8 & 0xFF, c13 = c1 & 0xFF;
        int c21 = c2 >> 16 & 0xFF, c22 = c2 >> 8 & 0xFF, c23 = c2 & 0xFF;
        return Math.abs(c11 - c21) > 5 || Math.abs(c12 - c22) > 5 || Math.abs(c13 - c23) > 5;
    }

    private File getScreenPic() throws Exception {
        File pic = new File(path + "pic.png");
        if (pic.exists()) {//备份一下之前的一张图片
            File back = new File(path + System.currentTimeMillis() + ".png");
            if (!back.exists() || back.delete()) pic.renameTo(back);
        }
        exec(adb + " shell screencap -p /sdcard/screen.png");
        exec(adb + " pull /sdcard/screen.png " + pic.getAbsolutePath());
        if (!pic.exists()) throw new Exception("error getScreenPic");
        return pic;
    }

    private void exec(String cmd) throws Exception {
        Process ps = null;
        try {
            System.out.println(cmd);
            ps = Runtime.getRuntime().exec(cmd.split(" "));
            int code = ps.waitFor();
            if (code != 0) throw new Exception("exec error(code=" + code + "): " + cmd);
        } finally {
            if (ps != null) ps.destroy();
        }
    }

    public WechatJumpGameToolService(WechatJumpGameToolController wechatJumpGameToolController) {
        this.wechatJumpGameToolController = wechatJumpGameToolController;
    }
}