封装java开发过程中常用的工具类。

**注意：**

- 因为代码变动频繁，建议自行下载源码，用 `mvn install` 命令安装，或者
- 从 [dist](https://gitee.com/xwintop/xcore/tree/master/dist) 目录下载临时打包版本，或者
- 添加 gitee 的 maven 托管库依赖

> 因为 "xcore" 是 gitee 的敏感词，因此无法将打包上传到 release 页面。带来不便敬请谅解。

## gitee 的 maven 托管库：

在 `pom.xml` 中加入下面内容可从 gitee 的托管库中下载依赖

```
<repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.gitee.xwintop</groupId>
        <artifactId>xcore</artifactId>
        <version>0.0.7</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

git 托管 maven可参考教程(若无法下载请拉取项目自行编译)。[教程地址：点击进入](http://blog.csdn.net/u011747754/article/details/78574026)

#### 版本记录
- 0.0.1-SNAPSHOT
  1. 完成基本功能配置
- 0.0.2-SNAPSHOT  2020-02-25
  1. 优化对话框等
- 0.0.3-SNAPSHOT  2020-02-26
  1. 移除多余引用的包
  2. 移除部分工具类
- 0.0.4-SNAPSHOT  2020-03-16
  1. 优化界面布局
- 0.0.5  2020-03-16
  1. 添加进度条对话框
- 0.0.6 2020-07-04
  1. 添加历史输入框功能封装
  2. 优化部分功能代码
- 0.0.7 2022-03-29
  1. 升级jdk版本为17
  2. 优化部分功能代码