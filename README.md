Knapsack problem for large values.
----------------------------------

example use case : video ad campaigns with forecasted impressions.

MemoizationUtil.java
  This class helps in maintaining low memory footprint for the memoization table( map in our case )
  for update :
  1) identify bin no
  2) check if bin no is seen before.
  3) if bin no is not seen, create new bin in the memory. eviction is done if cache is full.
  4) if bin no is seen, check if bin is in memory, if not load it from disk by checking eviction policy

  for get :
  1) identify bin no.
  2) load the bins which gives floor entry for the impression queried and return the value.


