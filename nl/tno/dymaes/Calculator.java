package nl.tno.dymaes;

import java.util.Enumeration;
import java.util.Hashtable;

import nl.tno.dymaes.datastructure.Countsite;
import nl.tno.dymaes.datastructure.Mapping;
import nl.tno.dymaes.datastructure.ODpair;

public class Calculator {

	
	private Manager manager;
	private LogWriter logger;
	
	
	// variables based on input file
	private int num_departure_intervals;
	private int num_count_intervals;
	private int num_count_sites;
	
	
	// settings estimation
	private int    max_iter;
	private double convergence_target; 
	private double errorsum_target;
	private double max_relative_od_grow;
	private double min_absolute_od_trips;
	private double factor_count_total;
	
	// internal variables
	private double errorsum_last_iteration  = 100000000; 
	private double convergence_actual;       
	
	
	/**
	 * Constructor
	 * @param man
	 */
	public Calculator(Manager man) {
		this.manager = man;
		this.logger  = man.getLogger();
		
	}

	public void doEstimate() {
		
		doInitializeEstimatedOD();             // Set start estimation with copy of input matrix
		
		doInitializeIntensityCountsite();      // Set all intensities at zero.
		
		doCalculateMappingIntensities(0);      // Determine intensities at countsites from mapping proportions and initial od matrix
				
		doCopyNewIntensityCountsite();         // Set start value iterations: use provided intensities from mapping
		
		doCalculateTotalError();
		
		doUpdateODpairCountvalues(0);
		
		/*   start iterative process  */
		
		int num_iter=max_iter;
		
		for (int iter = 1; iter <= num_iter; iter++) {
			logger.write("Iteration "+iter);
			
			doUpdateODpairCountMappingIntensities(iter);
			
			doApplyGrowfactor(iter);
			
			doUdateNewODMatrix();
			
			doResetNewIntensityCountsite();
			
			doCalculateMappingIntensities(iter);
			
			doCalculateTotalError();
			
			
			// Stop criterium
			if ((Math.abs(convergence_actual)<convergence_target) || (errorsum_last_iteration<errorsum_target) || (iter==num_iter) ) {
				iter=num_iter;
				logger.write("Stop criteria reached");
				manager.getDataWriter().doWriteResultsCountsite();
				manager.getDataWriter().doExportNewODMatrix();
			}
			
		}
		
		
	}
	
	
	
	
	
	

	

	/**
	 * Update difference between counts versus new estimated intensities. Convergence test.
	 * Done after each iteration.
	 */
	private void doCalculateTotalError() {
		logger.write("Calculate weighted error and convergence");
		 double errorsum =0;
		 double num_obs  =0;
		 Hashtable<Integer, Countsite> countsiteHashtable = manager.getCountHashTable();

			Enumeration f = countsiteHashtable.elements();
		    while(f.hasMoreElements()) {
		      double count_total =0;
		      double estim_intens_total =0;
		      
		      Countsite csite = (Countsite) f.nextElement();
		      Hashtable<Integer, Double> countvaluesHashtable = csite.getCountTable();
	        //loop over all observation count intervals
		      for (int h = 1; h <= num_count_intervals; h++) {
		    	  
		    	  count_total        = count_total        + csite.getCountvalue(h); 
		    	  estim_intens_total = estim_intens_total + csite.getNewIntensityvalue(h); 
		    	  //System.out.println(csite.getId()+" obs interval "+i+" Countvalue:" +csite.getCountvalue(i) + " Mapping intensity "+csite.getNewIntensityvalue(i));
		    	  
		    	  if (csite.getCountvalue(h)>0 && csite.getNewIntensityvalue(h)>0){
		    	    double error = Math.pow( (csite.getReliability()*csite.getCountvalue(h) - csite.getReliability()*csite.getNewIntensityvalue(h)), 2);
		    	    errorsum = errorsum + error;
		    	    num_obs  = num_obs+1;
		    	  }
		    	  
		    	  
		    	  
			  }
		      //Update new countsite totals
		      csite.setCount_total_all_obs(count_total);
		      csite.setIntensity_total_all_obs(estim_intens_total);
		      
		  }
		    
		  double error_change =  errorsum_last_iteration - errorsum;
		    
		  convergence_actual  = error_change / errorsum;
		  errorsum_last_iteration = errorsum;
		  logger.write("Convergence_gap:"+convergence_actual+" Error_sum:"+Math.sqrt(errorsum_last_iteration));
			
		  //System.out.println(" Errorsum ="+errorsum + " dCONVERGENCE = " + convergence_actual);
	}

