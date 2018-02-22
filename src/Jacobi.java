import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Semaphore;

/*
 * Jacobi.java
 * Author: Jeremiah Hanson
 * -------------------------------------------
 * Solution to the Jacobi problem
 */


public class Jacobi {

	public static int n = 10;
	public static int numProcs = 4;
	public static double left = 10.0, right = 800.0, top = 10.0, bottom = 800.0, epsilon = 0.1;
	public static double grid[][], newGrid[][];
	public static int height;
	public static double maxDiff[];
	public static Semaphore mutex, bar;
	public static double arrive[];
	public static JacobiThread threads[];
	public static Thread active[];
	public static long start, end;
	
	static final int MAXITERS = 100;
	
	public static void main(String[] args) {
		
		if (args.length > 0)
			n = Integer.parseInt(args[0]);
		if (args.length > 1)
			numProcs = Integer.parseInt(args[1]);
		if (args.length > 2)
			left = Integer.parseInt(args[2]);
		if (args.length > 3)
			top = Integer.parseInt(args[3]);
		if (args.length > 4)
			right = Integer.parseInt(args[4]);
		if (args.length > 5)
			bottom = Integer.parseInt(args[5]);
		if (args.length > 6)
			epsilon = Integer.parseInt(args[6]);
		
		mutex = new Semaphore(1);
		bar = new Semaphore(0);
		height = n/numProcs;
		maxDiff = new double[numProcs];
		arrive = new double[numProcs];
		threads = new JacobiThread[numProcs];
		active = new Thread[numProcs];
		
		grid = new double[n][n];
		newGrid = new double[n][n];
		
		// setup grid
		for (int x=0; x<n; x++) {
			grid[0][x] = top;
			newGrid[0][x] = top;
			
			grid[x][0] = left;
			newGrid[0][x] = left;
			
			grid[n-1][x] = bottom;
			newGrid[n-1][x] = bottom;
			
			grid[x][n-1] = right;
			newGrid[x][n-1] = right;
		}
		
		
		//printGrid(); 
		//seqRun();       // Sequential Run
		//printGrid();
		
		start = System.nanoTime();
		
		for (int i=0; i<numProcs; i++) {
			threads[i] = new JacobiThread(i+1);
		}
		for (int i=0; i<numProcs; i++) {
			active[i] = new Thread(threads[i]);
			active[i].start();
		}
		
		while (true) {
			int done = 0;
			for (int i=0; i<numProcs; i++) {
				if (!active[i].isAlive())
					done++;
			}
			if (done == numProcs)
				break;
		}
		//printGrid();
		end = System.nanoTime();
		
		try {
			writeToFile();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
			
	}
	
	/*
	 * printGrid
	 * Jeremiah Hanson
	 * -------------------------------
	 * prints the grid
	 */
	public static void printGrid() {
		for (int x=0; x<n; x++) {
			for (int y=0; y<n; y++) {
				if (grid[x][y] < 10) {
					System.out.printf(" %.4f", grid[x][y]);
				}
				else if (grid[x][y] < 100) {
					System.out.printf(" %.3f", grid[x][y]);
				}
				else {
					System.out.printf(" %.2f", grid[x][y]);
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	/*
	 * writeToFile
	 * Jeremiah Hanson
	 * --------------------------------
	 * writes data to a file
	 */
	public static void writeToFile() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("JacobiResults.txt", "UTF-8");
		writer.println("n: " + n);
		writer.println("numProcs: " + numProcs);
		writer.println("Top: " + top);
		writer.println("Left: " + left);
		writer.println("Bottom: " + bottom);
		writer.println("Right: " + right);
		writer.println("Espilon: " + epsilon);
		writer.printf("Time: %.9f", ((double)(end-start)/1000000000));
		writer.println("sec");
		writer.println();
		
		for (int x=0; x<n; x++) {
			for (int y=0; y<n; y++) {
				if (grid[x][y] < 10) {
					writer.printf(" %.4f", grid[x][y]);
				}
				else if (grid[x][y] < 100) {
					writer.printf(" %.3f", grid[x][y]);
				}
				else {
					writer.printf(" %.2f", grid[x][y]);
				}
			}
			writer.println();
		}
		
		writer.close();
	}
	
	/*
	 * seqRun
	 * Jeremiah Hanson
	 * -------------------------------
	 * runs Jacobi in sequential
	 */
	public static void seqRun() {
		while (true) {
			
			double newGrid[][] = new double[n][n];
			
			// calculation
			for (int x=1; x<n-1; x++) {
				for (int y=1; y<n-1; y++) {
					newGrid[x][y] = (grid[x-1][y] + grid[x+1][y] + grid[x][y-1] + grid[x][y+1])*0.25;
				}
			}
			
			// check for maxdiff
			double maxdiff = 0.0;
			for (int x=1; x<n-1; x++) {
				for (int y=1; y<n-1; y++) {
					double temp = 0.0;
					if (maxdiff < (temp = Math.abs(newGrid[x][y]-grid[x][y]))) {
						maxdiff = temp;
					}
				}
			}
			
			// check for termination
			if (maxdiff < epsilon)
				break;
			
			for (int x=1; x<n-1; x++) {
				for (int y=1; y<n-1; y++) {
					grid[x][y] = newGrid[x][y];
				}
			}
		}
	}

}


