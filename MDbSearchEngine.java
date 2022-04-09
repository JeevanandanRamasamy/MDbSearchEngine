import java.util.ArrayList;
import java.util.Collections;

import javax.sound.sampled.SourceDataLine;

/*
 * This class builds a hash table of words from movies descriptions. Each word maps to a set
 * of movies in which it occurs.
 */ 
public class MDbSearchEngine {
    
	private int    hashSize;   // the hash table size
	private double threshold;  // load factor threshold. load factor = wordCount/hashSize
    private int    wordCount;  // the number of unique words in the table
    private WordOccurrence[] hashTable;  // the hash table

    private ArrayList<String> noiseWords; // noisewords are not to be inserted in the hash table

	/** 
	 * Constructor initilizes the hash table.
	 * 
	 *  @param hashSize is the size for the hash table 
	 * 	@param threshold for the hash table load factor. Rehash occurs when the ratio 
	 * 	wordCount : hashSize exceeds the threshold.
	 *  @param noiseWordsFile contains words that will not be inserted into the hash table.
	 */
	public MDbSearchEngine (int hashSize, double threshold, String noiseWordsFile){

		this.hashSize   = hashSize;
		this.hashTable  = new WordOccurrence[hashSize];
        this.noiseWords = new ArrayList<String>();
		this.threshold  = threshold;
        this.wordCount  = 0;

        // Read noise words from file
        StdIn.setFile(noiseWordsFile);
        while ( !StdIn.isEmpty() ) {
            String word = StdIn.readString();
            if ( !noiseWords.contains(word) )
                noiseWords.add(word);
        }
    }

	/**
	 * Method used to map a word into an array index.
	 * 
	 * @param word the word
	 * @return array index within @hashTable
	 */
	private int hashFunction ( String word ) {
        int hashCode = Math.abs(word.toLowerCase().replaceAll("/[^a-z0-9]/","").hashCode());
		return hashCode % hashSize;
	}

	/**
	 * Returns the hash table load factor
	 * @return the load factor
	 */ 
	public double getLoadFactor () {
		return (double)wordCount/hashSize;
	}

	/**
	 * This method reads movies title and description from the input file.
     * 
	 * @param inputFile the file to be read containg movie's titles and descriptions.
     * 
     * The inputFile format:
     * 		Each line describes a movie's title, and a short description on the movie.
	 * 		title| word1 word2 word3;
     * 
	 *  	Note that title can have multiple words, there is no space between the last 
     *      word on the tile and '|'
	 * 		No duplicate movie name accepted.
     * 
	 * @return ArrayList of ArrayList of Strings, each inner ArrayList refers to a movie, 
	 * 		the first index contains the title, the remaining indices contain the movie's
	 * 		description words (one word per index). 
     * 
	 *  Example: 
	 * 		[
	 * 			[full title1][word1][word2]
	 * 			[full title2][word1]
	 * 			[full title3][word1][word2][word3][word4]
	 * 		]
	 */
	public ArrayList<ArrayList<String>> readInputFile ( String inputFile ) {

		ArrayList<ArrayList<String>> allMovies = new ArrayList<ArrayList<String>>();
		StdIn.setFile(inputFile);
		
        String[] read = StdIn.readAllStrings();
		
        for ( int i = 0; i < read.length; i++ ) {
			ArrayList<String> movie = new ArrayList<String>();
			String t = "";
			do {
                t += " "+read[i];
            } while ( read[i++].indexOf('|') == -1 );
			
            movie.add(t.substring(1,t.length()-1).toLowerCase().replaceAll("/[^a-z0-9]/",""));

			while ( i < read.length ) {
				if ( read[i].indexOf(';') != -1 ) {
					movie.add(read[i].substring(0,  read[i].indexOf(';')));
					break;
				}
				movie.add(read[i].toLowerCase().replaceAll("/[^a-z0-9]/","") );
				i++;
			}
			allMovies.add(movie);
		}
		return allMovies;
	}
	
	/** 
	 * This method calls readInputFile and uses its output to load the movies and their
     * descriptions words into the hashTable.
     * 
     * Use the result from readInputFile() to insert each word and its location
     * into the hash table.
     * 
     * Use isWord() to discard noise words, remove trailing punctuation, and to transform
     * the word into all lowercase character.
     * 
     * Use insertWordLocation() to insert each word into the hash table.
	 * 
	 * @param inputFile the file to be read containg movie's titles and descriptions
	 * 
	 */
	public void insertMoviesIntoHashTable (String inputFile) {
		ArrayList<ArrayList<String>> movieList = readInputFile(inputFile);
		for (ArrayList<String> movie : movieList) {
			for (int i = 1; i < movie.size(); i++) {
				String word = isWord(movie.get(i));
				if (word != null) {
					insertWordLocation(word, new Location(movie.get(0), i));
				}
			}
		}
	}

