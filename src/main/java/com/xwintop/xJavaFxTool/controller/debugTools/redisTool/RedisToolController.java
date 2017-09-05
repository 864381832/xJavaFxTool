package com.xwintop.xJavaFxTool.controller.debugTools.redisTool;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import com.xwintop.xJavaFxTool.services.debugTools.redisTool.RedisToolService;
import com.xwintop.xJavaFxTool.view.debugTools.redisTool.RedisToolView;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.Getter;

@Getter
public class RedisToolController extends RedisToolView {
	private RedisToolService redisToolService = new RedisToolService(this);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
		initService();
	}

	private void initView() {
		TreeItem<String> treeItem = new TreeItem<String>("Redis服务器");
		redisServiceTreeView.setRoot(treeItem);
	}

	private void initEvent() {
		redisServiceTreeView.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY) {
				MenuItem menu_addServer = new MenuItem("添加服务器");
				menu_addServer.setOnAction(event1 -> {
					int row = 0;
					GridPane page1Grid = new GridPane();
					page1Grid.setVgap(10);
					page1Grid.setHgap(10);

					TextField txName = createTextField(null, page1Grid, "Name:", row++);
					TextField txHost = createTextField("localhost", page1Grid, "Host:", row++);
					TextField txPort = createTextField("6379", page1Grid, "Port:", row++);
					TextField txPassword = createTextField(null, page1Grid, "Password:", row++);

					Alert alert = new Alert(Alert.AlertType.NONE, null, new ButtonType("取消", ButtonBar.ButtonData.NO),
							new ButtonType("确定", ButtonBar.ButtonData.YES));
					alert.setTitle("添加服务器");
					alert.setGraphic(page1Grid);
					alert.setWidth(200);
					Optional<ButtonType> _buttonType = alert.showAndWait();
					// 根据点击结果返回
					if (_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
						if (StringUtils.isEmpty(txName.getText()) || StringUtils.isEmpty(txHost.getText())
								|| StringUtils.isEmpty(txPort.getText())) {
							TooltipUtil.showToast("请输入服务器信息");
							return;
						}
						redisToolService.addServiceAddress(txName.getText(), txHost.getText(), Integer.parseInt(txPort.getText()), txPassword.getText());
					}
				});
				MenuItem menu_Refresh = new MenuItem("刷新");
				menu_Refresh.setOnAction(event1 -> {
				});
				redisServiceTreeView.setContextMenu(new ContextMenu(menu_addServer, menu_Refresh));
			}
		});
	}

	private void initService() {
	}

	@FXML
	private void treeLeftAction(ActionEvent event) {
	}

	@FXML
	private void treeRightAction(ActionEvent event) {
	}

	@FXML
	private void treeUpAction(ActionEvent event) {
	}

	@FXML
	private void treeRefurbishAction(ActionEvent event) {
	}

	private TextField createTextField(String textValue, GridPane gridPane, String label, int row) {
		TextField textField = new TextField(textValue);
		GridPane.setHgrow(textField, Priority.ALWAYS);
		gridPane.add(new Label(label), 0, row);
		gridPane.add(textField, 1, row);
		return textField;
	}
}
