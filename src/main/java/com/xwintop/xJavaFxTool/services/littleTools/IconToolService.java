package com.xwintop.xJavaFxTool.services.littleTools;

import java.io.File;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.xwintop.xJavaFxTool.controller.littleTools.IconToolController;
import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import com.xwintop.xcore.util.FileUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import lombok.Setter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

@Setter
public class IconToolService {
	private IconToolController iconToolController;

	public void saveConfigure() throws Exception {
		saveConfigure(ConfigureUtil.getConfigureFile("iconToolConfigure.properties"));
	}

	public void saveConfigure(File file) throws Exception {
		FileUtils.touch(file);
		PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
		xmlConfigure.clear();
		for (int i = 0; i < iconToolController.getIconSizeFlowPane().getChildren().size(); i++) {
			xmlConfigure.setProperty("tableBean" + i, "");
		}
		xmlConfigure.save();
		TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
	}

	public void otherSaveConfigureAction() throws Exception {
		String fileName = "iconToolConfigure.properties";
		File file = FileChooserUtil.chooseSaveFile(fileName, new FileChooser.ExtensionFilter("All File", "*.*"),
				new FileChooser.ExtensionFilter("Properties", "*.properties"));
		if (file != null) {
			saveConfigure(file);
			TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
		}
	}

	public void loadingConfigure() {
		loadingConfigure(ConfigureUtil.getConfigureFile("iconToolConfigure.properties"));
	}

	public void loadingConfigure(File file) {
		try {
			PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
			xmlConfigure.getKeys().forEachRemaining(new Consumer<String>() {
				@Override
				public void accept(String t) {
					// tableData.add(new
					// FileCopyTableBean(xmlConfigure.getString(t)));
				}
			});
		} catch (Exception e) {
			try {
				TooltipUtil.showToast("加载配置失败：" + e.getMessage());
			} catch (Exception e2) {
			}
		}
	}

	public void loadingConfigureAction() {
		File file = FileChooserUtil.chooseFile(new FileChooser.ExtensionFilter("All File", "*.*"),
				new FileChooser.ExtensionFilter("Properties", "*.properties"));
		if (file != null) {
			loadingConfigure(file);
		}
	}

	public void resettingSize() {
		iconToolController.getIconSizeFlowPane().getChildren().clear();
		for (String iconSizeString : iconToolController.getIconSizeStrings()) {
			addSizeAction(iconSizeString);
		}
	}

	public void addSizeAction(String text) {
		CheckBox checkBox = new CheckBox(text);
		checkBox.setPrefWidth(90);
		checkBox.setSelected(true);
		checkBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.SECONDARY) {
					MenuItem menu_Remove = new MenuItem("删除选中尺寸");
					menu_Remove.setOnAction(event1 -> {
						iconToolController.getIconSizeFlowPane().getChildren().remove(checkBox);
					});
					checkBox.setContextMenu(new ContextMenu(menu_Remove));
				}
			}
		});
		iconToolController.getIconSizeFlowPane().getChildren().add(checkBox);
	}

	public void buildIconAction() throws Exception {
		String iconFilePathText = iconToolController.getIconFilePathTextField().getText();
		String iconTargetPathText = iconToolController.getIconTargetPathTextField().getText();
		if (StringUtils.isEmpty(iconFilePathText)) {
			TooltipUtil.showToast("图标未选择！");
			return;
		}
		File iconFilePathFile = new File(iconFilePathText);
		if (!iconFilePathFile.exists()) {
			TooltipUtil.showToast("图标加载失败！");
			return;
		}
		File iconTargetPathFile = null;
		if (StringUtils.isEmpty(iconTargetPathText)) {
			iconTargetPathFile = new File(iconFilePathText).getParentFile();
		} else {
			iconTargetPathFile = new File(iconTargetPathText);
		}
		String iconFileName = FileUtil.getFileName(iconFilePathFile);
		if (StringUtils.isNotEmpty(iconToolController.getIconNameTextField().getText())) {
			iconFileName = iconToolController.getIconNameTextField().getText();
		}
		for (Node node : iconToolController.getIconSizeFlowPane().getChildren()) {
			CheckBox checkBox = ((CheckBox) node);
			if (checkBox.isSelected()) {
				String[] checkBoxText = checkBox.getText().split("\\*");
				int width = Integer.parseInt(checkBoxText[0]);
				int height = Integer.parseInt(checkBoxText[1]);
				String fileName = iconFileName + "-" + width;
				Builder<File> builderFile = Thumbnails.of(iconFilePathFile);
				builderFile.size(width, height)
						.keepAspectRatio(iconToolController.getIsKeepAspectRatioCheckBox().isSelected())
						.outputFormat(iconToolController.getIconFormatChoiceBox().getValue())
						.outputQuality(iconToolController.getOutputQualitySlider().getValue());
				if (iconToolController.getIsWatermarkCheckBox().isSelected()) {
					String watermarkPathText = iconToolController.getWatermarkPathTextField().getText();
					if (StringUtils.isNotEmpty(watermarkPathText)) {
						builderFile.watermark(iconToolController.getWatermarkPositionChoiceBox().getValue(),
								ImageIO.read(new File(watermarkPathText)),
								(float) iconToolController.getWatermarkOpacitySlider().getValue());
					}
				}
				builderFile.toFile(new File(iconTargetPathFile.getPath(), fileName));
			}
		}
	}

	public void buildIconTargetImageAction() throws Exception {
		String iconFilePathText = iconToolController.getIconFilePathTextField().getText();
		if (StringUtils.isEmpty(iconFilePathText)) {
			TooltipUtil.showToast("图标未选择！");
			return;
		}
		File iconFilePathFile = new File(iconFilePathText);
		if (!iconFilePathFile.exists()) {
			TooltipUtil.showToast("图标加载失败！");
			return;
		}
		Builder<File> builderFile = Thumbnails.of(iconFilePathFile);
		builderFile.size(150, 150).keepAspectRatio(iconToolController.getIsKeepAspectRatioCheckBox().isSelected())
				.outputFormat(iconToolController.getIconFormatChoiceBox().getValue())
				.outputQuality(iconToolController.getOutputQualitySlider().getValue());
		if (iconToolController.getIsWatermarkCheckBox().isSelected()) {
			String watermarkPathText = iconToolController.getWatermarkPathTextField().getText();
			if (StringUtils.isNotEmpty(watermarkPathText)) {
				builderFile.watermark(iconToolController.getWatermarkPositionChoiceBox().getValue(),
						ImageIO.read(new File(watermarkPathText)),
						(float) iconToolController.getWatermarkOpacitySlider().getValue());
			}
		}
		Image image = SwingFXUtils.toFXImage(builderFile.asBufferedImage(), null);
		iconToolController.getIconTargetImageView().setImage(image);
	}

	public IconToolService(IconToolController iconToolController) {
		super();
		this.iconToolController = iconToolController;
	}
}
