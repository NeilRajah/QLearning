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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class Environment extends JComponent {	
	//Constants
	private final String BACKGROUND_IMAGE = "src/main/bg.png";
	private final Color PATH_COLOR = Color.decode("#cabc91");
	private final Color OBSTACLE_COLOR = Color.decode("#a69150");
	private final Color GOAL_COLOR = Color.decode("#5eb173");
	private final Color AGENT_COLOR = Color.decode("#c28090");
	private final Color TEXT_COLOR = Color.decode("#a66650");
	
	//Attributes
	private Model model;
	private int rows;
	private int cols;
	private int pixelsPerCell;
	private int agentWidth;
	private int width;
	private int height;
	private int line;
	private BufferedImage bg;
	private Window window;
	private ArrayList<int[]> path;
	
	public Environment(Model model, int pixelsPerCell) {
		//Set attributes
		this.model = model;
		this.rows = model.getRows();
		this.cols = model.getCols();
		this.pixelsPerCell = pixelsPerCell;
		this.width = pixelsPerCell * rows;
		this.height = pixelsPerCell * cols;
		this.line = pixelsPerCell / 10;
		this.agentWidth = pixelsPerCell / 2;
		this.path = new ArrayList<int[]>();
		
		this.setPreferredSize(new Dimension(width, height));
		
		setUpBackground();
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
	
	public void setWindow(Window w) {
		this.window = w;
	}
	
	/**
	 * Read the background or draw it if it cannot be read
	 */
	private void setUpBackground() {
		try {
			bg = ImageIO.read(new File(BACKGROUND_IMAGE));
			Util.println("Opened background image from file");
			
		} catch (IOException e) {
			bg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = bg.createGraphics();
			g2.setStroke(new BasicStroke(1));
			
			//Draw squares with color based on the corresponding reward
			int[][] rewards = model.getRewards();
			for (int x = 0; x < rewards.length; x++) {
				for (int y = 0; y < rewards[x].length; y++) {
					switch(rewards[y][x]) {
						case (Model.GOAL_REWARD):
							g2.setColor(GOAL_COLOR);
							break;
						case (Model.PATH_REWARD):
							g2.setColor(PATH_COLOR);
							break;
						case (Model.OBSTACLE_REWARD):
							g2.setColor(OBSTACLE_COLOR);
							break;
						default:
							g2.setColor(Color.PINK);
					}
					g2.fillRect(x*pixelsPerCell, y*pixelsPerCell, pixelsPerCell, pixelsPerCell);
					// *** draw rewards as well? ***
				}
			}				
			
			//Draw lines to separate the grid squares
			g2.setColor(OBSTACLE_COLOR);
			for (int x = 0; x <= rewards.length; x++) 
				g2.drawLine(0, x * pixelsPerCell, width, x * pixelsPerCell);
			for (int y = 0; y <= rewards[0].length; y++)
				g2.drawLine(y * pixelsPerCell, 0, y * pixelsPerCell, height);				
		}
	}

	/**
	 * Update the Environment based on the model and sleep
	 * @param episode 
	 */
	public void update(int episode) {
		repaint();
		window.setTitle("Q-Learning: Episode " + episode);
		Util.sleep(10);
	}
	
	/**
	 * Draw the Environment
	 * @param g Drawing object
	 */
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		//Draw background
		g2.drawImage(bg, 0, 0, width, height, null);
		
		//Draw Agent
		g2.setColor(AGENT_COLOR);
		int agentX = (int) ((model.getAgentX() + 0.5) * pixelsPerCell - agentWidth/2);
		int agentY = (int) ((model.getAgentY() + 0.5) * pixelsPerCell - agentWidth/2);
		g2.fillOval(agentX, agentY, agentWidth, agentWidth);
		
		//Draw q-values
		g2.setColor(TEXT_COLOR);
		g2.setFont(g2.getFont().deriveFont(pixelsPerCell * 0.25f));
		FontMetrics fm = g2.getFontMetrics();
		
		//Convert from double to string and draw the Q-value centered in box
		double[][][] qValues = model.getQValues();
		for (int x = 0; x < qValues.length; x++) {
			for (int y = 0; y < qValues[x].length; y++) {
				String qVal = String.format("%.3f", Util.max(qValues[x][y]));
				Rectangle2D strBounds = fm.getStringBounds(qVal, g2);
				int strX = (int) ((x + 0.5) * pixelsPerCell - strBounds.getWidth() / 2);
				int strY = (int) ((y + 0.5) * pixelsPerCell + strBounds.getHeight() / 2);
				g2.drawString(qVal, strX, strY);
			}
		}
		
		//If there is a path, draw it
		if (path != null && path.size() > 0)
			drawPath(g2);
	}
	
	/**
	 * Draw the path, if it exists
	 * @param g2 Drawing object
	 */
	private void drawPath(Graphics2D g2) {
		//Configure line
		g2.setColor(Color.RED);
		Stroke s = new BasicStroke(pixelsPerCell/10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		g2.setStroke(s);
		
		//Draw a line from one spot to the next
		int[] start = Util.scaleArray(path.get(0), pixelsPerCell);
		int offset = pixelsPerCell/2;
		int prevX = start[1] + offset;
		int prevY = start[0] + offset;
		int START_SIZE = pixelsPerCell/5;
		g2.fillOval(prevX - START_SIZE/2, prevY - START_SIZE/2, START_SIZE, START_SIZE);
		
		for (int i = 1; i < path.size(); i++) {
			int[] pos = Util.scaleArray(path.get(i), pixelsPerCell);
			int x = pos[1] + offset;
			int y = pos[0] + offset;
			
			g2.drawLine(prevX, prevY, x, y);
			
			prevX = x;
			prevY = y;
		}
	}
	
	/**
	 * Set the path from a filename
	 * @param pathFilename Name of the File containing the path
	 */
	public void setPath(String pathFilename) {
		//Read the file
		try {
			Scanner s = new Scanner(new File(pathFilename));
			
			//Convert each line to the int[] with the position
			while (s.hasNextLine()) {
				String[] line = s.nextLine().split(" ");
				path.add(new int[] {Integer.parseInt(line[0]), Integer.parseInt(line[1])});
			}
			
			s.close();
			Util.println("Shortest path is", this.path.size()-1, "steps long");
			
		//If the file could not be found
		} catch (FileNotFoundException fnf) {
			Util.println("Could not find", pathFilename);
		}
	}
	
	/**
	 * Set the path to draw
	 * @param path Path to draw
	 */
	public void setPath(ArrayList<int[]> path) {
		this.path = path;
		Util.println("Shortest path is", this.path.size()-1, "steps long");
	}
	
	/*
	 * TBD
	 */
	public void showMousePath() {
		//after training, show path from spot mouse is over to the goal
	}
}