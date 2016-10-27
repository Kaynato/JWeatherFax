package wfax;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class WFPanel extends JPanel {

	// Nonsense.
	private static final long serialVersionUID = -2976431365107594560L;
	
	private WFImage image;
	private WFController wfcontroller;
	private WFWindow window;
	
	public WFPanel(int width, int height, WFWindow window) {
		this.window = window;
		
		setSize(width, height);
		setPreferredSize(getSize());
		
		image = new WFImage(this, Globals.I_WIDTH, Globals.I_HEIGHT);
		setBackground(Color.WHITE);
		wfcontroller = new WFController(this);
	}
	
	public void resizeImage(int width, int height) {
		image = new WFImage(this, width, height);
		
//		window.getFrame().pack();
	}

	@Override
	public void paintComponent(Graphics g) {
		
		int iw = image.getWidth();
		int ih = image.getHeight();
		BufferedImage after = null;

		if (Globals.RESIZE_IMAGE && (iw != getWidth() || ih != getHeight())) {
			after = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
			AffineTransform at = new AffineTransform();

			double xScale = (double)getWidth()/(double)iw;
			double yScale = (double)getHeight()/(double)ih;

			at.scale(xScale, yScale);
			AffineTransformOp scaleOp = 
					new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
			after = scaleOp.filter(image, after);
		}
		else
			after = image;
		
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(after, 0, 0, this);
		
		Toolkit.getDefaultToolkit().sync();
	}
	
	public WFImage image() {
		return image;
	}
	
	public void poll() {
		
	}
	
	public WFWindow window() {
		return window;
	}
	
	
	public WFController getController() {
		return wfcontroller;
	}
	

}
