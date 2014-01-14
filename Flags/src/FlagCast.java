import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import javax.swing.JPanel;


public class FlagCast extends JPanel {
	//this class holds the shapes storing
	//the positions for Semaphore flags
	
	private GeneralPath top;
	private GeneralPath topLeft;
	private GeneralPath left;
	private GeneralPath bottomLeft;
	private GeneralPath bottom;
	private GeneralPath bottomRight;
	private GeneralPath right;
	private GeneralPath topRight;
	//private RoundRectangle2D.Double pageW;	//pages to write the text on
	//private RoundRectangle2D.Double pageC;
	//private RoundRectangle2D.Double pageE;
	
	//the collection of all the flags:
	private ArrayList<BufferedImage> navyFlags = new ArrayList<BufferedImage>();
	private ArrayList<BufferedImage> racingFlags = new ArrayList<BufferedImage>();
	private ArrayList<AffineTransform> flagLocations = new ArrayList<AffineTransform>();
	
	private ArrayList<String> racingStrings = new ArrayList<String>();
	private ArrayList<String> intNavalStrings = new ArrayList<String>();
	//the flags currently hanging:
	private int[] flagIndices = {-1, -1, -1, -1, -1, -1};
	
	private boolean[] regionShow = {true, true, true, true, true, true, true, true};
	
	private int width;
	private int height;
	
	//private int xOff = 0;
	//private int yOff = 0;
	
	private int imgW = 125; //average image width
	private int imgH = 100;
	private int xeps = 26; // just a thing to make the masts curved
	
	private String leftS = null;
	private String centreS;
	private String rightS;
	//private String messageS; //for messages that need a long word
	private char[] rightC = null;
	private Random luckyWheel = new Random();
	
	//private boolean displayBottom = true;
	
	private Font happyFont = new Font("SansSerif", Font.BOLD, 27);
	AlphaComposite translucent = AlphaComposite.getInstance(AlphaComposite.SRC_OUT, 1f);
	AlphaComposite textComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
	
	public char[] semAnsRegions() {
		//return the indices for the regions of the semaphore answer
		//so guiControl knows where to look
		if(centreS == null) {
			return null;
		} else {
			char s = centreS.charAt(0);
			if(s >= 'a' && s <= 'z') {
				//it's an alphabetical character
				int[] answer = {9,9};
				if(s <= 'g') {
					answer[0] = 4;
					if(s <= 'c') {
						int loc = s - 'a' + 5;
						answer[1] = loc;
					} else {
						int loc = s - 'd';
						answer[1] = loc;
					}
				} else if( s <= 'n') {
					if(s == 'j') {
						//j is different
						answer[0] = 0;
						answer[1] = 2;
					} else {
						answer[0] = 5;
						if(s < 'j') {
							int loc = s - 'h' + 6;
							answer[1] = loc;
						} else {
							int loc = s - 'k';
							answer[1] = loc;
						}
					}
				} else if( s <= 's') {
					answer[0] = 6;
					if(s == 'o') {
						answer[1] = 7;
					} else {
						int loc = s - 'p';
						answer[1] = loc;
					}
				} else if( s == 't') {
					answer[0] = 6;
					answer[1] = 0;
				} else if(s == 'u') {
					answer[0] = 6;
					answer[1] = 1;
				} else if(s == 'v') {
					answer[0] = 0;
					answer[1] = 3;
				} else if(s == 'w') {
					answer[0] = 1;
					answer[1] = 2;
				} else if(s == 'x') {
					answer[0] = 1;
					answer[1] = 3;
				} else if(s == 'y') {
					answer[0] = 6;
					answer[1] = 2;
				} else {
					//z
					answer[0] = 2;
					answer[1] = 3;
				}
				char[] charAnswer = new char[2];
				charAnswer[0] = (char)(answer[0] + '0');
				charAnswer[1] = (char)(answer[1] + '0');
				return charAnswer;
			} else {
				char[] charAnswer = {'4','4'}; //space
				return charAnswer;
			}
		}
	}
	
