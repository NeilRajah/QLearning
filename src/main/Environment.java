/**
 * Environment
 * Author: Neil Balaskandarajah
 * Created on: 29/11/2020
 * Environment where learning occurs
 */

package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class Environment extends JComponent {	
	//Attributes
	private int rows;
	private int cols;
	private int pixelsPerCell;
	private int width;
	private int height;
	private int line;
	
	public Environment(int rows, int cols, int pixelsPerCell) {
		//Set attributes
		this.rows = rows;
		this.cols = cols;
		this.pixelsPerCell = pixelsPerCell;
		this.width = pixelsPerCell * rows;
		this.height = pixelsPerCell * cols;
		this.line = pixelsPerCell/20;
		
		this.setPreferredSize(new Dimension(width+line, height+line));
	}
	
	/**
	 * Get the width of the Environment
	 * @return Width of the Environment in pixels
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Get the height of the Environment
	 * @return Height of the Environment in pixels
	 */
	public int getHeight() {
		return height;
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		//Background
		g2.setColor(Color.decode("#12E772"));
		g2.fillRect(0, 0, width, height);
		
		//Grid Lines
		/*
		g2.setColor(Color.BLACK);
		g2.fillRect(line, line, width-line, height-line);
		g2.setStroke(new BasicStroke(line));
		g2.setColor(Color.WHITE);
		for (int x = 0; x < rows; x++)
			g2.drawLine(x*pixelsPerCell, 0, x*pixelsPerCell, height);
		
		for (int y = 0; y < cols; y++)
			g2.drawLine(0, y*pixelsPerCell, width, y*pixelsPerCell);
		*/
		
		int cell = pixelsPerCell-4*line;
		int corner = cell/10;
		g2.setColor(Color.decode("#00563F"));
		int x = 2*line;
		int y;
		
		for (int row = 0; row < rows; row++) {
			y = 2*line;
			for (int col = 0; col < cols; col++) {
				g2.fillRoundRect(x, y, cell, cell, corner, corner);
				Util.println(x,y);
				y += pixelsPerCell;
			}
			x += pixelsPerCell;
		}
			
	}
}