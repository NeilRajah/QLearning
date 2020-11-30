/**
 * Window
 * Author: Neil Balaskandarajah
 * Created on: 27/11/2020
 * Window with simulation
 */

package main;

import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window extends JFrame {
	//Attributes
	//Q-Learning
	private Model m;
	private int pixelsPerCell;			//Side length of a cell
	
	//Graphics
	private JPanel panel;
	private Environment env;
	
	public Window(Model m, int pixelsPerCell) {
		//Set attributes
		this.m = m;
		this.pixelsPerCell = pixelsPerCell;
		
		layoutComponents();
	}
	
	private void layoutComponents() {
		//Main panel
		this.panel = new JPanel();
		
		//Environment where learning takes place
		env = new Environment(m, pixelsPerCell);
		m.setEnvironment(env);
		env.setWindow(this);
		panel.add(env);
		panel.setBackground(Color.WHITE);
	}
	
	public void launch() {
		int w = env.getWidth();
		int h = env.getHeight();
		int sysH = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		
		//Set up the JFrame
		this.setTitle("Q-Learning");
		this.setContentPane(panel);
		this.setUndecorated(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(w/2, (sysH-h)/2, w, h);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
	}
	
	public void setPath(String pathFilename) {
		env.setPath(pathFilename);
	}
}
