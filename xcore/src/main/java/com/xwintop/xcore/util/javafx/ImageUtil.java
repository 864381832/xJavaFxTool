//package com.xwintop.xcore.util.javafx;
//
//import com.xwintop.xcore.util.FileUtil;
//import javafx.embed.swing.SwingFXUtils;
//import javafx.scene.image.*;
//import javafx.scene.image.Image;
//import lombok.Getter;
//import lombok.Setter;
//import org.apache.commons.imaging.ImageFormats;
//import org.apache.commons.imaging.Imaging;
//
//import javax.imageio.ImageIO;
//import javax.swing.*;
//import javax.swing.filechooser.FileSystemView;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.IOException;
//
///**
// * @ClassName: ImageUtil
// * @Description: 图片工具类
// * @author: xufeng
// * @date: 2017/12/28 0028 22:03
// */
//@Getter
//@Setter
//public class ImageUtil {
//
//    /**
//     * 获取图片BufferedImage
//     *
//     * @param path 图片路径
//     */
//    public static BufferedImage getBufferedImage(String path) {
//        return getBufferedImage(new File(path));
//    }
//
//    public static BufferedImage getBufferedImage(File file) {
//        BufferedImage bufferedImage = null;
//        try {
//            bufferedImage = Imaging.getBufferedImage(file);
//        } catch (Exception e) {
//            try {
//                bufferedImage = ImageIO.read(file);
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//        }
//        return bufferedImage;
//    }
//
//    /**
//     * 获取javafx图片
//     *
//     * @param path 图片路径
//     */
//    public static Image getFXImage(String path) {
//        return getFXImage(new File(path));
//    }
//
//    public static Image getFXImage(File file) {
//        Image image = null;
//        try {
//            image = SwingFXUtils.toFXImage(Imaging.getBufferedImage(file), null);
//        } catch (Exception e) {
//            image = new Image("file:" + file.getAbsolutePath());
//        }
//        return image;
//    }
//
//    public static Image getFXImage(byte[] bytes) {
//        Image image = null;
//        try {
//            image = SwingFXUtils.toFXImage(Imaging.getBufferedImage(bytes), null);
//        } catch (Exception e) {
//            image = new Image(new ByteArrayInputStream(bytes));
//        }
//        return image;
//    }
//
//
//    /**
//     * 保存图片
//     *
//     * @param image
//     * @param file
//     */
//    public static void writeImage(Image image, File file) throws Exception {
//        writeImage(SwingFXUtils.fromFXImage(image, null), file);
//    }
//
//    public static void writeImage(BufferedImage bufferedImage, File file) throws Exception {
//        try {
//            Imaging.writeImage(bufferedImage, file, ImageFormats.valueOf(FileUtil.getFileSuffixName(file).toUpperCase()));
//        } catch (Exception e) {
//            e.printStackTrace();
//            ImageIO.write(bufferedImage, FileUtil.getFileSuffixName(file), file);
//        }
//    }
//
//    //获取文件夹图标
//    public static ImageView getDirectoryIconImage() {
//        return getFileIconImage(FileSystemView.getFileSystemView().getHomeDirectory());
//    }
//
//    //获取文件图标
//    public static ImageView getFileIconImage(File file) {
//        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
//        BufferedImage bufferedImage = new BufferedImage(
//            icon.getIconWidth(),
//            icon.getIconHeight(),
//            BufferedImage.TYPE_INT_ARGB
//        );
//        icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
//        Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
//        return new ImageView(fxImage);
//    }
//
//    /**
//     * 根据传入类型变化图片像素
//     */
//    public static javafx.scene.image.Image pixWithImage(int type, javafx.scene.image.Image image) {
//        PixelReader pixelReader = image.getPixelReader();
//        if (image.getWidth() > 0 && image.getHeight() > 0) {
//            WritableImage wImage;
//            wImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
//            PixelWriter pixelWriter = wImage.getPixelWriter();
//
//            for (int y = 0; y < image.getHeight(); y++) {
//                for (int x = 0; x < image.getWidth(); x++) {
//                    javafx.scene.paint.Color color = pixelReader.getColor(x, y);
//                    switch (type) {
//                        case 1:
//                            // 颜色变轻
//                            color = color.brighter();
//                            break;
//                        case 2:
//                            // 颜色变深
//                            color = color.darker();
//                            break;
//                        case 3:
//                            // 灰度化
//                            color = color.grayscale();
//                            break;
//                        case 4:
//                            // 颜色反转
//                            color = color.invert();
//                            break;
//                        case 5:
//                            // 颜色饱和
//                            color = color.saturate();
//                            break;
//                        case 6:
//                            // 颜色不饱和
//                            color = color.desaturate();
//                            break;
//                        case 7:
//                            // 颜色灰度化后反转（字黑体，背景鲜亮，可用于强字弱景）
//                            color = color.grayscale();
//                            color = color.invert();
//                            break;
//                        case 8:
//                            // 颜色透明
//                            if (color.getOpacity() == 0) {
//                                color = new javafx.scene.paint.Color(color.getRed(), color.getGreen(), color.getBlue(), 0);
//                            } else {
//                                color = new javafx.scene.paint.Color(color.getRed(), color.getGreen(), color.getBlue(), 0.5);
//                            }
//                            break;
//                        default:
//                            break;
//                    }
//
//                    pixelWriter.setColor(x, y, color);
//                }
//            }
//            return wImage;
//        }
//        return null;
//    }
//
//    //将javafx中Color转换为awt中Color
//    public static java.awt.Color getAwtColor(javafx.scene.paint.Color color) {
//        java.awt.Color colorw = new Color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getOpacity());
//        return colorw;
//    }
//}
