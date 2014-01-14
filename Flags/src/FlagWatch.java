import javax.swing.*;
import javax.media.*;
import javax.media.format.*;
import javax.media.util.*;
import javax.media.control.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.*;

public class FlagWatch {

	@SuppressWarnings("restriction")
	private Player player = null;
	private JPanel videoWindow = null;
	@SuppressWarnings("restriction")
	private FrameGrabbingControl frameCamera = null;
	//used for identifying the flag
	private int flagRed = -1;
	private int flagBlue = -1;
	private int flagGreen = -1;
	private int rThresh = 10;
	private int gThresh = 15;
	private int bThresh = 30;
	
	//these things are for shape initialisation
	//private FlagCast stencil;
	//left and right are based on the hand of the user; not the position on the screen
	private ArrayList<Integer> xtop = new ArrayList<Integer>();
	private ArrayList<Integer> ytop = new ArrayList<Integer>();
	private ArrayList<Integer> xtopLeft = new ArrayList<Integer>();
	private ArrayList<Integer> ytopLeft = new ArrayList<Integer>();
	private ArrayList<Integer> xleft = new ArrayList<Integer>();
	private ArrayList<Integer> yleft = new ArrayList<Integer>();
	private ArrayList<Integer> xbottomLeft = new ArrayList<Integer>();
	private ArrayList<Integer> ybottomLeft = new ArrayList<Integer>();
	private ArrayList<Integer> xbottom = new ArrayList<Integer>();
	private ArrayList<Integer> ybottom = new ArrayList<Integer>();
	private ArrayList<Integer> xbottomRight = new ArrayList<Integer>();
	private ArrayList<Integer> ybottomRight = new ArrayList<Integer>();
	private ArrayList<Integer> xright = new ArrayList<Integer>();
	private ArrayList<Integer> yright = new ArrayList<Integer>();
	private ArrayList<Integer> xtopRight = new ArrayList<Integer>();
	private ArrayList<Integer> ytopRight = new ArrayList<Integer>();
	
	//where the shape coordinates will be stored
	//East and West are based on the screen layout
	private int[][] shapeN;
	private int[][] shapeNE;
	private int[][] shapeE;
	private int[][] shapeSE;
	private int[][] shapeS;
	private int[][] shapeSW;
	private int[][] shapeW;
	private int[][] shapeNW;
	
