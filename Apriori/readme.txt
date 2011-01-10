-----------------------------------------------------------------------------------------------------------------------------------------------
Readme for Apriori
-----------------------------------------------------------------------------------------------------------------------------------------------
Contents 
Section1: Information
Section2: Execution
Section3: Algorithm

-----------------------------------------------------------------------------------------------------------------------------------------------
Section1: Information

In order to run the Apriori Algorithm, we require a config file and a transaction file.

Contents of config.txt
- Number of items
- Number of transactions
- Min support (Given in decimal, Example - 0.6(60%))

Contents of transaction.txt
These are tab seperated values, where each line indicates a transaction and the values
used to represent if an item is present is 1 else 0. Please take care while entering 
the values. Each item has to be seprated by a tab[\t].

The transaction.txt file is taken from 
	courtesy: https://wiki.csc.calpoly.edu/datasets/wiki/apriori
	I've used 5000-out2.csv file for testing purpose. I converted
	the .csv file to .txt file for my convinience.

-----------------------------------------------------------------------------------------------------------------------------------------------
Section2: Execution

javac Apriori.java
java Apriori > output.txt

-----------------------------------------------------------------------------------------------------------------------------------------------
Section3: Algorithm 
	Courtesy: http://www2.cs.uregina.ca/~dbd/cs831/notes/itemsets/itemset_apriori.html

Pass 1
	- Generate the candidate itemsets in C1
	- Save the frequent itemsets in L1

Pass k
	- Generate the candidate itemsets in Ck from the frequent itemsets in Lk-1
	- Join Lk-1 p with Lk-1q, as follows: 
	
	- insert into Ck 
		select p.item1, p.item2, . . . , p.itemk-1, q.itemk-1 
		from Lk-1 p, Lk-1q 
		where p.item1 = q.item1, . . . p.itemk-2 = q.itemk-2, p.itemk-1 < q.itemk-1

	- Generate all (k-1)-subsets from the candidate itemsets in Ck
	- Prune all candidate itemsets from Ck where some (k-1)-subset of the candidate itemset is not in the frequent itemset Lk-1
	- Scan the transaction database to determine the support for each candidate itemset in Ck
	- Save the frequent itemsets in Lk.