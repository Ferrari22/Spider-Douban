/**
 *  程序入口类，线程池实现多线程爬虫，没有做任何反爬虫措施
 *  @author Scuderia
 */
package com.spider;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.io.IOException;

public class Main
{
    // 线程池大小
    private static final int threadPoolSize = Runtime.getRuntime().availableProcessors();
    // url队列
    private static LinkedBlockingQueue<String> queueUrl = new LinkedBlockingQueue<String>();
    // 同步主线程
    private static CountDownLatch latch1 = new CountDownLatch(1);

    /**
     *  拼凑URL，放入queueUrl队列中
     */
    public static void bookUrl() {
        boolean flag = true;
        for (int i = 0; i < 1000; i+=20) {
            String url = "https://book.douban.com/tag/小说?start=" + i +"&type=T";
            try {
                // 简单防止反爬虫
                Thread.sleep(1000);
                flag = Spider.getBookUrls(queueUrl, url);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                break;
            } catch (IOException e) {
                e.printStackTrace();
                // 获得信息出现异常，退出循环
                break;
            }
            // 若Url无效，则退出循环
            if (!flag)
                break;
        }
        latch1.countDown();
    }

    public static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    /**
    *   从队列中取出每个书页面url
    */
    static class Poll implements Runnable {

        private CountDownLatch latch;

        public Poll(CountDownLatch latch) {
            this.latch = latch;
        }        
        @Override
        public void run() {
            // 获取数目内容
            Spider.getBookInfos(queueUrl.poll());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            latch.countDown();
        }
    }

    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(threadPoolSize);
        // 获取URL集合
        bookUrl();
        // 等待所有URL入队
        await(latch1);
        int size = queueUrl.size();
        CountDownLatch latch2 = new CountDownLatch(size);
        for (int i = 0; i < size; i++)
            service.execute(new Poll(latch2));
        // 等待任务完成
        await(latch2);
        // 关闭线程池
        service.shutdown();
        // 释放连接池资源
        Spider.destory();
    }
}