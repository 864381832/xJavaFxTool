package com.xwintop.xJavaFxTool.controller.littleTools;

import com.xwintop.xcore.javafx.control.IntegerSpinner;
import com.xwintop.xcore.javafx.control.ToggleGrid;
import com.xwintop.xcore.util.KeyValue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class FragmentController {

    @FXML
    private ToggleGrid<String, Integer> tgValues;

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private RadioButton rdSelectByRule;

    @FXML
    private RadioButton rdSelectFreely;

    @FXML
    private IntegerSpinner spnStartValue;

    @FXML
    private IntegerSpinner spnInterval;

    private String clearMark = "*";

    private String allMark = "*";

    private int itemsCount;

    public void setOnValueChanged(Runnable onValueChanged) {
        tgValues.addSelectionUpdatedListener(onValueChanged);
    }

    public void setAllMark(String allMark) {
        this.allMark = allMark;
    }

    public void setClearMark(String clearMark) {
        this.clearMark = clearMark;
    }

    /**
     * 额外初始化
     */
    public void initialize(int minValue, int maxValue, String unit) {
        List<KeyValue<String, Integer>> items = new ArrayList<>();
        for (int i = minValue; i <= maxValue; i++) {
            items.add(new KeyValue<>(String.valueOf(i), i));
        }
        initialize(items, unit);
    }

    /**
     * 额外初始化
     */
    public void initialize(List<KeyValue<String, Integer>> items, String unit) {

        forEachRadioRoot((radioButton, radioRoot) -> {
            Set<Node> labels = radioRoot.lookupAll("Label");
            labels.forEach(node ->
                ((Label) node).setText(((Label) node).getText().replace("{{unit}}", unit))
            );
        });

        items.sort(Comparator.comparing(KeyValue::getValue));
        int minValue = items.get(0).getValue();
        int maxValue = items.get(items.size() - 1).getValue();

        this.itemsCount = maxValue - minValue + 1;
        this.spnStartValue.getSpinnerValueFactory().setMin(minValue);
        this.spnStartValue.getSpinnerValueFactory().setMax(maxValue);
        this.spnInterval.getSpinnerValueFactory().setMin(1);
        this.spnInterval.getSpinnerValueFactory().setMax(maxValue);

        setItems(items);
    }

    private void setItems(List<KeyValue<String, Integer>> items) {
        items.forEach(item -> tgValues.addCell(item));
    }

    public void initialize() {

        // 当不是自由选择时，禁用 tgValues
        tgValues.disableProperty().bind(rdSelectFreely.selectedProperty().not());

        // 当操作 RadioButton 中的元素时，自动选择该 RadioButton
        forEachRadioRoot((radioButton, radioRoot) -> {
            radioRoot.getChildrenUnmodifiable().forEach(node -> {
                if (node.isFocusTraversable()) {
                    node.focusedProperty().addListener((_ob, _old, focused) -> {
                        if (focused) {
                            radioButton.setSelected(true);
                        }
                    });
                }
            });
        });

        // 当操作某些元素时，立刻更新 tgValues 的展示
        spnStartValue.valueProperty().addListener((observable, oldValue, newValue) -> onValueChanged());
        spnInterval.valueProperty().addListener((observable, oldValue, newValue) -> onValueChanged());
        rdSelectByRule.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                onValueChanged();
            }
        });
    }

    private void forEachRadioRoot(BiConsumer<RadioButton, Parent> consumer) {
        toggleGroup.getToggles().forEach(toggle -> {
            RadioButton radioButton = (RadioButton) toggle;
            if (radioButton.getGraphic() instanceof Parent) {
                Parent radioRoot = (Parent) radioButton.getGraphic();
                consumer.accept(radioButton, radioRoot);
            }
        });
    }

    private void onValueChanged() {
        this.tgValues.select(n ->
            n.getValue() >= spnStartValue.getValue()
                && (n.getValue() - spnStartValue.getValue()) % spnInterval.getValue() == 0
        );
    }

    public void selectAll() {
        this.tgValues.select(n -> true);
    }

    public void selectNone() {
        this.tgValues.clearSelection();
    }

    public String generateCron() {
        if (rdSelectByRule.isSelected()) {
            return spnStartValue.getValue() + "/" + spnInterval.getValue();
        } else if (tgValues.getSelectedValues().size() == itemsCount) {
            return allMark;
        }else if (tgValues.getSelectedValues().size() == 0) {
            return clearMark;
        } else {
            List<String> valuesText = tgValues
                .getSelectedValues().stream().map(String::valueOf).collect(Collectors.toList());
            return String.join(",", valuesText);
        }
    }

    public void parseCron(String cron) {

    }
}
