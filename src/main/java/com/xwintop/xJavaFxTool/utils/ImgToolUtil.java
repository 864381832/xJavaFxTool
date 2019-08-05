package com.xwintop.xJavaFxTool.utils;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.URL;

/**
 * 图片处理
 * <pre>
 * new Img("pic/1.jpg").thumbnail(100,100).save("pic/1_thumb.jpg");
 * new Img("pic/1.jpg").resize(100,100).save("pic/1_scale.jpg");
 * new Img("pic/1.jpg").corner(1.0f).save("pic/1_corner.jpg");//0~1.0f，1.0相当于变成圆形
 * new Img("pic/1.jpg").square().save("pic/1_square.jpg");
 * new Img("pic/1.jpg").rotate(45).save("pic/1_rotate.jpg");
 * new Img("pic/1.jpg").draw("pic/logo.jpg").save("pic/1_logo.jpg");
 * new Img("img/2.jpg").corner(1.0f).thumbnail(50, 50).save("img/2_50.jpg").corner().save("img/2_50_round.jpg");
 * </pre>
 *
 * @author zhangheng
 */
@Setter
@Getter
public class ImgToolUtil {

    protected BufferedImage image;
    protected String type;
    protected boolean alphaed;

    protected ImgToolUtil() {

    }

    protected static InputStream open(String path) throws IOException {
        if (path == null) {
            throw new IOException("path is null");
        }
        return isUrlPath(path) ? new URL(path).openStream() : new FileInputStream(path);
    }

    public ImgToolUtil(BufferedImage image) throws IOException {
        this.image = image;
        this.alphaed = image.getTransparency() == Transparency.TRANSLUCENT;
    }

    public ImgToolUtil(String path) throws IOException {
        this(open(path));
    }

    public ImgToolUtil(InputStream path) throws IOException {
        this(path, null);
    }

    public ImgToolUtil(InputStream path, String type) throws IOException {
        if (path == null) {
            throw new IOException("path is null");
        }
        image = ImageIO.read(path);
        path.close();
        this.type = type;
        this.alphaed = image.getTransparency() == Transparency.TRANSLUCENT;
    }

    public int width() {
        return image.getWidth();
    }

    public int height() {
        return image.getHeight();
    }

    /**
     * 反转
     *
     * @param isHorizontal
     * @return
     */
    public ImgToolUtil flip(boolean isHorizontal) {
        image = getTransformOp(isHorizontal).filter(image, null);
        alphaed = true;
        return this;
    }

    protected AffineTransformOp getTransformOp(boolean isHorizontal) {
        AffineTransform transform;
        if (isHorizontal) {
            transform = new AffineTransform(-1, 0, 0, 1, width(), 0);// 水平翻转 
        } else {
            transform = new AffineTransform(1, 0, 0, -1, 0, height());// 垂直翻转 
        }
        //new AffineTransform(-1, 0, 0, -1, image.getWidth(), image.getHeight());// 旋转180度  
        return new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
    }

    /**
     * 拉伸到目标尺寸
     *
     * @param size
     * @return
     */
    public ImgToolUtil resize(int size) {
        return this.resize(size, size);
    }

    /**
     * 拉伸到目标大小
     *
     * @param width
     * @param height
     * @return
     */
    public ImgToolUtil resize(int width, int height) {
        image = resizeImp(image, width, height, false);
        return this;
    }

    /**
     * 缩略图到目标尺寸
     */
    public ImgToolUtil thumbnail(int size) {
        return this.thumbnail(size, size);
    }

    /**
     * 缩略图到目标大小
     *
     * @param width
     * @param height
     * @return
     */
    public ImgToolUtil thumbnail(int width, int height) {
        image = resizeImp(image, width, height, true);
        return this;
    }

    /**
     * 将图片变成方形（默认以最小边为准）
     */
    public ImgToolUtil square() {
        return this.square(true);
    }

    /**
     * 将图片变为方形
     *
     * @param small
     * @return
     */
    public ImgToolUtil square(boolean small) {
        int w = width();
        int h = height();
        if (w != h) {
            int s = small ? Math.min(w, h) : Math.max(w, h);
            resize(s, s);
        }
        return this;
    }

    /**
     * 图片处理成圆角
     *
     * @param radius 圆角尺寸,比如填10表示10px
     * @return
     */
    public ImgToolUtil corner(int radius) {
        image = cornerImp(image, radius);
        alphaed = true;
        return this;
    }

    /**
     * 图片处理成圆角
     *
     * @return
     */
    public ImgToolUtil corner() {
        return corner(Math.min(width(), height()));
    }

