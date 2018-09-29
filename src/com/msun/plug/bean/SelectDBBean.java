package com.msun.plug.bean;

/** 
 * Description: 数据库参数保存的bean
 * 
 * @author jiujiya
 * @version 1.0 
 */
public class SelectDBBean {
	
	// 数据库IP
	public String dbIpText = "127.0.0.1";
	// 端口号
	public String dbPortText = "3306";
	// 用户名
	public String dbUserText = "root";
	// 密码
	public String dbPsText = "111111";
	// 数据库名称
	public String dbNameText = "";
	// 数据库连接池
	public String driverClassName = "com.mysql.jdbc.Driver";
	
	public SelectDBBean() {
		
	}
	
	public SelectDBBean(String string) {
		String[] dbs = string.split(";");
		dbUserText = dbs[0];
		dbPsText = dbs[1];
		setDbUrl(dbs[2]);
	}

	/**
	 * @param url 数据库url
	 */
	public void setDbUrl(String url) {
		url = url.substring(13);
		dbIpText = url.split(":")[0];
		url = url.substring(dbIpText.length() + 1);
		dbPortText = url.split("/")[0];
		url = url.substring(dbPortText.length() + 1);
		dbNameText = url.split("\\?")[0];
	}
	
	public String getUrl() {
		return "jdbc:mysql://" + dbIpText + ":" + dbPortText + "/" 
				+ dbNameText + "?characterEncoding=utf8";
	}
}
