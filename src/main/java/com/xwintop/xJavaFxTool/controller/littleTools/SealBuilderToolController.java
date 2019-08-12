package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xJavaFxTool.services.littleTools.SealBuilderToolService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.littleTools.SealBuilderToolView;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * @ClassName: SealBuilderToolController
 * @Description: 印章生成工具
 * @author: xufeng
 * @date: 2019/8/12 0012 23:04
 */

@Getter
@Setter
@Slf4j
public class SealBuilderToolController extends SealBuilderToolView {
    private SealBuilderToolService sealBuilderToolService = new SealBuilderToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        onColorColorPicker.setValue(Color.RED);
        JavaFxViewUtil.setSpinnerValueFactory(fontSizeSpinner, 0, Integer.MAX_VALUE, 35);
        JavaFxViewUtil.setSpinnerValueFactory(fontSizeSpinner1, 0, Integer.MAX_VALUE, 0);
        JavaFxViewUtil.setSpinnerValueFactory(fontSizeSpinner2, 0, Integer.MAX_VALUE, 22);
        JavaFxViewUtil.setSpinnerValueFactory(fontSizeSpinner3, 0, Integer.MAX_VALUE, 100);
        JavaFxViewUtil.setSpinnerValueFactory(fontSpaceSpinner, 0, Double.MAX_VALUE, 35);
        JavaFxViewUtil.setSpinnerValueFactory(fontSpaceSpinner1, 0, Double.MAX_VALUE, 0);
        JavaFxViewUtil.setSpinnerValueFactory(fontSpaceSpinner2, 0, Double.MAX_VALUE, 10);
        JavaFxViewUtil.setSpinnerValueFactory(fontSpaceSpinner3, 0, Double.MAX_VALUE, 0);
        JavaFxViewUtil.setSpinnerValueFactory(marginSizeSpinner, 0, Integer.MAX_VALUE, 10);
        JavaFxViewUtil.setSpinnerValueFactory(marginSizeSpinner1, 0, Integer.MAX_VALUE, 0);
        JavaFxViewUtil.setSpinnerValueFactory(marginSizeSpinner2, 0, Integer.MAX_VALUE, 68);
        JavaFxViewUtil.setSpinnerValueFactory(marginSizeSpinner3, 0, Integer.MAX_VALUE, 0);
        JavaFxViewUtil.setSpinnerValueFactory(lineSizeSpinner, 0, Integer.MAX_VALUE, 5);
        JavaFxViewUtil.setSpinnerValueFactory(lineSizeSpinner1, 0, Integer.MAX_VALUE, 0);
        JavaFxViewUtil.setSpinnerValueFactory(lineSizeSpinner2, 0, Integer.MAX_VALUE, 0);
        JavaFxViewUtil.setSpinnerValueFactory(circleWidthSpinner, 0, Integer.MAX_VALUE, 140);
        JavaFxViewUtil.setSpinnerValueFactory(circleWidthSpinner1, 0, Integer.MAX_VALUE, 0);
        JavaFxViewUtil.setSpinnerValueFactory(circleWidthSpinner2, 0, Integer.MAX_VALUE, 0);
        JavaFxViewUtil.setSpinnerValueFactory(circleHeightSpinner, 0, Integer.MAX_VALUE, 140);
        JavaFxViewUtil.setSpinnerValueFactory(circleHeightSpinner1, 0, Integer.MAX_VALUE, 0);
        JavaFxViewUtil.setSpinnerValueFactory(circleHeightSpinner2, 0, Integer.MAX_VALUE, 0);

        JavaFxViewUtil.setSpinnerValueFactory(fontSizeSpinner4, 0, Integer.MAX_VALUE, 120);
        JavaFxViewUtil.setSpinnerValueFactory(fontSpaceSpinner4, 0, Double.MAX_VALUE, 0);
        JavaFxViewUtil.setSpinnerValueFactory(marginSizeSpinner4, 0, Integer.MAX_VALUE, 0);
        JavaFxViewUtil.setSpinnerValueFactory(lineSizeSpinner3, 0, Integer.MAX_VALUE, 16);

        JavaFxViewUtil.setSpinnerValueFactory(imageSizeSpinner, 0, Integer.MAX_VALUE, 300);

        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontFamilyChoiceBox.getItems().addAll(fontNames);
        fontFamilyChoiceBox.setValue("宋体");
        fontFamilyChoiceBox1.getItems().addAll(fontNames);
        fontFamilyChoiceBox1.setValue("宋体");
        fontFamilyChoiceBox2.getItems().addAll(fontNames);
        fontFamilyChoiceBox2.setValue("宋体");
        fontFamilyChoiceBox3.getItems().addAll(fontNames);
        fontFamilyChoiceBox3.setValue("宋体");
        fontFamilyChoiceBox4.getItems().addAll(fontNames);
        fontFamilyChoiceBox4.setValue("宋体");
    }

    private void initEvent() {
        Spinner[] spinners = new Spinner[]{imageSizeSpinner, fontSizeSpinner, fontSizeSpinner1, fontSizeSpinner2, fontSizeSpinner3,
                fontSpaceSpinner, fontSpaceSpinner1, fontSpaceSpinner2, fontSpaceSpinner3,
                marginSizeSpinner, marginSizeSpinner1, marginSizeSpinner2, marginSizeSpinner3,
                lineSizeSpinner, lineSizeSpinner1, lineSizeSpinner2,
                circleWidthSpinner, circleWidthSpinner1, circleWidthSpinner2,
                circleHeightSpinner, circleHeightSpinner1, circleHeightSpinner2,
                fontSizeSpinner4, fontSpaceSpinner4, marginSizeSpinner4, lineSizeSpinner3};
        for (Spinner spinner : spinners) {
            spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                builderAction();
            });
        }
    }

    private void initService() {
    }

    @FXML
    private void saveAction(ActionEvent event) throws Exception {
        String fileName = "x" + DateFormatUtils.format(new Date(), "yyyyMMddHHmm") + ".png";
        File file = FileChooserUtil.chooseSaveFile(fileName, new FileChooser.ExtensionFilter("All Images", "*.*"), new FileChooser.ExtensionFilter("PNG", "*.png"));
        if (file != null) {
            ImageIO.write(SwingFXUtils.fromFXImage(codeImageView.getImage(), null), "PNG", file);
            TooltipUtil.showToast("保存图片成功,图片在：" + file.getPath());
        }
    }

    @FXML
    private void builderAction() {
        try {
            sealBuilderToolService.builderAction();
        } catch (Exception e) {
            log.error("生成错误：", e);
        }
    }

}