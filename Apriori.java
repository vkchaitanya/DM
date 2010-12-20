import java.io.*;
import java.util.*;

/**
 * 
 * @author chaitu
 * Implement Apriori Algorithm for finding frequent itemsets
 * using candidate generation.
 * 
 * @input: config.txt
 * 		Contents of config.txt
 * 			- Number of items
 * 			- Number of transactions
 * 			- Min support (Given in decimal, by default 0.6(60%))
 * @input: transactions.txt
 */
public class Apriori {

	/*
	 * Input variable declarations
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
            for (int c = 0; c < itemsets.size(); c++) {
	            found = true;
	            int[] cand = itemsets.get(c);
	            for (int xx : cand) {
	                if (transactions[xx] == false) {
	                	found = false;
	                    break;
	                }
	            }
	            if (found) {
                    count[c]++;
	            }
            }
        }
        
        for (int i = 0; i < itemsets.size(); i++) {
        	if ((count[i] / (double) (totalTransactions)) >= minSupport) {
        		foundFrequentItemSet(itemsets.get(i), count[i]);
        		frequentItems.add(itemsets.get(i));
        	}
        }
        itemsets = frequentItems;
	}
	
	private void getDataSet(String row, boolean[] transactions){
		Arrays.fill(transactions, false);
		int j=0;
		StringTokenizer tokens = new StringTokenizer(row, " ");
		while(tokens.hasMoreElements()){
			int val = Integer.parseInt(tokens.nextElement().toString());
			if(val == 1)
				transactions[j]=true;
			j++;
		}
	}
	
	private void foundFrequentItemSet(int[] itemset, int support) {
        System.out.println(Arrays.toString(itemset) + "  ("+ ((support / (double) totalTransactions))+" "+support+")");
    }
	private void generateNewItemsets(){
		int currentSizeOfItemsets = itemsets.get(0).length;
		HashMap<String, int[]> tempCandidates = new HashMap<String, int[]>(); 
	        for(int i=0; i<itemsets.size(); i++) {
	            for(int j=i+1; j<itemsets.size(); j++) {
	                int[] X = itemsets.get(i);
	                int[] Y = itemsets.get(j);

	                assert (X.length==Y.length);
	                
	                //make a string of the first n-2 tokens of the strings
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