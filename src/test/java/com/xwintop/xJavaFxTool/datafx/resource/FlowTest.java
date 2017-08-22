package com.xwintop.xJavaFxTool.datafx.resource;

import java.util.Locale;
import java.util.ResourceBundle;

import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FlowTest extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ResourceBundle resources = ResourceBundle.getBundle("locale.Dorian", Locale.getDefault());
//		ResourceBundleFlow myFlow = ResourceBundleFlow.getMyFlow(ResourceController.class, resources);
//		myFlow.startInStage(primaryStage);
//		Parent parent = primaryStage.getScene().getRoot();
//		System.out.println(parent.getId());
		
		ResourceBundleFlow flow = ResourceBundleFlow.getMyFlow(ResourceController.class,resources);
		DefaultFlowContainer container = new DefaultFlowContainer();
		ViewFlowContext flowContext = new ViewFlowContext();
		flow.createHandler(flowContext).start(container);
		primaryStage.setResizable(true);
		primaryStage.setScene(new Scene(container.getView()));
		primaryStage.show();
		ResourceController object = (ResourceController) flowContext.getCurrentViewContext().getController();
		object.onAction();
	}

}
