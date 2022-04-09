import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;

public class Driver {
    
    public static void main (String[] args) {

		int hashTableSize = 20;
        double threshold = 8;
        String inputFile = "data.txt";
        String noiseWordsFile = "noisewords.txt";
        
        MDbSearchEngine rudb = new MDbSearchEngine(hashTableSize, threshold, noiseWordsFile);
		rudb.insertMoviesIntoHashTable(inputFile);
        rudb.print();

        String word1 = "emerald";
        String word2 = "city";

        ArrayList<MovieSearchResult> als = rudb.topTenSearch(word1, word2);
	
        if (als != null && als.size() > 0) {
            StdOut.println("The shortest distance between " + word1 + " and " + word2 + " is located at:");
            for ( MovieSearchResult s : als )
                System.out.println(s.getTitle()+"\t["+s.getMinDistance()+"]"); 
        } 
        else
            StdOut.println("There are no movies with the words " + word1 + " and " + word2 + " at their description.");
	}
}
