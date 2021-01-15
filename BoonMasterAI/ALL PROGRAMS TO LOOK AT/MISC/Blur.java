package edu.njit.cs370.benh.misc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Blur {
	
	private static final String ID = "Zeus_Electric_Shot.png";
	//private static final String ID = "Aphrodite_Wave_of_Despair.png";
	//private static final String ID = "Demeter_Aid.png";
	//private static final String ID = "Poseidon_Aid.png";
	private static final int BOX_SIZE = 4;
    private Signature signature = new Signature(ID);

	public Blur() {
		BufferedImage bi = loadImage(ID);
		BufferedImage pbi = doIt(bi);
		JFrame jf = new JFrame("Duck");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MyJPanel mjpw = new MyJPanel(bi);
		MyJPanel mjpe = new MyJPanel(pbi);
		jf.add(mjpw, BorderLayout.WEST);
		jf.add(mjpe, BorderLayout.EAST);
		jf.pack();
		jf.setResizable(false);
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(Blur::new);
	}

	private BufferedImage loadImage(String s) {
		BufferedImage bi = null;
		try (FileInputStream fis = new FileInputStream(new File("./images/" + s))) {
			bi = ImageIO.read(fis);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			System.exit(-1);
		}
		BufferedImage bix = new BufferedImage(
			(int) (Math.ceil((bi.getWidth() < 1000 ? bi.getWidth() : 800) / BOX_SIZE) * BOX_SIZE),
			(int) (Math.ceil((bi.getHeight() < 1000 ? bi.getHeight() : 600) / BOX_SIZE) * BOX_SIZE),
			BufferedImage.TYPE_INT_BGR
		);
		Graphics2D g2d = bix.createGraphics();
		g2d.drawRenderedImage(bi, new AffineTransform());
		g2d.dispose();
		return bix;
	}

	private class MyJPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private BufferedImage bi;
		public MyJPanel(BufferedImage bi) {
			this.bi = bi;
			setBackground(Color.BLACK);
			setPreferredSize(new Dimension(bi.getWidth(), bi.getHeight()));
		}
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.drawRenderedImage(bi, new AffineTransform());
			g2d.dispose();
		}
	}
	private BufferedImage doIt(BufferedImage bi) {
		BufferedImage biw = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
		Graphics2D g2d = biw.createGraphics();
		g2d.drawRenderedImage(bi, new AffineTransform());
		g2d.dispose();
		for (int r = 0; r < biw.getHeight(); r += BOX_SIZE) {
			for (int c = 0; c < biw.getWidth(); c += BOX_SIZE) {
				int[] pixels = new int[BOX_SIZE * BOX_SIZE];
				biw.getRGB(c, r, BOX_SIZE, BOX_SIZE, pixels, 0, BOX_SIZE);
				int totalR = 0;
				int totalG = 0;
				int totalB = 0;
				for (int i = 0; i < pixels.length; i++) {
					int red = (pixels[i] >> 0) & 0x000000ff;
					int green = (pixels[i] >> 8) & 0x000000ff;
					int blue = (pixels[i] >> 16) & 0x000000ff;
					totalR += red;
					totalG += green;
					totalB += blue;
				}
				int valueR = totalR / pixels.length;
				int valueG = totalG / pixels.length;
				int valueB = totalB / pixels.length;
				RGB rgb = new RGB(valueR, valueG, valueB);
				signature.getContent().add(rgb);
				for (int i = 0; i < pixels.length; i++) {
					pixels[i] = 0xff000000 | (valueB << 16) | (valueG << 8) | (valueR << 0);
				}
				biw.setRGB(c, r, BOX_SIZE, BOX_SIZE, pixels, 0, BOX_SIZE);
			}
		}
		System.out.println(this.signature);
		return biw;
	}
	
	private class Signature {
		
		private String id;
		private List<RGB> content = new LinkedList<>();

		public Signature(String id) {
			this.id = id;
		}

		@SuppressWarnings("unused")
		public String getId() {
			return id;
		}

		public List<RGB> getContent() {
			return content;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(id + " -> [ ");
			for (RGB rgb : content) {
				sb.append(rgb + " ");
			}
			sb.append("]");
			return sb.toString();
		}
		
	}
	
	private class RGB {
		int red;
		int green;
		int blue;
		public RGB(int r, int g, int b) {
			this.red = r;
			this.green = g;
			this.blue = b;
		}

		@Override
		public String toString() {
			return String.format("(%3d,%3d,%3d)", this.red, this.green, this.blue);
		}
	}
	
}
