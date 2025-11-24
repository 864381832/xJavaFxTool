package com.xwintop.xJavaFxTool.services.games;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Move extends Thread{
	private long time;
	private ImageView imageView;
	private int direID;
    double x;
    double maxx;
    double width;
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    Stage stage;
	private EventListener listen;
	private int petID;
	boolean exit;
	
	public Move(long time, ImageView imgView, int dire, Stage primaryStage, EventListener el) {
		this.time = time;
		imageView = imgView;
		direID = dire;
		stage = primaryStage;
		listen = el;
		petID = listen.petID;//此petID是调用Move时的petID
	}
	
	public void run() {
		//点击就停下
		/*使用listen.petID而不是定义一个变量int petID = listen.petID;
		 *是因为在运动过程中点击“切换宠物”时实际的petID会改变，所以使用listen.petID就可以做到同步改变。
		 *若下面使用listen.mainimg(petID,0)显示的就是点击“切换宠物”前的宠物，这个petID就是旧的petID。
		 */
		imageView.addEventHandler(MouseEvent.MOUSE_PRESSED,
				e ->{exit = true;listen.mainimg(listen.petID, 0);});
		while(!exit) {
			//如果petID!=listen.petID，则已“切换宠物”，此时要结束运动。
			if(petID!=listen.petID) {
				exit=true;
				return;
			}
		    width = imageView.getBoundsInLocal().getMaxX();
		    x = stage.getX();
		    maxx = screenBounds.getMaxX();
			double speed=15;
	        if(x+speed+width >= maxx | x-speed<=0 | time<=0) {
	        	this.interrupt();
	        	listen.mainimg(listen.petID, 0);
	        	return;
	        }
	        if(direID == 0) {	//向左走
	        	stage.setX(x-speed);
	        }
	        else if(direID == 1) {	//向右走
	        	stage.setX(x+speed);
	        }
	        time -= 300;
	        try {
			     Thread.sleep(300);
			    } catch (InterruptedException e) {    
			     e.printStackTrace();
			    }
		}
	}
}
