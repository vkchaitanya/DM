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
	String transactionFile = "testtransaction.txt";
		
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
		
		System.out.println("Frequent Itemsets");
        for (int i = 0; i < itemsets.size(); i++) {
        	if ((count[i] / (double) (totalTransactions)) >= minSupport) {
        		System.out.println(Arrays.toString(itemsets.get(i)) + " (" +  ((count[i] / (double) totalTransactions))+" "+count[i]+")");
        		frequentItems.add(itemsets.get(i));
        	}
        }
        itemsets = frequentItems;
	}
	
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
	
	private void generateNewItemsets(){
		int currentSizeOfItemsets = itemsets.get(0).length;
		HashMap<String, int[]> tempCandidates = new HashMap<String, int[]>(); 
        for(int i=0; i<itemsets.size(); i++) {
            for(int j=i+1; j<itemsets.size(); j++) {
                int[] X = itemsets.get(i);
                int[] Y = itemsets.get(j);
                assert (X.length==Y.length);
                int [] newCand = new int[currentSizeOfItemsets+1];
                for(int s=0; s<newCand.length-1; s++) {
                        newCand[s] = X[s];
            }
            int ndifferent = 0;
            for(int s1=0; s1<Y.length; s1++) {
                    boolean found = false;
                    // is Y[s1] in X?
                for(int s2=0; s2<X.length; s2++) {
                    if (X[s2]==Y[s1]) { 
						found = true;
                        break;
                    }
                }
				if (!found){ // Y[s1] is not in X
					ndifferent++;
					// we put the missing value at the end of newCand
					newCand[newCand.length -1] = Y[s1];
				}
            }
            assert(ndifferent>0);	                
            if (ndifferent==1) {
                Arrays.sort(newCand);
                tempCandidates.put(Arrays.toString(newCand),newCand);
            }
            }
        }	        
        itemsets = new ArrayList<int[]>(tempCandidates.values());
	}
}