	/**
	 * Calculate intensity at countsites using the mapping and start matrix or estimated matrix
	 * @param iter
	 */
	private void doCalculateMappingIntensities(int iter) {
		//Loop over alle Mapping records en bereken voor alle countsites & countobsinterval intensity
		logger.write("Estimate mapped intensities at countsites");
		Hashtable<Integer, Mapping> mappingHashtable = manager.getMappingHashTable();
		Enumeration<Mapping> e = mappingHashtable.elements();
	    while(e.hasMoreElements()) {
	      Mapping maprecord = (Mapping) e.nextElement();
	      Hashtable<Integer, Double> percentageTable = maprecord.getPercentageTable();
	      //System.out.println(maprecord.getMapping_key()+" ");
    	  Countsite csite          = maprecord.getCountsite();
    	  ODpair    od             = maprecord.getOdpair();
    	  int od_timeinterval      = maprecord.getOd_timeinterval();
    	  double odflow;
    	  double cum_flow;
	      //loop over alle count obs intervals
	      for (int h = 1; h <= num_count_intervals; h++) {
	    	  //System.out.println(" "+maprecord.getMapping_key()+" "+i+" "+maprecord.getPercentage(i));
	    	  double percentage        = maprecord.getPercentage(h);
	    	  
	    	  if (iter==0) {
	    		  odflow  = maprecord.getOdpair().getOriginaltrips(od_timeinterval);
	    	  } else {
	    		  odflow  = maprecord.getOdpair().getEstimatedtrips(od_timeinterval);
	    	  }
	    	  
	    	  double odflow_portion    = odflow * percentage;
	    	  
	    	  cum_flow          = 0;
	    	  //System.out.println(i+ " odflow ="+odflow);
  	    	  //System.out.println(i+ " odflowportion ="+odflow_portion);

	    	  if (iter==0) {
	    	      cum_flow          = csite.getIntensityvalue(h);
	    	      //System.out.println(i+ " cumflow ="+cum_flow);
	    	      if (csite.getCountvalue(h)>0) {
	    	        csite.setIntensityvalue(h, (cum_flow+odflow_portion) );    //Keep initial for statics
	    	      }
	    	  } else {
	    		  cum_flow          = csite.getNewIntensityvalue(h);
	    		  //System.out.println(i+ " cumflow ="+cum_flow);
	    		  if (csite.getCountvalue(h)>0) {
	    		    csite.setNewIntensityvalue(h, (cum_flow+odflow_portion) ); //each iteration updated
	    		  }
	    	  }
	    	  
  		 }  //end loop obs intervals h
	      
	    } // end loop maprecords
		
	}
	
	
private void doUpdateODpairCountvalues(int iter) {
		
		logger.write("Update OD-pair objects with countsite countvalues");
		Hashtable<Integer, Mapping> mappingHashtable = manager.getMappingHashTable();
		Enumeration<Mapping> e = mappingHashtable.elements();
	    while(e.hasMoreElements()) {
	      Mapping maprecord = (Mapping) e.nextElement();
	      Hashtable<Integer, Double> percentageTable = maprecord.getPercentageTable();
    	  Countsite csite          = maprecord.getCountsite();
    	  ODpair    od             = maprecord.getOdpair();
    	  int od_timeinterval      = maprecord.getOd_timeinterval();
    	  double odflow;
    	  double cum_flow;
	      //loop over alle count obs intervals
	      for (int h = 1; h <= num_count_intervals; h++) {
	    	  double percentage        = maprecord.getPercentage(h);
	    	  cum_flow          = 0;
	    	  if (iter==0) {
	    	      cum_flow          = csite.getIntensityvalue(h);
	    	  } else {
	    		  cum_flow          = csite.getNewIntensityvalue(h);
	    	  }
    
             doUpdateOdpairWithCountvalues(h, od_timeinterval, percentage, csite, od, cum_flow); 
			 }  //end loop obs intervals h
	      
	    } // end loop maprecords
		
	}




