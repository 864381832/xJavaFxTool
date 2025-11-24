package com.xwintop.xJavaFxTool.javafx;

public class PluginProgectPomBuildTool {
    public static String getPom_xml(String projectName) {
        return "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "\txsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "\t<modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "\t<groupId>com.xwintop</groupId>\n" +
                "\t<artifactId>x-"+projectName+"</artifactId>\n" +
                "\t<version>0.0.1</version>\n" +
                "\t<packaging>jar</packaging>\n" +
                "\t<name>x-" + projectName + "</name>\n" +
                "\n" +
                "\t<properties>\n" +
                "\t\t<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
                "\t</properties>\n" +
                "\n" +
                "\t<repositories>\n" +
                "\t\t<repository>\n" +
                "\t\t\t<id>aliyunmaven</id>\n" +
                "\t\t\t<url>http://maven.aliyun.com/nexus/content/groups/public/</url>\n" +
                "\t\t</repository>\n" +
                "\t\t<repository>\n" +
                "\t\t\t<id>xwintop-maven</id>\n" +
                "\t\t\t<url>https://xwintop.gitee.io/maven/repository</url>\n" +
                "\t\t</repository>\n" +
                "\t\t<repository>\n" +
                "\t\t\t<id>spring-snapshots</id>\n" +
                "\t\t\t<url>http://repo.spring.io/snapshot</url>\n" +
                "\t\t\t<snapshots>\n" +
                "\t\t\t\t<enabled>true</enabled>\n" +
                "\t\t\t</snapshots>\n" +
                "\t\t</repository>\n" +
                "\t\t<repository>\n" +
                "\t\t\t<id>spring-milestones</id>\n" +
                "\t\t\t<url>http://repo.spring.io/milestone</url>\n" +
                "\t\t</repository>\n" +
                "\t</repositories>\n" +
                "\n" +
                "\t<dependencies>\n" +
                "\t\t<dependency>\n" +
                "\t\t\t<groupId>junit</groupId>\n" +
                "\t\t\t<artifactId>junit</artifactId>\n" +
                "\t\t\t<version>4.12</version>\n" +
                "\t\t\t<scope>test</scope>\n" +
                "\t\t</dependency>\n" +
                "\t\t<dependency>\n" +
                "\t\t\t<groupId>com.xwintop</groupId>\n" +
                "\t\t\t<artifactId>xcore</artifactId>\n" +
                "\t\t\t<version>0.0.2-SNAPSHOT</version>\n" +
                "\t\t\t<scope>provided</scope>\n" +
                "\t\t</dependency>\n" +
                "\n" +
                "\t</dependencies>\n" +
                "\t\n" +
                "\t<build>\n" +
                "\t\t<plugins>\n" +
                "\t\t\t<plugin>\n" +
                "\t\t\t\t<groupId>org.apache.maven.plugins</groupId>\n" +
                "\t\t\t\t<artifactId>maven-assembly-plugin</artifactId>\n" +
                "\t\t\t\t<version>2.5.5</version>\n" +
                "\t\t\t\t<configuration>\n" +
                "\t\t\t\t\t<appendAssemblyId>false</appendAssemblyId>\n" +
                "\t\t\t\t\t<encoding>utf-8</encoding>\n" +
                "<!--\t\t\t\t\t<archive>-->\n" +
                "<!--\t\t\t\t\t\t<manifest>-->\n" +
                "<!--\t\t\t\t\t\t\t<mainClass>com.xwintop.xJavaFxTool.Main</mainClass>-->\n" +
                "<!--\t\t\t\t\t\t</manifest>-->\n" +
                "<!--\t\t\t\t\t</archive>-->\n" +
                "\t\t\t\t\t<descriptorRefs>\n" +
                "\t\t\t\t\t\t<descriptorRef>jar-with-dependencies</descriptorRef>\n" +
                "\t\t\t\t\t</descriptorRefs>\n" +
                "\t\t\t\t</configuration>\n" +
                "\t\t\t\t<executions>\n" +
                "\t\t\t\t\t<execution>\n" +
                "\t\t\t\t\t\t<id>make-assembly</id>\n" +
                "\t\t\t\t\t\t<phase>package</phase>\n" +
                "\t\t\t\t\t\t<goals>\n" +
                "\t\t\t\t\t\t\t<goal>single</goal>\n" +
                "\t\t\t\t\t\t</goals>\n" +
                "\t\t\t\t\t</execution>\n" +
                "\t\t\t\t</executions>\n" +
                "\t\t\t</plugin>\n" +
                "\t\t</plugins>\n" +
                "\t</build>\n" +
                "</project>\n";
    }

