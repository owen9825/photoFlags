import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class EyeDropper {
	//this class displays an image and gets the user to select
	//the colour of the flag
	
	JPanel mainPanel = new JPanel();
	JTextField text0 = new JTextField("Please select a few points on the flag");
	BufferedImage palette;
	int[] colours;
	
	int width;
	int height;
	int[] pixels;
	int nbands;
	int xFrame = 0;
	int yFrame = 0;
	
	int maxRed = 0;
	int minRed = 255;
	int maxGreen = 0;
	int minGreen = 255;
	int maxBlue = 0;
	int minBlue = 255;
	
	public EyeDropper(BufferedImage img) {
		palette = img;
		//display the picture in its own window
		SmallScreen screen = new SmallScreen(img);
		width = img.getWidth();
		height = img.getHeight();
		SampleModel sm = img.getSampleModel();
		nbands = sm.getNumBands();
		Raster inputRaster = img.getData();
		pixels = new int[nbands*width*height];
		inputRaster.getPixels(0, 0, width, height, pixels);
		screen.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				System.out.println("pixel chosen: (" + x + "," + y + ")");
				int offset = y*width*nbands + x*nbands;
				addSample(pixels[offset], pixels[offset+1], pixels[offset+2]);
			}
		});
		
		mainPanel.add(screen, BorderLayout.NORTH);
		mainPanel.add(text0, BorderLayout.SOUTH);	
	}
	
	private void addSample(int red, int green, int blue) {
		System.out.println("adding sample [" + red + "," + green + "," + blue + "]");
		if(red > maxRed)
			maxRed = red;
		if(red < minRed)
			minRed = red;
		if(green > maxGreen)
			maxGreen = green;
		if(green < minGreen)
			minGreen = green;
		if(blue > maxBlue)
			maxBlue = blue;
		if(blue < minBlue)
			minBlue = blue;
		//finished assigning
	}
	
	public JComponent getComponent() {
		return mainPanel;
	}
	
	public int[] returnSettings() {
		//return the colour settings
		System.out.println(maxRed + " " + minRed + " " + maxGreen + " " + minGreen + " " + maxBlue + " " + minBlue);
		int[] colourParameters = new int[6];
		colourParameters[1] = ((maxRed - minRed)/2);	//+- distance
		colourParameters[0] = colourParameters[1] + minRed;	//avg
		colourParameters[3] = ((maxGreen - minGreen)/2);
		colourParameters[2] = colourParameters[3] + minGreen;
		colourParameters[5] = ((maxBlue - minBlue))/2;
		colourParameters[4] = colourParameters[5] + minBlue;
		//finished assigning colour information
		return colourParameters;
	}
	
}
