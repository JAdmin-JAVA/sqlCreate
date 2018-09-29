package com.msun.plug.ui;

import com.msun.plug.bean.SelectDBBean;
import com.msun.plug.util.FileUtil;
import com.msun.plug.util.db.DBHelper;

/** 
 * Description: 启动界面
 * 
 * @author jiujiya
 * @version 1.0 
 */
public class UIStart {
	
	// 项目目录 临时保存文件
	public static String DIRPATH = "D://sql_create_temp_item_path.ini";

	public static void main(String[] args) {
		SelectDBBean bean = new SelectDBBean();
		boolean isDbConn = false;
		try {
			StringBuffer sb = FileUtil.readTextFile(DIRPATH);
			// 初始化数据库链接
			bean = new SelectDBBean(sb.toString());
			isDbConn = DBHelper.initDb(bean);
		} catch (Exception e) {
			// 吃掉异常
		}
		if(!isDbConn) {
			new SelectDBUI(bean).show();
			return;
		}
		new SqlAutoUI().show();
	}
	
}
