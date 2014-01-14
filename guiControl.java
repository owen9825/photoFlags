import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.*;

public class guiControl implements ActionListener {
	//this class is in control of the graphics, the buttons, the universe
	
	JFrame mainFrame = new JFrame("PhotoFlags 1.0");
	FlagWatch drapeau = null;
	
	JButton colourSet = new JButton("Set colour");
	JButton testFeature = new JButton("test feature");
	//JLabel smallScreen = new JLabel();
	//SmallScreen altDisp = null;
	
	BufferedImage lastPhoto = null;
	Color flagColour = null;
	
	public guiControl() {
		Container contentPane = mainFrame.getContentPane();
		BoxLayout layout = new BoxLayout(contentPane, BoxLayout.Y_AXIS);
		contentPane.setLayout(layout);
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				mainFrame.setVisible(false);
				mainFrame.dispose();
				System.exit(0);
			}
		});
		mainFrame.setSize(400,250);
		
		mainFrame.add(colourSet, BorderLayout.SOUTH);
		//mainFrame.add(testFeature, BorderLayout.NORTH);
		colourSet.addActionListener(this);
		colourSet.setActionCommand("newColours");
		testFeature.addActionListener(this);
		testFeature.setActionCommand("test");
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
		drapeau = new FlagWatch();
		JPanel webcam = drapeau.getDisplay();
		//Graphics g = webcam.getGraphics();
		Component c = webcam.getComponent(0);
		//temp
		/*Graphics g = c.getGraphics();
		Graphics2D g2 = (Graphics2D) g;
		Ellipse2D.Double circle = new Ellipse2D.Double();
		circle.setFrame(25, 25, 50.0, 50.0);
		g2.setColor(Color.GREEN);
		Area circ = new Area(circle);
		g2.fill(circ);*/
		//end temp
		c.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				screen2(drapeau.getSnapshot());				
				coupDeColour(x, y);
				//screen2(lastPhoto);
			}
		});
		//temp
		JPanel graphicsSet = new JPanel();
		graphicsSet.add(c);
		graphicsSet.setPreferredSize(c.getPreferredSize());
		Pear fruitPanel = new Pear();
		mainFrame.setGlassPane(fruitPanel);
		//fruitPanel.setVisible(true);
		//Graphics2D g2 = (Graphics2D)graphicsSet.getGraphics();
		
		/*Ellipse2D.Double circle = new Ellipse2D.Double();
		circle.setFrame(25, 25, 50.0, 50.0);
		g2.setPaint(Color.GREEN);
		Area circ = new Area(circle);
		g2.fill(circ);*/
		
		Color flagColour = new Color(246, 68, 92);
		drapeau.setColour(flagColour);
		mainFrame.add(graphicsSet, BorderLayout.CENTER);
		//mainFrame.add(c, BorderLayout.CENTER);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
	/*public void createAndShowGUI() {
		//mainFrame.setVisible(true);
		drapeau = new FlagWatch();
		JPanel webcam = drapeau.getDisplay();
		//Graphics g = webcam.getGraphics();
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(webcam.getPreferredSize());
		layeredPane.setLayout(new FlowLayout());
		layeredPane.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				screen2(drapeau.getSnapshot());				
				coupDeColour(x, y);
				//screen2(lastPhoto);
			}
		});
		Color flagColour = new Color(246, 68, 92);
		drapeau.setColour(flagColour);
		//layeredPane.add(webcam, JLayeredPane.DEFAULT_LAYER);
		ShapePanel rectPanel = new ShapePanel();
		//rectPanel.setOpaque(false);
		Pear fruitPanel = new Pear();
		layeredPane.add(rectPanel, JLayeredPane.PALETTE_LAYER);
		layeredPane.add(fruitPanel, JLayeredPane.MODAL_LAYER);
		mainFrame.add(layeredPane, BorderLayout.CENTER);
		//Mast jollyRodger = new Mast("nz-naval-flag");
		//mainFrame.add(jollyRodger, BorderLayout.SOUTH);
		//mainFrame.add(fruitPanel, BorderLayout.PAGE_END);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}*/
	
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		if(s.equals("newColours")) {
			//click a place on the screen with the new colour on it
			System.out.println("select the new colour from a place on the webcam footage");
		} else if(s.equals("test")) {
			System.out.println("testing..");
		}
	}
	
	private void coupDeColour(int x, int y) {
		//set the new control colour to be the one at x,y
		System.out.println("mouse pressed (" + x + "," + y + ")");
	}
	
}
