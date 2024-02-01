package com.xwintop.xJavaFxTool.services.developTools;

import com.xwintop.xJavaFxTool.controller.developTools.LuytenToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class LuytenToolService {
    private LuytenToolController luytenToolController;

    public LuytenToolService(LuytenToolController luytenToolController) {
        this.luytenToolController = luytenToolController;
    }

    public void openLuytenOnJavaFx() {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            MainWindow mainWindow = new MainWindow();
//            Model model = new Model(mainWindow);
//            luytenToolController.getLuytenSwingNode().setContent(model);
//        });
    }
}