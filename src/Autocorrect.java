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

    }

    // Recursive method for deleting letters
    public ArrayList<String> deleteLetter(String word) {
        // ArrayList to contain valid altered versions of the original word
        ArrayList<String> validWords = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            String newWord = word.substring(0,i) + word.substring(i);
            if (dict.containsKey(newWord)) {
                validWords.add(newWord);
            }
        }
        // Return the ArrayList of valid altered versions of the original word
        return validWords;
    }

    // Recursive method for adding letters
    public ArrayList<String> addLetter(String word) {
        // ArrayList to contain valid altered versions of the original word
        ArrayList<String> validWords = new ArrayList<>();
        String newWord;
        for (int i = 0; i < word.length(); i++) {
            for (int j = 97; j <= 122; j++) {
                newWord = word.substring(0, i) + j + word.substring(i);
                if (dict.containsKey(newWord)) {
                    validWords.add(newWord);
                }
            }
        }
        // Return the ArrayList of valid altered versions of the original word
        return validWords;
    }

    // Recursive method for swapping letters
    public ArrayList<String> swapLetter(String word) {
        // ArrayList to contain valid altered versions of the original word
        ArrayList<String> validWords = new ArrayList<>();
        String newWord;
        for (int i = 0; i < word.length(); i++) {
            for (int j = 97; j <= 122; j++) {
                newWord = word.substring(0,i) + j + word.substring(i + 1);
                if (dict.containsKey(newWord)) {
                    validWords.add(newWord);
                }
            }
        }
        // Return the ArrayList of valid altered versions of the original word
        return validWords;
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

        // HashMap to contain possible valid versions of the misspelled word
        // The keys are the words, and the values are the number of edits away from the original word
        HashMap<String, Integer> posWords = new HashMap<>();

        // ArrayLists to hold the various lists of possible edited versions of the typed word
        ArrayList<String> delete;
        ArrayList<String> add;
        ArrayList<String> swap;

        // Add typed temporarily to posWords
        posWords.put(typed, 0);

        // While the threshold hasn't been reached run another round of edits to find possible altered versions of typed
        for (String word : posWords.keySet()) {
            for (int i = 0; i < editLimit; i++) {
                // Get the valid words possible from deleting a letter
                delete = deleteLetter(typed);
                for (String newWord : delete) {
                    // Make sure each possible altered word hasn't already been added to the HashMap before adding it
                    if (!posWords.containsKey(word)) {
                        posWords.put(newWord, i);
                    }
                }
                // Get the valid words possible from adding a letter
                add = addLetter(typed);
                for (String newWord : add) {
                    // Make sure each possible altered word hasn't already been added to the HashMap before adding it
                    if (!posWords.containsKey(word)) {
                        posWords.put(newWord, i);
                    }
                }
                // Get the valid words possible from swapping a letter
                swap = swapLetter(typed);
                for (String newWord : swap) {
                    // Make sure each possible altered word hasn't already been added to the HashMap before adding it
                    if (!posWords.containsKey(word)) {
                        posWords.put(newWord, i);
                    }
                }
            }
        }

        // Remove the original misspelled word from the HashMap
        posWords.remove(typed);


        return new String[0];
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