SwitchHostsTool 切换Hosts工具

#### 项目简介：
SwitchHostsTool是使用javafx开发的一款切换Hosts工具，可快速编辑hosts文件内容，使用[richtextfx](https://github.com/FXMisc/RichTextFX)工具作为编辑器。
提供一键更新GitHub域名地址，快速解决GitHub无法访问问题。

**xJavaFxTool交流QQ群：== [387473650(此群已满)](https://jq.qq.com/?_wv=1027&k=59UDEAD) 请加群②[1104780992](https://jq.qq.com/?_wv=1027&k=bhAdkju9) ==**

#### 环境搭建说明：
- 开发环境为jdk1.8，基于maven构建
- 使用eclipase或Intellij Idea开发(推荐使用[Intellij Idea](https://www.jetbrains.com/?from=xJavaFxTool))
- 该项目为javaFx开发的实用小工具集[xJavaFxTool](https://gitee.com/xwintop/xJavaFxTool)的插件。
- 本项目使用了[lombok](https://projectlombok.org/),在查看本项目时如果您没有下载lombok 插件，请先安装,不然找不到get/set等方法
- 依赖的[xcore包](https://gitee.com/xwintop/xcore)已上传至git托管的maven平台，git托管maven可参考教程(若无法下载请拉取项目自行编译)。[教程地址：点击进入](http://blog.csdn.net/u011747754/article/details/78574026)

![切换Hosts工具.png](images/切换Hosts工具.png)

2020-12-13 v0.0.2
1. 优化界面布局，隐藏新增、删除按钮

2021-02-23 v0.0.3
1. 添加GitHub域名解析地址快速获取替换功能