    /**
	 * Given a word, returns it as a word if it is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters and digits, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return word (word without trailing punctuation, LOWER CASE)
	 */
	private String isWord ( String word ) {
		int p = 0;
    	char ch = word.charAt(word.length()-(p+1));
    	while (ch == '.' || ch == ',' || ch == '?' || ch == ':' || ch == ';' || ch == '!') {
    		p++;
            if ( p == word.length() ) {
                // the entire word is punctuation
                return null;
            }
    		int index = word.length()-(p+1);
    		if (index == -1) {
    			System.out.flush();
    		}
    		ch = word.charAt(word.length()-(p+1));	
    	}
        
    	word = word.substring(0,word.length()-p);

    	// are all characters alphabetic letters?
    	for (int i=0; i < word.length(); i++) {
    		if (!Character.isLetterOrDigit(word.charAt(i))) {
    			return null;
    		}
    	}
    	word = word.toLowerCase();
    	if (noiseWords.contains(word)) {
    		return null;
    	}
    	return word;
	}

    /*
	 * Prints the entire hash table
	 */
	public void print () {

        for ( int i = 0; i < hashTable.length; i++ ) {
            
            StdOut.printf("[%d]->", i);
            for ( WordOccurrence ptr = hashTable[i]; ptr != null; ptr = ptr.next ) {

                StdOut.print(ptr.toString());
                if ( ptr.next != null ) {
                    StdOut.print("->");
                }
            }
            StdOut.println();
        }
	}

	/**
	 * This method inserts a Location object @loc into the matching WordOccurrence object
	 * in the hash table. If the word is not present into the hash table, add a new 
     * WordOccurrence object into hash table. 
	 * 		
	 * @param word to be inserted
	 * @param loc the word's position within the description.
	 */
	public void insertWordLocation (String word, Location loc) {
		WordOccurrence w = getWordOccurrence(word);
		if (w != null)
			w.addOccurrence(loc);
		else {
			WordOccurrence newWord = new WordOccurrence(word);
			newWord.addOccurrence(loc);
			int hashVal = hashFunction(word);
			newWord.next = hashTable[hashVal];
			hashTable[hashVal] = newWord;
			wordCount++;
			if (getLoadFactor() > threshold)
				rehash(2 * hashSize);
		}
    }

	/**
	 * Rehash the hash table to newHashSize. Rehash happens when the load factor is
     * greater than the @threshold (load factor = wordCount/hashSize).
     * 
	 * @param newHashSize is the new hash size
	 */
	private void rehash (int newHashSize) {
        WordOccurrence[] newTable = new WordOccurrence[newHashSize];
		hashSize = newHashSize;
		for (int i = 0; i < hashTable.length; i++) {
			WordOccurrence wordOcc = hashTable[i];
			while (wordOcc != null) {
				int hashVal = hashFunction(wordOcc.getWord());
				WordOccurrence newWord = wordOcc;
				wordOcc = wordOcc.next;
				if (newTable[hashVal] == null) {
					newWord.next = null;
					newTable[hashVal] = newWord;
				}
				else {
					newWord.next = newTable[hashVal];
					newTable[hashVal] = newWord;
				}
			}
		}
		hashTable = newTable;
	}

	/** 
	 * Find the WordOccurrence object with the target word in the hash table
	 * @param word search target
	 * @return @word WordOccurrence object
	 */
	public WordOccurrence getWordOccurrence (String word) {
        for (WordOccurrence w = hashTable[hashFunction(word)]; w != null; w = w.next) {
			if (word.equals(w.getWord()))
				return w;
		}
		return null;
	}

	/**
	 * Checks if the movie is in the ArrayList of MovieSearchResults. This is a
	 * helper method for getIndex.
     * 
	 * @param title is the title of the movie searched
	 * @param movies is the ArrayList of MovieSearchResults
	 * @return index of movie in ArrayList
	 */
	private int movieInList (String title, ArrayList<MovieSearchResult> movies) {
		for (int i = 0; i < movies.size(); i++) {
			if (title.equals(movies.get(i).getTitle()))
				return i;
		}
		return -1;
	}

	/**
	 * Returns the index of the searched movie if it exists. Otherwise, adds
	 * the movie to the list and returns the last index. This is a helper
	 * method for createMovieSearchResult.
     * 
	 * @param loc is the location of the searched movie
	 * @param movies is the list of MovieSearchResults
	 */
	private int getIndex (Location loc, ArrayList<MovieSearchResult> movies) {
		String title = loc.getTitle();
		int index = movieInList(title, movies);
		if (index == -1) {
			movies.add(new MovieSearchResult(title));
			return movies.size() - 1;
		}
		return index;
	}
    
