package nl.tno.dymaes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import nl.tno.dymaes.datastructure.Countsite;
import nl.tno.dymaes.datastructure.ODpair;

public class DataWriter {

	
	private Manager manager;
	private Calculator calculator;
	
	public DataWriter(Manager manager) {
		this.manager = manager;
		this.calculator = manager.getCalculator();
	}

	/**
	 * Get results per countsite
	 */
	public void doWriteResultsCountsite() {
		
		int num_count_intervals = calculator.getNum_count_intervals();
		Hashtable<Integer, Countsite> countsiteHashtable = manager.getCountHashTable();

		String dir=manager.getOutputDir();
		String outFile=dir+File.separator+"output_count_statistics.csv";
		
		BufferedWriter export;
		try {
			
			export = new BufferedWriter (new FileWriter(outFile));
			//Header
			export.write("countsiteid,  ");
			for (int i = 1; i <= num_count_intervals; i++) {
				export.write(","+i); 
			
			}
			
			
			
			Enumeration e = countsiteHashtable.elements();
		    while(e.hasMoreElements()) {
		      
		      Countsite csite = (Countsite) e.nextElement();
		      Hashtable<Integer, Double> countvaluesHashtable = csite.getCountTable();
		      
		      //Countsite
		      export.newLine();
		      export.write(csite.getId()+", countvalues");
		      for (int i = 1; i <= num_count_intervals; i++) {
		    	  double count_value         = csite.getCountvalue(i);
		    	  export.write(","+count_value);
		      }
		      export.newLine();
		      export.write(csite.getId()+", mapped");
		      for (int i = 1; i <= num_count_intervals; i++) {
		    	  double intensity_original  = Math.round(csite.getIntensityvalue(i));
		    	  export.write(","+intensity_original);
		      }
		      export.newLine();
		      export.write(csite.getId()+", estimated");
		      for (int i = 1; i <= num_count_intervals; i++) {
		    	  double intensity_last_iter = Math.round(csite.getNewIntensityvalue(i));
		    	  export.write(","+intensity_last_iter);
		    	  
		        	  
		      }
		      export.newLine();
		      export.write(csite.getId()+", %mapped");
		      for (int i = 1; i <= num_count_intervals; i++) {
		    	  double count_value         = csite.getCountvalue(i);
		    	  double intensity_original  = csite.getIntensityvalue(i);
		    	  double perc                = Math.round(100* (intensity_original-count_value)/count_value);
		    	  export.write(","+perc);
		      }
		      export.newLine();
		      export.write(csite.getId()+", %estimated");
		      for (int i = 1; i <= num_count_intervals; i++) {
		    	  double count_value         = csite.getCountvalue(i);
		    	  double intensity_last_iter = csite.getNewIntensityvalue(i);
		    	  double perc_last_iter      = Math.round(100* (intensity_last_iter-count_value)/count_value);
		    	  export.write(","+perc_last_iter);
		      }
		      
		      
		    }
			
		

			
			export.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	
	/**
	 * EXport OD matrix
	 */
	public void doExportNewODMatrix() {
		
		int num_departure_intervals = calculator.getNum_departure_intervals();
		Hashtable<Integer, ODpair> odHashtable = manager.getOdHashTable();
		
		String dir=manager.getOutputDir();
		String outFile=dir+File.separator+"estimated_od.txt";
		
		BufferedWriter export;
		try {
			
			export = new BufferedWriter (new FileWriter(outFile));
			export.write("Origin Destination TimeSliceNumber Value");
			
		    Enumeration<ODpair> e = odHashtable.elements();
	        while(e.hasMoreElements()) {
	           ODpair od = (ODpair) e.nextElement();
	      
	           for (int k = 1; k <= num_departure_intervals; k++) {
                   if (od.getEstimatedtrips(k)>0){
                	   export.newLine();
                	   double value = (Math.ceil(10000 * od.getEstimatedtrips(k)))/10000;
                	   
	        	       export.write(od.getFromID()+" "+od.getToID()+" "+k+" "+value);
                   }
	           }
	      
	        }
			export.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		
		//Export ONLY the od-cells that have been changed
		outFile=dir+File.separator+"estimated_od_growthfactor.txt";

		try {
			
			export = new BufferedWriter (new FileWriter(outFile));
			export.write("Origin Destination TimeSliceNumber Growthfactor");
			
		    Enumeration<ODpair> e = odHashtable.elements();
	        while(e.hasMoreElements()) {
	           ODpair od = (ODpair) e.nextElement();
	      
	           for (int k = 1; k <= num_departure_intervals; k++) {
                   if (od.getEstimatedtrips(k)>0){
                	   
                	   double factor = od.getEstimatedtrips(k) /od.getOriginaltrips(k);
                	   double value = (Math.ceil(10000 * factor))/10000;
                	   if ((value<1) || (value>1)) {
                		  export.newLine();
	        	          export.write(od.getFromID()+" "+od.getToID()+" "+k+" "+value);
                	   }
                   }
	           }
	      
	        }
			export.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	

		
	}
	
		

		
}
