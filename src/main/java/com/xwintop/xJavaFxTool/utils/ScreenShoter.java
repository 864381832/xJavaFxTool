package com.xwintop.xJavaFxTool.utils;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.xwintop.xJavaFxTool.controller.littleTools.QRCodeBuilderController;

import javafx.embed.swing.SwingFXUtils;

public class ScreenShoter extends JFrame {
	private static final long serialVersionUID = 2824099274822727350L;

	public ScreenShoter(QRCodeBuilderController codeBuilderController) {
		setSize(SShoterConfig.SCREEN_SIZE);
		setUndecorated(true);
		setLocation(0, 0);
		setLocationRelativeTo(null);
		JPanel pane = new Shoter(codeBuilderController, this);
		setContentPane(pane);
		setVisible(true);
	}
}

class Shoter extends JPanel {
	private static final long serialVersionUID = 6749535851395089903L;
	private QRCodeBuilderController codeBuilderController;
	private JFrame shoter;
	BufferedImage screenImg = null;
	private Point _start = new Point(0, 0);
	private Point _end = new Point(1, 1);
	private JPanel buttons = null;
	private JButton buttonSave = null;
	private JButton buttonCancel = null;
	private JButton buttonExit = null;
	private int operationStatus = SShoterConfig.MOUSE_DRAG;
	private Point _moveStart = new Point();
	private Dimension rectSize;
	private Point buttonsPosition;

	public Shoter(QRCodeBuilderController codeBuilderController, JFrame shoter) {
		snapshot();
		this.shoter = shoter;
		this.codeBuilderController = codeBuilderController;
		setLayout(null);
		setBackground(new Color(200, 200, 200));
		this.addMouseListener(new RectListener());
		this.addMouseMotionListener(new RectDragListener());
		add(getButtons());
		buttons.setSize(SShoterConfig.BUTTONS_PANEL_SIZE);
		buttons.setVisible(false);
		setVisible(true);
	}

