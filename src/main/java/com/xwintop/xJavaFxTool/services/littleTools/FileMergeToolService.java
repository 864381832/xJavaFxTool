package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.FileMergeToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName: FileMergeToolService
 * @Description: 文件合并工具
 * @author: xufeng
 * @date: 2019/6/11 17:16
 */

@Getter
@Setter
@Slf4j
public class FileMergeToolService {
    private FileMergeToolController fileMergeToolController;

    public FileMergeToolService(FileMergeToolController fileMergeToolController) {
        this.fileMergeToolController = fileMergeToolController;
    }

    public void mergeAction() throws Exception { //拆分表格
        int fileTypeSelectIndex = fileMergeToolController.getFileTypeChoiceBox().getSelectionModel().getSelectedIndex();
        if (fileTypeSelectIndex == 0) {
            mergeExcel();
        } else if (fileTypeSelectIndex == 1) {
            mergeCsv();
        } else if (fileTypeSelectIndex == 2) {
            mergeFile();
        }
    }

    public void mergeExcel() throws Exception { //拆分Excel表格
        String filePath = fileMergeToolController.getSelectFileTextField().getText();
        FileInputStream fileInputStream = new FileInputStream(filePath);

        Workbook workbook = null;
        if (filePath.endsWith(".xls")) {
            POIFSFileSystem fileSystem = new POIFSFileSystem(fileInputStream);
            workbook = new HSSFWorkbook(fileSystem);
        } else if (filePath.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(fileInputStream);
        } else {
            throw new RuntimeException("错误提示: 您设置的Excel文件名不合法!");
        }
        String sheetSelectString = fileMergeToolController.getSheetSelectTextField().getText();
        Sheet[] sheets = null;
//        if (StringUtils.isEmpty(sheetSelectString)) {
//            sheets = new Sheet[]{workbook.getSheetAt(0)};
//        } else {
//            String[] sheetSelectStrings = sheetSelectString.merge(",");
//            sheets = new Sheet[sheetSelectStrings.length];
//            for (int i = 0; i < sheetSelectStrings.length; i++) {
//                sheets[i] = workbook.getSheetAt(Integer.valueOf(sheetSelectStrings[i]));
//            }
//        }
//        String newFilePath = StringUtils.appendIfMissing(filePath, "", ".xls", ".xlsx");
//        if (StringUtils.isNotEmpty(fileMergeToolController.getSaveFilePathTextField().getText())) {
//            newFilePath = StringUtils.appendIfMissing(fileMergeToolController.getSaveFilePathTextField().getText(), "/", "/", "\\") + Paths.get(newFilePath).getFileName();
//        }
//        if (fileMergeToolController.getmergeType1RadioButton().isSelected()) {
//            int allRowNumber = 0;
//            for (Sheet sheet : sheets) {
//                allRowNumber += sheet.getLastRowNum();// 行数
//            }
//            int mergeNumber = (int) Math.ceil((double) allRowNumber / fileMergeToolController.getmergeType1Spinner().getValue());
//            savemergeWorkbook(sheets, mergeNumber, newFilePath);
//        } else if (fileMergeToolController.getmergeType2RadioButton().isSelected()) {
//            int mergeNumber = fileMergeToolController.getmergeType2Spinner().getValue();
//            savemergeWorkbook(sheets, mergeNumber, newFilePath);
//        } else if (fileMergeToolController.getmergeType3RadioButton().isSelected()) {
//            String mergeType3String = fileMergeToolController.getmergeType3TextField().getText();
//            String[] mergeCellIndex = null;
//            if (StringUtils.isEmpty(mergeType3String)) {
//                mergeCellIndex = new String[]{"0"};
//            } else {
//                mergeCellIndex = mergeType3String.merge(",");
//            }
//            savemergeWorkbook(sheets, newFilePath, mergeCellIndex);
//        }
    }

    public void mergeCsv() throws Exception {
//        String filePath = fileMergeToolController.getSelectFileTextField().getText();
//        Reader fileReader = new FileReader(filePath);
//        String newFilePath = StringUtils.appendIfMissing(filePath, "", ".csv", ".CSV");
//        if (StringUtils.isNotEmpty(fileMergeToolController.getSaveFilePathTextField().getText())) {
//            newFilePath = StringUtils.appendIfMissing(fileMergeToolController.getSaveFilePathTextField().getText(), "/", "/", "\\") + Paths.get(newFilePath).getFileName();
//        }
//        CSVParser parser = CSVFormat.DEFAULT.parse(fileReader);
//        int mergeNumber = 0;
//        if (fileMergeToolController.getmergeType1RadioButton().isSelected()) {
//            List<CSVRecord> csvRecordList = parser.getRecords();
//            mergeNumber = (int) Math.ceil((double) csvRecordList.size() / fileMergeToolController.getmergeType1Spinner().getValue());
//            savemergeCsv(csvRecordList.iterator(), mergeNumber, newFilePath);
//        } else if (fileMergeToolController.getmergeType2RadioButton().isSelected()) {
//            mergeNumber = fileMergeToolController.getmergeType2Spinner().getValue();
//            savemergeCsv(parser.iterator(), mergeNumber, newFilePath);
//        } else if (fileMergeToolController.getmergeType3RadioButton().isSelected()) {
//            String mergeType3String = fileMergeToolController.getmergeType3TextField().getText();
//            String[] mergeCellIndex = null;
//            if (StringUtils.isEmpty(mergeType3String)) {
//                mergeCellIndex = new String[]{"0"};
//            } else {
//                mergeCellIndex = mergeType3String.merge(",");
//            }
//            savemergeCsv(parser.iterator(), newFilePath, mergeCellIndex);
//        }
//        fileReader.close();
    }

