package nl.tno.dymaes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import nl.tno.dymaes.datastructure.Countsite;
import nl.tno.dymaes.datastructure.Mapping;
import nl.tno.dymaes.datastructure.ODpair;

public class DataReader {


	private Manager manager;
	
	//Input files
	private String OD_filename;
	private String Mapping_filename;
	private String Counts_filename;
	private String Zonesfactor_filename;

	
	//Defaults calculator
	
	int    max_iter                 = 10;
	double convergence_target       = 0.01; 
	double errorsum_target          = 1.0;
	double max_relative_od_grow     = 1.8;     //must be >1.0
	double min_absolute_od_trips    = 10.0;
	double factor_count_total       = 0.1;  //[0-1]
	
	public DataReader(Manager man) {
		
		manager = man;
		
		//Init, can be overwritten by controlfile
		OD_filename      = "od_input_example.txt";
		Mapping_filename = "mapping_example.txt";
		Counts_filename  = "counts_example.txt";
		Zonesfactor_filename = "factor_zones.txt";
		
	}



	public void readControlfile() {
		
		String dir=manager.getInputDir();
		String iniFile=dir+File.separator+"user.ini";
		
		boolean exists = (new File(iniFile)).exists();
		if (exists) {
		    // File or directory exists
				
	    try{
	        Properties p = new Properties();
	        p.load(new FileInputStream(iniFile));
	        
	        OD_filename              = p.getProperty("OD_filename").trim();
	        Counts_filename          = p.getProperty("Counts_filename").trim();
	        Mapping_filename         = p.getProperty("Mapping_filename").trim();
	        Zonesfactor_filename     = p.getProperty("Zonesfactor_filename").trim();
	        	        
    	    max_iter                 = Integer.parseInt( p.getProperty("max_iter").trim());
			convergence_target       = Double.parseDouble( p.getProperty("convergence_target").trim());
			errorsum_target          = Double.parseDouble( p.getProperty("errorsum_target").trim());
			max_relative_od_grow     = Double.parseDouble( p.getProperty("max_relative_od_grow").trim());
			min_absolute_od_trips    = Double.parseDouble( p.getProperty("min_absolute_od_trips").trim());
			factor_count_total       = Double.parseDouble( p.getProperty("factor_count_total").trim());
	        
	        
	        p.list(System.out);
	        }
	      catch (Exception e) {
	        System.out.println(e);
	        }
	      
	      
		    } else
		    {
		    	//In case control file not exists, set defaults
		    	manager.getLogger().write("ERROR. user.ini not found");
		    	
		    }
			
    	 manager.getCalculator().setConvergence_target(convergence_target);
    	 manager.getCalculator().setErrorsum_target(errorsum_target);
    	 manager.getCalculator().setMax_iter(max_iter);
    	 manager.getCalculator().setMax_relative_od_grow(max_relative_od_grow);
    	 manager.getCalculator().setMin_absolute_od_trips(min_absolute_od_trips);
    	 manager.getCalculator().setFactor_count_total(factor_count_total);
	    }



	/**
	 * Read OD file
	 */
	public void readOD() {
		
		String dir=manager.getInputDir();
		String odFile=dir+File.separator+OD_filename;
		Hashtable<Integer, ODpair> odHashtable = manager.getOdHashTable();
		
        File aFile = new File(odFile);	
	    String line;
	    int numDep=0;
		try {
		      //use buffering, reading one line at a time
		      //FileReader always assumes default encoding is OK!
		      BufferedReader input =  new BufferedReader(new FileReader(aFile));
		      try {
		    	  input.readLine();
		    	  while (( line = input.readLine()) != null){
		    		 		    		 
		    		 String [] temp = null;
			         temp = line.split(" +");
			         int origin      = Integer.valueOf(temp[0]);
			         int destination = Integer.valueOf(temp[1]);
			         int timeslice   = Integer.valueOf(temp[2]);
			         double trips    = Double.valueOf(temp[3]);
			         
			         
			         //Check if object o-d exists
			         int odkey = origin*10000 + destination;
			         
			         if (odHashtable.containsKey(odkey)){
			        	 //Exists
			        	 ODpair thisODpair = odHashtable.get(odkey);          //Get existing object 
			        	 thisODpair.initTimeslicedata(timeslice);
			        	 thisODpair.setOriginaltrips(timeslice, trips);       //Set its trips at timeslice T
			        			        	 
			         } else {
			        	 // Is new
			        	 ODpair thisODpair = new ODpair(origin,destination);  //Create new object
			        	 thisODpair.initTimeslicedata(timeslice);
			        	 thisODpair.setOriginaltrips(timeslice, trips);       //Set its trips at timeslice T	
				         
				         odHashtable.put(thisODpair.getKey(), thisODpair);    //Let us find the object later on
			        	 
			         }
			         
			         numDep =Math.max(numDep, timeslice);

		    	  }
		
		      }
		      finally {
		        input.close();
		      }
		      
		      
		    }
		    catch (IOException ex){
		      ex.printStackTrace();
		    }
	         manager.getLogger().write("Number of od departure intervals :"+numDep);
	         manager.getLogger().write("Number of od pairs :"+odHashtable.size());
	         manager.getCalculator().setNum_departure_intervals(numDep);
	}



