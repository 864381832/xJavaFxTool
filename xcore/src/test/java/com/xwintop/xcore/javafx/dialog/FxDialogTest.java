package com.xwintop.xcore.javafx.dialog;

import com.xwintop.xcore.javafx.FxApp;
import com.xwintop.xcore.javafx.helper.LayoutHelper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import static com.xwintop.xcore.javafx.helper.LayoutHelper.button;
import static javafx.application.Application.launch;

public class FxDialogTest {

    public static void main(String[] args) {
        launch(FxDialogTextApp.class, args);
    }

    public static class FxDialogTextApp extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            FxApp.init(primaryStage, "/icon.png");

            primaryStage.setScene(new Scene(new BorderPane(
                LayoutHelper.vbox(10, 10,
                    button("打开带按钮的对话框", () -> openDialog(primaryStage)),
                    button("打开不带按钮的对话框", () -> openDialog2(primaryStage)),
                    button("不可通过顶部按钮关闭的对话框", () -> openDialog3(primaryStage))
                )
            ), 400, 300));
            primaryStage.show();
        }

        private void openDialog(Stage primaryStage) {

            // 创建对话框
            FxDialog<FxDialogTestController> dialog = new FxDialog<FxDialogTestController>()
                .setOwner(primaryStage)
                .setBodyFxml(getClass().getClassLoader(), "/sample-dialog-body.fxml")
                .setTitle("对话框标题")
                .setButtonTypes(ButtonType.OK, ButtonType.CANCEL);

            // 通过 Controller 对象初始化对话框内容
            FxDialogTestController controller = dialog.show();
            controller.initName("这是一个名字");

            // 用户如果修改了对话框内容，仍然通过 Controller 来获取
            // 这样避免了父窗体代码直接访问子窗体元素，职责混乱
            dialog.setButtonHandler(ButtonType.OK, (actionEvent, stage) -> {
                System.out.println("新的名字：" + controller.getName());
                stage.close();
            });

            dialog.setButtonHandler(ButtonType.CANCEL, (actionEvent, stage) -> {

            });
        }

        private void openDialog2(Stage primaryStage) {

            // 创建不带按钮的对话框
            FxDialog<FxDialogTestController> dialog = new FxDialog<FxDialogTestController>()
                .setOwner(primaryStage)
                .setBodyFxml(getClass().getClassLoader(), "/sample-dialog-body.fxml")
                .setTitle("对话框标题");

            // 通过 Controller 对象初始化对话框内容
            FxDialogTestController controller = dialog.show();
            controller.initName("这是一个名字");
        }

        private void openDialog3(Stage primaryStage) {

            // 创建不带按钮的对话框
            FxDialog<FxDialogTestController> dialog = new FxDialog<FxDialogTestController>()
                .setOwner(primaryStage)
                .setCloseable(false)
                .setBodyFxml(getClass().getClassLoader(), "/sample-dialog-body.fxml")
                .setButtonTypes(ButtonType.CLOSE)
                .setButtonHandler(ButtonType.CLOSE, (event, stage) -> stage.close())
                .setTitle("对话框");

            dialog.show();
        }
    }
}