    public void mergeFile() throws Exception {
        String filePath = fileMergeToolController.getSelectFileTextField().getText();
        String[] filePaths = filePath.split("\\|");
        List<File> fileList = new ArrayList<>();
        for (String path : filePaths) {
            File file = new File(path);
            if (file.isDirectory()) {
                fileList.addAll(Arrays.asList(file.listFiles()));
            } else {
                fileList.add(file);
            }
        }
        String newFilePath = null;
        if (fileList.get(0).isDirectory()) {
            if (StringUtils.isNotEmpty(fileMergeToolController.getSaveFilePathTextField().getText())) {
                newFilePath = StringUtils.appendIfMissing(fileMergeToolController.getSaveFilePathTextField().getText(), "/", "/", "\\") + "merge_file";
            } else {
                newFilePath = fileList.get(0).getPath() + "/merge_file.txt";
            }
        } else {
            if (StringUtils.isNotEmpty(fileMergeToolController.getSaveFilePathTextField().getText())) {
                newFilePath = StringUtils.appendIfMissing(fileMergeToolController.getSaveFilePathTextField().getText(), "/", "/", "\\") + "merge_" + fileList.get(0).getName();
            } else {
                newFilePath = fileList.get(0).getParent() + "/merge_" + fileList.get(0).getName();
            }
        }
        File resultFile = new File(newFilePath);
        try {
            FileChannel resultFileChannel = new FileOutputStream(resultFile, true).getChannel();
            for (File file : fileList) {
                FileChannel blk = new FileInputStream(file).getChannel();
                resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
                blk.close();
            }
            resultFileChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savemergeWorkbook(Sheet[] sheets, int mergeNumber, String newFilePath) throws Exception {
//        Workbook newWorkbook = null;
//        int addRowIndex = 0;
//        int saveFileIndex = 0;
//        Row firstRow = null;
//        Sheet newSheet = null;
//        for (Sheet sheet : sheets) {
//            Iterator<Row> iterator = sheet.rowIterator();
//            boolean isAddHead = fileMergeToolController.getIncludeHandCheckBox().isSelected();
//            if (isAddHead) {
//                firstRow = iterator.next();
//            }
//            while (iterator.hasNext()) {
//                if (newWorkbook == null) {
//                    if (fileMergeToolController.getOutputType1RadioButton().isSelected()) {
//                        newWorkbook = new HSSFWorkbook();
//                    } else {
//                        newWorkbook = new XSSFWorkbook();
//                    }
//                    newSheet = newWorkbook.createSheet();
//                    if (isAddHead) {
//                        Row newRow = newSheet.createRow(addRowIndex++);
//                        copyRow(firstRow, newRow);
//                    }
//                }
//                Row row = iterator.next();
//                Row newRow = newSheet.createRow(addRowIndex++);
//                copyRow(row, newRow);
//                if ((addRowIndex - mergeNumber) == (isAddHead ? 1 : 0)) {
//                    saveFile(newWorkbook, newFilePath + "-" + (saveFileIndex++));
//                    newWorkbook.close();
//                    newWorkbook = null;
//                    addRowIndex = 0;
//                }
//            }
//        }
//        if (newWorkbook != null) {
//            saveFile(newWorkbook, newFilePath + "-" + saveFileIndex);
//            newWorkbook.close();
//        }
    }

    private void savemergeCsv(Iterator<CSVRecord> iterator, String newFilePath) throws Exception {
//        boolean isAddHead = fileMergeToolController.getIncludeHandCheckBox().isSelected();
//        CSVPrinter printer = CSVFormat.DEFAULT.print(new PrintWriter(newFilePath + ".csv"));;
//        CSVRecord firstRecord = null;
//        while (iterator.hasNext()) {
//            CSVRecord record = iterator.next();
//            if (isAddHead && firstRecord == null) {
//                firstRecord = record;
//            }
//            if (printer == null) {
//                printer = CSVFormat.DEFAULT.print(new PrintWriter(newFilePath + "-" + (saveFileIndex++) + ".csv"));
//                if (isAddHead && saveFileIndex != 1) {
//                    printer.printRecord(IteratorUtils.toList(firstRecord.iterator()).toArray());
//                    addRowIndex++;
//                }
//            }
//            printer.printRecord(IteratorUtils.toList(record.iterator()).toArray());
//        }
//        if (printer != null) {
//            printer.flush();
//            printer.close();
//        }
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