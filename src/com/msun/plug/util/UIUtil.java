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
 * Description: 界面相关工具类
 * 
 * @author jiujiya
 * @version 1.0 
 */
public class UIUtil {
	
	/**
	 * 窗体居中
	 * @param shell
	 */
	public static void centerWindow(Shell shell) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension scmSize = toolkit.getScreenSize();
		shell.setLocation(scmSize.width / 2 - shell.getSize().x / 2, scmSize.height / 2 - shell.getSize().y / 2);
	}
	
	/**
	 * @return 返回随机的色块图片
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
	 * @return 返回随机颜色
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
     * 根据指定的宽高对{@link Image}图像进行绽放
     * @param src 原图对象
     * @param width 目标图像宽度
     * @param height 目标图像高度
     * @return 返回缩放后的{@link Image}对象
     */
	public static Image resize(Image src, int width, int height) {
        Image scaled = new Image(Display.getDefault(), width, height);
        GC gc = new GC(scaled);
        try{
            gc.setAdvanced(true);// 打开高级绘图模式
            gc.setAntialias(SWT.ON);// 设置消除锯齿
            gc.setInterpolation(SWT.HIGH); // 设置插值
            gc.drawImage(src, 0, 0, src.getBounds().width, src.getBounds().height,0, 0, width, height);
        }finally{
            gc.dispose();
        }
        return scaled;
    }
    
    /**
     * 根据缩放比例对{@link Image}对象进行缩放
     * @param src 原图对象
     * @param zoom 缩放比例
     * @return 返回缩放后的{@link Image}对象
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
        dialog.setText("消息");
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
        dialog.setText("温馨提示");
        dialog.setMessage(message);
        dialog.open();
	}
}
