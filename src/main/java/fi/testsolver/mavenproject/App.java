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
		
		int[][] duration = new int[3][3];
		duration[0][0] = 0;
		duration[0][1] = 10;
		duration[0][2] = 15;
		duration[1][0] = 10;
		duration[1][1] = 0;
		duration[1][2] = 15;
		duration[2][0] = 7;
		duration[2][1] = 10;
		duration[2][2] = 0;
		int[] demand = {0, 60, 60, 60};
		
		
		
		SolveOrToolsLP(duration, demand);
		
	}
	
	public static void SolveOrToolsLP(int[][] duration, int[] demand) {
		Loader.loadNativeLibraries();
		int numberOfNodes = duration[0].length;
		int numberOfCars = 1;
		int depotCount = 1;
		int maxTime = 5 * 60 * 60;
		int numberOfVehicles = 1;
		int[] allNodes = IntStream.range(0, numberOfNodes).toArray();
		int[] allTasks = IntStream.range(1, numberOfNodes).toArray();
		int[] allVehicles = IntStream.range(0, numberOfVehicles).toArray();
		int[] allDepots = IntStream.range(0, 1).toArray();
		
		String solverName = "GLOP";
		logger.info("Instantiating solver" + solverName);
		//MPSolver model = new MPSolver("SimpleGurobi", MPSolver.OptimizationProblemType.GUROBI_LINEAR_PROGRAMMING);
		MPSolver model= MPSolver.createSolver("GLOP");
		model.enableOutput();
		
		logger.info("Defining model...");
		MPVariable[][][] x = new MPVariable[numberOfNodes][numberOfNodes][numberOfVehicles];
		for (int k: allVehicles) {
			for(int i: allNodes) {
				for (int j: allNodes) {
					if (i != j) {
						x[i][j][k] = model.makeBoolVar(String.format("x%d_%d, %d", i, j, k));	
					}
				}
			}
		}
		
		
		
		// constraint 1: Leave every task at most once
		logger.info("Creating " +  String.valueOf(numberOfNodes) + "Constraint 1...");
		for (int j: allTasks) {
			MPConstraint constraint = model.makeConstraint(0, 1, "c1");
			for (int i: allNodes) {
				for (int k: allVehicles) {
					if (i != j) {
						constraint.setCoefficient(x[i][j][k], 1);
					}
				}
			}
		}
		
		//constraint 2: reach every task from at most one other task
		logger.info("Creating " +  String.valueOf(numberOfNodes) + "Constraint 2...");
		for (int i: allTasks) {
			MPConstraint constraint = model.makeConstraint(0, 1, "c2");
			for (int j: allNodes) {
				for (int k: allVehicles) {
					if (i != j) {
						constraint.setCoefficient(x[i][j][k], 1);
					}
				}
			}
		}
		
		//constraint 3: depart from own depot
		logger.info("Creating " +  String.valueOf(numberOfNodes) + "Constraint 3.1....");		
		for (int i: allDepots) {			
			int[] cars = new int[1];
			cars[0] = 0;
			for (int k: allVehicles) {
				MPConstraint constraint3 = model.makeConstraint(1, 1, "c3.1.");
				int car = cars[k];				
				for (int j : allNodes) {
					constraint3.setCoefficient(x[i][j][car], 1);
				}
			}
		}
//		
//		//constraint 3.2: return to own depot
		logger.info("Creating " +  String.valueOf(numberOfNodes) + "Constraint 3.2....");
		for (int j: allDepots) {
			
			int[] cars = new int[1];
			cars[0] = 0;
			for (int k: allVehicles) {
				MPConstraint constraint = model.makeConstraint(1, 1, "c3.2.");
				int car = cars[k];				
				for (int i : allNodes) {
					constraint.setCoefficient(x[i][j][car], 1);
				}
			}
		}
		
		//constraint 4: skip for now
		
		
		//constraint 5: number of vehicles in and out of a tasks's location stays the same
		logger.info("Creating " +  String.valueOf(numberOfNodes) + "Constraint 5...");
		for (int k: allVehicles) {
			for (int j: allNodes) {
				MPConstraint constraint = model.makeConstraint(0, 0, "c5");
				for (int i: allNodes) {
					if (i != j) {
						constraint.setCoefficient(x[i][j][k], 1);
						constraint.setCoefficient(x[j][i][k], -1);
					}
				}
			}
		}
		
		//constraint 6: the time-capacity of each vehicle should not exceed the maximum capacity
		logger.info("Creating " +  String.valueOf(numberOfNodes) + "Constraint 6...");
		for (int k: allVehicles) {
			MPConstraint constraint = model.makeConstraint(0, maxTime, "c6");
			for (int i: allNodes) {
				for (int j: allNodes) {
					constraint.setCoefficient(x[i][j][k] , (duration[i][j] + demand[j]));
				}
			}
		}
		
		//definition of auxilliary variable u
		MPVariable[] u = new MPVariable[numberOfNodes];
		for(int i: allTasks) {
			u[i] = model.makeIntVar(1, numberOfNodes, String.format("u_i%d", i));
		}
//		
		// MTZ subtour elimination
		// first forcing depot to be visited
//		MPConstraint constraint = model.makeConstraint(1, 1, "c7");
//		constraint.setCoefficient(u[0], 1);	
		logger.info("Creating " +  String.valueOf(numberOfNodes) + "Constraint 6...");
		for (int i: allTasks) {
			for (int j: allTasks) {
				for (int k: allVehicles) {
					if (i != j) {
						MPConstraint constraint = model.makeConstraint(-10, numberOfNodes, "c6 "+ String.format("%d_%d", i,j));
						constraint.setCoefficient(u[j], 1);
						constraint.setCoefficient(u[i], -1);
						constraint.setCoefficient(x[i][j][k], -6);
					}
				}
			}
		}		
		
		MPObjective objective = model.objective();		
		for (int k: allVehicles) {
			for (int i: allTasks) {
				for (int j: allTasks) {
					if (i != j) {
						objective.setCoefficient(x[i][j][k], 1);
					}
				}
			}
		}
		objective.setMaximization();
		
		//model.setTimeLimit(60);
		
		String test = model.exportModelAsLpFormat();
		System.out.println(test);
		
		MPSolver.ResultStatus resultStatus = model.solve();
		
		
		List<String> k0 = new ArrayList<>();
		
		// Check that the problem has a feasible solution.
		if (resultStatus == MPSolver.ResultStatus.OPTIMAL
		    || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
		  System.out.println("Total cost: " + objective.value() + "\n");
		  for (int k: allVehicles) {
			  for(int i: allNodes) {
				  for (int j: allNodes) {
					  if (i != j && x[i][j][k].solutionValue() == 1.0) {
						  k0.add(String.format("%d_%d, %d", i, j, k));
					  }
				  }
			  }
		  }
		} else {
		  System.err.println("No solution found.");
		}
		
		System.out.println("the array is hence: ");
		for (String str: k0) {
			System.out.println(str);
		}
		
		
		
	}
}