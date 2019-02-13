package nl.tno.dymaes;

import java.io.*;
import java.util.*;
import java.text.*;

public class LogWriter {
	
	private Manager manager;
	
	private String logFile;
	private String dir;

	/**
	 * Constructor
	 */
	public LogWriter(Manager mangr) {

		this.manager=mangr;
		dir=manager.getOutputDir();
		logFile=dir+File.separator+"log.txt";
		//System.out.println(logFile);
	}
	
	/**
	 * Adds log line to logfile
	 * @param s
	 */
    public void write(String s) {
	    write(logFile, s);
}

        
    
    public void write(String f, String s) {
        //TimeZone tz = TimeZone.getTimeZone("GMT+1:00"); // or PST, MID, etc ...
        TimeZone tz = TimeZone.getDefault();
        
        Date now = new Date();
        DateFormat df = new SimpleDateFormat ("yyyy.MM.dd  hh:mm:ss "); 
        df.setTimeZone(tz);
        String currentTime = df.format(now); 
        System.out.println(currentTime + " " + s);
        		
        FileWriter aWriter;
		try {
			aWriter = new FileWriter(f, true);
	        aWriter.write(currentTime + " " + s 
	                + System.getProperty("line.separator"));
	            aWriter.flush();
	            aWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        }
    
}