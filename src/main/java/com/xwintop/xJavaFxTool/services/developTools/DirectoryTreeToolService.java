package com.xwintop.xJavaFxTool.services.developTools;

import com.xwintop.xJavaFxTool.controller.developTools.DirectoryTreeToolController;
import com.xwintop.xJavaFxTool.utils.DirectoryTreeUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * @ClassName: DirectoryTreeToolService
 * @Description: 文件列表生成器工具
 * @author: xufeng
 * @date: 2017年11月13日 下午4:34:11
 */
@Getter
@Setter
@Log4j
public class DirectoryTreeToolService {

    private DirectoryTreeToolController directoryTreeToolController;

    public DirectoryTreeToolService(DirectoryTreeToolController directoryTreeToolController) {
        this.directoryTreeToolController = directoryTreeToolController;
    }

    public void buildDirectoryTreeString(){
        String projectPath = directoryTreeToolController.getProjectPathTextField().getText();
        if(StringUtils.isEmpty(projectPath)){
            projectPath = System.getProperty("user.dir");
        }
        File generateFile = new File(projectPath);
        DirectoryTreeUtil directoryTreeTUtil = new DirectoryTreeUtil(generateFile);
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(directoryTreeToolController.getIsFileCheckBox().isSelected()){
                    if(!pathname.isFile()){
                        return false;
                    }
                }
                if(directoryTreeToolController.getIsDirCheckBox().isSelected()){
                    if(!pathname.isDirectory()){
                        return false;
                    }
                }
                if(!directoryTreeToolController.getShowHideFileCheckBox().isSelected()){
                    if(pathname.isHidden()){
                        return false;
                    }
                }
                if(directoryTreeToolController.getNotShowEmptyListCheckBox().isSelected()){
                    if(pathname.isDirectory()){
                        if(pathname.listFiles().length == 0){
                            return false;
                        }
                    }
                }
                if(directoryTreeToolController.getFiltrationUsingCheckBox().isSelected()){
                    for(Map<String,String> action : directoryTreeToolController.getTableData()){
                        if(action.get("filtrationType").equals(directoryTreeToolController.getFiltrationTypeChoiceBoxStrings()[0])){
                            if(directoryTreeToolController.getFiltrationDirName().isSelected() || !pathname.isDirectory()){
                                if(pathname.getName().contains(action.get("filtrationCondition"))){
                                    return false;
                                }
                            }
                        }else if(action.get("filtrationType").equals(directoryTreeToolController.getFiltrationTypeChoiceBoxStrings()[1])){
                            if(directoryTreeToolController.getFiltrationDirName().isSelected() || !pathname.isDirectory()){
                                if(pathname.getName().startsWith(action.get("filtrationCondition"))){
                                    return false;
                                }
                            }
                        }else if(action.get("filtrationType").equals(directoryTreeToolController.getFiltrationTypeChoiceBoxStrings()[2])){
                            if(directoryTreeToolController.getFiltrationDirName().isSelected() || !pathname.isDirectory()){
                                if(pathname.getName().endsWith(action.get("filtrationCondition"))){
                                    return false;
                                }
                            }
                        }else if(action.get("filtrationType").equals(directoryTreeToolController.getFiltrationTypeChoiceBoxStrings()[3])){
                            if(directoryTreeToolController.getFiltrationDirName().isSelected() || !pathname.isDirectory()){
                                if(!pathname.getName().contains(action.get("filtrationCondition"))){
                                    return false;
                                }
                            }
                        }
                    }
                }
                return true;
            }
        };
        directoryTreeTUtil.setFileFilter(fileFilter);
//        directoryTreeTUtil.setFileFilter(pathname -> (!(pathname.isHidden() || pathname.getName().endsWith(".java")
//                || pathname.getName().startsWith("bundles") || pathname.getName().startsWith(".")
//                || pathname.getName().startsWith("target") || pathname.getName().startsWith("logs")
//                || pathname.getName().endsWith(".class") || pathname.getName().endsWith(".png")
//                || pathname.getName().endsWith(".js") || pathname.getName().endsWith(".css")
//                || pathname.getName().endsWith(".html") || pathname.getName().endsWith(".jpg")
//                || pathname.getName().endsWith(".fxml") || pathname.getName().endsWith(".dll"))));
        if(directoryTreeToolController.getShowDirDepthCheckBox().isSelected()){
            directoryTreeTUtil.setDeep(directoryTreeToolController.getShowDirDepthSpinner().getValue());
        }
        if(!directoryTreeToolController.getIncludeSubdirectoryCheckBox().isSelected()){
            directoryTreeTUtil.setDeep(0);
        }
        String generate = directoryTreeTUtil.generate();
        directoryTreeToolController.getShowDirectoryTreeTextArea().setText(generate);
    }
}