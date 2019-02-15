package com.easipass.gateway.route.bean;

import com.easipass.gateway.route.entity.SenderConfig;
import lombok.Data;

/**
 * @ClassName: SenderConfigFtp
 * @Description: Ftp发送配置
 * @author: xufeng
 * @date: 2018/4/11 11:05
 */

@Data
public class SenderConfigFtp implements SenderConfig {
    private String serviceName = "senderFtp";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = true;

    private String host;                //FTP主机地址
    private int port = 21;              //FTP主机端口
    private String user;                //FTP用户名
    private String password;            //FTP密码
    private boolean isFtps = false;     //是否为ftps连接
    private String path;                //ftp上路径
    private String fileName;//文件名,发送方式为附件时的文件名。支持变量替换。
    private String serversEncoding = "AUTO";//
    private String encoding = "AUTO";//编码。消息写入文件的编码。消息说明请参考[wiki:EpmsBook/SenderFs 文件发送器]中的编码配置描述。
    private int timeout = 300000;//连接超时设置，时间单位为秒。
    private int socketTimeout = 300000;//Socket超时设置，时间单位为秒。
    private boolean longConnection;//是否使用长连接。使用长连接可以提供消息发送的性能，但对FTP服务器的负担可能加重。在持续有消息发送时候，建议使用长连接。
    private boolean passive = false;//:是否被动式，默认为主动式。
    private boolean binary = false;//文件传输方式，是二进制还是ACSII方式。
    private boolean overload = false;//是否覆盖目标目录中相同文件名。
    private String tmpPath;//临时存放目录。
    private boolean hasTmpPath = true;//
    private String fileNamePostfix;//
    private boolean createPathFlag = false; //是否创建路径
}