	public char waveFlag() {
		//think of a new multi-choice selection for racing flags
		leftS = null; //painting condition
		for(int i = 0; i <= 1; i++) {
			//assign new flags
			flagIndices[i] = luckyWheel.nextInt(racingFlags.size());
		}
		flagIndices[2] = -1;
		flagIndices[3] = -1;
		for(int i = 4; i <= 5; i++) {
			flagIndices[i] = luckyWheel.nextInt(racingFlags.size());
		}
		//now to pick the answer
		int answer = 1 + luckyWheel.nextInt(4);
		int answerIndex = answer - 1;
		if(answer > 2)
			answer += 3;
		//^accounting for display discontinuity
		if(answerIndex >= 2)
			answerIndex += 2;
		//only 4 racing flags are shown; not 6
		centreS = racingStrings.get(flagIndices[answerIndex]);
		char charAnswer = (char)('0' + answer);
		return charAnswer;
		
	}
	
	public char hoistFlag(boolean longNames) {
		//think of a new multi-choice selection for navy flags
		leftS = null; //this is for painting
		System.out.println("flag collection has " + navyFlags.size() + " flags");
		for(int i = 0; i < flagIndices.length; i++) {
			//assign new flags
			flagIndices[i] = luckyWheel.nextInt(navyFlags.size());
		}
		//now to pick which one will be the answer
		int answer = 1 + luckyWheel.nextInt(6);
		//answer shows the region, answerIndex is for working out the string later
		int answerIndex = answer - 1;
		if(answer > 3)
			answer++;
		//remember there's a discontinuity because South doesn't have a flag there
		//set the appropriate indices
		//int[] indexes = {flag1, flag2, flag3, flag4, flag5, flag6};
		//flagIndices = indexes;
		//to-do: set the text for the user
		String flagLabel;
		if(flagIndices[answerIndex] <= 25) {
			//alphabetical entry
			if(!longNames) {
				char flagChar = (char)('a' + flagIndices[answerIndex]);
				flagLabel = "" + flagChar;
			} else {
				flagLabel = intNavalStrings.get(flagIndices[answerIndex]);
			}
		} else if(flagIndices[answerIndex] <= 35) {
			//number pennant
			char flagChar = (char)('0' + flagIndices[answerIndex] - 26);
			flagLabel = "" + flagChar;
		} else if(flagIndices[answerIndex] <= 38) {
			//substitute
			System.out.println("repeat pennant");
			char ansChar = (char)('0' + flagIndices[answerIndex] - 36);
			flagLabel = "sub " + ansChar;
		} else {
			//distress or answer
			System.out.println("answer");
			flagLabel = "ans";
		}
		centreS = flagLabel;
		//
		char charAnswer = (char)(answer + '0');
		return charAnswer;
		
	}
	
	public void shiftLeft() {
		if(rightC == null || rightC.length <= 0) {
			//generate a new word
			leftS = "";
			//think of a new current character
			char cs = (char)('a' + luckyWheel.nextInt(26));
			centreS = "" + cs;
			rightC = new char[1 + luckyWheel.nextInt(7)]; //a new word of length 1-7
			for(int i = 0; i < rightC.length; i++) {
				int alphabetNum = luckyWheel.nextInt(27);
				if(alphabetNum >= 26) {
					rightC[i] = '_'; //space
				} else {
					rightC[i] = (char)('a' + alphabetNum);
				}
			}
			rightS = "";
			for(char cw : rightC) {
				rightS += cw;
			}
		} else {
			//we don't need a new word
			leftS = "" + leftS + centreS;
			centreS = "" + rightC[0];
			if(rightC.length > 1) {
				//move the characters along
				char[] newWord = new char[rightC.length -1];
				rightS = "";
				for(int i = 0; i < newWord.length; i++) {
					newWord[i] = rightC[i+1];
					rightS += newWord[i];
				}
				rightC = newWord;
			} else {
				rightC = null;
				rightS = null;
			}
		}
	}
	
