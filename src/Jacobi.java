/*
 * Jacobi.java
 * Author: Jeremiah Hanson
 * -------------------------------------------
 * Solution to the Jacobi problem
 */
public class Jacobi {

	private static int n = 10;
	private static int numProcs = 2;
	private static double left = 10.0, right = 10.0, top = 800.0, bottom = 800.0, epsilon = 0.1;
	
	public static void main(String[] args) {
		
		if (args.length > 0)
			n = Integer.parseInt(args[0]);
		System.out.println(n);
		if (args.length > 1)
			numProcs = Integer.parseInt(args[1]);
		if (args.length > 2)
			left = Integer.parseInt(args[2]);

	}

}