	/**
	 * Read mapping file (describes proportion of od_flow departed at slice T using the countsite-link at count interval Z)
	 */
	public void readMapping() {
		String dir=manager.getInputDir();
		String odFile=dir+File.separator+Mapping_filename;
		Hashtable<Integer, Mapping> mappingHashtable = manager.getMappingHashTable();
		Hashtable<Integer, ODpair> odHashtable = manager.getOdHashTable();
		Hashtable<Integer, Countsite> countHashtable = manager.getCountHashTable();
		
        File aFile = new File(odFile);	
	    String line;
		try {
		      //use buffering, reading one line at a time
		      //FileReader always assumes default encoding is OK!
		      BufferedReader input =  new BufferedReader(new FileReader(aFile));
		      try {
		    	  input.readLine();
		    	  while (( line = input.readLine()) != null){
			         int origin = -1;
			         int destination= -1; 
			         int timeslice = -1;
			         int linknumber = -1;
			         int countinterval = -1;
			         double percentage = 0;
		    		 String [] temp = null;
			         temp = line.split(" +"); //Plus means multiple spaces as 1
			        
			         
			         if (temp.length<6) {
			        	 System.out.println("Error in line: "+Integer.valueOf(temp[0])+" "+Integer.valueOf(temp[1])+" "+Integer.valueOf(temp[2])+" "+Integer.valueOf(temp[3]));

			         } else {
			          origin        = Integer.valueOf(temp[0]);
			          destination   = Integer.valueOf(temp[1]);
			          timeslice     = Integer.valueOf(temp[2]);
			          linknumber    = Integer.valueOf(temp[3]);
			          countinterval = Integer.valueOf(temp[4]);
			          percentage = Double.valueOf(temp[5]);  // % of TOTAL ODFLOW in departure slice T is observed in countinterval Z at this link 
			          }
			         
			         ODpair thisODpair          = null;
			         Countsite thisCountsite    = null;
			         
			         //Get ODpair object
			         int odkey = origin*10000 + destination;
			         if (odHashtable.containsKey(odkey)) {
			           thisODpair = odHashtable.get(odkey);
			         } else {
			        	 System.out.println("Error: mapping indicates the od pair is used but not found in od matrix");  
			         }
			         
			         //Get Countsite object
			         boolean ignoreCountsite = false;
			         int mappingkey=0;
			         if ((thisODpair!=null) && (countHashtable.containsKey(linknumber))) {
				           thisCountsite = countHashtable.get(linknumber);
				           mappingkey = thisODpair.getKey()*100000 + thisCountsite.getId()*100 + timeslice; //max 999 countsites //max 99 slices
					         
				         } else {
				        	 ignoreCountsite = true;
				        	 //System.out.println("Error: mapping indicates the countsite linknumber is used but not found in count file read");  
				         }
			         
			         
			         
			         //Store as object
			         if ((thisODpair!=null) && (thisCountsite!=null) && (ignoreCountsite==false)) {
			        	 
			        	 Mapping thisMapping=null;
			        	 
			        	//Check if exist o-d-t-l
			        	if  (mappingHashtable.containsKey(mappingkey)){
			        		//Exists
			        		thisMapping = mappingHashtable.get(mappingkey);
			        		
			        	}  else {
			        		//Is new
			        		thisMapping = new Mapping(thisODpair, thisCountsite, timeslice);
			        		
			        		
				            mappingHashtable.put(mappingkey, thisMapping);
			        		
			        		
			        	}
			        	
			        	//Store percentage for this mapping (assume no duplicate entries)
			        	thisMapping.setPercentage(countinterval, percentage);
			            
			            
			         } else
			         {
			        	 //System.out.println("Error: mapping record not created");
			         }

		    	  }
		
		      }
		      finally {
		        input.close();
		      }
		      
		      
		    }
		    catch (IOException ex){
		      ex.printStackTrace();
		    }
		
		
	}



