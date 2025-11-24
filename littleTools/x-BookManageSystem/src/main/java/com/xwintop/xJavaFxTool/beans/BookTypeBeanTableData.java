package com.xwintop.xJavaFxTool.beans;

import javafx.beans.property.SimpleStringProperty;

public class BookTypeBeanTableData {
    private  SimpleStringProperty bookTypeId;
    private  SimpleStringProperty bookTypeName;
    private  SimpleStringProperty bookTypeDesciption;

    public BookTypeBeanTableData() {}

    public BookTypeBeanTableData(String bookTypeId, String bookTypeName, String bookTypeDesciption) {
        this.bookTypeId = new SimpleStringProperty( bookTypeId);
        this.bookTypeName = new SimpleStringProperty(bookTypeName);
        this.bookTypeDesciption = new SimpleStringProperty(bookTypeDesciption);
    }

    public String getBookTypeId() {
        return bookTypeId.get();
    }

    public SimpleStringProperty bookTypeIdProperty() {
        return bookTypeId;
    }

    public void setBookTypeId(String bookTypeId) {
        this.bookTypeId.set(bookTypeId);
    }

    public String getBookTypeName() {
        return bookTypeName.get();
    }

    public SimpleStringProperty bookTypeNameProperty() {
        return bookTypeName;
    }

    public void setBookTypeName(String bookTypeName) {
        this.bookTypeName.set(bookTypeName);
    }

    public String getBookTypeDesciption() {
        return bookTypeDesciption.get();
    }

    public SimpleStringProperty bookTypeDesciptionProperty() {
        return bookTypeDesciption;
    }

    public void setBookTypeDesciption(String bookTypeDesciption) {
        this.bookTypeDesciption.set(bookTypeDesciption);
    }
}
