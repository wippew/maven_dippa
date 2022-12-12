package fi.testsolver.mavenproject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;



public class App {
	
	private static final Logger logger = Logger.getLogger(App.class.getName());
	
	public static void main(String[] args) throws Exception {
		
		int depotCount = 1;
		List<List<Integer>> depots = new ArrayList<List<Integer>>(depotCount);
		for(int i = 0; i < depotCount; i++)  {
			depots.add(new ArrayList<Integer>());
	    }
		depots.get(0).add(0);
		depots.get(0).add(1);
		int vehicleCount = 0;
		for (List<Integer> depot: depots) {
			vehicleCount += depot.size();
		}
		int taskCount = 5;
		int totalCount = depotCount + taskCount;
		int timeOfWorkingDay = 6 * 3600;
		
		//List<MaintenanceWorkDTO> data = Utils.getDataForTasks(totalCount);
		
//		int fetchNewDurations = 1;
//		if (fetchNewDurations == 1) {
//			Double[][] duration = DurationService.getDurationMatrix(totalCount, data);
//			SOLVE_LP_ORTOOLS ortools = new SOLVE_LP_ORTOOLS();
//			int[] demand = new int[duration.length];
//			for (double d: demand) {
//				d = 60;
//			}
//			ortools.SolveOrToolsLP(duration, demand);
//		} else {
//			
//		}
		
		Double[][] duration = new Double[totalCount][totalCount];
		duration[0][0] = 0.0;
		duration[0][1] = 10.0;
		duration[0][2] = 15.0;
		duration[0][3] = 25.0;
		duration[0][4] = 25.0;
		duration[0][5] = 25.0;
		duration[1][0] = 10.0;
		duration[1][1] = 0.0;
		duration[1][2] = 15.0;
		duration[1][3] = 15.0;
		duration[1][4] = 15.0;
		duration[1][5] = 15.0;
		duration[2][0] = 7.0;
		duration[2][1] = 10.0;
		duration[2][2] = 0.0;
		duration[2][3] = 10.0;
		duration[2][4] = 10.0;
		duration[2][5] = 10.0;
		duration[3][0] = 20.0;
		duration[3][1] = 30.0;
		duration[3][2] = 20.0;
		duration[3][3] = 0.0;
		duration[3][4] = 10.0;
		duration[3][5] = 20.0;
		duration[4][0] = 10.0;
		duration[4][1] = 20.0;
		duration[4][2] = 10.0;
		duration[4][3] = 20.0;
		duration[4][4] = 0.0;
		duration[4][5] = 10.0;
		duration[5][0] = 20.0;
		duration[5][1] = 10.0;
		duration[5][2] = 20.0;
		duration[5][3] = 10.0;
		duration[5][4] = 20.0;
		duration[5][5] = 0.0;
		
		int[] demand = {0, 3600, 3600, 3600};
		
		SOLVE_LP_ORTOOLS ortools = new SOLVE_LP_ORTOOLS();
		ortools.SolveOrToolsLP(duration, demand, vehicleCount, depots);
		
		
	}
}
