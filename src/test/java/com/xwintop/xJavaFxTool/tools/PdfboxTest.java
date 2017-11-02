package com.xwintop.xJavaFxTool.tools;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class PdfboxTest {
    // 待解析PDF
    private File pdfFile = new File("C:\\Users\\Administrator\\Desktop\\倪明静\\pdf文档\\图森A5空气净化器.pdf");
    @Test
    public void testmain() throws Exception {
        PDDocument document = null;
            // 方式一：
            /**
             InputStream input = null;
             input = new FileInputStream( pdfFile );
             //加载 pdf 文档
             PDFParser parser = new PDFParser(new RandomAccessBuffer(input));
             parser.parse();
             document = parser.getPDDocument();
             **/
            // 方式二：
            document=PDDocument.load(pdfFile);
            // 获取页码
            int pages = document.getNumberOfPages();
            // 读文本内容
            PDFTextStripper stripper=new PDFTextStripper();
            // 设置按顺序输出
            stripper.setSortByPosition(true);
            stripper.setStartPage(1);
            stripper.setEndPage(pages);
            String content = stripper.getText(document);
            System.out.println(content);
    }

    @Test
    public void readImage() throws Exception {
        // 空白PDF
        File pdfFile_out = new File("D:\\TestXf\\1.pdf");

        PDDocument document = null;
        PDDocument document_out = null;
        try {
            document = PDDocument.load(pdfFile);
//            document_out = PDDocument.load(pdfFile_out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int pages_size = document.getNumberOfPages();

        System.out.println("getAllPages==============="+pages_size);
        int j=0;

        for(int i=0;i<pages_size;i++) {
            PDPage page = document.getPage(i);
            PDPage page1 = document_out.getPage(0);
            PDResources resources = page.getResources();
            Iterable xobjects = resources.getXObjectNames();

            if (xobjects != null) {
                Iterator imageIter = xobjects.iterator();
                while (imageIter.hasNext()) {
                    COSName key = (COSName) imageIter.next();
                    if(resources.isImageXObject(key)){
                            PDImageXObject image = (PDImageXObject) resources.getXObject(key);

                            // 方式一：将PDF文档中的图片 分别存到一个空白PDF中。
//                            PDPageContentStream contentStream = new PDPageContentStream(document_out,page1, PDPageContentStream.AppendMode.APPEND,true);
//
//                            float scale = 1f;
//                            contentStream.drawImage(image, 20,20,image.getWidth()*scale,image.getHeight()*scale);
//                            contentStream.close();
//                            document_out.save("/Users/xiaolong/Downloads/123"+j+".pdf");
//                            System.out.println(image.getSuffix() + ","+image.getHeight() +"," + image.getWidth());

                             // 方式二：将PDF文档中的图片 分别另存为图片。
                             File file = new File("D:\\TestXf/123"+j+".png");
                             FileOutputStream out = new FileOutputStream(file);
                             InputStream input = image.createInputStream();
                             int byteCount = 0;
                             byte[] bytes = new byte[1024];
                             while ((byteCount = input.read(bytes)) > 0)
                             {
                             out.write(bytes,0,byteCount);
                             }
                             out.close();
                             input.close();
                        //image count
                        j++;
                    }
                }
            }
        }
        System.out.println(j);
    }
}
