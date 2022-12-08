package fi.testsolver.mavenproject;

import java.util.stream.IntStream;

import com.google.ortools.Loader;
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
	
	public static void SolveOrToolsLP(Double[][] duration, int[] demand) {
		Loader.loadNativeLibraries();
		int numberOfNodes = duration[0].length;
		int depotCount = 1;
		int maxTime = 6 * 60 * 60;
		int numberOfVehicles = 1;
		int[] allNodes = IntStream.range(0, numberOfNodes).toArray();
		int[] allTasks = IntStream.range(1, numberOfNodes).toArray();
		int[] allVehicles = IntStream.range(0, numberOfVehicles).toArray();
		int[] allDepots = IntStream.range(0, 1).toArray();
		
		MPSolver model= MPSolver.createSolver("SCIP");
		model.enableOutput();
		
		logger.info("Defining model...");
		MPVariable[][][] x = new MPVariable[numberOfNodes][numberOfNodes][numberOfVehicles];
		for (int k: allVehicles) {
			for(int i: allNodes) {
				for (int j: allNodes) {
					if (i != j) {
						x[i][j][k] = model.makeBoolVar( String.format("x%d_%d, %d", i, j, k));		
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
			for (int k = 0; k < cars.length; k++) {
				MPConstraint constraint0 = model.makeConstraint(1, 1, "c3.1.");
				int car = cars[k];				
				for (int j : allNodes) {
					constraint0.setCoefficient(x[i][j][car], 1);
				}
				
				MPConstraint constraint1 = model.makeConstraint(1, 1, "c3.2.");			
				for (int j : allNodes) {
					constraint1.setCoefficient(x[j][i][car], 1);
				}
			}
		}
		
		//constraint 4: no trip is conducted by a vehicle not belonging to a depot
		logger.info("Creating Constraint 4...");
//		for (int depotIndex: allDepots) {
//			int [] cars = new int[1];
//			cars[0] = 0;
//			for (int otherDepot: allDepots) {
//				if (depotIndex != otherDepot) {					
//					for (int k = 0; k < cars.length; k++) {
//						MPConstraint constraint0 = model.makeConstraint(0, 0, "c4");
//						int car = cars[k];
//						for (int j: allNodes) {
//							constraint0.setCoefficient(x[otherDepot][j][car], 1);
//						}
//						MPConstraint constraint1 = model.makeConstraint(0, 0, "c4");
//						for (int i: allNodes) {
//							constraint1.setCoefficient(x[i][otherDepot][car], 1);
//						}
//					}
//				}
//			}
//			
//		}
		
		
		//constraint 5: number of vehicles in and out of a tasks's location stays the same
		logger.info("Creating Constraint 5...");
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
//		logger.info("Creating " +  String.valueOf(numberOfNodes) + "Constraint 6...");
//		for (int k: allVehicles) {
//			MPConstraint constraint = model.makeConstraint(0, maxTime, "c6");
//			for (int i: allNodes) {
//				for (int j: allNodes) {
//					constraint.setCoefficient(x[i][j][k] , (duration[i][j] + demand[j]));
//				}
//			}
//		}
		
		//definition of auxilliary variable u
		MPVariable[] u = new MPVariable[numberOfNodes];
		for(int i: allTasks) {
			u[i] = model.makeIntVar(1, numberOfNodes, String.format("u_i%d", i));
		}
//		
		// MTZ subtour elimination
		// first forcing depot to be visited
//		MPConstraint constraintTest = model.makeConstraint(1, 1, "MTZ_DEPOT");
//		constraintTest.setCoefficient(u[0], 1);	
//		
//		
//		//then force all to be more than 2
//		for (int i :allTasks) {
//			MPConstraint constraintTest2 = model.makeConstraint(2, numberOfNodes, "MTZ_DEPOT_2");
//			constraintTest2.setCoefficient(u[i], 1);
//		}
//		
		
//		logger.info("Creating " +  String.valueOf(numberOfNodes) + "Constraint 7...");
		for (int i: allTasks) {
			for (int j: allTasks) {
				for (int k: allVehicles) {
					if (i != j) {
						MPConstraint constraint = model.makeConstraint(-numberOfNodes+1, numberOfNodes, "c7 "+ String.format("%d_%d", i,j));
						constraint.setCoefficient(u[j], 1);
						constraint.setCoefficient(u[i], -1);
						constraint.setCoefficient(x[i][j][k], -numberOfNodes);
					}
				}
			}
		}		
		
		MPObjective objective = model.objective();		
		for (int k: allVehicles) {
			for (int i: allNodes) {
				for (int j: allNodes) {
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
		List<Double> k1 = new ArrayList<>();
		List<Double> k2 = new ArrayList<>();
		
		k2.add(u[1].solutionValue());
		k2.add(u[2].solutionValue());
		k2.add(u[3].solutionValue());

		
		// Check that the problem has a feasible solution.
		if (resultStatus == MPSolver.ResultStatus.OPTIMAL
		    || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
		  System.out.println("Total cost: " + objective.value() + "\n");
		  for (int k: allVehicles) {
			  for(int i: allNodes) {
				  for (int j: allNodes) {
					  
					  if (i != j && x[i][j][k].solutionValue() >= 0.5) {
						  double testtest = x[i][j][k].solutionValue();
						  k0.add(String.format("%d_%d", i, j));
						  k1.add(testtest);
						  System.out.println("ASDASD");
					  }
						  
//						  if (k == 0) {
//							  k0.add(String.format("%d_%d", i, j));
//						  } else {
//							  k1.add(String.format("%d_%d", i, j));
//						  }
					  }
				  }
			  }
		} else {
		  System.err.println("No solution found.");
		}
		
		System.out.println("the array 0 is hence: ");
		System.out.println(k0);		
		
		System.out.println("the array 1 is hence: ");
		System.out.println(k1);	
		
		System.out.println("the array 2 is hence: ");
		System.out.println(k2);	
	}
}
