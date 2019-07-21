package com.xwintop.xJavaFxTool.services.developTools;

import com.xwintop.xJavaFxTool.controller.developTools.AsciiPicToolController;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.ImageUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @ClassName: AsciiPicToolService
 * @Description: 图片转ascii
 * @author: xufeng
 * @date: 2017/12/24 0024 23:18
 */

@Getter
@Setter
@Slf4j
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
            if (!"不压缩".equals(asciiPicToolController.getImageSizeComboBox().getValue())) {
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
                ImageUtil.writeImage(asciiPicToolController.getImageImageView().getImage(), file);
                TooltipUtil.showToast("保存图片成功,图片在：" + file.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            TooltipUtil.showToast(e.getMessage());
        }
    }


    /**
     * 转换图片为Excel表
     */
    public void saveImageToExcelAction() throws Exception {
        TooltipUtil.showToast("正在转换，请稍后......");
        String path = asciiPicToolController.getFilePathTextField().getText();
        File imagePath = new File(path);
        BufferedImage bi = ImageUtil.getBufferedImage(imagePath);
        int width = bi.getWidth();
        int height = bi.getHeight();

        // 读取占位的文件，设置列宽
        XSSFWorkbook excel = new XSSFWorkbook();
        XSSFSheet sht = excel.createSheet();

        ExecutorService executor = new ThreadPoolExecutor(height, height * width, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        CountDownLatch latch = new CountDownLatch(height * width);
        for (int i = 0; i < height; i++) {
            sht.setColumnWidth(i, (short) 500);
            Row row = sht.createRow(i);
            row.setHeight((short) 250);
            int finalI = i;
            for (int j = 0; j < width; j++) {
                int finalJ = j;
                executor.execute(() -> {
                    XSSFCellStyle style = excel.createCellStyle();
                    style.setFillForegroundColor(new XSSFColor(new Color(bi.getRGB(finalJ, finalI))));
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    row.createCell(finalJ).setCellStyle(style);
                    log.info("完成" + finalJ + "列：" + finalI);
                    latch.countDown();
                });
            }
        }
        latch.await();
        File outFile = new File(imagePath.getParent(), imagePath.getName().split("\\.")[0] + ".xlsx");
        FileOutputStream out = new FileOutputStream(outFile);
        excel.write(out);
        excel.close();
        out.close();
        log.info("转换完成，文件保存在：" + outFile.getAbsolutePath());
        TooltipUtil.showToast("转换完成，文件保存在：" + outFile.getAbsolutePath());
    }
}