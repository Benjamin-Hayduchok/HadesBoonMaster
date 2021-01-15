package edu.njit.cs370.benh.clip;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Clip {

	public static void main(String[] args) throws Exception {
		BufferedImage bi = ImageIO.read(new File("./images/hades_screenshot.png"));
		new Clip().clip(bi);
	}

	private void clip(BufferedImage bi) throws Exception {
		Point2D[] centers = {
			new Point2D.Double(50, 300),
			new Point2D.Double(50, 393),
			new Point2D.Double(50, 487),
			new Point2D.Double(50, 581),
			new Point2D.Double(50, 674),
			new Point2D.Double(50, 768),
			
			new Point2D.Double(186, 300),
			new Point2D.Double(186, 393),
			new Point2D.Double(186, 487),
			new Point2D.Double(186, 581),
			new Point2D.Double(186, 674),
			new Point2D.Double(186, 768),
			
			new Point2D.Double(314, 300),
			new Point2D.Double(314, 393),
			new Point2D.Double(314, 487),
			new Point2D.Double(314, 581),
			new Point2D.Double(314, 674),
			new Point2D.Double(314, 768),
			
			//new Point2D.Double(122, 347),
			new Point2D.Double(122, 440),
			new Point2D.Double(122, 534),
			new Point2D.Double(122, 627),
			new Point2D.Double(122, 721),
			
			new Point2D.Double(250, 347),
			new Point2D.Double(250, 440),
			new Point2D.Double(250, 534),
			new Point2D.Double(250, 627),
			new Point2D.Double(250, 721),	
		};
		BufferedImage bim = ImageIO.read(new File("./images/mask76x76.png"));
		for (Point2D p2d : centers) {
			Rectangle2D r2d = new Rectangle2D.Double();
			r2d.setFrameFromCenter(p2d, new Point2D.Double(p2d.getX() - 38.0, p2d.getY() - 38.0));
			BufferedImage bis = bi.getSubimage((int) r2d.getX(), (int) r2d.getY(), (int) r2d.getWidth(), (int) r2d.getHeight());
			BufferedImage bix = new BufferedImage(bis.getWidth(), bis.getHeight(), bis.getType());
			Graphics2D g2d = bix.createGraphics();
			g2d.drawRenderedImage(bis, new AffineTransform());
			g2d.drawRenderedImage(bim, new AffineTransform());
			g2d.dispose();
			ImageIO.write(bix, "png", new File("./clips/" + 
			String.format("%03d", ((int) p2d.getX()))
					+ "_"+ (int) p2d.getY() + ".png"));
		}
		// Begin Special Processing
		Point2D p2d = new Point2D.Double(472, 232);
		Rectangle2D r2d = new Rectangle2D.Double();
		r2d.setFrameFromCenter(p2d, new Point2D.Double(p2d.getX() - 59.0, p2d.getY() - 59.0));
		BufferedImage bis = bi.getSubimage((int) r2d.getX(), (int) r2d.getY(), (int) r2d.getWidth(), (int) r2d.getHeight());
		BufferedImage bix = new BufferedImage(76, 76, bis.getType());
		Graphics2D g2d = bix.createGraphics();
		g2d.drawRenderedImage(bis, AffineTransform.getScaleInstance(76.0 / 118.0, 76.0 / 118.0));
		g2d.drawRenderedImage(bim, new AffineTransform());
		g2d.dispose();
		ImageIO.write(bix, "png", new File("./clips/" + (int) p2d.getX() + "_"+ (int) p2d.getY() + ".FP.png"));
		// End Special Processing
		
		
		// Begin Special Processing // 409 232
		p2d = new Point2D.Double(409, 232);
		r2d = new Rectangle2D.Double();
		r2d.setFrameFromCenter(p2d, new Point2D.Double(p2d.getX() - 59.0, p2d.getY() - 59.0));
		bis = bi.getSubimage((int) r2d.getX(), (int) r2d.getY(), (int) r2d.getWidth(), (int) r2d.getHeight());
		bix = new BufferedImage(76, 76, bis.getType());
		g2d = bix.createGraphics();
		g2d.drawRenderedImage(bis, AffineTransform.getScaleInstance(76.0 / 118.0, 76.0 / 118.0));
		g2d.drawRenderedImage(bim, new AffineTransform());
		g2d.dispose();
		ImageIO.write(bix, "png", new File("./clips/" + (int) p2d.getX() + "_"+ (int) p2d.getY() + ".FP.png"));
		// End Special Processing	
	}	
}
