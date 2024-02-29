package com.xwintop.xJavaFxTool.services.littleTools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.BiConsumer;

import javax.imageio.ImageIO;

import com.xwintop.xJavaFxTool.utils.ImageUtil;
import com.xwintop.xcore.util.ConfigureUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xwintop.xJavaFxTool.controller.littleTools.IconToolController;
import com.xwintop.xJavaFxTool.utils.ImgToolUtil;
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

/**
 * @ClassName: IconToolService
 * @Description: 图标生成工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:20
 */

@Setter
public class IconToolService {
	private IconToolController iconToolController;

	public void saveConfigure() throws Exception {
		saveConfigure(ConfigureUtil.getConfigureFile("iconToolConfigure.json"));
	}

	/**
	 * @Title: saveConfigure
	 * @Description: 保存配置到文件中
	 */
	public void saveConfigure(File file) throws Exception {
		FileUtils.touch(file);
		Properties xmlConfigure = new Properties();
        xmlConfigure.load(new FileInputStream(file));
		xmlConfigure.clear();
		xmlConfigure.put("iconFilePathTextField", iconToolController.getIconFilePathTextField().getText());
		xmlConfigure.put("iconTargetPathTextField", iconToolController.getIconTargetPathTextField().getText());
		xmlConfigure.put("isCornerCheckBox", iconToolController.getIsCornerCheckBox().isSelected());
		xmlConfigure.put("cornerSizeSlider", iconToolController.getCornerSizeSlider().getValue());
		xmlConfigure.put("isKeepAspectRatioCheckBox", iconToolController.getIsKeepAspectRatioCheckBox().isSelected());
		xmlConfigure.put("isWatermarkCheckBox", iconToolController.getIsWatermarkCheckBox().isSelected());
		xmlConfigure.put("watermarkPathTextField", iconToolController.getWatermarkPathTextField().getText());
		xmlConfigure.put("iconFormatChoiceBox", iconToolController.getIconFormatChoiceBox().getValue());
		xmlConfigure.put("iconNameTextField", iconToolController.getIconNameTextField().getText());
		xmlConfigure.put("iosIconCheckBox", iconToolController.getIosIconCheckBox().isSelected());
		xmlConfigure.put("androidCheckBox", iconToolController.getAndroidCheckBox().isSelected());
		xmlConfigure.put("watermarkPositionChoiceBox", iconToolController.getWatermarkPositionChoiceBox().getValue());
		xmlConfigure.put("watermarkOpacitySlider", iconToolController.getWatermarkOpacitySlider().getValue());
		xmlConfigure.put("outputQualitySlider", iconToolController.getOutputQualitySlider().getValue());
		List<String> iconSizeFlowPaneList = new ArrayList<String>();
		for (Node node : iconToolController.getIconSizeFlowPane().getChildren()) {
			CheckBox checkBox = ((CheckBox) node);
			iconSizeFlowPaneList.add(checkBox.getText() + "_" + checkBox.isSelected());
		}
		xmlConfigure.put("iconSizeFlowPaneList", iconSizeFlowPaneList);
//		xmlConfigure.save();
		TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
	}

	public void otherSaveConfigureAction() throws Exception {
		String fileName = "iconToolConfigure.properties";
		File file = FileChooserUtil.chooseSaveFile(fileName, new FileChooser.ExtensionFilter("All File", "*.*"),
				new FileChooser.ExtensionFilter("Properties", "*.json"));
		if (file != null) {
			saveConfigure(file);
			TooltipUtil.showToast("保存配置成功,保存在：" + file.getPath());
		}
	}

	public void loadingConfigure() {
		loadingConfigure(ConfigureUtil.getConfigureFile("iconToolConfigure.properties"));
	}

	/**
	 * @Title: loadingConfigure
	 * @Description: 从文件中加载配置
	 */
	public void loadingConfigure(File file) {
		try {
            Properties xmlConfigure = new Properties();
            xmlConfigure.load(new FileInputStream(file));
//			iconToolController.getIconFilePathTextField().setText(xmlConfigure.getString("iconFilePathTextField"));
//			iconToolController.getIconTargetPathTextField().setText(xmlConfigure.getString("iconTargetPathTextField"));
//			iconToolController.getIsCornerCheckBox().setSelected(xmlConfigure.getBoolean("isCornerCheckBox"));
//			iconToolController.getCornerSizeSlider().setValue(xmlConfigure.getDouble("cornerSizeSlider"));
//			iconToolController.getIsKeepAspectRatioCheckBox().setSelected(xmlConfigure.getBoolean("isKeepAspectRatioCheckBox"));
//			iconToolController.getIsWatermarkCheckBox().setSelected(xmlConfigure.getBoolean("isWatermarkCheckBox"));
//			iconToolController.getWatermarkPathTextField().setText(xmlConfigure.getString("watermarkPathTextField"));
//			iconToolController.getIconFormatChoiceBox().setValue(xmlConfigure.getString("iconFormatChoiceBox"));
//			iconToolController.getIconNameTextField().setText(xmlConfigure.getString("iconNameTextField"));
//			iconToolController.getIosIconCheckBox().setSelected(xmlConfigure.getBoolean("iosIconCheckBox"));
//			iconToolController.getAndroidCheckBox().setSelected(xmlConfigure.getBoolean("androidCheckBox"));
//			iconToolController.getWatermarkPositionChoiceBox().setValue(Positions.valueOf(xmlConfigure.getString("watermarkPositionChoiceBox")));
//			iconToolController.getWatermarkOpacitySlider().setValue(xmlConfigure.getDouble("watermarkOpacitySlider"));
//			iconToolController.getOutputQualitySlider().setValue(xmlConfigure.getDouble("outputQualitySlider"));
//			List<Object> iconSizeFlowPaneList = xmlConfigure.getList("iconSizeFlowPaneList");
//			iconToolController.getIconSizeFlowPane().getChildren().clear();
//			for (Object iconSize : iconSizeFlowPaneList) {
//				String[] checkBox = iconSize.toString().split("_");
//				addSizeAction(checkBox[0], Boolean.parseBoolean(checkBox[1]));
//			}
		} catch (Exception e) {
			try {
				TooltipUtil.showToast("加载配置失败：" + e.getMessage());
			} catch (Exception e2) {
			}
		}
	}

