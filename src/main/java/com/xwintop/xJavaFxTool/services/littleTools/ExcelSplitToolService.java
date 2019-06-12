package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.ExcelSplitToolController;
import com.xwintop.xJavaFxTool.utils.DirectoryTreeUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

/**
 * @ClassName: ExcelSplitToolService
 * @Description: Excel拆分工具
 * @author: xufeng
 * @date: 2019/4/25 0025 23:19
 */

@Getter
@Setter
@Slf4j
public class ExcelSplitToolService {
    private ExcelSplitToolController excelSplitToolController;

    public ExcelSplitToolService(ExcelSplitToolController excelSplitToolController) {
        this.excelSplitToolController = excelSplitToolController;
    }

    public void splitAction() throws Exception { //拆分表格
        int fileTypeSelectIndex = excelSplitToolController.getFileTypeChoiceBox().getSelectionModel().getSelectedIndex();
        if (fileTypeSelectIndex == 0) {
            splitExcel();
        } else if (fileTypeSelectIndex == 1) {
            splitCsv();
        } else if (fileTypeSelectIndex == 2) {
            splitFile();
        }
    }

    public void splitExcel() throws Exception { //拆分Excel表格
        String filePath = excelSplitToolController.getSelectFileTextField().getText();
        FileInputStream fileInputStream = new FileInputStream(filePath);
        try {
            Workbook workbook = null;
            if (filePath.endsWith(".xls")) {
                POIFSFileSystem fileSystem = new POIFSFileSystem(fileInputStream);
                workbook = new HSSFWorkbook(fileSystem);
            } else if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fileInputStream);
            } else {
                throw new RuntimeException("错误提示: 您设置的Excel文件名不合法!");
            }
            String sheetSelectString = excelSplitToolController.getSheetSelectTextField().getText();
            Sheet[] sheets = null;
            if (StringUtils.isEmpty(sheetSelectString)) {
                sheets = new Sheet[]{workbook.getSheetAt(0)};
            } else {
                String[] sheetSelectStrings = sheetSelectString.split(",");
                sheets = new Sheet[sheetSelectStrings.length];
                for (int i = 0; i < sheetSelectStrings.length; i++) {
                    sheets[i] = workbook.getSheetAt(Integer.valueOf(sheetSelectStrings[i]));
                }
            }
            String newFilePath = StringUtils.appendIfMissing(filePath, "", ".xls", ".xlsx");
            if (StringUtils.isNotEmpty(excelSplitToolController.getSaveFilePathTextField().getText())) {
                newFilePath = StringUtils.appendIfMissing(excelSplitToolController.getSaveFilePathTextField().getText(), "/", "/", "\\") + Paths.get(newFilePath).getFileName();
            }
            if (excelSplitToolController.getSplitType1RadioButton().isSelected()) {
                int allRowNumber = 0;
                for (Sheet sheet : sheets) {
                    allRowNumber += sheet.getLastRowNum();// 行数
                }
                int splitNumber = (int) Math.ceil((double) allRowNumber / excelSplitToolController.getSplitType1Spinner().getValue());
                saveSplitWorkbook(sheets, splitNumber, newFilePath);
            } else if (excelSplitToolController.getSplitType2RadioButton().isSelected()) {
                int splitNumber = excelSplitToolController.getSplitType2Spinner().getValue();
                saveSplitWorkbook(sheets, splitNumber, newFilePath);
            } else if (excelSplitToolController.getSplitType3RadioButton().isSelected()) {
                String splitType3String = excelSplitToolController.getSplitType3TextField().getText();
                String[] splitCellIndex = null;
                if (StringUtils.isEmpty(splitType3String)) {
                    splitCellIndex = new String[]{"0"};
                } else {
                    splitCellIndex = splitType3String.split(",");
                }
                saveSplitWorkbook(sheets, newFilePath, splitCellIndex);
            }
        } finally {
            fileInputStream.close();
        }
    }

    public void splitCsv() throws Exception {
        String filePath = excelSplitToolController.getSelectFileTextField().getText();
        Reader fileReader = new FileReader(filePath);
        String newFilePath = StringUtils.appendIfMissing(filePath, "", ".csv", ".CSV");
        if (StringUtils.isNotEmpty(excelSplitToolController.getSaveFilePathTextField().getText())) {
            newFilePath = StringUtils.appendIfMissing(excelSplitToolController.getSaveFilePathTextField().getText(), "/", "/", "\\") + Paths.get(newFilePath).getFileName();
        }
        CSVParser parser = CSVFormat.DEFAULT.parse(fileReader);
        int splitNumber = 0;
        if (excelSplitToolController.getSplitType1RadioButton().isSelected()) {
            List<CSVRecord> csvRecordList = parser.getRecords();
            splitNumber = (int) Math.ceil((double) csvRecordList.size() / excelSplitToolController.getSplitType1Spinner().getValue());
            saveSplitCsv(csvRecordList.iterator(), splitNumber, newFilePath);
        } else if (excelSplitToolController.getSplitType2RadioButton().isSelected()) {
            splitNumber = excelSplitToolController.getSplitType2Spinner().getValue();
            saveSplitCsv(parser.iterator(), splitNumber, newFilePath);
        } else if (excelSplitToolController.getSplitType3RadioButton().isSelected()) {
            String splitType3String = excelSplitToolController.getSplitType3TextField().getText();
            String[] splitCellIndex = null;
            if (StringUtils.isEmpty(splitType3String)) {
                splitCellIndex = new String[]{"0"};
            } else {
                splitCellIndex = splitType3String.split(",");
            }
            saveSplitCsv(parser.iterator(), newFilePath, splitCellIndex);
        }
        fileReader.close();
    }

    public void splitFile() throws Exception {
        String filePath = excelSplitToolController.getSelectFileTextField().getText();
        String newFilePath = filePath;
        if (StringUtils.isNotEmpty(excelSplitToolController.getSaveFilePathTextField().getText())) {
            newFilePath = StringUtils.appendIfMissing(excelSplitToolController.getSaveFilePathTextField().getText(), "/", "/", "\\") + Paths.get(newFilePath).getFileName();
        }
        int splitNumber = 0;
        if (excelSplitToolController.getSplitType1RadioButton().isSelected()) {
            File file = new File(filePath);
            LineNumberReader rf = new LineNumberReader(new FileReader(file));
            rf.skip(file.length());
            splitNumber = (int) Math.ceil((double) rf.getLineNumber() / excelSplitToolController.getSplitType1Spinner().getValue());
            saveSplitFile(filePath, splitNumber, newFilePath);
        } else if (excelSplitToolController.getSplitType2RadioButton().isSelected()) {
            splitNumber = excelSplitToolController.getSplitType2Spinner().getValue();
            saveSplitFile(filePath, splitNumber, newFilePath);
        } else if (excelSplitToolController.getSplitType3RadioButton().isSelected()) {
            throw new Exception("文件不支持按类拆分。");
        }
    }

    private void saveSplitWorkbook(Sheet[] sheets, int splitNumber, String newFilePath) throws Exception {
        Workbook newWorkbook = null;
        int addRowIndex = 0;
        int saveFileIndex = 0;
        Row firstRow = null;
        Sheet newSheet = null;
        for (Sheet sheet : sheets) {
            Iterator<Row> iterator = sheet.rowIterator();
            boolean isAddHead = excelSplitToolController.getIncludeHandCheckBox().isSelected();
            if (isAddHead) {
                firstRow = iterator.next();
            }
            while (iterator.hasNext()) {
                if (newWorkbook == null) {
                    if (excelSplitToolController.getOutputType1RadioButton().isSelected()) {
                        newWorkbook = new HSSFWorkbook();
                    } else {
                        newWorkbook = new XSSFWorkbook();
                    }
                    newSheet = newWorkbook.createSheet();
                    if (isAddHead) {
                        Row newRow = newSheet.createRow(addRowIndex++);
                        copyRow(firstRow, newRow);
                    }
                }
                Row row = iterator.next();
                Row newRow = newSheet.createRow(addRowIndex++);
                copyRow(row, newRow);
                if ((addRowIndex - splitNumber) == (isAddHead ? 1 : 0)) {
                    saveFile(newWorkbook, newFilePath + "-" + (saveFileIndex++));
                    newWorkbook.close();
                    newWorkbook = null;
                    addRowIndex = 0;
                }
            }
        }
        if (newWorkbook != null) {
            saveFile(newWorkbook, newFilePath + "-" + saveFileIndex);
            newWorkbook.close();
        }
    }

    private void saveSplitWorkbook(Sheet[] sheets, String newFilePath, String[] splitCellIndex) throws Exception {
        Map<String, List<Row>> rowMap = new HashMap<>();
        Row firstRow = null;
        for (Sheet sheet : sheets) {
            Iterator<Row> iterator = sheet.rowIterator();
            boolean isAddHead = excelSplitToolController.getIncludeHandCheckBox().isSelected();
            if (isAddHead) {
                firstRow = iterator.next();
            }
            while (iterator.hasNext()) {
                Row row = iterator.next();
                String rowString = "";
                for (String cellIndex : splitCellIndex) {
                    Cell cell = row.getCell(Integer.valueOf(cellIndex));
                    rowString += getCellValue(cell);
                }
                if (rowMap.containsKey(rowString)) {
                    rowMap.get(rowString).add(row);
                } else {
                    List<Row> rowList = new ArrayList<>();
                    rowList.add(row);
                    rowMap.put(rowString, rowList);
                }
            }
        }
        for (Map.Entry<String, List<Row>> stringListEntry : rowMap.entrySet()) {
            String key = stringListEntry.getKey();
            List<Row> rows = stringListEntry.getValue();
            Workbook newWorkbook = null;
            if (excelSplitToolController.getOutputType1RadioButton().isSelected()) {
                newWorkbook = new HSSFWorkbook();
            } else {
                newWorkbook = new XSSFWorkbook();
            }
            Sheet newSheet = newWorkbook.createSheet();
            int addRowIndex = 0;
            if (excelSplitToolController.getIncludeHandCheckBox().isSelected()) {
                Row newRow = newSheet.createRow(addRowIndex++);
                copyRow(firstRow, newRow);
            }
            for (Row row : rows) {
                Row newRow = newSheet.createRow(addRowIndex++);
                copyRow(row, newRow);
            }
            saveFile(newWorkbook, newFilePath + "-" + key);
            if (newWorkbook != null) {
                newWorkbook.close();
            }
        }

    }

    private void saveSplitCsv(Iterator<CSVRecord> iterator, int splitNumber, String newFilePath) throws Exception {
        int addRowIndex = 0;
        int saveFileIndex = 0;
        boolean isAddHead = excelSplitToolController.getIncludeHandCheckBox().isSelected();
        CSVPrinter printer = null;
        CSVRecord firstRecord = null;
        while (iterator.hasNext()) {
            CSVRecord record = iterator.next();
            if (isAddHead && firstRecord == null) {
                firstRecord = record;
            }
            if (printer == null) {
                printer = CSVFormat.DEFAULT.print(new PrintWriter(newFilePath + "-" + (saveFileIndex++) + ".csv"));
                if (isAddHead && saveFileIndex != 1) {
                    printer.printRecord(IteratorUtils.toList(firstRecord.iterator()).toArray());
                    addRowIndex++;
                }
            }
            printer.printRecord(IteratorUtils.toList(record.iterator()).toArray());
            addRowIndex++;
            if ((addRowIndex - splitNumber) == (isAddHead ? 1 : 0)) {
                printer.flush();
                printer.close();
                printer = null;
                addRowIndex = 0;
            }
        }
        if (printer != null) {
            printer.flush();
            printer.close();
        }
    }

    private void saveSplitCsv(Iterator<CSVRecord> iterator, String newFilePath, String[] splitCellIndex) throws Exception {
        CSVRecord firstRecord = null;
        Map<String, List<CSVRecord>> rowMap = new HashMap<>();
        while (iterator.hasNext()) {
            CSVRecord record = iterator.next();
            if (firstRecord == null) {
                firstRecord = record;
                if (excelSplitToolController.getIncludeHandCheckBox().isSelected()) {
                    continue;
                }
            }
            String rowString = "";
            for (String cellIndex : splitCellIndex) {
                rowString += record.get(Integer.valueOf(cellIndex));
            }
            if (rowMap.containsKey(rowString)) {
                rowMap.get(rowString).add(record);
            } else {
                List<CSVRecord> rowList = new ArrayList<>();
                rowList.add(record);
                rowMap.put(rowString, rowList);
            }
        }
        for (Map.Entry<String, List<CSVRecord>> stringListEntry : rowMap.entrySet()) {
            String key = stringListEntry.getKey();
            List<CSVRecord> rows = stringListEntry.getValue();
            CSVPrinter printer = CSVFormat.DEFAULT.print(new PrintWriter(newFilePath + "-" + key + ".csv"));
            if (excelSplitToolController.getIncludeHandCheckBox().isSelected()) {
                printer.printRecord(IteratorUtils.toList(firstRecord.iterator()).toArray());
            }
            for (CSVRecord row : rows) {
                printer.printRecord(IteratorUtils.toList(row.iterator()).toArray());
            }
            printer.flush();
            printer.close();
        }
    }

    private void saveSplitFile(String filePath, int splitNumber, String newFilePath) throws Exception {
        int addRowIndex = 0;
        int saveFileIndex = 0;
        LineIterator lineIterator = FileUtils.lineIterator(new File(filePath));
        File newFile = new File(newFilePath + "-" + (saveFileIndex++));
        newFile.delete();
        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            FileUtils.writeStringToFile(newFile, line + DirectoryTreeUtil.LINE_SEPARATOR, true);
            if (++addRowIndex == splitNumber) {
                newFile = new File(newFilePath + "-" + (saveFileIndex++));
                newFile.delete();
                addRowIndex = 0;
            }
        }
        lineIterator.close();
    }

    private static void saveFile(Workbook workbook, String filePath) {
        if (workbook instanceof HSSFWorkbook) {
            filePath = StringUtils.appendIfMissing(filePath, ".xls", ".xls", ".xlsx");
        } else if (workbook instanceof XSSFWorkbook) {
            filePath = StringUtils.appendIfMissing(filePath, ".xlsx", ".xls", ".xlsx");
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(filePath));
            workbook.write(out);
            workbook.close();
        } catch (IOException e) {
            log.error("报错文件失败：", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                log.error("FileOutputStream close Fail" + e.getMessage());
            }
        }
    }

    private static void copyRow(Row row, Row newRow) {
        try {
            newRow.setHeight(row.getHeight());
            newRow.setHeightInPoints(row.getHeightInPoints());
            newRow.setZeroHeight(row.getZeroHeight());
            newRow.setRowStyle(row.getRowStyle());
        } catch (Exception e) {

        }
        row.cellIterator().forEachRemaining(cell -> {
            setCellValue(cell, newRow.createCell(cell.getColumnIndex(), cell.getCellType()));
        });
    }

    private static void setCellValue(Cell cell, Cell newCell) {
        try {
            newCell.setCellStyle(cell.getCellStyle());
        } catch (Exception e) {
        }
        // 以下是判断数据的类型
        switch (cell.getCellType()) {
            case BLANK:// 空值
                newCell.setBlank();
                break;
            case BOOLEAN:
                newCell.setCellValue(cell.getBooleanCellValue());// 布尔型
                break;
            case FORMULA:
                newCell.setCellFormula(cell.getCellFormula());// 公式型
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

    private static String getCellValue(Cell cell) {
        String str = null;
        switch (cell.getCellType()) {
            case BLANK:// 空值
                str = "";
                break;
            case BOOLEAN:
                str = String.valueOf(cell.getBooleanCellValue());// 布尔型
                break;
            case FORMULA:
                str = cell.getCellFormula();// 公式型
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
        return str;
    }
}