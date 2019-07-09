package com.xwintop.xJavaFxTool.services.developTools.xTransferTool;

import com.xwintop.xJavaFxTool.controller.developTools.xTransferTool.TransferToolController;
import com.xwintop.xJavaFxTool.controller.developTools.xTransferTool.TransferToolDataSourceController;
import com.xwintop.xJavaFxTool.utils.TransferViewUtil;
import com.xwintop.xTransfer.datasource.bean.DataSourceConfigDruid;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @ClassName: TransferToolDataSourceService
 * @Description: 数据源配置
 * @author: xufeng
 * @date: 2019/6/28 18:00
 */

@Getter
@Setter
@Slf4j
public class TransferToolDataSourceService {
    private TransferToolDataSourceController transferToolDataSourceController;

    private TransferToolController transferToolController;

    public TransferToolDataSourceService(TransferToolDataSourceController transferToolDataSourceController) {
        this.transferToolDataSourceController = transferToolDataSourceController;
    }

    public void saveConfigAction() throws Exception {
        DataSourceConfigDruid dataSourceConfigDruid = transferToolDataSourceController.getDataSourceConfigDruid();
        String fileName = transferToolDataSourceController.getFileName();
        String dataSourceConfigDruidName = transferToolDataSourceController.getTabName();
        Map<String, DataSourceConfigDruid> dataSourceConfigDruidMap = transferToolController.getTransferToolService().getDataSourceConfigFileMap().get(fileName);
        if (!dataSourceConfigDruid.getId().equals(dataSourceConfigDruidName)) {
            transferToolDataSourceController.setTabName(dataSourceConfigDruid.getId());
            transferToolController.getTransferToolService().getTaskConfigTabMap().get(dataSourceConfigDruidName).setText(dataSourceConfigDruid.getId());
            transferToolController.getTransferToolService().getTaskConfigTabMap().put(dataSourceConfigDruid.getId(), transferToolController.getTransferToolService().getTaskConfigTabMap().get(dataSourceConfigDruidName));
            dataSourceConfigDruidMap.put(dataSourceConfigDruid.getId(), dataSourceConfigDruidMap.get(dataSourceConfigDruidName));
            dataSourceConfigDruidMap.remove(dataSourceConfigDruidName);
            transferToolController.getConfigurationTreeView().getRoot().getChildren().forEach(stringTreeItem -> {
                if (fileName.equals(stringTreeItem.getValue())) {
                    stringTreeItem.getChildren().forEach(stringTreeItem1 -> {
                        if (dataSourceConfigDruidName.equals(stringTreeItem1.getValue())) {
                            stringTreeItem1.setValue(dataSourceConfigDruid.getId());
                        }
                    });
                }
            });
        }
        String configYamlString = TransferViewUtil.saveConfig(transferToolController, fileName, dataSourceConfigDruidMap.values().toArray());
        transferToolController.getTransferToolService().getTaskConfigFileStringMap().put(fileName, configYamlString);
    }

    public void viewTaskConfigAction() throws Exception {
        String fileName = transferToolDataSourceController.getFileName();
        Map<String, DataSourceConfigDruid> dataSourceConfigDruidMap = transferToolController.getTransferToolService().getDataSourceConfigFileMap().get(fileName);
        String configYamlString = TransferViewUtil.getYamlString(transferToolController.getFlowStyleChoiceBox().getValue().toString(), dataSourceConfigDruidMap.values().toArray());
        transferToolController.getTransferToolService().addTaskFileTextArea(fileName, configYamlString);
    }
}
