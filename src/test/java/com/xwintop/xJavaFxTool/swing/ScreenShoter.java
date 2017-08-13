package com.xwintop.xJavaFxTool.swing;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * ScreenShoter.
 * 
 * @author <dongyado@gmail.com>
 * @version 1.0
 */
public class ScreenShoter extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2824099274822727350L;

	private static JFrame frame;
	private Robot robot;
	private BufferedImage screenimg;
	private JFrame shoter;
	private JPanel pane;

	public ScreenShoter() {

		setTitle(SShoterConfig.FRAME_TITLE);
		setSize(SShoterConfig.FRAME_SIZE);

		setLocation((SShoterConfig.SCREEN_SIZE.width - SShoterConfig.FRAME_SIZE.width) / 2,
				(SShoterConfig.SCREEN_SIZE.height - SShoterConfig.FRAME_SIZE.height) / 2);

		// setLocation( 0,0 );

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// this.setUndecorated(true);

		Container contentPane = this.getContentPane();

		contentPane.setBackground(new Color(0xdd, 0xdd, 0xdd));
		// Container cPane = this.getContentPane();
		GridBagLayout gbl = new GridBagLayout();

		setLayout(gbl);

		GridBagConstraints c = new GridBagConstraints();
		// c.fill = GridBagConstraints.BOTH;

		JLabel jlb1 = new JLabel(" Start: CTRL + E | Save : CTRL + S");
		JLabel jlb2 = new JLabel("Cancel: CTRL + D | Exit : CTRL + Q");
		jlb1.setSize(SShoterConfig.FRAME_LABEL_SIZE);
		build(c, 0, 0, 2, 1, 0, 20);
		Color fontColor = new Color(200, 100, 0);
		jlb1.setForeground(fontColor);
		contentPane.add(jlb1, c);
		// jlb2.setSize( SShoterConfig.FRAME_LABEL_SIZE);
		build(c, 0, 1, 2, 1, 0, 20);
		jlb2.setForeground(fontColor);
		contentPane.add(jlb2, c);

		// Color bfontColor = new Color( 0x66, 0x66, 0x66 );
		JButton buttonStart = new JButton("start");
		JButton buttonEnd = new JButton("Exit");
		// this.setAutoRequestFocus(true);
		buttonStart.setSize(SShoterConfig.FRAME_BUTTON_SIZE);
		buttonStart.setForeground(fontColor);
		buttonStart.setBackground(new Color(0xf5, 0xf5, 0xf5));
		buttonStart.setFocusPainted(false);
		build(c, 0, 2, 1, 1, 50, 0);
		contentPane.add(buttonStart, c);

		buttonEnd.setForeground(fontColor);
		buttonEnd.setSize(SShoterConfig.FRAME_BUTTON_SIZE);
		buttonEnd.setBackground(new Color(0xf5, 0xf5, 0xf5));
		buttonEnd.setFocusPainted(false);
		build(c, 1, 2, 1, 1, 50, 0);
		contentPane.add(buttonEnd, c);
		buttonEnd.addActionListener(new ButtonEndActionListener());
		buttonEnd.registerKeyboardAction(new ButtonEndActionListener(),
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

		buttonStart.addActionListener(new ButtonStartActionListener());
		buttonStart.registerKeyboardAction(new ButtonStartActionListener(),
				KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

		shoter = new JFrame();
		shoter.setSize(SShoterConfig.SCREEN_SIZE);
		shoter.setAlwaysOnTop(true);
		// shoter.setAutoRequestFocus(true);
		shoter.setUndecorated(true);

		shoter.setLocation(0, 0);
		shoter.setLocationRelativeTo(null);
		shoter.setVisible(false);

	}

	public class ButtonStartActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				ScreenShoter.this.setVisible(false);
				Thread.sleep(100);
				robot = new Robot();
				screenimg = robot.createScreenCapture(new Rectangle(0, 0, (int) SShoterConfig.SCREEN_SIZE.getWidth(),
						(int) SShoterConfig.SCREEN_SIZE.getHeight()));
				pane = new Shoter(ScreenShoter.this, screenimg);
				shoter.setContentPane(pane);
				shoter.setVisible(true);

			} catch (AWTException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public class ButtonEndActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.exit(0);
		}

	}

	public static JFrame getFrame() {
		return frame;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ScreenShoter().setVisible(true);
	}

	public JFrame getWindow() {
		return this.shoter;
	}

	/**
	 * build constraints for GridBagLayout
	 * 
	 */
	public static void build(GridBagConstraints c, int gridx, int gridy, int gridwidth, int gridheight, int weightx,
			int weighty) {
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = gridwidth;
		c.gridheight = gridheight;
		c.weightx = weightx;
		c.weighty = weighty;
	}

}
