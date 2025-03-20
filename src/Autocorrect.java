import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Autocorrect
 * <p>
 * A command-line tool to suggest similar words when given one not in the dictionary.
 * </p>
 * @author Zach Blick
 * @author Stevie K. Halprin
 */
public class Autocorrect {

    /**
     * Constucts an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */

    public static void main(String[] args) {
        // Create a new Autocorrect object
        // Input a large dictionary that's loaded in, and a
        Autocorrect auto = new Autocorrect(loadDictionary("large"), 4);
        // Run autocorrect
        auto.run();
    }

    // Runner method for autocorrect
    private void run() {
        while (true) {
            // Ask the user for input
            System.out.print("Type a word here: ");
            // Get input using scanner
            Scanner s = new Scanner(System.in);
            String typed = s.next();
            // Do autocorrect
            String[] closestWords = runTest(typed);
            // If no close words were found, print "No matches found."
            if (closestWords.length == 0) {
                System.out.println("No matches found.");
            }
            // Otherwise print the top 3 recommended words
            for (String word : closestWords) {
                System.out.println(word);
            }
            System.out.println();
        }
    }


    // HashMap to hold dictionary of words, with value being minimum number of edits needed to get to typed word
    public static HashMap<String, Integer> dict;
    // Integer to represent threshold for edits to typed words
    public static int editLimit;

    public Autocorrect(String[] words, int threshold) {
        // Convert the dictionary to a HashMap for faster lookups
        dict = new HashMap<>();
        for (int i = 0; i < words.length; i++) {
            dict.put(words[i], 0);
        }
        // Set editLimit equal to the inputted threshold
        editLimit = threshold;
    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distnace, then sorted alphabetically.
     */
    public String[] runTest(String typed) {
        // Reset the edit distance of the words in the dictionary
        for (String key : dict.keySet()) {
            dict.put(key, 0);
        }
        // If the typed word exists in the dictionary, return an empty array
        if (dict.containsKey(typed)) {
            String[] finalReturn = new String[1];
            finalReturn[0] = "This word already exists in the dictionary.";
            return finalReturn;
        }


        // Integer to hold length of typed word
        int length = typed.length();
        // Update the edit value of every word in the dictionary
        for (String key : dict.keySet()) {
            // Only add words that are within 3 of the typed word's length
            if (!(key.length() > length + editLimit || key.length() < length - editLimit)) {
                // Take out all words that have a longest shared subsequence of length > typed.length() / 2
                if (!(longestSharedSubstring(typed, key) < length * 3 / 4)) {
                    // Add the minimum number of edits it would take to convert typed to the dict word
                    dict.put(key, editDistance(typed, key));
                }
            }
        }

        // THE WAY I MAKE THE FINAL LIST ALPHABETICALLY SORTED WAS MADE WITH THE HELP OF GEMINI, AN LLM
        // Create a new TreeMap so keys can be sorted alphabetically
        TreeMap<String, Integer> sortedMap = new TreeMap<>(dict);

        // ArrayList of (to be) ordered words to be returned
        ArrayList<String> finalWords = new ArrayList<>();
        // Add the words from dict to the ArrayList with those requiring the fewest edits first
        // Only add words that have an edit value below or equal to the threshold
        // Only return the 3 closest words
        for (int i = 1; i <= editLimit; i++) {
            for (String key : sortedMap.keySet()) {
                if (sortedMap.get(key) == i) {
                    finalWords.add(key);
                }
                // Make sure the number of words added to finalWords is less than 3 before continuing
                if (finalWords.size() == 3) {
                    // If not, return the top 3 recommended words
                    return finalWords.toArray(new String[finalWords.size()]);
                }
            }
        }
        // Return the recommended replacement words
        return finalWords.toArray(new String[finalWords.size()]);
    }

    // Returns the minimum number of edits it would take to make one word match the other (using tabulation)
    public static int editDistance(String word1, String word2) {
        // 2D array representing possible paths for substrings
        int[][] path = new int[word1.length() + 1][word2.length() + 1];

        // Fill the first row with the set edit distance
        for (int i = 0; i < word2.length() + 1; i++) {
            path[0][i] = i;
        }
        // Then fill the first column with the set edit distances
        for (int i = 1; i < word1.length() + 1; i++) {
            path[i][0] = i;
        }

        // Iterate through the substring board bottom-up (tabulation approach)
        for (int i = 1; i < word1.length() + 1; i++) {
            for (int j = 1; j < word2.length() + 1; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    // If the current letters match, set current index to upper-left diagonal index
                    path[i][j] = path[i - 1][j - 1];
                }
                // Otherwise if the letters don't match...
                else {
                    // If the up-left diagonal index exists, set the current index to one more than
                    // the lowest of the left, up, and up-left diagonal indexes
                    path[i][j] = Math.min(Math.min(path[i][j - 1], path[i - 1][j]), path[i - 1][j - 1]) + 1;
                }
            }
        }

        // Return the length of the substring path requiring the minimum number of edits
        return path[word1.length()][word2.length()];
    }



    // Returns the length of the longest shared substring
    public static int longestSharedSubstring(String doc1, String doc2) {
        // 2D array representing possible paths for substrings
        int[][] path = new int[doc1.length()][doc2.length()];

        // Holder integers to represent indexes to the left and above of the current index
        int left;
        int up;

        // Iterate through the substring board bottom-up (tabulation approach)
        for (int i = 0; i < doc1.length(); i++) {
            for (int j = 0; j < doc2.length(); j++) {
                // If the current letters of each doc match, set the current index to the upper-left diagonal index + 1
                // Note- upper-left diagonal index represents longest substring within confines of current indexes of
                // each of the docs, so current index should be 1 greater because matching leads to adding another
                // letter (the current letter)
                if (doc1.charAt(i) == doc2.charAt(j)) {
                    // Make sure the upper left diagonal index exists
                    if (i > 0 && j > 0) {
                        path[i][j] = path[i - 1][j - 1] + 1;
                    }
                    // Otherwise set the current index to 1 (start of its own substring)
                    else {
                        path[i][j] = 1;
                    }
                }
                // Otherwise take in the length of the longest substring that has previously been found within the
                // current indexes of each of the docs on the board
                else {
                    // If there is a valid index above the current index, set 'up' to the index above
                    if (i > 0) {
                        up = path[i - 1][j];
                    }
                    // Otherwise set 'up' to an empty String
                    else {
                        up = 0;
                    }
                    // If there is a valid index to the left of the current index, set 'left' to the left index
                    if (j > 0) {
                        left = path[i][j - 1];
                    }
                    // Otherwise set 'left' to an empty String
                    else {
                        left = 0;
                    }

                    // Set the current index to the longer of the left and up indexes
                    if (left > up) {path[i][j] = left;}
                    else {path[i][j] = up;}
                }
            }
        }

        // Return the length of the longest substring
        return path[doc1.length() - 1][doc2.length() - 1];
    }


    /**
     * Loads a dictionary of words from the provided textfiles in the dictionaries directory.
     * @param dictionary The name of the textfile, [dictionary].txt, in the dictionaries directory.
     * @return An array of Strings containing all words in alphabetical order.
     */
    private static String[] loadDictionary(String dictionary)  {
        try {
            String line;
            BufferedReader dictReader = new BufferedReader(new FileReader("dictionaries/" + dictionary + ".txt"));
            line = dictReader.readLine();

            // Update instance variables with test data
            int n = Integer.parseInt(line);
            String[] words = new String[n];

            for (int i = 0; i < n; i++) {
                line = dictReader.readLine();
                words[i] = line;
            }
            return words;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}