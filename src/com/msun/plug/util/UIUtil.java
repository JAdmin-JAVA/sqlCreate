package com.msun.plug.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/** 
 * Description: ������ع�����
 * 
 * @author jiujiya
 * @version 1.0 
 */
public class UIUtil {
	
	/**
	 * �������
	 * @param shell
	 */
	public static void centerWindow(Shell shell) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension scmSize = toolkit.getScreenSize();
		shell.setLocation(scmSize.width / 2 - shell.getSize().x / 2, scmSize.height / 2 - shell.getSize().y / 2);
	}
	
	/**
	 * @return ���������ɫ��ͼƬ
	 */
	public static Image getSImage(Color color, Device device) {
		Image image = new Image(device, 12, 12);
		GC gc = new GC(image);
		try {
			if(color != null) {
				gc.setBackground(color); 
				gc.fillRectangle(image.getBounds());
			}
		} finally {
			gc.dispose(); 
		}
		return image;
	}
	
	/**
	 * @return ���������ɫ
	 */
	public static Color produceColor(Device device) {  
        Random random = new Random();  
        int red = random.nextInt(255);  
        int green = random.nextInt(255);  
        int blue = random.nextInt(255);  
        RGB rgb = new RGB(red, green, blue);  
        Color color = new Color(device, rgb);  
        return color;  
    }
	
	/**
     * ����ָ���Ŀ�߶�{@link Image}ͼ���������
     * @param src ԭͼ����
     * @param width Ŀ��ͼ����
     * @param height Ŀ��ͼ��߶�
     * @return �������ź��{@link Image}����
     */
	public static Image resize(Image src, int width, int height) {
        Image scaled = new Image(Display.getDefault(), width, height);
        GC gc = new GC(scaled);
        try{
            gc.setAdvanced(true);// �򿪸߼���ͼģʽ
            gc.setAntialias(SWT.ON);// �����������
            gc.setInterpolation(SWT.HIGH); // ���ò�ֵ
            gc.drawImage(src, 0, 0, src.getBounds().width, src.getBounds().height,0, 0, width, height);
        }finally{
            gc.dispose();
        }
        return scaled;
    }
    
    /**
     * �������ű�����{@link Image}�����������
     * @param src ԭͼ����
     * @param zoom ���ű���
     * @return �������ź��{@link Image}����
     */
	public static Image resize(Image src, float zoom) {
        Rectangle bounds = src.getBounds();
        bounds.width*=zoom;
        bounds.height*=zoom;
        return resize(src,bounds.width,bounds.height);
    }
    
	/**
	 * @param shell
	 * @param message
	 */
	public static void alert(Shell shell, String message) {
		MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
        dialog.setText("��Ϣ");
        dialog.setMessage(message);
        dialog.open();
	}
	
	/**
	 * @param shell
	 * @param e
	 */
	public static void error(Shell shell, Exception e) {
		error(shell, e.getMessage());
	}
	
	/**
	 * @param shell
	 * @param message
	 */
	public static void error(Shell shell, String message) {
		MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
        dialog.setText("��ܰ��ʾ");
        dialog.setMessage(message);
        dialog.open();
	}
}
