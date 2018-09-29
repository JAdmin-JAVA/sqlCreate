package com.msun.plug.bean;

/** 
 * Description: ���ݿ���������bean
 * 
 * @author jiujiya
 * @version 1.0 
 */
public class SelectDBBean {
	
	// ���ݿ�IP
	public String dbIpText = "127.0.0.1";
	// �˿ں�
	public String dbPortText = "3306";
	// �û���
	public String dbUserText = "root";
	// ����
	public String dbPsText = "111111";
	// ���ݿ�����
	public String dbNameText = "";
	// ���ݿ����ӳ�
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
	 * @param url ���ݿ�url
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
