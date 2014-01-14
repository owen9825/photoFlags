
import java.awt.*;
import java.awt.geom.*;
import javax.swing.JPanel;


public class ShapePanel extends JPanel {
	//this panel is to draw the shapes and flags
	
	RoundRectangle2D topLeft = new RoundRectangle2D.Double();
	RoundRectangle2D topRight = new RoundRectangle2D.Double();
	Area tL = new Area(topLeft);
	Area tR = new Area(topRight);
	
	public ShapePanel() {
		//
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Dimension d = getSize();
		int w = d.width;
		int h = d.height;
		
		topLeft.setFrame(2.0, 2.0, 60.0, 60.0);
		topRight.setFrame(4.0, 60.0, 60.0, 30.0);
		tL = new Area(topLeft);
		tR = new Area(topRight);
		
		AlphaComposite translucent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
		
		g2.setColor(Color.BLUE);
		g2.setComposite(translucent);
		g2.fill(tL);
		g2.setColor(Color.RED);
		g2.fill(tR);
		setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		
	}


}
