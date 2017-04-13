/**
 *  数据库连接类
 *  @author Scuderia
 */
package com.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBOperation
{
    // 数据库名
    private String dataBaseName;
    // 用户名
    private String userName;
    // 密码  
    private String passWord;
    // 连接对象
    private Connection conn;
    // 数据库驱动名
    private static final String NAME = "com.mysql.jdbc.Driver";  

    public DBOperation(String dataBaseName, String userName, String passWord) {
        this.dataBaseName = dataBaseName;
        this.userName = userName;
        this.passWord = passWord;
    }

    /**
     * 初始化获得数据库驱动
     */
    static {
        try {
            Class.forName(NAME);
        } catch (ClassNotFoundException e) {
            System.out.println("加载驱动失败！");
        }
    }

    /**
     * 连接数据库
     */
    public Connection getConnection() {
        String url = "jdbc:mysql://localhost/" + dataBaseName;
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, userName, passWord);
            }
        } catch (SQLException se) {
            System.out.println("获取连接失败！");
        }
        return conn;
    }
}