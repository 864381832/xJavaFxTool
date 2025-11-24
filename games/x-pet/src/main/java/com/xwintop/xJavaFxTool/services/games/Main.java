package com.xwintop.xJavaFxTool.services.games;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {
	private static ImageView imageView;
	EventListener listen;
	VBox messageBox;
	int petID = 1;//宠物ID。罗小黑=0，比丢=1
	double xOffset = 0; 
	double yOffset = 0;
	
	public void start(Stage primaryStage) {
		try {
			/*
			 * 创建初始的图
			 * 加载相对路径的图片要用class.getResource，不然运行jar包时会报错：找不到文件路径！
			 * 路径中第一个“/”是必需的，它表示类的根目录，类文件夹在此项目中与lxh和biu在同一级
			 */
			Image image = new Image(this.getClass().getResourceAsStream("/biu/biu0.gif"));
	      	imageView = new ImageView(image);
	      	imageView.setX(0); 
	      	imageView.setY(0);
	      	imageView.setLayoutX(0);
	      	imageView.setLayoutY(50);
	      	//设置图片显示的大小
	      	imageView.setFitHeight(150); 
	      	imageView.setFitWidth(150); 
	      	//添加图片的点击事件
	      	listen = new EventListener(imageView , petID);
	      	imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, listen);
	      	
	      	imageView.setPreserveRatio(true); //保留 width：height的比例
	      	imageView.setStyle("-fx-background:transparent;");//容器背景设为透明
	      	
	      	UI ui = new UI(imageView, petID, listen,primaryStage);
	      	ui.addMessageBox("你好吖~");
	      	
			AnchorPane pane = new AnchorPane(ui.getMessageBox(),ui.getImageView());
			
			pane.setStyle("-fx-background:transparent;");
			//使窗体能拖动。先获取按下鼠标时的坐标p1，再将窗体坐标设为p1加拖动的位移量
			pane.setOnMousePressed(event -> {
			    xOffset = event.getSceneX();
			    yOffset = event.getSceneY();
			});
			pane.setOnMouseDragged(event -> {
				primaryStage.setX(event.getScreenX() - xOffset);
				primaryStage.setY(event.getScreenY() - yOffset);
			});
			
			Scene scene = new Scene(pane,400,400);
			scene.setFill(null);

			primaryStage.setScene(scene);
			//设置窗体的初始位置
			primaryStage.setX(850);
			primaryStage.setY(400);
			primaryStage.setAlwaysOnTop(true);//窗口总显示在最前
			//修改任务栏图标
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
			//下句隐藏任务栏图标，但javafx的stage.initStyle(Style)只能有一个起效，只好作罢
//			primaryStage.initStyle(StageStyle.UTILITY);
			primaryStage.initStyle(StageStyle.TRANSPARENT);//背景透明
			/*
			 * 点击任务栏的“关闭窗口”时，播放告别动画，同时使托盘的图标也关闭.
			 * event.consume()是必需的，这样才能真正阻止Window Close事件的默认处理。
			 * 如果仅仅使用System.exit(0);则不需要event.consume();
			*/
			primaryStage.setOnCloseRequest( event ->{event.consume(); ui.end();});
			primaryStage.show();
			
			ui.setTray(primaryStage);//添加系统托盘
			Thread thread = new Thread(ui);
			thread.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		launch(args);
	}
}