	/**
	 * Read count file
	 */
	public void readCounts() {
				
		String dir=manager.getInputDir();
		String odFile=dir+File.separator+Counts_filename;
		Hashtable<Integer, Countsite> countsHashtable = manager.getCountHashTable();
		
        File aFile = new File(odFile);	
	    String line;
	    int numObs=0;
	    int numCountsites=0;
		try {
		      //use buffering, reading one line at a time
		      //FileReader always assumes default encoding is OK!
		      BufferedReader input =  new BufferedReader(new FileReader(aFile));
		      try {
		    	  input.readLine();
		    	  while (( line = input.readLine()) != null){
		    		 		    		 
		    		 String [] temp = null;
			         temp = line.split(" +");
			         int countsiteid      = Integer.valueOf(temp[0]);
			         int linknumber       = Integer.valueOf(temp[1]);
			         int numobservations  = Integer.valueOf(temp[2]);
			         double reliability   = Double.valueOf(temp[3+numobservations]); 
			         
			         Countsite thisCountsite = new Countsite(countsiteid,linknumber,reliability);
			         // Store the individual countvalues for each observation interval			         
			         for (int i = 1; i <= numobservations; i++) {
						double countvalue = Double.valueOf(temp[2+i]);
						thisCountsite.setCountvalue(i, countvalue);
						//System.out.println(2+i+" "+i+" "+countvalue);
					 }
			         countsHashtable.put(linknumber, thisCountsite);
			         numCountsites=numCountsites+1;
			         numObs=Math.max(numObs, numobservations);
			         
		    	  }
		
		      }
		      finally {
		        input.close();
		      }
		      
		      
		    }
		    catch (IOException ex){
		      ex.printStackTrace();
		    }
		    
		  //For calculation we use the defined number of observation intervals
	         manager.getLogger().write("Number of count sites :"+numCountsites);
	         manager.getLogger().write("Number of count observation intervals :"+numObs);
	         manager.getCalculator().setNum_count_sites(numCountsites);
	         manager.getCalculator().setNum_count_intervals(numObs);
		
	}
	
	/**
	 * Read zonal growth factor file
	 */
	public void readZonesfactors() {
				
		String dir=manager.getInputDir();
		String zoneFile=dir+File.separator+Zonesfactor_filename;
		Hashtable<Integer, Double> zonefactorTable = manager.getZonefactorTable();
		int numZonalrestrictions = 0;
        File aFile = new File(zoneFile);	
	    String line;
	    try {
		      //use buffering, reading one line at a time
		      //FileReader always assumes default encoding is OK!
		      BufferedReader input =  new BufferedReader(new FileReader(aFile));
		      try {
		    	  input.readLine(); //header
		    	  while (( line = input.readLine()) != null){
		    		 		    		 
		    		 String [] temp = null;
			         temp = line.split(" +");
			         int zoneid            = Integer.valueOf(temp[0]);
			         double growthfactor   = Double.valueOf(temp[1]); 
			         
			         if (growthfactor<1.0) {
			        	 manager.getLogger().write("Error: Specified restricted max zonal growthfactor must be > 1.0. Ignored! ");
			         } else
			         {
			           zonefactorTable.put(zoneid, growthfactor);
			           numZonalrestrictions = numZonalrestrictions +1;
			         }
		    	  }
		
		      }
		      finally {
		        input.close();
		      }
		      
		      
		    }
		    catch (IOException ex){
		      ex.printStackTrace();
		    }
	
	
		    manager.getLogger().write("Number of zonal growth factor restrictions :"+numZonalrestrictions);
	    
	    
	}
			    
	
}
