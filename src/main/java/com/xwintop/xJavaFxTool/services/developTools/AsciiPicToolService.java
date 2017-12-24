package com.xwintop.xJavaFxTool.services.developTools;

import com.xwintop.xJavaFxTool.controller.developTools.AsciiPicToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    public void buildAction() {
        String path = asciiPicToolController.getFilePathTextField().getText();
        final String base = "@#&$%*o!;.";// 字符串由复杂到简单
        try {
            StringBuilder stringBuffer = new StringBuilder();
            BufferedImage image = ImageIO.read(new File(path));
            if(!asciiPicToolController.getImageSizeComboBox().getValue().equals("不压缩")){
                String[] size = asciiPicToolController.getImageSizeComboBox().getValue().split("\\*");
                image = Thumbnails.of(image).size(Integer.parseInt(size[0]),Integer.parseInt(size[1])).asBufferedImage();
            }
            for (int y = 0; y < image.getHeight(); y += 2) {
                for (int x = 0; x < image.getWidth(); x++) {
                    final int pixel = image.getRGB(x, y);
                    final int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = pixel & 0xff;
                    final float gray = 0.299f * r + 0.578f * g + 0.114f * b;
                    final int index = Math.round(gray * (base.length() + 1) / 255);
//                    System.out.print(index >= base.length() ? " " : String.valueOf(base.charAt(index)));
                    stringBuffer.append(index >= base.length() ? " " : String.valueOf(base.charAt(index)));
                }
//                System.out.println();
                stringBuffer.append("\n");
            }
            asciiPicToolController.getAsciiTextArea().setText(stringBuffer.toString());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}