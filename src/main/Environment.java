/**
 * Environment
 * Author: Neil Balaskandarajah
 * Created on: 27/11/2020
 * Environment where learning takes place
 */

package main;

public class Environment {
	//Constants
	private final int NUM_ACTIONS = 4;
	private enum ACTIONS {
		LEFT,
		RIGHT,
		DOWN,
		UP
	}
	
	//Attributes
	private int rows;
	private int cols; 
	private int[][][] q_vals;
	
	public Environment(String filename) {
		
	}
	
	public Environment(int rows, int cols, int[] start, int[] goal, int[][] obstacles) {
		this.rows = rows;
		this.cols = cols;
		
		/*
		 * 3D array of Q-values Q(s,a)
		 * x-dimension: 11 horizontal grid spots
		 * y-dimension: 11 vertical grid spots
		 * action dimension: 4 moves
		 */
		q_vals = new int[rows][cols][NUM_ACTIONS];
		
		//Add the rewards
		createRewards(start, goal, obstacles);
	}
	
	private void createRewards(int[] start, int[] goal, int[][] obstacles) {
		
	}
}