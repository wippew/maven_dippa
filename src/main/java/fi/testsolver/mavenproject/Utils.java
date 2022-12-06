package fi.testsolver.mavenproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;  
import org.apache.poi.hssf.usermodel.HSSFWorkbook; 

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.proj4j.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;

public class Utils {
	
	private String fileName = "Mobilenote.xls";
	private String sheet = "Huolto";
	private static String fileLocation = "C:\\Users\\Victor\\eclipse-workspace\\mavenproject\\src\\main\\java\\fi\\testsolver\\mavenproject\\Mobilenote.xls";
	
	public static List<MaintenanceWorkDTO> getDataForTasks(int numberOfTasks) {
		Double depotFirstLatitude = 60.875438;
		Double depotFirstLongitude = 23.252894;
		String depotWaypoint = depotFirstLatitude.toString() + "," + depotFirstLongitude.toString();
		

		List<Double> XCoordinatesColumn = getXCoordinatesFromFile(fileLocation);
		List<Double> YCoordinatesColumn = getYCoordinatesFromFile(fileLocation);
		List<String> typesAsString = getTypesAsStringFromFile(fileLocation);
		List<Integer> demands = getDemandsFromFile(typesAsString);
		
		
		List<MaintenanceWorkDTO> ret = new ArrayList<MaintenanceWorkDTO>();
		MaintenanceWorkDTO depotDTO = new MaintenanceWorkDTO(depotFirstLatitude, depotFirstLongitude, 0, "depot_1", depotWaypoint);
		ret.add(depotDTO);
		for (int i = 0; i < numberOfTasks; i++) {
			Double currentX = XCoordinatesColumn.get(i);
			Double currentY = YCoordinatesColumn.get(i);
			Integer currentDemand = demands.get(i);
			String currentType = typesAsString.get(i);
			String currentWaypoint = currentX.toString() + "," + currentY.toString();
			MaintenanceWorkDTO dto = new MaintenanceWorkDTO(currentX, currentY, currentDemand, currentType, currentWaypoint);
			ret.add(dto);
		}		
		return ret;
	}
	
	
	public static List<Double> getXCoordinatesFromFile(String filePath) {
	    File file = new File(filePath);
	    List<Double> array = new ArrayList<Double>();
	    try {
	        FileInputStream inputStream = new FileInputStream(file);
	        Workbook workBook = new HSSFWorkbook(inputStream);
	        Sheet sheet = workBook.getSheetAt(0);
        	int firstRow = sheet.getFirstRowNum();
        	int lastRow = sheet.getLastRowNum();
        	for (int index = firstRow + 1; index <= lastRow; index++) {
        	    Row row = sheet.getRow(index);
        	    Cell cell = row.getCell(35);
        	    Double numericValue = cell.getNumericCellValue();
        	    array.add(numericValue);
        	}
	        inputStream.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return array;
	}
	
	public static List<Double> getYCoordinatesFromFile(String filePath) {
	    File file = new File(filePath);
	    List<Double> array = new ArrayList<Double>();
	    try {
	        FileInputStream inputStream = new FileInputStream(file);
	        Workbook workBook = new HSSFWorkbook(inputStream);
	        Sheet sheet = workBook.getSheetAt(0);
        	int firstRow = sheet.getFirstRowNum();
        	int lastRow = sheet.getLastRowNum();
        	for (int index = firstRow + 1; index <= lastRow; index++) {
        	    Row row = sheet.getRow(index);
        	    Cell cell = row.getCell(36);
        	    Double numericValue = cell.getNumericCellValue();
        	    array.add(numericValue);
        	}
	        inputStream.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return array;
	}
	
	public static List<String> getTypesAsStringFromFile(String filePath) {
	    File file = new File(filePath);
	    List<String> array = new ArrayList<String>();
	    try {
	        FileInputStream inputStream = new FileInputStream(file);
	        Workbook workBook = new HSSFWorkbook(inputStream);
	        Sheet sheet = workBook.getSheetAt(0);
        	int firstRow = sheet.getFirstRowNum();
        	int lastRow = sheet.getLastRowNum();
        	for (int index = firstRow + 1; index <= lastRow; index++) {
        	    Row row = sheet.getRow(index);
        	    Cell cell = row.getCell(1);
        	    String strValue = cell.getStringCellValue();
        	    array.add(strValue);
        	}
	        inputStream.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return array;
	}
	
	public static int convertTypeToDemand(String type) {
		switch(type) {
			case "Rumputarkastus 1v":
				return TypeConstants.RUMPUTARKASTUS_1V;
			case "Siltatarkastus 1v":
				return TypeConstants.SILTATARKASTUS_1V;
			case "Vaihde 2v huolto":
				return TypeConstants.VAIHDE_2V_HUOLTO;
			case "Opastinhuolto 12kk":
				return TypeConstants.OPASTINHUOLTO_12KK;
			case "Akselinlaskijahuolto 12 kk":
				return TypeConstants.AKSELINLASKIJAHUOLTO_12KK;
			case "Kävelytarkastus 1 v kevät":
				return TypeConstants.KAVELYTARKASTUS_1V_KEVAT;
			case "Kaapit ja kojut 12kk":
				return TypeConstants.KAAPIT_JA_KOJUT_12KK;
			case "Liikennepaikkatarkastus 1v":
				return TypeConstants.LIIKENNEPAIKKATARKASTUS_1V;
		}
		return 0;
	}
	
	public static List<Integer> getDemandsFromFile(List<String> typesAsString) {
		List<Integer> array = new ArrayList<>();
		for (String str: typesAsString) {
			int converted = convertTypeToDemand(str);
			array.add(converted);
		}
		return array;
	}
	
	public static void convertTM35FINToWGS84() {
		GeometryFactory gf = new GeometryFactory();
		try {
			CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326", true);
			CoordinateReferenceSystem inCRS = CRS.decode("EPSG:4326");
			CoordinateReferenceSystem outCRS = CRS.decode("epsg:3067");
			
			MathTransform transform = CRS.findMathTransform(inCRS, outCRS, true);
            Point p = gf.createPoint(new Coordinate(296681.527970467,6755838.498112611));
            Geometry g = JTS.transform(p, transform);
            System.out.println(g.getCoordinate());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
