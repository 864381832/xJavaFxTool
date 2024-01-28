package com.xwintop.xJavaFxTool.controller.games.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * 生成唯一解数独，并且提供解数独方法。
 */
public class Sudoku {

    private Matrix puzzle;
    private Matrix ans;

    /**
     * 初始化数独，生成数独问题及答案。
     * <p> 随机生成数独第一行，其余补0，将此打乱转为稀疏矩阵求解并返回其中一个全覆盖矩阵作为数独答案，随机取其稀疏矩阵并验证作为数独问题。
     *
     * @param level 默认值为1
     * @return 以List类型返回数独的问题和答案
     */
    public List<Matrix> init() {
        return init(1);
    }

    public List<Matrix> init(int level) {
        int clue = getClue(level);
        // 随机数独第一行
        puzzle = new Matrix(9, 9);
        List<Integer> firstRow = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(firstRow);
        for (int i = 0; i < 9; i++) {
            puzzle.set(0, i, firstRow.get(i));
        }
        // 返回乱序精确度覆盖矩阵
        // DLX dlx = new DLX(createSparseMatrix(puzzle));
        List<int[]> tmpList = Arrays.asList(createSparseMatrix(puzzle).toArray());
        Collections.shuffle(tmpList);
        DLX dlx = new DLX(new Matrix(tmpList.toArray(new int[tmpList.size()][])));
        dlx.solve();
        Matrix coverMatrix = dlx.getAns().get(0);

        ans = coverMatrixToSudoku(coverMatrix);

        // 选取子矩阵并转为数独问题
        List<int[]> coverList = Arrays.asList(coverMatrix.toArray());
        do {
            Collections.shuffle(coverList);
            Matrix puzzleCoverMatrix = new Matrix(coverList.subList(0, clue).toArray(new int[clue][]));
            puzzle = coverMatrixToSudoku(puzzleCoverMatrix);
        } while (!testPuzzle(puzzle, level));

        // 以List类型返回puzzle和ans
        List<Matrix> ret = new ArrayList<Matrix>();
        ret.add(puzzle);
        ret.add(ans);
        return ret;
    }

    /**
     * 解数独问题，以List类型返回数独的所有解，并将数独问题及其中一个解保存于成员变量puzzle和ans中。
     */
    public List<Matrix> solve(Matrix puzzle) {
        this.puzzle = puzzle;
        List<Matrix> ansList = new ArrayList<Matrix>();
        Matrix sparseMatrix = createSparseMatrix(puzzle);
        DLX dlx = new DLX(sparseMatrix);
        dlx.solve();
        for (Matrix x : dlx.getAns()) {
            ansList.add(coverMatrixToSudoku(x));
        }
        this.ans = ansList.get(0);
        return ansList;
    }

