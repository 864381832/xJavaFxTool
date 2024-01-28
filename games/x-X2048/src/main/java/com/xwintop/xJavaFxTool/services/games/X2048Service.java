package com.xwintop.xJavaFxTool.services.games;

import com.xwintop.xJavaFxTool.controller.games.X2048Controller;
import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: X2048Service
 * @Description: 2048游戏
 * @author: xufeng
 * @date: 2019/4/25 0025 23:33
 */

@Getter
@Setter
@Slf4j
public class X2048Service {
    private X2048Controller x2048Controller;
    public int size;
    private int[][] table;
    private int[][] tableold;
    private Point[][] tableFlight;
    private int[][] tableAfter;
    private Point[] tableAdd = new Point[2];
    private State state;
    private boolean keyPressed;
    private boolean startFlag = false;
    private KeyCode code;
    private List<Pane> paneList = new ArrayList<>();
    public double width;
    public double height;
    public int margin = 0;
    public int scoreInt = 0;
    public long mLastSize = 0;
    private boolean mMousePressed;
    private final String fontName = "System";
    SimpleStringProperty score = new SimpleStringProperty("0");
    private Color[] color = new Color[]{
            Color.TRANSPARENT,
            Color.rgb(255, 236, 158),
            Color.rgb(255, 193, 0),
            Color.rgb(255, 81, 0),
            Color.rgb(255, 0, 92),
            Color.rgb(185, 0, 255),
            Color.rgb(122, 0, 255),
            Color.rgb(0, 60, 255),
            Color.rgb(0, 207, 255),
            Color.rgb(0, 255, 99),
            Color.rgb(126, 255, 0),
            Color.rgb(190, 255, 0),
    };
    public Transition m2048ColorTransition;
    private Point mPressedPoint;
    private Point mReleasedPoint;

