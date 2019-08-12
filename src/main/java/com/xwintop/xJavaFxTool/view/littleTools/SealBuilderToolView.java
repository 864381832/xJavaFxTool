package com.xwintop.xJavaFxTool.view.littleTools;

import com.jfoenix.controls.JFXCheckBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: SealBuilderToolView
 * @Description: 印章生成工具
 * @author: xufeng
 * @date: 2019/8/12 0012 23:04
 */

@Getter
@Setter
public abstract class SealBuilderToolView implements Initializable {
    @FXML
    protected TabPane sealTypeTabPane;
    @FXML
    protected TextField contentTextField;
    @FXML
    protected JFXCheckBox isBoldCheckBox;
    @FXML
    protected ChoiceBox<String> fontFamilyChoiceBox;
    @FXML
    protected Spinner<Integer> fontSizeSpinner;
    @FXML
    protected Spinner<Double> fontSpaceSpinner;
    @FXML
    protected Spinner<Integer> marginSizeSpinner;
    @FXML
    protected TextField contentTextField1;
    @FXML
    protected JFXCheckBox isBoldCheckBox1;
    @FXML
    protected ChoiceBox<String> fontFamilyChoiceBox1;
    @FXML
    protected Spinner<Integer> fontSizeSpinner1;
    @FXML
    protected Spinner<Double> fontSpaceSpinner1;
    @FXML
    protected Spinner<Integer> marginSizeSpinner1;
    @FXML
    protected TextField contentTextField2;
    @FXML
    protected JFXCheckBox isBoldCheckBox2;
    @FXML
    protected ChoiceBox<String> fontFamilyChoiceBox2;
    @FXML
    protected Spinner<Integer> fontSizeSpinner2;
    @FXML
    protected Spinner<Double> fontSpaceSpinner2;
    @FXML
    protected Spinner<Integer> marginSizeSpinner2;
    @FXML
    protected TextField contentTextField3;
    @FXML
    protected JFXCheckBox isBoldCheckBox3;
    @FXML
    protected ChoiceBox<String> fontFamilyChoiceBox3;
    @FXML
    protected Spinner<Integer> fontSizeSpinner3;
    @FXML
    protected Spinner<Double> fontSpaceSpinner3;
    @FXML
    protected Spinner<Integer> marginSizeSpinner3;
    @FXML
    protected Spinner<Integer> lineSizeSpinner;
    @FXML
    protected Spinner<Integer> circleWidthSpinner;
    @FXML
    protected Spinner<Integer> circleHeightSpinner;
    @FXML
    protected Spinner<Integer> lineSizeSpinner1;
    @FXML
    protected Spinner<Integer> circleWidthSpinner1;
    @FXML
    protected Spinner<Integer> circleHeightSpinner1;
    @FXML
    protected Spinner<Integer> lineSizeSpinner2;
    @FXML
    protected Spinner<Integer> circleWidthSpinner2;
    @FXML
    protected Spinner<Integer> circleHeightSpinner2;

    @FXML
    protected TextField contentTextField4;
    @FXML
    protected JFXCheckBox isBoldCheckBox4;
    @FXML
    protected ChoiceBox<String> fontFamilyChoiceBox4;
    @FXML
    protected Spinner<Integer> fontSizeSpinner4;
    @FXML
    protected Spinner<Double> fontSpaceSpinner4;
    @FXML
    protected Spinner<Integer> marginSizeSpinner4;
    @FXML
    protected Spinner<Integer> lineSizeSpinner3;

    @FXML
    protected ColorPicker onColorColorPicker;
    @FXML
    protected Spinner<Integer> imageSizeSpinner;
    @FXML
    protected Button saveButton;
    @FXML
    protected ImageView codeImageView;

}
