import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

    // HashMap to hold dictionary of words
    public static HashMap<String, Boolean> dict;
    // HashMap to hold possible words to be returned
    public static HashMap<String, Integer> posWords;
    // Integer to represent threshold for edits to typed words
    public static int editLimit;

    public Autocorrect(String[] words, int threshold) {
        // Convert the dictionary to a HashMap
        dict = new HashMap<>();
        for (int i = 0; i < words.length; i++) {
            dict.put(words[i], false);
        }
        // Set editLimit equal to the inputted threshold
        editLimit = threshold;
        // Initialize HashMap holding possible words
        posWords = new HashMap<>();

    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distnace, then sorted alphabetically.
     */
    public String[] runTest(String typed) {
        // If the typed word exists in the dictionary, return an empty array
        if (dict.containsKey(typed)) {
            return new String[0];
        }
        // Integer representing length of typed String
        int length = typed.length();
        // Add every word in the dictionary to posWords with the number of edits it takes to get to from the typed word
        for (String key : dict.keySet()) {
            // Put the dictionary word in posWords with the number of edits it took to get to from the typed word
            // the number of edits is equal to the longest shared substring + difference in length of the Strings
            posWords.put(key, key.length() - longestSharedSubstring(typed, key) + Math.abs(length - key.length()));
        }

        // ArrayList of (to be) ordered words to be returned
        ArrayList<String> finalWords = new ArrayList<>();
        // Add the words from posWords to the ArrayList with those requiring the fewest edits first
        for (int i = 1; i < editLimit; i++) {
            for (String key : posWords.keySet()) {
                if (posWords.get(key) == i) {
                    finalWords.add(key);
                }
            }
        }
        // Return the recommended replacement words
        return finalWords.toArray(new String[finalWords.size()]);
    }

    // Returns the length of the longest shared substring between two given words (using tabulation)
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
                    // Otherwise set 'up' to 0
                    else {
                        up = 0;
                    }
                    // If there is a valid index to the left of the current index, set 'left' to the left index
                    if (j > 0) {
                        left = path[i][j - 1];
                    }
                    // Otherwise set 'left' to 0
                    else {
                        left = 0;
                    }

                    // Set the current index to the longer of the left and up indexes
                    path[i][j] = Math.max(left, up);
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