	public void loadingConfigureAction() {
		File file = FileChooserUtil.chooseFile(new FileChooser.ExtensionFilter("All File", "*.*"),
				new FileChooser.ExtensionFilter("Properties", "*.json"));
		if (file != null) {
			loadingConfigure(file);
		}
	}

	/**
	 * @Title: resettingSize
	 * @Description: 重置图片尺寸选择CheckBox
	 */
	public void resettingSize() {
		iconToolController.getIconSizeFlowPane().getChildren().clear();
		for (String iconSizeString : iconToolController.getIconSizeStrings()) {
			addSizeAction(iconSizeString);
		}
	}

	public void addSizeAction(String text) {
		addSizeAction(text, true);
	}

	/**
	 * @Title: addSizeAction
	 * @Description: 添加图片尺寸选择CheckBox
	 */
	public void addSizeAction(String text, boolean isSelect) {
		CheckBox checkBox = new CheckBox(text);
		checkBox.setPrefWidth(90);
		checkBox.setSelected(isSelect);
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

	/**
	 * @Title: buildIconAction
	 * @Description: 生成图片
	 */
	public void buildIconAction() throws Exception {
		String iconFilePathText = iconToolController.getIconFilePathTextField().getText();
		String iconTargetPathText = iconToolController.getIconTargetPathTextField().getText();
		String iconFormat = iconToolController.getIconFormatChoiceBox().getValue();
		if (StringUtils.isEmpty(iconFilePathText)) {
			TooltipUtil.showToast("图标未选择！");
			throw new Exception("图标未选择！");
		}
		File iconFilePathFile = new File(iconFilePathText);
		if (!iconFilePathFile.exists()) {
			TooltipUtil.showToast("图标加载失败！");
			throw new Exception("图标加载失败！");
		}
		File iconTargetPathFile = null;
		if (StringUtils.isEmpty(iconTargetPathText)) {
			iconTargetPathFile = new File(iconFilePathText).getParentFile();
		} else {
			iconTargetPathFile = new File(iconTargetPathText);
		}
		String iconFileName = FilenameUtils.getBaseName(iconFilePathFile.getName());
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
				BufferedImage bufferedImage = getBufferedImage(iconFilePathFile, width, height);
//				ImageIO.write(bufferedImage, iconFormat,
//						new File(iconTargetPathFile.getPath(), fileName + "." + iconFormat));
				ImageUtil.writeImage(bufferedImage,new File(iconTargetPathFile.getPath(),fileName + "." + iconFormat));
			}
		}
		if (iconToolController.getIosIconCheckBox().isSelected()) {
			File appIconAppiconsetPathFile = new File(iconTargetPathFile.getPath() + "/ios/AppIcon.appiconset/");
			File contentsFile = new File(Object.class.getResource("/data/iosAppIcon/Contents.json").getFile());
			FileUtils.copyFileToDirectory(contentsFile, appIconAppiconsetPathFile);
			BufferedImage bufferedImage512 = getBufferedImage(iconFilePathFile, 512, 512);
//			ImageIO.write(bufferedImage512, "png", new File(iconTargetPathFile.getPath() + "/ios", "iTunesArtwork.png"));
			ImageUtil.writeImage(bufferedImage512, new File(iconTargetPathFile.getPath() + "/ios", "iTunesArtwork.png"));
			BufferedImage bufferedImage1024 = getBufferedImage(iconFilePathFile, 1024, 1024);
//			ImageIO.write(bufferedImage1024, "png", new File(iconTargetPathFile.getPath() + "/ios", "iTunesArtwork@2x.png"));
			ImageUtil.writeImage(bufferedImage1024, new File(iconTargetPathFile.getPath() + "/ios", "iTunesArtwork@2x.png"));

			JSONArray jSONArray = JSON.parseObject(FileUtils.readFileToString(contentsFile, Charset.defaultCharset()))
					.getJSONArray("images");
			for (int i = 0; i < jSONArray.size(); i++) {
				JSONObject jsonObject = jSONArray.getJSONObject(i);
				System.out.println(jsonObject.toString());
				float size = Float.parseFloat(jsonObject.getString("size").split("x")[0])
						* Integer.parseInt(jsonObject.getString("scale").substring(0, 1));
				BufferedImage bufferedImage = getBufferedImage(iconFilePathFile, (int) size, (int) size);
//				ImageIO.write(bufferedImage, "png", new File(iconTargetPathFile.getPath() + "/ios/AppIcon.appiconset",
//						jsonObject.getString("filename")));
				ImageUtil.writeImage(bufferedImage,new File(iconTargetPathFile.getPath() + "/ios/AppIcon.appiconset",
						jsonObject.getString("filename")));
			}
		}
		if (iconToolController.getAndroidCheckBox().isSelected()) {
			File appIconAppiconsetPathFile = new File(iconTargetPathFile.getPath() + "/android/");
			appIconAppiconsetPathFile.mkdirs();
			BufferedImage bufferedImage512 = getBufferedImage(iconFilePathFile, 512, 512);
//			ImageIO.write(bufferedImage512, "png", new File(appIconAppiconsetPathFile, "ic_launcher.png"));
			ImageUtil.writeImage(bufferedImage512, new File(appIconAppiconsetPathFile, "ic_launcher.png"));
			final String iconTargetPathFileString = iconTargetPathFile.getPath() + "/android/";
			Map<String, Integer> sizeMap = new HashMap<String, Integer>();
			sizeMap.put("mipmap-hdpi", 72);
			sizeMap.put("mipmap-ldpi", 36);
			sizeMap.put("mipmap-mdpi", 48);
			sizeMap.put("mipmap-xhdpi", 96);
			sizeMap.put("mipmap-xxhdpi", 114);
			sizeMap.put("mipmap-xxxhdpi", 192);
			sizeMap.forEach(new BiConsumer<String, Integer>() {
				@Override
				public void accept(String key, Integer value) {
					try {
						new File(iconTargetPathFileString + key).mkdirs();
						BufferedImage bufferedImage = getBufferedImage(iconFilePathFile, value, value);
//						ImageIO.write(bufferedImage, "png", new File(iconTargetPathFileString + key, "ic_launcher.png"));
						ImageUtil.writeImage(bufferedImage, new File(iconTargetPathFileString + key, "ic_launcher.png"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		TooltipUtil.showToast("生成图片成功，请在路径：" + iconTargetPathFile.getPath() + "下查看。");
	}

	/**
	 * @Title: getBufferedImage
	 * @Description: 根据条件获取生成的图片
	 */
	private BufferedImage getBufferedImage(File iconFilePathFile, int width, int height) throws Exception {
//		Builder<File> builderFile = Thumbnails.of(iconFilePathFile);
		Builder<BufferedImage> builderFile = Thumbnails.of(ImageUtil.getBufferedImage(iconFilePathFile));
		builderFile.size(width, height).keepAspectRatio(iconToolController.getIsKeepAspectRatioCheckBox().isSelected())
				.outputFormat(iconToolController.getIconFormatChoiceBox().getValue())
				.outputQuality(iconToolController.getOutputQualitySlider().getValue());
		if (iconToolController.getIsWatermarkCheckBox().isSelected()) {
			String watermarkPathText = iconToolController.getWatermarkPathTextField().getText();
			if (StringUtils.isNotEmpty(watermarkPathText)) {
				try {
					builderFile.watermark(iconToolController.getWatermarkPositionChoiceBox().getValue(),
							ImageIO.read(new File(watermarkPathText)),
							(float) iconToolController.getWatermarkOpacitySlider().getValue());
				} catch (Exception e) {
					throw new Exception("水印图片加载异常！");
				}
			}
		}
		BufferedImage bufferedImage = builderFile.asBufferedImage();
		if (iconToolController.getIsCornerCheckBox().isSelected()) {
			ImgToolUtil imgToolUtil = new ImgToolUtil(bufferedImage);
			imgToolUtil.corner((float) iconToolController.getCornerSizeSlider().getValue());
			bufferedImage = imgToolUtil.getImage();
		}
		return bufferedImage;
	}

	/**
	 * @Title: buildIconTargetImageAction
	 * @Description: 生成预览图片
	 */
	public void buildIconTargetImageAction() throws Exception {
		String iconFilePathText = iconToolController.getIconFilePathTextField().getText();
		if (StringUtils.isEmpty(iconFilePathText)) {
			TooltipUtil.showToast("图标未选择！");
			throw new Exception("图标未选择！");
		}
		File iconFilePathFile = new File(iconFilePathText);
		if (!iconFilePathFile.exists()) {
			throw new Exception("图标加载失败！");
		}
		BufferedImage bufferedImage = getBufferedImage(iconFilePathFile, 150, 150);
		Image image = SwingFXUtils.toFXImage(bufferedImage, null);
		iconToolController.getIconTargetImageView().setImage(image);
	}

	public IconToolService(IconToolController iconToolController) {
		super();
		this.iconToolController = iconToolController;
	}
}
