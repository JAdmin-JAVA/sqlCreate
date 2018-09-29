package com.msun.plug.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import com.msun.plug.util.FileUtil;
import com.msun.plug.util.UIUtil;
import com.msun.plug.util.db.DBHelper;

/** 
 * Description: 自定创建sql的界面
 * 
 * @author jiujiya
 * @version 1.0 
 */
public class SqlAutoUI {
	
	/** 主窗体 */
	private Shell shell;
	/** 左侧tree */
	private Tree tree;
	/** 左侧表的列的tree */
	private Tree columnTree;
	/** 输出的sql */
	private Text sqlText;
	/** 输出的strinbuffer */
	private Text sbText;
	/** join 类型 */
	private Combo joinType;
	/** 是否全部大写 */
	private Button isUp;

	/** 按钮组 groop用不好，暂时一个一个来 */
	private Button selectButton;
	private Button insertButton;
	private Button updateButton;
	private Button deleteButton;
	
	/** 边框 */
	private int MARGIN = 5;
	/** 总高度 */
	private int HEIGHT = 500;

	/** 表别名 */
	private String allBM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/** join 类型的选项 */
	private String[] joinTypeItem = new String[] {"inner join", "left join", "rigth join"};

	public static void main(String[] args) {
		new SqlAutoUI().show();
	}

	public SqlAutoUI() {

		shell = new Shell(192);
		shell.setText("Sql生成工具");
		shell.setLayout(null);

		// 添加左侧tree
		addTree();

		// 添加选择的view
		int rightX = MARGIN * 2 + tree.getBounds().width;
		int controlViewHeight = 50;
		Composite controlView = addControlView();
		controlView.setBounds(rightX, MARGIN, 790, controlViewHeight);
		
		// 添加表的列
		int lastY = MARGIN * 2 + controlViewHeight;
		columnTree = addColumnView();
		columnTree.setBounds(rightX, lastY, 300, 443);
		
		int LastX = MARGIN + rightX + columnTree.getBounds().width;
		Label label = new Label(shell, 0);
		label.setText(" 已将sql内容复制到剪切板，insert、update、delete暂不支持多表！");
		label.setBounds(LastX, lastY, 484, 30);
		
		// 添加输出的sql
		lastY += 30;
		sqlText = getTextarea();
		sqlText.setBounds(LastX, lastY, 484, 203);
		
		// 添加输出的strinbuffer
		sbText = getTextarea();
		sbText.setBounds(LastX, lastY + MARGIN + sqlText.getBounds().height, 484, 203);

		// 设置主窗体大小
		shell.setSize(tree.getBounds().width + controlView.getBounds().width + MARGIN * 3 + 6, HEIGHT + 46);
		UIUtil.centerWindow(shell);
	}
	
