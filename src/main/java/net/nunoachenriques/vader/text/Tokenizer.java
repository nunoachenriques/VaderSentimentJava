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

import java.util.List;
import java.util.regex.Pattern;

/**
 * A simple tokenizer of plain text.
 *
 * @author Nuno A. C. Henriques [nunoachenriques.net]
 * @see java.util.regex.Pattern
 */
public interface Tokenizer {

    /**
     * Classic text split in tokens based on a {@link Pattern}.
     *
     * @param s The text to be split (tokenized).
     * @param p The compiled {@link Pattern} to use on text split.
     * @return The tokens list after text split (tokenization).
     */
    List<String> split(String s, Pattern p);

    /**
     * Clean text based on a {@link Pattern} and then a classic
     * text split in tokens also based on a {@link Pattern}.
     *
     * @param s The text to be tokenized.
     * @param p The compiled {@link Pattern} to use on text split.
     * @param c The compiled {@link Pattern} to match and remove from text.
     * @param r The text ({@code string}) to replace (e.g., "").
     * @return The tokens list after text clean and split.
     */
    List<String> cleanAndSplit(String s, Pattern p, Pattern c, String r);

    /**
     * Classic white space (e.g., {@code Pattern.compile("\\p{Space}")}) text
     * split in tokens.
     *
     * @param s Text to be split.
     * @return The tokens list after text white space split (tokenization).
     */
    List<String> splitWhitespace(String s);

    /**
     * First, punctuation (e.g., {@code Pattern.compile("\\p{Punct}")}) is
     * removed from text and then a classic white space
     * (e.g., {@code Pattern.compile("\\p{Space}")}) text split in tokens.
     *
     * @param s Text to be cleaned and split.
     * @param r The text ({@code string}) to replace (e.g., "").
     * @return The tokens list after text clean and white space split.
     */
    List<String> cleanPunctuationAndSplitWhitespace(String s, String r);

    /**
     * Removes tokens (items in the list) that do not comply with the
     * required {@code min} and {@code max} length.
     *
     * @param l List of text ({@code String}) tokens.
     * @param min Minimum length.
     * @param max Maximum length.
     */
    void removeTokensBySize(List<String> l, int min, int max);
}
