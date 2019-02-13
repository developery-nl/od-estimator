Alternative od estimater. Criteria can be changed using source code java in Eclipse 

M.Minderhoud



HOW TO USE?
---------------------
Edit user.ini file in root of "/input" folder to start estimation with specified input files (in a specified sub directory). 

Possible settings are:

OD_filename            : location of initial demand file (relative path from "/input" as root)
Mapping_filename       : location of mapping file (relative path from "/input" as root)
Counts_filename        : location of counts file (relative path from "/input" as root)
Zonesfactor_filename   : location of zonal constraint file (relative path from "/input" as root)

max_iter               : max number of iteration 
convergence_target     : convergence target as percentage of improvement from last iteration
errorsum_target        : convergence target absolute summed error
max_relative_od_grow   : maximum growth factor to apply for a matrix cell. (minimum growth factor is defined by: 1/max_relative_od_grow)
min_absolute_od_trips  : minimum threshold for a matrix cell, only apply a growth factor when trips higher than threshold
factor_count_total     : proportion of original total matrix sum in objective function (0: ignore 1: try to maintain initial trip sum)


SAMPLE:


OD_filename            = /os_regulier/os initial demand.txt
Mapping_filename       = /os_regulier/sorted_LinkMapIJTKD_os_2011_04_20.txt
Counts_filename        = /os_regulier/OS_reg_pae.txt
Zonesfactor_filename   = /os_regulier/factor_zones.txt

max_iter               = 14 
convergence_target     = 0.001
errorsum_target        = 100.0
max_relative_od_grow   = 6.5
min_absolute_od_trips  = 0.3
factor_count_total     = 0.05





INPUT FILES NEEDED
----------------------
The input for the model requires 4 files:



1. Demand matrix text file     

   Text file with for each origin-destination-timeslice the number of departures. 
   This is the start matrix wich will be used to create a new estimated matrix

   SAMPLE:

Origin Destination TimeSliceNumber Value Vehicletype
1 33 2 0.01 1
1 33 3 0.01 1
1 33 4 0.01 1
1 33 5 0.01 1
1 33 6 0.02 1
1 33 7 0.02 1
1 33 8 0.02 1
1 33 9 0.02 1
1 33 10 0.02 1




2. Mapping text file         

   Text files describes which proportion of trips of an od-pair 
   uses a route over a certain count site linknumber
   departed at a certain departure time slice
   and arrived in a certain observation time period
 

   sAMPLE:

or des  t  linknr  obs proportion
1  274  1  922852  1  0.0386
1  274  1  922852  2  0.0386
1  274  1  922852  3  0.0386
1  274  2  922852  4  0.0386
1  274  2  922852  5  0.0386
1  274  2  922852  6  0.0386
1  274  3  922852  7  0.0386
1  274  3  922852  8  0.0386
1  274  3  922852  9  0.0386
1  274  4  922852  10  0.0386
1  274  4  922852  11  0.0386
1  274  4  922852  12  0.0386
1  274  5  922852  13  0.0386
1  274  5  922852  14  0.0386
1  274  5  922852  15  0.0386
1  274  6  922852  16  0.0404
1  274  6  922852  17  0.0404
1  274  6  922852  18  0.0404
1  274  7  922852  19  0.0534
1  274  7  922852  20  0.0534







3.  Counts text file          

    Text file describes for each count site the number of counted vehicles per observation time period


    sAMPLE:

270 5 SimPeriod, Observation Interval, NB of Observations, weight factor for this count 
1 897722 54 0 0 0 0 0 0 10 10 10 10 10 10 11 13 14 15 16 17 18 19 21 22 23 24 25 27 29 30 32 33 35 36 38 40 41 43 42 41 40 40 39 38 37 36 36 35 34 33 33 33 33 33 33 33 5
2 897721 54 0 0 0 0 0 0 33 33 33 33 33 33 34 34 35 36 37 38 39 40 41 42 43 44 43 42 41 41 40 39 38 37 37 36 35 34 35 35 36 36 37 37 38 38 39 39 40 41 41 41 41 41 41 41 5
3 894601 54 0 0 0 0 0 0 3 3 3 3 3 3 4 4 4 4 4 4 4 5 5 5 5 5 5 5 6 6 6 6 6 6 6 6 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 5








4.  Zonal constraint text file

    Text file with list of zones that need a growth factor constraint
    If no list is available, keep file empty except header line
 
    SAMPLE:

zoneid max_relative_od_grow
597 1.0
898 1.0
919 1.0
1083 1.0



OUTPUT FILES
--------------------------------------------------
The estimation results are stored in the "/output" folder. 
Please copy the results to another directory before performing another run (they will be overwritten).


The output consist of 3 files:

   estimated od.txt			: the estimated new matrix [import this one into Omnitrans] 
   estimated od growth factor.txt	: the growthfactors applied to matrix cells [in case value 1.0, unchanged, not in this file]
   output count statistics.csv		: statistics for each count site with comparison between counts, original intensity at the site and new intensity at the site (both calculated using mapping). 