/**
 *  �����࣬ʵ��HTML���ݽ������������ݿ�
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
    // ���ݿ��������
    private static DBHandler dh;

    static {
        dh = new DBHandler();
    }

    /**
     *  ��ȡ��Ŀ�б�
     *  ����ÿ��ҳ����ƴ��URLʱ��û����Ŀ��ϢҲ�ܻ�ȡҳ�棬����Ҫ�ж��Ƿ��ܻ�ȡ����Ϣ
     */
    private static boolean getBooksUrl(LinkedBlockingQueue<String> queue, String urlTopics) throws IOException {

        System.out.println("����ץȡ�� " + urlTopics);
        Document doc;
        try {
            doc = Jsoup.connect(urlTopics)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                .get();
            Elements listDiv = doc.getElementsByAttributeValue("class","info");
            // �ж�urlTopicsΪ��Чֵ
            if (listDiv.isEmpty())
                return false;
            for (Element element :listDiv) {
                Elements links = element.getElementsByTag("a");
                for (Element link : links) {
                    String linkHref = link.attr("href");
                    // ɸѡ��ȷ�ĵ�ַ
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
    *   ��ÿ��ҳ���з������
    */
    private static void getBooksInfo(String urlBook) {
        String[] params = new String[3];
        Document doc;
        try {
            doc = Jsoup.connect(urlBook)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                .get();
            // ��ȡ����
            Elements divInfo = doc.getElementsByAttributeValue("id","wrapper");
            for (Element element :divInfo) {
                Elements h1 = element.getElementsByTag("h1");
                params[0] = h1.text();
            }
            // ��ȡ��������
            Elements ratingNum = doc.select("strong.ll").select("strong.rating_num");
            params[1] = ratingNum.text();
            // ��ȡ���ݡ����߼��
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
    *   �ͷ���Դ
    */
    public static void destory() {
        dh.shutdown();
    }
}