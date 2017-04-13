/**
 *  连接池实现，支持获取连接，释放连接
 *  @author Scuderia
 */
package com.pool;

import java.util.LinkedList;
import java.sql.Connection;

public class ConnectionPool {

    // 连接池默认大小
    private static final int INIT_POOLSIZE = 5;
    // 连接队列
    private LinkedList<Connection> pool = new LinkedList<Connection>();

    public ConnectionPool(int initialSize) {
        if (initialSize < 0)
            throw new IllegalArgumentException();
        if (initialSize == 0) {
            initPool(INIT_POOLSIZE);
        }
        if (initialSize > 0) {
            initPool(initialSize);
        }
    }

    public ConnectionPool() {
        this(INIT_POOLSIZE);
    }

    /**
    *   初始化连接池
    */
    private void initPool(int size) {
        for (int i = 0; i < size; i++) {
            DBOperation conn = new DBOperation("spider", "root", "root321");
            pool.addLast(conn.getConnection());
        }
    }
    /**
    *   释放连接放回连接池
    */
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            synchronized (pool) {
                pool.addLast(connection);
                // 通知其他消费者
                pool.notifyAll();
            }
        }
    }
    /**
    *   获取连接
    */
    public Connection fetchConnection() throws InterruptedException {
        synchronized (pool) {
            // 连接池无可用连接，则等待
            while (pool.isEmpty()) {
                pool.wait();
            }
            return pool.removeFirst();
        }
    }

    /**
    *   在mills内无法获取连接，将会返回null
    */
    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool) {
            // 直接获取
            if (mills < 0) {
                return fetchConnection();
            } else {
                // 结束时间
                long end = System.currentTimeMillis() + mills;
                // 保持申请时间
                long remaining = mills;
                while (pool.isEmpty() && remaining > 0) {
                    pool.wait(remaining);
                    // 到达等待时间，退出循环
                    remaining = end - System.currentTimeMillis();
                }
                Connection result = null;
                if (!pool.isEmpty()) {
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }

    /**
    *   结束连接池
    */
    public void shutdown() {
        int size = pool.size();
        for (int i = 0; i < size; i++) {
            try {
                pool.get(i).close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}