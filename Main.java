/**
 *  ��������࣬�̳߳�ʵ�ֶ��߳����棬û�����κη������ʩ
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
    // �̳߳ش�С
    private static final int threadPoolSize = Runtime.getRuntime().availableProcessors();
    // url����
    private static LinkedBlockingQueue<String> queueUrl = new LinkedBlockingQueue<String>();
    // ͬ�����߳�
    private static CountDownLatch latch1 = new CountDownLatch(1);

    /**
     *  ƴ��URL������queueUrl������
     */
    public static void bookUrl() {
        boolean flag = true;
        for (int i = 0; i < 1000; i+=20) {
            String url = "https://book.douban.com/tag/С˵?start=" + i +"&type=T";
            try {
                // �򵥷�ֹ������
                Thread.sleep(1000);
                flag = Spider.getBookUrls(queueUrl, url);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                break;
            } catch (IOException e) {
                e.printStackTrace();
                // �����Ϣ�����쳣���˳�ѭ��
                break;
            }
            // ��Url��Ч�����˳�ѭ��
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
    *   �Ӷ�����ȡ��ÿ����ҳ��url
    */
    static class Poll implements Runnable {

        private CountDownLatch latch;

        public Poll(CountDownLatch latch) {
            this.latch = latch;
        }        
        @Override
        public void run() {
            // ��ȡ��Ŀ����
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
        // ��ȡURL����
        bookUrl();
        // �ȴ�����URL���
        await(latch1);
        int size = queueUrl.size();
        CountDownLatch latch2 = new CountDownLatch(size);
        for (int i = 0; i < size; i++)
            service.execute(new Poll(latch2));
        // �ȴ��������
        await(latch2);
        // �ر��̳߳�
        service.shutdown();
        // �ͷ����ӳ���Դ
        Spider.destory();
    }
}