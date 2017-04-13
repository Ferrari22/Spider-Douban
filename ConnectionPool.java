/**
 *  ���ӳ�ʵ�֣�֧�ֻ�ȡ���ӣ��ͷ�����
 *  @author Scuderia
 */
package com.pool;

import java.util.LinkedList;
import java.sql.Connection;

public class ConnectionPool {

    // ���ӳ�Ĭ�ϴ�С
    private static final int INIT_POOLSIZE = 5;
    // ���Ӷ���
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
    *   ��ʼ�����ӳ�
    */
    private void initPool(int size) {
        for (int i = 0; i < size; i++) {
            DBOperation conn = new DBOperation("spider", "root", "root321");
            pool.addLast(conn.getConnection());
        }
    }
    /**
    *   �ͷ����ӷŻ����ӳ�
    */
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            synchronized (pool) {
                pool.addLast(connection);
                // ֪ͨ����������
                pool.notifyAll();
            }
        }
    }
    /**
    *   ��ȡ����
    */
    public Connection fetchConnection() throws InterruptedException {
        synchronized (pool) {
            // ���ӳ��޿������ӣ���ȴ�
            while (pool.isEmpty()) {
                pool.wait();
            }
            return pool.removeFirst();
        }
    }

    /**
    *   ��mills���޷���ȡ���ӣ����᷵��null
    */
    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool) {
            // ֱ�ӻ�ȡ
            if (mills < 0) {
                return fetchConnection();
            } else {
                // ����ʱ��
                long end = System.currentTimeMillis() + mills;
                // ��������ʱ��
                long remaining = mills;
                while (pool.isEmpty() && remaining > 0) {
                    pool.wait(remaining);
                    // ����ȴ�ʱ�䣬�˳�ѭ��
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
    *   �������ӳ�
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