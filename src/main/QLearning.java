/**
 * QLearning
 * Author: Neil Balaskandarajah
 * Created on: 27/11/2020
 * Single agent Q-learning on a grid
 */

package main;

public class QLearning {

	/**
	 * Run the simulation
	 */
	public static void main(String[] args) {
		/*
		int width = 8;
		int height = 8;
		int[] goal = new int[] {width-1, height-1};
		int numObstacles = 5;
		int[][] obstacles = new int[numObstacles][];
		for (int o = 0; o < numObstacles; o++)
			obstacles[o] = new int[] {(int) (Math.random() * (width-1)), (int) (Math.random() * (height-1))};
		
		Environment env = new Environment(width, height, goal, obstacles);
		int[][] rewards = env.getRewards();
		print2DArray(rewards);
		*/
		
		Model env = new Model("src/main/basic.env");
		Util.SHOW_CALLER = true;
		Util.print2DArray(env.getRewards(), "%4d ");
		
//		Util.println("Non-terminal States:");
//		for (int[] xy : env.nonTerminalStates) {
//			Util.println(String.format("(%d %d)", xy[0], xy[1]));
//		}
	}
}
