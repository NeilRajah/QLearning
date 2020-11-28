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
		int width = 8;
		int height = 8;
		int[] goal = new int[] {width-1, height-1};
		int numObstacles = 5;
		int[][] obstacles = new int[numObstacles][];
		for (int o = 0; o < numObstacles; o++)
			obstacles[o] = new int[] {(int) (Math.random() * (width-1)), (int) (Math.random() * (height-1))};
		
//		Environment env = new Environment(width, height, goal, obstacles);
//		int[][] rewards = env.getRewards();
//		print2DArray(rewards);
		
		Environment env = new Environment("src/main/basic.env");
		print2DArray(env.getRewards(), "%4d ");
	}
	
	/**
	 * Print a 2D array 
	 * @param arr Array to print
	 * @param formatStr Format for output of each element
	 */
	public static void print2DArray(int[][] arr, String formatStr) {
		for (int x = 0; x < arr.length; x++) {
			for (int y = 0; y < arr[x].length; y++) {
				System.out.printf(formatStr, arr[x][y]);
			}
			System.out.println();
		}
	}
	
	/**
	 * Print a 2D array 
	 * @param arr Array to print
	 */
	public static void print2DArray(int[][] arr) {
		for (int x = 0; x < arr.length; x++) {
			for (int y = 0; y < arr[x].length; y++) {
				System.out.printf("%d ", arr[x][y]);
			}
			System.out.println();
		}
	}
}
