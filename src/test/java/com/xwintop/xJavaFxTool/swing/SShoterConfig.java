package com.xwintop.xJavaFxTool.swing;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;

/***
 * configure settings for ScreenShoter.
 * 
 * */
public class SShoterConfig {

	//title of the frame
	public static final String FRAME_TITLE = "ScreenShoter";
	
	//screen size
	public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	
	//frame size
	public static final Dimension FRAME_SIZE = new Dimension( 250, 120 );
	
	//frame button size
	public static final Dimension FRAME_BUTTON_SIZE = new Dimension( 60, 20 );
	public static final Dimension FRAME_LABEL_SIZE = new Dimension( 200, 30 );
	
	//buttons
	//buttons panel
	public static final Dimension BUTTONS_PANEL_SIZE = new Dimension( 210, 40 );
	
	public static final Dimension BUTTONS_SIZE = new Dimension( 60, 20 );
	
	
	//the status of the operation
	
	public static final int MOUSE_DRAG = 0;
	public static final int MOUSE_MOVE = 1;
	public static final int MOUSE_NORMAL = 2;
	
	//move state
	public static final int MOUSE_MOVE_W = 11;
	
	public static final int MOUSE_MOVE_WN = 12;
	public static final int MOUSE_MOVE_WS = 13;
	
	public static final int MOUSE_MOVE_E  = 14;
	
	public static final int MOUSE_MOVE_EN = 15;
	public static final int MOUSE_MOVE_ES = 16;
	
	public static final int MOUSE_MOVE_N = 17;
	public static final int MOUSE_MOVE_S = 18;
	
	//the edge of the move
	public static final int MOVE_EDGE = 10;

	
	
	
	
	//the default image type
	public static final String DEFAULT_IMAGE_EXTENSION = "png";
	
	
	//cursors
	
	public static final Cursor CURSOR_NORMAL = new Cursor ( Cursor.DEFAULT_CURSOR );
	public static final Cursor CURSOR_MOVE   = new Cursor( Cursor.MOVE_CURSOR );
	
	public static final Cursor NW_RESIZE_CURSOR = new Cursor( Cursor.NW_RESIZE_CURSOR );
	public static final Cursor W_RESIZE_CURSOR = new Cursor( Cursor.W_RESIZE_CURSOR );
	public static final Cursor SW_RESIZE_CURSOR = new Cursor( Cursor.SW_RESIZE_CURSOR );
	public static final Cursor N_RESIZE_CURSOR = new Cursor ( Cursor.N_RESIZE_CURSOR );
	public static final Cursor S_RESIZE_CURSOR   = new Cursor( Cursor.S_RESIZE_CURSOR );
	public static final Cursor NE_RESIZE_CURSOR = new Cursor( Cursor.NE_RESIZE_CURSOR );
	public static final Cursor E_RESIZE_CURSOR = new Cursor( Cursor.E_RESIZE_CURSOR );
	public static final Cursor SE_RESIZE_CURSOR = new Cursor( Cursor.SE_RESIZE_CURSOR );
}
