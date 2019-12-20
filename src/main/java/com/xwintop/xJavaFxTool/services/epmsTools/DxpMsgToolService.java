package com.xwintop.xJavaFxTool.services.epmsTools;

import com.xwintop.xJavaFxTool.controller.epmsTools.DxpMsgToolController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.swing.filechooser.FileSystemView;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.Date;

/**
 * @ClassName: DxpMsgToolService
 * @Description: dxp报文解析工具
 * @author: xufeng
 * @date: 2019/12/20 11:13
 */

@Getter
@Setter
@Slf4j
public class DxpMsgToolService {
    private DxpMsgToolController dxpMsgToolController;

    public void createAction() throws Exception {
        String dxpMode = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<DxpMsg ver=\"1.0\" Id=\"dxpEport\" xmlns=\"http://www.chinaport.gov.cn/dxp\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><TransInfo><CopMsgId></CopMsgId><SenderId>DXPEDCINVT000002</SenderId><ReceiverIds><ReceiverId>DXPENT0000024643</ReceiverId></ReceiverIds><CreatTime></CreatTime><MsgType></MsgType></TransInfo><Data></Data><AddInfo><FileName></FileName><IcCard>1</IcCard></AddInfo></DxpMsg>";
//        SAXReader reader = new SAXReader();
//        Document document = reader.read(new ByteArrayInputStream(dxpMode.getBytes()));
//        Element rootElement = document.getRootElement();
//        Element transInfo = rootElement.element("TransInfo");
//        transInfo.element("CopMsgId").setText(dxpMsgToolController.getCopMsgIdTextField().getText());
//        transInfo.element("MsgType").setText(dxpMsgToolController.getMsgTypeTextField().getText());
//        if (StringUtils.isEmpty(dxpMsgToolController.getCreatTimeTextField().getText())) {
//            transInfo.element("CreatTime").setText(DateFormatUtils.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
//        } else {
//            transInfo.element("CreatTime").setText(dxpMsgToolController.getCreatTimeTextField().getText());
//        }
//        Element addInfo = rootElement.element("AddInfo");
//        addInfo.element("FileName").setText(dxpMsgToolController.getFileNameTextField().getText());
//        rootElement.element("Data").setText(Base64.encodeBase64String(dxpMsgToolController.getDataTextArea().getText().getBytes("utf-8")));
        File file = null;
        if (StringUtils.isEmpty(dxpMsgToolController.getSavePathTextField().getText())) {
            FileSystemView fsv = FileSystemView.getFileSystemView();
            File com = fsv.getHomeDirectory();    //这便是读取桌面路径的方法了
            file = new File(com, dxpMsgToolController.getFileNameTextField().getText());
        } else {
            file = new File(dxpMsgToolController.getSavePathTextField().getText(), dxpMsgToolController.getFileNameTextField().getText());
        }
//        OutputFormat format = OutputFormat.createPrettyPrint();
//        FileOutputStream fileOutputStream = new FileOutputStream(file);
//        try {
//            XMLWriter writer = new XMLWriter(fileOutputStream, format);
//            writer.write(document);
//        } finally {
//            fileOutputStream.close();
//        }
        dxpMode = dxpMode.replace("<CopMsgId></CopMsgId>", "<CopMsgId>" + dxpMsgToolController.getCopMsgIdTextField().getText() + "</CopMsgId>");
        dxpMode = dxpMode.replace("<MsgType></MsgType>", "<MsgType>" + dxpMsgToolController.getMsgTypeTextField().getText() + "</MsgType>");
        if (StringUtils.isEmpty(dxpMsgToolController.getCreatTimeTextField().getText())) {
            dxpMode = dxpMode.replace("<CreatTime></CreatTime>", "<CreatTime>" + DateFormatUtils.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") + "</CreatTime>");
        } else {
            dxpMode = dxpMode.replace("<CreatTime></CreatTime>", "<CreatTime>" + dxpMsgToolController.getCreatTimeTextField().getText() + "</CreatTime>");
        }
        dxpMode = dxpMode.replace("<FileName></FileName>", "<FileName>" + dxpMsgToolController.getFileNameTextField().getText() + "</FileName>");
        dxpMode = dxpMode.replace("<Data></Data>", "<Data>" + Base64.encodeBase64String(dxpMsgToolController.getDataTextArea().getText().getBytes("utf-8")) + "</Data>");
        FileUtils.write(file, dxpMode, "utf-8");
    }

    public void parserDxpMsg(byte[] msg) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new ByteArrayInputStream(msg));
        Element rootElement = document.getRootElement();
        Element transInfo = rootElement.element("TransInfo");
        dxpMsgToolController.getCopMsgIdTextField().setText(transInfo.elementTextTrim("CopMsgId"));
        dxpMsgToolController.getMsgTypeTextField().setText(transInfo.elementTextTrim("MsgType"));
        dxpMsgToolController.getCreatTimeTextField().setText(transInfo.elementTextTrim("CreatTime"));
        Element addInfo = rootElement.element("AddInfo");
        if (addInfo != null) {
            dxpMsgToolController.getFileNameTextField().setText(addInfo.elementTextTrim("FileName"));
        }
        try {
            SAXReader readerData = new SAXReader();
            Document documentData = readerData.read(new ByteArrayInputStream(Base64.decodeBase64(rootElement.elementTextTrim("Data"))));
            OutputFormat format = OutputFormat.createPrettyPrint();
            StringWriter stringWriter = new StringWriter();
            XMLWriter writer = new XMLWriter(stringWriter, format);
            writer.write(documentData);
            dxpMsgToolController.getDataTextArea().setText(stringWriter.toString());
        } catch (Exception e) {
            dxpMsgToolController.getDataTextArea().setText(new String(Base64.decodeBase64(rootElement.elementTextTrim("Data"))));
        }
    }

    public DxpMsgToolService(DxpMsgToolController dxpMsgToolController) {
        this.dxpMsgToolController = dxpMsgToolController;
    }
}