package com.xwintop.xJavaFxTool.services.littleTools;

import com.xwintop.xJavaFxTool.controller.littleTools.HbaseToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

@Getter
@Setter
@Slf4j
public class HbaseToolService {
    private HbaseToolController hbaseToolController;

    public HbaseToolService(HbaseToolController hbaseToolController) {
        this.hbaseToolController = hbaseToolController;
    }

    public void connectAction() throws Exception {
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", hbaseToolController.getHbaseUrlTextField().getText());
        configuration.set("zookeeper.znode.parent", "/");
        Connection connection = ConnectionFactory.createConnection(configuration);

        Admin admin = connection.getAdmin();
        TableName[] tableName = admin.listTableNames();
        for(TableName tn : tableName){
//            list.add(tn.getNameWithNamespaceInclAsString());
        }
    }
}