/**
 * Util
 * Author: Neil Balaskandarajah
 * Created on: 28/11/2020
 * Global utility functions
 */

package main;

public class Util {
	//Constants
	private static final int DEFAULT_STACK_INDEX = 3;	//For printing messages to the console
	
	//Variables
	public static boolean SHOW_CALLER = false;			//Show the method's caller
	
	//Math
	
	/**
	 * Return a random integer in range
	 * @param range Range to generate within
	 * @return Random integer in [0, range]
	 */
	public static int randInt(int range) {
		return (int) Math.round(Math.random() * range);
	}
	
	/**
	 * Return a random integer between min and max
	 * @param min Bottom val
	 * @param max Top val
	 * @return Random integer in [min, max]
	 */
	public static int randInt(int min, int max) {
		return (int) Math.round(((max - min) * Math.random())) + min;
	}
	
	/**
	 * Return the greatest integer in an array
	 * @param arr Array to find max value
	 * @return Greatest value in arr
	 */
	public static int max(int[] arr) {
		int max = arr[0];
		for (int i = 1; i < arr.length; i++)
			if (arr[i] > max)
				max = arr[i];
		return max;
	}
	
	//Output
	
	/**
	 * Print a 2D array 
	 * @param arr Array to print
	 * @param formatStr Format for output of each element
	 */
	public static void print2DArray(int[][] arr, String formatStr) {
		if (SHOW_CALLER) {
			printCallerInfo(DEFAULT_STACK_INDEX);
			System.out.println();
		}
		for (int x = 0; x < arr.length; x++) {
			for (int y = 0; y < arr[x].length; y++)
				System.out.printf(formatStr, arr[x][y]);
			System.out.println();
		}
	}
	
	/**
	 * Print a 2D array 
	 * @param arr Array to print
	 */
	public static void print2DArray(int[][] arr) {
		print2DArray(arr, "%d ");
	}
	
	/**
	 * Get the information of the caller method
	 * @param stackIndex index of element in stack
	 */
	private static void printCallerInfo(int stackIndex) {
		StackTraceElement stack = Thread.currentThread().getStackTrace()[stackIndex];
		System.out.print("[" + stack.getClassName() + "." + stack.getMethodName() + "()] ");
	}
	
	/**
	 * Print a variable amount of objects to the screen
	 * @param objects Objects to print
	 */
	public static void print(Object ... objects) {
		if (SHOW_CALLER)
			printCallerInfo(DEFAULT_STACK_INDEX);
		for (Object o : objects)
			System.out.print(o.toString() + " ");
	}
	
	/**
	 * Print a variable amount of objects to the screen with a new line after
	 * @param objects Objects to print
	 */
	public static void println(Object ... objects) {
		print(objects);
		System.out.println();
	} 
}