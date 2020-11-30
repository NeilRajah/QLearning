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
	public static final int OBSTACLE_REWARD = -100;			//Punishment for hitting obstacle
	public static final int PATH_REWARD = -1;				//Reward for moving along path
	public static final int GOAL_REWARD = 100;				//Reward for reaching goal
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
	private double[][][] qValues;					//Q-values (state, action) pairs
	private int[][] rewards;						//Rewards (same dimension as grid)
	private ArrayList<int[]> nonTerminalStates;		//Safe states for the agent to be in
	private int numEpisodes;						//Number of episodes to simulate
	private int agentX, agentY;						//X and Y positions of agent
	private double epsilon = 0.9;					//How often the Agent wants to choose a random move to explore its environment
	private double discountFactor = 0.9;			//How much to discount future rewards
	private double learningRate = 0.9;				//The rate the agent should learn at
	private Environment env;						//Environment to update when live training
	
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
			
			/*
			 * 3D array of Q-values Q(s,a)
			 * x-dimension: 11 horizontal grid spots
			 * y-dimension: 11 vertical grid spots
			 * action dimension: 4 moves
			 * Initial values are zero
			 */
			qValues = new double[rows][cols][NUM_ACTIONS];
			for (int r = 0; r < rows; r++)
				for (int c = 0; c < cols; c++)
					for (int a = 0; a < NUM_ACTIONS; a++)
						qValues[r][c][a] = 0;
			
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
		qValues = new double[rows][cols][NUM_ACTIONS];
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++)
				for (int a = 0; a < NUM_ACTIONS; a++)
					qValues[r][c][a] = 0;
		
		//Add the rewards
		createRewards(goal, obstacles);
	}
	
	//Attributes
	
	/**
	 * Get the rows in the Model
	 * @return Number of rows in the Model
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Get the cols in the Model
	 * @return Number of rows in the Model
	 */
	public int getCols() {
		return cols;
	}
	
	/**
	 * Get the Q-values in the grid
	 * @return Grid's Q-values
	 */
	public double[][][] getQValues() {
		return qValues;
	}
	
	/**
	 * Get the rewards
	 * @return 2D array of rewards, indexed by their (x,y) positions
	 */
	public int[][] getRewards() {
		return rewards;
	}

	/**
	 * Get the X position of the agent
	 * @return X position of agent on grid
	 */
	public int getAgentX() {
		return agentX;
	}
	
	/**
	 * Get the Y position of the agent
	 * @return Y position of agent on grid
	 */
	public int getAgentY() {
		return agentY;
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
	
	public void setEnvironment(Environment env) {
		this.env = env;
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
		/*
		 * There is an (epsilon * 100) % chance that the agent chooses the best move. In all other cases,
		 * the agent chooses a random move. This encourages it to explore its environment.
		 */
		return Math.random() < epsilon ? ACTIONS[Util.maxIndex(qValues[row][col])] : ACTIONS[Util.randInt(ACTIONS.length-1)];
		//*** check if the random int in best move corresponds to the correct direction
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
				return new int[] {Math.min(row+1, this.rows-1), col};
			case LEFT:
				return new int[] {row, Math.max(col-1, 0)};
			case RIGHT:
				return new int[] {row, Math.min(col+1, this.cols-1)};
				
			//shouldn't go here
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
	public ArrayList<int[]> getShortestPath(int startRow, int startCol) {
		//No path if the agent starts at a terminal state
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
	 * Calculate the temporal difference for a current state and previous Q value
	 * @param row Current row agent is at
	 * @param col Current column agent is at
	 * @param action Action agent took
	 * @param oldQValue Previous Q value
	 * @return Temporal difference between old and now state
	 */
	private double temporalDifference(int row, int col, ACTION action, double oldQ) {
		return rewards[row][col] + (discountFactor * Util.max(qValues[row][col])) - oldQ;
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
			double oldQ;
			
			//Episode ends when the agent hits a terminal state
			while (!isTerminalState(pos[0], pos[1])) {
				//Choose which action to take
				ACTION action = getNextAction(pos[0], pos[1], epsilon);
				// *** grow epsilon over time to explore early, choose best later ***
				
				//Perform action, transition to next state
				prevPos = pos;
				pos = getNextLocation(pos[0], pos[1], action);
				
				//Update Q values
				oldQ = qValues[prevPos[0]][prevPos[1]][action.ordinal()];
				double temporalDifference = temporalDifference(pos[0], pos[1], action, oldQ);
				qValues[prevPos[0]][prevPos[1]][action.ordinal()] = learningRate * temporalDifference + oldQ;
			}
		}
		Util.println("Trained for", numEpisodes, "episodes");
		
		double[][] avgQ = new double[qValues.length][qValues[0].length];
		for (int x = 0; x < avgQ.length; x++)
			for (int y = 0; y < avgQ[0].length; y++)
				avgQ[x][y] = Util.avg(qValues[x][y]);
		Util.print2DArray(avgQ, "%6.2f ");
	}
	
	public void liveTrain() {
		for (int episode = 0; episode < numEpisodes; episode++) {
			int[] start = getStartingLocation();
			agentX = start[0];
			agentY = start[1];
			int[] prevPos;
			double oldQ;
			
			//Episode ends when the agent hits a terminal state
			while (!isTerminalState(agentX, agentY)) {
				//Choose which action to take
				ACTION action = getNextAction(agentX, agentY, epsilon);
				// *** grow epsilon over time to explore early, choose best later ***
				
				//Perform action, transition to next state
				prevPos = new int[] {agentX, agentY};
				int[] newPos = getNextLocation(agentX, agentY, action);
				agentX = newPos[0];
				agentY = newPos[1];
				
				//Update Q values
				oldQ = qValues[prevPos[0]][prevPos[1]][action.ordinal()];
				double temporalDifference = temporalDifference(agentX, agentY, action, oldQ);
				qValues[prevPos[0]][prevPos[1]][action.ordinal()] = learningRate * temporalDifference + oldQ;
				
				//Update the environment
				env.update(episode);
			}
		}
		Util.println("Trained for", numEpisodes, "episodes");
	}
}