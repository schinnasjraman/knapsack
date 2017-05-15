Knapsack problem for large values.
----------------------------------

example use case : video ad campaigns with forecasted impressions.

> # RevenueOptimization.java

  This class checks for maximum profit for given impression count.

  only monetizable impression counts are considered.

  for ex. if the knpasack weigh is 10 and you have 3 and 4 as item weights, you have to go over the points 3, 4, 6, 8, 9.
  If we want to find the max value for, say 7, identify the first lowest number below 7 and return its value.

> MemoizationUtil.java

  This class helps in maintaining low memory footprint for the memoization table( map in our case ).

  for update :
  1) identify bin no
  2) check if bin no is seen before.
  3) if bin no is not seen, create new bin in the memory. eviction is done if cache is full.
  4) if bin no is seen, check if bin is in memory, if not load it from disk by checking eviction policy

  for get :
  1) identify bin no.
  2) load the bins which gives floor entry for the impression queried and return the value.



> Constants.java

 1) input/output location.
 2) bin on disk location.( for memoization )
 2) bin size and no of bins. ( for memoization )


> Input file format:

> 2000000000 // forecasted impression
> Acme,1000000,5000 // advertiser name, no of impression per campaign, revenue for the campaign
> Lorem,2000000,9000
> Ipsum,3000000,20000


> output file format:

> Sample result 3
> Acme,2,2000000,10000 //advertiser name, no of campaigns, total impressions, total revenue
> Lorem,0,0,0
> Ipsum,666,1998000000,13320000
> 2000000000,13330000 // overall total impression, total revenue.



