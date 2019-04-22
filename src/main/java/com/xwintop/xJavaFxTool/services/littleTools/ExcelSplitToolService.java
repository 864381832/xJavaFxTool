package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.ExcelSplitToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

import static org.apache.poi.ss.usermodel.CellType.BLANK;

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
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        POIFSFileSystem fileSystem = new POIFSFileSystem(bufferedInputStream);
        HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);
//        HSSFSheet sheet = workbook.getSheet("Sheet1");
        HSSFSheet sheet = workbook.getSheetAt(0);

        int lastRowIndex = sheet.getLastRowNum();// 行数
        System.out.println("行数：" + lastRowIndex);
        for (int i = 0; i <= lastRowIndex; i++) {
            HSSFRow row = sheet.getRow(i);
            if (row == null) {
                break;
            }
            short lastCellNum = row.getLastCellNum();// 列数
            for (int j = 0; j < lastCellNum; j++) {
                HSSFCell cell = row.getCell(j);
                String str = null;
                switch (cell.getCellType()) {
                    case BLANK:// 空值
                        str = "";
                        break;
                    case BOOLEAN:
                        str = String.valueOf(cell.getBooleanCellValue());// 布尔型
                        break;
                    case FORMULA:
                        str = String.valueOf(cell.getCellFormula());// 公式型
                        break;
                    case NUMERIC:
                        str = String.valueOf(cell.getNumericCellValue());// 数值型
                        break;
                    case STRING:
                        str = String.valueOf(cell.getStringCellValue());// 字符型
                        break;
                    default:
                        str = null;
                        break;
                }
                System.out.print(str + "\t\t");
            }
            System.out.println();
        }
        bufferedInputStream.close();
    }
}