	public void snapshot() {
		try {
			Robot robot = new Robot();
			screenImg = robot.createScreenCapture(
					new Rectangle(0, 0, SShoterConfig.SCREEN_SIZE.width, SShoterConfig.SCREEN_SIZE.height));
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public JPanel getButtons() {
		buttons = new JPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttons.setBackground(new Color(200, 200, 200));

		buttonSave = new JButton("Save");
		// buttonSave
		buttonSave.setSize(SShoterConfig.BUTTONS_SIZE);
		ActionListener buttonSaveLis = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// ImageIO.write(getNewImage(), fileExtension, file);
					codeBuilderController.snapshotActionCallBack(SwingFXUtils.toFXImage(getNewImage(), null));
					buttons.setVisible(false);
					_start = new Point(0, 0);
					_end = new Point(0, 0);
					shoter.setVisible(false);
					shoter.dispose();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		};
		buttons.add(buttonSave);
		buttonSave.addActionListener(buttonSaveLis);

		buttonSave.registerKeyboardAction(buttonSaveLis, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK),
				JComponent.WHEN_IN_FOCUSED_WINDOW);

		buttonCancel = new JButton("Cancel");
		buttonCancel.setSize(SShoterConfig.BUTTONS_SIZE);
		ActionListener cancelLis = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_start = new Point(0, 0);
				_end = new Point(0, 0);
				buttons.setVisible(false);
				Shoter.this.setCursor(SShoterConfig.CURSOR_NORMAL);
				operationStatus = SShoterConfig.MOUSE_DRAG;
				Shoter.this.repaint();
			}
		};
		buttonCancel.addActionListener(cancelLis);
		buttonCancel.registerKeyboardAction(cancelLis, KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK),
				JComponent.WHEN_IN_FOCUSED_WINDOW);

		buttons.add(buttonCancel);
		// exit
		buttonExit = new JButton("Exit");
		buttonExit.setSize(SShoterConfig.BUTTONS_SIZE);
		buttons.add(buttonExit);
		ActionListener buttonExitLis = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				codeBuilderController.snapshotActionCallBack(null);
				shoter.setVisible(false);
				shoter.dispose();
			}
		};
		buttonExit.addActionListener(buttonExitLis);
		buttonExit.registerKeyboardAction(buttonExitLis, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		return buttons;

	}

	@Override
	public void paintComponent(Graphics comp) {
		Graphics2D comp2D = (Graphics2D) comp;
		comp2D.drawImage(screenImg, 0, 0, this);
		comp2D.setColor(new Color(0, 0, 0, 100));
		comp2D.fill(new Rectangle(0, 0, SShoterConfig.SCREEN_SIZE.width, SShoterConfig.SCREEN_SIZE.height));

		/*
		 * comp2D.setColor(new Color( 0, 0, 0)); BasicStroke stroke = new
		 * BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
		 * comp2D.setStroke(stroke);
		 * 
		 * Rectangle _rect = new Rectangle(_start, new Dimension(_end.x - _start.x,
		 * _end.y - _start.y)); comp2D.draw(_rect); Color ColorFill = new Color(255,
		 * 255, 255, 0); comp2D.setColor(ColorFill); comp2D.fill(_rect);
		 * comp2D.drawImage(getNewImage(), _start.x, _start.y, this);
		 */

		if (_end.x - _start.x > 0 && _end.y - _start.y > 0) {
            comp2D.drawImage(getNewImage(), _start.x, _start.y, this);
        }
	}

	private class RectListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if (operationStatus == SShoterConfig.MOUSE_MOVE) {
				_moveStart = new Point((int) (e.getX() - _start.getX()), (int) (e.getY() - _start.getY()));
				buttons.setVisible(true);
			} else if (operationStatus == SShoterConfig.MOUSE_DRAG) {
				_start = e.getPoint();
				_end = e.getPoint();
				// hidden the buttons
				buttons.setVisible(false);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (operationStatus == SShoterConfig.MOUSE_DRAG) {
				_end = e.getPoint();
				rectSize = new Dimension((int) (_end.getX() - _start.getX()), (int) (_end.getY() - _start.getY()));
				setButtonsPosition();
				buttons.setVisible(true);
				operationStatus = SShoterConfig.MOUSE_NORMAL;
				Shoter.this.repaint();
			}

		}
	}

	/**
	 * Mouse Move Listener
	 */
	private class RectDragListener implements MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {
			if (operationStatus == SShoterConfig.MOUSE_DRAG) {
				_end = e.getPoint();
			} else if (operationStatus == SShoterConfig.MOUSE_MOVE) {
				// _moveTemp = _moveStart;
				// _moveEnd = e.getPoint();
				if (e.getX() - _moveStart.getX() >= 0
						&& e.getX() - _moveStart.x + rectSize.width <= SShoterConfig.SCREEN_SIZE.width) {
					_start.x = e.getX() - _moveStart.x;
					_end.x = _start.x + rectSize.width;
				} else if (e.getX() - _moveStart.getX() < 0) {
					_start.x = 0;
				} else if (e.getX() - _moveStart.x + rectSize.width > SShoterConfig.SCREEN_SIZE.width) {
					_end.x = SShoterConfig.SCREEN_SIZE.width;
				}
				if (e.getY() - _moveStart.getY() >= 0
						&& e.getY() - _moveStart.y + rectSize.height <= SShoterConfig.SCREEN_SIZE.height) {
					_start.y = e.getY() - _moveStart.y;
					_end.y = _start.y + rectSize.height;
				} else if (e.getY() - _moveStart.getY() < 0) {
					_start.y = 0;
				} else if (e.getY() - _moveStart.y + rectSize.height > SShoterConfig.SCREEN_SIZE.height) {
					_end.y = SShoterConfig.SCREEN_SIZE.height;
				}
				// rectSize.setSize((int) (_end.getX() - _start.getX() ), (int)(_end.getY() -
				// _start.getY() ));
			} else if (operationStatus == SShoterConfig.MOUSE_MOVE_W) {
				_start.x = e.getX();
				rectSize.setSize((int) (_end.getX() - _start.getX()), (int) (_end.getY() - _start.getY()));
			} else if (operationStatus == SShoterConfig.MOUSE_MOVE_N) {

				_start.y = e.getY();
				rectSize.setSize((int) (_end.getX() - _start.getX()), (int) (_end.getY() - _start.getY()));
			} else if (operationStatus == SShoterConfig.MOUSE_MOVE_E) {

				_end.x = e.getX();
				rectSize.setSize((int) (_end.getX() - _start.getX()), (int) (_end.getY() - _start.getY()));
			} else if (operationStatus == SShoterConfig.MOUSE_MOVE_S) {

				_end.y = e.getY();
				rectSize.setSize((int) (_end.getX() - _start.getX()), (int) (_end.getY() - _start.getY()));
			}
			setButtonsPosition();
			Shoter.this.repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (operationStatus == SShoterConfig.MOUSE_DRAG) {
                return;
            }
			// in the sub image
			if (e.getX() > _start.getX() + SShoterConfig.MOVE_EDGE && e.getX() < _end.getX() - SShoterConfig.MOVE_EDGE
					&& e.getY() > _start.getY() + SShoterConfig.MOVE_EDGE
					&& e.getY() < _end.getY() - SShoterConfig.MOVE_EDGE) {

				Shoter.this.setCursor(SShoterConfig.CURSOR_MOVE);

				operationStatus = SShoterConfig.MOUSE_MOVE;

				// out the sub image
			} else if (e.getX() < _start.getX() || e.getX() > _end.getX() || e.getY() < _start.getY()
					|| e.getY() > _end.getY()) {
				Shoter.this.setCursor(SShoterConfig.CURSOR_NORMAL);
				// in the sub image edge area
			} else {
				if (e.getX() >= _start.getX() && e.getX() <= _start.getX() + SShoterConfig.MOVE_EDGE) {
					if (e.getY() >= _start.getY() && e.getY() <= _start.getY() + SShoterConfig.MOVE_EDGE) {

						operationStatus = SShoterConfig.MOUSE_MOVE_WN;
						Shoter.this.setCursor(SShoterConfig.NW_RESIZE_CURSOR);

					} else if (e.getY() >= _start.getY() + SShoterConfig.MOVE_EDGE
							&& e.getY() <= _end.getY() - SShoterConfig.MOVE_EDGE) {

						operationStatus = SShoterConfig.MOUSE_MOVE_W;
						Shoter.this.setCursor(SShoterConfig.W_RESIZE_CURSOR);

					} else if (e.getY() >= _end.getY() - SShoterConfig.MOVE_EDGE && e.getY() <= _end.getY()) {

						operationStatus = SShoterConfig.MOUSE_MOVE_WS;
						Shoter.this.setCursor(SShoterConfig.SW_RESIZE_CURSOR);
					}

				} else if (e.getX() > _start.getX() + SShoterConfig.MOVE_EDGE
						&& e.getX() < _end.getX() - SShoterConfig.MOVE_EDGE) {
					if (e.getY() >= _start.getY() && e.getY() <= _start.getY() + SShoterConfig.MOVE_EDGE) {

						operationStatus = SShoterConfig.MOUSE_MOVE_N;
						Shoter.this.setCursor(SShoterConfig.N_RESIZE_CURSOR);
					} else {

						operationStatus = SShoterConfig.MOUSE_MOVE_S;
						Shoter.this.setCursor(SShoterConfig.S_RESIZE_CURSOR);
					}

				} else {
					if (e.getY() >= _start.getY() && e.getY() <= _start.getY() + SShoterConfig.MOVE_EDGE) {
						operationStatus = SShoterConfig.MOUSE_MOVE_WS;
						Shoter.this.setCursor(SShoterConfig.NE_RESIZE_CURSOR);
					} else if (e.getY() >= _start.getY() + SShoterConfig.MOVE_EDGE
							&& e.getY() <= _end.getY() - SShoterConfig.MOVE_EDGE) {

						operationStatus = SShoterConfig.MOUSE_MOVE_E;
						Shoter.this.setCursor(SShoterConfig.E_RESIZE_CURSOR);

					} else if (e.getY() >= _end.getY() - SShoterConfig.MOVE_EDGE && e.getY() <= _end.getY()) {

						operationStatus = SShoterConfig.MOUSE_MOVE_ES;
						Shoter.this.setCursor(SShoterConfig.SE_RESIZE_CURSOR);
					}

				}
			}

		}
	}

	public void setButtonsPosition() {
		buttonsPosition = new Point();
		if (_end.y + SShoterConfig.BUTTONS_PANEL_SIZE.getHeight() < SShoterConfig.SCREEN_SIZE.getHeight()) {
            buttonsPosition.y = (int) _end.getY();
        } else {
			buttonsPosition.y = (int) (_end.y - SShoterConfig.BUTTONS_PANEL_SIZE.getHeight());
		}
		if (_end.getX() - SShoterConfig.BUTTONS_PANEL_SIZE.getWidth() >= SShoterConfig.BUTTONS_PANEL_SIZE.getWidth()) {
            buttonsPosition.x = (int) (_end.x - SShoterConfig.BUTTONS_PANEL_SIZE.getWidth());
        } else {
            buttonsPosition.x = (int) _start.getX();
        }
		buttons.setLocation(buttonsPosition);
	}

	public BufferedImage getNewImage() {
		return this.screenImg.getSubimage((int) _start.getX(), (int) _start.getY(), (int) (_end.getX() - _start.getX()),
				(int) (_end.getY() - _start.getY()));

	}
}

