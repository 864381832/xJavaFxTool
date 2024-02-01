package com.xwintop.xJavaFxTool.controller.games;

import com.xwintop.xJavaFxTool.services.games.Main;
import com.xwintop.xJavaFxTool.view.games.petView;
import com.xwintop.xJavaFxTool.services.games.petService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

@Getter
@Setter
@Slf4j
public class petController extends petView {
    private petService petService = new petService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void openPetAction(ActionEvent event) {
        System.out.println("dd");
//        Main.main(new String[]{});
        petService.showPet();
    }

}