    public static String getGitignore(String projectName) {
        return "# Default ignored files\n" +
                "#/.gitignore\n" +
                "/x-" + projectName + ".iml\n" +
                "/log/\n" +
                "/target/";
    }

    public static String getMain_java(String projectName, String xmlPath) {
        return "package com.xwintop.xJavaFxTool;\n" +
                "\n" +
                "import javafx.application.Application;\n" +
                "import javafx.event.EventHandler;\n" +
                "import javafx.fxml.FXMLLoader;\n" +
                "import javafx.scene.Parent;\n" +
                "import javafx.scene.Scene;\n" +
                "import javafx.stage.Stage;\n" +
                "import javafx.stage.WindowEvent;\n" +
                "import lombok.extern.slf4j.Slf4j;\n" +
                "\n" +
                "import java.net.URL;\n" +
                "import java.util.ResourceBundle;\n" +
                "\n" +
                "@Slf4j\n" +
                "public class Main extends Application {\n" +
                "    public static void main(String[] args) {\n" +
                "        try {\n" +
                "            launch(args);\n" +
                "        } catch (Exception e) {\n" +
                "            log.error(e.getMessage(), e);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void start(Stage primaryStage) throws Exception {\n" +
                "        FXMLLoader fXMLLoader = Main.getFXMLLoader();\n" +
                "        ResourceBundle resourceBundle = fXMLLoader.getResources();\n" +
                "        Parent root = fXMLLoader.load();\n" +
                "        primaryStage.setResizable(true);\n" +
                "        primaryStage.setTitle(resourceBundle.getString(\"Title\"));\n" +
                "//        primaryStage.getIcons().add(new Image(\"/images/icon.jpg\"));\n" +
                "        primaryStage.setScene(new Scene(root));\n" +
                "        primaryStage.show();\n" +
                "        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {\n" +
                "            @Override\n" +
                "            public void handle(WindowEvent event) {\n" +
                "                System.exit(0);\n" +
                "            }\n" +
                "        });\n" +
                "    }\n" +
                "\n" +
                "    public static FXMLLoader getFXMLLoader() {\n" +
                "        ResourceBundle resourceBundle = ResourceBundle.getBundle(\"locale." + projectName + "\");\n" +
                "        URL url = Object.class.getResource(\"/com/xwintop/xJavaFxTool/fxmlView/" + xmlPath + "/" + projectName + ".fxml\");\n" +
                "        FXMLLoader fXMLLoader = new FXMLLoader(url, resourceBundle);\n" +
                "        return fXMLLoader;\n" +
                "    }\n" +
                "}";
    }

    public static String getToolFxmlLoaderConfiguration_xml(String projectName, String xmlPath) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<root>\n" +
                "\t<ToolFxmlLoaderConfiguration title=\"" + xmlPath + "\" menuId=\"p-" + xmlPath + "\" menuParentId=\"moreToolsMenu\" isMenu=\"true\" />\n" +
                "\t<ToolFxmlLoaderConfiguration>\n" +
                "\t\t<url>/com/xwintop/xJavaFxTool/fxmlView/" + xmlPath + "/" + projectName + ".fxml</url>\n" +
                "\t\t<resourceBundleName>locale." + projectName + "</resourceBundleName>\n" +
                "\t\t<className></className>\n" +
                "\t\t<title>Title</title>\n" +
                "\t\t<isDefaultShow></isDefaultShow>\n" +
                "\t\t<menuId></menuId>\n" +
                "\t\t<menuParentId>p-" + xmlPath + "</menuParentId>\n" +
                "\t\t<controllerType>Node</controllerType>\n" +
                "\t</ToolFxmlLoaderConfiguration>\n" +
                "</root>";
    }

    public static String getPluginList_Json(String projectName, String xmlPath){
        return "{\n" +
                "    \"name\": \""+projectName+"\",\n" +
                "    \"synopsis\": \"\",\n" +
                "    \"jarName\": \"x-"+projectName+"\",\n" +
                "    \"version\": \"0.0.1\",\n" +
                "    \"versionNumber\": 1,\n" +
                "    \"downloadUrl\": \"https://xwintop.gitee.io/xjavafxtool-plugin/plugin-libs/"+xmlPath+"x-"+projectName+"-0.0.1.jar\"\n" +
                "  },";
    }

    public static String getLocal_properties(String projectName) {
        return "# Dorian.properties是默认的\"Dorian\"资源束文件。  \n" +
                "# 作为中国人,我用自己的地区作为默认  \n" +
                "Title=" + projectName;
    }

    public static String getLocal_en_US_properties(String projectName) {
        return "# 文件Dorian_en_US.properties，是美国地区的资源束  \n" +
                "# 它覆盖了默认资源束  \n" +
                "Title=" + projectName;
    }
}
