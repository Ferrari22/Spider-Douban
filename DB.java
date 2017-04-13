/**
 *  ���ݿ����������
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
    // ���Ӷ���
    private ConnectionPool pools = null;
    // ���ݿ�Ԥ�������                         
    private PreparedStatement preparedStatement = null;
    // ִ��sql���������
    private ResultSet resultSet = null;

    public DB() {
        // ���ӳش�СΪ10
        this.pools = new ConnectionPool(10);
    }

    public void shutdown() {
        pools.shutdown();
    }

    /**
     * �����ݿ��в�ѯ��ý����
     */
    public ResultSet executeQueryRS(String sql) {
        Connection conn = null;
        try {
            // �������
            conn = pools.fetchConnection();
            // Ԥ����sql���
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
     * �����ݿ��в�ѯ��ý��������������
     */
    public ResultSet executeQueryRS(String sql, Object[] params) {
        Connection conn = null;
        try {
            // �������
            conn = pools.fetchConnection();
            // Ԥ����sql���
            preparedStatement = conn.prepareStatement(sql);
            // ������в���
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
     * �������ת��ΪList����
     */
    public List<HashMap<String, String>> executeQuery(String sql) {
        ResultSet rs = executeQueryRS(sql);
        // �й����ݵ���Ϣ����
        ResultSetMetaData rsmd = null;
        // �洢�����ֶε���Ŀ  
        int columnCount = 0;  
        try {  
            rsmd = rs.getMetaData();
            columnCount = rsmd.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();  
        }
        // �洢�������
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
     * �������ת��ΪList���󣨴�������
     */
    public List<HashMap<String, String>> executeQuery(String sql, Object[] params) {
        ResultSet rs = executeQueryRS(sql, params);
        // �й����ݵ���Ϣ����
        ResultSetMetaData rsmd = null;
        // �洢�����ֶε���Ŀ  
        int columnCount = 0;  
        try {  
            rsmd = rs.getMetaData();
            columnCount = rsmd.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();  
        }
        // �洢�������
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
     * ���ڸ������ݿ����ݣ���������
     */
    public int executeUpdate(String sql, Object[] params) {
        Connection conn = null;
        // ��䷵��ֵ
        int affectedLine = 0;
        try {
            conn = pools.fetchConnection();
            preparedStatement = conn.prepareStatement(sql);
            // ������в���
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
     * �������ݿ����ݣ��޲�����
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