    /**
     * 根据数独问题，创建稀疏矩阵，将数独问题转为精确度覆盖问题。四个约束条件：
     * <ul>
     * <li>每格有且只有一个数字 (0-80) index = i * 9 + j
     * <li>每行九个数字不重复 (81-161) index = i * 9 + x - 1 + 81
     * <li>每列九个数字不重复 (162-242) index = j * 9 + x - 1 + 162
     * <li>每块九个数字不重复 (243-323) index = n * 9 + x - 1 + 243
     * </ul>
     * 转换后，每行有 4 * 9 * 9 = 324 个元素 行数最多有 9 * 9 * 9 = 729，理论上有 9 + 72 × 9 = 657 行
     *
     * @param puzzle Sudoku puzzle
     * @return Sparse matrix
     */
    public Matrix createSparseMatrix(Matrix puzzle) {
        List<int[]> lines = new ArrayList<int[]>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (puzzle.get(i, j) != 0) {
                    int[] line = new int[9 * 9 * 4];
                    line[9 * i + j] = 1;
                    line[9 * i + puzzle.get(i, j) - 1 + 9 * 9] = 1;
                    line[9 * j + puzzle.get(i, j) - 1 + 9 * 9 * 2] = 1;
                    line[9 * getBlock(i, j) + puzzle.get(i, j) - 1 + 9 * 9 * 3] = 1;
                    lines.add(line);
                } else {
                    for (int k = 1; k <= 9; k++) {
                        int[] line = new int[9 * 9 * 4];
                        line[9 * i + j] = 1;
                        line[9 * i + k - 1 + 9 * 9] = 1;
                        line[9 * j + k - 1 + 9 * 9 * 2] = 1;
                        line[9 * getBlock(i, j) + k - 1 + 9 * 9 * 3] = 1;
                        lines.add(line);
                    }
                }
            }
        }
        Matrix sparseMatrix = new Matrix(lines.toArray(new int[lines.size()][]));
        return sparseMatrix;
    }

    public Matrix coverMatrixToSudoku(Matrix coverMatrix) {
        Matrix sudoku = new Matrix(9, 9);
        for (int i = 0; i < coverMatrix.length; i++) {
            int row = 0, col = 0, num = 0;
            for (int j = 0; j < coverMatrix.get(0).length; j++) {
                if (coverMatrix.get(i, j) == 1) {
                    if (j >= 81 && j < 162) {
                        row = (j - 81) / 9;
                        num = j % 9 + 1;
                    } else if (j >= 162 && j < 243) {
                        col = (j - 162) / 9;
                    }
                }
            }
            sudoku.set(row, col, num);
        }
        return sudoku;
    }

    /**
     * 数独分布检验及唯一解检验。
     */
    private boolean testPuzzle(Matrix puzzle, int level) {
        // 数独分布检验
        if (!testBlankNumber(puzzle, level)) return false;
        // 唯一解检验
        if (!testOnlyAns(puzzle)) return false;

        return true;
    }

    private boolean testBlankNumber(Matrix puzzle, int level) {
        int minBlankNumber = 0;
        int maxBlankNumber = 9;
        if (level == 1) {
            minBlankNumber = 3;
            maxBlankNumber = 7;
        } else if (level == 2) {
            minBlankNumber = 4;
            maxBlankNumber = 8;
        } else if (level == 3) {
            minBlankNumber = 4;
            maxBlankNumber = 9;
        } else if (level == 4) {
            minBlankNumber = 5;
            maxBlankNumber = 9;
        }

        int rowBlankNumber;
        for (int[] row : puzzle.toArray()) {
            rowBlankNumber = 0;
            for (int x : row) {
                if (x == 0) rowBlankNumber++;
            }
            if (rowBlankNumber < minBlankNumber || rowBlankNumber > maxBlankNumber) return false;
        }
        int colsBlankNumber[] = new int[9];
        int blocksBlankNumber[] = new int[9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (puzzle.get(i, j) == 0) {
                    colsBlankNumber[j]++;
                    blocksBlankNumber[getBlock(i, j)]++;
                }
            }
        }
        for (int k = 0; k < 9; k++) {
            if (colsBlankNumber[k] < minBlankNumber || colsBlankNumber[k] > maxBlankNumber) return false;
            if (blocksBlankNumber[k] < minBlankNumber || blocksBlankNumber[k] > maxBlankNumber) return false;
        }
        return true;
    }

    private boolean testOnlyAns(Matrix puzzle) {
        DLX dlx = new DLX(new Sudoku().createSparseMatrix(puzzle));
        dlx.solve(2);
        if (dlx.getAns().size() > 1) return false;
        return true;
    }

    // TODO
    private int getClue(int level) {
        if (level == 1) return 42;
        else if (level == 2) return 36;
        else if (level == 3) return 30;
        else if (level == 4) return 26;
        else System.err.println("Level must be one of 1, 2, 3, 4.");
        return 0;
    }

    /**
     * 给定行列索引，返回所在区块索引。
     */
    public int getBlock(int i, int j) {
        if (i < 3) {
            if (j < 3) return 0;
            else if (j < 6) return 1;
            else return 2;
        } else if (i < 6) {
            if (j < 3) return 3;
            else if (j < 6) return 4;
            else return 5;

        } else {
            if (j < 3) return 6;
            else if (j < 6) return 7;
            else return 8;
        }
    }

    public Matrix getAns() {
        return ans;
    }
}
