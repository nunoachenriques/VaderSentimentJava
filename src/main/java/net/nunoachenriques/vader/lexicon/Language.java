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

import java.util.List;
import java.util.Map;

/**
 * A simple tokenizer of plain text.
 *
 * @author Nuno A. C. Henriques [nunoachenriques.net]
 */
public interface Language {

    /**
     * Gets the predefined punctuation list for texts in this language.
     *
     * @return A punctuation list.
     */
    List<String> getPunctuation();

    /**
     * Gets the predefined negative words list in this language.
     *
     * @return A negative words list.
     */
    List<String> getNegativeWords();

    /**
     * Gets the predefined booster (increment or decrement) dictionary.
     *
     * @return A map with key-value pairs of words and increment or decrement
     * valence.
     */
    Map<String, Float> getBoosterDictionary();

    /**
     * Gets the predefined idiomatic expressions valence map.
     *
     * @return A map with key-value pairs of idiomatic expressions and valence.
     */
    Map<String, Float> getSentimentLadenIdioms();

    /**
     * Gets predefined single words valence.
     *
     * @return A map with key-value pairs of words and valence.
     */
    Map<String, Float> getWordValenceDictionary();

    /**
     * Is NOT upper if is a URL of type "http://" or "HTTP://", a number as a
     * string, has one character in lower case. Is upper otherwise.
     *
     * @param token Text sample.
     * @return False if is not upper, true otherwise.
     */
    boolean isUpper(String token);
}
