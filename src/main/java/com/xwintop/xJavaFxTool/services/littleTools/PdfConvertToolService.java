package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.PdfConvertToolController;
import com.xwintop.xJavaFxTool.utils.ImgToolUtil;
import com.xwintop.xcore.util.FileUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.imageio.ImageIO;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: PdfConvertToolService
 * @Description: Pdf转换工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:35
 */

@Getter
@Setter
@Slf4j
public class PdfConvertToolService {
	private PdfConvertToolController pdfConvertToolController;

	public PdfConvertToolService(PdfConvertToolController pdfConvertToolController) {
		this.pdfConvertToolController = pdfConvertToolController;
	}

	public void fileOriginalPathChange(){
		try {
			File pdfFile = new File(pdfConvertToolController.getFileOriginalPathTextField().getText());
			PDDocument document = PDDocument.load(pdfFile);
			int pageCount = document.getNumberOfPages();
			pdfConvertToolController.getPdfVersionLabel().setText(""+document.getVersion());
			pdfConvertToolController.getPageCountLabel().setText(""+pageCount);
			pdfConvertToolController.getChoosePageRangeSlider().setMax(pageCount-1);
			pdfConvertToolController.getChoosePageRangeSlider().setHighValue(pageCount-1);
		} catch (Exception e) {
			log.error(e.getMessage());
			TooltipUtil.showToast("pdf读取错误：" + e.getMessage());
		}
	}

	public void pdfToImageAction() {
		try {
			File pdfFile = new File(pdfConvertToolController.getFileOriginalPathTextField().getText());
			String fileTargetPath = pdfConvertToolController.getFileTargetPathTextField().getText();
			if(StringUtils.isEmpty(fileTargetPath)){
				fileTargetPath = pdfFile.getParent();
			}
			PDDocument document= PDDocument.load(pdfFile);
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			int startPage = (int)pdfConvertToolController.getChoosePageRangeSlider().getLowValue();
			int endPage = (int)pdfConvertToolController.getChoosePageRangeSlider().getHighValue();
			String imageType = pdfConvertToolController.getImageTypeChoiceBox().getValue();
			int dpi = 1024;
			try {
				dpi = Integer.parseInt(pdfConvertToolController.getImageDpiComboBox().getValue());
			}catch (Exception e){
				log.error("分辨率转换错误："+e.getMessage());
			}
			for(int pageCounter = 0;pageCounter < document.getNumberOfPages();pageCounter++){
				if(pageCounter < startPage || pageCounter > endPage){
					continue;
				}
				BufferedImage bim = pdfRenderer.renderImageWithDPI(pageCounter, 300, ImageType.RGB);
//				BufferedImage bim = pdfRenderer.Image(pageCounter, dpi, ImageType.RGB);
				ImgToolUtil imgToolUtil = new ImgToolUtil(bim);
				imgToolUtil.resize(dpi,dpi*bim.getHeight()/bim.getWidth());
				ImageIO.write(imgToolUtil.getImage(), imageType, new File(fileTargetPath, FileUtil.getFileName(pdfFile)+pageCounter + "."+imageType));
			}
//			int pageCounter = 0;
//			for (PDPage page : document.getPages()) {
//				// note that the page number parameter is zero based
//				BufferedImage bim = pdfRenderer.renderImageWithDPI(pageCounter, 300, ImageType.RGB);
//				// suffix in filename will be used as the file format
//				// ImageIOUtil.writeImage(bim, pdfFilename + "-" +
//				// (pageCounter++) + ".png", 300);
//				ImageIO.write(bim, "png", new File(pdfConvertToolController.getFileTargetPathTextField().getText(), pdfFile.getName()+(pageCounter++) + ".png"));
//			}
			document.close();
			TooltipUtil.showToast("pdf转换成image成功，保存在：" + fileTargetPath);
		} catch (Exception e) {
			log.error(e.getMessage());
			TooltipUtil.showToast("pdf转换错误：" + e.getMessage());
		}
	}

	public void pdfToTxtAction() {
		PDDocument document= null;
		try {
			File pdfFile = new File(pdfConvertToolController.getFileOriginalPathTextField().getText());
			String fileTargetPath = pdfConvertToolController.getFileTargetPathTextField().getText();
			if(StringUtils.isEmpty(fileTargetPath)){
				fileTargetPath = pdfFile.getParent();
			}
			document= PDDocument.load(pdfFile);
			int startPage = (int)pdfConvertToolController.getChoosePageRangeSlider().getLowValue()+1;
			int endPage = (int)pdfConvertToolController.getChoosePageRangeSlider().getHighValue()+1;
			
			File textFile = new File(fileTargetPath, FileUtil.getFileName(pdfFile) + ".txt");
	        Writer output = null;// 文件输入流，生成文本文件
	        // 文件输入流，写入文件倒textFile  
            output = new OutputStreamWriter(new FileOutputStream(textFile),"UTF-8");  
            // PDFTextStrippe来提取文本  
            PDFTextStripper stripper = new PDFTextStripper();  
            // 设置是否排序  
            stripper.setSortByPosition(true);  
            // 设置起始页  
            stripper.setStartPage(startPage);  
            // 设置结束页  
            stripper.setEndPage(endPage);  
            // 调用PDFTextStripper的writeText提取并输出文本  
            stripper.writeText(document, output);
            output.close();
            TooltipUtil.showToast("pdf转换成txt成功，保存在：" + textFile.getAbsolutePath());
		} catch (Exception e) {
			log.error(e.getMessage());
			TooltipUtil.showToast("pdf转换错误：" + e.getMessage());
		}finally {
			try {
				document.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void pdfToWordAction() {
		TooltipUtil.showToast("该功能暂未开发，欢迎加入，谢谢。");
	}

	public void wordToPdfAction() {
		TooltipUtil.showToast("该功能暂未开发，欢迎加入，谢谢。");
	}

}
