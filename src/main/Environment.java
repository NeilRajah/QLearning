/**
 * Environment
 * Author: Neil Balaskandarajah
 * Created on: 27/11/2020
 * Environment where learning takes place
 */

package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Environment {
	//Constants
	private final int NUM_ACTIONS = 4;				//Number of actions the agent can take
	private final int OBSTACLE_REWARD = -100;		//Punishment for hitting obstacle
	private final int PATH_REWARD = -1;				//Reward for moving along path
	private final int GOAL_REWARD = 100;			//Reward for reaching goal
	
	//Actions agent can take
	private enum ACTIONS {
		LEFT,
		RIGHT,
		DOWN,
		UP
	}
	
	//Attributes
	private int rows;								//Rows in the grid
	private int cols; 								//Columns in the grid
	private int[][][] q_vals;						//Q-values (state, action) pairs
	private int[][] rewards;						//Rewards (same dimension as grid)
	
	/**
	 * Create an Environment from a file
	 * @param filename Name of the file containing the Environment
	 */
	public Environment(String filename) {
		try {
			Scanner s = new Scanner(new File(filename));
			
			//Add lines from file to list
			ArrayList<String> lines = new ArrayList<String>();
			while (s.hasNextLine())
				lines.add(s.nextLine());
			
			//Set size of the Environment
			this.rows = lines.get(0).length();
			this.cols = lines.size();
			
			//Create rewards
			final char GOAL = 'g';
			final char OBSTACLE = '#';
			final char PATH = '.';
			this.rewards = new int[rows][cols];
			
			//Add rewards for the goal and the obstacles
			int x = 0;
			for (String line : lines) {
				for (int y = 0; y < line.length(); y++) {
					char symbol = line.charAt(y);
					
					if (symbol == GOAL)
						rewards[x][y] = GOAL_REWARD;
					else if (symbol == OBSTACLE)
						rewards[x][y] = OBSTACLE_REWARD;
					else if (symbol == PATH)
						rewards[x][y] = PATH_REWARD;
				}
				x++;
			}
			
			s.close();
			
		//Could not find file
		} catch (FileNotFoundException e) {
			System.out.println(filename + " was not found!");
		}
	}
	
	/**
	 * Create an Environment with numeric values
	 * @param rows Rows in the grid
	 * @param cols Columns in the grid
	 * @param goal Goal (x,y)
	 * @param obstacles Obstacles in the grid
	 */
	public Environment(int rows, int cols, int[] goal, int[][] obstacles) {
		//Set size attributes
		this.rows = rows;
		this.cols = cols;
		
		/*
		 * 3D array of Q-values Q(s,a)
		 * x-dimension: 11 horizontal grid spots
		 * y-dimension: 11 vertical grid spots
		 * action dimension: 4 moves
		 * Initial values are zero
		 */
		q_vals = new int[rows][cols][NUM_ACTIONS];
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++)
				for (int a = 0; a < NUM_ACTIONS; a++)
					q_vals[r][c][a] = 0;
		
		//Add the rewards
		createRewards(goal, obstacles);
	}
	
	/**
	 * Create the rewards based on the list of goals and obstacles
	 */
	private void createRewards(int[] goal, int[][] obstacles) {
		//Create rewards and fill with base value
		rewards = new int[rows][cols];
		for (int x = 0; x < rows; x++)
			for (int y = 0; y < cols; y++)
				rewards[x][y] = PATH_REWARD;
		
		//Add goal reward
		rewards[goal[0]][goal[1]] = GOAL_REWARD;
		
		//Add obstacle rewards
		for (int o = 0; o < obstacles.length; o++) {
			int[] obst = obstacles[o];
			rewards[obst[0]][obst[1]] = OBSTACLE_REWARD;
		}
	}
	
	/**
	 * Get the rewards
	 * @return 2D array of rewards, indexed by their (x,y) positions
	 */
	public int[][] getRewards() {
		return rewards;
	}
	
	//Training
	
	/**
	 * Train the model
	 * 1. Choose random non-terminal state
	 * 2. Choose action using epsilon greedy algorithm
	 * 3. Perform chosen action and transition to next state
	 * 4. Receive reward for moving to new state, calculate temporal difference
	 * 5. Update Q-value for previous state-action pair
	 * 6. If current state is terminal, goto 1, else 2
	 */
	public void train() {
		
	}
}