	public FlagCast(int width1, int height1) {
		width = width1;
		height = height1;
		//specify width and height to make the shapes the right proportions
		double[] xtopPts = {0.32*width, 0.32*width, 0.35*width, 0.65*width, 0.68*width, 0.68*width};
		double[] ytopPts = {0, 0.29*height, 0.31*height, 0.31*height, 0.29*height, 0.0};
		top = new GeneralPath(GeneralPath.WIND_NON_ZERO, xtopPts.length);
		top.moveTo(xtopPts[0],ytopPts[0]);
		for(int index = 1; index < xtopPts.length; index++) {
			top.lineTo(xtopPts[index], ytopPts[index]);
		}
		
		double[] xtopLeftPts = {0.65*width, 0.68*width, 0.75*width, width, width, 0.65*width};
		double[] ytopLeftPts = {0.31*height, 0.29*height, 0.02*height, 0.08*height, 0.36*height, 0.36*height};
		topLeft = new GeneralPath(GeneralPath.WIND_NON_ZERO, xtopLeftPts.length);
		topLeft.moveTo(xtopLeftPts[0], ytopLeftPts[0]);
		for(int index = 1; index < xtopLeftPts.length; index++) {
			topLeft.lineTo(xtopLeftPts[index], ytopLeftPts[index]);
		}
		
		double[] xleftPts = {0.68*width, width, width, 0.93*width, 0.68*width, 0.65*width, 0.65*width, 0.68*width};
		double[] yleftPts = {0.45*height, 0.45*height, 0.74*height, 0.74*height, 0.69*height, 0.67*height, 0.59*height, 0.59*height};
		left = new GeneralPath(GeneralPath.WIND_NON_ZERO, xleftPts.length);
		left.moveTo(xleftPts[0], yleftPts[0]);
		for(int index = 1; index < xleftPts.length; index++) {
			left.lineTo(xleftPts[index], yleftPts[index]);
		}
		left.closePath();
		
		double[] xbottomLeftPts = {0.71*width, 0.9*width, width, width, 0.71*width};
		double[] ybottomLeftPts = {0.76*height, 0.85*height, 0.85*height, height, height};
		bottomLeft = new GeneralPath(GeneralPath.WIND_NON_ZERO, xbottomLeftPts.length);
		bottomLeft.moveTo(xbottomLeftPts[0], ybottomLeftPts[0]);
		for(int index = 1; index < xbottomLeftPts.length; index++) {
			bottomLeft.lineTo(xbottomLeftPts[index], ybottomLeftPts[index]);
		}
		bottomLeft.closePath();
		
		double[] xbottomPts = {0.35*width, 0.65*width, 0.65*width, 0.68*width, 0.68*width, 0.32*width, 0.32*width, 0.35*width};
		double[] ybottomPts = {0.59*height, 0.59*height, 0.67*height, 0.69*height, height, height, 0.69*height, 0.67*height};
		bottom = new GeneralPath(GeneralPath.WIND_NON_ZERO, xbottomPts.length);
		bottom.moveTo(xbottomPts[0], ybottomPts[0]);
		for(int index = 1; index < xbottomPts.length; index++) {
			bottom.lineTo(xbottomPts[index], ybottomPts[index]);
		}
		bottom.closePath();
		
		double[] xbottomRight = {0, 0.1*width, 0.29*width, 0.29*width, 0};
		double[] ybottomRight = {0.85*height, 0.85*height, 0.76*height, height, height};
		bottomRight = new GeneralPath(GeneralPath.WIND_NON_ZERO, xbottomRight.length);
		bottomRight.moveTo(xbottomRight[0], ybottomRight[0]);
		for(int index = 1; index < xbottomRight.length; index++) {
			bottomRight.lineTo(xbottomRight[index], ybottomRight[index]);
		}
		
		double[] xright = {0, 0.32*width, 0.32*width, 0.35*width, 0.35*width, 0.32*width, 0.07*width, 0};
		double[] yright = {0.45*height, 0.45*height, 0.59*height, 0.59*height, 0.67*height, 0.69*height, 0.74*height, 0.74*height};
		right = new GeneralPath(GeneralPath.WIND_NON_ZERO, xright.length);
		right.moveTo(xright[0], yright[0]);
		for(int index = 1; index < xright.length; index++) {
			right.lineTo(xright[index], yright[index]);
		}
		
		double[] xtopRight = {0, 0.25*width, 0.32*width, 0.35*width, 0.35*width, 0};
		double[] ytopRight = {0.08*height, 0.02*height, 0.29*height, 0.31*height, 0.36*height, 0.36*height};
		topRight = new GeneralPath(GeneralPath.WIND_NON_ZERO, xtopRight.length);
		topRight.moveTo(xtopRight[0], ytopRight[0]);
		for(int index = 1; index < xtopRight.length; index++) {
			topRight.lineTo(xtopRight[index], ytopRight[index]);
		}
		
		//make the pages on which to draw the text
		//pageW = new RoundRectangle2D.Double(0.15*width, 0.80*height, 0.2*width, 0.07*height, 9.0, 9.0);
		//pageC = new RoundRectangle2D.Double(0.49*width, 0.76*height, 0.05*width, 0.06*height, 9.0, 9.0);
		//pageE = new RoundRectangle2D.Double(0.68*width, 0.80*height, 0.2*width, 0.07*height, 9.0, 9.0);
		
		//set the translations for displaying images of flags
		double tx, ty;
		
		//NE
		tx = 0.7*width;
		ty = 0.37*height - imgH;
		if(ty < 0)
			ty = 0.0;
		AffineTransform trans = AffineTransform.getTranslateInstance(tx, ty);
		flagLocations.add(trans);
		
		//E
		ty = 0.6*height - 0.5*imgH;
		if(ty < 0)
			ty = 0.34*height;
		tx += xeps;
		if(tx > width)
			tx -= xeps;
		trans = AffineTransform.getTranslateInstance(tx,ty);
		flagLocations.add(trans);
		
		//SE
		tx = 0.7*width;
		ty = 0.8*height;
		trans = AffineTransform.getTranslateInstance(tx,ty);
		flagLocations.add(trans);
		
		//SW
		tx = 0.3*width - imgW;
		if(tx < 0)
			tx = 0;
		trans = AffineTransform.getTranslateInstance(tx,ty);
		flagLocations.add(trans);
		
		//W
		tx -= xeps;
		if(tx < 0)
			tx = 0;
		ty = 0.6*height - 0.5*imgH;
		if(ty < 0)
			ty = 0.34*height;
		trans = AffineTransform.getTranslateInstance(tx,ty);
		flagLocations.add(trans);
		
		//NW
		tx = 0.3*width - imgW;
		if(tx < 0)
			tx = 0;
		ty = 0.37*height - imgH;
		if(ty < 0)
			ty = 0.0;
		trans = AffineTransform.getTranslateInstance(tx,ty);
		flagLocations.add(trans);
		
		System.out.println("finished AffineTransforms");
		
		//write the racing strings
		racingStrings.add("finish");
		racingStrings.add("danger - race stop");
		racingStrings.add("slippery track");
		racingStrings.add("slow vehicles on track");
		racingStrings.add("danger");
		racingStrings.add("you're being passed");
		racingStrings.add("all clear");
		racingStrings.add("warning for you");
		racingStrings.add("you are disqualified");
		racingStrings.add("telemetry failure");
		
		//write the naval strings
		intNavalStrings.add("diver below / spd trial");
		intNavalStrings.add("explosives");
		intNavalStrings.add("affirmitive");
		intNavalStrings.add("keep clear, difficult manoevre");
		intNavalStrings.add("altering to starboard");
		intNavalStrings.add("I'm disabled, please communicate");
		intNavalStrings.add("requesting pilot");
		intNavalStrings.add("pilot available");
		intNavalStrings.add("altering to port");
		intNavalStrings.add("sending semaphore message");
		intNavalStrings.add("stop instantly");
		intNavalStrings.add("hold on, I must communicate");
		intNavalStrings.add("doctor available");
		intNavalStrings.add("no");
		intNavalStrings.add("man overboard");
		intNavalStrings.add("all aboard");
		intNavalStrings.add("healthy, requesting free practique");
		intNavalStrings.add("way is off my ship, feel past");
		intNavalStrings.add("my engines are full speed astern");
		intNavalStrings.add("don't pass ahead of me");
		intNavalStrings.add("you're standing into danger");
		intNavalStrings.add("assistance required please");
		intNavalStrings.add("medical assistance required");
		intNavalStrings.add("stop your intentions, watch my signals");
		intNavalStrings.add("carrying mails");
		intNavalStrings.add("shore station");
	}
	
