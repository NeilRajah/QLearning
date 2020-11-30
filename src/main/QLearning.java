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
		Util.SHOW_CALLER = true;
				
//		model.train();
		
		Window w = new Window(model, 50);
		w.launch();
		
//		//Live train the model
		model.liveTrain();
		
		//Train the model
		long startTime = System.currentTimeMillis();
//		model.train();
//		Util.println("Training took", (System.currentTimeMillis() - startTime) / 1000.0, "seconds");

		//Print the shortest path to the goal
		Util.println("\nShortest Path:");
		startTime = System.currentTimeMillis();
		ArrayList<int[]> shortestPath = model.getShortestPath(0, 0);
		Util.println("Shortest Path took", (System.currentTimeMillis() - startTime) / 1000.0, "seconds");
		for (int[] p : shortestPath)
			System.out.println(p[0] +" "+ p[1]);
		//Create 2D array, graphics to visualize
		
//		w.showPath("basicPath.path");
	}
}
