package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.ExcelSplitToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Getter
@Setter
@Slf4j
public class ExcelSplitToolService {
    private ExcelSplitToolController excelSplitToolController;

    public ExcelSplitToolService(ExcelSplitToolController excelSplitToolController) {
        this.excelSplitToolController = excelSplitToolController;
    }

    public void splitAction() throws Exception { //拆分表格
        String filePath = excelSplitToolController.getSelectFileTextField().getText();
        FileInputStream fileInputStream = new FileInputStream(filePath);

        Workbook workbook = null;
        if (filePath.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(fileInputStream);
        } else if (filePath.endsWith(".xls")) {
            POIFSFileSystem fileSystem = new POIFSFileSystem(fileInputStream);
            workbook = new HSSFWorkbook(fileSystem);
        } else {
            throw new RuntimeException("错误提示: 您设置的Excel文件名不合法!");
        }
        Sheet sheet = workbook.getSheetAt(0);
        int lastRowIndex = sheet.getLastRowNum();// 行数
        log.info("读取到Excel文件行数：" + lastRowIndex);
        int splitNumber = 0;
        if (excelSplitToolController.getSplitType1RadioButton().isSelected()) {
            splitNumber = lastRowIndex / (excelSplitToolController.getSplitType1Spinner().getValue() - 1);
        } else if (excelSplitToolController.getSplitType2RadioButton().isSelected()) {
            splitNumber = excelSplitToolController.getSplitType2Spinner().getValue();
        } else if (excelSplitToolController.getSplitType3RadioButton().isSelected()) {

        }
        Workbook newWorkbook = null;
        if (excelSplitToolController.getOutputType1RadioButton().isSelected()) {
            newWorkbook = new HSSFWorkbook();
        } else {
            newWorkbook = new XSSFWorkbook();
        }
        Sheet newSheet = newWorkbook.createSheet();
        int addRowIndex = 0;
        for (int i = 0; i <= lastRowIndex; i++) {
            Row row = newSheet.createRow(addRowIndex++);
            sheet.getRow(i).cellIterator().forEachRemaining(cell -> {
                setCellValue(cell, row.createCell(cell.getColumnIndex(), cell.getCellType()));
            });
            if (addRowIndex == splitNumber) {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(new File(new File(filePath).getParent(), "newExcel" + Math.random() + ".xls"));
                    newWorkbook.write(out);
                } catch (IOException e) {
                    System.out.println(e.toString());
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                        System.out.println(e.toString());
                    }
                }
                addRowIndex = 0;
                if (excelSplitToolController.getOutputType1RadioButton().isSelected()) {
                    newWorkbook = new HSSFWorkbook();
                } else {
                    newWorkbook = new XSSFWorkbook();
                }
                newSheet = newWorkbook.createSheet();
            }
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(new File(filePath).getParent(), "newExcel" + Math.random() + ".xls"));
            newWorkbook.write(out);
        } catch (IOException e) {
            System.out.println(e.toString());
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
//        for (int i = 0; i <= lastRowIndex; i++) {
//            Row row = sheet.getRow(i);
//            if (row == null) {
//                break;
//            }
//            short lastCellNum = row.getLastCellNum();// 列数
//            for (int j = 0; j < lastCellNum; j++) {
//                Cell cell = row.getCell(j);
//                String str = null;
//                switch (cell.getCellType()) {
//                    case BLANK:// 空值
//                        str = "";
//                        break;
//                    case BOOLEAN:
//                        str = String.valueOf(cell.getBooleanCellValue());// 布尔型
//                        break;
//                    case FORMULA:
//                        str = String.valueOf(cell.getCellFormula());// 公式型
//                        break;
//                    case NUMERIC:
//                        str = String.valueOf(cell.getNumericCellValue());// 数值型
//                        break;
//                    case STRING:
//                        str = String.valueOf(cell.getStringCellValue());// 字符型
//                        break;
//                    default:
//                        str = null;
//                        break;
//                }
//                System.out.print(str + "\t\t");
//            }
//            System.out.println();
//        }
    }

    public static void setCellValue(Cell cell, Cell newCell) {
        // 以下是判断数据的类型
        switch (cell.getCellType()) {
            case BLANK:// 空值
                newCell.setBlank();
                break;
            case BOOLEAN:
                newCell.setCellValue(cell.getBooleanCellValue());// 布尔型
                break;
            case FORMULA:
                newCell.setCellValue(cell.getCellFormula());// 公式型
                break;
            case NUMERIC:
                newCell.setCellValue(cell.getNumericCellValue());// 数值型
                break;
            case STRING:
                newCell.setCellValue(cell.getStringCellValue());// 字符型
                break;
            default:
                newCell.setBlank();
                break;
        }
    }
}