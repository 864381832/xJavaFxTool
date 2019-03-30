#### 项目简介：
**gitee地址：**[xJavaFxTool](https://gitee.com/xwintop/xJavaFxTool)

**GitHub地址：**[xJavaFxTool](https://github.com/864381832/xJavaFxTool)

**xJavaFxTool交流QQ群：== [387473650](https://jq.qq.com/?_wv=1027&k=59UDEAD) ==**

xJavaFxTool是使用javaFx开发的实用小工具集，目前项目刚刚建立，利用业余时间把工作中遇到的一些问题总结起来，打包进小工具集中，供大家参考和使用，里面包含了javaFx的一些功能的示例，如布局、国际化、第三方UI库（[controlsfx](http://fxexperience.com/controlsfx/)、[JFoenix](http://www.jfoenix.com/)等）、外部jar包加载（插件机制）等一些常用功能，想学习javaFx的同学可以参考参考，学习javaFx的资料参考[www.javafxchina.net](http://www.javafxchina.net/main/)

由于SpringBoot的火热，项目已经出SpringBoot-javafx版本，[xJavaFxTool-spring](https://gitee.com/xwintop/xJavaFxTool-spring) 欢迎参考，谢谢。

下载试用地址：

（可直接下载安装使用，不需要任何第三方环境，兼容xp、windows7、8、10等系统）[xJavaFxTool-0.1.4.exe](http://files.git.oschina.net/group1/M00/02/EA/PaAvDFp8eByATSAzBhUKCsTbpUc258.exe?token=3020a95a8a5f6509f5fa9575090d54db&ts=1518139040&attname=xJavaFxTool-install.exe)

（本地需要有jdk1.8环境）[xJavaFxTool-0.1.4.jar](http://files.git.oschina.net/group1/M00/02/EA/PaAvDFp8_Q-AETFIBCfy93hiJyo600.jar?token=e668b2e779efabf8e7414f9dd8e3f0a6&ts=1518140688&attname=xJavaFxTool-0.1.4.jar)（可直接运行的jar包）

（本地需要有jdk1.8环境）[xJavaFxTool-windows-x64-0.1.4.exe](http://files.git.oschina.net/group1/M00/02/EA/PaAvDFp8cFOAZmzcBCy8AHMtHjA024.exe?token=0317b8fc5268c42d110e1d008db28760&ts=1518139040&attname=xJavaFxTool-windows-x64-0.1.4.exe) （Windows平台64位运行包）

（本地需要有jdk1.8环境）[xJavaFxTool-windows-x86-0.1.4.exe](http://files.git.oschina.net/group1/M00/02/EA/PaAvDFp8ckuAejoZBCw6AG93rus326.exe?token=9ee4902139bcf39c0c8c6cdb30f87194&ts=1518139040&attname=xJavaFxTool-windows-x86-0.1.4.exe) （Windows平台32位运行包）

支持插件开发，将插件jar包放至根目录libs下即可自动加载（插件开发示例见[开源项目xJavaFxPlugIn](https://gitee.com/xwintop/xJavaFxPlugIn)，目前刚刚搭建，后续会持续更新）；

#### 环境搭建说明：
- 开发环境为jdk1.8，基于maven构建；

- 使用eclipase或Intellij Idea开发(推荐使用[Intellij Idea](https://www.jetbrains.com/idea/))

- 本项目使用了[lombok](https://projectlombok.org/),在查看本项目时如果您没有下载lombok 插件，请先安装,不然找不到get/set方法；

- 依赖的[xcore包](https://gitee.com/xwintop/xcore)已上传至git托管的maven平台，git托管maven可参考教程。[教程地址：点击进入](http://blog.csdn.net/u011747754/article/details/78574026)

#### 目前集成的小工具有：
1、FileCopy：文件复制（支持自动调度拷贝功能）；

2、CronExpBuilder：Cron表达式生成器；

3、CharacterConverter：编码转换；

4、EncryptAndDecrypt：加密解密（Ascii、Hex、Base64、Base32、URL、MD5、SHA、AES、DES、文件加密DM5、文件加密SHA1、摩斯密码）；

5、TimeTool：Time转换（常用格式转换（含时区）、计算时间差、时间叠加计算）；

6、LinuxPathToWindowsPath：路径转换；

7、QRCodeBuilder：二维码生成工具（自动生成、加入logo、截图识别、自定义格式）；

8、IdCardGenerator：身份证生成器；

9、RegexTester：正则表达式生成工具；

10、ShortURL：网址缩短（目前支持百度、新浪、缩我等短网址缩短）；

11、EscapeCharacter：转义字符（支持Html、XML、Java、JavaScript、CSV、Sql）；

12、ZHConverter：字符串转换（使用hanlp开源工具，实现拼音、简体-繁体、简体-臺灣正體、简体-香港繁體、繁體-臺灣正體、繁體-香港繁體、香港繁體-臺灣正體、数字金额-大写金额等直接的转换）；

13、Mq调试工具（目前仅支持ActiveMq）；

14、Http调试工具（支持自定义发送数据、header和cookie）；

15、json格式化编辑工具；

16、IconTool：图标生成工具；

17、RedisTool：Redis连接工具；

18、WebSourcesTool：网页源码下载工具；

19、SwitchHostsTool：切换Hosts工具；

20、FtpServer：Ftp服务器（快速搭建本地Ftp服务）；

21、CmdTool：Cmd调试工具；

22、FtpClientTool：Ftp(s)/Sftp客户端调试工具（批量上传、下载、删除文件及文件夹）（implicit/explicit SSL/TLS）；

23、PdfConvertTool：Pdf转换工具（目前仅支持pdf转图片、pdf转text功能）；

24、DirectoryTreeTool：文件列表生成器；

25、ImageTool：图片压缩工具（批量压缩、修改尺寸、转换格式）；

26、AsciiPicTool：图片转码工具（包括图片生成banner码、图片转Base64码）；

27、KafkaTool：Kafka调试工具（未完善）；

28、EmailTool：Email群发工具（支持自定义群发模版）；

29、ColorCodeConverterTool：颜色代码转换工具（包括16进制、RGB、ARGB、RGBA、HSL、HSV等代码之间转换）；

30、SmsTool：短信群发工具（目前支持中国移动、中国电信、腾讯云、阿里云、梦网云通讯等平台）；

31、ScriptEngineTool：脚本引擎调试工具（目前支持JavaScript、Groovy、Python、Lua等脚本）；

32、FileRenameTool：文件重命名工具（未完善）；

33、JsonConvertTool：Json转换工具（目前支持Json转Xml、Json转Java实体类、Json转C#实体类、Json转Excel、Json转Yaml）；

34、WechatJumpGameTool：微信跳一跳助手；

35、TextToSpeechTool：语音转换工具（调用百度语音转换api）；

36、2048：小游戏2048；

37、SocketTool：Socket调试工具(使用Apache Mina实现Tcp、Udp服务端和Client端)；

38、ImageAnalysisTool:图片解析工具（1、.atlas文件反解析；2、图片快速拆分工具）；

39、DecompilerWxApkgTool:微信小程序反编译工具（一键反编译微信小程序包）；

项目开发中，以后会陆续添加新工具，欢迎大家参与其中，多提提意见，谢谢。


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
│  │   │  ├─ melloware
│  │   │  │  └─ jintellitype	JIntellitype工具lib
│  │   │  └─ xwintop
│  │   │   └─ xJavaFxTool
│  │   │    └─ fxmlView     .fxml文件
│  │   ├─ config	配置文件
│  │   │  ├─ log4j.properties	log4j配置文件
│  │   │  └─ toolFxmlLoaderConfiguration.xml	系统菜单加载配置文件
│  │   ├─ css	样式资源
│  │   ├─ data	数据资源
│  │   ├─ images	图片资源
│  │   ├─ locale	国际化
│  │   └─ web	html工具
│  └─ test  测试类
│   ├─ java
│   └─ resources
├─ xJavaFxTool.jar	直接运行程序包

```

#### 特别感谢
在一个人还年轻的时候，我觉得，就应该着手致力做一些对社会有意义的事情，一如开源。至此，感谢以下贡献者（排名不分先后）：
+ 李柱
+ luming

#### 后续计划
不定期添加汇总开发过程中需求的痛点工具，大家有工作上的痛点处可进群讨论，后期可能就会出相应的工具解决方案，谢谢大家的支持。

#### 项目截图如下：

![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/文件复制.png "文件复制.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/邮件发送工具.png "邮件发送工具.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/Cron表达式生成器.png "Cron表达式生成器.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/Mq调试工具.png "Mq调试工具.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/正则表达式生成工具.png "正则表达式生成工具.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/二维码生成工具.png "二维码生成工具.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/json格式化编辑工具.png "json格式化编辑工具.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/网址缩短.png "网址缩短.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/字符串转换.png "字符串转换.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/Http调试工具.png "Http调试工具.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/编码转换.png "编码转换.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/转义字符.png "转义字符.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/加密解密.png "加密解密.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/Time转换.png "Time转换.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/图标生成工具.png "图标生成工具.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/Redis连接工具.png "Redis连接工具.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/Ftp服务器.png "Ftp服务器.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/文件列表生成器.png "文件列表生成器.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/图片压缩工具.png "图片压缩工具.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/Ftp客户端调试工具.png "Ftp客户端调试工具.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/Pdf转换工具.png "Pdf转换工具.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/图片转码工具.png "图片转码工具.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/Cmd调试工具.png "Cmd调试工具.png")
![输入图片说明](https://git.oschina.net/xwintop/xJavaFxTool/raw/master/images/短信群发工具.png "短信群发工具.png")