    /**
     * 图片处理成圆角
     *
     * @param percent 圆角比例
     * @return
     */
    public ImgToolUtil corner(float percent) {
        if (percent > 360) {
            percent = percent % 360;
        }
        if (percent > 1) {
            percent = percent / 360;
        }
        float n = Math.min(width(), height()) * percent;
        return corner((int) n);
    }

    /**
     * 图片剪切
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public ImgToolUtil cut(int x, int y, int width, int height) {
        image = image.getSubimage(x, y, width, height);
        return this;
    }

    public ImgToolUtil save(String path) throws IOException {
        String format = type == null ? "jpg" : type;
        return save(path, format);
    }

    public ImgToolUtil save(String path, String format) throws IOException {
        if (alphaed) {
            format = "png";
        }
        ImageIO.write(image, format, new File(path));
        return this;
    }

    public ImgToolUtil save(OutputStream path) throws IOException {
        String format = type == null ? "jpg" : type;
        return save(path, format);
    }

    public ImgToolUtil save(OutputStream path, String format) throws IOException {
        if (alphaed) {
            format = "png";
        }
        ImageIO.write(image, format, path);
        return this;
    }

    /**
     * 对图片进行旋转
     *
     * @param degree 0-360
     * @return
     */
    public ImgToolUtil rotate(double degree) {
        image = rotateImp(image, degree);
        alphaed = true;
        return this;
    }

    private static double[] calculatePosition(double x, double y, double angle) {
        double nx = (Math.cos(angle) * x) - (Math.sin(angle) * y);
        double ny = (Math.sin(angle) * x) + (Math.cos(angle) * y);
        return new double[]{nx, ny};
    }

    protected static BufferedImage rotateImp(BufferedImage image, double angle) {
        int width = image.getWidth();
        int height = image.getHeight();
        angle = Math.toRadians(angle);

        double[][] postions = new double[4][];
        postions[0] = calculatePosition(0, 0, angle);
        postions[1] = calculatePosition(width, 0, angle);
        postions[2] = calculatePosition(0, height, angle);
        postions[3] = calculatePosition(width, height, angle);
        double minX = Math.min(
                Math.min(postions[0][0], postions[1][0]),
                Math.min(postions[2][0], postions[3][0])
        );
        double maxX = Math.max(
                Math.max(postions[0][0], postions[1][0]),
                Math.max(postions[2][0], postions[3][0])
        );
        double minY = Math.min(
                Math.min(postions[0][1], postions[1][1]),
                Math.min(postions[2][1], postions[3][1])
        );
        double maxY = Math.max(
                Math.max(postions[0][1], postions[1][1]),
                Math.max(postions[2][1], postions[3][1])
        );
        int newWidth = (int) Math.ceil(maxX - minX);
        int newHeight = (int) Math.ceil(maxY - minY);
        BufferedImage ret = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = ret.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.rotate(angle, newWidth / 2, newHeight / 2);
        int centerX = (int) Math.round((newWidth - width) / 2.0);
        int centerY = (int) Math.round((newHeight - height) / 2.0);
        g.drawImage(image, centerX, centerY, null);
        g.dispose();
        return ret;
    }

    public ImgToolUtil draw(BufferedImage img, Align align) {
        return this.draw(img, align, 0.0d);
    }

    public ImgToolUtil draw(BufferedImage img) {
        return this.draw(img, Align.BOTTOM_RIGHT, 0);
    }

    public ImgToolUtil draw(BufferedImage img, Align align, int offset) {
        return this.draw(img, align, 0.0d, offset, 1.0f);
    }

    public ImgToolUtil draw(BufferedImage img, Align align, Double degree) {
        return this.draw(img, align, degree, 0, 1.0f);
    }

    public ImgToolUtil draw(BufferedImage img, Align align, Double degree, int offset, float alpha) {
        return draw(img, align, degree, offset, offset, alpha);
    }

    public ImgToolUtil draw(String imgPath, Align align) throws IOException {
        return this.draw(ImageIO.read(new File(imgPath)), align);
    }

    public ImgToolUtil draw(String imgPath) throws IOException {
        return this.draw(ImageIO.read(new File(imgPath)));
    }

    public ImgToolUtil draw(String imgPath, Align align, Double degree, int offsetX, int offsetY, float alpha) throws IOException {
        return draw(ImageIO.read(new File(imgPath)), align, degree, offsetX, offsetY, alpha);
    }

