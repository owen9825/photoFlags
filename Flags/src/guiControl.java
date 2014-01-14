import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.*;

public class guiControl extends Thread implements ActionListener {
	//this class is in control of the graphics, the buttons, the universe
	
	JFrame mainFrame = new JFrame("PhotoFlags 1.0");
	FlagWatch drapeau = null;
	
	FlagCast handShapes;
	
	JButton colourSet = new JButton("Set colour");
	JButton ambience = new JButton("Configure Ambience");
	JButton playButton = new JButton("Play!");
	JButton bSemaphore = new JButton("Semaphore");
	JButton bIntNaval = new JButton("Int Naval");
	JButton bMotor = new JButton("Motorsport");
	JButton newGame = new JButton("New game");
	//JLabel smallScreen = new JLabel();
	//SmallScreen altDisp = null;
	
	BufferedImage lastPhoto = null;
	Color flagColour = null;
	private ArrayList<Clip> clips = new ArrayList<Clip>();
	
	private boolean keepGoing = true;
	private boolean started = false;
	private char game = 's';
	private int surrenderTimer = 12; //how many iterations before the answer is displayed
	
	public guiControl() {
		Container contentPane = mainFrame.getContentPane();
		//BoxLayout layout = new BoxLayout(contentPane, BoxLayout.Y_AXIS);
		GroupLayout layout = new GroupLayout(contentPane);
		//OverlayLayout layout = new OverlayLayout(contentPane);
		contentPane.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				mainFrame.setVisible(false);
				//is this necessary?
				drapeau.flushing();
				handShapes.flushing();
				mainFrame.dispose();
				System.exit(0);
			}
		});
		mainFrame.setSize(400,250);
		
		//mainFrame.add(colourSet, BorderLayout.SOUTH);
		//mainFrame.add(testFeature, BorderLayout.NORTH);
		colourSet.addActionListener(this);
		colourSet.setActionCommand("newColours");
		ambience.setEnabled(false);
		ambience.addActionListener(this);
		ambience.setActionCommand("ambience");
		playButton.setEnabled(false);
		playButton.addActionListener(this);
		playButton.setActionCommand("play");
		bSemaphore.addActionListener(this);
		bSemaphore.setActionCommand("sem");
		bIntNaval.addActionListener(this);
		bIntNaval.setActionCommand("navy");
		bMotor.addActionListener(this);
		bMotor.setActionCommand("motor");
		newGame.addActionListener(this);
		newGame.setActionCommand("game");
	}
		
	public void screen2(BufferedImage pic) {
		if(lastPhoto == null) {
			lastPhoto = pic;
			//SmallScreen altDisp = new SmallScreen(lastPhoto);
			//JFrame picFrame = new JFrame("Snapshot");
			//picFrame.add(altDisp);
			//picFrame.pack();
			//picFrame.setVisible(true);
			//mainFrame.add(altDisp, BorderLayout.EAST);
			//mainFrame.pack();
		} else {
			lastPhoto = pic;
			SmallScreen altDisp = new SmallScreen(lastPhoto);
			drapeau.identify(lastPhoto);
			JFrame picFrame = new JFrame("Snapshot");
			picFrame.add(altDisp);
			picFrame.pack();
			picFrame.setVisible(true);
			//mainFrame.pack();
		}
	}
		
	public void createAndShowGUI() {
		Container contentPane = mainFrame.getContentPane();
		//BoxLayout layout = new BoxLayout(contentPane, BoxLayout.Y_AXIS);
		GroupLayout layout = (GroupLayout)contentPane.getLayout();
		drapeau = new FlagWatch();
		JPanel webcam = drapeau.getDisplay();
		Component c = webcam.getComponent(0);
		c.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				screen2(drapeau.getSnapshot());				
				coupDeColour(x, y);
			}
		});
		//JPanel graphicsSet = new JPanel();
		//graphicsSet.add(c);
		//graphicsSet.setPreferredSize(c.getPreferredSize());
		Dimension dp = c.getPreferredSize();
		handShapes = new FlagCast(dp.width, dp.height);
		//Pear fruitPanel = new Pear();
		//mainFrame.add(fruitPanel);
		mainFrame.setGlassPane(handShapes);
		handShapes.setVisible(false);
		Color flagColour = new Color(246, 68, 92);
		drapeau.setColour(flagColour);
		drapeau.setShapes(handShapes);
		//handShapes.setLocation(c.getLocation());
		//handShapes.translate(c.getX(), c.getY());
		
		GroupLayout.SequentialGroup hlowGroup = layout.createSequentialGroup();
		hlowGroup.addComponent(colourSet);
		hlowGroup.addComponent(ambience);
		hlowGroup.addComponent(playButton);
		GroupLayout.ParallelGroup hscreenGroup = layout.createParallelGroup();
		hscreenGroup.addComponent(c);
		hscreenGroup.addGroup(hlowGroup);
		GroupLayout.ParallelGroup hgameGroup = layout.createParallelGroup();
		hgameGroup.addComponent(bSemaphore);
		hgameGroup.addComponent(bIntNaval);
		hgameGroup.addComponent(bMotor);
		hgameGroup.addComponent(newGame);
		GroupLayout.SequentialGroup hframeGroup = layout.createSequentialGroup();
		hframeGroup.addGroup(hscreenGroup);
		hframeGroup.addGroup(hgameGroup);
		layout.setHorizontalGroup(hframeGroup);
		
		GroupLayout.ParallelGroup vlowGroup = layout.createParallelGroup();
		vlowGroup.addComponent(colourSet);
		vlowGroup.addComponent(ambience);
		vlowGroup.addComponent(playButton);
		GroupLayout.SequentialGroup vscreenGroup = layout.createSequentialGroup();
		vscreenGroup.addComponent(c);
		vscreenGroup.addGroup(vlowGroup);
		GroupLayout.SequentialGroup vgameGroup = layout.createSequentialGroup();
		vgameGroup.addComponent(bSemaphore);
		vgameGroup.addComponent(bIntNaval);
		vgameGroup.addComponent(bMotor);
		vgameGroup.addGap(50);
		vgameGroup.addComponent(newGame);
		GroupLayout.ParallelGroup vframeGroup = layout.createParallelGroup();
		vframeGroup.addGroup(vscreenGroup);
		vframeGroup.addGroup(vgameGroup);
		layout.setVerticalGroup(vframeGroup);
		
		//mainFrame.add(graphicsSet, BorderLayout.CENTER);

		mainFrame.pack();
		handShapes.translate(c.getX(), c.getY());
		handShapes.loadFlags();
		System.out.println("image loading complete");
		handShapes.setVisible(true);
		handShapes.hideRegion('8');
		mainFrame.setVisible(true);
		
		loadSounds();
		playSound(0);
	}
	
	private void loadSounds() {
		//open the sound files
		File soundFile = new File("spawn.wav");
		Clip clip = null;
		try {
			AudioInputStream chipSound = AudioSystem.getAudioInputStream(soundFile);
			DataLine.Info info = new DataLine.Info(Clip.class, chipSound.getFormat());
			clip = (Clip)AudioSystem.getLine(info);
			clip.open(chipSound);
			clips.add(clip);
			
			Clip clip2;
			File soundFile2 = new File("whoosh.wav");
			AudioInputStream chipSound2 = AudioSystem.getAudioInputStream(soundFile2);
			DataLine.Info info2 = new DataLine.Info(Clip.class, chipSound2.getFormat());
			clip2 = (Clip)AudioSystem.getLine(info2);
			clip2.open(chipSound2);
			clips.add(clip2);
			
			Clip clip3;
			File soundFile3 = new File("show.wav");
			AudioInputStream chipSound3 = AudioSystem.getAudioInputStream(soundFile3);
			DataLine.Info info3 = new DataLine.Info(Clip.class, chipSound3.getFormat());
			clip3 = (Clip)AudioSystem.getLine(info3);
			clip3.open(chipSound3);
			clips.add(clip3);
		} catch(Exception ioE) {
			System.out.println("Error accessing the sound file");
			ioE.printStackTrace();
		}
	}
	
	private void playSound(int s) {
		//play one of the sounds in the dj collection
		/*if(s == 1) {
			//just play the beep
			playButton.getToolkit().beep();
		}*/
		if(s >= 0 && s <= 2) {
			Clip myClip = clips.get(s);
			myClip.setFramePosition(0);
			myClip.start();
		}
	}
		
	
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		if(s.equals("newColours")) {
			//click a place on the screen with the new colour on it
			System.out.println("select the new colour from a place on the webcam footage");
			int chances = 3;
			BufferedImage snapshot = drapeau.getSnapshot();
			while(chances > 0) {
				if(snapshot != null)
					break;
				snapshot = drapeau.getSnapshot();
				chances--;
			}
			EyeDropper pipette = new EyeDropper(snapshot);
			int result = JOptionPane.showConfirmDialog(mainFrame,
					pipette.getComponent(),
					"Colour selection", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
			if(result == JOptionPane.OK_OPTION) {
				int[] colourSettings = pipette.returnSettings();
				drapeau.calibrate(colourSettings[0], colourSettings[1],
						colourSettings[2], colourSettings[3],
						colourSettings[4], colourSettings[5]);
				//now it's possible to configure the ambience
				ambience.setEnabled(true);
			}		
		} else if(s.equals("ambience")) {
			System.out.println("configuring ambience...");
			int chances = 3;
			BufferedImage snapshot = drapeau.getSnapshot();
			//assume that 3 chances are enough
			while(chances > 0) {
				if(snapshot != null)
					break;
				snapshot = drapeau.getSnapshot();
				chances--;
			}
			//now done earlier
			//drapeau.setShapes(handShapes);
			//handShapes.setVisible(true);
			drapeau.ambienceConfig(snapshot);
			//now we can play!
			playButton.setEnabled(true);
		} else if(s.equals("play")) {
			System.out.println("play!");
			playGame();
		} else if(s.equals("sem")) {
			System.out.println("Semaphore button pressed");
			//change the buttons
			if(game == 'n' || game == 'o') {
				//disable naval features
				bIntNaval.setForeground(Color.BLACK);
				handShapes.displayChoices(-1, -1, -1, -1, -1, -1); //take down all flags
			} else if(game == 'm') {
				bMotor.setForeground(Color.BLACK);
			}
			bSemaphore.setForeground(Color.MAGENTA);
			game = 's';
			//handShapes.displayChoices(-1, -1, -1, -1, -1, -1); //take down all flags
		} else if(s.equals("navy")) {
			System.out.println("Navy button pressed");
			if(game == 's') {
				//turn off semaphore features
				bSemaphore.setForeground(Color.BLACK);
			} else if(game == 'm') {
				//turn off motorsport features
				bMotor.setForeground(Color.BLACK);
			}
			game = 'n';
			//change the buttons
			bIntNaval.setForeground(Color.MAGENTA);
		} else if(s.equals("motor")) {
			System.out.println("Motorsport button pressed");
			//change the buttons
			if(game == 's') {
				//turn off semaphore features
				bSemaphore.setForeground(Color.BLACK);
			} else if(game == 'n' || game == 'o') {
				bIntNaval.setForeground(Color.BLACK);
			}
			game = 'm';
			bMotor.setForeground(Color.MAGENTA);
		} else if(s.equals("game")) {
			System.out.println("new game");
			if(game == 'n') {
				game = 'o';
			} else if(game == 'o') {
				game = 'n';
			}
			//System.out.println("showing image");
			//handShapes.displayChoices(0,1,2,3,4,5);
		}
		
	}
	
	private void coupDeColour(int x, int y) {
		//set the new control colour to be the one at x,y
		System.out.println("mouse pressed (" + x + "," + y + ")");
	}
	
	private void playGame() {
		/*int chances = 3;
		BufferedImage snapshot = drapeau.getSnapshot();
		while(chances > 0) {
			if(snapshot != null)
				break;
			snapshot = drapeau.getSnapshot();
			chances--;
		}
		//check if the flag is in region 3
		boolean isCorrect = drapeau.checkRegion('3', snapshot);
		System.out.println("bottom-left hand flag: " + isCorrect);
		isCorrect = drapeau.checkRegion('7', snapshot);
		System.out.println("top-right hand flag: " + isCorrect);*/
		//start the run method
		if(!started) {
			this.start();
			started = true;
		}
		//else do nothing
	}
	
	public void run() {
		//this will go continuously and hopefully still allow button presses
		char answer1 = '7';
		char answer2 = '1';	//chars take up less space, that's why I send them around
		int answerWait = 0;
		//ReentrantLock correctLock = new ReentrantLock();
		while(keepGoing) {
			try {
				int chances = 3;
				BufferedImage snapshot = drapeau.getSnapshot();
				while(chances > 0) {
					if(snapshot != null)
						break;
					snapshot = drapeau.getSnapshot();
					chances--;
				}
				boolean isCorrect;
				if(game == 's') {
					isCorrect = (drapeau.checkRegion(answer1, snapshot) && drapeau.checkRegion(answer2, snapshot));
				} else {
					//motorsport or naval
					isCorrect = drapeau.checkRegion(answer1, snapshot);
				}
				if(isCorrect) {
					//correctLock.lock();
					System.out.println("correct!");
					playSound(1);
					//hide regions
					handShapes.hideRegion('8');
					//new question
					if(game == 's') {
						handShapes.shiftLeft();
						char[] answers = handShapes.semAnsRegions();
						answer1 = answers[0];
						answer2 = answers[1];
					} else if(game == 'n') {
						//international naval with short names
						answer1 = handShapes.hoistFlag(false);
					} else if(game == 'o') {
						//international naval with long names
						answer1 = handShapes.hoistFlag(true);
					} else {
						//game = 'm'
						answer1 = handShapes.waveFlag();
					}
					//correctLock.unlock();
					Thread.sleep(400);
					//ignore immediate answers; in multi choice it's just luck and
					//in semaphore, it's too fast for others to read
				} else {
					//correctLock.lock();
					answerWait++;
					System.out.println("zzz");
					if(answerWait >= surrenderTimer) {
						//show the regions
						System.out.println("argh, come on");
						playSound(2);
						handShapes.showRegion(answer1);
						if(game == 's')
							handShapes.showRegion(answer2);
						//beep
						answerWait = 0;
					}
					//correctLock.unlock();
				}
				Thread.sleep(500);
			} catch(InterruptedException e) {
				//do something
			}
		}
	}
	
}
