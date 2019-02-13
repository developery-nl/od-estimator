package nl.tno.dymaes.datastructure;

import java.util.Hashtable;

public class ODpair {
	
	private final int fromID;
	private final int toID;
	private final int odkey;
	
	
	//For each od_timeinterval the corresponding data-object 
	private Hashtable<Integer, ODpairTimesliceData> timesliceTable = new Hashtable<Integer, ODpairTimesliceData>();
	
	//Total trips over all slices	
	private double odpair_sum_original;
	   
	
	/**
	 * Constructor
	 * @param from
	 * @param to
	 */
	public ODpair(int from, int to) {
		this.fromID   = from;
		this.toID     = to;
		this.odkey    = fromID*10000 + toID; //max zones 9999
			
	}
		
	/**
	 * Unique key to identify this od-relation object: fromID*10000+toID
	 * @return
	 */
	public int getKey() {
		return odkey;
	}
	public int getFromID() {
		return fromID;
	}
	public int getToID() {
		return toID;
	}
	
	
	/**
	 * Create object for wrapping all data
	 * @param timeslice
	 */
	public void initTimeslicedata(int timeslice){
		
		if (timesliceTable.containsKey(timeslice)) {
			   //skip
			} else {
			  //new
				ODpairTimesliceData thisDataODpair = new ODpairTimesliceData(this,timeslice); 
				timesliceTable.put(timeslice, thisDataODpair);	
			}
		
	}
	
	
	/**
	 * Get corresponding data object
	 * @param timeslice
	 * @return
	 */
	public ODpairTimesliceData getTimeslicedataObject(int timeslice){
		ODpairTimesliceData result=null;
		if (timesliceTable.containsKey(timeslice)) {
			   result = timesliceTable.get(timeslice);
			} 
		return result;
	}
	
	/**
	 * Set trip value for specified timeinterval
	 * @param od_timeinteval
	 * @param trips
	 */
	public void setOriginaltrips(int od_timeinterval, double trips){
	
		ODpairTimesliceData theDataObject =	getTimeslicedataObject(od_timeinterval);
		if (theDataObject!=null){
			theDataObject.setOriginaltrips(trips);
		}
		
	}
	/**
	 * Get trip value for specified timeinterval and 0 if not specified
	 * @param od_timeinterval
	 * @return
	 */
	public double getOriginaltrips(int od_timeinterval){
		double result=0;
		
		ODpairTimesliceData theDataObject =	getTimeslicedataObject(od_timeinterval);
		if (theDataObject!=null){
		   result = theDataObject.getOriginaltrips();
		}
		return result;
		
	}
	
	public void setEstimatedtrips(int od_timeinterval, double trips){

		ODpairTimesliceData theDataObject =	getTimeslicedataObject(od_timeinterval);
		if (theDataObject!=null){
			theDataObject.setEstimatedtrip(trips);
		}
	}
	
	public double getEstimatedtrips(int od_timeinterval){
		double result=0;

		ODpairTimesliceData theDataObject =	getTimeslicedataObject(od_timeinterval);
		if (theDataObject!=null){
		   result = theDataObject.getEstimatedtrip();
		}
		return result;
	}

	public void setNewEstimatedtrips(int od_timeinterval, double trips){

		ODpairTimesliceData theDataObject =	getTimeslicedataObject(od_timeinterval);
		if (theDataObject!=null){
			theDataObject.setNew_estimatedtrips(trips);
		}
	}
	
	public double getNewEstimatedtrips(int od_timeinterval){
		double result=0;

		ODpairTimesliceData theDataObject =	getTimeslicedataObject(od_timeinterval);
		if (theDataObject!=null){
		   result = theDataObject.getNew_estimatedtrips();
		}
		return result;
	}
	

	public void setCountssum(int od_timeinterval, double value) {

		ODpairTimesliceData theDataObject =	getTimeslicedataObject(od_timeinterval);
		if (theDataObject!=null){
			theDataObject.setCountssum(value);
		}
		
	}
	public double getCountsum(int od_timeinterval){
		double result=0;

		ODpairTimesliceData theDataObject =	getTimeslicedataObject(od_timeinterval);
		if (theDataObject!=null){
		   result = theDataObject.getCountssum();
		}
		return result;
	}
	public void setIntensitysum(int od_timeinterval, double value) {

		ODpairTimesliceData theDataObject =	getTimeslicedataObject(od_timeinterval);
		if (theDataObject!=null){
			theDataObject.setIntensitysum(value);
		}
		
	}
	public double getIntensitysum(int od_timeinterval){
		double result=0;

		ODpairTimesliceData theDataObject =	getTimeslicedataObject(od_timeinterval);
		if (theDataObject!=null){
		   result = theDataObject.getIntensitysum();
		}
		return result;
	}
	
	public void setIntensitysumPreviousIteration(int od_timeinterval, double value) {

		ODpairTimesliceData theDataObject =	getTimeslicedataObject(od_timeinterval);
		if (theDataObject!=null){
			theDataObject.setIntensitysumPreviousIteration(value);
		}
		
	}
	public double getIntensitysumPreviousIteration(int od_timeinterval){
		double result=0;

		ODpairTimesliceData theDataObject =	getTimeslicedataObject(od_timeinterval);
		if (theDataObject!=null){
		   result = theDataObject.getIntensitysumPreviousIteration();
		}
		return result;
	}


	public void setOriginaltrips_allslices(double odpair_sum_original) {
		this.odpair_sum_original = odpair_sum_original;
		
	}
	public double getOriginaltrips_allslices() {
		return odpair_sum_original;
		
	}
	

}
