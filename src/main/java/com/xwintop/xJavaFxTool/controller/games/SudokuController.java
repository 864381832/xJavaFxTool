package com.xwintop.xJavaFxTool.controller.games;

import com.xwintop.xJavaFxTool.controller.games.sudoku.CStatus;
import com.xwintop.xJavaFxTool.controller.games.sudoku.Matrix;
import com.xwintop.xJavaFxTool.controller.games.sudoku.SCell;
import com.xwintop.xJavaFxTool.controller.games.sudoku.Sudoku;
import com.xwintop.xJavaFxTool.services.games.SudokuService;
import com.xwintop.xJavaFxTool.view.games.SudokuView;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * @ClassName: SudokuController
 * @Description: 数独游戏
 * @author: xufeng
 * @date: 2020/1/6 15:22
 */

public class SudokuController extends SudokuView {
    private SudokuService sudokuService = new SudokuService(this);
    Sudoku sudoku = new Sudoku();

    private Matrix puzzle;
    private Matrix ans;
    private Map<String, String> ansMap;

    private boolean stop;
    private long startTime;
    private long curTime;
    private Thread thread;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        showNoteIcon();
        times.setText("5");
    }

    private void initEvent() {
        addCellFocusedProperty();
    }

    private void initService() {
        sudokuService.initTimer();
        puzzle = sudoku.init(levelChoiceBox.getSelectionModel().getSelectedIndex() + 1).get(0);
        ans = sudoku.getAns();
        ansMap = sudokuService.ansParse(this.ans);
        sudokuService.setCellNums(puzzle);
    }

    private void addCellFocusedProperty() {
        levelChoiceBox.setFocusTraversable(false);
        for (SCell cell : cells) {
            cell.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue.booleanValue()) {
                        sudokuService.showSelectedCells(cell);
                    }
                }
            });
        }
    }

    @FXML
    private void keyListener(KeyEvent e) {
        String input = e.getText();
        SCell cell = ((SCell) e.getSource());
        if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
            if (cell.status.equals(CStatus.INIT) && !cell.getText().isEmpty()) {
                sudokuService.highLight(cell);
            }
        } else if (!noteItem.isSelected()) {
            setInputText(cell, input);
        } else {
            cell.setNoteText(input);
        }
    }

    @FXML
    private void mouseListener(MouseEvent e) {
        SCell cell = (SCell) e.getSource();
        if (cell.status.equals(CStatus.NOTE) || showSelectedRC.isSelected() || showSelectedBlock.isSelected()) return;
        sudokuService.highLight(cell);
    }

    private void setInputText(SCell cell, String input) {
        if (!(cell.getText().isEmpty() || cell.status.equals(CStatus.NOTE) || cell.status.equals(CStatus.ERROR)))
            return;
        if (!input.isEmpty() && "123456789".contains(input)) {
            if (ansMap.get(cell.getId()).equals(input)) {
                cell.setInitStatus();
                ;
                cell.setText(input);
                if (sudokuService.checkSucess()) {
                    stop = true;
                    AlertUtil.showInfoAlert("Sucessful", "Sucessful!\nTime: " + timer.getText());
                }
            } else {
                cell.setErrorStatus();
                int leftTimes = Integer.parseInt(times.getText()) - 1;
                if (leftTimes < 0) {
                    AlertUtil.showInfoAlert("Fail", "Fail!\nTime: " + timer.getText());
                    times.setFill(Color.RED);
                }
                times.setText(String.valueOf(leftTimes));
            }
        } else if (input.equals("0")) {
            cell.setText("");
        }
    }

    @FXML
    private void newGame() {
        noteItem.setSelected(false);
        showCandidates.setSelected(false);
        showNoteIcon();
        times.setText("5");
        times.setFill(Color.BLACK);
        puzzle = sudoku.init(levelChoiceBox.getSelectionModel().getSelectedIndex() + 1).get(0);
        ans = sudoku.getAns();
        ansMap = sudokuService.ansParse(this.ans);
        sudokuService.setCellNums(puzzle);
        sudokuService.initTimer();
    }

    @FXML
    private void solve() {
        int num = 0;
        for (SCell cell : cells) {
            if (cell.getText().isEmpty()) num = 0;
            else num = Integer.parseInt(cell.getText());
            puzzle.set(Character.getNumericValue(cell.getId().charAt(4)), Character.getNumericValue(cell.getId().charAt(5)), num);
            ans = sudoku.solve(puzzle).get(0);
            sudokuService.setCellNums(ans);
            stop = true;
        }
    }

    @FXML
    private void openFile() {
        File file = FileChooserUtil.chooseFile(new ExtensionFilter("Text Files", "*.txt"), new ExtensionFilter("All Files", "*.*"));
        if (file != null) {
            String[][] inputPuzzle = new String[9][9];
            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                int lineNum = 0;
                while ((line = reader.readLine()) != null) {
                    inputPuzzle[lineNum] = line.trim().split("\\s+");
                    lineNum++;
                }
                reader.close();
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        puzzle.set(i, j, Integer.parseInt(inputPuzzle[i][j]));
                    }
                }
            } catch (Exception e) {
                TooltipUtil.showToast("加载文件失败：" + e.getMessage());
                return;
            }
            ans = sudoku.solve(puzzle).get(0);
            sudokuService.setCellNums(puzzle);
            sudokuService.initTimer();
        }
    }

    @FXML
    private void saveFile() {
        File file = FileChooserUtil.chooseSaveFile("Sudoku.txt");
        BufferedWriter writer;
        if (file != null) {
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                String num;
                for (int i = 0; i < cells.size(); i++) {
                    num = cells.get(i).getText();
                    if (num.isEmpty() || num.equals("X")) writer.write("0 ");
                    else writer.write(num + " ");
                    if ((i + 1) % 9 == 0) writer.newLine();
                }
                writer.close();
            } catch (IOException e) {
                TooltipUtil.showToast("保存文件失败：" + e.getMessage());
            }
        }
    }

    @FXML
    private void showCandidates() {
        if (showCandidates.isSelected()) {
            String known;
            List<String> candidates = new ArrayList<String>();
            for (SCell cell : cells) {
                candidates.clear();
                known = "";
                if (cell.getText().isEmpty() || cell.status.equals(CStatus.ERROR)) {
                    int row = Character.getNumericValue(cell.getId().charAt(4));
                    int column = Character.getNumericValue(cell.getId().charAt(5));
                    int block = sudoku.getBlock(row, column);
                    for (SCell cell2 : cells) {
                        String cell2Text = cell2.getText();
                        int row2 = Character.getNumericValue(cell2.getId().charAt(4));
                        int column2 = Character.getNumericValue(cell2.getId().charAt(5));
                        int block2 = sudoku.getBlock(row2, column2);
                        if (cell2Text.length() == 1 && !cell.status.equals(CStatus.ERROR) && !known.contains(cell2Text)) {
                            if (row2 == row || column2 == column || block2 == block) {
                                known += cell2Text;
                            }
                        }
                    }
                    for (int i = 1; i < 10; i++) {
                        if (!known.contains(String.valueOf(i))) {
                            candidates.add(String.valueOf(i));
                        }
                    }
                    Collections.sort(candidates);
                    String cellText = String.join("", candidates);
                    cell.setNoteStatus(sudokuService.getFontSize(cellText));
                    cell.wrapTextProperty().setValue(true);
                    cell.setText(cellText);
                }
            }
        } else {
            for (SCell cell : cells) {
                if (cell.status.equals(CStatus.NOTE)) {
                    cell.setText("");
                }
            }
        }
    }

    @FXML
    private void hideAllHighLightCells() {
        for (SCell cell : cells) {
            cell.isHighLight = false;
            if (cell.status.equals(CStatus.INIT) && !cell.isHighLight) {
                cell.setInitStatus();
            } else if (cell.status.equals(CStatus.NOTE)) {
                cell.setNoteStatus(sudokuService.getFontSize(cell.getText()));
                cell.setEffect(null);
            } else if (cell.status.equals(CStatus.ERROR)) {
                cell.setErrorStatus();
                cell.setEffect(null);
            }
        }
    }

    @FXML
    private void showNoteIcon() {
        if (noteItem.isSelected()) {
            noteIcon.setVisible(true);
        } else {
            noteIcon.setVisible(false);
        }
    }

    @FXML
    private void showHelp() {
        AlertUtil.showInfoAlert("Help", "Operation：\n - Move: direction key (↑↓←→) or mouse.\n - Input: numeric key.\n\nFunction:\n - Open: open a local sudoku file.\n - Save: save as sudoku file.\n - Solve: show the answer of sudoku.\n - Highlight: highlight cells.\n - Note mode: input but not verify.\n - Show candidates: show all candidates.\n - Show selected row/column/block.");
    }

    @FXML
    private void showAbout() {
        AlertUtil.showInfoAlert("About", "Sudoku v0.1\n2017-07-14");
    }

    /**
     * 父控件被移除前调用
     */
    public void onCloseRequest(Event event) {
        stop = true;
    }
}
