import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.color.*;
import java.awt.image.renderable.*;
import javax.media.jai.*;

import javax.swing.JPanel;


public class Mast extends JPanel{
	//this class should be based on the w4 tutorial file
	
	private PlanarImage source = null;
	private JScrollPane scrollPane = null;
	JLabel iconLabel;
	
	public Mast(String filename) {
		//setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		File f = new File(filename);
		if(f.exists() && f.canRead()) {
			source = JAI.create("fileload", filename);
		} else {
			return;
		}
		iconLabel.setIcon(new IconJAI(source));
		scrollPane.setViewportView(iconLabel);
		this.getRootPane().add(scrollPane, BorderLayout.CENTER);
		
	}
	
	/*public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		gr.drawLine(250, 300, 1, 0);
		PlanarImage img = JAI.create("fileload", "nz-naval-flag.gif");
		gr.drawImage(img, 0, 0, this);
		Graphics2D g2D = (Graphics2D)gr;
		//g2D.drawImage(img, null, null);
		//gr.drawImage(img, 0, 20, img.getWidth(null), img.getHeight(null), null);
	}*/
}
