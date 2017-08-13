package com.xwintop.xJavaFxTool.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * 二维码工具类
 */
/**
 * @ClassName: QRCodeUtil
 * @Description: TODO
 * @author: Administrator
 * @date: 2017年8月12日 下午2:21:08
 */
public class QRCodeUtil {
	private static final int width = 300;// 默认二维码宽度
	private static final int height = 300;// 默认二维码高度
	private static final String format = "png";// 默认二维码文件格式
	private static final Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();// 二维码参数
	private static final Map<DecodeHintType, Object> dhints = new HashMap<DecodeHintType, Object>();// 识别二维码参数

	static {
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");// 字符编码
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);// 容错等级 L、M、Q、H 其中 L 为最低, H 为最高
		hints.put(EncodeHintType.MARGIN, 2);// 二维码边界空白大小 1,2,3,4 (4为默认,最大)

		dhints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
	}

	/**
	 * 返回一个 BufferedImage 对象
	 * 
	 * @param content
	 *            二维码内容
	 * @param width
	 *            宽
	 * @param height
	 *            高
	 */
	public static BufferedImage toBufferedImage(String content, int width, int height)
			throws WriterException, IOException {
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		return MatrixToImageWriter.toBufferedImage(bitMatrix);
	}

	public static BufferedImage toBufferedImage(String content) throws WriterException, IOException {
		return toBufferedImage(content,width, height);
	}

	/**
	 * @Title: toImage
	 * @Description: 转换为javaFxImage二维码图片
	 */
	public static Image toImage(String content, int width, int height) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			writeToStream(content, stream, width, height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Image image = new Image(new ByteArrayInputStream(stream.toByteArray()));
		return image;
	}
	
	public static Image toImage(String content, int width, int height,String format,ErrorCorrectionLevel errorCorrectionLevel,Integer margin,Color onColor,Color offColor,String formatImage) {
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();// 二维码参数
		hints.put(EncodeHintType.CHARACTER_SET, format);// 字符编码
		hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);// 容错等级 L、M、Q、H 其中 L 为最低, H 为最高
		hints.put(EncodeHintType.MARGIN, margin);// 二维码边界空白大小 1,2,3,4 (4为默认,最大)
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			java.awt.Color onColorw = new java.awt.Color((float)onColor.getRed(), (float)onColor.getGreen(), (float)onColor.getBlue(), (float)onColor.getOpacity());
			java.awt.Color offColorw = new java.awt.Color((float)offColor.getRed(), (float)offColor.getGreen(), (float)offColor.getBlue(), (float)offColor.getOpacity());
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			MatrixToImageConfig config = new MatrixToImageConfig(onColorw.getRGB(), offColorw.getRGB());
			MatrixToImageWriter.writeToStream(bitMatrix, formatImage, stream,config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Image image = new Image(new ByteArrayInputStream(stream.toByteArray()));
		return image;
	}

	/**
	 * 从Image中解析二维码
	 */
	public static String toDecode(Image image) {
		try {
			BufferedImage bimage = SwingFXUtils.fromFXImage(image, null);
			LuminanceSource source = new BufferedImageLuminanceSource(bimage);
			Binarizer binarizer = new HybridBinarizer(source);
			BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
			Result result = new MultiFormatReader().decode(binaryBitmap, dhints);// 对图像进行解码
			return result.getText();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Title: toDecode
	 * @Description: 从文件总解析二维码
	 */
	public static String toDecode(File file) {
		try {
			BufferedImage image = ImageIO.read(file);
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			Binarizer binarizer = new HybridBinarizer(source);
			BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
			Result result = new MultiFormatReader().decode(binaryBitmap, dhints);// 对图像进行解码
			return result.getText();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将二维码图片输出到一个流中
	 * 
	 * @param content
	 *            二维码内容
	 * @param stream
	 *            输出流
	 * @param width
	 *            宽
	 * @param height
	 *            高
	 */
	public static void writeToStream(String content, OutputStream stream, int width, int height)
			throws WriterException, IOException {
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
//		MatrixToImageConfig config = new MatrixToImageConfig(java.awt.Color.GREEN.getRGB(), java.awt.Color.RED.getRGB());
//		MatrixToImageWriter.writeToStream(bitMatrix, format, stream,config);
		MatrixToImageWriter.writeToStream(bitMatrix, format, stream);
	}

	/**
	 * 生成二维码图片文件
	 * 
	 * @param content
	 *            二维码内容
	 * @param path
	 *            文件保存路径
	 * @param width
	 *            宽
	 * @param height
	 *            高
	 */
	public static void createQRCode(String content, String path, int width, int height)
			throws WriterException, IOException {
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		// toPath() 方法由 jdk1.7 及以上提供
		MatrixToImageWriter.writeToPath(bitMatrix, format, new File(path).toPath());
	}
}
