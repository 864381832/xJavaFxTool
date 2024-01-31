package com.xwintop.xJavaFxTool.services.games;

import com.xwintop.xJavaFxTool.controller.games.ChineseChessController;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
@Slf4j
public class ChineseChessService {
    private ChineseChessController chineseChessController;

    static Piece[][] points;
    static boolean isRedTurn = true;//黑红双方轮流下棋
    static Piece selectedPiece = null;//记录当前选中的棋子名称
    static ArrayList<Piece> allPieces = new ArrayList<>();
    private String record;//棋谱

    public void initialize() {
        chineseChessController.getCheckerBoard().getChildren().removeAll();
        allPieces.clear();
        record = "";
        chineseChessController.getCheckerBoard().getChildren().clear();
        for (Piece[] pieces : points = new Piece[9][10]) {
            pieces = null;
        }
        isRedTurn = true;
        selectedPiece = null;
        drawLines();
        initPutPieces();
        StartButtonOnAction();
    }//初始化

    private String printRecord(int startX, int startY, int dstX, int dstY) //棋谱记录
    {
        String step = selectedPiece.name;
        String start;
        start = getRoad(startX);

        String action, dst;
        if (startY == dstY) {
            action = "平";
            dst = getRoad(dstX);
        } else {
            if (selectedPiece.isRed)
                dst = getChineseNumber(Math.abs(startY - dstY));
            else
                dst = Integer.toString(Math.abs(startY - dstY));
            if ((selectedPiece.isRed && dstY < startY) || (!selectedPiece.isRed && dstY > startY)) {
                action = "进";
            } else action = "退";

        }
        return step + start + action + dst;
    }

    String getChineseNumber(int x) //棋谱记录
    {
        String ret = null;
        switch (x) {
            case 1:
                ret = "一";
                break;
            case 2:
                ret = "二";
                break;
            case 3:
                ret = "三";
                break;
            case 4:
                ret = "四";
                break;
            case 5:
                ret = "五";
                break;
            case 6:
                ret = "六";
                break;
            case 7:
                ret = "七";
                break;
            case 8:
                ret = "八";
                break;
            case 9:
                ret = "九";
                break;
            default:
        }
        return ret;
    }

    private static String getRoad(int x) //棋谱记录
    {
        String road = null;
        if (selectedPiece.isRed) {
            switch (x) {
                case 8:
                    road = "一";
                    break;
                case 7:
                    road = "二";
                    break;
                case 6:
                    road = "三";
                    break;
                case 5:
                    road = "四";
                    break;
                case 4:
                    road = "五";
                    break;
                case 3:
                    road = "六";
                    break;
                case 2:
                    road = "七";
                    break;
                case 1:
                    road = "八";
                    break;
                case 0:
                    road = "九";
                    break;
                default:
            }
        } else {
            road = Integer.toString(x + 1);
        }
        return road;
    }

    private void initPutPieces() //初始化摆放棋子
    {
        //将
        Jiang redShuai = new Jiang(4, 9, true);
        Jiang blackJiang = new Jiang(4, 0, false);
        //马
        Ma redMa1 = new Ma(1, 9, true);
        Ma redMa2 = new Ma(7, 9, true);
        Ma blackMa1 = new Ma(1, 0, false);
        Ma blackMa2 = new Ma(7, 0, false);
        //象
        Xiang redXiang1 = new Xiang(2, 9, true);
        Xiang redXiang2 = new Xiang(6, 9, true);
        Xiang blackXiang1 = new Xiang(2, 0, false);
        Xiang blackXiang2 = new Xiang(6, 0, false);
        //炮
        Pao redPao1 = new Pao(1, 7, true);
        Pao redPao2 = new Pao(7, 7, true);
        Pao blackPao1 = new Pao(1, 2, false);
        Pao blackPao2 = new Pao(7, 2, false);
        //兵
        Bing redbing1 = new Bing(0, 6, true);
        Bing redbing2 = new Bing(2, 6, true);
        Bing redbing3 = new Bing(4, 6, true);
        Bing redbing4 = new Bing(6, 6, true);
        Bing redbing5 = new Bing(8, 6, true);
        Bing blackbing1 = new Bing(0, 3, false);
        Bing blackbing2 = new Bing(2, 3, false);
        Bing blackbing3 = new Bing(4, 3, false);
        Bing blackbing4 = new Bing(6, 3, false);
        Bing blackbing5 = new Bing(8, 3, false);
        //车
        Ju redJu1 = new Ju(0, 9, true);
        Ju redJu2 = new Ju(8, 9, true);
        Ju blackJu1 = new Ju(0, 0, false);
        Ju blackJu2 = new Ju(8, 0, false);
        //士
        Shi redShi1 = new Shi(3, 9, true);
        Shi redShi2 = new Shi(5, 9, true);
        Shi blackShi1 = new Shi(3, 0, false);
        Shi blackShi2 = new Shi(5, 0, false);
        chineseChessController.getCheckerBoard().getChildren().addAll(allPieces);
    }

