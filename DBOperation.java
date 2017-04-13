/**
 *  ���ݿ�������
 *  @author Scuderia
 */
package com.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBOperation
{
    // ���ݿ���
    private String dataBaseName;
    // �û���
    private String userName;
    // ����  
    private String passWord;
    // ���Ӷ���
    private Connection conn;
    // ���ݿ�������
    private static final String NAME = "com.mysql.jdbc.Driver";  

    public DBOperation(String dataBaseName, String userName, String passWord) {
        this.dataBaseName = dataBaseName;
        this.userName = userName;
        this.passWord = passWord;
    }

    /**
     * ��ʼ��������ݿ�����
     */
    static {
        try {
            Class.forName(NAME);
        } catch (ClassNotFoundException e) {
            System.out.println("��������ʧ�ܣ�");
        }
    }

    /**
     * �������ݿ�
     */
    public Connection getConnection() {
        String url = "jdbc:mysql://localhost/" + dataBaseName;
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, userName, passWord);
            }
        } catch (SQLException se) {
            System.out.println("��ȡ����ʧ�ܣ�");
        }
        return conn;
    }
}