	@SuppressWarnings("restriction")
	public FlagWatch() {
		videoWindow = new JPanel();
		//JTextField videoOut = new JTextField("Smile!\nsay 'my engines are going full speed astern'");
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
	      //videoWindow.add(videoOut, BorderLayout.SOUTH);
	      frameCamera = (FrameGrabbingControl)player.getControl("javax.media.control.FrameGrabbingControl");
	    } 
	    catch (Exception e) {
	    	System.out.println("Error while initializing webcam");
	    	e.printStackTrace();
	    }
	    shapeNE = new int[1][1];
		
	}
	
	public JPanel getDisplay() {
		return videoWindow;
	}
	
	@SuppressWarnings("restriction")
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
		//just set the colours and use the default thresholds
		flagRed = sampleColour.getRed();
		flagBlue = sampleColour.getBlue();
		flagGreen = sampleColour.getGreen();
		
	}
	
	@SuppressWarnings("restriction")
	public void flushing() {
		player.stop();
		player.deallocate();
		player.close();
	}
	
	public void calibrate(int red, int rVar, int green, int gVar, int blue, int bVar) {
		//using these settings, calibrate the flag detection sensor
		flagRed = red;
		flagBlue = blue;
		flagGreen = green;
		//make sure the bounds aren't too loose
		if(rVar > 50) {
			System.out.println("red theshold (" + rVar + ") too loose");
			rThresh = 40;
		} else if(rVar < 11) {
			System.out.println("red threshold (" + rVar + ") too tight");
			rThresh = 11;
		} else {
			rThresh = rVar;
		}
		if(gVar > 50) {
			System.out.println("green threshold (" + gVar + ") too loose");
			gThresh = 40;
		} else if(gVar < 11) {
			System.out.println("green threshold (" + gVar + ") too tight");
			gThresh = 11;
		} else {
			gThresh = gVar;
		}
		if(bVar > 50) {
			System.out.println("blue threshold (" + bVar + ") too loose");
			bThresh = 40;
		} else if(bVar < 11) {
			System.out.println("blue threshold (" + bVar + ") too tight");
			bThresh = 11;
		} else {
			bThresh = bVar;
		}
		System.out.println("new colours assigned");
		System.out.println("colour settings: \n" +
				"red: " + flagRed + " +- " + rThresh + "\n" +
				"green: " + flagGreen + " +- " + gThresh + "\n" +
				"blue: " + flagBlue + " +- " + bThresh);
	}
	
	public void setShapes(FlagCast shapes) {
		//set the shapes required for choice detection and ambient occlusion
		//stencil = shapes;
		//construct the ArrayLists for shape detection
		int width = shapes.getIntWidth();
		int height = shapes.getIntHeight();
		GeneralPath pathN = shapes.getShape(1);
		GeneralPath pathNE = shapes.getShape(2);
		GeneralPath pathE = shapes.getShape(3);
		GeneralPath pathSE = shapes.getShape(4);
		GeneralPath pathS = shapes.getShape(5);
		GeneralPath pathSW = shapes.getShape(6);
		GeneralPath pathW = shapes.getShape(7);
		GeneralPath pathNW = shapes.getShape(8);
		
		int j = 0;
		int i;
		Rectangle rectN = pathNE.getBounds();
		int yN = (int)rectN.getMaxY();
		//from 0 down to this height, only 3 shapes have to be checked
		for(; j <= yN; j++) {
			for(i = 0; i < width; i++) {
				//check the 3 North shapes
				if(pathNW.contains(i,j)) {
					xtopRight.add(new Integer(i));
					ytopRight.add(new Integer(j));
				} else if(pathN.contains(i,j)) {
					xtop.add(new Integer(i));
					ytop.add(new Integer(j));
				} else if(pathNE.contains(i,j)) {
					xtopLeft.add(new Integer(i));
					ytopLeft.add(new Integer(j));
				}						
			}
		}
		//now for the horizontal lines of pixels a bit below..
		Rectangle rectM = pathW.getBounds();
		j = (int)rectM.getMinY(); //start j at the top of these rectangles
		Rectangle rectS = pathSW.getBounds();
		int stop = (int)rectS.getMinY();
		for(; j < stop; j++) {
			for(i = 0; i < width; i++) {
				//check the 3 Middle shapes
				if(pathW.contains(i,j)) {
					xright.add(new Integer(i));
					yright.add(new Integer(j));
				} else if(pathE.contains(i,j)) {
					xleft.add(new Integer(i));
					yleft.add(new Integer(j));
				} else if(pathS.contains(i,j)) {
					xbottom.add(new Integer(i));
					ybottom.add(new Integer(j));
				} //the order of those 3 shapes were sorted based on probability
			}
		}
		//now j is past the height of the middle shapes
		for(; j < height; j++) {
			for(i = 0; i < width; i++) {
				//check the 3 South shapes
				if(pathS.contains(i,j)) {
					xbottom.add(new Integer(i));
					ybottom.add(new Integer(j));
				} else if(pathSW.contains(i,j)) {
					xbottomRight.add(new Integer(i));
					ybottomRight.add(new Integer(j));
				} else if(pathSE.contains(i,j)) {
					xbottomLeft.add(new Integer(i));
					ybottomLeft.add(new Integer(j));
				}
			}
		}
		//finished storing the coordinates of all shapes
		System.out.println("shape allocation complete");
		//revealFirstPixels();
	}
	
	/*public void revealFirstPixels() {
		try {
			PrintStream out = new PrintStream(new FileOutputStream("unfilteredPixels.txt"));
			out.println("pixel coordinates:");
			for(int i = 0; i < xbottom.size(); i++) {
				out.println(xbottom.get(i).intValue() + " " + ybottom.get(i).intValue());
			}
			out.close();
		} catch(Exception e) {
			System.out.println("error in printing the unfiltered pixels");
		}
		System.out.println("unfiltered pixel printing is complete");
	}*/
	
	public void ambienceConfig(BufferedImage pic) {
		//we can assume the flag isn't in this picture
		//mark out all the areas that look like the flag
		int width = pic.getWidth();
		int height = pic.getHeight();
		SampleModel sm = pic.getSampleModel();
		int nbands = sm.getNumBands();
		Raster inputRaster = pic.getData();
		int[] pixels = new int[nbands*width*height];
		inputRaster.getPixels(0, 0, width, height, pixels);
		int offset = 0;
		int shapi;
		int band = 0;
		//now traverse the pixels
		for(shapi = 0; shapi < xtop.size(); shapi++) {
			//traverse all pixels of the top shape
			int w = (xtop.get(shapi)).intValue();
			int h = (ytop.get(shapi)).intValue();
			band = 0;
			offset = h*width*nbands + w*nbands;
			if(Math.abs(pixels[offset] - flagRed) < rThresh) {
				//red enough
				band++;
				if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
					//green enough
					band++;
					if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
						//blue enough
						xtop.remove(shapi);
						ytop.remove(shapi);
						if(shapi >= 1) {
							//also remove the pixel before it
							xtop.remove(shapi-1);
							ytop.remove(shapi-1);
							if(shapi < xtop.size()) {
								//also remove the pixel after it
								xtop.remove(shapi-1);
								ytop.remove(shapi-1);
							}
						} else {
							if(shapi < xtop.size()) {
								xtop.remove(shapi);
								ytop.remove(shapi);
							}	
						}
						//finished removing pixels	
					}
				}
			}
		}
		//now for the next shape:
		for(shapi = 0; shapi < xtopLeft.size(); shapi++) {
			//traverse all pixels of the topLeft shape
			int w = (xtopLeft.get(shapi)).intValue();
			int h = (ytopLeft.get(shapi)).intValue();
			band = 0;
			offset = h*width*nbands + w*nbands;
			if(Math.abs(pixels[offset] - flagRed) < rThresh) {
				//red enough
				band++;
				if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
					//green enough
					band++;
					if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
						//blue enough
						xtopLeft.remove(shapi);
						ytopLeft.remove(shapi);
						if(shapi >= 1) {
							//also remove the pixel before it
							xtopLeft.remove(shapi-1);
							ytopLeft.remove(shapi-1);
							if(shapi < xtopLeft.size()) {
								//also remove the pixel after it
								xtopLeft.remove(shapi-1);
								ytopLeft.remove(shapi-1);
							}
						} else {
							if(shapi < xtopLeft.size()) {
								xtopLeft.remove(shapi);
								ytopLeft.remove(shapi);
							}	
						}
						//finished removing pixels	
					}
				}
			}
		}
		//next shape..
		for(shapi = 0; shapi < xleft.size(); shapi++) {
			//traverse all pixels of the left shape
			int w = (xleft.get(shapi)).intValue();
			int h = (yleft.get(shapi)).intValue();
			band = 0;
			offset = h*width*nbands + w*nbands;
			if(Math.abs(pixels[offset] - flagRed) < rThresh) {
				//red enough
				band++;
				if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
					//green enough
					band++;
					if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
						//blue enough
						xleft.remove(shapi);
						yleft.remove(shapi);
						if(shapi >= 1) {
							//also remove the pixel before it
							xleft.remove(shapi-1);
							yleft.remove(shapi-1);
							if(shapi < xleft.size()) {
								//also remove the pixel after it
								xleft.remove(shapi-1);
								yleft.remove(shapi-1);
							}
						} else {
							if(shapi < xleft.size()) {
								xleft.remove(shapi);
								yleft.remove(shapi);
							}	
						}
						//finished removing pixels	
					}
				}
			}
		}
		//next shape
		for(shapi = 0; shapi < xbottomLeft.size(); shapi++) {
			//traverse all pixels of the bottomLeft shape
			int w = (xbottomLeft.get(shapi)).intValue();
			int h = (ybottomLeft.get(shapi)).intValue();
			band = 0;
			offset = h*width*nbands + w*nbands;
			if(Math.abs(pixels[offset] - flagRed) < rThresh) {
				//red enough
				band++;
				if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
					//green enough
					band++;
					if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
						//blue enough
						xbottomLeft.remove(shapi);
						ybottomLeft.remove(shapi);
						if(shapi >= 1) {
							//also remove the pixel before it
							xbottomLeft.remove(shapi-1);
							ybottomLeft.remove(shapi-1);
							if(shapi < xbottomLeft.size()) {
								//also remove the pixel after it
								xbottomLeft.remove(shapi-1);
								ybottomLeft.remove(shapi-1);
							}
						} else {
							if(shapi < xbottomLeft.size()) {
								xbottomLeft.remove(shapi);
								ybottomLeft.remove(shapi);
							}	
						}
						//finished removing pixels	
					}
				}
			}
		}
		//next shape
		for(shapi = 0; shapi < xbottom.size(); shapi++) {
			//traverse all pixels of the bottom shape
			int w = (xbottom.get(shapi)).intValue();
			int h = (ybottom.get(shapi)).intValue();
			band = 0;
			offset = h*width*nbands + w*nbands;
			if(Math.abs(pixels[offset] - flagRed) < rThresh) {
				//red enough
				band++;
				if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
					//green enough
					band++;
					if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
						//blue enough
						xbottom.remove(shapi);
						ybottom.remove(shapi);
						if(shapi >= 1) {
							//also remove the pixel before it
							xbottom.remove(shapi-1);
							ybottom.remove(shapi-1);
							if(shapi < xbottom.size()) {
								//also remove the pixel after it
								xbottom.remove(shapi-1);
								ybottom.remove(shapi-1);
							}
						} else {
							if(shapi < xbottom.size()) {
								xbottom.remove(shapi);
								ybottom.remove(shapi);
							}	
						}
						//finished removing pixels	
					}
				}
			}
		}
		//next shape
		for(shapi = 0; shapi < xbottomRight.size(); shapi++) {
			//traverse all pixels of the bottomRight shape
			int w = (xbottomRight.get(shapi)).intValue();
			int h = (ybottomRight.get(shapi)).intValue();
			band = 0;
			offset = h*width*nbands + w*nbands;
			if(Math.abs(pixels[offset] - flagRed) < rThresh) {
				//red enough
				band++;
				if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
					//green enough
					band++;
					if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
						//blue enough
						xbottomRight.remove(shapi);
						ybottomRight.remove(shapi);
						if(shapi >= 1) {
							//also remove the pixel before it
							xbottomRight.remove(shapi-1);
							ybottomRight.remove(shapi-1);
							if(shapi < xbottomRight.size()) {
								//also remove the pixel after it
								xbottomRight.remove(shapi-1);
								ybottomRight.remove(shapi-1);
							}
						} else {
							if(shapi < xbottomRight.size()) {
								xbottomRight.remove(shapi);
								ybottomRight.remove(shapi);
							}	
						}
						//finished removing pixels	
					}
				}
			}
		}
		//next shape
		for(shapi = 0; shapi < xright.size(); shapi++) {
			//traverse all pixels of the right shape
			int w = (xright.get(shapi)).intValue();
			int h = (yright.get(shapi)).intValue();
			band = 0;
			offset = h*width*nbands + w*nbands;
			if(Math.abs(pixels[offset] - flagRed) < rThresh) {
				//red enough
				band++;
				if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
					//green enough
					band++;
					if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
						//blue enough
						xright.remove(shapi);
						yright.remove(shapi);
						if(shapi >= 1) {
							//also remove the pixel before it
							xright.remove(shapi-1);
							yright.remove(shapi-1);
							if(shapi < xright.size()) {
								//also remove the pixel after it
								xright.remove(shapi-1);
								yright.remove(shapi-1);
							}
						} else {
							if(shapi < xright.size()) {
								xright.remove(shapi);
								yright.remove(shapi);
							}	
						}
						//finished removing pixels	
					}
				}
			}
		}
		//next shape..
		for(shapi = 0; shapi < xtopRight.size(); shapi++) {
			//traverse all pixels of the topRight shape
			int w = (xtopRight.get(shapi)).intValue();
			int h = (ytopRight.get(shapi)).intValue();
			band = 0;
			offset = h*width*nbands + w*nbands;
			if(Math.abs(pixels[offset] - flagRed) < rThresh) {
				//red enough
				band++;
				if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
					//green enough
					band++;
					if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
						//blue enough
						xtopRight.remove(shapi);
						ytopRight.remove(shapi);
						if(shapi >= 1) {
							//also remove the pixel before it
							xtopRight.remove(shapi-1);
							ytopRight.remove(shapi-1);
							if(shapi < xtopRight.size()) {
								//also remove the pixel after it
								xtopRight.remove(shapi-1);
								ytopRight.remove(shapi-1);
							}
						} else {
							if(shapi < xtopRight.size()) {
								xtopRight.remove(shapi);
								ytopRight.remove(shapi);
							}	
						}
						//finished removing pixels	
					}
				}
			}
		}
		//now all the shapes are completely free of ambience!!!
		//convert the ArrayList<Integer>'s to int[][]'s for faster access and lower memory
		fillPoints();
		System.out.println("ambient occlusion complete");
	}
	
	public void fillPoints() {
		//move the coordinates from the ArrayList<Integer>'s to int[][]'s for
		//faster access and lower memory :)
		System.out.println("optimising shape correspondence...");
		int xs = xtop.size();
		System.out.println("top box to have " + xs + " points");
		shapeN = new int[xs][2];
		for(int i = 0; i < xs; i++) {
			shapeN[i][0] = (xtop.get(i)).intValue();
			shapeN[i][1] = (ytop.get(i)).intValue();
		}
		
		xs = xtopLeft.size();
		shapeNE = new int[xs][2];
		for(int i = 0; i < xs; i++) {
			shapeNE[i][0] = (xtopLeft.get(i)).intValue();
			shapeNE[i][1] = (ytopLeft.get(i)).intValue();
		}
		
		xs = xleft.size();
		shapeE = new int[xs][2];
		for(int i = 0; i < xs; i++) {
			shapeE[i][0] = (xleft.get(i)).intValue();
			shapeE[i][1] = (yleft.get(i)).intValue();
		}
		
		xs = xbottomLeft.size();
		this.shapeSE = new int[xs][2];
		for(int i = 0; i < xs; i++) {
			shapeSE[i][0] = (xbottomLeft.get(i)).intValue();
			shapeSE[i][1] = (ybottomLeft.get(i)).intValue();
		}
		
		xs = xbottom.size();
		shapeS = new int[xs][2];
		for(int i = 0; i < xs; i++) {
			shapeS[i][0] = (xbottom.get(i)).intValue();
			shapeS[i][1] = (ybottom.get(i)).intValue();
		}
		
		xs = xbottomRight.size();
		shapeSW = new int[xs][2];
		for(int i = 0; i < xs; i++) {
			shapeSW[i][0] = (xbottomRight.get(i)).intValue();
			shapeSW[i][1] = (ybottomRight.get(i)).intValue();
		}
		
		xs = xright.size();
		shapeW = new int[xs][2];
		for(int i = 0; i < xs; i++) {
			shapeW[i][0] = (xright.get(i)).intValue();
			if(i < 3) {
				System.out.println("just assigned " + shapeW[i][0]);
			}
			shapeW[i][1] = (yright.get(i)).intValue();
		}
		
		xs = xtopRight.size();
		shapeNW = new int[xs][2];
		for(int i = 0; i < xs; i++) {
			shapeNW[i][0] = (xtopRight.get(i)).intValue();
			shapeNW[i][1] = (ytopRight.get(i)).intValue();
		}
		
		//now get rid of the ArrayList<Integer>'s to cut back on memory
		xtop = null;
		ytop = null;
		xtopLeft = null;
		ytopLeft = null;
		xleft = null;
		yleft = null;
		xbottomLeft = null;
		ybottomLeft = null;
		xbottom = null;
		ybottom = null;
		xbottomRight = null;
		ybottomRight = null;
		xright = null;
		yright = null;
		xtopRight = null;
		ytopRight = null;
		//revealArray();
	}
	
	/*public void revealArray() {
		//reveal the contents of shapeSE[][] now that it should be full of points
		try {
			PrintStream out = new PrintStream(new FileOutputStream("filteredPixels.txt"));
			out.println("pixel coordinates in the int[][] array");
			for(int i = 0; i < shapeSE.length; i++) {
				out.println(shapeSE[i][0] + " " + shapeSE[i][1]);
			}
			out.close();
		} catch(Exception e) {
			System.out.println("error in printing the pixels");
		}
		System.out.println("pixel printing is complete");
	}*/
	
	boolean checkRegion(char region, BufferedImage pic) {
		//check if the flag is in the specified region in that picture
		boolean conclusion = false;
		if(region < '0' || region > '7') {
			System.out.println("incorrect method call for checkRegion()");
			return false;
		}
		int width = pic.getWidth();
		int height = pic.getHeight();
		SampleModel sm = pic.getSampleModel();
		int nbands = sm.getNumBands();
		Raster inputRaster = pic.getData();
		int[] pixels = new int[nbands*width*height];
		inputRaster.getPixels(0, 0, width, height, pixels);
		int offset = 0;
		int pix;
		int band = 0;
		int colourCount = 0;
		//now traverse the pixels
		
		//binary search for runtime speed
		if(region <= '3') {
			if(region <= '1') {
				if(region == '0') {
					//use shapeN for checking
					for(pix = 0; pix < shapeN.length; pix++) {
						//traverse all pixels of the top shape
						int w = shapeN[pix][0];
						int h = shapeN[pix][1];
						band = 0;
						offset = h*width*nbands + w*nbands;
						if(Math.abs(pixels[offset] - flagRed) < rThresh) {
							//red enough
							band++;
							if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
								//green enough
								band++;
								if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
									//blue enough
									colourCount++;
									if(colourCount > 3) {
										//alright, that's enough colours
										return true;
									}
									//finished removing pixels	
								}
							}
						}
					}
				} else {
					//region == 1
					for(pix = 0; pix < shapeNE.length; pix++) {
						//traverse all pixels of the top shape
						int w = shapeNE[pix][0];
						int h = shapeNE[pix][1];
						band = 0;
						offset = h*width*nbands + w*nbands;
						if(Math.abs(pixels[offset] - flagRed) < rThresh) {
							//red enough
							band++;
							if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
								//green enough
								band++;
								if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
									//blue enough
									colourCount++;
									if(colourCount > 3) {
										//alright, that's enough colours
										return true;
									}
									//finished removing pixels	
								}
							}
						}
					}
				}
			} else {
				if(region == '2') {
					for(pix = 0; pix < shapeE.length; pix++) {
						//traverse all pixels of the top shape
						int w = shapeE[pix][0];
						int h = shapeE[pix][1];
						band = 0;
						offset = h*width*nbands + w*nbands;
						if(Math.abs(pixels[offset] - flagRed) < rThresh) {
							//red enough
							band++;
							if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
								//green enough
								band++;
								if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
									//blue enough
									colourCount++;
									if(colourCount > 3) {
										//alright, that's enough colours
										return true;
									}
									//finished removing pixels	
								}
								System.out.println("");
							}
						}
					}	
				} else {
					//region == 3
					//System.out.println("there are " + shapeSE.length + " pixels in this region");
					for(pix = 0; pix < shapeSE.length; pix++) {
						//traverse all pixels of the top shape
						int w = shapeSE[pix][0];
						int h = shapeSE[pix][1];
						band = 0;
						offset = h*width*nbands + w*nbands;
						if(Math.abs(pixels[offset] - flagRed) < rThresh) {
							//red enough
							band++;
							if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
								//green enough
								//System.out.print("g ");
								band++;
								if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
									//blue enough
									//System.out.print("b");
									colourCount++;
									if(colourCount > 3) {
										//alright, that's enough colours
										return true;
									}
									//finished removing pixels	
								}
							}
						}
					}
				}
			}
		} else {
			if(region <= '5') {
				if(region == '4') {
					for(pix = 0; pix < shapeS.length; pix++) {
						//traverse all pixels of the top shape
						int w = shapeS[pix][0];
						int h = shapeS[pix][1];
						band = 0;
						offset = h*width*nbands + w*nbands;
						if(Math.abs(pixels[offset] - flagRed) < rThresh) {
							//red enough
							band++;
							if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
								//green enough
								band++;
								if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
									//blue enough
									colourCount++;
									if(colourCount > 3) {
										//alright, that's enough colours
										return true;
									}
									//finished removing pixels	
								}
							}
						}
					}
				} else {
					//region == 5
					for(pix = 0; pix < shapeSW.length; pix++) {
						//traverse all pixels of the top shape
						int w = shapeSW[pix][0];
						int h = shapeSW[pix][1];
						band = 0;
						offset = h*width*nbands + w*nbands;
						if(Math.abs(pixels[offset] - flagRed) < rThresh) {
							//red enough
							band++;
							if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
								//green enough
								band++;
								if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
									//blue enough
									colourCount++;
									if(colourCount > 3) {
										//alright, that's enough colours
										return true;
									}
									//finished removing pixels	
								}
							}
						}
					}
				}
			} else {
				if(region == '6') {
					for(pix = 0; pix < shapeW.length; pix++) {
						//traverse all pixels of the top shape
						int w = shapeW[pix][0];
						int h = shapeW[pix][1];
						band = 0;
						offset = h*width*nbands + w*nbands;
						if(Math.abs(pixels[offset] - flagRed) < rThresh) {
							//red enough
							band++;
							if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
								//green enough
								band++;
								if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
									//blue enough
									colourCount++;
									if(colourCount > 3) {
										//alright, that's enough colours
										return true;
									}
									//finished removing pixels	
								}
							}
						}
					}
				} else {
					//region == 7
					for(pix = 0; pix < shapeNW.length; pix++) {
						//traverse all pixels of the top shape
						int w = shapeNW[pix][0];
						int h = shapeNW[pix][1];
						band = 0;
						offset = h*width*nbands + w*nbands;
						if(Math.abs(pixels[offset] - flagRed) < rThresh) {
							//red enough
							band++;
							if(Math.abs(pixels[offset + band] - flagGreen) < gThresh) {
								//green enough
								band++;
								if(Math.abs(pixels[offset + band] - flagBlue) < bThresh) {
									//blue enough
									colourCount++;
									if(colourCount > 3) {
										//alright, that's enough colours
										return true;
									}
									//finished removing pixels	
								}
							}
						}
					}
				}
			}
		}
		return conclusion;
	}
	
	//get rid of this, it's being replaced by a method where you pick the sector for where to look
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
	/*
	public void revealShape() {
		//for the moment, it'll be the SE shape
		try {
			PrintStream out = new PrintStream(new FileOutputStream("shapePixels.txt"));
			out.println("pixel coordinates:");
			for(int i = 0; i < shapeS.length; i++) {
				out.println(shapeS[i][0] + " " + shapeS[i][1]);
			}
			out.close();
		} catch(Exception e) {
			System.out.println("error in printing the pixels");
		}
		System.out.println("pixel printing is complete");
	}*/
	
}