	private void doUpdateODpairCountMappingIntensities(int iter) {
		
		logger.write("Update OD-pair objects with countsite mapping");
		Hashtable<Integer, Mapping> mappingHashtable = manager.getMappingHashTable();
		Enumeration<Mapping> e = mappingHashtable.elements();
	    while(e.hasMoreElements()) {
	      Mapping maprecord = (Mapping) e.nextElement();
	      Hashtable<Integer, Double> percentageTable = maprecord.getPercentageTable();

    	  Countsite csite          = maprecord.getCountsite();
    	  ODpair    od             = maprecord.getOdpair();
    	  int od_timeinterval      = maprecord.getOd_timeinterval();
    	  double odflow;
    	  double cum_flow;
	      //loop over alle count obs intervals
	      for (int h = 1; h <= num_count_intervals; h++) {
	    	  double percentage        = maprecord.getPercentage(h);
	    	  cum_flow          = 0;

	    	  if (iter==0) {
	    	      cum_flow          = csite.getIntensityvalue(h);
	    	  } else {
	    		  cum_flow          = csite.getNewIntensityvalue(h);
	    	  }
	    	  
             doUpdateOdpairWithMappedIntensity(h, od_timeinterval, percentage, csite, od, cum_flow);
             
			 }  //end loop obs intervals h
	      
	    } // end loop maprecords
		
	}
	
	
	/**
	 * Cumulative storage per o-d-k of mapped countsite intensity
	 * @param h  observation interval countsite
	 * @param k  departure time slice od pair
	 * @param percentage the percentage of od flow at countsite flow 
	 * @param csite countsite object
	 * @param od odpair object
	 */
	private void doUpdateOdpairWithMappedIntensity(int h, int k, double percentage, Countsite csite, ODpair od, double intens) {
  	  
  	  if (percentage>0 ) {
	    		double intensity_mapped= percentage * intens;
	    		
	    		//For this k, how much is the (averaged) contribution to total previous mapped intensity (Standardize over num k intervals and x countsites)
  	           	
	    		double intensity_last_iter = (csite.getIntensity_total_all_obs()/num_departure_intervals)/num_count_sites;
	    		double countvalue = (csite.getCount_total_all_obs()/num_departure_intervals)/num_count_sites;
  	        	
  	        	double factor=factor_count_total; //Contribution shape average count. In case=1 then target is that total counted is equal to total estimated, profile count over time is ignored
  	        
  	        	if (intensity_last_iter>0 && countvalue>0){
	    		    od.setIntensitysum(k, od.getIntensitysum(k) + factor * intensity_last_iter  + (1-factor) * intensity_mapped);
  	        	}
	      }
		
	}

	private void doUpdateOdpairWithCountvalues(int h, int k, double percentage, Countsite csite, ODpair od, double intens) {
	  	  
	  	  if (percentage>0 ) {
		    		double count_mapped= percentage * csite.getCountvalue(h);
		    		
		    		//For this k, how much is the (averaged) contribution to total previous mapped intensity (Standardize over num k intervals and x countsites)
	  	           	
		    		double intensity_last_iter = (csite.getIntensity_total_all_obs()/num_departure_intervals)/num_count_sites;
		    		double countvalue = (csite.getCount_total_all_obs()/num_departure_intervals)/num_count_sites;
	  	        	
	  	        	double factor=factor_count_total; //Contribution shape average count. In case=1 then target is that total counted is equal to total estimated, profile count over time is ignored
	  	        
	  	        	if (intensity_last_iter>0 && countvalue>0){
		    		  od.setCountssum(k, od.getCountsum(k) + factor * countvalue  + (1-factor) * count_mapped);
	  	        	}
		      }
			
		}
	
