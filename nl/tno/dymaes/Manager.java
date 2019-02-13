package nl.tno.dymaes;

import java.io.File;
import java.util.Hashtable;

import nl.tno.dymaes.datastructure.Countsite;
import nl.tno.dymaes.datastructure.ODpair;
import nl.tno.dymaes.datastructure.Mapping;

public class Manager {
	
	private LogWriter logger;
	private String inputDir;
	private String outputDir;		
	private DataReader dataReader;
	private DataWriter dataWriter;
	private Calculator calculator;
	
	//Structures to store data
	private Hashtable<Integer, ODpair>    odHashTable      = new Hashtable<Integer, ODpair>();
	private Hashtable<Integer, Countsite> countHashTable   = new Hashtable<Integer, Countsite>();
	private Hashtable<Integer, Mapping>   mappingHashTable = new Hashtable<Integer, Mapping>();
	private Hashtable<Integer, Double>    zonefactorTable = new Hashtable<Integer, Double>();


	
	
	
	/**
	 * Constructor
	 */
	public Manager() {

		
		//Initialize
		initializeModel();
			
		//Classes with methods
		this.dataReader      = new DataReader(this);
		this.logger          = new LogWriter(this);
		this.calculator      = new Calculator(this);
		this.dataWriter      = new DataWriter(this);

		
		//Run
		runModel();
	}


	private void initializeModel() {

		String inputDirName  = "input";
		String outputDirName = "output";
		
		inputDir = this.getCurrentPath()+File.separator+inputDirName;
		new File(inputDir).mkdirs();
	
		outputDir = this.getCurrentPath()+File.separator+outputDirName;
		new File(outputDir).mkdirs();
		
	}


	/**
	 *  Model runs
	 */
	public void runModel(){

		logger.write("Model started");
	
		////       Read input        ////
		
		dataReader.readControlfile();
		dataReader.readOD();
		dataReader.readZonesfactors();
		dataReader.readCounts();
		dataReader.readMapping();
	
		
		logger.write("All data read");
		
		/////       CalculateNewOD   ////
		calculator.doEstimate();
		
		
		
	}



    public String getCurrentPath(){
	File dir = new File (".");
	String path = null; 
	try {
		path = dir.getCanonicalPath();
	   	
	  }
	catch(Exception e) {
		e.printStackTrace();
	  }
	return path;
}

	public String getInputDir() {
		return inputDir;
	}
	public String getOutputDir() {
		return outputDir;
	}
	public Hashtable<Integer, ODpair> getOdHashTable() {
		return odHashTable;
	}
	public Hashtable<Integer, Countsite> getCountHashTable() {
		return countHashTable;
	}
	public Hashtable<Integer, Mapping> getMappingHashTable() {
		return mappingHashTable;
	}
	public Hashtable<Integer, Double> getZonefactorTable() {
		return zonefactorTable;
	}
	public Calculator getCalculator() {
		return calculator;
	}
	public DataWriter getDataWriter() {
		return dataWriter;
	}
	public LogWriter getLogger() {
		return logger;
	}


}
