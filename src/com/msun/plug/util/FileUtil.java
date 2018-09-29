package com.msun.plug.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/** 
 * Description: �ļ�������
 * 
 * @author jiujiya
 * @version 1.0 
 */
public class FileUtil {
	
	/**
	 * ��UTF-8��ȡ �ı��ļ�
	 * @param path
	 * @return
	 */
	public static StringBuffer readTextFile(String path) {
		return readTextFile(path, "UTF-8");
	}
    
    public static List<String> readTextsFile(String path){
        return readTextsFile(path, "UTF-8");
    }
    /**
     * ��ȡ �ı��ļ�
     * @param path
     * @param encoding ����
     * @return
     */
    public static List<String> readTextsFile(String path, String encoding){
        File file = new File(path);
        if (!file.exists()) return null;
        List<String> list = new ArrayList<String>();
        BufferedReader br = null;
        try {
            try {
                br = new BufferedReader(new InputStreamReader (new FileInputStream(file), encoding));
                String line = null;
                while( (line = br.readLine()) != null) {
                    list.add(line);
                }
                return list;
            }finally {
                if (br != null) {
                    br.close();
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
    }
    
	/**
	 * ��ȡ �ı��ļ�
	 * @param path
	 * @param encoding ����
	 * @return
	 */
	public static StringBuffer readTextFile(String path, String encoding) {
		File file = new File(path);
		if (!file.exists()) return new StringBuffer("");
		StringBuffer str = new StringBuffer();
        List<String> list = readTextsFile(path, encoding);
        for (String string : list) {
            str.append(string);
        }
        return str;
	}
	
	 /**
     * ׷������
     * @param file
     * @param content
     */
    public static void writeToTxtByFileWriter(File file, String content) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(content);
			osw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(osw);
			close(fos);
		}
    }

    /**
     * �ر���Դ
     * @param close
     */
    public static void close(Closeable obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
    }
}