	/**
	 * Initialize 
	 * Only for initial iteration 0
	 */
	private void doInitializeEstimatedOD() {
		logger.write("Iteration 0 (initialization)");
		Hashtable<Integer, ODpair> odHashtable = manager.getOdHashTable();
		Enumeration<ODpair> e = odHashtable.elements();
	    while(e.hasMoreElements()) {
	      ODpair od = (ODpair) e.nextElement();
	      
	      //loop over all departure intervals
	      //System.out.println("Start matrix");
	      double odpair_sum_original = 0;
	      for (int k = 1; k <= num_departure_intervals; k++) {
	        od.setEstimatedtrips(k, od.getOriginaltrips(k));
	        od.setNewEstimatedtrips(k, 0); 
	        od.setCountssum(k,0); //is done only here
	        od.setIntensitysum(k,0);
	        od.setIntensitysumPreviousIteration(k,0);
	        odpair_sum_original = odpair_sum_original + od.getOriginaltrips(k);
	        
	        //System.out.println(od.getFromID()+" "+od.getToID()+" "+k+" "+od.getEstimatedtrips(k));
	      }
	      od.setOriginaltrips_allslices(odpair_sum_original);
	      
	  }
	}
	
	
	/**
	 * Update od matrix with results of iteration, reset 'cumulative' to 0 for next iteration  
	 * For 1st and further iterations
	 */
	private void doUdateNewODMatrix() {
		logger.write("Update matrix");
		Hashtable<Integer, ODpair> odHashtable = manager.getOdHashTable();
		Enumeration<ODpair> e = odHashtable.elements();
	    while(e.hasMoreElements()) {
	      ODpair od = (ODpair) e.nextElement();
	      
	      //loop over all departure intervals, replace Previous by New (last iteration) values
	      //System.out.println("Updated matrix");
	      for (int k = 1; k <= num_departure_intervals; k++) {
	        od.setEstimatedtrips(k, od.getNewEstimatedtrips(k));
	        od.setNewEstimatedtrips(k,0); //reset for next iter
	        
	      
	        
	        od.setIntensitysumPreviousIteration(k, od.getIntensitysum(k) );
	        od.setIntensitysum(k,0);
	        
	        
	        //System.out.println(od.getFromID()+" "+od.getToID()+" "+k+" "+od.getEstimatedtrips(k));
	      }
	      
	      
	  }
		
	}
	
	
	/**
	 * Initialize count site intensities (startvalues and estimated)
	 * Only for inital iteration 0
	 */
	private void doInitializeIntensityCountsite() {
		Hashtable<Integer, Countsite> countsiteHashtable = manager.getCountHashTable();

		Enumeration e = countsiteHashtable.elements();
	    while(e.hasMoreElements()) {
	      Countsite csite = (Countsite) e.nextElement();
	      Hashtable<Integer, Double> countvaluesHashtable = csite.getCountTable();
	      
	      //System.out.println(csite.getId()+" "+csite.getCountvalue(1));
	      
	      //loop over all observation count intervals
	      for (int i = 1; i <= num_count_intervals; i++) {
	    	  
	    	  csite.setIntensityvalue(i, 0);
	    	  csite.setNewIntensityvalue(i, 0);
		  }
	      
	  }
		
	}

	
	private void doResetNewIntensityCountsite() {
		Hashtable<Integer, Countsite> countsiteHashtable = manager.getCountHashTable();

		Enumeration e = countsiteHashtable.elements();
	    while(e.hasMoreElements()) {
	      Countsite csite = (Countsite) e.nextElement();
	      Hashtable<Integer, Double> countvaluesHashtable = csite.getCountTable();
	      
   
	      //loop over all observation count intervals
	      for (int i = 1; i <= num_count_intervals; i++) {
	    	  
		    	  csite.setNewIntensityvalue(i, 0);
		  }
	      
	      
	  }
		
	}
	
	/**
	 * Copy calculated start intensities to estimated intensities
	 * Only for inital iteration 0
	 */
	private void doCopyNewIntensityCountsite() {
		Hashtable<Integer, Countsite> countsiteHashtable = manager.getCountHashTable();

		Enumeration e = countsiteHashtable.elements();
	    while(e.hasMoreElements()) {
	      Countsite csite = (Countsite) e.nextElement();
	      Hashtable<Integer, Double> countvaluesHashtable = csite.getCountTable();
      	      
	      
	      //loop over all observation count intervals
	      for (int i = 1; i <= num_count_intervals; i++) {
	    	  
	    	  //Update 'new', since we will use the 'new' to calculate the next estimate
	    	  csite.setNewIntensityvalue(i, csite.getIntensityvalue(i));
	    	  //System.out.println(csite.getId()+" obs interval "+i+" Countvalue:" +csite.getCountvalue(i) + " Mapping intensity "+csite.getNewIntensityvalue(i));
		  }
	      
	  }
		
	}
	
	
	

