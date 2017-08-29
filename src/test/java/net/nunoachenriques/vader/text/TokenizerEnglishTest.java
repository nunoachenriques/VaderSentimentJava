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

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Testing the tokenizer comparing with the Apache Lucene one.
 * Different results, just for completion sake.
 *
 * @author Nuno A. C. Henriques [nunoachenriques.net]
 */
public class TokenizerEnglishTest {

    private static final ClassLoader LOADER = TokenizerEnglishTest.class.getClassLoader();
    private static Tokenizer tokenizer;

    @Before
    public void init() {
        tokenizer = new TokenizerEnglish();
    }

    @Test
    public void testAmazonReviewSnippetsGTV() {
        testTokenizerVsLuceneFromAnimeshPandey("amazonReviewSnippets_GroundTruth_vader.tsv");
    }

    @Test
    public void testMovieReviewSnippetsGTV() {
        testTokenizerVsLuceneFromAnimeshPandey("movieReviewSnippets_GroundTruth_vader.tsv");
    }

    @Test
    public void testNytEditorialSnippetsGTV() {
        testTokenizerVsLuceneFromAnimeshPandey("nytEditorialSnippets_GroundTruth_vader.tsv");
    }

    @Test
    public void testTweetsGTV() {
        testTokenizerVsLuceneFromAnimeshPandey("tweets_GroundTruth_vader.tsv");
    }

    private void testTokenizerVsLuceneFromAnimeshPandey(String file) {
        InputStream is = LOADER.getResourceAsStream(file);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\t");
                String text = data[5];
                // words only
                List<String> wordsOnly = tokenizer.cleanPunctuationAndSplitWhitespace(text, " ");
                tokenizer.removeTokensBySize(wordsOnly, 2, Integer.MAX_VALUE);
                List<String> wordsOnlyLucene = cleanPunctuationAndSplitWhitespaceLucene(text);
                Assert.assertEquals("wordsOnly!", wordsOnlyLucene, wordsOnly);
                // words plus emoticons!
                List<String> wordsAndEmoticonsList = tokenizer.splitWhitespace(text);
                tokenizer.removeTokensBySize(wordsAndEmoticonsList, 2, Integer.MAX_VALUE);
                List<String> wordsAndEmoticonsListLucene = splitWhitespaceLucene(text);
                Assert.assertEquals("wordsAndEmoticons", wordsAndEmoticonsListLucene, wordsAndEmoticonsList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // From: Animesh Pandey original Java port from Python.
    private List<String> splitWhitespaceLucene(String s) {
        StringReader reader = new StringReader(s);
        org.apache.lucene.analysis.Tokenizer whiteSpaceTokenizer = new WhitespaceTokenizer();
        whiteSpaceTokenizer.setReader(reader);
        ArrayList<String> tokenizedString = null;
        try (TokenStream tokenStream = new LengthFilter(whiteSpaceTokenizer, 2, Integer.MAX_VALUE)) {
            final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            tokenizedString = new ArrayList<>();
            while (tokenStream.incrementToken()) {
                tokenizedString.add(charTermAttribute.toString());
            }
            tokenStream.end();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return tokenizedString;
    }

    // From: Animesh Pandey original Java port from Python.
    private List<String> cleanPunctuationAndSplitWhitespaceLucene(String s) {
        StringReader reader = new StringReader(s);
        StandardTokenizer removePunctuationTokenizer = new StandardTokenizer();
        removePunctuationTokenizer.setReader(reader);
        ArrayList<String> tokenizedString = null;
        try (TokenStream tokenStream = new LengthFilter(removePunctuationTokenizer, 2, Integer.MAX_VALUE)) {
            final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            tokenizedString = new ArrayList<>();
            while (tokenStream.incrementToken()) {
                tokenizedString.add(charTermAttribute.toString());
            }
            tokenStream.end();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return tokenizedString;
    }
}
