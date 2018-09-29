package com.msun.plug.util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.msun.plug.bean.SelectDBBean;

/** 
 * Description: 数据库工具类
 * 
 * @author jiujiya
 * @version 1.0 
 */
public class DBHelper {
	
	/** 数据库url */
	public static String url;
	/** 数据库驱动类 */
	public static String driverName;
	/** 数据库用户名 */
	public static String user;
	/** 数据库密码 */
	public static String password;
	/** 数据库名称 */
	public static String dbname;
	
	public Connection conn = null;
	
	public PreparedStatement pst = null;

	public DBHelper(String sql) {
		try {
			Class.forName(driverName);
			this.conn = DriverManager.getConnection(url, user, password);
			this.pst = this.conn.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("数据库连接失败：" + e.getMessage());
		}
	}
	
	/**
	 * @return 是否连接成功
	 */
	public static boolean checkConn() {
		try {
			Class.forName(driverName);
			DriverManager.getConnection(url, user, password);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 数据库属性初始化
	 * @param map
	 */
	public static boolean initDb(SelectDBBean bean) {
		if(bean == null || "".equals(bean.dbNameText)) return false;
		url = bean.getUrl();
		driverName = bean.driverClassName;
		user = bean.dbUserText;
		password = bean.dbPsText;
		dbname = bean.dbNameText;
		return checkConn();
	}

	public void close() {
		try {
			this.conn.close();
			this.pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return 获取项目所有的表
	 */
	public static List<Map<String, Object>> getTables() {
		String sql = "SELECT TABLE_NAME, TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='" + DBHelper.dbname + "'";
		return DBHelper.queryForList(sql);
	}
	
	/**
	 * 获取表字段信息
	 * @param tableName
	 * @return
	 */
	public static List<Map<String, Object>> getOneTable(String tableName){
		String sql = "select COLUMN_NAME, DATA_TYPE, COLUMN_TYPE, COLUMN_COMMENT from information_schema.columns where table_schema = '"
				+ DBHelper.dbname + "' and table_name = '" + tableName + "'";
		List<Map<String, Object>> list = DBHelper.queryForList(sql);
		for (Map<String, Object> map : list) {
			String type = (String) map.get("COLUMN_TYPE");
			if (type.contains("(")) {
				String lengthStr = type.substring(type.indexOf("(") + 1, type.indexOf(")"));
				if (!(lengthStr.contains(",")))
					map.put("CHARACTER_MAXIMUM_LENGTH", lengthStr);
			}
		}
		return list;
	}

	/**
	 * queryForList
	 * @param sql
	 * @return
	 */
	public static List<Map<String, Object>> queryForList(String sql) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			DBHelper db1 = new DBHelper(sql);
			ResultSet ret = db1.pst.executeQuery();
			ResultSetMetaData rsmd = ret.getMetaData();
			int columnCount = ret.getMetaData().getColumnCount();
			while (ret.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 1; i < columnCount + 1; ++i)
					map.put(rsmd.getColumnName(i), ret.getObject(i));

				list.add(map);
			}
			ret.close();
			db1.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("数据库查询失败：" + e.getMessage());
		}
		return list;
	}
}