	/**
	 * Finds all occurrences of wordA and wordB in the hash table, and add them to an 
	 * ArrayList of MovieSearchResult based on titles.
	 * 		(no need to calculate distance here)
     * 
	 * @param wordA is the first queried word
	 * @param wordB is the second queried word
	 * @return ArrayList of MovieSearchResult objects.
	 */
	public ArrayList<MovieSearchResult> createMovieSearchResult (String wordA, String wordB) {
		WordOccurrence occurrenceA = getWordOccurrence(wordA), occurrenceB = getWordOccurrence(wordB);
		if (occurrenceA == null || occurrenceB == null)
			return null;
		ArrayList<MovieSearchResult> movies = new ArrayList<MovieSearchResult>();
		for (Location loc : occurrenceA.getLocations())
			movies.get(getIndex(loc, movies)).addOccurrenceA(loc.getPosition());
		for (Location loc : occurrenceB.getLocations())
			movies.get(getIndex(loc, movies)).addOccurrenceB(loc.getPosition());
		return movies;
	}

	/**
	 * Compares the remaining values of the ArrayList with the last value of the other
	 * ArrayList in order to find the minimum distance. This is a helper method for
	 * calculateMinDistance.
     * 
	 * @param num is the last value of the other ArrayList
	 * @param list is the ArrayList with the remaining values
	 * @param index is the first index of the remaining values
	 * @param min is the minimum distance determined by calculateMinDistance
	 * before this method is called
	 * @return the minimum distance between the two words in the MovieSearchResult
	 */
	private int compareRemainingValues (int num, ArrayList<Integer> list, int index, int min) {
		while (index < list.size()) {
			int dist = Math.abs(list.get(index++) - num);
			if (dist < min)
				min = dist;
		}
		return min;
	}

	/**
     * Computes the minimum distance between the two wordA and wordB in @msr.
     * In another words, this method computes how close these two words appear 
     * in the description of the movie (MovieSearchResult refers to one movie).
     * 
     * If the movie's description doesn't contain one, or both words set distance to -1;
	 * 
	 * NOTE: the ArrayLists for A and B will always be in order since the words were added in order.
	 * 		
	 * The shortest distance between two words can be found by keeping track of the index 
     * of previous wordA and wordB, then find the next location of either word and calculate 
     * the distance between the word and the previous location of the other word.
     * 
	 * For example:
	 * 		wordA locations: 1 3 5 11
	 * 		wordB locations: 4 10 12 
	 * 		start previousA as 1, and previousB as 4, calculate distance as abs(1-4) = 3
	 * 		because 1<4, 	update previousA to 3, 	abs(4-3)   = 1 , smallest so far
	 * 		because 3<4, 	update previousA to 5, 	abs(5-4)   = 1 
	 * 		because 5>4, 	update previousB to 10, abs(5-10)  = 5
	 * 		because 5<10, 	update previousA to 11, abs(11-10) = 1
	 * 		End because all elements from A have been used.
	 * 		
	 * @param msr the MovieSearchResult object to be updated with the minimum distance between its 
     * words.
	 */
	public void calculateMinDistance (MovieSearchResult msr){
		ArrayList<Integer> listA = msr.getArrayListA(), listB = msr.getArrayListB();
		if (listA.isEmpty() || listB.isEmpty())
			msr.setMinDistance(-1);
		else {
			int a = 0, b = 0, min = Integer.MAX_VALUE;
			while (a < listA.size() && b < listB.size()) {
				int dist = Math.abs(listA.get(a) - listB.get(b));
				if (dist < min)
					min = dist;
				if (listA.get(a) < listB.get(b))
					a++;
				else if (listA.get(a) > listB.get(b))
					b++;
			}
			if (a < listA.size())	// Compares the rest of ListA with the last element of B
				min = compareRemainingValues(listB.get(b - 1), listA, ++a, min);
			else					// Compares the rest of ListB with the last element of A
				min = compareRemainingValues(listA.get(a - 1), listB, ++b, min);
			msr.setMinDistance(min);
		}
	}

	/**
	 * This method's purpose is to search the movie database to find movies that 
     * contain two words (wordA and wordB) in their description.
	 * 
     * @param wordA the first word to search
     * @param wordB the second word to search
     * 
     * @return ArrayList of MovieSearchResult, with length <= 10. Each
	 * MovieSearchResult object returned must have a non -1 distance (meaning that
     * both words appear in the description). The ArrayList is expected to be 
     * sorted from the smallest distance to the greatest.
	 * 
	 * NOTE: feel free to use Collections.sort(arrayListOfMovieSearchResult); to sort.
	 */
	public ArrayList<MovieSearchResult> topTenSearch(String wordA, String wordB){
		ArrayList<MovieSearchResult> movies = createMovieSearchResult(wordA, wordB);
		if (movies == null)
			return null;
		ArrayList<MovieSearchResult> result = new ArrayList<MovieSearchResult>();
		for (MovieSearchResult msr : movies)
			calculateMinDistance(msr);
		Collections.sort(movies);
		for (int i = 0; i < movies.size(); i++) {
			if (movies.get(i).getMinDistance() != -1)
				result.add(movies.get(i));
			if (result.size() == 10)
				return result;
		}
		return result;
	}
}
