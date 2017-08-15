package com.xwintop.xJavaFxTool.controller.epmsTools;

import java.net.URL;
import java.util.ResourceBundle;

import com.xwintop.xcore.util.javafx.FileChooserUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class MessageViewerController implements Initializable {
	@FXML
	private TreeView<String> treeViewMessageStructure;
	@FXML
	private TextArea textAreaMessageText;
	@FXML
	private Button buttonTreeViewToMessageText;
	@FXML
	private Button buttonMessageTextToTreeView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
	}

	private void initView() {
		TreeItem<String> treeItem = new TreeItem<String>("暂无数据");
		
		treeViewMessageStructure.setRoot(treeItem);
	}

	private void initEvent() {
		FileChooserUtil.setOnDragByOpenFile(textAreaMessageText);
	}

	@FXML
	private void treeViewToMessageText(ActionEvent event){
	}

	@FXML
	private void messageTextToTreeView(ActionEvent event){
		TreeItem<String> rootTreeItem = treeViewMessageStructure.getRoot();
		rootTreeItem.setExpanded(true);
		rootTreeItem.getChildren().clear();
		rootTreeItem.setValue(textAreaMessageText.getAccessibleText());
		String textAreaMessageTextString = textAreaMessageText.getText();
		String[] textAreaMessageTextStrings = textAreaMessageTextString.replace("\n", "").split("'");
		for(int i=0;i<textAreaMessageTextStrings.length;i++){
			String[] textStrings = textAreaMessageTextStrings[i].split(":");
			TreeItem<String> treeItem = new TreeItem<String>(i+"\t"+textStrings[0]);
			for(int j= 1;j<textStrings.length;j++){
				TreeItem<String> treeItem2 = new TreeItem<String>(j+"\t"+textStrings[j]);
				treeItem.getChildren().add(treeItem2);
			}
			rootTreeItem.getChildren().add(treeItem);
		}
	}

}