class SShoterConfig {
	// screen size
	public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

	// frame button size
	public static final Dimension FRAME_BUTTON_SIZE = new Dimension(60, 20);
	public static final Dimension FRAME_LABEL_SIZE = new Dimension(200, 30);

	// buttons
	// buttons panel
	public static final Dimension BUTTONS_PANEL_SIZE = new Dimension(210, 40);

	public static final Dimension BUTTONS_SIZE = new Dimension(60, 20);

	// the status of the operation

	public static final int MOUSE_DRAG = 0;
	public static final int MOUSE_MOVE = 1;
	public static final int MOUSE_NORMAL = 2;

	// move state
	public static final int MOUSE_MOVE_W = 11;

	public static final int MOUSE_MOVE_WN = 12;
	public static final int MOUSE_MOVE_WS = 13;

	public static final int MOUSE_MOVE_E = 14;

	public static final int MOUSE_MOVE_EN = 15;
	public static final int MOUSE_MOVE_ES = 16;

	public static final int MOUSE_MOVE_N = 17;
	public static final int MOUSE_MOVE_S = 18;

	// the edge of the move
	public static final int MOVE_EDGE = 10;

	// the default image type
	public static final String DEFAULT_IMAGE_EXTENSION = "png";

	// cursors
	public static final Cursor CURSOR_NORMAL = new Cursor(Cursor.DEFAULT_CURSOR);
	public static final Cursor CURSOR_MOVE = new Cursor(Cursor.MOVE_CURSOR);

	public static final Cursor NW_RESIZE_CURSOR = new Cursor(Cursor.NW_RESIZE_CURSOR);
	public static final Cursor W_RESIZE_CURSOR = new Cursor(Cursor.W_RESIZE_CURSOR);
	public static final Cursor SW_RESIZE_CURSOR = new Cursor(Cursor.SW_RESIZE_CURSOR);
	public static final Cursor N_RESIZE_CURSOR = new Cursor(Cursor.N_RESIZE_CURSOR);
	public static final Cursor S_RESIZE_CURSOR = new Cursor(Cursor.S_RESIZE_CURSOR);
	public static final Cursor NE_RESIZE_CURSOR = new Cursor(Cursor.NE_RESIZE_CURSOR);
	public static final Cursor E_RESIZE_CURSOR = new Cursor(Cursor.E_RESIZE_CURSOR);
	public static final Cursor SE_RESIZE_CURSOR = new Cursor(Cursor.SE_RESIZE_CURSOR);
}
