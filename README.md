# Spider-Douban
基于连接池、线程池的Java爬虫
## ConnectionPool
连接池支持在一定时间内获取不到连接即返回null，释放连接，只支持简单操作，后面会继续完善。
## Spider
因为主要测试连接池，所以爬虫只是简单线程Sleep处理，并没有做任何反爬虫处理，后期会加上。
## Main
程序入口，使用线程池管理线程，使用CountDownLatch来同步任务。
## Run
环境基于JDK1.8，可以 java com.spider.Main 直接运行 
