package nl.tno.dymaes.datastructure;

import java.util.Hashtable;

public class Countsite {

	    private int id;
	    private int linknumber;
	    private double reliability;
	
	    private double count_total_all_obs;
	    private double intensity_total_all_obs;
	    
	    //For each count_timeinterval the corresponding counted value 
	    private Hashtable<Integer, Double> countTable = new Hashtable<Integer, Double>();
	
	   //For each count_timeinterval the estimated intensity value using mapping 
	    private Hashtable<Integer, Double> originalintensityTable = new Hashtable<Integer, Double>();
	
	   //For each count_timeinterval the new estimated intensity value (ideal Target=countTable)  
	    private Hashtable<Integer, Double> newintensityTable = new Hashtable<Integer, Double>();
	
	    /**
	     * Constructor countsite object
	     * @param id
	     * @param linknumber
	     * @param reliability
	     */
	    
		public Countsite(int id, int linknumber, double reliability) {
			this.id          = id;
			this.linknumber  = linknumber;
			this.reliability = reliability;
		}
		
		
		public int getId() {
			return id;
		}
		public int getLinknumber() {
			return linknumber;
		}
		public double getReliability() {
			return reliability;
		}
		public Hashtable<Integer, Double> getCountTable() {
			return countTable;
		}
		/**
		 * Set trip value for specified timeinterval
		 * @param od_timeinteval
		 * @param trips
		 */
		public void setCountvalue(int count_timeinterval, double countvalue){
			countTable.put(count_timeinterval, countvalue);
		}
		/**
		 * Get value for specified timeinterval and ERROR if not specified
		 * @param od_timeinterval
		 * @return
		 */
		public double getCountvalue(int count_timeinterval){
			double result=0;
			if (countTable.containsKey(count_timeinterval)) {
			   result = countTable.get(count_timeinterval);
			} else {
				//Error
			}
			return result;
		}
		
		/**
		 * Set intensity value (calculated from mapping) for specified timeinterval
		 * @param od_timeinteval
		 * @param trips
		 */
		public void setIntensityvalue(int count_timeinterval, double countvalue){
			originalintensityTable.put(count_timeinterval, countvalue);
		}
		/**
		 * Get intensity value (calculated from mapping) for specified timeinterval and ERROR if not specified
		 * @param od_timeinterval
		 * @return
		 */
		public double getIntensityvalue(int count_timeinterval){
			double result=0;
			if (originalintensityTable.containsKey(count_timeinterval)) {
			   result = originalintensityTable.get(count_timeinterval);
			} else {
				//Error
			}
			return result;
		}
		
		/**
		 * Set intensity value (new calculated) for specified timeinterval
		 * @param od_timeinteval
		 * @param trips
		 */
		public void setNewIntensityvalue(int count_timeinterval, double countvalue){
			newintensityTable.put(count_timeinterval, countvalue);
		}
		/**
		 * Get intensity value (new calculated) for specified timeinterval and ERROR if not specified
		 * @param od_timeinterval
		 * @return
		 */
		public double getNewIntensityvalue(int count_timeinterval){
			double result=0;
			if (newintensityTable.containsKey(count_timeinterval)) {
			   result = newintensityTable.get(count_timeinterval);
			} else {
				//Error
			}
			return result;
		}
		public double getCount_total_all_obs() {
			return count_total_all_obs;
		}
		public double getIntensity_total_all_obs() {
			return intensity_total_all_obs;
		}
		public void setCount_total_all_obs(double count_total_all_obs) {
			this.count_total_all_obs = count_total_all_obs;
		}
		public void setIntensity_total_all_obs(double intensity_total_all_obs) {
			this.intensity_total_all_obs = intensity_total_all_obs;
		}



		

}