    /**
     * 绘制图片水印
     *
     * @param img     图片
     * @param align   位置
     * @param degree  旋转角度
     * @param offsetX 偏移x
     * @param offsetY 偏移y
     * @param alpha   透明度
     * @return
     */
    public ImgToolUtil draw(BufferedImage img, Align align, Double degree, int offsetX, int offsetY, float alpha) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        int x;
        int y;
        if (align == Align.BOTTOM_RIGHT) {
            x = (width() - w - offsetX);
            y = (height() - h - offsetY);
        } else if (align == Align.BOTTOM_LEFT) {
            x = offsetX;
            y = (height() - h - offsetY);
        } else if (align == Align.TOP_LEFT) {
            x = offsetX;
            y = offsetY;
        } else if (align == Align.TOP_RIGHT) {
            x = (width() - w - offsetX);
            y = offsetY;
        } else {
            x = (width() - w - offsetX) / 2;
            y = (height() - h - offsetY) / 2;
        }
        if (degree != 0) {
            img = rotateImp(img, degree);
        }
        drawApply(img, x, y, alpha);
        return this;
    }

    protected void drawApply(Image img, int x, int y, float alpha) {
        image = drawIcon(image, img, x, y, alpha);
    }

    public static Image icon(String path) {
        return new ImageIcon(path).getImage();
    }

    protected static BufferedImage drawIcon(BufferedImage image, Image icon, int x, int y, Float alpha) {
        int w = image.getWidth();
        int h = image.getHeight();
        boolean hasAlpha = image.getColorModel().hasAlpha();

        BufferedImage ret = new BufferedImage(w, h, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = ret.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha == null ? 1.0f : alpha));
        g2.drawImage(icon, x, y, null);
//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));   
        g2.dispose();
        return ret;
    }

    /**
     * 制作圆角图片（适合圆角头像啥的）
     *
     * @param image
     * @param radius 圆角尺寸
     * @return
     */
    protected static BufferedImage cornerImp(BufferedImage image, int radius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return output;
    }

    /**
     * 生成缩略图
     *
     * @param source
     * @param width     目标宽度
     * @param height    目标高度
     * @param ratioKeep 是否等比缩放
     * @return
     */
    protected static BufferedImage resizeImp(BufferedImage source, int width, int height, boolean ratioKeep) {
        BufferedImage target = null;
        double sx = (double) width / source.getWidth();
        double sy = (double) height / source.getHeight();
        if (ratioKeep) {
            if (sx > 1 && sy > 1) {
                return source;
            }
            if (sx < sy) {
                sy = sx;
                height = (int) (sy * source.getHeight());
            } else {
                sx = sy;
                width = (int) (sx * source.getWidth());
            }
        }
        if (source.getType() == BufferedImage.TYPE_CUSTOM) {
            ColorModel cm = source.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            target = new BufferedImage(cm, raster, alphaPremultiplied, null);
        } else {
            target = new BufferedImage(width, height, source.getType());
        }
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;
    }

    private static boolean isUrlPath(String path) {
//        return path.startsWith("http:")||path.startsWith("https:");
        return path.indexOf("://") > 0;
    }

    /**
     * 图标绘制位置
     */
    public static enum Align {
        CENTER,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        TOP_RIGHT
    }

    /**
     * 根据传入类型变化图片像素
     */
    public static javafx.scene.image.Image pixWithImage(int type, javafx.scene.image.Image image) {
        PixelReader pixelReader = image.getPixelReader();
        if (image.getWidth() > 0 && image.getHeight() > 0) {
            WritableImage wImage;
            wImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
            PixelWriter pixelWriter = wImage.getPixelWriter();

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    javafx.scene.paint.Color color = pixelReader.getColor(x, y);
                    switch (type) {
                        case 1:
                            // 颜色变轻
                            color = color.brighter();
                            break;
                        case 2:
                            // 颜色变深
                            color = color.darker();
                            break;
                        case 3:
                            // 灰度化
                            color = color.grayscale();
                            break;
                        case 4:
                            // 颜色反转
                            color = color.invert();
                            break;
                        case 5:
                            // 颜色饱和
                            color = color.saturate();
                            break;
                        case 6:
                            // 颜色不饱和
                            color = color.desaturate();
                            break;
                        case 7:
                            // 颜色灰度化后反转（字黑体，背景鲜亮，可用于强字弱景）
                            color = color.grayscale();
                            color = color.invert();
                            break;
                        case 8:
                            // 颜色透明
                            color = new javafx.scene.paint.Color(color.getRed(), color.getGreen(), color.getBlue(), 0.5);
                            break;
                        default:
                            break;
                    }

                    pixelWriter.setColor(x, y, color);
                }
            }
            return wImage;
        }
        return null;
    }
}
