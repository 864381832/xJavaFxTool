package com.xwintop.xJavaFxTool.datafx.resource;

import java.util.ResourceBundle;

import io.datafx.controller.ViewConfiguration;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowContainer;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.scene.Node;

public class ResourceBundleFlow extends Flow {

	public static ResourceBundleFlow getMyFlow(Class<?> startViewControllerClass, ResourceBundle resources) {
		ViewConfiguration viewConfiguration = new ViewConfiguration();
		viewConfiguration.setResources(resources);
		ResourceBundleFlow myFlow = new ResourceBundleFlow(startViewControllerClass, viewConfiguration);
		return myFlow;
	}

	public ResourceBundleFlow(Class<?> startViewControllerClass) {
		super(startViewControllerClass);
	}

	public ResourceBundleFlow(Class<?> startViewControllerClass, ViewConfiguration viewConfiguration) {
		super(startViewControllerClass, viewConfiguration);
	}

	@Override
	public FlowHandler createHandler(ViewFlowContext flowContext) {
		return new FlowHandler(this, flowContext, this.getViewConfiguration());
	}

	@Override
	public <T extends Node> T start(FlowContainer<T> flowContainer) throws FlowException {
		createHandler(new ViewFlowContext()).start(flowContainer);
		return flowContainer.getView();
	}
}
