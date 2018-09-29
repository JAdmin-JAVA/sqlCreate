package com.msun.plug.ui;

import com.msun.plug.bean.SelectDBBean;
import com.msun.plug.util.FileUtil;
import com.msun.plug.util.db.DBHelper;

/** 
 * Description: ��������
 * 
 * @author jiujiya
 * @version 1.0 
 */
public class UIStart {
	
	// ��ĿĿ¼ ��ʱ�����ļ�
	public static String DIRPATH = "D://sql_create_temp_item_path.ini";

	public static void main(String[] args) {
		SelectDBBean bean = new SelectDBBean();
		boolean isDbConn = false;
		try {
			StringBuffer sb = FileUtil.readTextFile(DIRPATH);
			// ��ʼ�����ݿ�����
			bean = new SelectDBBean(sb.toString());
			isDbConn = DBHelper.initDb(bean);
		} catch (Exception e) {
			// �Ե��쳣
		}
		if(!isDbConn) {
			new SelectDBUI(bean).show();
			return;
		}
		new SqlAutoUI().show();
	}
	
}