	public void flushing() {
		for(BufferedImage b : navyFlags) {
			b.flush();
		}
		navyFlags = null;
	}
	
	public int getIntWidth() {
		return width;
	}
	
	public int getIntHeight() {
		return height;
	}
	
	public void hideRegion(char rc) {
		int r = rc - '0';
		if(r >= 8) {
			boolean[] newRegions = new boolean[8]; //false by default
			regionShow = newRegions;
		} else {
			if(r > 0) {
				regionShow[r] = false;
			}
		}
	}
	
	public void showRegion(char rc) {
		//show the flag-detecting regions
		int r = rc - '0';
		if(r >= 8) {
			boolean[] newRegions = {true, true, true, true, true, true, true, true};
			regionShow = newRegions;
		} else {
			if(r >= 0) {
				regionShow[r] = true;
			}
		}
	}
	
	public void loadFlags() {
		//load in all available flags
		
		try{
			char prefix = 'A';
			for(; prefix <= 'Z'; prefix++) {
				File f = new File(prefix + ".gif");
				if(f.exists() && f.canRead()) {
					BufferedImage img = ImageIO.read(f);
					navyFlags.add(img);
				} else {
					System.out.println("unable to locate image for naval flag");
				}
			}
			prefix = '0';
			for(; prefix <= '9'; prefix++) {
				File f = new File(prefix + ".gif");
				if(f.exists() && f.canRead()) {
					BufferedImage img = ImageIO.read(f);
					navyFlags.add(img);
				} else {
					System.out.println("unable to locate image of number pennant");
				}
			}
			prefix = '1';
			String prePrefix = "sub";
			for(; prefix <= '3'; prefix++) {
				File f = new File(prePrefix + prefix + ".gif");
				if(f.exists() && f.canRead()) {
					BufferedImage img = ImageIO.read(f);
					navyFlags.add(img);
				} else {
					System.out.println("unable to locate image of substitute pennant");
				}
			}
			File f = new File("amswer1.gif");
			if(f.exists() && f.canRead()) {
				BufferedImage img = ImageIO.read(f);
				navyFlags.add(img);
			} else {
				System.out.println("unable to locate answer pennant");
			}
			//now for the racing flags
			prefix = '0';
			for(; prefix <= '9'; prefix++) {
				f = new File("race_" + prefix + ".gif"); //not really a prefix anymore
				if(f.exists() && f.canRead()) {
					BufferedImage img = ImageIO.read(f);
					racingFlags.add(img);
				} else {
					System.out.println("unable to locate image of racing flag");
				}
			}
			
		} catch(Exception e) {
			System.out.println("something went wrong while reading the images");
		}
	}
	
