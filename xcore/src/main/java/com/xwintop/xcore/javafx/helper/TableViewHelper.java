package com.xwintop.xcore.javafx.helper;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseEvent;

/**
 * 一次性完成对 TableView 的所有配置
 *
 * @author yidin
 */
public class TableViewHelper<T> {

    public static <T> TableViewHelper<T> of(TableView<T> tableView) {
        return new TableViewHelper<>(tableView);
    }

    private TableView<T> tableView;

    private TableViewHelper(TableView<T> tableView) {
        this.tableView = tableView;
    }

    public TableViewHelper<T> addStrColumn(String text, Function<T, String> toString) {
        this.tableView.getColumns().add(TableViewHelper.createStrColumn(text, toString));
        return this;
    }

    public TableViewHelper<T> addStrPropertyColumn(String text, Function<T, ObservableValue<String>> ob) {
        this.tableView.getColumns().add(TableViewHelper.createStrPropColumn(text, ob));
        return this;
    }

    public TableViewHelper<T> addNodeColumn(String text, Function<T, Node> toNode) {
        TableColumn<T, T> column = new TableColumn<>(text);
        column.setCellFactory(col -> new TableCell<T, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);

                if (!empty) {
                    setGraphic(toNode.apply(item));
                }
            }
        });
        this.tableView.getColumns().add(column);
        return this;
    }

    public TableViewHelper<T> addIntColumn(String text, Function<T, Integer> toInt) {
        this.tableView.getColumns().add(TableViewHelper.createIntColumn(text, toInt));
        return this;
    }

    public TableViewHelper<T> addIntPropertyColumn(String text, Function<T, ObservableValue<Integer>> ob) {
        this.tableView.getColumns().add(TableViewHelper.createIntPropColumn(text, ob));
        return this;
    }

    public TableViewHelper<T> addDoubleColumn(String text, Function<T, Double> toDouble) {
        this.tableView.getColumns().add(TableViewHelper.createDoubleColumn(text, toDouble));
        return this;
    }

    public TableViewHelper<T> addDoublePropertyColumn(String text, Function<T, ObservableValue<Double>> ob) {
        this.tableView.getColumns().add(TableViewHelper.createDoublePropColumn(text, ob));
        return this;
    }

    public TableViewHelper<T> addDateColumn(String text, Function<T, Date> toDate, String datePattern) {
        this.tableView.getColumns().add(TableViewHelper.createDateColumn(text, toDate, datePattern));
        return this;
    }

    public <R> TableViewHelper<T> addPropColumn(String text, Function<T, ObservableValue<R>> func) {
        this.tableView.getColumns().add(TableViewHelper.createPropColumn(text, func));
        return this;
    }

    public TableViewHelper<T> addCheckboxColumn(
            String text, Function<T, Boolean> toBoolean, Consumer<T> onItemClick) {

        this.tableView.getColumns().add(TableViewHelper.createCheckboxColumn(text, toBoolean, onItemClick));
        return this;
    }

    public TableViewHelper<T> setColumnWidths(double... widths) {
        TableViewHelper.setColumnWidths(this.tableView, widths);
        return this;
    }

    public TableViewHelper<T> setOnItemSelect(Consumer<T> itemConsumer) {
        TableViewHelper.setOnItemSelect(tableView, itemConsumer);
        return this;
    }

    public TableViewHelper<T> setOnItemChange(BiConsumer<T, T> itemConsumer) {
        TableViewHelper.setOnItemSelectChange(tableView, itemConsumer);
        return this;
    }

    public TableViewHelper<T> setOnItemDoubleClick(Consumer<T> func) {
        TableViewHelper.setOnClicked(this.tableView, 2, func);
        return this;
    }

    public TableViewHelper<T> setOnItemClick(Consumer<T> func) {
        TableViewHelper.setOnClicked(this.tableView, 1, func);
        return this;
    }

    public TableViewHelper<T> setItemRelatedNodes(Node... nodes) {
        TableViewHelper.setItemRelatedNodes(this.tableView, nodes);
        return this;
    }

    public TableViewHelper<T> setOnTableEmpty(Runnable action) {
        TableViewHelper.setOnTableEmpty(this.tableView, action);
        return this;
    }

    //////////////////////////////////////////////////////////////

    public static <S, T> void setColumnValueFactory(
        TableColumn<S, T> tableColumn,
        Function<S, T> function
    ) {
        tableColumn.setCellValueFactory(
            f -> new ReadOnlyObjectWrapper<>(function.apply(f.getValue())));
    }

    public static <T> TableColumn<T, String> createStrColumn(String text, Function<T, String> toString) {
        TableColumn<T, String> tableColumn = new TableColumn<>(text);
        tableColumn.setCellValueFactory(param -> new SimpleStringProperty(toString.apply(param.getValue())));
        return tableColumn;
    }

    public static <T> TableColumn<T, String> createStrPropColumn(
        String text, Function<T, ObservableValue<String>> toStringProperty) {

        TableColumn<T, String> tableColumn = new TableColumn<>(text);
        tableColumn.setCellValueFactory(param -> toStringProperty.apply(param.getValue()));
        return tableColumn;
    }

    public static <T> TableColumn<T, Integer> createIntColumn(String text, Function<T, Integer> toInt) {
        TableColumn<T, Integer> tableColumn = new TableColumn<>(text);
        tableColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(toInt.apply(param.getValue())));
        return tableColumn;
    }

    public static <T> TableColumn<T, Integer> createIntPropColumn(
        String text, Function<T, ObservableValue<Integer>> toIntProperty) {

        TableColumn<T, Integer> tableColumn = new TableColumn<>(text);
        tableColumn.setCellValueFactory(cell -> toIntProperty.apply(cell.getValue()));
        return tableColumn;
    }

    private static <T> TableColumn<T, ?> createDoubleColumn(String text, Function<T, Double> toDouble) {
        TableColumn<T, Double> tableColumn = new TableColumn<>(text);
        tableColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(toDouble.apply(param.getValue())));
        return tableColumn;
    }

    private static <T> TableColumn<T, ?> createDoublePropColumn(
        String text, Function<T, ObservableValue<Double>> toDoubleProperty) {

        TableColumn<T, Double> tableColumn = new TableColumn<>(text);
        tableColumn.setCellValueFactory(cell -> toDoubleProperty.apply(cell.getValue()));
        return tableColumn;
    }

    public static <T, R> TableColumn<T, R> createPropColumn(
        String text, Function<T, ObservableValue<R>> toValueProperty) {

        TableColumn<T, R> tableColumn = new TableColumn<>(text);
        tableColumn.setCellValueFactory(cell -> toValueProperty.apply(cell.getValue()));
        return tableColumn;
    }

    @SuppressWarnings("unchecked")
    public static <T> TableColumn<T, Boolean> createCheckboxColumn(
        String text, Function<T, Boolean> converter, Consumer<T> onItemClick) {

        TableColumn<T, Boolean> tableColumn = new TableColumn<>(text);
        tableColumn.setCellValueFactory(param -> new SimpleBooleanProperty(converter.apply(param.getValue())));
        tableColumn.setCellFactory(list -> {
            CheckBoxTableCell<T, Boolean> cell = new CheckBoxTableCell<>(null, null);
            if (onItemClick != null) {
                cell.setOnMouseClicked(event -> {
                    onItemClick.accept((T) cell.getTableRow().getItem());
                    if (tableColumn.getTableView() != null) {
                        tableColumn.getTableView().refresh();
                    }
                });
            }
            return cell;
        });
        return tableColumn;
    }

    public static <T, R> TableColumn<T, R> createColumn(String text, Function<T, R> converter) {
        TableColumn<T, R> tableColumn = new TableColumn<>(text);
        tableColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(converter.apply(param.getValue())));
        return tableColumn;
    }

    public static <T> TableColumn<T, String> createDateColumn(String text, Function<T, Date> toDate, String datePattern) {
        TableColumn<T, String> tableColumn = new TableColumn<>(text);
        tableColumn.setCellValueFactory(param -> {
            Date date = toDate.apply(param.getValue());
            if (date == null) {
                return new SimpleStringProperty("");
            } else {
                return new SimpleStringProperty(new SimpleDateFormat(datePattern).format(date));
            }
        });
        return tableColumn;
    }

    @SafeVarargs
    public static <T> void setColumns(TableView<T> tableView, TableColumn<T, ?>... columns) {
        tableView.getColumns().addAll(columns);
    }

    public static void setColumnWidths(TableView tableView, double... widths) {
        for (int i = 0; i < widths.length; i++) {
            double width = widths[i];
            TableColumn column = (TableColumn) tableView.getColumns().get(i);
            column.setPrefWidth(width);
        }
    }

    public static <T> void setOnItemSelect(TableView<T> tableView, Consumer<T> tConsumer) {
        tableView.getSelectionModel().selectedItemProperty().addListener((_ob, _old, _new) -> {
            if (_new != null) {
                tConsumer.accept(_new);
            }
        });
    }

    public static <T> void setOnItemSelectChange(TableView<T> tableView, BiConsumer<T, T> biTConsumer) {
        tableView.getSelectionModel().selectedItemProperty()
            .addListener((_ob, _old, _new) -> biTConsumer.accept(_old, _new));
    }

    public static <T> void setOnClicked(TableView<T> tableView, int clickCount, Consumer<T> tConsumer) {
        tableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == clickCount) {
                T selectedItem = tableView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    tConsumer.accept(selectedItem);
                    tableView.refresh();
                }
            }
        });
    }

    public static <T> void setOnTableEmpty(TableView<T> tableView, Runnable action) {
        tableView.getItems().addListener((ListChangeListener<T>) c -> {
            if (tableView.getItems().isEmpty()) {
                action.run();
            }
        });
    }

    public static void setItemRelatedNodes(TableView tableView, Node... nodes) {
        for (Node node : nodes) {
            node.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        }
    }
}
