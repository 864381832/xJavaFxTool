package com.xwintop.xJavaFxTool.controller.littleTools;

import com.tulskiy.keymaster.common.Provider;
import com.xwintop.xJavaFxTool.utils.CorrectionLevel;
import com.xwintop.xJavaFxTool.utils.QRCodeUtil;
import com.xwintop.xJavaFxTool.utils.ScreenShoter;
import com.xwintop.xJavaFxTool.view.littleTools.QRCodeBuilderView;
import com.xwintop.xcore.javafx.FxApp;
import com.xwintop.xcore.javafx.helper.ChoiceBoxHelper;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;
import javax.swing.KeyStroke;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 二维码生成工具
 *
 * @author xufeng
 * @since 2019/4/25 0025 23:26
 */

@Slf4j
public class QRCodeBuilderController extends QRCodeBuilderView {

    private Provider provider;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
    }

    private void initView() {

        initChoiceBox();

        codeFormatChoiceBox.getItems().addAll("utf-8", "gb2312", "ISO-8859-1", "US-ASCII", "utf-16");
        codeFormatChoiceBox.setValue("utf-8");
        onColorColorPicker.setValue(Color.BLACK);
        offColorColorPicker.setValue(Color.WHITE);
        marginChoiceBox.getItems().addAll(1, 2, 3, 4);
        marginChoiceBox.setValue(1);
        formatImageChoiceBox.getItems().addAll("png", "jpg", "gif", "jpeg", "bmp");
        formatImageChoiceBox.setValue("png");
    }

    private void initChoiceBox() {
        errorCorrectionLevelChoiceBox.getSelectionModel().selectedItemProperty()
            .addListener((_o, _v, newValue) -> logoSlider.setMax(newValue.getMaxOverlay()));

        ChoiceBoxHelper.setContentDisplay(
            errorCorrectionLevelChoiceBox, CorrectionLevel.class, CorrectionLevel::getName
        );

        errorCorrectionLevelChoiceBox.setValue(CorrectionLevel.H);
    }

    private void initEvent() {

        FxApp.primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            try {
                provider = Provider.getCurrentProvider(false);
                provider.register(KeyStroke.getKeyStroke('S', InputEvent.ALT_DOWN_MASK), hotKey -> {
                    snapshotAction(null);
                });
                TooltipUtil.showToast("按alt+s可快速截图识别！！！");
            } catch (Exception e) {
                log.error("热键注册失败", e);
            }
        });

        contentTextField.textProperty().addListener((_ob, _old, _new) -> Platform.runLater(() -> builderAction(null)));
    }

    @FXML
    private void builderAction(ActionEvent event) {
        if (StringUtils.isEmpty(contentTextField.getText())) {
            return;
        }
        try {
            Image image = QRCodeUtil.toImage(contentTextField.getText(), (int) codeImageView.getFitWidth(),
                (int) codeImageView.getFitHeight(), codeFormatChoiceBox.getValue(),
                errorCorrectionLevelChoiceBox.getValue().getErrorCorrectionLevel(),
                marginChoiceBox.getValue(), onColorColorPicker.getValue(),
                offColorColorPicker.getValue(), formatImageChoiceBox.getValue());
            codeImageView.setImage(image);
            if (logoCheckBox.isSelected() && codeImageView1.getImage() != null) {
                Image image1 = QRCodeUtil.encodeImgLogo(image, codeImageView1.getImage(), (int) logoSlider.getValue());
                codeImageView.setImage(image1);
            }
        } catch (Exception e) {
            log.error("生成图片失败", e);
        }
    }

    @FXML
    private void snapshotAction(ActionEvent event) {
        // 默认情况下，Fx运行时会在最后一个stage的close(或hide)后自动关闭，即自动调用Application.stop()
        // 除非通过Platform.setImplicitExit(false)取消这个默认行为。这样,即使所有Fx窗口关闭（或隐藏）,Fx运行时还在正常运行
        Platform.setImplicitExit(false);
        if (FxApp.primaryStage.isShowing()) {
            Platform.runLater(() -> {
                FxApp.primaryStage.hide();
            });
        }
        new ScreenShoter(this);
    }

    @FXML
    private void snapshotDesktopAction(ActionEvent event) throws Exception {
        Platform.setImplicitExit(false);
        try {
            FxApp.primaryStage.hide();
            Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
            Robot robot = new Robot();
            BufferedImage screenImg = robot.createScreenCapture(
                new Rectangle(0, 0, SCREEN_SIZE.width, SCREEN_SIZE.height));
            FxApp.primaryStage.show();
            String code = QRCodeUtil.toDecode(screenImg);
            if (StringUtils.isNotEmpty(code)) {
                contentTextField.setText(code);
            } else {
                Platform.runLater(() -> {
                    TooltipUtil.showToast("未识别到二维码。");
                });
            }
        } catch (Exception e) {
            TooltipUtil.showToast("发生异常:" + e.getMessage());
        } finally {
            Platform.setImplicitExit(true);
        }
    }

    @FXML
    private void saveAction(ActionEvent event) throws Exception {
        String fileName =
            "x" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "." + formatImageChoiceBox.getValue();
        File file = FileChooserUtil.chooseSaveFile(fileName, new FileChooser.ExtensionFilter("All Images", "*.*"),
            new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("PNG", "*.png"),
            new FileChooser.ExtensionFilter("gif", "*.gif"), new FileChooser.ExtensionFilter("jpeg", "*.jpeg"),
            new FileChooser.ExtensionFilter("bmp", "*.bmp"));
        if (file != null) {
            String[] fileType = file.getPath().split("\\.");
            ImageIO.write(SwingFXUtils.fromFXImage(codeImageView.getImage(), null), fileType[fileType.length - 1],
                file);
            TooltipUtil.showToast("保存图片成功,图片在：" + file.getPath());
        }
    }

    @FXML
    private void logoAction(ActionEvent event) throws Exception {
        File file = FileChooserUtil.chooseFile(new FileChooser.ExtensionFilter("All Images", "*.*"),
            new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("PNG", "*.png"),
            new FileChooser.ExtensionFilter("gif", "*.gif"), new FileChooser.ExtensionFilter("jpeg", "*.jpeg"),
            new FileChooser.ExtensionFilter("bmp", "*.bmp"));
        if (file != null) {
            Image image = SwingFXUtils.toFXImage(ImageIO.read(file), null);
            codeImageView1.setImage(image);
        }
    }

    public void snapshotActionCallBack(Image image) {
        Platform.runLater(() -> {
            FxApp.primaryStage.show();
        });
        codeImageView1.setImage(image);
        String code = QRCodeUtil.toDecode(image);
        if (StringUtils.isNotEmpty(code)) {
            contentTextField.setText(code);
        }
        Platform.setImplicitExit(true);
    }

    /**
     * 父控件被移除前调用
     */
    public void onCloseRequest(javafx.event.Event event) {
        try {
            if (provider != null) {
                provider.reset();
                provider.stop();
            }
        } catch (Exception e) {
            log.error("停止快捷键监听失败：", e);
        }
    }
}
