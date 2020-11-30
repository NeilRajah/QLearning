/**
 * Util
 * Author: Neil Balaskandarajah
 * Created on: 28/11/2020
 * Global utility functions
 */

package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Util {
	//Constants
	private static final int DEFAULT_STACK_INDEX = 3;	//For printing messages to the console
	
	//Variables
	public static boolean SHOW_CALLER = false;			//Show the method's caller
	
	//Threads
	
	/**
	 * Pause the Thread of the method calling this
	 * @param pause Time to pause the thread in milliseconds
	 */
	public static void sleep(long pause) {
		try {
			Thread.sleep(pause);
		} catch (InterruptedException i) {}
	}
	
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
	 * Return the greatest number in an array
	 * @param arr Array to find max value
	 * @return Greatest value in arr
	 */
	public static double max(double[] arr) {
		double max = arr[0];
		for (int i = 1; i < arr.length; i++)
			if (arr[i] > max)
				max = arr[i];
		return max;
	}
	
	/**
	 * Return the greatest number in an array
	 * @param arr Array to find max value
	 * @return Greatest value in arr
	 */
	public static int maxIndex(double[] arr) {
		int maxIndex = 0;
		for (int i = 1; i < arr.length; i++)
			if (arr[i] > arr[maxIndex])
				maxIndex = i;
		return maxIndex;
	}
	
	/**
	 * Return the sum of an array
	 * @param arr Array of values to sum
	 * @return Sum of the array
	 */
	public static double sum(double[] arr) {
		double sum = 0;
		for (int i = 0; i < arr.length; i++)
			sum += arr[i];
		return sum;
	}
	
	/**
	 * Return the average of an array
	 * @param arr Array of values to average
	 * @return Average of the array
	 */
	public static double avg(double[] arr) {
		return sum(arr) / (double) arr.length;
	}
	
	/**
	 * Scale an array by a linear scalar
	 * @param arr Array to scale
	 * @param s Scaling factor
	 * @return Scaled array
	 */
	public static int[] scaleArray(int[] arr, int s) {
		for (int i = 0; i < arr.length; i++)
			arr[i] *= s;
		return arr;
	}
	
	/**
	 * Scale an array by a linear scalar
	 * @param arr Array to scale
	 * @param s Scaling factor
	 * @return Scaled array
	 */
	public static double[] scaleArray(double[] arr, double s) {
		for (int i = 0; i < arr.length; i++)
			arr[i] *= s;
		return arr;
	}

	/**
	 * Get a random element in the list
	 * @param list List to get element from
	 * @return The random element
	 */
	public static Object getRandomElement(List<?> list) {
		return list.get(randInt(list.size()-1));
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
	public static void print2DArray(double[][] arr) {
		print2DArray(arr, "%.2f ");
	}
	
	/**
	 * Print a 2D array 
	 * @param arr Array to print
	 * @param formatStr Format for output of each element
	 */
	public static void print2DArray(double[][] arr, String formatStr) {
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
		if (SHOW_CALLER)
			printCallerInfo(DEFAULT_STACK_INDEX);
		for (Object o : objects)
			System.out.print(o.toString() + " ");
		System.out.println();
	} 
}