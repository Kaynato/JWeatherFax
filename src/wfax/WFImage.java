package wfax;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class WFImage extends BufferedImage {
	
	private WFPanel owner;

	public WFImage(WFPanel owner, int width, int height) {
		super(width, height, TYPE_INT_RGB);
		this.owner = owner;
		clear();
	}
	
	public void setPixel(int x, int y, int value) {
		if (x >= getWidth())
			x %= getWidth();
		while (x < 0)
			x += getWidth();
		
		if (y >= getHeight())
			y %= getHeight();
		while (y < 0)
			y += getHeight();
		
		
		super.setRGB(x, y, (value << 16) | (value << 8) | value);
		
		owner.repaint();
		
//		System.out.println("Write to ("+x+", "+y+")");
	}
	
	public void setRGB(int x, int y, int rgb) {
		if (x >= getWidth())
			x %= getWidth();
		while (x < 0)
			x += getWidth();
		
		if (y >= getHeight())
			y %= getHeight();
		while (y < 0)
			y += getHeight();
		
		
		super.setRGB(x, y, rgb);
		
		owner.repaint();
		
//		System.out.println("Write to ("+x+", "+y+")");
	}
	
	// Get greyscale
	public int getPixel(int x, int y) {
		if (x >= getWidth())
			x %= getWidth();
		while (x < 0)
			x += getWidth();
		
		if (y >= getHeight())
			y %= getHeight();
		while (y < 0)
			y += getHeight();
		
		int value = getRGB(x, y);
		int r = 0xFF & value >> 16;
		int g = 0xFF & value >> 8;
		int b = 0xFF & value;
		return (r+g+b)/3;
	}
	
	public void clear() {
		Graphics2D g = createGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, getWidth(), getHeight());
		g.dispose();
		
		owner.repaint();
	}
	
	public void rect(int x, int y, int width, int height, Color color) {
		Graphics2D g = createGraphics();
		g.setColor(color);
		g.drawRect(x, y, width, height);
		g.dispose();
		
		owner.repaint();
	}

}
