package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.PdfConvertToolController;
import com.xwintop.xJavaFxTool.utils.ImgToolUtil;
import com.xwintop.xcore.util.FileUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
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
			PDDocument document;
			document = PDDocument.load(pdfFile);
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
		} catch (Exception e) {
			log.error(e.getMessage());
			TooltipUtil.showToast("pdf转换错误：" + e.getMessage());
		}
	}

	public void pdfToTxtAction() {
	}

	public void pdfToWordAction() {
	}

	public void wordToPdfAction() {
	}

}
