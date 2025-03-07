import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
    public String deleteLetter(String word, int editScore) {
        if (dict.containsKey(word)) {
            return
        }
    }

    // Recursive method for swapping letters
    public String[] swapLetter(String word, int editScore) {

    }

    // Recursive method for adding letters
    public String[] addLetter(String word, int editScore) {

    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distnace, then sorted alphabetically.
     */
    public String[] runTest(String typed) {

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