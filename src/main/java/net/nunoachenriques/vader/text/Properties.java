package net.nunoachenriques.vader.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import net.nunoachenriques.vader.lexicon.English;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implements the text pre-processing steps of the input string for sentiment
 * analysis based on text properties. It uses the tokenizer available from the
 * text package.
 *
 * @author Animesh Pandey Created on 4/10/2016.
 * @see net.nunoachenriques.vader.lexicon.English
 */
public class Properties {

    private static final Logger LOGGER = LogManager.getLogger(Properties.class);
    private final String inputText;
    private ArrayList<String> wordsAndEmoticons;
    private ArrayList<String> wordsOnly;
    private boolean isCapDIff;

    public Properties(String inputText) throws IOException {
        this.inputText = inputText;
        setWordsAndEmoticons();
        setCapDIff(isAllCapDifferential());
    }

    /*
     * Tokenizes the input string, preserving the punctuation marks.
     */
    private void setWordsAndEmoticons() throws IOException {
        setWordsOnly();
        ArrayList<String> wordsAndEmoticonsList = new TokenizerLucene().defaultSplit(inputText);
        for (String currentWord : wordsOnly) {
            for (String currentPunc : English.PUNCTUATION_LIST) {
                String pWord = currentWord + currentPunc;
                Integer pWordCount = Collections.frequency(wordsAndEmoticonsList, pWord);
                while (pWordCount > 0) {
                    int index = wordsAndEmoticonsList.indexOf(pWord);
                    wordsAndEmoticonsList.remove(pWord);
                    wordsAndEmoticonsList.add(index, currentWord);
                    pWordCount = Collections.frequency(wordsAndEmoticonsList, pWord);
                }

                String wordP = currentPunc + currentWord;
                Integer wordPCount = Collections.frequency(wordsAndEmoticonsList, wordP);
                while (wordPCount > 0) {
                    int index = wordsAndEmoticonsList.indexOf(wordP);
                    wordsAndEmoticonsList.remove(wordP);
                    wordsAndEmoticonsList.add(index, currentWord);
                    wordPCount = Collections.frequency(wordsAndEmoticonsList, wordP);
                }
            }
        }
        this.wordsAndEmoticons = wordsAndEmoticonsList;
    }

    private void setWordsOnly() throws IOException {
        this.wordsOnly = new TokenizerLucene().removePunctuation(inputText);
    }

    private void setCapDIff(boolean capDIff) {
        isCapDIff = capDIff;
    }

    /*
     * True iff the the tokens have yelling words i.e. all caps in the
     * tokens e.g. [GET, THE, HELL, OUT] returns false [GET, the, HELL, OUT]
     * returns true [get, the, hell, out] returns false
     */
    private boolean isAllCapDifferential() {
        int countAllCaps = 0;
        for (String s : wordsAndEmoticons) {
            LOGGER.debug(s + "\t" + English.isUpper(s));
            if (English.isUpper(s)) {
                countAllCaps++;
            }
        }
        int capDifferential = wordsAndEmoticons.size() - countAllCaps;
        LOGGER.debug(wordsAndEmoticons.size() + "\t" + capDifferential + "\t" + countAllCaps);
        return (0 < capDifferential) && (capDifferential < wordsAndEmoticons.size());
    }

    public ArrayList<String> getWordsAndEmoticons() {
        return wordsAndEmoticons;
    }

    public ArrayList<String> getWordsOnly() {
        return wordsOnly;
    }

    public boolean isCapDIff() {
        return isCapDIff;
    }
}