	public void displayChoices(int img1, int img2, int img3, int img4, int img5, int img6) {
		//display the current flags able to be selected
		//all the ints refer to positions in the collection
		int[] indices = {img1, img2, img3, img4, img5, img6};
		flagIndices = indices;
		
	}
	
	/*public void showAnswer(boolean b) {
		//show or hide the answer
		
	}
	*/
	public void translate(int x, int y) {
		//set the offset for the frame
		AffineTransform translation = AffineTransform.getTranslateInstance(x, y);
		top.transform(translation);
		topLeft.transform(translation);
		left.transform(translation);
		bottomLeft.transform(translation);
		bottom.transform(translation);
		bottomRight.transform(translation);
		right.transform(translation);
		topRight.transform(translation);
	}
	
	public GeneralPath getShape(int shapeNum) {
		switch(shapeNum) {
			case 1: return top;
			case 2: return topLeft;
			case 3: return left;
			case 4: return bottomLeft;
			case 5: return bottom;
			case 6: return bottomRight;
			case 7: return right;
			case 8: return topRight;
		}
		//else
		return new GeneralPath();
	}
	
	public int chooseXPos() {
		//chooses where to place strings based on their length
		double answer = 0;
		answer = 0.5*width - 5*(centreS.length());
		return (int)answer;
	}
	
