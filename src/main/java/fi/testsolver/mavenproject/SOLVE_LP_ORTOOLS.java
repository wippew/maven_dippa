package fi.testsolver.mavenproject;

import java.util.stream.IntStream;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.sat.Literal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SOLVE_LP_ORTOOLS {
	
	public void SOLVE_LP_ORTOOLS() {
		
	}
	
	private static final Logger logger = Logger.getLogger(SOLVE_LP_ORTOOLS.class.getName());
	
	public void SolveOrToolsLP(int[][] duration, int[] demand) {
		int numberOfNodes = duration[0].length;
		int numberOfCars = 1;
		int depotCount = 1;
		int maxTime = 5 * 60 * 60;
		int[] allNodes = IntStream.range(0, numberOfNodes).toArray();
		int[] allTasks = IntStream.range(1, numberOfNodes).toArray();
		int[] allVehicles = IntStream.range(0, 3).toArray();
		
		String solverName = "GUROBI_MIP";
		logger.info("Instantiating solver" + solverName);
		MPSolver model = MPSolver.createSolver(solverName);
		model.enableOutput();
		
		logger.info("Defining model...");
		MPVariable[][][] x = new MPVariable[3][numberOfNodes][numberOfNodes];
		for (int k: allVehicles) {
			for(int i: allNodes) {
				for (int j: allNodes) {
					x[i][j][k] = model.makeBoolVar(String.format("x%d_%d, %d", i, j, k));	
				}
			}
		}
		
		MPVariable[] u = new MPVariable[numberOfNodes];
		for(int i: allNodes) {
			u[i] = model.makeIntVar(0, numberOfNodes, String.format("u_i%d", i));
		}
		
		// constraint 1: Leave every task at most once
		logger.info("Creating " +  String.valueOf(numberOfNodes) + "Constraint 1...");
		for (int j: allNodes) {
			MPConstraint constraint = model.makeConstraint(0, 1, "c1");
			for (int i: allNodes) {
				for (int k: allVehicles) {
					constraint.setCoefficient(x[i][j][k], 1);
				}
			}
		}
		
		//constraint 2: reach every task from at most one other task
		for (int i: allNodes) {
			MPConstraint constraint = model.makeConstraint(0, 1, "c2");
			for (int j: allNodes) {
				for (int k: allVehicles) {
					constraint.setCoefficient(x[i][j][k], 1);
				}
			}
		}
		
		//constraint 3: depart from own depot and return to own depot
		for (int depot_index= 0; depot_index < depotCount; depot_index++) {
			int[] cars = new int[1];
			cars[0] = 1;
			for (int k: allVehicles) {
				int car = cars[k];
				MPConstraint constraint = model.makeConstraint(1, 1, "c3");
				for (int j : allNodes) {
					constraint.setCoefficient(x[depot_index][j][car], 1);
				}
				for (int i : allNodes) {
					constraint.setCoefficient(x[i][depot_index][car], 1);
				}
			}
		}
		
		//constraint 4: skip for now
		
		
		//constraint 5: number of vehicles in and out of a tasks's location stays the same
		for (int k: allVehicles) {
			for (int j: allNodes) {
				MPConstraint constraint = model.makeConstraint(0, 0, "c5");
				for (int i: allNodes) {
					constraint.setCoefficient(x[i][j][k], 1);
					constraint.setCoefficient(x[j][i][k], -1);
				}
			}
		}
		
		//constraint 6: the time-capacity of each vehicle should not exceed the maximum capacity
		for (int k: allVehicles) {
			MPConstraint constraint = model.makeConstraint(0, maxTime, "c6");
			for (int i: allNodes) {
				for (int j: allNodes) {
					constraint.setCoefficient(x[i][j][k] , (duration[i][j] + demand[j]));
				}
			}
		}
		
		// MTZ subtour elimination
		// first forcing depot to be visited
//		MPConstraint constraint = model.makeConstraint(1, 1, "c7");
//		constraint.setCoefficient(u[0], 1);		
		for (int i: allTasks) {
			for (int j: allTasks) {
				for (int k: allVehicles) {
					if (i != j) {
						MPConstraint constraint = model.makeConstraint(-6, numberOfNodes);
						constraint.setCoefficient(u[j], 1);
						constraint.setCoefficient(u[i], -1);
						constraint.setCoefficient(x[i][j][k], -7);
					}
				}
			}
		}
		
		MPObjective objective = model.objective();
		for (int k: allVehicles) {
			for (int i: allTasks) {
				for (int j: allTasks) {
					objective.setCoefficient(x[i][j][k], 1);
				}
			}
		}
		
		//model.setTimeLimit(60);
		
		MPSolver.ResultStatus resultStatus = model.solve();
		
		List<String> k0 = new ArrayList<>();
		
		// Check that the problem has a feasible solution.
		if (resultStatus == MPSolver.ResultStatus.OPTIMAL
		    || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
		  System.out.println("Total cost: " + objective.value() + "\n");
		  for (int k: allVehicles) {
			  for(int i: allNodes) {
				  for (int j: allNodes) {
					  if (x[i][j][k].solutionValue() == 1) {
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
