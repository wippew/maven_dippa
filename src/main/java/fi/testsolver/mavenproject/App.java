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
		
		int vehicleCount = 1;
		int depotCount = 1;
		int taskCount = 2;
		int totalCount = depotCount + taskCount;
		int timeOfWorkingDay = 6 * 3600;
		
		List<MaintenanceWorkDTO> data = Utils.getDataForTasks(totalCount);		
		
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
		
		Double[][] duration = new Double[3][3];
		duration[0][0] = 0.0;
		duration[0][1] = 10.0;
		duration[0][2] = 15.0;
		duration[1][0] = 10.0;
		duration[1][1] = 0.0;
		duration[1][2] = 15.0;
		duration[2][0] = 7.0;
		duration[2][1] = 10.0;
		duration[2][2] = 0.0;

		
		int[] demand = {0, 3600, 3600, 3600};
		
		SOLVE_LP_ORTOOLS ortools = new SOLVE_LP_ORTOOLS();
		ortools.SolveOrToolsLP(duration, demand);
		
		
	}
}
