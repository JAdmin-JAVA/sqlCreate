package com.msun.plug.ui;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.msun.plug.bean.SelectDBBean;
import com.msun.plug.util.FileUtil;
import com.msun.plug.util.UIUtil;
import com.msun.plug.util.db.DBHelper;

/** 
 * Description: �������ݿ�����Ľ���
 * 
 * @author jiujiya
 * @version 1.0 
 */
public class SelectDBUI {
	
	/** ������ */
	private Shell shell;
	
	// ���ݿ�IP
	Text dbIpText;
	// �˿ں�
	Text dbPortText;
	// �û���
	Text dbUserText;
	// ����
	Text dbPsText;
	// ���ݿ�����
	Text dbNameText;
	// ����bean
	SelectDBBean bean;
	// ȷ�ϰ�ť
	Button okButton;

	public static void main(String[] args) {
		SelectDBBean bean = new SelectDBBean();
		new SelectDBUI(bean).show();
	}

	public SelectDBUI(SelectDBBean bean) {
		this.bean = bean;
		
		shell = new Shell(SWT.CLOSE | SWT.MIN | SWT.RESIZE);
		shell.setText("ѡ�����ݿ�");
		shell.setLayout(null);

		addView();

		// �����������С
		shell.setSize(430, 250);
		UIUtil.centerWindow(shell);
	}

	/**
	 * ��ӽ���
	 * @throws Exception 
	 */
	private void addView(){
		int y = 20;
		Label dbIpLabel = new Label(shell, 0);
		dbIpLabel.setText("���ݿ�IP��");
		dbIpLabel.setBounds(20, y+3, 70, 28);
		dbIpText = new Text(shell, 2048);
		dbIpText.setBounds(100, y, 140, 28);
		dbIpText.setText(bean.dbIpText);
		
		Label dbPortLabel = new Label(shell, 0);
		dbPortLabel.setText("�˿ںţ�");
		dbPortLabel.setBounds(260, y+3, 70, 28);
		dbPortText = new Text(shell, 2048);
		dbPortText.setBounds(330, y, 70, 28);
		dbPortText.setText(bean.dbPortText);

		y += 40;
		Label dbUserLabel = new Label(shell, 0);
		dbUserLabel.setText("�û�����");
		dbUserLabel.setBounds(20 + 12, y+3, 60, 28);
		dbUserText = new Text(shell, 2048);
		dbUserText.setBounds(100, y, 115, 28);
		dbUserText.setText(bean.dbUserText);
		
		Label dbPsLabel = new Label(shell, 0);
		dbPsLabel.setText("���룺");
		dbPsLabel.setBounds(235, y+3, 50, 28);
		dbPsText = new Text(shell, 2048);
		dbPsText.setBounds(285, y, 115, 28);
		dbPsText.setText(bean.dbPsText);
		
		y += 40;
		Label dbNameLabel = new Label(shell, 0);
		dbNameLabel.setText("���ݿ�����");
		dbNameLabel.setBounds(20, y+3, 70, 28);
		dbNameText = new Text(shell, 2048);
		dbNameText.setBounds(100, y, 300, 28);
		dbNameText.setText(bean.dbNameText);

		y += 50;
		okButton = new Button(shell, 0);
		okButton.setText("ȷ��");
		okButton.setBounds(140, y, 120, 35);
		
		okButton.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseDown(MouseEvent arg0) {
				checkDB();
			}
		});
	}
	
	/**
	 * Ч�����ݿ�
	 * @throws IOException 
	 */
	public void checkDB() {
		okButton.setEnabled(false);
		okButton.setText("�������ӡ���");
		// ��ʼ�����ݿ�����
		bean.driverClassName = bean.driverClassName;
		bean.dbNameText = dbNameText.getText();
		bean.dbUserText = dbUserText.getText();
		bean.dbPsText = dbPsText.getText();
		bean.dbPortText = dbPortText.getText();
		bean.dbIpText = dbIpText.getText();
		if(DBHelper.initDb(bean)) {
			FileUtil.writeToTxtByFileWriter(new File(UIStart.DIRPATH), DBHelper.user + ";" + DBHelper.password + ";" + DBHelper.url);
			close();
			new SqlAutoUI().show();
		}else {
			okButton.setEnabled(true);
			okButton.setText("ȷ��");
			UIUtil.error(shell, "���ݿ�����ʧ��");
		}
	}

	public void show() {
		shell.open();
		Display display = shell.getDisplay();
		while (!(shell.isDisposed()))
			if (!(display.readAndDispatch()))
				display.sleep();
	}
	
	public void close() {
		shell.dispose();
	}
}