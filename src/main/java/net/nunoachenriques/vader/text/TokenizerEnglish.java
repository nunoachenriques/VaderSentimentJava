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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A simple tokenizer of plain text.
 *
 * @author Nuno A. C. Henriques [nunoachenriques.net]
 * @see net.nunoachenriques.vader.text.Tokenizer
 * @see java.util.regex.Pattern
 */
public class TokenizerEnglish
        implements Tokenizer {

    /**
     * All types of white space (e.g., line feed).
     *
     * <a href="https://github.com/python/cpython/blob/3.6/Lib/string.py">Python
     * string.whitespace</a> equivalent to <a
     * href="https://docs.oracle.com/javase/7/docs/api/index.html?java/util/regex/Pattern.html">Java \p{Space}</a>
     */
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\p{Space}");
    /**
     * The defined characters as punctuation except for contractions
     * (e.g., "can't" in english is kept as "can't" instead of "cant"), URL
     * (e.g., nunoachenriques.net), and name abbreviations
     * (e.g., J.R.R. => J.R.R).
     *
     * <a href="https://github.com/python/cpython/blob/3.6/Lib/string.py">Python
     * string.punctuation</a> equivalent to <a
     * href="https://docs.oracle.com/javase/7/docs/api/index.html?java/util/regex/Pattern.html">Java \p{Punct}</a>
     */
    private static final Pattern PUNCTUATION_EXCLUDE_CONTRACTION_PATTERN = Pattern.compile("[\\p{Punct}&&[^.']]|(?<=(^|\\s|\\p{Punct}))[.']|[.'](?=($|\\s|\\p{Punct}))");

    public TokenizerEnglish() {
    }

    @Override
    public List<String> split(String s, Pattern p) {
        return new LinkedList<>(Arrays.asList(p.split(s)));
    }

    @Override
    public List<String> cleanAndSplit(String s, Pattern p, Pattern c, String r) {
        return new LinkedList<>(Arrays.asList(p.split(c.matcher(s).replaceAll(r))));
    }

    @Override
    public List<String> splitWhitespace(String s) {
        return split(s, WHITESPACE_PATTERN);
    }

    @Override
    public List<String> cleanPunctuationAndSplitWhitespace(String s, String r) {
        return cleanAndSplit(s, WHITESPACE_PATTERN, PUNCTUATION_EXCLUDE_CONTRACTION_PATTERN, r);
    }

    @Override
    public void removeTokensBySize(List<String> l, int min, int max) {
        Iterator<String> i = l.iterator();
        while (i.hasNext()) {
            String t = i.next();
            if (t.length() < min || t.length() > max) {
                i.remove();
            }
        }
    }
}
