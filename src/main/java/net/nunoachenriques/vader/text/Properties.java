/*
 * Copyright 2017 Nuno A. C. Henriques [nunoachenriques.net]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.nunoachenriques.vader.text;

import net.nunoachenriques.vader.lexicon.Language;

import java.util.Collections;
import java.util.List;

/**
 * Implements the text processing steps required by the VADER sentiment analysis
 * based on text properties. It uses the tokenizer available from the text
 * package, a different implementation may be coded and used.
 *
 * @author Nuno A. C. Henriques [nunoachenriques.net]
 * @see net.nunoachenriques.vader.lexicon.Language
 */
public class Properties {

    private static final int TOKEN_SIZE_MIN = 2;
    private static final int TOKEN_SIZE_MAX = Integer.MAX_VALUE;

    private final String text;
    private final Language language;
    private final Tokenizer tokenizer;
    private List<String> wordsAndEmoticons;
    private boolean isCapDifferential;

    /**
     * Default constructor. Does all the processing on instantiation, use the
     * getters afterwards.
     *
     * @param text The text string to be processed.
     * @param language The {@link Language} implementation class instance.
     * @param tokenizer The tokenizer to be used for text processing.
     */
    public Properties(String text, Language language, Tokenizer tokenizer) {
        this.text = text;
        this.language = language;
        this.tokenizer = tokenizer;
        setWordsAndEmoticons();
        setCapDifferential();
    }

    /**
     * Counts a letter frequency in a string.
     * From: https://stackoverflow.com/a/43747500/8418165
     *
     * @param s Text sample to search for the letter occurrences.
     * @param l The letter to be counted, use only one (e.g., "N").
     * @return The number of occurrences of the letter in the text sample.
     */
    public static int countLetter(String s, String l) {
        int numberOfLetters = 0;
        int positionOfLetter = s.indexOf(l);
        while (positionOfLetter != -1) {
            numberOfLetters++;
            positionOfLetter = s.indexOf(l, positionOfLetter + 1);
        }
        return numberOfLetters;
    }

    /**
     * Gets the list of words and emoticons extracted from the text.
     *
     * @return The list of words and emoticons strings.
     */
    public List<String> getWordsAndEmoticons() {
        return wordsAndEmoticons;
    }

    /**
     * Checks if capitalized (yelling) words exists. Yelling is differential.
     * Only mixed capitalized between non-capitalized make a difference
     * (e.g., [GET, THE, HELL, OUT] returns false, [GET, the, HELL, OUT] returns
     * true, [get, the, hell, out] returns false).
     *
     * @return True if capitalized words are differentiating, false otherwise.
     */
    public boolean isCapDifferential() {
        return isCapDifferential;
    }

    private void setWordsAndEmoticons() {
        // words only!
        List<String> wordsOnly = tokenizer.cleanPunctuationAndSplitWhitespace(text, " ");
        tokenizer.removeTokensBySize(wordsOnly, TOKEN_SIZE_MIN, TOKEN_SIZE_MAX);
        // words plus emoticons!
        List<String> wordsAndEmoticonsList = tokenizer.splitWhitespace(text);
        tokenizer.removeTokensBySize(wordsAndEmoticonsList, TOKEN_SIZE_MIN, TOKEN_SIZE_MAX);
        for (String currentWord : wordsOnly) {
            for (String currentPunctuation : language.getPunctuation()) {
                String pWord = currentWord + currentPunctuation;
                Integer pWordCount = Collections.frequency(wordsAndEmoticonsList, pWord);
                while (pWordCount > 0) {
                    int index = wordsAndEmoticonsList.indexOf(pWord);
                    wordsAndEmoticonsList.remove(pWord);
                    wordsAndEmoticonsList.add(index, currentWord);
                    pWordCount = Collections.frequency(wordsAndEmoticonsList, pWord);
                }
                String wordP = currentPunctuation + currentWord;
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

    /*
     * True iff the tokens have yelling words (e.g., [GET, THE, HELL, OUT]
     * returns false, [GET, the, HELL, OUT] returns true,
     * [get, the, hell, out] returns false).
     */
    private void setCapDifferential() {
        int countAllCaps = 0;
        for (String s : wordsAndEmoticons) {
            if (language.isUpper(s)) {
                countAllCaps++;
            }
        }
        int capDifferential = wordsAndEmoticons.size() - countAllCaps;
        isCapDifferential = (0 < capDifferential)
                && (capDifferential < wordsAndEmoticons.size());
    }
}
