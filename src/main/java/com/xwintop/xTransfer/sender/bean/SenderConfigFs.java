package com.xwintop.xTransfer.sender.bean;

import lombok.Data;

/**
 * @ClassName: SenderConfigFs
 * @Description: Fs发送配置
 * @author: xufeng
 * @date: 2018/4/11 11:05
 */

@Data
public class SenderConfigFs implements SenderConfig {
    private String serviceName = "senderFs";//对应服务名称
    private String id;//如果留空则系统自动分配
    private boolean enable = true;//是否开启
    private boolean async = false;//是否异步执行
    /** 是否发生异常时退出任务 */
    private boolean exceptionExit = true;
    private String fileNameFilterRegex;//文件名过滤正则表达式

    /** 发送目录 */
    private String path;
    /** 发送缓冲目录 */
    private String tmp;
    private String fileName;//文件名，支持变量替换。
    /** 编码 */
    private String encoding = "utf-8";
    /** 是否创建目录 */
    private boolean createPathFlag = false;
    /** 是否有缓冲目录 */
    private boolean hasTmpPath = true;
    private String postfixName;//后缀名添加(当无缓存目录时生效，默认值为msgId)
    /** 是否覆盖重名文件 */
    private boolean overload = false;
}
