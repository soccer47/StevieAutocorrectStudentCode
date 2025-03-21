After loading in the dictionary text file and converting all the words into a HashMap (for constant time lookups), 
my Autocorrect method will then run the runTest() method. This method will first make sure that the typed word doesn't 
exist in the dictionary (if the word is a valid word, an array of length 1 will be returned, containing the String 
"This word already exists in the dictionary."). If the word doesn't exist in the dictionary, my method then proceeds to 
traverse through every word in the dictionary, with two methods. For every dictionary word, I make sure that the
dictionary word's length is within threshold distance (which I decided to set to 4 after trial and error testing) of the 
typed word's length, and I subsequently run my edit distance algorithm.

After the initial list of dictionary words has been shortened significantly, I then run the editDistance method on each 
remaining word in the dictionary, and set these integer as the values in the dictionary HashMap for each word/key. The 
editDistance() method uses tabulation, and has time complexity proportional to the product of the length of the two 
words. The runTest() method finds and returns the lowest number of edits needed to convert 1 word to another (in this 
case the typed word to the given dictionary word) using a tabulation approach to find this distance in time proportional 
to the product of the lengths of the two inputted words. Edits can include a character swap, delete, or addition, with 
all of these edits counting being weighted as 1 edit. After creating a 2D array of integers (with the height and width 
being equal to word1's length + 1 and word2's length + 1 respectively) to represent the indexes of the two inputted 
words, I use a nested for loop to perform row-major traversal through the array, filling out each index as I go. The 
value at each index represents the minimum number of edits needed to go from the given substring of 1 word to the given 
substring of the other, with the bottom right index containing the minimum edit distance from word1 to word2 (and vice 
versa). When filling out each index, the given index is set to the lowest edit distance out of the up, left, and up-left 
diagonal indexes in the array, representing adding the next letter to word1, adding the next letter to word2, and 
swapping the next letters in both words respectively. After the final/bottom right index has been filled, the value 
contained at this index is returned as the minimum edit distance between the two words.

After the edit distances to the typed word has been found and added as the value for every word in the dictionary
HashMap, I use a for-loop to find the remaining three words with the lowest edit distance to the word, adding them to an
array of Strings. If there are multiple words with the same edit distance and adding them all would exceed 3 returned 
words, I only return the first words that are compared by my for-loop before the limit has been met.