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
        // Convert the dictionary to a HashMap for faster lookups
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

        // Add every word in the dictionary to posWords with the number of edits it takes to get to from the typed word
        for (String key : dict.keySet()) {
            // Put the dictionary word in posWords with the number of edits it took to get to from the typed word
            posWords.put(key, diff(typed, key));
        }

        // ArrayList of (to be) ordered words to be returned
        ArrayList<String> finalWords = new ArrayList<>();
        // Add the words from posWords to the ArrayList with those requiring the fewest edits first
        for (int i = 1; i <= editLimit; i++) {
            for (String key : posWords.keySet()) {
                if (posWords.get(key) == i) {
                    finalWords.add(key);
                }
            }
        }
        // Return the recommended replacement words
        return finalWords.toArray(new String[finalWords.size()]);
    }

    // Returns the minimum number of edits it would take to make one word match the other (using tabulation)
    public static int diff(String word1, String word2) {
        // 2D array representing possible paths for substrings
        int[][] path = new int[word1.length()][word2.length()];

        // Iterate through the substring board bottom-up (tabulation approach)
        for (int i = 0; i < word1.length(); i++) {
            for (int j = 0; j < word2.length(); j++) {
                // If current letters of the words match, set current index to upper-left diagonal index (if possible)
                if (word1.charAt(i) == word2.charAt(j)) {
                    // Make sure the upper left diagonal index exists
                    if (i > 0 && j > 0) {
                        path[i][j] = path[i - 1][j - 1];
                    }
                    // If not, instead set the current index to the existing of the left or up indexes
                    else {
                        if (j > 0) {
                            path[i][j] = path[i][j - 1];
                        }
                        else if (i > 0) {
                            path[i][j] = path[i - 1][j];
                        }
                        // Otherwise (if both i and j are 0 and the current letters match) set the current index to 0
                        else {
                            path[i][j] = 0;
                        }
                    }
                }
                // Otherwise if the letters don't match...
                else {
                    // If the up-left diagonal index exists, set the current index to its value + 1
                    if (i > 0 && j > 0) {
                        path[i][j] = path[i - 1][j - 1] + 1;
                    }
                    // Otherwise continue and set the current index to one more than the lower of the left or up indexes
                    else {
                        if (j > 0) {
                            path[i][j] = path[i][j - 1] + 1;
                        }
                        else if (i > 0) {
                            path[i][j] = path[i - 1][j] + 1;
                        }
                        // Otherwise (if i and j are 0 and the current letters don't match) set the current index to 1
                        else {
                            path[i][j] = 1;
                        }
                    }
                }
            }
        }

        // Return the length of the substring path requiring the minimum number of edits
        return path[word1.length() - 1][word2.length() - 1];
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