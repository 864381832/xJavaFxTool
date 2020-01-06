package com.xwintop.xJavaFxTool.services.games;

import com.xwintop.xJavaFxTool.controller.games.SudokuController;
import com.xwintop.xJavaFxTool.controller.games.sudoku.CStatus;
import com.xwintop.xJavaFxTool.controller.games.sudoku.Matrix;
import com.xwintop.xJavaFxTool.controller.games.sudoku.SCell;
import com.xwintop.xJavaFxTool.controller.games.sudoku.Sudoku;
import com.xwintop.xcore.util.javafx.AlertUtil;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Slf4j
public class SudokuService {
    private SudokuController sudokuController;

    Sudoku sudoku = new Sudoku();

    private Matrix puzzle;
    private Matrix ans;
    private Map<String, String> ansMap;

    private boolean stop;
    private long startTime;
    private long curTime;
    private Thread thread;

    private void setCellNums(Matrix puzzle) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                sudokuController.getCells().get(i * 9 + j).setInitStatus();
                if (puzzle.get(i, j) != 0) {
                    sudokuController.getCells().get(i * 9 + j).setText(Integer.toString(puzzle.get(i, j)));
                } else {
                    sudokuController.getCells().get(i * 9 + j).setText("");
                }
            }
        }
    }

    private void initTimer() {
        stop = false;
        startTime = System.currentTimeMillis();
        thread = new Thread() {
            public void run() {
                while (!stop) {
                    curTime = System.currentTimeMillis();
                    int interval = (int) ((curTime - startTime) / 1000);
                    int hour = interval / 3600;
                    int minute = (interval / 60) % 60;
                    int second = interval % 60;
                    sudokuController.getTimer().setText(String.format("%02d:%02d:%02d", hour, minute, second));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 将二维数组类型的数独ANS转为Map类型。
     */
    private Map<String, String> ansParse(Matrix ans) {
        Map<String, String> ansMap = new HashMap<String, String>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                ansMap.put("cell" + i + j, String.valueOf(ans.get(i, j)));
            }
        }
        return ansMap;
    }

    private boolean checkSucess() {
        for (SCell cell : sudokuController.getCells()) {
            if (cell.getText().isEmpty() || cell.status.equals(CStatus.NOTE)) {
                return false;
            }
        }
        return true;
    }

    private void setInputText(SCell cell, String input) {
        if (!(cell.getText().isEmpty() || cell.status.equals(CStatus.NOTE) || cell.status.equals(CStatus.ERROR)))
            return;
        if (!input.isEmpty() && "123456789".contains(input)) {
            if (ansMap.get(cell.getId()).equals(input)) {
                cell.setInitStatus();
                ;
                cell.setText(input);
                if (checkSucess()) {
                    stop = true;
                    AlertUtil.showInfoAlert("Sucessful", "Sucessful!\nTime: " + sudokuController.getTimer().getText());
                }
            } else {
                cell.setErrorStatus();
                int leftTimes = Integer.parseInt(sudokuController.getTimes().getText()) - 1;
                if (leftTimes < 0) {
                    AlertUtil.showInfoAlert("Fail", "Fail!\nTime: " + sudokuController.getTimer().getText());
                    sudokuController.getTimes().setFill(Color.RED);
                }
                sudokuController.getTimes().setText(String.valueOf(leftTimes));
            }
        } else if (input.equals("0")) {
            cell.setText("");
        }
    }

    private void highLight(SCell cell) {
        // cell空或ERROR或已经高亮，取消所有高亮
        if (cell.getText().isEmpty() || cell.status.equals(CStatus.ERROR) || cell.status.equals(CStatus.NOTE) || cell.isHighLight) {
            for (SCell cell2 : sudokuController.getCells()) {
                if (cell2.status.equals(CStatus.INIT)) {
                    cell2.setInitStatus();
                }
            }
            // cell未高亮，则执行高亮
        } else {
            for (SCell cell2 : sudokuController.getCells()) {
                if (cell2.getText().equals(cell.getText()) && !cell2.status.equals(CStatus.NOTE)) {
                    cell2.setHighLightStatus();
                } else if (cell2.status.equals(CStatus.INIT)) {
                    cell2.setInitStatus();
                } else if (cell2.status.equals(CStatus.ERROR)) {
                    cell2.setEffect(null);
                    cell2.isHighLight = false;
                    cell2.setErrorStatus();
                } else if (cell2.status.equals(CStatus.NOTE)) {
                    cell2.setEffect(null);
                    cell2.isHighLight = false;
                    cell2.setNoteStatus(getFontSize(cell2.getText()));
                }
            }
        }
    }

    private void showSelectedCells(SCell focusCells) {
        if (!sudokuController.getShowSelectedRC().isSelected() && !sudokuController.getShowSelectedBlock().isSelected())
            return;
        List<SCell> showCells = new ArrayList<SCell>();
        for (SCell cell : sudokuController.getCells()) {
            if (sudokuController.getShowSelectedRC().isSelected() && (cell.getId().charAt(4) == focusCells.getId().charAt(4) || cell.getId().charAt(5) == focusCells.getId().charAt(5))) {
                showCells.add(cell);
            }
            if (sudokuController.getShowSelectedBlock().isSelected() && sudoku.getBlock(Character.getNumericValue(cell.getId().charAt(4)), Character.getNumericValue(cell.getId().charAt(5))) == sudoku.getBlock(Character.getNumericValue(focusCells.getId().charAt(4)), Character.getNumericValue(focusCells.getId().charAt(5)))) {
                showCells.add(cell);
            }
        }
        if (sudokuController.getShowSelectedRC().isSelected() || sudokuController.getShowSelectedBlock().isSelected()) {
            for (SCell cell : sudokuController.getCells()) {
                if (cell.equals(focusCells)) {
                    cell.setEffect(null);
                    cell.isHighLight = false;
                } else if (showCells.contains(cell)) {
                    cell.setHighLightStatus();
                } else {
                    cell.isHighLight = false;
                    if (cell.status.equals(CStatus.INIT) && !cell.isHighLight) {
                        cell.setInitStatus();
                    } else if (cell.status.equals(CStatus.NOTE)) {
                        cell.setNoteStatus(getFontSize(cell.getText()));
                        cell.setEffect(null);
                    } else if (cell.status.equals(CStatus.ERROR)) {
                        cell.setErrorStatus();
                        cell.setEffect(null);
                    }
                }
            }
        }
    }

    private int getFontSize(String cellText) {
        if (cellText.length() <= 2) return 24 - cellText.length() * 4;
        else return 12;
    }

    public SudokuService(SudokuController sudokuController) {
        this.sudokuController = sudokuController;
    }
}
