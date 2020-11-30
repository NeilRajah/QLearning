/**
 * QLearning
 * Author: Neil Balaskandarajah
 * Created on: 27/11/2020
 * Single agent Q-learning on a grid
 */

package main;

import java.util.ArrayList;

public class QLearning {

	/**
	 * Run the simulation
	 */
	public static void main(String[] args) {
		//Create a Model from a file
		Model model = new Model("src/main/basic.env");
//		int[][] obstacles = new int[][] {{2, 3}, {5, 5}};
//		Model model = new Model(30, 30, new int[] {29,29}, obstacles, 5000);
		// *** create random obstacles, not obscuring the goal ***
		Util.SHOW_CALLER = true;
				
//		model.train();
		
		Window w = new Window(model, 50); // *** make pixelsPerCell based on numCells
		w.launch();
		
//		//Live train the model
//		model.liveTrain();
		
		//Train the model
		long startTime = System.currentTimeMillis();
		model.train();
//		Util.println("Training took", (System.currentTimeMillis() - startTime) / 1000.0, "seconds");

		//Print the shortest path to the goal
		int[] randomPos = (int[]) Util.getRandomElement(model.getNonTerminalStates());
		ArrayList<int[]> shortestPath = model.getShortestPath(0, 0);
		//Create 2D array, graphics to visualize
		
		//Set the path to draw
//		w.showPath("src/main/basicPath.path");
		w.showPath(shortestPath);
	}
}