	/**
	 * @return 获取textarea
	 */
	public Text getTextarea() {
		Text text = new Text(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		text.setFont(new Font(text.getDisplay(), "宋体", 9, SWT.NORMAL));
		return text;
	}
	
	/**
	 * @return 左侧表的列的tree
	 */
	private Tree addColumnView() {
		CheckboxTreeViewer checkboxTreeViewer = new CheckboxTreeViewer(shell, SWT.BORDER);
		checkboxTreeViewer.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				String ele = event.getElement().toString();
				if(ele.contains(";")) {
					boolean checked = event.getChecked();
					for (TreeItem item : checkboxTreeViewer.getTree().getItems()) {
						if(item.getData().toString().equals(ele)) {
							for (TreeItem item2 : item.getItems()) {
								item2.setChecked(checked);
							}
						}
					}
				}
				createSql();
			}
		});
		checkboxTreeViewer.getTree().addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent paramMouseEvent) {
			}
			
			@Override
			public void mouseDown(MouseEvent paramMouseEvent) {
				columnTree.setMenu(null);
				if(columnTree.getSelection().length == 0) return;
				final TreeItem item = columnTree.getSelection()[0];
				String data = item.getData().toString();
				if(data.endsWith(";")) {
					return;
				}
				Menu menu = new Menu(columnTree);
				if(data.contains(";")) {
					MenuItem menuItem = new MenuItem(menu, 8);
					menuItem.setText("删除");
					menuItem.addSelectionListener(new SelectionListener() {
						
						@Override
						public void widgetSelected(SelectionEvent paramSelectionEvent) {
							String pData = item.getData().toString();
							String tableName = getTableName(pData.split(";")[0]);
							TreeItem treeItem = getItem(tableName);
							treeItem.removeAll();
							treeItem.dispose();
							createSql();
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
							
						}
					});
					columnTree.setMenu(menu);
					return;
				}
				String pData = item.getParentItem().getData().toString();
				if(pData.endsWith(";")) {
					return;
				}
				String tableName = getTableName(pData.split(";")[1]);
				String selectMenu = "";
				if(data.contains(",")) {
					selectMenu = data.split(",")[1];
				}
				for (String st : getSelectColumns(tableName)) {
					MenuItem menuItem = new MenuItem(menu, 8);
					boolean isJoined = selectMenu.equals(st);
					menuItem.setText((isJoined ? "取消" : "") + "关联：" + st);
					menuItem.setData(st);
					menuItem.addSelectionListener(new SelectionListener() {
						
						@Override
						public void widgetSelected(SelectionEvent paramSelectionEvent) {
							item.getParentItem().setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
							TreeItem item2 = getItem(tableName, st);
							Image sI;
							if(isJoined) {
								sI = UIUtil.getSImage(null, shell.getDisplay());
								item.setData(item.getText());
							}else {
								sI = UIUtil.getSImage(UIUtil.produceColor(shell.getDisplay()), shell.getDisplay());
								item.setData(item.getText() + "," + st);
							}
							item.setImage(sI);
							item2.setImage(sI);
							createSql();
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
							
						}
					});
				}
				columnTree.setMenu(menu);
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent paramMouseEvent) {
				
			}
		});
		return checkboxTreeViewer.getTree();
	}

	/**
	 * @return 控制view
	 */
	private Composite addControlView() {
		Composite parent = new Composite(shell, 2048);
		parent.setLayout(null);

		int x = 10;
		
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				Button button = (Button) event.getSource();
				if(button.getText().equals("update") || button.getText().equals("delete")) {
					TreeItem[] items = columnTree.getItems();
					if(items.length == 0) return;
					TreeItem[] itemss = items[0].getItems();
					if(itemss.length == 0) return;
					itemss[0].setChecked(true);
					for (int i = 1; i < itemss.length; i++) {
						itemss[i].setChecked(false);
					}
					if(button.getText().equals("update") && itemss.length >= 2) {
						itemss[1].setChecked(true);
					}
				}
				createSql();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				
			}
		};
		
		selectButton = new Button(parent, SWT.RADIO);
		selectButton.setText("select");
		selectButton.setBounds(x, 4, 65, 38);
		selectButton.setSelection(true);
		selectButton.addSelectionListener(listener);

		x += selectButton.getBounds().width + 10;
		insertButton = new Button(parent, SWT.RADIO);
		insertButton.setText("insert");
		insertButton.setBounds(x, 4, 65, 38);
		insertButton.addSelectionListener(listener);

		x += insertButton.getBounds().width + 10;
		updateButton = new Button(parent, SWT.RADIO);
		updateButton.setText("update");
		updateButton.setBounds(x, 4, 75, 38);
		updateButton.addSelectionListener(listener);

		x += updateButton.getBounds().width + 10;
		deleteButton = new Button(parent, SWT.RADIO);
		deleteButton.setText("delete");
		deleteButton.setBounds(x, 4, 70, 38);
		deleteButton.addSelectionListener(listener);

		x += deleteButton.getBounds().width + 10;
		joinType = new Combo(parent, SWT.READ_ONLY);
		joinType.setBounds(x, 10, 100, 28);
		joinType.setItems(joinTypeItem);
		joinType.select(0);
		joinType.addSelectionListener(listener);

		x += joinType.getBounds().width + 10;
		isUp = new Button(parent, SWT.CHECK);
		isUp.setText("全部大写");
		isUp.setBounds(x, 10, 100, 28);
		isUp.addSelectionListener(listener);

		x += deleteButton.getBounds().width + 170;
		Button changeButton = new Button(parent, 0);
		changeButton.setText("切换数据库");
		changeButton.setBounds(x, 10, 100, 28);

		// 添加事件
		changeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				close();
				FileUtil.writeToTxtByFileWriter(new File(UIStart.DIRPATH), "");
				UIStart.main(null);
			}
		});
		
		return parent;
	}

	/**
	 * 添加左侧tree
	 */
	private void addTree() {
		int maxLength = 0;
		tree = new Tree(shell, 268437504);
		// 添加数据
		List<Map<String, Object>> menus = DBHelper.getTables();
		for (Map<String, Object> map : menus) {
			TreeItem zmItem = new TreeItem(tree, SWT.None);
			String zm = (String) map.get("TABLE_NAME");
			zmItem.setText(zm);
			zmItem.setData(zm);
			if (zm.length() > maxLength) {
				maxLength = zm.length();
			}
		}
		tree.getItem(0).setExpanded(true);
		// 双击事件
		tree.setBounds(MARGIN, MARGIN, 50 + 9 * maxLength, HEIGHT);
		tree.addListener(8, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Point point = new Point(event.x, event.y);
				setColumn(tree.getItem(point), "");
			}
		});
		addLeftMenu();
	}
	
	/**
	 * 給据表名、列名 获取选项
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	private TreeItem getItem(String tableName, String columnName) {
		for (TreeItem treeItem : columnTree.getItems()) {
			if(treeItem.getData().toString().startsWith(tableName + ";")) {
				for (TreeItem tree2 : treeItem.getItems()) {
					if(tree2.getData().equals(columnName) || tree2.getData().toString().startsWith(columnName + ";")) {
						return tree2;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 給据表名 获取选项
	 * @param tableName
	 * @return
	 */
	private TreeItem getItem(String tableName) {
		for (TreeItem treeItem : columnTree.getItems()) {
			if(treeItem.getData().toString().startsWith(tableName + ";")) {
				return treeItem;
			}
		}
		return null;
	}
	
	/**
	 * 查看某个表的关联关系
	 * @param tableBM
	 * @param tableName
	 * @return 
	 */
	private String getJoinColumn(Map<String, String> tableBM, String tableName) {
		tableName = getTableName(tableName);
		String tableNameBM = tableBM.get(tableName);
		StringBuffer sb = new StringBuffer();
		for (TreeItem treeItem : columnTree.getItems()) {
			String data = treeItem.getData().toString();
			if(data.startsWith(tableName + ";")) {
				// 获取表关联的 另一个表的名字
				String otherTable = tableBM.get(getTableName(data.split(";")[1]));
				sb.append(joinType.getText().toUpperCase() + " " + tableName + " " + tableNameBM);
				StringBuffer on = new StringBuffer();
				for (TreeItem tree2 : treeItem.getItems()) {
					String data2 = tree2.getData().toString();
					if(!data2.contains(",")) {
						continue;
					}
					String[] data2s = data2.split(",");
					on.append(otherTable + "." + data2s[1] + " = " + tableNameBM + "." + data2s[0] + " AND ");
				}
				if(on.toString().endsWith(" AND ")) {
					on.delete(on.length() - 5, on.length() - 1);
				}
				if(on.length() != 0) {
					sb.append(" ON ").append(on);
				}
				sb.append("\r\n");
				break;
			}
		}
		return sb.toString();
	}
	
	/**
	 * @return 获取所有选中的表 表只有在列表中，都算有
	 */
	private List<String> getAllSelectTables(){
		List<String> list = new ArrayList<>();
		if(columnTree == null) return list;
		for (TreeItem treeItem : columnTree.getItems()) {
			list.add(treeItem.getData().toString().split(";")[0]);
		}
		return list;
	}
	
	/**
	 * @return 获取所有选中的表和字段
	 */
	private Map<String, List<String>> getAllSelectColumns(){
		Map<String, List<String>> map = new LinkedHashMap<>();
		for (TreeItem treeItem : columnTree.getItems()) {
			List<String> list = new ArrayList<>();
			map.put(treeItem.getData().toString().split(";")[0], list);
			for (TreeItem tree2 : treeItem.getItems()) {
				if(tree2.getChecked()) {
					list.add(tree2.getData().toString());
				}
			}
		}
		return map;
	}
	
	/**
	 * @param tableName
	 * @return
	 */
	private String getTableName(String tableName) {
		if(tableName.equals("主表")) {
			tableName = getAllSelectTables().get(0);
		}
		return tableName;
	}
	
	/**
	 * @return 获取所有选中的表和字段
	 */
	private List<String> getSelectColumns(String tableName){
		Map<String, List<String>> menuMap = getAllSelectColumns();
		for (Entry<String, List<String>> entry : menuMap.entrySet()) {
			if(tableName.equals(entry.getKey())) {
				return entry.getValue();
			}
		}
		return new ArrayList<>();
	}
	
	/**
	 * 重置左侧右键菜单
	 */
	private void addLeftMenu() {
		Menu menu = new Menu(tree);
		tree.setMenu(menu);
		MenuItem addItem = new MenuItem(menu, 8);
		addItem.setText("设置为主表");
		addItem.setData("");
		
		SelectionListener listener = new SelectionListener() {
			public void widgetSelected(SelectionEvent enven) {
				TreeItem item = tree.getSelection()[0];
				MenuItem item2 = (MenuItem) enven.getSource();
				setColumn(item, item2.getData().toString());
			}

			public void widgetDefaultSelected(SelectionEvent enven) {
			}
		};
		
		addItem.addSelectionListener(listener);
		
		List<String> list = getAllSelectTables();
		if(list.isEmpty()) return;
		
		MenuItem joinItem = new MenuItem(menu, 8);
		joinItem.setText("jion 主表");
		joinItem.setData("主表");
		joinItem.addSelectionListener(listener);
		
		for (int i = 1; i < list.size(); i++) {
			MenuItem comItem = new MenuItem(menu, 8);
			comItem.setText("jion " + list.get(i));
			comItem.setData(list.get(i));
			comItem.addSelectionListener(listener);
		}

	}

	/**
	 * 获取数据库里面的列
	 * @param item
	 * @param tableName
	 */
	private void setColumn(TreeItem item, String tableName) {
		if(item == null) return;
		String tName = item.getText();
		if(tableName.equals("")) {
			columnTree.removeAll();
		}else {
			tName += " :join " + tableName;
			// 效验表是否存在
			String finalTableName = getTableName(item.getText());
			for(String str : getAllSelectTables()) {
				if(str.equals(finalTableName)) {
					UIUtil.alert(shell, "表" + finalTableName + "已在列表中");
					return;
				}
			}
		}
		List<Map<String, Object>> columns = DBHelper.getOneTable(item.getText());
		TreeItem tItem = new TreeItem(columnTree, SWT.None);
		tItem.setText(tName);
		tItem.setData(item.getText() + ";" + tableName);
		tItem.setChecked(true);
		for (Map<String, Object> map : columns) {
			TreeItem zmItem = new TreeItem(tItem, SWT.None);
			String zm = (String) map.get("COLUMN_NAME");
			zmItem.setText(zm);
			zmItem.setData(zm);
			zmItem.setChecked(true);
		}
		tItem.setExpanded(true);
		createSql();
		addLeftMenu();
	}
	
	/**
	 * 生成sql
	 */
	private void createSql() {
		sqlText.setText("");
		String str = "";
		try {
			Map<String, List<String>> selectColumns = getAllSelectColumns();
			if(selectColumns.size() == 0) return;
			if(selectButton.getSelection()) {
				str = createSelectSql(selectColumns);
			}else if(insertButton.getSelection()) {
				str = createInsertSql(selectColumns);
			}else if(updateButton.getSelection()) {
				str = createUpateSql(selectColumns);
			}else if(deleteButton.getSelection()) {
				str = createDeleteSql(selectColumns);
			}
			if(isUp.getSelection()) {
				str = str.toUpperCase();
			}
			sqlText.setText(str);
		}catch (Exception e) {
			e.printStackTrace();
		}
		setSysClipboardText(str);
		sqlToSb(str);
	}
	
	/**
	 * 获取select语句
	 * @param selectColumns
	 * @return
	 */
	private String createSelectSql(Map<String, List<String>> selectColumns) {
		// 获取所有的咧，并格式为 表别名.列名 allBM
		List<String> allColumn = new ArrayList<>();
		List<String> allTable = new ArrayList<>();
		Map<String, String> tableBM = new HashMap<>();
		int index = 0;
		for (Entry<String, List<String>> entry : selectColumns.entrySet()) {
			for (String column : entry.getValue()) {
				allColumn.add(allBM.charAt(index) + "." + column);
			}
			allTable.add(entry.getKey());
			tableBM.put(entry.getKey(), allBM.charAt(index) + "");
			index++;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		int[] maxLength = getColumnMaxLength(allColumn);
		sb.append(getColumnsFormat(allColumn, maxLength));
		sb.append("\r\n FROM ").append(allTable.get(0)).append(" A\r\n");
		for (int i = 1; i < allTable.size(); i++) {
			sb.append(getJoinColumn(tableBM, allTable.get(i)));
		}
		if(allColumn.size() <= 3) {
			return sb.toString().replaceAll("\\r\\n", "").replaceAll("  ", " ");
		}
		return sb.toString();
	}
	
	/**
	 * 获取update语句 只处理主表
	 * @param selectColumns
	 * @return
	 */
	private String createUpateSql(Map<String, List<String>> selectColumns) {
		// 只处理主表
		Entry<String, List<String>> entry = selectColumns.entrySet().iterator().next();
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE " + entry.getKey() + " SET ");
		List<String> list = entry.getValue();
		for (int i = 1; i < list.size(); i++) {
			sb.append(list.get(i) + " = ?, ");
		}
		if(!list.isEmpty()) {
			sb.delete(sb.length() - 2, sb.length() - 1);
		}
		sb.append(" WHERE " + list.get(0) + " = ? ");
		return sb.toString();
	}
	
	/**
	 * 获取delete语句 只处理主表
	 * @param selectColumns
	 * @return
	 */
	private String createDeleteSql(Map<String, List<String>> selectColumns) {
		// 只处理主表
		Entry<String, List<String>> entry = selectColumns.entrySet().iterator().next();
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM " + entry.getKey());
		List<String> list = entry.getValue();
		if(list.isEmpty()) {
			return sb.toString();
		}
		sb.append(" WHERE ");
		for (String string : list) {
			sb.append(string + " = ? AND ");
		}
		if(!list.isEmpty()) {
			sb.delete(sb.length() - 4, sb.length() - 1);
		}
		return sb.toString();
	}
	
	/**
	 * 获取insert语句 只处理主表
	 * @param selectColumns
	 * @return
	 */
	private String createInsertSql(Map<String, List<String>> selectColumns) {
		// 只处理主表
		Entry<String, List<String>> entry = selectColumns.entrySet().iterator().next();
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO " + entry.getKey() + "(");
		List<String> list = entry.getValue();
		int[] maxLength = getColumnMaxLength(list);
		sb.append(getColumnsFormat(list, maxLength));
		sb.append("\r\n)VALUES(");
		for (int i = 0; i < list.size(); i++) {
			if(i % 4 == 0) {
				sb.append("\r\n ");
			}
			if(i == list.size() - 1) {
				sb.append("?");
			}else {
				sb.append(getText("?,", maxLength[i % 4]));
			}
		}
		sb.append(" \r\n)");
		if(list.size() <= 3) {
			return sb.toString().replaceAll("\\r\\n", "").replaceAll("  ", " ");
		}
		return sb.toString();
	}
	
	/**
	 * 获取列最佳宽度（1行四列）
	 * @param list
	 * @return
	 */
	private int[] getColumnMaxLength(List<String> list) {
		int[] maxLength = new int[] {0, 0, 0, 0};
		for (int i = 0; i < list.size(); i++) {
			String tree2 = list.get(i);
			int nowLength = tree2.length() + 1;
			if(maxLength[i % 4] < nowLength) {
				maxLength[i % 4] = nowLength;
			}
		}
		return maxLength;
	}
	
	/**
	 * 格式化输出每行
	 * @param list
	 * @param maxLength
	 * @return 
	 */
	private String getColumnsFormat(List<String> list, int[] maxLength) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if(i % 4 == 0) {
				sb.append("\r\n ");
			}
			String tree2 = list.get(i);
			if(i == list.size() - 1) {
				sb.append(tree2);
			}else {
				sb.append(getText(tree2 + ",", maxLength[i % 4]));
			}
		}
		return sb.toString();
	}
	
	/**
	 * 将sql转为sb
	 */
	private void sqlToSb(String sql) {
		sbText.setText("");
		StringBuffer sb = new StringBuffer("StringBuffer sql = new StringBuffer();\r\n");
		for (String line : sql.split("\r\n")) {
			sb.append("sql.append(\" ").append(line).append(" \");\r\n");
		}
		if(sql.contains("?")) {
			sb.append("Object[] args = new Object[]{ ?,?,? };\r\n");
			if(sql.contains("SELECT")) {
				sb.append("List queryList = getJdbcTemplate().queryForList(sql.toString(), args);\r\n");
			}else {
				sb.append("getJdbcTemplate().update(sql.toString(), args);\r\n");
			}
		}else if(!sql.contains("SELECT")) {
			sb.append("getJdbcTemplate().update(sql.toString());\r\n");
		}else {
			sb.append("List queryList = getJdbcTemplate().queryForList(sql.toString());\r\n");
		}
		sbText.setText(sb.toString());
		sbText.selectAll();
	}
	
	/** 
     * 将字符串复制到剪切板。 
     */  
    public static void setSysClipboardText(String writeMe) {  
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();  
        Transferable tText = new StringSelection(writeMe);  
        clip.setContents(tText, null);  
    } 
	
	private String getText(String text, int maxLength) {
		int length = text.length();
		for (int i = 0; i < (maxLength - length); i++) {
			text += " ";
		}
		return text + " ";
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