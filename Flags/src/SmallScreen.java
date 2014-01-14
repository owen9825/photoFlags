import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class SmallScreen extends JPanel {

	BufferedImage image;
	Dimension size = new Dimension();
	
	public SmallScreen(BufferedImage newImg) {
		image = newImg;
		size.setSize(image.getWidth(), image.getHeight());
	}
	
	public Dimension getPreferredSize() {
		return size;
	}
	
	protected void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, this);
	}
	
}