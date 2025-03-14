import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

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

//    public static void main(String[] args) {
//        // Create a new Autocorrect object
//        // Input a large dictionary that's loaded in, and a
//        Autocorrect auto = new Autocorrect(loadDictionary("large"), 3);
//
//        while(true) {
//            auto.runTest(args[0]);
//        }
//    }

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
        // If the typed word exists in the dictionary, return an empty array
        if (dict.containsKey(typed)) {
            return new String[0];
        }

        // Integer to hold length of typed word
        int length = typed.length();
        // Update the edit value of every word in the dictionary
        for (String key : dict.keySet()) {
            // Only add words that are within 3 of the typed word's length
            if (!(key.length() > length + editLimit || key.length() < length - editLimit)) {
                // Add the minimum number of edits it would take to convert typed to the dict word
                dict.put(key, editDistance(typed, key));
            }
        }

        // THE WAY I MAKE THE FINAL LIST ALPHABETICALLY SORTED WAS MADE WITH THE HELP OF GEMINI, AN LLM
        // Create a new TreeMap so keys can be sorted alphabetically
        TreeMap<String, Integer> sortedMap = new TreeMap<>(dict);

        // ArrayList of (to be) ordered words to be returned
        ArrayList<String> finalWords = new ArrayList<>();
        // Add the words from dict to the ArrayList with those requiring the fewest edits first
        // Only add words that have an edit value below or equal to the threshold
        for (int i = 1; i <= editLimit; i++) {
            for (String key : sortedMap.keySet()) {
                if (sortedMap.get(key) == i) {
                    finalWords.add(key);
                }
            }
        }
        // Return the recommended replacement words
        return finalWords.toArray(new String[finalWords.size()]);
    }

    // Returns the minimum number of edits it would take to make one word match the other (using tabulation)
    public static int editDistance(String word1, String word2) {
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
                        // If the left index exists, set the current index to the left index
                        if (j > 0) {
                            path[i][j] = path[i][j - 1];
                            // Make sure the current character hasn't already appeared in its given word
                            if (word2.substring(0, j).contains(word2.charAt(j) + "")) {
                                // If the current character is a repeat, add 1 to the current index
                                path[i][j]++;
                            }
                        }
                        // Otherwise if the up index exists, set the current index to the up index
                        else if (i > 0) {
                            path[i][j] = path[i - 1][j];
                            // Make sure the current character hasn't already appeared in its given word
                            if (word1.substring(0, i).contains(word1.charAt(i) + "")) {
                                // If the current character is a repeat, add 1 to the current index
                                path[i][j]++;
                            }
                        }
                        // Otherwise (if both i and j are 0 and the current letters match) set the current index to 0
                        else {
                            path[i][j] = 0;
                        }
                    }
                }
                // Otherwise if the letters don't match...
                else {
                    // If the up-left diagonal index exists, set the current index to one more than
                    // the lowest of the left, up, and up-left diagonal indexes
                    if (i > 0 && j > 0) {
                        path[i][j] = Math.min(path[i][j - 1], path[i - 1][j]);
                        path[i][j] = Math.min(path[i][j], path[i - 1][j - 1]) + 1;
                    }
                    // Otherwise set the current index to one more than the existing of the left or up indexes
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