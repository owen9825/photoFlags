import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import javax.media.*;
import javax.media.format.*;
import javax.media.util.*;
import javax.media.control.*;
import javax.media.protocol.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import com.sun.image.codec.jpeg.*;

public class FlagWatch {

	private Player player = null;
	JPanel videoWindow = null;
	FrameGrabbingControl frameCamera = null;
	//used for identifying the flag
	int flagRed = -1;
	int flagBlue = -1;
	int flagGreen = -1;
	int rThresh = 10;
	int gThresh = 15;
	int bThresh = 30;
	
	public FlagWatch() {
		videoWindow = new JPanel();
		JTextField videoOut = new JTextField("Smile!\nsay 'my engines are going full speed astern'");
		MediaLocator ml = new MediaLocator("vfw://0");
		try {
			//try this...
			Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, true);
			//^temp
	      player = Manager.createRealizedPlayer(ml);
	      player.start();
	      Component hmmComp;
	      if ((hmmComp = player.getVisualComponent()) != null) {
	        videoWindow.add(hmmComp,BorderLayout.NORTH);
	      }
	      videoWindow.add(videoOut, BorderLayout.SOUTH);
	      frameCamera = (FrameGrabbingControl)player.getControl("javax.media.control.FrameGrabbingControl");
	    } 
	    catch (Exception e) {
	    	System.out.println("Error while initializing webcam");
	    	e.printStackTrace();
	    }
		
	}
	
	public JPanel getDisplay() {
		return videoWindow;
	}
	
	public BufferedImage getSnapshot() {
		Buffer snapshotBuf = frameCamera.grabFrame();
		BufferToImage btoi = new BufferToImage((VideoFormat)snapshotBuf.getFormat());
		BufferedImage img = (BufferedImage) btoi.createImage(snapshotBuf);
		//Image img = (new BufferToImage((VideoFormat)snapshotBuf.getFormat()).createImage(snapshotBuf));
        //BufferedImage buffImg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        //return buffImg;
		return img;
	}
	
	public void setColour(Color sampleColour) {
		flagRed = sampleColour.getRed();
		flagBlue = sampleColour.getBlue();
		flagGreen = sampleColour.getGreen();
		
	}
	
	public int[] identify(BufferedImage pic) {
		//this method looks for the flag and returns the regions where it is
		System.out.println("identifying...");
		int[] regions = {0,0};
		//break into pixels
		int width = pic.getWidth();
		int height = pic.getHeight();
		SampleModel sm = pic.getSampleModel();
		int nbands = sm.getNumBands();
		Raster inputRaster = pic.getData();
		int[] pixels = new int[nbands*width*height];
		inputRaster.getPixels(0, 0, width, height, pixels);
		int offset = 0;
		//now traverse the pixels
		for(int h=0; h < height; h++) {
			for(int w = 0; w < width; w++) {
				offset = h*width*nbands + w*nbands;
				int band = 0;
				if(Math.abs(pixels[offset] - flagRed) < rThresh) {
					//red enough
					//System.out.print("r");
					band++;
					if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
						//green enough
						//System.out.print("g");
						band++;
						if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
							//blue enough
							//System.out.print("b");
							//System.out.println("Flag colour spotted at (" + w + "," + h + ")");
							System.out.println(w + " " + h);
						}
					}
					//System.out.println("");
				}
				w++; //skip every alternate w just so it runs faster
			}
			h++; //skip every alternate h just so it runs faster
		}
		return regions;
	}
	
}
