package com.xwintop.xJavaFxTool;

import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CoordinateTransformToolMain {
    public static void main(String[] args) {
        try {
            Application.launch(CoordinateTransformToolApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}