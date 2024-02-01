package com.xwintop.xJavaFxTool.controller.developTools;

import com.xwintop.xJavaFxTool.services.developTools.LuytenToolService;
import com.xwintop.xJavaFxTool.view.developTools.LuytenToolView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import us.deathmarine.luyten.Luyten;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
public class LuytenToolController extends LuytenToolView {
    private LuytenToolService luytenToolService = new LuytenToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            MainWindow mainWindow = new MainWindow();
//////                mainWindow.setVisible(true);
//            luytenSwingNode.setContent(mainWindow.getRootPane());
//        });
//        luytenToolService.openLuytenOnJavaFx();
    }

    private void initEvent() {
        Luyten.main(new String[]{});
    }

    private void initService() {
    }

    @FXML
    private void openLuytenAction(ActionEvent event) {
        Luyten.main(new String[]{});
    }
}