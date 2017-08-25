//package com.xwintop.xJavaFxTool.tools;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.junit.Test;
//
//import name.pachler.nio.file.ClosedWatchServiceException;
//import name.pachler.nio.file.FileSystems;
//import name.pachler.nio.file.Path;
//import name.pachler.nio.file.Paths;
//import name.pachler.nio.file.StandardWatchEventKind;
//import name.pachler.nio.file.WatchEvent;
//import name.pachler.nio.file.WatchKey;
//import name.pachler.nio.file.WatchService;
//
///** 
// * @ClassName: JpatchwatchTest 
// * @Description: 目录文件监听工具类
// * @author: xufeng
// * @date: 2017年8月25日 上午10:36:21  
// */
//public class JpatchwatchTest {
//	@Test
//	public void testJpatchwatch() {
//		WatchService watchService = FileSystems.getDefault().newWatchService();
//		Path watchedPath = Paths.get("E:/testImage1");
//		WatchKey key = null;
//		try {
//			key = watchedPath.register(watchService, StandardWatchEventKind.ENTRY_CREATE,
//					StandardWatchEventKind.ENTRY_DELETE);
//		} catch (UnsupportedOperationException uox) {
//			System.err.println("filehing not supported!");
//			// handle this error here
//		} catch (IOException iox) {
//			System.err.println("I/Ors");
//			// handle this error here
//		}
//
//		for (;;) {
//			// take() will block until a file has been created/deleted
//			WatchKey signalledKey;
//			try {
//				signalledKey = watchService.take();
//			} catch (InterruptedException ix) {
//				// we'll ignore being interrupted
//				continue;
//			} catch (ClosedWatchServiceException cwse) {
//				// other thread closed watch service
//				System.out.println("watchice closed, terminating.");
//				break;
//			}
//
//			// get list of events from key
//			List<WatchEvent<?>> list = signalledKey.pollEvents();
//
//			// VERY IMPORTANT! call reset() AFTER pollEvents() to allow the
//			// key to be reported again by the watch service
//			signalledKey.reset();
//
//			// we'll simply print what has happened; real applications
//			// will do something more sensible here
//			for (WatchEvent e : list) {
//				String message = "";
//				if (e.kind() == StandardWatchEventKind.ENTRY_CREATE) {
//					Path context = (Path) e.context();
//					message = context.toString() + " created";
//				} else if (e.kind() == StandardWatchEventKind.ENTRY_DELETE) {
//					Path context = (Path) e.context();
//					message = context.toString() + " deleted";
//				} else if (e.kind() == StandardWatchEventKind.OVERFLOW) {
//					message = "OVERFLOW: more changes happened than we could retreive";
//				}
//				System.out.println(message);
//			}
//		}
//	}
//}
