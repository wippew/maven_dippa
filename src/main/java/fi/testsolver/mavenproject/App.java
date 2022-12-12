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
		
		int depotCount = 2;
		List<List<Integer>> depots = new ArrayList<List<Integer>>(depotCount);
		for(int i = 0; i < depotCount; i++)  {
			depots.add(new ArrayList<Integer>());
	    }
		depots.get(0).add(0);
		depots.get(0).add(1);
		depots.get(1).add(2);
		depots.get(1).add(3);
		int vehicleCount = 0;
		for (List<Integer> depot: depots) {
			vehicleCount += depot.size();
		}
		int taskCount = 30;
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
		for (int i = 0; i < totalCount; i++) {
			for (int j = 0; j < totalCount; j++) {
				if (i == j) {
					duration[i][j] = 0.0;
				} else {
					duration[i][j] = (1.0 + i + j) * 60;
				}
			}
		}
		
		int[] demand = new int[totalCount];
		for (int i = 0; i < totalCount; i++) {
			demand[i] = 1800;
		}		
		demand[0] = 0;
		demand[1] = 0;
		
		SOLVE_LP_ORTOOLS ortools = new SOLVE_LP_ORTOOLS();
		ortools.SolveOrToolsLP(duration, demand, vehicleCount, depots);
		
		
	}
}
