package com.xwintop.xcore.javafx.decorator;

import com.xwintop.xcore.javafx.cells.ListCellFactory;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 封装对 ComboBox 的配置
 */
@SuppressWarnings("unchecked")
public class ComboBoxDecorator<T> {

    public static <T> ComboBoxDecorator<T> of(ComboBox<T> comboBox) {
        return new ComboBoxDecorator<>(comboBox);
    }

    private ComboBox<T> comboBox;

    private ComboBoxDecorator(ComboBox<T> comboBox) {
        this.comboBox = comboBox;
    }

    /**
     * 设置下拉选项和当前选项的文本
     */
    public ComboBoxDecorator<T> setToStringFunction(Function<T, String> toString) {
        setCellFactory(new ListCellFactory<T>().withTextFunction(toString));
        return this;
    }

    /**
     * 设置下拉选项的 CellFactory
     */
    public ComboBoxDecorator<T> setCellFactory(Callback<ListView<T>, ListCell<T>> cellFactory) {
        this.comboBox.setCellFactory(cellFactory);
        this.comboBox.setButtonCell(cellFactory.call(null));
        return this;
    }

    /**
     * 设置当前选项的文本
     */
    public ComboBoxDecorator<T> setButtonCellToStringFunction(Function<T, String> toStringFunction) {
        this.comboBox.setButtonCell(new ComboBoxListCell<>(new StringConverter<T>() {
            @Override
            public String toString(T object) {
                return toStringFunction.apply(object);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        }));
        return this;
    }

    /**
     * 设置选项列表
     */
    public ComboBoxDecorator<T> setItems(Collection<T> tCollection) {
        this.comboBox.getItems().setAll(tCollection);
        return this;
    }

    /**
     * 设置选项列表
     */
    @SafeVarargs
    public final ComboBoxDecorator<T> setItems(T... tCollection) {
        this.comboBox.getItems().setAll(tCollection);
        return this;
    }

    /**
     * 根据指定规则设置当前选项
     */
    public ComboBoxDecorator<T> setInitialValue(Predicate<T> valueMatcher) {
        this.comboBox.getSelectionModel().select(
            this.comboBox.getItems().stream().filter(valueMatcher).findFirst().orElse(null)
        );
        return this;
    }

    /**
     * 设置当选项变化时的处理
     */
    public ComboBoxDecorator<T> setOnChange(Consumer<T> newValueConsumer) {
        this.comboBox.getSelectionModel().selectedItemProperty().addListener(
            (_ob, _old, _new) -> newValueConsumer.accept(_new)
        );
        return this;
    }
}
