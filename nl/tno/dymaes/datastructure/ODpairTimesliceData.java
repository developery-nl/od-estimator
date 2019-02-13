package nl.tno.dymaes.datastructure;

public class ODpairTimesliceData {
	
	private final ODpair od;
	private final int od_timeinterval;
	

	//original trip value 
	private double originaltrips=0;
	//PREVIOUS estimated trip value 
	private double estimatedtrip=0;
	//NEW estimated trip value 
	private double new_estimatedtrips=0;
	//Countsvalue sum (contribution to countsites)
	private double countssum=0;
	//Intensityvalue sum (contribution to countsites)
	private double intensitysum=0;
	//Intensityvalue sum (contribution to countsites)
	private double intensitysumPreviousIteration=0;
	
	
	/**
	 * Constructor
	 * @param odobject
	 * @param od_timeinterval
	 */
	public ODpairTimesliceData(ODpair odobject, int od_timeinterval) {
		this.od              = odobject;
		this.od_timeinterval = od_timeinterval;
	}
	
	public double getCountssum() {
		return countssum;
	}
	public double getEstimatedtrip() {
		return estimatedtrip;
	}
	public double getIntensitysum() {
		return intensitysum;
	}
	public double getIntensitysumPreviousIteration() {
		return intensitysumPreviousIteration;
	}
	public double getNew_estimatedtrips() {
		return new_estimatedtrips;
	}
	public ODpair getOd() {
		return od;
	}
	public int getOd_timeinterval() {
		return od_timeinterval;
	}
	public double getOriginaltrips() {
		return originaltrips;
	}
	public void setCountssum(double countssum) {
		this.countssum = countssum;
	}
	public void setEstimatedtrip(double estimatedtrip) {
		this.estimatedtrip = estimatedtrip;
	}
	public void setIntensitysum(double intensitysum) {
		this.intensitysum = intensitysum;
	}
	public void setIntensitysumPreviousIteration(
			double intensitysumPreviousIteration) {
		this.intensitysumPreviousIteration = intensitysumPreviousIteration;
	}
	public void setNew_estimatedtrips(double new_estimatedtrips) {
		this.new_estimatedtrips = new_estimatedtrips;
	}
	public void setOriginaltrips(double originaltrips) {
		this.originaltrips = originaltrips;
	}
	
	
	
	
	
}
