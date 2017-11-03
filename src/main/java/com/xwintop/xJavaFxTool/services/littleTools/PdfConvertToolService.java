package com.xwintop.xJavaFxTool.services.littleTools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.xwintop.xJavaFxTool.controller.littleTools.PdfConvertToolController;
import com.xwintop.xcore.util.javafx.TooltipUtil;

import javafx.event.ActionEvent;
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

	public void pdfToImageAction() {
		try {
			File pdfFile = new File(pdfConvertToolController.getFileOriginalPathTextField().getText());
			PDDocument document;
			document = PDDocument.load(pdfFile);
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			int pageCounter = 0;
			for (PDPage page : document.getPages()) {
				// note that the page number parameter is zero based
				BufferedImage bim = pdfRenderer.renderImageWithDPI(pageCounter, 300, ImageType.RGB);
				// suffix in filename will be used as the file format
				// ImageIOUtil.writeImage(bim, pdfFilename + "-" +
				// (pageCounter++) + ".png", 300);
				ImageIO.write(bim, "png", new File(pdfConvertToolController.getFileTargetPathTextField().getText(), pdfFile.getName()+(pageCounter++) + ".png"));
			}
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
