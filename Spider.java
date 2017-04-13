/**
 *  爬虫类，实现HTML内容解析，插入数据库
 *  @author Scuderia
 */
package com.spider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import com.db.DBHandler;

public class Spider
{
    // 数据库操作对象
    private static DBHandler dh;

    static {
        dh = new DBHandler();
    }

    /**
     *  获取书目列表
     *  由于每个页面在拼凑URL时，没有数目信息也能获取页面，所有要判断是否能获取到信息
     */
    private static boolean getBooksUrl(LinkedBlockingQueue<String> queue, String urlTopics) throws IOException {

        System.out.println("正在抓取： " + urlTopics);
        Document doc;
        try {
            doc = Jsoup.connect(urlTopics)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                .get();
            Elements listDiv = doc.getElementsByAttributeValue("class","info");
            // 判断urlTopics为无效值
            if (listDiv.isEmpty())
                return false;
            for (Element element :listDiv) {
                Elements links = element.getElementsByTag("a");
                for (Element link : links) {
                    String linkHref = link.attr("href");
                    // 筛选正确的地址
                    if (linkHref.length() > 43)
                        continue;
                    if(!queue.offer(linkHref))
                        return false;
                }
            }
        } catch (IOException ie) {
            throw ie;
        }
        return true;
    }
    /**
    *   从每个页面中分析结果
    */
    private static void getBooksInfo(String urlBook) {
        String[] params = new String[3];
        Document doc;
        try {
            doc = Jsoup.connect(urlBook)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                .get();
            // 获取书名
            Elements divInfo = doc.getElementsByAttributeValue("id","wrapper");
            for (Element element :divInfo) {
                Elements h1 = element.getElementsByTag("h1");
                params[0] = h1.text();
            }
            // 获取豆瓣评分
            Elements ratingNum = doc.select("strong.ll").select("strong.rating_num");
            params[1] = ratingNum.text();
            // 获取内容、作者简介
            Elements introductions = doc.select("div.intro");
            String str = "";
            for (Element intro : introductions) {
                str += intro.text();
                str += "\n";
            }
            params[2] = str;
            dh.insert(params);
            System.out.println(params[0]);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public static boolean getBookUrls(LinkedBlockingQueue<String> queue, String urlTopics) throws IOException {
        return getBooksUrl(queue, urlTopics);
    }

    public static void getBookInfos(String urlBook) {
        getBooksInfo(urlBook);
    }
    /**
    *   释放资源
    */
    public static void destory() {
        dh.shutdown();
    }
}