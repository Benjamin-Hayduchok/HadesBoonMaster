package edu.njit.cs370.benh.screenshot;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

public class Screenshot {
	   
    private void doit() throws Exception {
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	Thread.sleep(3000);
		BufferedImage bi = new Robot().createScreenCapture(
			new Rectangle(
				screenSize.width,
				screenSize.height
			)
		);
		Thread.sleep(500);
		ImageIO.write(
			bi,
			"png",
			new FileOutputStream(
				"./images/hades_screenshot.png"
			)
		);
		System.out.println("Screenshot taken");
    }
    
	public static void main(String[] args) throws Exception, InterruptedException {
		new Screenshot().doit();
	}
}
