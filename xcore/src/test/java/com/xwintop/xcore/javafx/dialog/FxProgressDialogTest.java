package com.xwintop.xcore.javafx.dialog;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.xwintop.xcore.javafx.helper.LayoutHelper.button;
import static com.xwintop.xcore.javafx.helper.LayoutHelper.hbox;

public class FxProgressDialogTest {

    public static void main(String[] args) {
        Application.launch(FxProgressDialogTestApp.class, args);
    }

    public static class FxProgressDialogTestApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(
            hbox(50, 10, Pos.CENTER,
                button("展示数字进度", () -> startProgress1(primaryStage, false)),
                button("展示百分比进度", () -> startProgress1(primaryStage, true)),
                button("不展示进度", () -> startProgress2(primaryStage))
            )
        ));
        primaryStage.show();
    }

    private void startProgress2(Stage primaryStage) {
        ProgressTask task = new ProgressTask() {
            @Override
            protected void execute() throws Exception {
                // 只要不调用 updateProgress()，就不会展示进度
                Thread.sleep(3000);
                System.out.println("执行完毕。");
            }
        };

        FxProgressDialog dialog = FxProgressDialog.create(primaryStage, task, "请稍候...");
        dialog.show();
    }

    private void startProgress1(Stage primaryStage, boolean showPercentage) {

        ProgressTask progressTask = new ProgressTask() {
            @Override
            protected void execute() throws Exception {
                int current = 0, max = 100;
                try {
                    while (current < max) {
                        updateProgress(current, max);
                        current += 4;
                        Thread.sleep(200);
                    }
                    System.out.println("执行完毕。");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        progressTask.setOnCancelled(event -> System.out.println("用户取消了"));

        FxProgressDialog fxProgressDialog =
            FxProgressDialog.create(primaryStage, progressTask, "正在执行某个重要任务...");

        fxProgressDialog.setShowAsPercentage(showPercentage);
        fxProgressDialog.show();
    }}
}
