package com.xwintop.xJavaFxTool.tools;

import java.io.File;

import org.junit.Test;

import com.xwintop.xJavaFxTool.utils.DirectoryTreeUtil;

public class DirectoryTreeTest {
	@Test
	public void directoryTreeTest() {
		File generateFile = new File("E:\\GitFiles\\publicServive");
		String generate = DirectoryTreeV1.create(generateFile).setDeep(20)
				.setFileFilter(pathname -> (!(pathname.isHidden() || pathname.getName().contains("java"))))
				/*
				 * .showLength() .showModify() .showPermission()
				 * .addAppendContent(new DirectoryTreeV1.AppendContent() {
				 * 
				 * @Override public String appendContent(File file) { return "["
				 * + file.getPath() + "]"; } })
				 */
				.generate(generateFile);
		System.out.println(generate);
	}

	@Test
	public void test1() {
		// File generateFile = new File("E:\\GitFiles\\publicServive");
		File generateFile = new File(System.getProperty("user.dir"));
		DirectoryTreeUtil directoryTreeTUtil = new DirectoryTreeUtil(generateFile);
		directoryTreeTUtil.setFileFilter(pathname -> (!(pathname.isHidden() || pathname.getName().endsWith(".java")
				|| pathname.getName().startsWith("bundles") || pathname.getName().startsWith(".")
				|| pathname.getName().startsWith("target") || pathname.getName().startsWith("logs")
				|| pathname.getName().endsWith(".class") || pathname.getName().endsWith(".png")
				|| pathname.getName().endsWith(".js") || pathname.getName().endsWith(".css")
				|| pathname.getName().endsWith(".html") || pathname.getName().endsWith(".jpg")
				|| pathname.getName().endsWith(".fxml") || pathname.getName().endsWith(".dll"))));
//		directoryTreeTUtil.showLength();
//		directoryTreeTUtil.showModify();
//		directoryTreeTUtil.showPermission();
		String generate = directoryTreeTUtil.generate();
		System.out.println(generate);
	}
}
