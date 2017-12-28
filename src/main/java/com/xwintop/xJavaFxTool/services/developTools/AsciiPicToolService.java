package com.xwintop.xJavaFxTool.services.developTools;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.xwintop.xJavaFxTool.controller.developTools.AsciiPicToolController;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.ImageUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName: AsciiPicToolService
 * @Description: 图片转ascii
 * @author: xufeng
 * @date: 2017/12/24 0024 23:18
 */

@Getter
@Setter
@Log4j
public class AsciiPicToolService {
    private AsciiPicToolController asciiPicToolController;

    public AsciiPicToolService(AsciiPicToolController asciiPicToolController) {
        this.asciiPicToolController = asciiPicToolController;
    }

    public void buildBannerAction() {
        String path = asciiPicToolController.getFilePathTextField().getText();
        final String base = "@#&$%*o!;.";// 字符串由复杂到简单
        try {
            StringBuilder stringBuffer = new StringBuilder();
            BufferedImage image = ImageUtil.getBufferedImage(path);
            if (!asciiPicToolController.getImageSizeComboBox().getValue().equals("不压缩")) {
                String[] size = asciiPicToolController.getImageSizeComboBox().getValue().split("\\*");
                image = Thumbnails.of(image).size(Integer.parseInt(size[0]), Integer.parseInt(size[1])).asBufferedImage();
            }
            for (int y = 0; y < image.getHeight(); y += 2) {
                for (int x = 0; x < image.getWidth(); x++) {
                    final int pixel = image.getRGB(x, y);
                    final int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = pixel & 0xff;
                    final float gray = 0.299f * r + 0.578f * g + 0.114f * b;
                    final int index = Math.round(gray * (base.length() + 1) / 255);
                    stringBuffer.append(index >= base.length() ? " " : String.valueOf(base.charAt(index)));
                }
                stringBuffer.append("\n");
            }
            asciiPicToolController.getCodeTextArea().setText(stringBuffer.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            TooltipUtil.showToast(e.getMessage());
        }
    }

    public void buildBase64Action() {
        String path = asciiPicToolController.getFilePathTextField().getText();
        try {
            String encodeBase64 = Base64.encodeBase64String(FileUtils.readFileToByteArray(new File(path)));
            asciiPicToolController.getCodeTextArea().setText(encodeBase64);
        } catch (final IOException e) {
            e.printStackTrace();
            TooltipUtil.showToast(e.getMessage());
        }
    }

    //base64编码转图片
    public void buildBase64ToImage(String base64) {
        if (Base64.isBase64(base64)) {
            try {
                byte[] base64Byte = Base64.decodeBase64(base64);
//              Image image = new Image(new ByteArrayInputStream(base64Byte));
                Image image = ImageUtil.getFXImage(base64Byte);
                asciiPicToolController.getImageImageView().setImage(image);
                asciiPicToolController.getImageImageView().setFitWidth(image.getWidth());
                asciiPicToolController.getImageImageView().setFitHeight(image.getHeight());
            } catch (Exception e) {
                e.printStackTrace();
                TooltipUtil.showToast("图片转换失败：" + e.getMessage());
            }
        }
    }

    public void saveImageAction() {
        try {
            String fileName = "x" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".jpg";
//            File file = FileChooserUtil.chooseSaveFile(fileName, new FileChooser.ExtensionFilter("All Images", "*.*"),
//                    new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("PNG", "*.png"),
//                    new FileChooser.ExtensionFilter("gif", "*.gif"), new FileChooser.ExtensionFilter("jpeg", "*.jpeg"),
//                    new FileChooser.ExtensionFilter("bmp", "*.bmp"));
            File file = FileChooserUtil.chooseSaveImageFile(fileName);
            if (file != null) {
                String[] fileType = file.getPath().split("\\.");
//                ImageIO.write(SwingFXUtils.fromFXImage(asciiPicToolController.getImageImageView().getImage(), null), fileType[fileType.length - 1],
//                        file);
                ImageUtil.writeImage(asciiPicToolController.getImageImageView().getImage(),file);
                TooltipUtil.showToast("保存图片成功,图片在：" + file.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            TooltipUtil.showToast(e.getMessage());
        }
    }
}