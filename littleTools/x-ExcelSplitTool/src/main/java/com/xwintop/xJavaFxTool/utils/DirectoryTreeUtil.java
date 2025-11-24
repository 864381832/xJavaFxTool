package com.xwintop.xJavaFxTool.utils;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 目录树生成工具. {@linkplain #setDeep(int) 设置查询目录深度}
 * {@linkplain #showLength()} 显示文件大小内容} {@linkplain #showModify() 显示文件修改时间内容}
 * {@linkplain #showPermission() 显示文件权限内容}
 * {@linkplain #addAppendContent(Function) 自定义显示文件内容信息}
 *
 * @author xufeng
 */
@Getter
@Setter
public class DirectoryTreeUtil {
	private File generateFile;// 初始文件夹
	/* 换行符 */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	/* 空字符串 */
	private static final String EMPTY = "";
	/* 文件连接符 */
	private static final String VERTICAL = "│ ", INTERMEDIATE = "├─", END = "└─";
	/* 目录间距 */
//	private static final String SPACING = "\t";
	private static final String SPACING = " ";
	/* 结果集收集 */
	private final StringBuilder r = new StringBuilder();
	/* 默认查询文件目录深度,默认为Integer.MAX_VALUE */
	private int deep = Integer.MAX_VALUE;
	/* 文件筛选过滤器 */
	private FileFilter fileFilter = pathname -> true;
	/* 写出文件名及其他信息，默认只输出文件名称 */
	private AppendTo displayContent = new AppendTo(file -> " " + file.getName());
	private Comparator<File> sort = null;// 自定义排序显示结果 tree.

	public DirectoryTreeUtil() {
	}

	public DirectoryTreeUtil(File generateFile) {
		this.generateFile = generateFile;
	}

	/**
	 * 目录读取子文件.
	 */
	List<File> fetchFiles(File file) {
		final File[] files = file.listFiles(this.fileFilter);
		List<File> fList = (files == null ? Collections.emptyList() : Arrays.asList(files));
		if (sort != null) {
			fList.sort(sort);
		}
		return fList;
	}

	/**
	 * 显示文件大小.
	 *
	 * @return the tree
	 */
	public DirectoryTreeUtil showLength() {
		this.displayContent.add(file -> ("[" + file.length() + "B" + "]"));
		return this;
	}

	/**
	 * 显示文件修改时间.
	 *
	 * @return the tree
	 */
	public DirectoryTreeUtil showModify() {
		this.displayContent.add(file -> ("[" + new Date(file.lastModified()) + "]"));
		return this;
	}

	/**
	 * 显示文件权限.
	 *
	 * @return the tree
	 */
	public DirectoryTreeUtil showPermission() {
		this.displayContent.add(file -> ("[" + (file.canRead() ? "r-" : "--") + (file.canWrite() ? "w-" : "--")
				+ (file.canExecute() ? "x-" : "--") + "]"));
		return this;
	}

	/**
	 * 自定义添加读取 file 解析内容到输出内容.
	 *
	 * @param appendContent
	 *            the append content
	 * @return the tree
	 */
	public DirectoryTreeUtil addAppendContent(Function<File, String> appendContent) {
		this.displayContent.add(appendContent);
		return this;
	}

	/**
	 * 生成文件.
	 *
	 * @return 结果内容
	 */
	public final String generate() {
		if (generateFile.exists()) {
			this.generateHandle(generateFile, EMPTY, 0);
		} else {
			System.err.println(generateFile.getPath() + " not found!");
		}
		return this.r.toString();
	}

	private void generateHandle(File file, String prefix, int deep) {
		final List<File> files = this.fetchFiles(file);
		if (files.isEmpty()) {
			return;
		}
		deep++;
		final int length = files.size();
		for (int i = 0; i < length; i++) {
			final File f = files.get(i);

			final boolean isLast = (i >= length - 1);
			this.r.append(prefix).append(isLast ? END : INTERMEDIATE);
			this.appendDisplayContent(f);
			this.r.append(LINE_SEPARATOR);

			if (f.isDirectory() && deep <= this.deep) {
				this.generateHandle(f, prefix + (!(length <= 1 || isLast) ? VERTICAL : EMPTY) + SPACING, deep);
			}
		}
	}

	/**
	 * 处理定义文件内容
	 *
	 * @param f
	 *            f
	 */
	private void appendDisplayContent(File f) {
		final List<Function<File, String>> appendContents = displayContent.appendContents;
		for (Function<File, String> to : appendContents) {
			this.r.append(to.apply(f));
		}
	}

	/**
	 * 可累积显示 tree 中具体文件属性内容
	 */
	private static class AppendTo {
		private final List<Function<File, String>> appendContents = new ArrayList<>();

		AppendTo(Function<File, String> appendTo) {
			if (appendTo != null) {
				this.appendContents.add(appendTo);
			}
		}

		void add(Function<File, String> to) {
			if (to != null) {
				this.appendContents.add(0, to);
			}
		}
	}

	/**
	 * 判断文件(夹)名是否满足匹配.
	 */
	public static boolean ifMatchText(String fileName, String csText, String ncsText, boolean sRegex, Pattern csPattern, Pattern ncsPattern) {
		boolean match = true;
		String lFileName = fileName.toLowerCase();
		String lcsText = csText.toLowerCase();
		String lncsText = ncsText.toLowerCase();
		if (sRegex) {
			if (csText.length() != 0) {
				Matcher m = csPattern.matcher(fileName);
				match = m.find();
			}
			if (match && ncsText.length() != 0) {
				Matcher m = ncsPattern.matcher(fileName);
				match = !m.find();
			}
		} else {
			if (csText.length() != 0) {
				match = lFileName.contains(lcsText);
			}
			if (match && ncsText.length() != 0) {
				match = !lFileName.contains(lncsText);
			}
		}
		return match;
	}
}
