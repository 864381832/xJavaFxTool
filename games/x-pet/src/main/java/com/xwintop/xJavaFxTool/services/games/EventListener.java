package com.xwintop.xJavaFxTool.services.games;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class EventListener implements EventHandler<MouseEvent> {
	private ImageView imageView;
	int gifID = 0;//图片编号
	int petID = 0;//宠物ID
	double time = 3;//播放动画的时间
	public EventListener(ImageView imgView, int pet) {
		imageView=imgView;
		petID = pet;
	}
	public void handle(MouseEvent e) {
		if(gifID!=0) return;	//如果动作没做完，就不允许再做新的动作
		double x = e.getX();
		double y = e.getY();
//		System.out.println(x+" "+y);//测试眼睛等部位的位置
		//选择动作
		if(petID == 0) lxhBehavior(x,y);
		else biuBehavior(x,y);
		loadImg(petID,gifID,time);//显示图片
	}
	//罗小黑的动作
	public void lxhBehavior(double x,double y) {
		//以下的“左”“右”都是相对于用户来说的
		//点击左眼
		if(x>20 & x<42 & y>125 & y<143) {
			gifID = 1;
			time = 2.8;
		}
		//点击右眼
		else if(x>63 & x<90 & y>125 & y<143) {
			gifID = 2;
			time = 3.85;
		}
		//点击右耳
		else if(x>93 & x<110 & y>80 & y<100) {
			gifID = 3;
			time = 6.3;
		}
		//点击身体
		else if(x>110 & x<130 & y>125 & y<155) {
			gifID = 4;
			time = 3;
		}
		//点击小小黑
		else if(x>152 & x<175 & y>157 & y<172) {
			gifID = 5;
			time = 3.5;
		}
		else {
			gifID = 0;
		}
	}
	//比丢的动作
	private void biuBehavior(double x, double y) {
		//以下的“左”“右”都是相对于用户来说的
		//点击左眼
		if(x>40 & x<51 & y>60 & y<67) {
			gifID = 1;
			time = 3.7;
		}
		//点击右眼
		else if(x>87 & x<100 & y>58 & y<69) {
			gifID = 2;
			time = 4.45;
		}
		//点击嘴
		else if(x>62 & x<76 & y>61 & y<69) {
			gifID = 3;
			time = 5.3;
		}
		//点击左手
		else if(x>31 & x<49 & y>87 & y<117) {
			gifID = 4;
			time = 1.75;
		}
		//点击右手
		else if(x>86 & x<107 & y>85 & y<114) {
			gifID = 5;
			time = 4;
		}
		//点击额头
		else if(x>43 & x<94 & y>34 & y<52) {
			gifID = 6;
			time = 1.8;
		}
		//点击肚子
		else if(x>64 & x<79 & y>86 & y<130) {
			gifID = 7;
			time = 4.1;
		}
		else {
			gifID = 0;
		}
	}
	//点击部位后加载图片
	public void loadImg(int petID,int gifID, double time) {
		this.gifID = gifID;
		if(gifID!=0) {
			Image newimage;
		if(petID==0)
			newimage = new Image(this.getClass().getResourceAsStream("/lxh/罗小黑"+gifID+".gif"));
		else 
			newimage = new Image(this.getClass().getResourceAsStream("/biu/biu"+gifID+".gif"));
		
		imageView.setImage(newimage);
		//中断动图的播放，切换至主图
		new Timeline(new KeyFrame(Duration.seconds(time), ae ->mainimg(this.petID,0))).play();
		}
    }
	//主图，负责等待时和退出时的动作
	public void mainimg(int pet,int key) {
		Image newimage;
		if(pet==0)
			newimage = new Image(this.getClass().getResourceAsStream("/lxh/罗小黑"+key+".gif"));
		else
			newimage = new Image(this.getClass().getResourceAsStream("/biu/biu"+key+".gif"));
		imageView.setImage(newimage);
		//这里是为了保证能做出新的动作，对应于handle方法的if(gifID!=0) return;
		//同时也是为了做其他动作时不被“自行走动”和“自娱自乐”打断
		if(key == 0) gifID=0;
    }
	
//	public void setPetID(int id) {
//		petID = id;
//	}
}
