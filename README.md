**gitee地址：**[xJavaFxTool](https://gitee.com/xwintop/xJavaFxTool)

**GitHub地址：**[xJavaFxTool](https://github.com/864381832/xJavaFxTool)

**腾讯云开发平台地址：**[xJavaFxTool](https://dev.tencent.com/u/xwintop/p/xJavaFxTool)

<p align="center">
	<a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.html">
		<img src="https://img.shields.io/:license-apache-blue.svg" ></img>
	</a>
	<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
		<img src="https://img.shields.io/badge/JDK-1.8+-green.svg" ></img>
	</a>
	<a target="_blank" href="https://gitee.com/xwintop/xJavaFxTool/stargazers">
		<img src='https://gitee.com/xwintop/xJavaFxTool/badge/star.svg?theme=dark' alt='gitee star'></img>
	</a>
	<a target="_blank" href='https://github.com/864381832/xJavaFxTool'>
		<img src="https://img.shields.io/github/stars/864381832/xJavaFxTool.svg?style=social" alt="github star"></img>
	</a>
</p>

[英文说明/English Documentation](README_EN.md)

**xJavaFxTool交流QQ群：== [387473650](https://jq.qq.com/?_wv=1027&k=59UDEAD) ==**

#### 项目简介：
xJavaFxTool是使用javaFx开发的实用小工具集，利用业余时间把工作中遇到的一些问题总结起来，打包进小工具集中，供大家参考和使用，里面包含了javaFx的一些功能的示例，如布局、国际化、第三方UI库([controlsfx](http://fxexperience.com/controlsfx/)、[JFoenix](http://www.jfoenix.com/)等)、外部jar包加载(插件机制)等一些常用功能，想学习javaFx的同学可以参考参考，学习javaFx的资料参考[www.javafxchina.net](http://www.javafxchina.net/main/)

由于SpringBoot的火热，项目已经出SpringBoot-javafx版本，[xJavaFxTool-spring](https://gitee.com/xwintop/xJavaFxTool-spring) 欢迎参考，谢谢。

#### 下载试用地址：
- 可直接运行的jar包(本地需要有jdk1.8环境)[xJavaFxTool-0.1.7.jar](https://dev.tencent.com/s/8c2f9586-475f-4ece-9027-74c98b8c2c6c)
- Windows x86安装包(兼容xp、windows7、8、10等系统)[xJavaFxTool-0.1.7-windows-x86.exe](https://dev.tencent.com/s/61e19e79-9745-4746-8e46-8f39f14a0ed4)
- Windows x64安装包(兼容xp、windows7、8、10等系统)[xJavaFxTool-0.1.7-windows-x64.exe](https://dev.tencent.com/s/dce303db-cd17-40fa-99fd-c3be431fc384)
- Linux x64 [xJavaFxTool-0.1.7-linux-x64.zip](https://dev.tencent.com/s/8c55b6f4-ddec-4a3e-b808-33519fea313a) (Linux平台64位运行压缩包，解压即可运行)
- Mac OS X x64 [xJavaFxTool-0.1.7-macosx-x64.dmg](https://dev.tencent.com/s/c923e93f-6729-4748-87b6-2ffd71451a46)
- Mac OS X x64 [xJavaFxTool-0.1.7-macosx-x64.pkg](https://dev.tencent.com/s/bb44f658-2123-4f8b-a514-7361d3bf4c89)
#### 若上面下链接失效可使用下面下载链接：
- 百度云链接：[https://pan.baidu.com/s/193fhGnJL4dDWcqDnFJcHbA](https://pan.baidu.com/s/193fhGnJL4dDWcqDnFJcHbA)  提取码：mokl
- 腾讯微云链接：[https://share.weiyun.com/5T6FPLW](https://share.weiyun.com/5T6FPLW) 提取码：java

支持插件开发，将插件jar包放至根目录libs下即可自动加载(插件开发示例见[开源项目xJavaFxPlugIn](https://gitee.com/xwintop/xJavaFxPlugIn)，后续准备将小工具拆分至各插件中按需加载，目前插件功能暂不完善，后续将各功能拆分至各模块按需加载，减小jar包的大小);

#### 环境搭建说明：
- 开发环境为jdk1.8，基于maven构建
- 使用eclipase或Intellij Idea开发(推荐使用[Intellij Idea](https://www.jetbrains.com/idea/))
- 本项目使用了[lombok](https://projectlombok.org/),在查看本项目时如果您没有下载lombok 插件，请先安装,不然找不到get/set等方法
- 依赖的[xcore包](https://gitee.com/xwintop/xcore)已上传至git托管的maven平台，git托管maven可参考教程(若无法下载请拉取项目自行编译)。[教程地址：点击进入](http://blog.csdn.net/u011747754/article/details/78574026)
- 使用[javafx-maven-plugin](https://github.com/javafx-maven-plugin/javafx-maven-plugin)插件进行打包操作(可打包windows、Linux、Mac安装包)
- 使用[exe4j](https://www.ej-technologies.com/download/exe4j/files)将jar包转成exe执行文件(仅供参考，可使用其它程序打包)
- 使用[InnoSetup](http://www.jrsoftware.org/)可进行制作windows安装包

#### 目前集成的小工具有：
1. FileCopy：文件复制(支持自动调度拷贝功能)(使用[quartz](https://www.quartz-scheduler.org/)工具)
2. CronExpBuilder：Cron表达式生成器
3. CharacterConverter：编码转换
4. EncryptAndDecrypt：加密解密(Ascii、Hex、Base64、Base32、URL、MD5、SHA、AES、DES、文件加密DM5、文件加密SHA1、摩斯密码、Druid加密)(使用[commons-codec](http://commons.apache.org/codec/)工具)
5. TimeTool：Time转换(常用格式转换(含时区)、计算时间差、时间叠加计算)
6. LinuxPathToWindowsPath：路径转换(使用[oshi](https://github.com/oshi/oshi)工具)
7. QRCodeBuilder：二维码生成工具(自动生成、加入logo、截图识别、自定义格式)(使用[google.zxing](https://github.com/zxing/zxing)、[jkeymaster](https://github.com/tulskiy/jkeymaster)等工具)
8. IdCardGenerator：身份证生成器
9. RegexTester：正则表达式生成工具
10. ShortURL：网址缩短(目前支持百度、新浪、缩我等短网址缩短)
11. EscapeCharacter：转义字符(支持Html、XML、Java、JavaScript、CSV、Sql)(使用[commons-lang3](https://commons.apache.org/lang)工具)
12. ZHConverter：字符串转换(使用[hanlp](http://hanlp.com/)开源工具，实现拼音、简体-繁体、简体-臺灣正體、简体-香港繁體、繁體-臺灣正體、繁體-香港繁體、香港繁體-臺灣正體、数字金额-大写金额等直接的转换)
13. ActiveMqTool:Mq调试工具(目前仅支持[ActiveMq](http://activemq.apache.org))
14. HttpTool：Http调试工具(支持自定义发送数据、header和cookie)(使用[okhttp](https://square.github.io/okhttp/))
15. jsonEditor：json格式化编辑工具
16. IconTool：图标生成工具(使用[thumbnailator](https://github.com/coobird/thumbnailator)工具)
17. RedisTool：Redis连接工具(使用[jedis](https://github.com/xetorthio/jedis)工具)
18. WebSourcesTool：网页源码下载工具
19. SwitchHostsTool：切换Hosts工具(使用[richtextfx](https://github.com/FXMisc/RichTextFX)工具)
20. FtpServer：Ftp服务器(快速搭建本地Ftp服务)(基于[apache.ftpserver](https://mina.apache.org/ftpserver-project))
21. CmdTool：Cmd调试工具
22. FtpClientTool：Ftp(s)/Sftp客户端调试工具(批量上传、下载、删除文件及文件夹)(implicit/explicit SSL/TLS)(使用[jsch](http://www.jcraft.com/jsch)、[commons-io](http://commons.apache.org/io/)等工具)
23. PdfConvertTool：Pdf转换工具(目前仅支持pdf转图片、pdf转text功能)(使用[pdfbox](https://pdfbox.apache.org/)工具)
24. DirectoryTreeTool：文件列表生成器
25. ImageTool：图片压缩工具(批量压缩、修改尺寸、转换格式)
26. AsciiPicTool：图片转码工具(包括图片生成banner码、图片转Base64码、图片转Excel表)
27. KafkaTool：Kafka调试工具(未完善)(使用了[kafka-clients](http://kafka.apache.org/))
28. EmailTool：Email群发工具(支持自定义群发模版)(使用[commons-email](https://commons.apache.org/email)工具)
29. ColorCodeConverterTool：颜色代码转换工具(包括16进制、RGB、ARGB、RGBA、HSL、HSV等代码之间转换)
30. SmsTool：短信群发工具(目前支持中国移动、中国电信、腾讯云、阿里云、梦网云通讯等平台)
31. ScriptEngineTool：脚本引擎调试工具(目前支持JavaScript、Groovy、Python、Lua等脚本)(使用[groovy](http://groovy-lang.org)、[jython](https://jython.org)、[luaj](http://www.luaj.org/luaj.html)等工具)
32. FileRenameTool：文件重命名工具
33. JsonConvertTool：Json转换工具(目前支持Json转Xml、Json转Java实体类、Json转C#实体类、Json转Excel、Json转Yaml、Properties转Yaml、Yaml转Properties)(使用[fastjson](https://github.com/alibaba/fastjson)、[snakeyaml](https://bitbucket.org/asomov/snakeyaml)、[dom4j](https://dom4j.github.io)等工具)
34. WechatJumpGameTool：微信跳一跳助手
35. TextToSpeechTool：语音转换工具(调用[百度语音](https://ai.baidu.com/tech/speech/tts)转换api)
36. 2048：小游戏2048
37. SocketTool：Socket调试工具(使用[Apache Mina](http://mina.apache.org)实现Tcp、Udp服务端和Client端)
38. ImageAnalysisTool:图片解析工具(1、.atlas文件反解析2、图片快速拆分工具)
39. DecompilerWxApkgTool:微信小程序反编译工具(一键反编译微信小程序包)
40. ZookeeperTool:Zookeeper工具(方便对zookeeper的一系列操作，包括新增、修改、删除(包括子文件)、重命名、复制、添加变更通知)(使用[zkclient](https://github.com/sgroschupf/zkclient)工具)
41. ExcelSplitTool:Excel拆分工具(支持对xls、xlsx、csv及文件进行拆分操作)(使用[commons-csv](http://commons.apache.org/csv)工具)
42. PathWatchTool:文件夹监控工具
43. CharsetDetectTool:文件编码检测工具(使用[juniversalchardet](https://github.com/albfernandez/juniversalchardet)工具)
44. TransferTool:传输工具(集成各种传输协议，使用自定义定时任务(简单模式、cron表达式模式)，分为Receiver接收器、Filter处理器、Sender发送器)
45. ScanPortTool:端口扫描工具
46. FileMergeTool:文件合并工具(支持对xls、xlsx、csv及文件进行合并操作)(使用[apache.poi](http://poi.apache.org/)工具)
47. SedentaryReminderTool:久坐提醒工具
48. RandomGeneratorTool:随机数生成工具(使用[hutool](https://hutool.cn)工具)
49. ClipboardHistoryTool:剪贴板历史工具
50. FileSearchTool:文件搜索工具(使用[lucene](https://lucene.apache.org/)搜索引擎)
51. Mp3ConvertTool:Mp3转换工具(目前支持网易云音乐.ncm、QQ音乐.qmc转换为mp3格式)(使用[jaudiotagger](http://www.jthink.net/jaudiotagger/)工具)
52. SealBuilderTool:印章生成工具
53. BullsAndCowsGame:猜数字小游戏

传输工具目前支持功能如下：

Receiver接收器：

| 标题 | 配置类名 | 说明 |
| ------------- | ------------- | ------------- |
| Fs            | ReceiverConfigFs      | 从磁盘文件中读取消息    |
| Ftp           | ReceiverConfigFtp     | 使用Ftp/Ftps协议中接收消息    |
| Http          | ReceiverConfigHttp    | 使用http/https协议接收消息(支持拉取模式和提供Restfull接口方式) |
| Ftp           | ReceiverConfigFtp     | 使用Ftp/Ftps协议接收消息   |
| SFtp          | ReceiverConfigSftp    | 使用SFtp协议接收消息       |
| Email         | ReceiverConfigEmail   | 使用Email协议接收消息          |
| Jms           | ReceiverConfigJms     | 使用Jms协议接收消息        |
| Kafka         | ReceiverConfigKafka   | 使用Kafka协议接收消息        |
| IbmMq         | ReceiverConfigIbmMq   | 使用IbmMq协议接收消息        |
| RabbitMq      | ReceiverConfigRabbitMq| 使用RabbitMq协议接收消息     |
| RocketMq      | ReceiverConfigRocketMq| 使用RocketMq协议接收消息     |
| ActiveMq      | ReceiverConfigActiveMq| 使用ActiveMq协议接收消息     |
| Hdfs          | ReceiverConfigHdfs    | 使用HDFS协议接收消息         |

Filter处理器：

| 标题          | 配置类名                   |  说明  |
| --------      | -----                    | :----  |
| Backup        | FilterConfigBackup        | 将消息备份到文件系统中   |
| Compress      | FilterConfigCompress      | 将消息进行压缩操作       |
| Decompress    | FilterConfigDecompress    | 将消息进行解压操作       |
| EncryptDecrypt| FilterConfigEncryptDecrypt| 将消息进行加密解密操作   |
| OracleSqlldr  | FilterConfigOracleSqlldr  | 将消息存入Oracle数据库   |
| GroovyScript  | FilterConfigGroovyScript  | 将执行Groovy脚本   |
| PythonScript  | FilterConfigPythonScript  | 将执行Python脚本   |
| JavaScript    | FilterConfigJavaScript    | 将执行JavaScript脚本   |
| LuaScript     | FilterConfigLuaScript     | 将执行Lua脚本   |

Sender发送器：

| 标题          | 配置类名               |  说明  |
| --------      | -----                | :----  |
| Fs            | SenderConfigFs      | 从磁盘文件中读取消息    |
| Ftp           | SenderConfigFtp     | 使用Ftp/Ftps协议中发送消息    |
| Http          | SenderConfigHttp    | 使用http/https协议发送消息  |
| Ftp           | SenderConfigFtp     | 使用Ftp/Ftps协议发送消息   |
| SFtp          | SenderConfigSftp    | 使用SFtp协议发送消息       |
| Email         | SenderConfigEmail   | 使用Email中发送消息          |
| Jms           | SenderConfigJms     | 使用Jms协议发送消息        |
| Kafka         | SenderConfigKafka   | 使用Kafka协议发送消息        |
| IbmMq         | SenderConfigIbmMq   | 使用IbmMq协议发送消息        |
| RabbitMq      | SenderConfigRabbitMq| 使用RabbitMq协议发送消息     |
| RocketMq      | SenderConfigRocketMq| 使用RocketMq协议发送消息     |
| ActiveMq      | SenderConfigActiveMq| 使用ActiveMq协议发送消息     |
| Hdfs          | SenderConfigHdfs    | 使用HDFS协议发送消息     |


项目开发中，以后会陆续添加新工具，欢迎大家参与其中，多提提意见，谢谢。

计划添加功能：

- [ ] 监控功能(文件夹深度、Ftp/Ftps/sftp文件数量、mq深度)
- [ ] 转换excel为sql插入语句
- [ ] 记录键盘使用情况小工具
- [ ] 随机文件生成器(带模版引擎)
- [x] 文件搜索功能

#### 项目结构

```
xJavaFxTool
├─ images	项目截图
├─ lib	外部引用jar包存放
├─ libs	插件jar包存放
├─ pom.xml	maven配置文件
├─ README.md	说明文件
├─ src
│  ├─ main
│  │  ├─ java
│  │  │  └─ com
│  │  │   └─ xwintop
│  │  │    └─ xJavaFxTool
│  │  │     ├─ common	第三方工具类
│  │  │     ├─ config	springBoot配置类
│  │  │     ├─ controller	javafx控制层
│  │  │     │  ├─ assistTools   辅助工具控制层
│  │  │     │  ├─ codeTools	Code工具控制层
│  │  │     │  ├─ debugTools	调试工具控制层
│  │  │     │  ├─ developTools	开发工具控制层
│  │  │     │  ├─ epmsTools	epms工具控制层
│  │  │     │  ├─ javaFxTools	javaFx工具控制层
│  │  │     │  ├─ littleTools	小工具控制层
│  │  │     │  └─ webTools	html工具控制层
│  │  │     ├─ job	定时任务处理job
│  │  │     ├─ main	主函数包
│  │  │     ├─ manager	管理层
│  │  │     ├─ model	基础bean类层
│  │  │     ├─ services	工具服务层
│  │  │     │  ├─ assistTools   辅助工具服务层
│  │  │     │  ├─ codeTools	Code工具服务层
│  │  │     │  ├─ debugTools	调试工具服务层
│  │  │     │  ├─ developTools	开发工具服务层
│  │  │     │  ├─ epmsTools	epms工具服务层
│  │  │     │  ├─ javaFxTools	javaFx工具服务层
│  │  │     │  ├─ littleTools	小工具服务层
│  │  │     │  └─ webTools	html工具服务层
│  │  │     ├─ utils	系统工具类
│  │  │     ├─ view	javafx视图层
│  │  │     │  ├─ assistTools   辅助工具视图层
│  │  │     │  ├─ codeTools	Code工具视图层
│  │  │     │  ├─ debugTools	调试工具视图层
│  │  │     │  ├─ developTools	开发工具视图层
│  │  │     │  ├─ javaFxTools	javaFx工具视图层
│  │  │     │  ├─ littleTools	小工具视图层
│  │  │     │  └─ webTools	html工具视图层
│  │  │     └─ web	web控制视图层
│  │  └─ resources
│  │   ├─ com
│  │   │  └─ xwintop
│  │   │   └─ xJavaFxTool
│  │   │    └─ fxmlView     .fxml文件
│  │   ├─ config	配置文件
│  │   │  └─ toolFxmlLoaderConfiguration.xml	系统菜单加载配置文件
│  │   ├─ css	样式资源
│  │   ├─ data	数据资源
│  │   ├─ images	图片资源
│  │   ├─ locale	国际化
│  │   ├─ web	html工具
│  │   ├─ application.yaml	SpringBoot配置文件
│  │   ├─ banner.txt	启动banner图片
│  │   └─ logback.xml	logback日志配置文件
│  └─ test  测试类
│   ├─ java
│   └─ resources
├─ xJavaFxTool.jar	直接运行程序包

```

#### 特别感谢
在一个人还年轻的时候，我觉得，就应该着手致力做一些对社会有意义的事情，一如开源。至此，感谢以下贡献者(排名不分先后)：
+ [李柱](https://gitee.com/loyalty521)
+ [luming](https://gitee.com/jeeweb)
+ [码志](https://gitee.com/dazer1992)
+ 你的女神

#### 后续计划
不定期添加汇总开发过程中需求的痛点工具，大家有工作上的痛点处可进群讨论，后期可能就会出相应的工具解决方案，谢谢大家的支持。

#### 项目截图如下：

![输入图片说明](images/传输工具.png "传输工具.png")
![输入图片说明](images/文件复制.png "文件复制.png")
![输入图片说明](images/邮件发送工具.png "邮件发送工具.png")
![输入图片说明](images/Cron表达式生成器.png "Cron表达式生成器.png")
![输入图片说明](images/Mq调试工具.png "Mq调试工具.png")
![输入图片说明](images/正则表达式生成工具.png "正则表达式生成工具.png")
![输入图片说明](images/二维码生成工具.png "二维码生成工具.png")
![输入图片说明](images/json格式化编辑工具.png "json格式化编辑工具.png")
![输入图片说明](images/网址缩短.png "网址缩短.png")
![输入图片说明](images/字符串转换.png "字符串转换.png")
![输入图片说明](images/Http调试工具.png "Http调试工具.png")
![输入图片说明](images/编码转换.png "编码转换.png")
![输入图片说明](images/转义字符.png "转义字符.png")
![输入图片说明](images/加密解密.png "加密解密.png")
![输入图片说明](images/Time转换.png "Time转换.png")
![输入图片说明](images/图标生成工具.png "图标生成工具.png")
![输入图片说明](images/Redis连接工具.png "Redis连接工具.png")
![输入图片说明](images/Ftp服务器.png "Ftp服务器.png")
![输入图片说明](images/文件列表生成器.png "文件列表生成器.png")
![输入图片说明](images/图片压缩工具.png "图片压缩工具.png")
![输入图片说明](images/Ftp客户端调试工具.png "Ftp客户端调试工具.png")
![输入图片说明](images/Pdf转换工具.png "Pdf转换工具.png")
![输入图片说明](images/图片转码工具.png "图片转码工具.png")
![输入图片说明](images/Cmd调试工具.png "Cmd调试工具.png")
![输入图片说明](images/短信群发工具.png "短信群发工具.png")
![输入图片说明](images/zookeeper工具.png "zookeeper工具.png")
![输入图片说明](images/文件重命名工具.png "文件重命名工具.png")
![输入图片说明](images/Json转换工具.png "Json转换工具.png")
![输入图片说明](images/语音转换工具.png "语音转换工具.png")
![输入图片说明](images/Socket调试工具.png "Socket调试工具.png")
![输入图片说明](images/Excel拆分工具.png "Excel拆分工具.png")
![输入图片说明](images/文件编码检测工具.png "文件编码检测工具.png")
![输入图片说明](images/端口扫描工具.png "端口扫描工具.png")
![输入图片说明](images/文件合并工具.png "文件合并工具.png")
![输入图片说明](images/随机数生成工具.png "随机数生成工具.png")
![输入图片说明](images/剪贴板历史工具.png "剪贴板历史工具.png")
![输入图片说明](images/久坐提醒工具.png "久坐提醒工具.png")
![输入图片说明](images/印章生成工具.png "印章生成工具.png")
