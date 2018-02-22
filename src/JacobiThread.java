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
		if (Jacobi.numProcs < 4) {
			if (thread > 1)
				firstRow = (thread - 1)*Jacobi.height;
			else
				firstRow = (thread - 1)*Jacobi.height + 1;
			
			if (Jacobi.numProcs != 1 && Jacobi.numProcs%2 == 1)
				lastRow = firstRow + Jacobi.height;
			else if (Jacobi.numProcs == 1) 
				lastRow = firstRow + Jacobi.height - 2;
			else 
				lastRow = firstRow + Jacobi.height - 1;
		}
		else {
			firstRow = (thread - 1)*Jacobi.height + 1;
			lastRow = firstRow + Jacobi.height;
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		myDiff = 0.0;
		barrier(worker);
		for (int iters=1; iters<Jacobi.MAXITERS; iters+=2) {
			
			for (int x=firstRow; x<lastRow; x++) {
				for (int y=1; y<Jacobi.n-1; y++) {
					if (x < Jacobi.n-1)
						Jacobi.newGrid[x][y] = (Jacobi.grid[x-1][y] + Jacobi.grid[x+1][y] + Jacobi.grid[x][y-1] + Jacobi.grid[x][y+1])*0.25;
				}
			}
			
			barrier(worker);
			for (int x=firstRow; x<lastRow; x++) {
				for (int y=1; y<Jacobi.n-1; y++) {
					if (x < Jacobi.n-1)
						Jacobi.grid[x][y] = (Jacobi.newGrid[x-1][y] + Jacobi.newGrid[x+1][y] + Jacobi.newGrid[x][y-1] + Jacobi.newGrid[x][y+1])*0.25;
				}
			}
			
			barrier(worker);
		}
		
		for (int x=firstRow; x<lastRow; x++) {
			for (int y=1; y<Jacobi.n-1; y++) {
				double temp = 0.0;
				if (x < Jacobi.n-1){
					if (myDiff < (temp = Math.abs(Jacobi.newGrid[x][y]-Jacobi.grid[x][y]))) {
						myDiff = temp;
					}
				}
			}
		}
		Jacobi.maxDiff[worker-1] = myDiff;
		barrier(worker);
		//System.out.println("worker " + worker + " finished");
	}
	
	/*
	 * barrier
	 * Jeremiah Hanson
	 * ----------------------------
	 * dissemination barrier
	 */
	public void barrier(int w) {
		
		if (Jacobi.numProcs == 1)
			return;
		else if (Jacobi.numProcs == 2) {
			Jacobi.arrive[w-1]++;
			
			int j = w % Jacobi.numProcs;
			while (Jacobi.arrive[j] < Jacobi.arrive[w-1]) {
				System.out.print("");
			}
		}
		else if (Jacobi.numProcs == 3) {
			for (int s=1; s<2; s++) {
				Jacobi.arrive[w-1]++;
				
				int j = ((w-1)+s) % Jacobi.numProcs;
				while (Jacobi.arrive[j] < Jacobi.arrive[w-1]) {
					System.out.print("");
				}
			}
		}
		else if (Jacobi.numProcs == 4) {
			for (int s=1; s<2; s++) {
				Jacobi.arrive[w-1]++;
				
				int j = ((w-1)+s) % Jacobi.numProcs;
				while (Jacobi.arrive[j] < Jacobi.arrive[w-1]) {
					System.out.print("");
				}
			}
		}
		else {
			int j = 0;
			for (int s=1; s<(Jacobi.numProcs/2)+1; s++) {
				Jacobi.arrive[w-1]++;
				
				j = (int)(Math.pow(w, s));
				if (j > 3) {
					j = (j-1)%Jacobi.numProcs;
				}
				while (Jacobi.arrive[j] < Jacobi.arrive[w-1]);
			}
			
		}
		
	}
}