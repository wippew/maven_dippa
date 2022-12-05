package fi.testsolver.mavenproject;

public class MaintenanceWorkDTO {
	public MaintenanceWorkDTO(Double xCoordinates, Double yCoordinates, Integer demand, String type, String waypoint) {
		this.xCoordinates = xCoordinates;
		this.yCoordinates = yCoordinates;
		this.demand = demand;
		this.type = type;
		this.waypoint = waypoint;
	}
	
	public double xCoordinates;
	public double yCoordinates;
	public Integer demand;
	public String type;
	public String waypoint;	
	
}
