package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.FileMergeToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
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

    public void mergeAction() throws Exception { //合并操作
        int fileTypeSelectIndex = fileMergeToolController.getFileTypeChoiceBox().getSelectionModel().getSelectedIndex();

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
                newFilePath = fileList.get(0).getPath() + "/merge_file";
            }
        } else {
            if (StringUtils.isNotEmpty(fileMergeToolController.getSaveFilePathTextField().getText())) {
                newFilePath = StringUtils.appendIfMissing(fileMergeToolController.getSaveFilePathTextField().getText(), "/", "/", "\\") + "merge_" + fileList.get(0).getName();
            } else {
                newFilePath = fileList.get(0).getParent() + "/merge_" + fileList.get(0).getName();
            }
        }
        File resultFile = new File(newFilePath);
        if (fileTypeSelectIndex == 0) {
            mergeExcel(fileList, newFilePath);
        } else if (fileTypeSelectIndex == 1) {
            mergeCsv(fileList, newFilePath);
        } else if (fileTypeSelectIndex == 2) {
            mergeFile(fileList, newFilePath);
        }
    }

    public void mergeExcel(List<File> fileList, String newFilePath) throws Exception { //拆分Excel表格
        newFilePath = StringUtils.appendIfMissing(newFilePath, ".xls", ".xls", ".xlsx");
        boolean isAddHead = fileMergeToolController.getIncludeHandCheckBox().isSelected();
        Workbook newWorkbook = new HSSFWorkbook();
        if (!newFilePath.endsWith(".xls")) {
            newWorkbook = new XSSFWorkbook();
        }
        Sheet newSheet = newWorkbook.createSheet();
        int addRowIndex = 0;
        boolean addHeadRecord = false;
        for (File file : fileList) {
            String filePath = file.getPath();
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
                Iterator<Sheet> sheetIterator = workbook.sheetIterator();
                while (sheetIterator.hasNext()) {
                    Sheet sheet = sheetIterator.next();
                    Iterator<Row> iterator = sheet.rowIterator();
                    Row firstRow = null;
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        if (isAddHead && firstRow == null) {
                            firstRow = row;
                            if (!addHeadRecord) {
                                addHeadRecord = true;
                                Row newRow = newSheet.createRow(addRowIndex++);
                                copyRow(firstRow, newRow);
                            }
                        } else {
                            Row newRow = newSheet.createRow(addRowIndex++);
                            copyRow(row, newRow);
                        }
                    }
                }
            } finally {
                fileInputStream.close();
            }
        }
        saveFile(newWorkbook, newFilePath);
        newWorkbook.close();
    }

    public void mergeCsv(List<File> fileList, String newFilePath) throws Exception {
        newFilePath = StringUtils.appendIfMissing(newFilePath, ".csv", ".csv", ".CSV");
        boolean isAddHead = fileMergeToolController.getIncludeHandCheckBox().isSelected();
        CSVPrinter printer = CSVFormat.DEFAULT.print(new PrintWriter(newFilePath));
        boolean addHeadRecord = false;
        for (File file : fileList) {
            Reader fileReader = new FileReader(file);
            CSVParser parser = CSVFormat.DEFAULT.parse(fileReader);
            Iterator<CSVRecord> iterator = parser.iterator();
            CSVRecord firstRecord = null;
            while (iterator.hasNext()) {
                CSVRecord record = iterator.next();
                if (isAddHead && firstRecord == null) {
                    firstRecord = record;
                    if (!addHeadRecord) {
                        addHeadRecord = true;
                        printer.printRecord(IteratorUtils.toList(record.iterator()).toArray());
                    }
                } else {
                    printer.printRecord(IteratorUtils.toList(record.iterator()).toArray());
                }
            }
            fileReader.close();
        }
        printer.flush();
        printer.close();
    }

    public void mergeFile(List<File> fileList, String newFilePath) throws Exception {
        File resultFile = new File(newFilePath);
        try {
            FileChannel resultFileChannel = new FileOutputStream(resultFile, true).getChannel();
            for (File file : fileList) {
                FileChannel blk = new FileInputStream(file).getChannel();
                resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
                blk.close();
            }
            resultFileChannel.close();
        } catch (Exception e) {
            log.error("合并文件失败：", e);
        }
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

}