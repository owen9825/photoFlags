
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;


public class Entrance implements ActionListener {
	JFrame welcomeFrame = new JFrame("Welcome");
	
	Button okBtn = new Button("OK");
	Label label1a = new Label("Welcome to PhotoFlags 1.0");
	Label label1b = new Label("Written in 2010 by Owen Miller");
		
	public Entrance() {
		Container contentPane = welcomeFrame.getContentPane();
		BoxLayout layout = new BoxLayout(contentPane, BoxLayout.Y_AXIS);
		contentPane.setLayout(layout);
		
		welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		welcomeFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				welcomeFrame.setVisible(false);
				welcomeFrame.dispose();
				System.exit(0);
			}
		});
		
		welcomeFrame.setSize(400,250);
		welcomeFrame.add(label1a, BorderLayout.NORTH);
		welcomeFrame.add(label1b, BorderLayout.NORTH);
		welcomeFrame.add(okBtn, BorderLayout.SOUTH);
		okBtn.addActionListener(this);
		okBtn.setActionCommand("okBtn");
		
	}
	
	public void setVisible(boolean state) {
		welcomeFrame.setVisible(state);
	}
	
	public void actionPerformed(ActionEvent e) {	
		String s = e.getActionCommand();
		if(s.equals("okBtn")) {
			//mainFrame.setVisible(false);
			//do something
			System.out.println("Right this way captain");
			//welcomeFrame.dispose();
			guiControl overlord = new guiControl();
			overlord.createAndShowGUI();
			welcomeFrame.dispose();
			
		}
		//mainFrame.dispose();
	}
	
	public static void main(String[] args){
		//weighted interval scheduling, written in 2009
		//by Owen Miller
		System.out.println("Welcome!");
		Entrance mainWindow = new Entrance();
		mainWindow.setVisible(true);
		//mainWindow.overlord = new guiControl();

	}
}