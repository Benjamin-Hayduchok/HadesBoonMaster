package edu.njit.cs370.benh.misc;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

public class Enumerate {
    private static final String PATH = "./clips/";
	public static void main(String[] args) throws Exception {
		new Enumerate().doIt();
	}

	private void doIt() throws Exception {
		File f = new File(PATH);
		String[] files = f.list((dir, name) -> name.endsWith(".png"));
		List<String> list = Arrays.asList(files);
		Collections.sort(list);
		for (String file : list) {
			BufferedImage bi = ImageIO.read(new File(PATH + file));
			BufferedImage bib = new BufferedImage(128, 128, BufferedImage.TYPE_INT_BGR);
			Graphics2D g2d = bib.createGraphics();
			g2d.drawRenderedImage(bi, AffineTransform.getScaleInstance(128.0 / 76.0, 128.0 / 76.0));
			ImageIO.write(bib, "png", new FileOutputStream("./bigs/" + file));
		}
	}
}
