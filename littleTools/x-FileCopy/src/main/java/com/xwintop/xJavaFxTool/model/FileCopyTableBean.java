package com.xwintop.xJavaFxTool.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class FileCopyTableBean {
    private SimpleStringProperty copyFileOriginalPath;//原文件路径
    private SimpleStringProperty copyFileTargetPath;//拷贝路径
    private SimpleStringProperty copyNumber;//拷贝份数
    private SimpleBooleanProperty isCopy;
    private SimpleBooleanProperty isRename;
    private SimpleBooleanProperty isDelete;//删除原文件
    private SimpleBooleanProperty isDeleteSourceFile;//删除存在文件
    private SimpleStringProperty remarks;//备注

    public FileCopyTableBean(String copyFileOriginalPath, String copyFileTargetPath, String copyNumber, Boolean isCopy, Boolean isRename, Boolean isDelete, Boolean isDeleteSourceFile, String remarks) {
        this.copyFileOriginalPath = new SimpleStringProperty(copyFileOriginalPath);
        this.copyFileTargetPath = new SimpleStringProperty(copyFileTargetPath);
        this.copyNumber = new SimpleStringProperty(copyNumber);
        this.isCopy = new SimpleBooleanProperty(isCopy);
        this.isRename = new SimpleBooleanProperty(isRename);
        this.isDelete = new SimpleBooleanProperty(isDelete);
        this.isDeleteSourceFile = new SimpleBooleanProperty(isDeleteSourceFile);
        this.remarks = new SimpleStringProperty(remarks);
    }

    public FileCopyTableBean(String propertys) {
        String[] strings = propertys.split("__", 8);
        this.copyFileOriginalPath = new SimpleStringProperty(strings[0]);
        this.copyFileTargetPath = new SimpleStringProperty(strings[1]);
        this.copyNumber = new SimpleStringProperty(strings[2]);
        this.isCopy = new SimpleBooleanProperty(Boolean.valueOf(strings[3]));
        this.isRename = new SimpleBooleanProperty(Boolean.valueOf(strings[4]));
        this.isDelete = new SimpleBooleanProperty(Boolean.valueOf(strings[5]));
        this.isDeleteSourceFile = new SimpleBooleanProperty(Boolean.valueOf(strings[6]));
        this.remarks = new SimpleStringProperty(strings[7]);
    }

    public String getPropertys() {
        return copyFileOriginalPath.get() + "__" + copyFileTargetPath.get() + "__" + copyNumber.get() + "__" + isCopy.get() + "__" + isRename.get() + "__" + isDelete.get() + "__" + isDeleteSourceFile.get() + "__" + remarks.get();
    }

    public String getCopyFileOriginalPath() {
        return copyFileOriginalPath.get();
    }

    public void setCopyFileOriginalPath(String copyFileOriginalPath) {
        this.copyFileOriginalPath.set(copyFileOriginalPath);
    }

    public String getCopyFileTargetPath() {
        return copyFileTargetPath.get();
    }

    public void setCopyFileTargetPath(String copyFileTargetPath) {
        this.copyFileTargetPath.set(copyFileTargetPath);
    }

    public String getCopyNumber() {
        return copyNumber.get();
    }

    public void setCopyNumber(String copyNumber) {
        this.copyNumber.set(copyNumber);
    }

    public BooleanProperty isCopyProperty() {
        return isCopy;
    }

    public Boolean getIsCopy() {
        return isCopy.get();
    }

    public void setIsCopy(Boolean isCopy) {
        this.isCopy.set(isCopy);
    }

    public BooleanProperty isRenameProperty() {
        return isRename;
    }

    public Boolean getIsRename() {
        return isRename.get();
    }

    public void setIsRename(Boolean isRename) {
        this.isRename.set(isRename);
    }

    public BooleanProperty isDeleteProperty() {
        return isDelete;
    }

    public Boolean getIsDelete() {
        return isDelete.get();
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete.set(isDelete);
    }

    public BooleanProperty isDeleteSourceFileProperty() {
        return isDeleteSourceFile;
    }

    public Boolean getIsDeleteSourceFile() {
        return isDeleteSourceFile.get();
    }

    public void setIsDeleteSourceFile(Boolean isDeleteSourceFile) {
        this.isDeleteSourceFile.set(isDeleteSourceFile);
    }

    public String getRemarks() {
        return remarks.get();
    }

    public void setRemarks(String remarks) {
        this.remarks.set(remarks);
    }
}
