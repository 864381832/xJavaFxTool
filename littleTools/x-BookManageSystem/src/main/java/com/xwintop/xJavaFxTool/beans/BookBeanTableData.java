package com.xwintop.xJavaFxTool.beans;

import javafx.beans.property.SimpleStringProperty;

public class BookBeanTableData {
    private SimpleStringProperty bookId;
    private  SimpleStringProperty bookName;
    private  SimpleStringProperty bookAuthor;
    private SimpleStringProperty bookAuthorSex;
    private SimpleStringProperty bookPrice;
    private SimpleStringProperty bookDescription;
    private SimpleStringProperty bookType;

    public BookBeanTableData() {}

    public BookBeanTableData(String bookId, String bookName, String bookAuthor, String bookAuthorSex, String bookPrice, String bookDescription, String bookType) {
        this.bookId = new SimpleStringProperty(bookId);
        this.bookName = new SimpleStringProperty(bookName);
        this.bookAuthor = new SimpleStringProperty(bookAuthor);
        this.bookAuthorSex = new SimpleStringProperty(bookAuthorSex);
        this.bookPrice = new SimpleStringProperty(bookPrice);
        this.bookDescription = new SimpleStringProperty(bookDescription);
        this.bookType = new SimpleStringProperty(bookType);
    }

    public String getBookId() {
        return bookId.get();
    }

    public SimpleStringProperty bookIdProperty() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId.set(bookId);
    }

    public String getBookName() {
        return bookName.get();
    }

    public SimpleStringProperty bookNameProperty() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName.set(bookName);
    }

    public String getBookAuthor() {
        return bookAuthor.get();
    }

    public SimpleStringProperty bookAuthorProperty() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor.set(bookAuthor);
    }

    public String getBookAuthorSex() {
        return bookAuthorSex.get();
    }

    public SimpleStringProperty bookAuthorSexProperty() {
        return bookAuthorSex;
    }

    public void setBookAuthorSex(String bookAuthorSex) {
        this.bookAuthorSex.set(bookAuthorSex);
    }

    public String getBookPrice() {
        return bookPrice.get();
    }

    public SimpleStringProperty bookPriceProperty() {
        return bookPrice;
    }

    public void setBookPrice(String bookPrice) {
        this.bookPrice.set(bookPrice);
    }

    public String getBookDescription() {
        return bookDescription.get();
    }

    public SimpleStringProperty bookDescriptionProperty() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription.set(bookDescription);
    }

    public String getBookType() {
        return bookType.get();
    }

    public SimpleStringProperty bookTypeProperty() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType.set(bookType);
    }
}
