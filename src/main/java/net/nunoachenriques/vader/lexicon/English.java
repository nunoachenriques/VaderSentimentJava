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
package net.nunoachenriques.vader.lexicon;

import org.pmw.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains the constants that are the used by the sentiment analysis
 * for the English language. The constants are the same as the ones used in the
 * original Python implementation.
 *
 * @author Nuno A. C. Henriques [nunoachenriques.net]
 * @see <a href="http://www.nltk.org/_modules/nltk/sentiment/vader.html">NLTK
 * source</a>
 * @see
 * <a href="https://github.com/cjhutto/vaderSentiment/blob/master/vaderSentiment/vaderSentiment.py">
 * vaderSentiment original Python source</a>
 */
public final class English
        implements Language {

    private static final ClassLoader LOADER = English.class.getClassLoader();
    private static final String LEXICON_FILE = "net/nunoachenriques/vader/lexicon/english.txt";
    private static final float BOOSTER_WORD_INCREMENT = 0.293f;
    private static final float DAMPENER_WORD_DECREMENT = -0.293f;
    private static final List<String> PUNCTUATION = Arrays.asList(
            ".", "!", "?", ",", ";", ":", "-", "'", "\"",
            "!!", "!!!", "??", "???", "?!?", "!?!", "?!?!", "!?!?"
    );
    private static final List<String> NEGATIVE_WORDS = Arrays.asList(
            "aint", "arent",
            "cannot", "cant", "couldnt",
            "darent",
            "didnt", "doesnt",
            "ain't", "aren't",
            "can't", "couldn't",
            "daren't",
            "didn't", "doesn't",
            "dont",
            "hadnt", "hasnt", "havent",
            "isnt",
            "mightnt", "mustnt",
            "neither",
            "don't",
            "hadn't", "hasn't", "haven't",
            "isn't",
            "mightn't", "mustn't",
            "neednt", "needn't",
            "never", "none", "nope", "nor", "not", "nothing", "nowhere",
            "oughtnt", "shant", "shouldnt", "uhuh", "wasnt", "werent",
            "oughtn't", "shan't", "shouldn't", "uh-uh", "wasn't", "weren't",
            "without", "wont", "wouldnt",
            "won't", "wouldn't", "rarely", "seldom", "despite"
    );

    private static final Map<String, Float> BOOSTER_DICTIONARY = createBoosterDictionary();
    private static Map<String, Float> createBoosterDictionary() {
        Map<String, Float> m = new HashMap<>();
        m.put("decidedly", BOOSTER_WORD_INCREMENT);
        m.put("uber", BOOSTER_WORD_INCREMENT);
        m.put("barely", DAMPENER_WORD_DECREMENT);
        m.put("particularly", BOOSTER_WORD_INCREMENT);
        m.put("enormously", BOOSTER_WORD_INCREMENT);
        m.put("less", DAMPENER_WORD_DECREMENT);
        m.put("absolutely", BOOSTER_WORD_INCREMENT);
        m.put("kinda", DAMPENER_WORD_DECREMENT);
        m.put("flipping", BOOSTER_WORD_INCREMENT);
        m.put("awfully", BOOSTER_WORD_INCREMENT);
        m.put("purely", BOOSTER_WORD_INCREMENT);
        m.put("majorly", BOOSTER_WORD_INCREMENT);
        m.put("substantially", BOOSTER_WORD_INCREMENT);
        m.put("partly", DAMPENER_WORD_DECREMENT);
        m.put("remarkably", BOOSTER_WORD_INCREMENT);
        m.put("really", BOOSTER_WORD_INCREMENT);
        m.put("sort of", DAMPENER_WORD_DECREMENT);
        m.put("little", DAMPENER_WORD_DECREMENT);
        m.put("fricking", BOOSTER_WORD_INCREMENT);
        m.put("sorta", DAMPENER_WORD_DECREMENT);
        m.put("amazingly", BOOSTER_WORD_INCREMENT);
        m.put("kind of", DAMPENER_WORD_DECREMENT);
        m.put("just enough", DAMPENER_WORD_DECREMENT);
        m.put("fucking", BOOSTER_WORD_INCREMENT);
        m.put("occasionally", DAMPENER_WORD_DECREMENT);
        m.put("somewhat", DAMPENER_WORD_DECREMENT);
        m.put("kindof", DAMPENER_WORD_DECREMENT);
        m.put("friggin", BOOSTER_WORD_INCREMENT);
        m.put("incredibly", BOOSTER_WORD_INCREMENT);
        m.put("totally", BOOSTER_WORD_INCREMENT);
        m.put("marginally", DAMPENER_WORD_DECREMENT);
        m.put("more", BOOSTER_WORD_INCREMENT);
        m.put("considerably", BOOSTER_WORD_INCREMENT);
        m.put("fabulously", BOOSTER_WORD_INCREMENT);
        m.put("sort of", DAMPENER_WORD_DECREMENT);
        m.put("hardly", DAMPENER_WORD_DECREMENT);
        m.put("very", BOOSTER_WORD_INCREMENT);
        m.put("sortof", DAMPENER_WORD_DECREMENT);
        m.put("kind-of", DAMPENER_WORD_DECREMENT);
        m.put("scarcely", DAMPENER_WORD_DECREMENT);
        m.put("thoroughly", BOOSTER_WORD_INCREMENT);
        m.put("quite", BOOSTER_WORD_INCREMENT);
        m.put("most", BOOSTER_WORD_INCREMENT);
        m.put("completely", BOOSTER_WORD_INCREMENT);
        m.put("frigging", BOOSTER_WORD_INCREMENT);
        m.put("intensely", BOOSTER_WORD_INCREMENT);
        m.put("utterly", BOOSTER_WORD_INCREMENT);
        m.put("highly", BOOSTER_WORD_INCREMENT);
        m.put("extremely", BOOSTER_WORD_INCREMENT);
        m.put("unbelievably", BOOSTER_WORD_INCREMENT);
        m.put("almost", DAMPENER_WORD_DECREMENT);
        m.put("especially", BOOSTER_WORD_INCREMENT);
        m.put("fully", BOOSTER_WORD_INCREMENT);
        m.put("frickin", BOOSTER_WORD_INCREMENT);
        m.put("tremendously", BOOSTER_WORD_INCREMENT);
        m.put("exceptionally", BOOSTER_WORD_INCREMENT);
        m.put("flippin", BOOSTER_WORD_INCREMENT);
        m.put("hella", BOOSTER_WORD_INCREMENT);
        m.put("so", BOOSTER_WORD_INCREMENT);
        m.put("greatly", BOOSTER_WORD_INCREMENT);
        m.put("hugely", BOOSTER_WORD_INCREMENT);
        m.put("deeply", BOOSTER_WORD_INCREMENT);
        m.put("unusually", BOOSTER_WORD_INCREMENT);
        m.put("entirely", BOOSTER_WORD_INCREMENT);
        m.put("slightly", DAMPENER_WORD_DECREMENT);
        m.put("effing", BOOSTER_WORD_INCREMENT);
        return m;
    }
    private static final Map<String, Float> SENTIMENT_LADEN_IDIOMS = createSentimentLadenIdioms();
    private static Map<String, Float> createSentimentLadenIdioms() {
        Map<String, Float> m = new HashMap<>();
        m.put("cut the mustard", 2f);
        m.put("bad ass", 1.5f);
        m.put("kiss of death", -1.5f);
        m.put("yeah right", -2f);
        m.put("the bomb", 3f);
        m.put("hand to mouth", -2f);
        m.put("the shit", 3f);
        return m;
    }
    private static final Map<String, Float> WORD_VALENCE_DICTIONARY = getWordValenceDictionary(LEXICON_FILE);
    private static Map<String, Float> getWordValenceDictionary(String filename) {
        InputStream lexFile = LOADER.getResourceAsStream(filename);
        Map<String, Float> lexDictionary = new HashMap<>();
        if (lexFile != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(lexFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] lexFileData = line.split("\\t");
                    String currentText = lexFileData[0];
                    Float currentTextValence = Float.parseFloat(lexFileData[1]);
                    lexDictionary.put(currentText, currentTextValence);
                }
            } catch (IOException ioe) {
                Logger.error(ioe);
            }
        }
        return lexDictionary;
    }

    /**
     * Default constructor.
     */
    public English() {
        // Void.
    }

    @Override
    public List<String> getPunctuation() {
        return PUNCTUATION;
    }

    @Override
    public List<String> getNegativeWords() {
        return NEGATIVE_WORDS;
    }

    @Override
    public Map<String, Float> getBoosterDictionary() {
        return BOOSTER_DICTIONARY;
    }

    @Override
    public Map<String, Float> getSentimentLadenIdioms() {
        return SENTIMENT_LADEN_IDIOMS;
    }

    @Override
    public Map<String, Float> getWordValenceDictionary() {
        return WORD_VALENCE_DICTIONARY;
    }

    @Override
    public boolean isUpper(String token) {
        if (token.toLowerCase().startsWith("http://")) {
            return false;
        }
        if (!token.matches(".*[a-zA-Z]+.*")) {
            return false;
        }
        for (int i = 0; i < token.length(); i++) {
            if (Character.isLowerCase(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
