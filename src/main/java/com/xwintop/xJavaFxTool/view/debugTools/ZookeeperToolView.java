package com.xwintop.xJavaFxTool.view.debugTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: ZookeeperToolView
 * @Description: Zookeeper工具
 * @author: xufeng
 * @date: 2019/4/8 17:00
 */

@Getter
@Setter
public abstract class ZookeeperToolView implements Initializable {
    @FXML
    protected TextField zkServersTextField;
    @FXML
    protected Spinner<Integer> connectionTimeoutSpinner;
    @FXML
    protected Button connectButton;
    @FXML
    protected Button disconnectButton;
    @FXML
    protected Button refreshButton;
    @FXML
    protected TreeView<String> nodeTreeView;
    @FXML
    protected Button nodeDataSaveButton;
    @FXML
    protected TextArea nodeDataValueTextArea;
    @FXML
    protected TextField A_VERSIONTextField;
    @FXML
    protected TextField C_TIMETextField;
    @FXML
    protected TextField C_VERSIONTextField;
    @FXML
    protected TextField CZXIDTextField;
    @FXML
    protected TextField DATA_LENGTHTextField;
    @FXML
    protected TextField EPHEMERAL_OWNERTextField;
    @FXML
    protected TextField M_TIMETextField;
    @FXML
    protected TextField MZXIDTextField;
    @FXML
    protected TextField NUM_CHILDRENTextField;
    @FXML
    protected TextField PZXIDTextField;
    @FXML
    protected TextField VERSIONTextField;
    @FXML
    protected TextField aclSchemeTextField;
    @FXML
    protected TextField aclIdTextField;
    @FXML
    protected TextField aclPermissionsTextField;

}