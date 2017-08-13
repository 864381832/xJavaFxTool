package com.xwintop.xJavaFxTool.utils;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import javax.swing.JFrame;

import com.xwintop.xJavaFxTool.controller.littleTools.QRCodeBuilderController;

import javafx.embed.swing.SwingFXUtils;

public class SnapshotRectUtil extends JFrame {
	private static final long serialVersionUID = 1L;
	private int orgx, orgy, endx, endy;
	private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	private BufferedImage image;
	private BufferedImage tempImage;
	private BufferedImage saveImage;
	private Graphics g;

	@Override
	public void paint(Graphics g) {
		RescaleOp ro = new RescaleOp(0.8f, 0, null);
		tempImage = ro.filter(image, null);
		g.drawImage(tempImage, 0, 0, this);
	}

	public SnapshotRectUtil(QRCodeBuilderController codeBuilderController) {
		snapshot();
		setUndecorated(true);
		setVisible(true);
		setSize(d);// 最大化窗口
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				orgx = e.getX();
				orgy = e.getY();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				codeBuilderController.snapshotActionCallBack(SwingFXUtils.toFXImage(saveImage, null));
				setVisible(false);
				SnapshotRectUtil.this.dispose();
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				endx = e.getX();
				endy = e.getY();
				g = getGraphics();
				g.drawImage(tempImage, 0, 0, SnapshotRectUtil.this);
				int x = Math.min(orgx, endx);
				int y = Math.min(orgy, endy);
				int width = Math.abs(endx - orgx) + 1;
				int height = Math.abs(endy - orgy) + 1;
				// 加上1，防止width或height为0
				g.setColor(Color.BLUE);
				g.drawRect(x - 1, y - 1, width + 1, height + 1);
				// 减1，加1都是为了防止图片将矩形框覆盖掉
				saveImage = image.getSubimage(x, y, width, height);
				g.drawImage(saveImage, x, y, SnapshotRectUtil.this);
			}
		});
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {// 按Esc键退出
//					codeBuilderController.snapshotActionCallBack(SwingFXUtils.toFXImage(saveImage, null));
//					setVisible(false);
				}
			}
		});
	}
	
	public void snapshot() {
		try {
			Robot robot = new Robot();
			// Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			image = robot.createScreenCapture(new Rectangle(0, 0, d.width, d.height));
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}