    public void Init() {
        mLastSize = 4;
        x2048Controller.getTbScore().textProperty().bind(score);
        x2048Controller.getSliderSize().setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                long n = Math.round(x2048Controller.getSliderSize().getValue());
                x2048Controller.getSliderSize().setValue(n);
                if (n != mLastSize) {
                    mLastSize = n;
                    ImplInit((int) n);
                }
                x2048Controller.getPlayArea().requestFocus();
            }
        });
        x2048Controller.getSliderSize().valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                long o = Math.round(oldValue.doubleValue());
                long n = Math.round(newValue.doubleValue());
            }
        });
        x2048Controller.getBtnReset().focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    x2048Controller.getPlayArea().requestFocus();
                }
            }
        });
    }

    public void ImplInit(int size) {
        this.size = size;
        startFlag = true;
        scoreInt = 0;
        score.setValue("0");
        state = State.RUNNING;
        table = new int[size][size];
        tableFlight = new Point[size][size];
        tableAfter = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                tableFlight[i][j] = new Point(0, 0);
            }
        }
        x2048Controller.getPlayArea().getChildren().clear();
        paneList.clear();
        width = x2048Controller.getPlayArea().getWidth();
        height = x2048Controller.getPlayArea().getHeight();
        if (width > height) {
            width = height;
        } else {
            height = width;
        }
        Rectangle bk = new Rectangle();
        if (size == 3) {
            margin = 20;
        }
        if (size == 4) {
            margin = 15;
        }
        if (size == 5) {
            margin = 10;
        }
        if (size == 6) {
            margin = 6;
        }
        bk.setWidth(width - margin / 2);
        bk.setHeight(height - margin / 2);
        bk.setFill(Color.grayRgb(195));
        bk.setArcWidth(10);
        bk.setArcHeight(10);
        x2048Controller.getPlayArea().getChildren().add(bk);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                StackPane pane = new StackPane();
                Rectangle r = new Rectangle();
                r.setFill(Color.grayRgb(225));
                r.setStroke(Color.TRANSPARENT);
                pane.setLayoutX(j * (width - margin) / size + margin / 2 - 1);
                pane.setLayoutY(i * (height - margin) / size + margin / 2 - 1);
                pane.setPrefWidth((width - margin) / size - margin / 2 + 2);
                pane.setPrefHeight((height - margin) / size - margin / 2 + 2);
                r.setWidth((width - margin) / size - margin / 2 + 2);
                r.setHeight((height - margin) / size - margin / 2 + 2);
                pane.getChildren().add(r);
                x2048Controller.getPlayArea().getChildren().add(pane);
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                StackPane pane = new StackPane();
                Rectangle r = new Rectangle();
                r.setFill(color[0]);
                r.setStroke(Color.TRANSPARENT);
                r.setArcHeight(30);
                r.setArcWidth(30);
                Text text = new Text("");
                text.setFill(Color.BLACK);
                if (size == 3) {
                    text.setFont(new Font(fontName, 45));
                    r.setArcHeight(40);
                    r.setArcWidth(40);
                }
                if (size == 4) {
                    text.setFont(new Font(fontName, 40));
                    r.setArcHeight(30);
                    r.setArcWidth(30);
                }
                if (size == 5) {
                    text.setFont(new Font(fontName, 35));
                    r.setArcHeight(20);
                    r.setArcWidth(20);
                }
                if (size == 6) {
                    text.setFont(new Font(fontName, 25));
                    r.setArcHeight(10);
                    r.setArcWidth(10);
                }

                pane.setLayoutX(j * (width - margin) / size + margin / 2);
                pane.setLayoutY(i * (height - margin) / size + margin / 2);
                pane.setPrefWidth((width - margin) / size - margin / 2);
                pane.setPrefHeight((height - margin) / size - margin / 2);
                r.setWidth((width - margin) / size - margin / 2);
                r.setHeight((height - margin) / size - margin / 2);
                pane.getChildren().add(r);
                pane.getChildren().add(text);
                paneList.add(pane);
                x2048Controller.getPlayArea().getChildren().add(pane);

            }
        }
        AfterMove();
    }

    private void Render() {
        int add = 1;
        if (startFlag) {
            add = 2;
            startFlag = false;
        }
        ParallelTransition parallelTransition2 = new ParallelTransition();
        List<MoveAnime> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                StackPane pane = (StackPane) paneList.get(i * size + j);
                if (tableFlight != null) {
                    if (tableFlight[i][j].my != 0 || tableFlight[i][j].mx != 0) {
                        list.add(new MoveAnime(this, pane, tableFlight[i][j].mx, tableFlight[i][j].my));
                    }
                }
                if (tableAfter != null && tableAfter[i][j] != 0) {
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), pane);
                    scaleTransition.setFromX(1.15);
                    scaleTransition.setFromY(1.15);
                    scaleTransition.setToX(1);
                    scaleTransition.setToY(1);
                    parallelTransition2.getChildren().add(scaleTransition);
                }

                for (int k = 0; k < add; k++) {
                    if (i == tableAdd[k].mx && j == tableAdd[k].my) {
                        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), pane);
                        scaleTransition.setFromX(0.85);
                        scaleTransition.setFromY(0.85);
                        scaleTransition.setToX(1);
                        scaleTransition.setToY(1);
                        parallelTransition2.getChildren().add(scaleTransition);
                    }
                }
            }
        }
        StartMoveAnime(list);
        SequentialTransition s = new SequentialTransition();
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(100));
        s.getChildren().addAll(pauseTransition, parallelTransition2);
        s.play();
    }

    private void StartMoveAnime(List<MoveAnime> list) {
        Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(100));
            }

            @Override
            protected void interpolate(double frac) {
                for (MoveAnime item : list) {
                    item.mPane.setLayoutX(item.mFromX + frac * item.mMoveX);
                    item.mPane.setLayoutY(item.mFromY + frac * item.mMoveY);
                }
            }
        };
        animation.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        StackPane pane = (StackPane) paneList.get(i * size + j);
                        pane.setLayoutX(j * (width - margin) / size + margin / 2);
                        pane.setLayoutY(i * (height - margin) / size + margin / 2);
                        Rectangle r = (Rectangle) pane.getChildren().get(0);
                        Text t = (Text) pane.getChildren().get(1);
                        r.setFill(color[table[i][j]]);
                        if (table[i][j] != 0) {
                            t.setText(String.format("%d", (int) Math.pow(2, table[i][j])));
                        } else {
                            t.setText("");
                        }
                    }
                }
            }
        });
        animation.play();
    }

    private void AddRandomBlock() {
        int add = 1;
        if (startFlag) {
            add = 2;
        }
        List<Integer> list = CheckEmptyArea();
        if (list.size() != 0) {
            for (int j = 0; j < add; j++) {
                int i = (int) (Math.random() * list.size());
                int k = list.get(i);
                table[k / size][k % size] = 1;
                list.remove(i);
                tableAdd[j] = new Point(k / size, k % size);

            }
        }
    }

    private List<Integer> CheckEmptyArea() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (table[i][j] == 0) {
                    list.add(i * size + j);
                }
            }
        }
        return list;
    }

    public void OnKeyPressed(KeyEvent keyEvent) {
        if (!keyPressed) {
            keyPressed = true;
            code = keyEvent.getCode();
        }
    }

    public void OnKeyReleased(KeyEvent event) {
        if (keyPressed) {
            keyPressed = false;
            if (event.getCode() == code) {
                ProcessCode(code);
            }
        }
    }

    private boolean OnChange() {
        if (state == State.RUNNING) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (table[i][j] != tableold[i][j]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void ProcessCode(KeyCode code) {
        tableold = new int[size][size];
        tableAfter = new int[size][size];
        tableFlight = new Point[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(table[i], 0, tableold[i], 0, size);
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                tableFlight[i][j] = new Point(0, 0);
                tableAfter[i][j] = 0;
            }
        }
        int[] l;
        switch (code) {
            case UP:
                l = new int[size];
                for (int i = 0; i < size; i++) {
                    l[i] = 0;
                }
                for (int i = 1; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (table[i][j] != 0) {
                            int k = i - 1;
                            for (; k >= l[j]; k--) {
                                if (table[k + 1][j] == table[k][j]) {
                                    table[k + 1][j] = 0;
                                    table[k][j]++;
                                    l[j] = k + 1;
                                    tableFlight[i][j].my--;
                                    tableAfter[k][j] = 1;
                                } else if (table[k + 1][j] != 0 && table[k][j] == 0) {
                                    table[k][j] = table[k + 1][j];
                                    table[k + 1][j] = 0;
                                    tableFlight[i][j].my--;
                                }
                            }

                        }
                    }
                }
                if (OnChange()) {
                    AfterMove();
                }
                break;
            case DOWN:
                l = new int[size];
                for (int i = 0; i < size; i++) {
                    l[i] = size - 1;
                }
                for (int i = size - 2; i >= 0; i--) {
                    for (int j = 0; j < size; j++) {
                        if (table[i][j] != 0) {
                            int k = i + 1;
                            for (; k <= l[j]; k++) {
                                if (table[k - 1][j] == table[k][j]) {
                                    table[k - 1][j] = 0;
                                    table[k][j]++;
                                    l[j] = k - 1;
                                    tableFlight[i][j].my++;
                                    tableAfter[k][j] = 1;
                                } else if (table[k - 1][j] != 0 && table[k][j] == 0) {
                                    table[k][j] = table[k - 1][j];
                                    table[k - 1][j] = 0;
                                    tableFlight[i][j].my++;
                                }
                            }
                        }
                    }
                }
                if (OnChange()) {
                    AfterMove();
                }
                break;
            case LEFT:
                l = new int[size];
                for (int i = 0; i < size; i++) {
                    l[i] = 0;
                }
                for (int j = 1; j < size; j++) {
                    for (int i = 0; i < size; i++) {
                        if (table[i][j] != 0) {
                            int k = j - 1;
                            for (; k >= l[i]; k--) {
                                if (table[i][k] == table[i][k + 1]) {
                                    table[i][k + 1] = 0;
                                    table[i][k]++;
                                    l[i] = k + 1;
                                    tableFlight[i][j].mx--;
                                    tableAfter[i][k] = 1;
                                } else if (table[i][k + 1] != 0 && table[i][k] == 0) {
                                    table[i][k] = table[i][k + 1];
                                    table[i][k + 1] = 0;
                                    tableFlight[i][j].mx--;
                                }
                            }

                        }
                    }
                }
                if (OnChange()) {
                    AfterMove();
                }
                break;
            case RIGHT:
                l = new int[size];
                for (int i = 0; i < size; i++) {
                    l[i] = size - 1;
                }
                for (int j = size - 2; j >= 0; j--) {
                    for (int i = 0; i < size; i++) {
                        if (table[i][j] != 0) {
                            int k = j + 1;
                            for (; k <= l[i]; k++) {
                                if (table[i][k] == table[i][k - 1]) {
                                    table[i][k - 1] = 0;
                                    table[i][k]++;
                                    l[i] = k - 1;
                                    tableFlight[i][j].mx++;
                                    tableAfter[i][k] = 1;
                                } else if (table[i][k - 1] != 0 && table[i][k] == 0) {
                                    table[i][k] = table[i][k - 1];
                                    table[i][k - 1] = 0;
                                    tableFlight[i][j].mx++;
                                }
                            }

                        }
                    }
                }
                if (OnChange()) {
                    AfterMove();
                }
                break;
        }

    }

    private void AfterMove() {
        AddRandomBlock();
        AddScore();
        if (CheckLose()) {
            state = State.GAMEOVER;
        }
        if (Check2048()) {
            state = State.GAMESUCCESS;
        }

        Render();
        if (state == State.GAMEOVER) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("抱歉");
            alert.setHeaderText("游戏结束");
            alert.setContentText("xwintop，欢迎支持！\n请点击按钮重置游戏！");
            alert.show();
        } else if (state == State.GAMESUCCESS) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("666");
            alert.setHeaderText("大吉大利，晚上吃鸡");
            alert.setContentText("你赢了？--xwintop。\n请点击按钮重置游戏！");
            alert.show();
        }
    }

    private boolean Check2048() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (table[i][j] == 11) {
                    StackPane pane = (StackPane) paneList.get(i * size + j);
                    Rectangle rectangle = (Rectangle) pane.getChildren().get(0);
                    Timeline timeline = new Timeline();
                    KeyValue[] values = new KeyValue[]{
                            new KeyValue(rectangle.fillProperty(), Color.rgb(255, 0, 0)),
                            new KeyValue(rectangle.fillProperty(), Color.rgb(255, 125, 0)),
                            new KeyValue(rectangle.fillProperty(), Color.rgb(255, 255, 0)),
                            new KeyValue(rectangle.fillProperty(), Color.rgb(0, 255, 0)),
                            new KeyValue(rectangle.fillProperty(), Color.rgb(0, 255, 255)),
                            new KeyValue(rectangle.fillProperty(), Color.rgb(0, 0, 255)),
                            new KeyValue(rectangle.fillProperty(), Color.rgb(255, 0, 255)),
                            new KeyValue(rectangle.fillProperty(), Color.rgb(255, 0, 0))
                    };
                    KeyFrame[] frames = new KeyFrame[]{
                            new KeyFrame(Duration.millis(0), values[0]),
                            new KeyFrame(Duration.millis(200), values[1]),
                            new KeyFrame(Duration.millis(400), values[2]),
                            new KeyFrame(Duration.millis(600), values[3]),
                            new KeyFrame(Duration.millis(800), values[4]),
                            new KeyFrame(Duration.millis(1000), values[5]),
                            new KeyFrame(Duration.millis(1200), values[6]),
                            new KeyFrame(Duration.millis(1400), values[7]),
                    };
                    timeline.getKeyFrames().addAll(frames);
                    timeline.setCycleCount(Timeline.INDEFINITE);
                    return true;
                }
            }
        }
        return false;
    }

    private void AddScore() {
        int temp = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (tableAfter[i][j] != 0) {
                    temp += (int) (Math.pow(2, table[i][j]));
                }
            }
        }
        scoreInt += temp;
        score.setValue(String.format("%d", scoreInt));

    }

    private boolean CheckLose() {
        int s = CheckEmptyArea().size();
        if (s == 0) {
            if (table[0][0] != 0) {
                if (table[0][0] == table[0][1] || table[0][0] == table[1][0]) {
                    return false;
                }
            }
            if (table[0][size - 1] != 0) {
                if (table[0][size - 1] == table[0][size - 2] || table[0][size - 1] == table[1][size - 1]) {
                    return false;
                }
            }
            if (table[size - 1][size - 1] != 0) {
                if (table[size - 1][size - 1] == table[size - 1][size - 2] || table[size - 1][size - 1] == table[size - 2][size - 1]) {
                    return false;
                }
            }
            if (table[size - 1][0] != 0) {
                if (table[size - 1][0] == table[size - 2][0] || table[size - 1][0] == table[size - 1][1]) {
                    return false;
                }
            }
            for (int i = 1; i < size - 1; i++) {
                if (table[i][0] != 0) {
                    if (table[i][0] == table[i - 1][0]
                            || table[i][0] == table[i + 1][0]
                            || table[i][0] == table[i][1]) {
                        return false;
                    }
                }
                if (table[i][size - 1] != 0) {
                    if (table[i][size - 1] == table[i - 1][size - 1]
                            || table[i][size - 1] == table[i + 1][size - 1]
                            || table[i][size - 1] == table[i][size - 2]) {
                        return false;
                    }
                }
                if (table[0][i] != 0) {
                    if (table[0][i] == table[0][i - 1]
                            || table[0][i] == table[0][i + 1]
                            || table[0][i] == table[1][i]) {
                        return false;
                    }
                }
                if (table[size - 1][i] != 0) {
                    if (table[size - 1][i] == table[size - 1][i - 1]
                            || table[size - 1][i] == table[size - 1][i + 1]
                            || table[size - 1][i] == table[size - 2][i]) {
                        return false;
                    }
                }
            }
            for (int i = 1; i < size - 1; i++) {
                for (int j = 1; j < size - 1; j++) {
                    if (table[i][j] != 0) {
                        if (table[i][j] == table[i - 1][j]
                                || table[i][j] == table[i + 1][j]
                                || table[i][j] == table[i][j + 1]
                                || table[i][j] == table[i][j - 1]) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void MouseManipulation() {
        if (Math.abs(mPressedPoint.mx - mReleasedPoint.mx) < 50) {
            if (mReleasedPoint.my - mPressedPoint.my > 50) {
                ProcessCode(KeyCode.DOWN);
            } else if (mReleasedPoint.my - mPressedPoint.my < -50) {
                ProcessCode(KeyCode.UP);
            }
        }
        if (Math.abs(mPressedPoint.my - mReleasedPoint.my) < 50) {
            if (mReleasedPoint.mx - mPressedPoint.mx > 50) {
                ProcessCode(KeyCode.RIGHT);
            } else if (mReleasedPoint.mx - mPressedPoint.mx < -50) {
                ProcessCode(KeyCode.LEFT);
            }
        }
    }

    public X2048Service(X2048Controller x2048Controller) {
        this.x2048Controller = x2048Controller;
    }

    public static class Point {
        int mx;
        int my;

        public Point(int x, int y) {
            mx = x;
            my = y;
        }
    }

}

enum State {
    RUNNING,
    GAMEOVER,
    GAMESUCCESS,
}

class MoveAnime {
    StackPane mPane;
    double mFromX;
    double mFromY;
    double mMoveX;
    double mMoveY;

    MoveAnime(X2048Service ctrl, StackPane pane, int dx, int dy) {
        mPane = pane;
        mFromX = pane.getLayoutX();
        mFromY = pane.getLayoutY();
        mMoveX = ctrl.width / ctrl.size * dx;
        mMoveY = ctrl.height / ctrl.size * dy;
    }
}

