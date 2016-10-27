package wfax;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class WFWindow {

	private WFPanel panel;
	
	private JFrame frame;

	public WFWindow() {
		frame = new JFrame("Weatherfacimile Decoder");

		panel = new WFPanel(Globals.W_WIDTH, Globals.W_HEIGHT, this);

		if (Globals.DO_WINDOW) {
			frame.getContentPane().add(panel);

			// Center
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			panel.image().clear();
		}
	}

	public WFPanel getPanel() {
		return panel;
	}
	
	public JFrame getFrame() {
		return frame;
	}

}
