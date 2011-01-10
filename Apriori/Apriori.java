import java.io.*;
import java.util.*;

/**
 * 
 * @author chaitu
 * Implement Apriori Algorithm for finding frequent itemsets
 * 
 * @input config.txt
 * 		Contents of config.txt
 * 			- Number of items
 * 			- Number of transactions
 * 			- Min support (Given in decimal, by default 0.6(60%))
 * @input: transactions.txt
 * 
 * References: 
 * 1. http://www2.cs.uregina.ca/~dbd/cs831/notes/itemsets/itemset_prog1.html
 * 2. http://allmybrain.com/2007/11/12/implementing-the-apriori-data-mining-algorithm-with-javascript/
 * 3. http://www.codeproject.com/KB/recipes/AprioriAlgorithm.aspx
 */
public class Apriori {

	/*
	 * @param Input variable declarations
	 */
	int numItems;
	int totalTransactions;
	double minSupport;
	int itemsetNumber=0;
	ArrayList<int[]> itemsets = null;
	String configFile = "config.txt";
	String transactionFile = "transaction.txt";
		
	public Apriori() throws NumberFormatException, IOException {
		config();
		compute();
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		Apriori ap = new Apriori();
	}
	
	/**
	 * @throws IOException 
	 * @throws NumberFormatException 
	 * @input: config.txt
	 * Reads the following values from the config.txt file
	 * 	- Number of items
	 *  - Number of transactions
	 *  - Minimum support
	 */
	private void config() throws NumberFormatException, IOException {
		try{			
			FileInputStream inputStream = new FileInputStream(configFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            
            numItems = Integer.valueOf(bufferedReader.readLine()).intValue();
            totalTransactions = Integer.valueOf(bufferedReader.readLine()).intValue();
            minSupport = Double.valueOf(bufferedReader.readLine()).doubleValue();
		}
		catch(FileNotFoundException fileNotFound){
			System.out.println("Error!!" + fileNotFound.getMessage());
		}
		catch (IOException ioException) {
			System.out.println("Error!!" + ioException.getMessage());
		}
	}
	
	/**
	 * @return: Values to the output file
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * This function implements the Apriori algorithm. 
	 */
	private void compute() throws FileNotFoundException, IOException{
		createItemsets();
		do{
			itemsetNumber++;
			getFrequentItemsets();
			if(itemsets.size()!=0){
				generateNewItemsets();
			}
		}
		while(itemsets.size()>1);
	}
	
	private void createItemsets(){
		itemsets = new ArrayList<int[]>();
		for(int i=0; i<numItems; i++){
			int[] candidate = {i};
			itemsets.add(candidate);
		}
	}
	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * 
	 * In the first iteration, each item is a member of the set of candidate 
	 * 1-itemsets, C1. The algorithm simply scans all of the transactions in 
	 * order to count the number of occurrences of each item. Based on the 
	 * minimum support, The set of frequent 1-itemsets, L1, will then be determined.
	 */
	private void getFrequentItemsets() throws FileNotFoundException, IOException{
		ArrayList<int[]> frequentItems = new ArrayList<int[]>();
		boolean found;		
		int count[] = new int[itemsets.size()]; 
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(transactionFile)));
        boolean[] transactions = new boolean[numItems];
        
        for (int i = 0; i < totalTransactions; i++) {
            String row = reader.readLine();
            getDataSet(row, transactions);
            for (int j = 0; j < itemsets.size(); j++) {
	            found = true;
	            int[] candidate = itemsets.get(j);
	            for (int item : candidate) {
	                if (transactions[item] == false) {
	                	found = false;
	                	break;
	                }
	            }
	            if (found) {
                    count[j]++;
	            }
            }
        }
		
        System.out.println("Candidate Itemsets");
		for (int i = 0; i < itemsets.size(); i++) {
			System.out.println(Arrays.toString(itemsets.get(i)) + " (" +  ((count[i] / (double) totalTransactions))+" "+count[i]+")");
        }        
		
		//Apriori property: All nonempty subsets of a frequent itemset must also be frequent.
		System.out.println("Frequent Itemsets");
        for (int i = 0; i < itemsets.size(); i++) {
        	if ((count[i] / (double) (totalTransactions)) >= minSupport) {
        		System.out.println(Arrays.toString(itemsets.get(i)) + " (" +  ((count[i] / (double) totalTransactions))+" "+count[i]+")");
        		frequentItems.add(itemsets.get(i));
        	}
        }
        itemsets = frequentItems;
	}
	
	/**
	 * @param row
	 * @param transactions
	 * You pass in each line, where each line is a transaction,
	 * and each item is separated by tab values[\t]. The transaction
	 * array is a boolean array, where if an item is present in the  
	 * transaction is set to 1, else to 0. 
	 */
	private void getDataSet(String row, boolean[] transactions){
		Arrays.fill(transactions, false);
		int j=0;
		StringTokenizer tokens = new StringTokenizer(row, "\t");
		while(tokens.hasMoreElements()){
			int val = Integer.parseInt(tokens.nextElement().toString());
			if(val == 1)
				transactions[j]=true;
			j++;
		}
	}
	
	/**
	 * To discover the set of frequent k-itemsets where K>1, 
	 * join L1 on L1 to generate a candidate set of 2-itemsets
	 * and this process of generating new Itemsets continues
	 * until no more frequent k-itemsets can be found. 
	 */
	private void generateNewItemsets(){
		int size = itemsets.get(0).length;
		HashMap<String, int[]> candidateList = new HashMap<String, int[]>(); 
		for(int i=0; i<itemsets.size(); i++) {
			for(int j=i+1; j<itemsets.size(); j++) {
				int[] listA = itemsets.get(i);
				int[] listB = itemsets.get(j);
				assert (listA.length==listB.length);
				int [] newCandidates = new int[size+1];
				for(int s=0; s<newCandidates.length-1; s++) {
					newCandidates[s] = listA[s];
				}
				int difference = 0;
				for(int l1=0; l1<listB.length; l1++) {
					boolean found = false;
					for(int l2=0; l2<listA.length; l2++) {
						if (listA[l2]==listB[l1]) { 
							found = true;
							break;
						}
					}
					if (!found){
						difference++;
						newCandidates[newCandidates.length -1] = listB[l1];
					}
				}
				assert(difference>0);	                
				if (difference==1) {
					Arrays.sort(newCandidates);
					candidateList.put(Arrays.toString(newCandidates),newCandidates);
				}
			}
		}    
		itemsets = new ArrayList<int[]>(candidateList.values());
	}
}