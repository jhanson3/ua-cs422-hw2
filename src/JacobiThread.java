/*
 * JacobiThread
 * Jeremiah Hanson
 * -------------------------------
 * thread to run Jacobi
 */
public class JacobiThread implements Runnable{
	
	int firstRow, lastRow;
	double myDiff = 0.0;
	int worker;
	
	/*
	 * Constructor
	 */
	public JacobiThread(int thread) {
		worker = thread;
		if (thread > 1)
			firstRow = (thread - 1)*Jacobi.height;
		else
			firstRow = (thread - 1)*Jacobi.height + 1;
		lastRow = firstRow + Jacobi.height - 1;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		myDiff = 0.0;
		Jacobi.barrier(worker);
		for (int iters=1; iters<Jacobi.MAXITERS; iters+=2) {
			
			for (int x=firstRow; x<lastRow; x++) {
				for (int y=1; y<Jacobi.n-1; y++) {
					Jacobi.newGrid[x][y] = (Jacobi.grid[x-1][y] + Jacobi.grid[x+1][y] + Jacobi.grid[x][y-1] + Jacobi.grid[x][y+1])*0.25;
				}
			}
			
			Jacobi.barrier(worker);
			for (int x=firstRow; x<lastRow; x++) {
				for (int y=1; y<Jacobi.n-1; y++) {
					Jacobi.grid[x][y] = (Jacobi.newGrid[x-1][y] + Jacobi.newGrid[x+1][y] + Jacobi.newGrid[x][y-1] + Jacobi.newGrid[x][y+1])*0.25;
				}
			}
			
			Jacobi.barrier(worker);
		}
		
		for (int x=firstRow; x<lastRow; x++) {
			for (int y=1; y<Jacobi.n-1; y++) {
				double temp = 0.0;
				if (myDiff < (temp = Math.abs(Jacobi.newGrid[x][y]-Jacobi.grid[x][y]))) {
					myDiff = temp;
				}
			}
		}
		Jacobi.maxDiff[worker-1] = myDiff;
		Jacobi.barrier(worker);
	}
}