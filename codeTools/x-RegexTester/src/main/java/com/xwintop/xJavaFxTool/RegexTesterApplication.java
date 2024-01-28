/**
 *    Copyright (c) [2019] [xufeng]
 *    [x-RegexTester] is licensed under Mulan PSL v2.
 *    You can use this software according to the terms and conditions of the Mulan PSL v2.
 *    You may obtain a copy of Mulan PSL v2 at:
 *             http://license.coscl.org.cn/MulanPSL2
 *    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *    See the Mulan PSL v2 for more details.
 */
package com.xwintop.xJavaFxTool;

import com.xwintop.xcore.util.javafx.JavaFxSystemUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class RegexTesterApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fXMLLoader = RegexTesterApplication.getFXMLLoader();
        ResourceBundle resourceBundle = fXMLLoader.getResources();
        Parent root = fXMLLoader.load();
        primaryStage.setResizable(true);
        primaryStage.setTitle(resourceBundle.getString("Title"));
        primaryStage.getIcons().add(new Image("/images/RegexTesterIcon.png"));
        double[] screenSize = JavaFxSystemUtil.getScreenSizeByScale(0.74D, 0.8D);
        primaryStage.setScene(new Scene(root, screenSize[0], screenSize[1]));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }

    public static FXMLLoader getFXMLLoader() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.RegexTester");
        URL url = RegexTesterApplication.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/codeTools/RegexTester.fxml");
        FXMLLoader fXMLLoader = new FXMLLoader(url, resourceBundle);
        return fXMLLoader;
    }
}