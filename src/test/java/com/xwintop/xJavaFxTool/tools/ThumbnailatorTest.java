//package com.xwintop.xJavaFxTool.tools;
//
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//
//import javax.imageio.ImageIO;
//
//import org.junit.Test;
//
//import net.coobird.thumbnailator.Thumbnails;
//import net.coobird.thumbnailator.geometry.Positions;
//
///**
// * @ClassName: ThumbnailatorTest
// * @Description: google图片处理框架
// * @author: xufeng
// * @date: 2017年8月24日 上午10:40:16
// */
//public class ThumbnailatorTest {
//	/*
//	 * 若图片横比200小，高比300小，不变 ；若图片横比200小，高比300大，高缩小到300，图片比例不变
//	 * 若图片横比200大，高比300小，横缩小到200，图片比例不变 ；若图片横比200大，高比300大，图片按比例缩小，横为200或高为300
//	 */
//	@Test
//	public void testsize() throws IOException {
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").size(200, 300).toFile("c:/a380_200x300.jpg");
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").size(2560, 2048).toFile("c:/a380_2560x2048.jpg");
//	}
//
//	@Test // scale(比例)
//	public void testscale() throws IOException {
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").scale(0.25f).toFile("c:/a380_25%.jpg");
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").scale(1.10f).toFile("c:/a380_110%.jpg");
//	}
//
//	@Test // keepAspectRatio(false)默认是按照比例缩放的
//	public void testkeepAspectRatio() throws IOException {
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").size(200, 200).keepAspectRatio(false)
//				.toFile("c:/a380_200x200.jpg");
//	}
//
//	@Test // 压缩至指定图片尺寸（例如：横400高300），不保持图片比例
//	public void testforceSize() throws IOException {
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").forceSize(200, 200).toFile("c:/a380_200x200.jpg");
//	}
//
//	@Test // outputQuality：输出的图片质量，范围：0.0~1.0，1为最高质量
//	public void testoutputQuality() throws IOException {
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").scale(1f).outputQuality(0.25f).outputFormat("jpg")
//				.toFile("c:/a380_200x200.jpg");
//	}
//
//	@Test // rotate(角度),正数：顺时针负数：逆时针
//	public void testrotate() throws IOException {
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").size(1280, 1024).rotate(90).toFile("c:/a380_rotate+90.jpg");
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").size(1280, 1024).rotate(-90).toFile("c:/a380_rotate-90.jpg");
//	}
//
//	@Test // watermark(位置，水印图，透明度)
//	public void testwatermark() throws Exception {
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").size(1280, 1024)
//				.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File("E:/testImage1/watermark.png")), 0.5f)
//				.outputQuality(0.8f).toFile("c:/a380_watermark_bottom_right.jpg");
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").size(1280, 1024)
//				.watermark(Positions.CENTER, ImageIO.read(new File("E:/testImage1/watermark.png")), 0.5f)
//				.outputQuality(0.8f).toFile("c:/a380_watermark_center.jpg");
//	}
//
//	@Test
//	public void testsourceRegion() throws IOException {
//		// 图片中心400*400的区域
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").sourceRegion(Positions.CENTER, 400, 400).size(200, 200)
//				.keepAspectRatio(false).toFile("c:/a380_region_center.jpg");
//		// 图片右下400*400的区域
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").sourceRegion(Positions.BOTTOM_RIGHT, 400, 400).size(200, 200)
//				.keepAspectRatio(false).toFile("c:/a380_region_bootom_right.jpg");
//		// 指定坐标
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").sourceRegion(600, 500, 400, 400).size(200, 200)
//				.keepAspectRatio(false).toFile("c:/a380_region_coord.jpg");
//	}
//
//	@Test // 图像格式
//	public void testoutputFormat() throws IOException {
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").size(1280, 1024).outputFormat("png")
//				.toFile("c:/a380_1280x1024.png");
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").size(1280, 1024).outputFormat("gif")
//				.toFile("c:/a380_1280x1024.gif");
//	}
//
//	@Test // toOutputStream(流对象)
//	public void testOutputStream() throws Exception {
//		OutputStream os = new FileOutputStream("c:/a380_1280x1024_OutputStream.png");
//		Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").size(1280, 1024).toOutputStream(os);
//	}
//
//	@Test // 输出到BufferedImage
//	public void testBufferedImage() throws IOException {
//		BufferedImage thumbnail = Thumbnails.of("E:/testImage1/a380_1280x1024.jpg").size(1280, 1024).asBufferedImage();
//		ImageIO.write(thumbnail, "jpg", new File("c:/a380_1280x1024_BufferedImage.jpg"));
//	}
//}
