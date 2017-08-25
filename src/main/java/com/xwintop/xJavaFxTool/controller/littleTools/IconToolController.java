package com.xwintop.xJavaFxTool.controller.littleTools;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;

import com.xwintop.xJavaFxTool.services.littleTools.IconToolService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.littleTools.IconToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import net.coobird.thumbnailator.geometry.Positions;

/** 
 * @ClassName: IconToolController 
 * @Description: 图标转换工具
 * @author: xufeng
 * @date: 2017年8月25日 上午9:54:55  
 */
@Getter
@Setter
public class IconToolController extends IconToolView {
	private IconToolService iconToolService = new IconToolService(this);
	private String[] iconSizeStrings = new String[] { "16*16", "20*20", "28*28", "29*29", "32*32", "36*36", "40*40",
			"48*48", "50*50", "57*57", "58*58", "60*60", "64*64", "72*72", "76*76", "80*80", "87*87", "90*90", "96*96",
			"100*100", "108*108", "114*114", "120*120", "128*128", "144*144", "152*152", "155*155", "167*167",
			"180*180", "192*192", "256*256", "512*512" };
	private String[] iconFormatStrings = new String[] { "png", "jpg", "gif", "jpeg", "bmp" };
	private Positions[] watermarkPositions = Positions.values();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initView();
		initEvent();
		initService();
	}

	private void initView() {
		iconToolService.resettingSize();
		JavaFxViewUtil.setSliderLabelFormatter(watermarkOpacitySlider, "0.0");
		JavaFxViewUtil.setSliderLabelFormatter(outputQualitySlider, "0.0");
		JavaFxViewUtil.setSpinnerValueFactory(widthSpinner, 1, Integer.MAX_VALUE);
		JavaFxViewUtil.setSpinnerValueFactory(heightSpinner, 1, Integer.MAX_VALUE);
		iconFormatChoiceBox.getItems().addAll(iconFormatStrings);
		iconFormatChoiceBox.setValue(iconFormatStrings[0]);
		watermarkPositionChoiceBox.getItems().addAll(watermarkPositions);
		watermarkPositionChoiceBox.setValue(watermarkPositions[8]);
	}

	private void initEvent() {
		FileChooserUtil.setOnDrag(iconFilePathTextField, FileChooserUtil.FileType.FILE);
		FileChooserUtil.setOnDrag(iconTargetPathTextField, FileChooserUtil.FileType.FOLDER);
		FileChooserUtil.setOnDrag(watermarkPathTextField, FileChooserUtil.FileType.FILE);
		for (Node node : iconSizeFlowPane.getChildren()) {
			((CheckBox) node).selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					boolean isAllSelect = true;
					for (Node node : iconSizeFlowPane.getChildren()) {
						if (!((CheckBox) node).isSelected()) {
							isAllSelect = false;
						}
					}
					setSelectButtonDisable(isAllSelect);
				}
			});
		}
		iconFilePathTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
				if(StringUtils.isEmpty(newValue)){
					iconImageView.setImage(null);
					return;
				}
				try {
					File file = new File(newValue);
					Image image = SwingFXUtils.toFXImage(ImageIO.read(file), null);
					iconImageView.setImage(image);
				} catch (Exception e) {
					TooltipUtil.showToast("图片加载异常");
				}
			}
		});
		watermarkPathTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
				if(StringUtils.isEmpty(newValue)){
					watermarkImageView.setImage(null);
					return;
				}
				try {
					File file = new File(newValue);
					Image image = SwingFXUtils.toFXImage(ImageIO.read(file), null);
					watermarkImageView.setImage(image);
				} catch (Exception e) {
					TooltipUtil.showToast("图片加载异常");
				}
			}
		});
	}

	private void initService() {
		iconToolService.loadingConfigure();
	}

	/** 
	 * @Title: chooseOriginalPathAction 
	 * @Description: 选择图标图片
	 */
	@FXML
	private void chooseOriginalPathAction(ActionEvent event) throws Exception {
		File file = FileChooserUtil.chooseFile(new FileChooser.ExtensionFilter("All Images", "*.*"),
				new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("PNG", "*.png"),
				new FileChooser.ExtensionFilter("gif", "*.gif"), new FileChooser.ExtensionFilter("jpeg", "*.jpeg"),
				new FileChooser.ExtensionFilter("bmp", "*.bmp"));
		if (file != null) {
			try {
				iconFilePathTextField.setText(file.getPath());
				Image image = SwingFXUtils.toFXImage(ImageIO.read(file), null);
				iconImageView.setImage(image);
			} catch (Exception e) {
				TooltipUtil.showToast("图片加载异常");
			}
		}
	}

	/** 
	 * @Title: chooseTargetPathAction 
	 * @Description: 选择生成目录（为空则为原图片目录）
	 */
	@FXML
	private void chooseTargetPathAction(ActionEvent event) {
		File file = FileChooserUtil.chooseDirectory();
		if (file != null) {
			iconTargetPathTextField.setText(file.getPath());
		}
	}

	@FXML
	private void addSizeAction(ActionEvent event) {
		iconToolService.addSizeAction(widthSpinner.getValue() + "*" + heightSpinner.getValue());
	}

	/** 
	 * @Title: resettingSizeAction 
	 * @Description: 重置图片尺寸选择CheckBox
	 */
	@FXML
	private void resettingSizeAction(ActionEvent event) {
		iconToolService.resettingSize();
	}

	/** 
	 * @Title: saveConfigure 
	 * @Description: 保存配置
	 */
	@FXML
	private void saveConfigure(ActionEvent event) throws Exception {
		iconToolService.saveConfigure();
	}

	/** 
	 * @Title: otherSaveConfigureAction 
	 * @Description: 配置另存为
	 */
	@FXML
	private void otherSaveConfigureAction(ActionEvent event) throws Exception {
		iconToolService.otherSaveConfigureAction();
	}

	/** 
	 * @Title: loadingConfigureAction 
	 * @Description: 加载配置
	 */
	@FXML
	private void loadingConfigureAction(ActionEvent event) {
		iconToolService.loadingConfigureAction();
	}

	/** 
	 * @Title: buildIconAction 
	 * @Description: 生成图片
	 */
	@FXML
	private void buildIconAction(ActionEvent event) throws Exception {
		try {
			iconToolService.buildIconAction();
		} catch (Exception e) {
			TooltipUtil.showToast(e.getMessage());
		}
	}
	/** 
	 * @Title: buildIconTargetImageAction 
	 * @Description: 生成预览图片
	 */
	@FXML
	private void buildIconTargetImageAction(ActionEvent event) throws Exception {
		try {
			iconToolService.buildIconTargetImageAction();
		} catch (Exception e) {
			TooltipUtil.showToast(e.getMessage());
		}
	}

	@FXML
	private void allSelectAction(ActionEvent event) {
		for (Node node : iconSizeFlowPane.getChildren()) {
			((CheckBox) node).setSelected(true);
		}
		setSelectButtonDisable(true);
	}

	@FXML
	private void inverseAction(ActionEvent event) {
		for (Node node : iconSizeFlowPane.getChildren()) {
			CheckBox checkBox = ((CheckBox) node);
			checkBox.setSelected(!checkBox.isSelected());
		}
		allSelectButton.setDisable(false);
		inverseButton.setDisable(false);
		allNotSelectButton.setDisable(false);
	}

	@FXML
	private void allNotSelectAction(ActionEvent event) {
		for (Node node : iconSizeFlowPane.getChildren()) {
			((CheckBox) node).setSelected(false);
		}
		setSelectButtonDisable(false);
	}

	@FXML
	private void selectWatermarkAction(ActionEvent event) throws Exception {
		File file = FileChooserUtil.chooseFile(new FileChooser.ExtensionFilter("All Images", "*.*"),
				new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("PNG", "*.png"),
				new FileChooser.ExtensionFilter("gif", "*.gif"), new FileChooser.ExtensionFilter("jpeg", "*.jpeg"),
				new FileChooser.ExtensionFilter("bmp", "*.bmp"));
		if (file != null) {
			try {
				watermarkPathTextField.setText(file.getPath());
				Image image = SwingFXUtils.toFXImage(ImageIO.read(file), null);
				watermarkImageView.setImage(image);
			} catch (Exception e) {
				TooltipUtil.showToast("水印图片加载异常");
			}
		}
	}

	/**
	 * @Title: setSelectButtonDisable
	 * @Description: 设置按钮状态
	 */
	private void setSelectButtonDisable(boolean isAllSelect) {
		if (isAllSelect) {
			allSelectButton.setDisable(true);
			inverseButton.setDisable(true);
			allNotSelectButton.setDisable(false);
		} else {
			allSelectButton.setDisable(false);
			inverseButton.setDisable(false);
			allNotSelectButton.setDisable(true);
		}
	}
}