    private void drawLines() //绘制棋盘线
    {
        final int UNIT = CheckerBoard.UNIT;
        final int RIGHT_X = CheckerBoard.RIGHT_X;
        final int LEFT_X = CheckerBoard.LEFT_X;
        final int TOP_Y = CheckerBoard.TOP_Y;
        final int BOTTOM_Y = CheckerBoard.BOTTOM_Y;
        for (int i = TOP_Y; i <= BOTTOM_Y; i += UNIT) {
            Line newLine = new Line();
            newLine.setStartX(LEFT_X);
            newLine.setStartY(i);
            newLine.setEndX(RIGHT_X);
            newLine.setEndY(i);
            chineseChessController.getCheckerBoard().getChildren().add(newLine);
        }

        for (int i = LEFT_X; i <= RIGHT_X; i += UNIT) {
            Line newtopLine = new Line();
            newtopLine.setStartY(TOP_Y);
            newtopLine.setStartX(i);
            newtopLine.setEndY(TOP_Y + 4 * UNIT);
            newtopLine.setEndX(i);
            Line newbottomLine = new Line();
            newbottomLine.setStartY(TOP_Y + 5 * UNIT);
            newbottomLine.setStartX(i);
            newbottomLine.setEndY(BOTTOM_Y);
            newbottomLine.setEndX(i);
            chineseChessController.getCheckerBoard().getChildren().addAll(newtopLine, newbottomLine);
        }
        Line line = new Line(LEFT_X, TOP_Y + 4 * UNIT, LEFT_X, TOP_Y + 5 * UNIT);
        chineseChessController.getCheckerBoard().getChildren().add(line);
        line = new Line(RIGHT_X, TOP_Y + 4 * UNIT, RIGHT_X, TOP_Y + 5 * UNIT);
        chineseChessController.getCheckerBoard().getChildren().add(line);

        Line crossLine1 = new Line(LEFT_X + 3 * UNIT, TOP_Y, LEFT_X + 5 * UNIT, TOP_Y + 2 * UNIT);
        Line crossLine2 = new Line(LEFT_X + 3 * UNIT, TOP_Y + 2 * UNIT, LEFT_X + 5 * UNIT, TOP_Y);
        chineseChessController.getCheckerBoard().getChildren().addAll(crossLine1, crossLine2);
        crossLine1 = new Line(LEFT_X + 3 * UNIT, BOTTOM_Y, LEFT_X + 5 * UNIT, BOTTOM_Y - 2 * UNIT);
        crossLine2 = new Line(LEFT_X + 3 * UNIT, BOTTOM_Y - 2 * UNIT, LEFT_X + 5 * UNIT, BOTTOM_Y);
        chineseChessController.getCheckerBoard().getChildren().addAll(crossLine1, crossLine2);
    }

    public void checkerBoardOnPressed(MouseEvent e) //画布的鼠标点击响应函数
    {
        if (selectedPiece != null) {
            if (e.getButton().equals(MouseButton.SECONDARY)) { //右键取消选中
                selectedPiece.cancelSelected();
                return;
            }
            int startX = selectedPiece.x;
            int startY = selectedPiece.y;
            int targetX = CheckerBoard.pxToX(e.getX());
            int targetY = CheckerBoard.pxToY(e.getY()); //计算目标像素值
            if (selectedPiece.isRed == isRedTurn) {
                try {
                    selectedPiece.moveTo(targetX, targetY);
                    record = record + printRecord(startX, startY, targetX, targetY) + "\t";
                    if (!isRedTurn)
                        record = record + "\n";
                    isRedTurn = !isRedTurn;                   //回合交替
                    selectedPiece.cancelSelected();         //移动后取消选定
                    playMusic1();
                } catch (Piece.CanNotMoveToException ex) {
                    //if()
                    //       playMusic2();
                    //selectedPiece.cancelSelected();//否则无法选中 执行完moveto后直接抛出异常cancel掉
                    //if()
                }
            } else {
                playMusic2();
                selectedPiece.cancelSelected();
            }
        }
    }

    static void playMusic1() {// 背景音乐播放
        try {
            AudioClip ac = new AudioClip(Object.class.getResource("/chessAudio/setpiece.wav").toString());
            ac.play(1.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void playMusic2() {// 背景音乐播放
        try {
            AudioClip ac = new AudioClip(Object.class.getResource("/chessAudio/can'tmove.wav").toString());
            ac.play(1.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveRecordButtonOnAction() throws IOException   //保存棋谱的响应函数
    {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
        File file = new File(ft.format(date) + ".txt");
        synchronized (file) {
            FileWriter fw = new FileWriter(file.getName());
            fw.write(record);
            fw.close();
        }

    }

    public void showRecordButtonOnAction()   //显示棋谱的响应函数
    {
        Alert rec = new Alert(Alert.AlertType.INFORMATION);
        rec.setTitle("棋谱");
        rec.setGraphic(null);
        rec.setHeaderText(null);
        rec.setContentText(record);
        rec.show();
    }

    public void StartButtonOnAction() //开始游戏
    {
        for (Piece i : allPieces) {
            i.setVisible(true);
        }
    }

    public ChineseChessService(ChineseChessController chineseChessController) {
        this.chineseChessController = chineseChessController;
    }
}