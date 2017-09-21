//package com.xwintop.xJavaFxTool.tools;
//
//import org.junit.Test;
//
//import lombok.extern.log4j.Log4j;
//import us.codecraft.webmagic.Page;
//import us.codecraft.webmagic.Site;
//import us.codecraft.webmagic.Spider;
//import us.codecraft.webmagic.processor.PageProcessor;
//
//@Log4j
//public class WebmagicTest implements PageProcessor {
//	// 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
//    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
//
//    @Override
//    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
//    public void process(Page page) {
//    	log.info(page.getHtml());
//    	log.info(page.getHtml().links().all());
//        // 部分二：定义如何抽取页面信息，并保存下来
//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
//        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
//        if (page.getResultItems().get("name") == null) {
//            //skip this page
//            page.setSkip(true);
//        }
//        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
//
//        // 部分三：从页面发现后续的url地址来抓取
////        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/[\\w\\-]+/[\\w\\-]+)").all());
//        page.addTargetRequests(page.getHtml().links().all());
//    }
//
//    @Override
//    public Site getSite() {
//        return site;
//    }
//
//    @Test
//    public void test(){
//    	Spider.create(new WebmagicTest())
//        //从"https://github.com/code4craft"开始抓
////        .addUrl("http://ofo.xwintop.com/")
//        .addUrl("http://127.0.0.1:8020/xTool/vueTest/index.html")
//        //开启5个线程抓取
//        .thread(5)
//        //启动爬虫
//        .run();
//    }
//    
//}