	/**
	 *  Apply growfactor for o-d-k pairs. Filter for constraints.
	 */
	private void doApplyGrowfactor(int iter) {
		logger.write("Estimate new od matrix: apply grow factor");
		Hashtable<Integer, ODpair> odHashtable = manager.getOdHashTable();
		Enumeration<ODpair> e = odHashtable.elements();
		//loop over all od-pairs
	    while(e.hasMoreElements()) {
	      ODpair od = (ODpair) e.nextElement();
	      
	      //loop over all departure intervals k within one od-pair
	      for (int k = 1; k <= num_departure_intervals; k++) {
	    	    
	    	  
	    	  //Target to match all counsite-departure slice k totals
	          double countsum     = od.getCountsum(k);
	          double intensitysum = od.getIntensitysum(k);
	          double intensitysumprevious = intensitysum;
	          
	          if (iter>1) {
	           intensitysumprevious = od.getIntensitysumPreviousIteration(k);
	          }
	          
	          
	    	  if (countsum>0 && intensitysum>0) {
	    		  double smoothfactor           = 1.0/iter;
	    	      double od_change_factor       = countsum / ( (smoothfactor)*intensitysum + (1-smoothfactor)*intensitysumprevious );
	    	      double intensity_last_iter    = od.getEstimatedtrips(k);
	    	      double intensity_this_iter    = od_change_factor * intensity_last_iter;
	    	      
	    	      intensity_this_iter    = doCheckRelativeGrowthLimit(od, k, intensity_this_iter);
	    	      intensity_this_iter    = doCheckAbsoluteMinimum(od, k, intensity_this_iter);
	    	      
	    	      od.setNewEstimatedtrips(k,intensity_this_iter);
	    	  }	else
	    	  {
	    		  //In case no counts or no mapping intensity found keep original trip value
	    		  od.setNewEstimatedtrips(k,od.getEstimatedtrips(k));
	    	  }
	      }
	    }
	}
	
	private double doCheckAbsoluteMinimum(ODpair od, int od_timeinterval,	double intensity_this_iter) {
		double result = intensity_this_iter;
		
		double min_tripvalue = min_absolute_od_trips;
			
		double original_trips =	od.getOriginaltrips(od_timeinterval);
		
		if (original_trips<=min_tripvalue){
			result=original_trips;
		}

		return result;
	}

	private double doCheckRelativeGrowthLimit(ODpair od, int od_timeinterval, double intensity_this_iter) {
		double result = intensity_this_iter;
		double factor_zonalrestriction = max_relative_od_grow;
		
		//Check is there is a zonal restriction specified for trips to or from the od-pair
		int orig = od.getFromID();
		int dest = od.getToID();
		
		Hashtable<Integer, Double> zonefactorTable = manager.getZonefactorTable();
		
		if (zonefactorTable.containsKey(orig)) {
			double orig_factor_zonalrestriction = zonefactorTable.get(orig);
			
			if (orig_factor_zonalrestriction<factor_zonalrestriction) {
				factor_zonalrestriction = orig_factor_zonalrestriction;
			}
		}
		if (zonefactorTable.containsKey(dest)) {
			double dest_factor_zonalrestriction = zonefactorTable.get(dest);
			
			if (dest_factor_zonalrestriction<factor_zonalrestriction) {
				factor_zonalrestriction = dest_factor_zonalrestriction;
			}
		}
		
		
		// Apply max / min value growth factor
		double max_value = (double)factor_zonalrestriction * od.getOriginaltrips(od_timeinterval);
		double min_value = (1/(double)factor_zonalrestriction) * od.getOriginaltrips(od_timeinterval);
		
		if (intensity_this_iter>max_value){
			result=max_value;
		}
		if (intensity_this_iter<min_value){
			result=min_value;
		}
		
		


		
		return result;
	}

		
	public int getNum_count_intervals() {
		return num_count_intervals;
	}
	public int getNum_count_sites() {
		return num_count_sites;
	}
	public int getNum_departure_intervals() {
		return num_departure_intervals;
	}
	public double getConvergence_actual() {
		return convergence_actual;
	}
	public double getErrorsum_last_iteration() {
		return errorsum_last_iteration;
	}
	public void setNum_count_intervals(int num_count_intervals) {
		this.num_count_intervals = num_count_intervals;
	}
	public void setNum_count_sites(int numCountsites) {
		this.num_count_sites = numCountsites;		
	}
	public void setNum_departure_intervals(int num_departure_intervals) {
		this.num_departure_intervals = num_departure_intervals;
	}
	public void setMin_absolute_od_trips(double min_absolute_od_trips) {
		this.min_absolute_od_trips = min_absolute_od_trips;
	}
	public void setMax_relative_od_grow(double max_relative_od_grow) {
		this.max_relative_od_grow = max_relative_od_grow;
	}
	public void setMax_iter(int max_iter) {
		this.max_iter = max_iter;
	}
	public void setErrorsum_target(double errorsum_target) {
		this.errorsum_target = errorsum_target;
	}
	public void setConvergence_target(double convergence_target) {
		this.convergence_target = convergence_target;
	}

	public void setFactor_count_total(double factor_count_total) {
		this.factor_count_total = factor_count_total;
		
	}


	
	
	
	
}
