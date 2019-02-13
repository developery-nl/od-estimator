package nl.tno.dymaes.datastructure;

import java.util.Hashtable;

public class Mapping {

	private final Countsite countsite;
	private final ODpair odpair;
	private final int od_timeinterval;
	private final int mapping_key;
	
	//A Hash with for each count_timeinterval as key the percentage of this od's traffic using the link
	private Hashtable<Integer, Double> percentageTable = new Hashtable<Integer, Double>();
	
	
	/**
	 * Constructor mapping record [for each countsite, for each related od-pair, for od time slice] the storage of percentages
	 * @param thisodpair
	 * @param countsite
	 */
	public Mapping(ODpair thisodpair, Countsite thiscountsite, int od_timeinterval) {
		this.odpair = thisodpair;
		this.countsite = thiscountsite;
		this.od_timeinterval = od_timeinterval;
		mapping_key = thisodpair.getKey()*100000 + thiscountsite.getId()*100 + od_timeinterval; //max 999 countsites //max 99 slices
	}
	
		
	/**
	 * Set percentage of flow for a specified count_timeinterval that uses this countsite link for the given OD-pair
	 * @param count_timeinterval
	 * @param percentage
	 */
	public void setPercentage(int count_timeinterval, double percentage){
		
		percentageTable.put(count_timeinterval, percentage);
	}
	
	/**
	 * Get percentage of flow for given count_timeinterval [for this od]
	 * @param count_timeinterval
	 * @return
	 */
	public double getPercentage(int count_timeinterval){
		double result=0.0;
		
		if (percentageTable.containsKey(count_timeinterval)){
			result = percentageTable.get(count_timeinterval);
		} else
		{
		  //not defined, thus zero	
		}
		
		return result;
	}
	
	public Countsite getCountsite() {
		return countsite;
	}
	public ODpair getOdpair() {
		return odpair;
	}
	public int getOd_timeinterval() {
		return od_timeinterval;
	}
	public int getMapping_key() {
		return mapping_key;
	}
	public Hashtable<Integer, Double> getPercentageTable() {
		return percentageTable;
	}
	
}
