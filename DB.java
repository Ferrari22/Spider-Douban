/**
 *  数据库基本操作类
 *  @author Scuderia
 */
package com.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pool.ConnectionPool;

public class DB
{
    // 连接对象
    private ConnectionPool pools = null;
    // 数据库预处理对象                         
    private PreparedStatement preparedStatement = null;
    // 执行sql语句结果对象
    private ResultSet resultSet = null;

    public DB() {
        // 连接池大小为10
        this.pools = new ConnectionPool(10);
    }

    public void shutdown() {
        pools.shutdown();
    }

    /**
     * 从数据库中查询获得结果集
     */
    public ResultSet executeQueryRS(String sql) {
        Connection conn = null;
        try {
            // 获得连接
            conn = pools.fetchConnection();
            // 预编译sql语句
            preparedStatement = conn.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pools.releaseConnection(conn);
        }
        return resultSet;
    }

    /**
     * 从数据库中查询获得结果集（带参数）
     */
    public ResultSet executeQueryRS(String sql, Object[] params) {
        Connection conn = null;
        try {
            // 获得连接
            conn = pools.fetchConnection();
            // 预编译sql语句
            preparedStatement = conn.prepareStatement(sql);
            // 添加所有参数
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i+1, params[i]);
            }
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pools.releaseConnection(conn);
        }
        return resultSet;
    }

    /**
     * 将结果集转化为List对象
     */
    public List<HashMap<String, String>> executeQuery(String sql) {
        ResultSet rs = executeQueryRS(sql);
        // 有关数据的信息对象
        ResultSetMetaData rsmd = null;
        // 存储所有字段的数目  
        int columnCount = 0;  
        try {  
            rsmd = rs.getMetaData();
            columnCount = rsmd.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();  
        }
        // 存储结果对象
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();  
          
        try {  
            while(rs.next()) {
                HashMap<String, String> map = new HashMap<String, String>();  
                for(int i = 1; i <= columnCount; i++) {
                    map.put(rsmd.getColumnLabel(i), rs.getString(i));               
                }  
                list.add(map);  
            }  
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 将结果集转化为List对象（带参数）
     */
    public List<HashMap<String, String>> executeQuery(String sql, Object[] params) {
        ResultSet rs = executeQueryRS(sql, params);
        // 有关数据的信息对象
        ResultSetMetaData rsmd = null;
        // 存储所有字段的数目  
        int columnCount = 0;  
        try {  
            rsmd = rs.getMetaData();
            columnCount = rsmd.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();  
        }
        // 存储结果对象
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();  
          
        try {  
            while(rs.next()) {
                HashMap<String, String> map = new HashMap<String, String>();  
                for(int i = 1; i <= columnCount; i++) {
                    map.put(rsmd.getColumnLabel(i), rs.getString(i));               
                }  
                list.add(map);  
            }  
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 用于更新数据库数据（带参数）
     */
    public int executeUpdate(String sql, Object[] params) {
        Connection conn = null;
        // 语句返回值
        int affectedLine = 0;
        try {
            conn = pools.fetchConnection();
            preparedStatement = conn.prepareStatement(sql);
            // 添加所有参数
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i+1, params[i]);
            }
            affectedLine = preparedStatement.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            pools.releaseConnection(conn);
        }
        return affectedLine;
    }

    /**
     * 更新数据库数据（无参数）
     */
    public int executeUpdate(String sql) {
        Connection conn = null;
        int affectedLine = 0;
        try {
            conn = pools.fetchConnection();
            preparedStatement = conn.prepareStatement(sql);
            affectedLine = preparedStatement.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            pools.releaseConnection(conn);
        }
        return affectedLine;
    }
}