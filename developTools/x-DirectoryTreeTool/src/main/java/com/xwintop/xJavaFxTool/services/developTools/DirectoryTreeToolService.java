package com.xwintop.xJavaFxTool.services.developTools;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.xwintop.xJavaFxTool.controller.developTools.DirectoryTreeToolController;
import com.xwintop.xJavaFxTool.utils.DirectoryTreeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: DirectoryTreeToolService
 * @Description: 文件列表生成器工具
 * @author: xufeng
 * @date: 2017年11月13日 下午4:34:11
 */
@Getter
@Setter
@Slf4j
public class DirectoryTreeToolService {
	private DirectoryTreeToolController directoryTreeToolController;
	private Integer totalDirNum = 0;
	private Integer totalFileNum = 0;

	public DirectoryTreeToolService(DirectoryTreeToolController directoryTreeToolController) {
		this.directoryTreeToolController = directoryTreeToolController;
	}

	public void buildDirectoryTreeString() {
		String projectPath = directoryTreeToolController.getProjectPathTextField().getText();
		if (StringUtils.isEmpty(projectPath)) {
			projectPath = System.getProperty("user.dir");
		}
		File generateFile = new File(projectPath);
		totalFileNum = 0;
		totalDirNum = 0;
		DirectoryTreeUtil directoryTreeTUtil = new DirectoryTreeUtil(generateFile);
		FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (directoryTreeToolController.getIsFileCheckBox().isSelected()) {
					if (!pathname.isFile()) {
						return false;
					}
				}
				if (directoryTreeToolController.getIsDirCheckBox().isSelected()) {
					if (!pathname.isDirectory()) {
						return false;
					}
				}
				if (!directoryTreeToolController.getShowHideFileCheckBox().isSelected()) {
					if (pathname.isHidden()) {
						return false;
					}
				}
				if (directoryTreeToolController.getNotShowEmptyListCheckBox().isSelected()) {
					if (pathname.isDirectory()) {
						if (pathname.listFiles().length == 0) {
							return false;
						}
					}
				}
				if (directoryTreeToolController.getFiltrationUsingCheckBox().isSelected()) {
					for (Map<String, String> action : directoryTreeToolController.getTableData()) {
						String[] filtrationTypeChoiceBoxStrings = directoryTreeToolController
								.getFiltrationTypeChoiceBoxStrings();
						String filtrationType = action.get("filtrationType");
						String filtrationCondition = action.get("filtrationCondition");
						String scopeType = action.get("scopeType");
						if (scopeType.equals(directoryTreeToolController.getScopeTypeChoiceBoxStrings()[1])
								&& !pathname.isDirectory()) {
							continue;
						}
						if (scopeType.equals(directoryTreeToolController.getScopeTypeChoiceBoxStrings()[2])
								&& pathname.isDirectory()) {
							continue;
						}
						if (filtrationType.equals(filtrationTypeChoiceBoxStrings[0])) {
							if (pathname.getName().contains(filtrationCondition)) {
								return false;
							}
						} else if (filtrationType.equals(filtrationTypeChoiceBoxStrings[1])) {
							if (pathname.getName().startsWith(filtrationCondition)) {
								return false;
							}
						} else if (filtrationType.equals(filtrationTypeChoiceBoxStrings[2])) {
							if (pathname.getName().endsWith(filtrationCondition)) {
								return false;
							}
						} else if (filtrationType.equals(filtrationTypeChoiceBoxStrings[3])) {
							if (!pathname.getName().contains(filtrationCondition)) {
								return false;
							}
						}
					}
				}
				if (pathname.isDirectory()) {
					totalDirNum++;
				}
				if (pathname.isFile()) {
					totalFileNum++;
				}
				return true;
			}
		};
		directoryTreeTUtil.setFileFilter(fileFilter);
		if (directoryTreeToolController.getShowDirDepthCheckBox().isSelected()) {
			directoryTreeTUtil.setDeep(directoryTreeToolController.getShowDirDepthSpinner().getValue());
		}
		if (!directoryTreeToolController.getIncludeSubdirectoryCheckBox().isSelected()) {
			directoryTreeTUtil.setDeep(0);
		}
		if (directoryTreeToolController.getShowFileLengthCheckBox().isSelected()) {
			directoryTreeTUtil.showLength();
		}
		if (directoryTreeToolController.getShowModifyCheckBox().isSelected()) {
			directoryTreeTUtil.showModify();
		}
		if (directoryTreeToolController.getShowPermissionCheckBox().isSelected()) {
			directoryTreeTUtil.showPermission();
		}
		String generate = directoryTreeTUtil.generate();
		directoryTreeToolController.getShowDirectoryTreeTextArea().setText(generate);
	}

	public void showFileInfo() {
		directoryTreeToolController.getTotalDirNumLabel().setText("" + totalDirNum);
		directoryTreeToolController.getTotalFileNumLabel().setText("" + totalFileNum);
	}
}