	public void paintComponent(Graphics g) {
        
    	super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setComposite(translucent);
        
        if(regionShow[0]) {
        	g2.setColor(Color.BLUE);
        	g2.fill(top);
        }
        if(regionShow[1]) {
        	g2.setColor(Color.RED);
        	g2.fill(topLeft);
        }
        if(regionShow[2]) {
	        g2.setColor(Color.GREEN.darker().darker());
	        g2.fill(left);
        }
        if(regionShow[3]) {
        	g2.setColor(Color.YELLOW);
        	g2.fill(bottomLeft);
        }
        if(regionShow[4]) {
        	g2.setColor(Color.CYAN);
        	g2.fill(bottom);
        }
        if(regionShow[5]) {
        	g2.setColor(Color.MAGENTA);
        	g2.fill(bottomRight);
        }
        if(regionShow[6]) {
	        g2.setColor(Color.ORANGE);
	        g2.fill(right);
        }
        if(regionShow[7]) {
	        g2.setColor(Color.LIGHT_GRAY);
	        g2.fill(topRight);
        }
        
        //now for the images
        translucent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
        g2.setComposite(translucent);
        if(flagIndices[3] != -1) {
	        for(int f = 0; f < flagIndices.length; f++) {
		        if(flagIndices[f] != -1) {
		        	BufferedImage img1 = navyFlags.get(flagIndices[f]);
		        	//Rectangle bounds = topLeft.getBounds();
		        	//AffineTransform transform = AffineTransform.getTranslateInstance(bounds.x, bounds.y);
		        	//g2.drawImage(img1, bounds.x, bounds.y, null);
		        	//g2.drawImage(img1, transform, null);
		        	g2.drawImage(img1, flagLocations.get(f), null);
		        }
	        }
        } else if(flagIndices[0] != -1) {
        	//motorsport flags
        	for(int f = 0; f < flagIndices.length; f++) {
		        if(flagIndices[f] != -1) {
		        	BufferedImage img1 = racingFlags.get(flagIndices[f]);
		        	//Rectangle bounds = topLeft.getBounds();
		        	//AffineTransform transform = AffineTransform.getTranslateInstance(bounds.x, bounds.y);
		        	//g2.drawImage(img1, bounds.x, bounds.y, null);
		        	//g2.drawImage(img1, transform, null);
		        	g2.drawImage(img1, flagLocations.get(f), null);
		        }
	        }
        }
        //finally, write the text
        if(leftS != null) {
        	g2.setComposite(textComposite);
        	g2.setFont(happyFont);
        	//g2.setComposite(textComposite);
        	g2.setColor(Color.MAGENTA);
        	g2.drawString(leftS, (int)(0.16*width), (int)(0.85*height));
        	//int centreXpos = chooseXPos();
        	g2.drawString(centreS, chooseXPos(), (int)(0.81*height));
        	//pageC.width = 2*(0.5*width - centreXpos + 20);
        	//pageC.x = centreXpos - 9;
        	if(rightS != null) {
        		g2.drawString(rightS, (int)(0.7*width), (int)(0.85*height));
        	}
        	//g2.setColor(Color.DARK_GRAY);
        	//g2.setComposite(translucent);
        	//g2.fill(pageW);
        	//g2.fill(pageC);
        	//g2.fill(pageE);
        } else if(centreS != null) {
        	//naval game
        	g2.setComposite(textComposite);
        	g2.setFont(happyFont);
        	g2.setColor(Color.MAGENTA);
        	int centreXpos = chooseXPos();
        	g2.drawString(centreS, centreXpos, (int)(0.81*height));
        	g2.setColor(Color.DARK_GRAY);
        	//pageC.width = (int)(2.4*(0.5*(double)width - (double)centreXpos + 11.0));
        	//pageC.x = centreXpos - 10;
        	//g2.setComposite(translucent);
        	//g2.fill(pageC);
        }
    }
	
	public boolean isOpaque() {
		return false;
	}
	
}
