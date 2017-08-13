package com.xwintop.xJavaFxTool.swing;

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
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.filechooser.FileSystemView;

/**
 * java截屏 运行后将当前屏幕截取，并最大化显示。 拖拽鼠标，选择自己需要的部分。 按Esc键保存图片到桌面，并退出程序。
 * 点击右上角（没有可见的按钮），退出程序，不保存图片。
 * 
 * @author JinCeon
 */
public class SnapshotTest {
	public static void main(String[] args) {
		// 全屏运行
		RectD rd = new RectD();
		// GraphicsDevice gd =
		// GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		// gd.setFullScreenWindow(rd);
	}
}

class RectD extends JFrame {
	private static final long serialVersionUID = 1L;
	int orgx, orgy, endx, endy;
	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	BufferedImage image;
	BufferedImage tempImage;
	BufferedImage saveImage;
	Graphics g;

	@Override
	public void paint(Graphics g) {
		RescaleOp ro = new RescaleOp(0.8f, 0, null);
		tempImage = ro.filter(image, null);
		g.drawImage(tempImage, 0, 0, this);
	}

	public RectD() {
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
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				endx = e.getX();
				endy = e.getY();
				g = getGraphics();
				g.drawImage(tempImage, 0, 0, RectD.this);
				int x = Math.min(orgx, endx);
				int y = Math.min(orgy, endy);
				int width = Math.abs(endx - orgx) + 1;
				int height = Math.abs(endy - orgy) + 1;
				// 加上1，防止width或height为0
				g.setColor(Color.BLUE);
				g.drawRect(x - 1, y - 1, width + 1, height + 1);
				// 减1，加1都是为了防止图片将矩形框覆盖掉
				saveImage = image.getSubimage(x, y, width, height);
				g.drawImage(saveImage, x, y, RectD.this);
			}
		});
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// 按Esc键退出
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					// saveToFile();
					// System.exit(0);
					setVisible(false);
				}
			}
		});
	}

	public void saveToFile() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
		String name = sdf.format(new Date());
		File path = FileSystemView.getFileSystemView().getHomeDirectory();
		String format = "jpg";
		File f = new File(path + File.separator + name + "." + format);
		try {
			ImageIO.write(saveImage, format, f);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
