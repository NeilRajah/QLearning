/**
 * environment
 * Author: Neil Balaskandarajah
 * Created on: 27/11/2020
 * environment where learning takes place
 */

package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Model {
	//Constants
	private final int NUM_ACTIONS = 4;						//Number of actions the agent can take
	private final int OBSTACLE_REWARD = -100;				//Punishment for hitting obstacle
	private final int PATH_REWARD = -1;						//Reward for moving along path
	private final int GOAL_REWARD = 100;					//Reward for reaching goal
	private final int[] ERROR_STATE = new int[] {-1, -1};	//Error state
	
	//Actions agent can take
	private enum ACTION {
		LEFT,
		RIGHT,
		DOWN,
		UP
	}
	public static ACTION[] ACTIONS = ACTION.values();
	
	//Attributes
	private int rows;								//Rows in the grid
	private int cols; 								//Columns in the grid
	private int[][][] q_vals;						//Q-values (state, action) pairs
	private int[][] rewards;						//Rewards (same dimension as grid)
	private ArrayList<int[]> nonTerminalStates;		//Safe states for the agent to be in
	private int numEpisodes;						//Number of episodes to simulate
	private double epsilon = 0.9;					//How often the Agent wants to choose a random move to explore its environment
	private double discount_factor = 0.9;			//How much to discount future rewards
	private double learning_rate = 0.9;				//The rate the agent should learn at
	
	/**
	 * Create a Model from a file
	 * @param filename Name of the file containing the environment
	 */
	public Model(String filename) {
		try {
			Scanner s = new Scanner(new File(filename));
			
			ArrayList<String> lines = new ArrayList<String>();
			//Get the number of episodes (first line)
			numEpisodes = Integer.parseInt(s.nextLine());
			
			//Add lines from file to list
			while (s.hasNextLine())
				lines.add(s.nextLine());
			
			//Set size of the environment
			this.rows = lines.get(0).length();
			this.cols = lines.size();
			
			//Create rewards
			final char GOAL = 'g';
			final char OBSTACLE = '#';
			final char PATH = '.';
			this.rewards = new int[rows][cols];
			this.nonTerminalStates = new ArrayList<int[]>();
			
			//Add rewards for the goal and the obstacles
			int x = 0;
			for (String line : lines) {
				for (int y = 0; y < line.length(); y++) {
					char symbol = line.charAt(y);
					
					//Assign reward values based on symbol
					if (symbol == GOAL)
						rewards[x][y] = GOAL_REWARD;
					else if (symbol == OBSTACLE)
						rewards[x][y] = OBSTACLE_REWARD;
					else if (symbol == PATH) {
						rewards[x][y] = PATH_REWARD;
						nonTerminalStates.add(new int[] {x,y});
					}
				}
				x++;
			}
			
			s.close();
			
		//Could not find file
		} catch (FileNotFoundException fnf) {
			Util.println("Model:", filename, "was not found!");
		} catch (NumberFormatException nf) {
			Util.println("Model: First line of file must be number of episodes to simulate!");
		}
	}
	
	/**
	 * Create a Model with numeric values
	 * @param rows Rows in the grid
	 * @param cols Columns in the grid
	 * @param goal Goal (x,y)
	 * @param obstacles Obstacles in the grid
	 */
	public Model(int rows, int cols, int[] goal, int[][] obstacles) {
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
	 * Return whether a position on the grid is a terminal state (one where the episode ends if landed on)
	 * @param row Row of the position
	 * @param col Column of the position
	 * @return Whether the reward at (row, col) is not the default reward
	 */
	private boolean isTerminalState(int row, int col) {
		return rewards[row][col] != PATH_REWARD;
	}
	
	/**
	 * Get the starting location for the episode
	 * @return Random non-terminal spot in grid
	 */
	private int[] getStartingLocation() {
		return nonTerminalStates.get(Util.randInt(nonTerminalStates.size()-1));
	}
	
	/**
	 * Choose the next action using the greedy epsilon algorithm
	 * @param row Row the agent is at
	 * @param col Column the agent is at
	 * @param epsilon Determines whether the agent is choosing best option or exploring
	 * @return Next action for the agent to take
	 */
	private ACTION getNextAction(int row, int col, double epsilon) {
		//There is an (epsilon * 100) % chance that the agent chooses the best move
		if (Math.random() < epsilon)
			return ACTIONS[Util.max(q_vals[row][col])];
		
		//In other cases, it chooses a random move. This encourages it to explore its environment
		return ACTIONS[Util.randInt(ACTIONS.length)];
	}
	
	/**
	 * Get the next location based on the action
	 * @param row Row the agent is at
	 * @param col Column the agent is at
	 * @param action Action the agent is taking
	 * @return New (x,y) position for the agent
	 */
	private int[] getNextLocation(int row, int col, ACTION action) {
		switch (action) {
			case UP:
				return new int[] {Math.max(row-1, 0), col};
			case DOWN:
				return new int[] {Math.min(row+1, this.rows), col};
			case LEFT:
				return new int[] {row, Math.max(col-1, 0)};
			case RIGHT:
				return new int[] {row, Math.min(col+1, this.cols)};
			default:
				return ERROR_STATE;
		}
	}
	
	/**
	 * Get the shortest path between any location and the goal
	 * @param startRow Row to begin searching from
	 * @param startCol Column to begin searching from
	 * @return Shortest path defined by the (x,y) coordinates of each point along it
	 */
	private ArrayList<int[]> getShortestPath(int startRow, int startCol) {
		if (isTerminalState(startRow, startCol)) {
			return null;
		} else {
			//Create the shortest path list, starting at the start position
			ArrayList<int[]> shortestPath = new ArrayList<int[]>();
			int[] currentPos = new int[] {startRow, startCol};
			shortestPath.add(currentPos);
			
			//Path always ends on a terminal state
			while (!isTerminalState(currentPos[0], currentPos[1])) {
				ACTION action = getNextAction(currentPos[0], currentPos[1], 1.0); //Choose the best action
				
				//Move to the next location on the path and add it to the list
				currentPos = getNextLocation(currentPos[0], currentPos[1], action);
				shortestPath.add(currentPos);
			}
			return shortestPath;
		}
	}
	
	/**
	 * Train the model
	 * 1. Choose random non-terminal state to start at
	 * 2. Choose action using epsilon greedy algorithm
	 * 3. Perform chosen action and transition to the next state
	 * 4. Receive reward for moving to new state, calculate temporal difference
	 * 5. Update Q-value of previous state-action pair
	 * 6. If current state is terminal, start new episode, else repeat from 2
	 */
	public void train() {
		for (int episode = 0; episode < numEpisodes; episode++) {
			int[] pos = getStartingLocation();
			int[] prevPos;
			
			while (!isTerminalState(pos[0], pos[1])) {
				//Choose which action to take
				ACTION action = getNextAction(pos[0], pos[1], epsilon);
				// *** grow epsilon over time to explore early, choose best later ***
				
				//Perform action, transition to next state
				prevPos = pos;
				pos = getNextLocation(pos[0], pos[1], action);
				
				/*
				 * To-Do
				 * 4, 5, 6
				 * Temporal Difference helper method
				 * Start graphics
				 */
			}
		}
	}
}