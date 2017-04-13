/**
 *  ���ݿ����
 *  @author Scuderia
 */
package com.db;

public class DBHandler
{
    private DB db;

    public DBHandler() {
        db = new DB();
    }
    /**
    *   �������ݵ����ݿ�
    */
    public int insert(String[] params) {
        String sql = "insert into bookinfo(name, score, introduction) values(?,?,?)";
        return db.executeUpdate(sql, params);
    }

    public void shutdown() {
        db.shutdown();
    }
}