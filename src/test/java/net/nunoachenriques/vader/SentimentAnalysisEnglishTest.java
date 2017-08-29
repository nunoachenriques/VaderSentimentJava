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
package net.nunoachenriques.vader;

import net.nunoachenriques.vader.lexicon.English;
import net.nunoachenriques.vader.lexicon.Language;
import net.nunoachenriques.vader.text.Tokenizer;
import net.nunoachenriques.vader.text.TokenizerEnglish;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Testing using the Ground Truth files from the author of the original
 * implementation of the VADER algorithm in Python. 100% OK means the port to
 * Java is compliant with the original algorithm.
 * This is for the {@link English} language.
 *
 * @author Nuno A. C. Henriques [nunoachenriques.net]
 * @see English
 * @see TokenizerEnglish
 */
public class SentimentAnalysisEnglishTest {

    private static final ClassLoader LOADER = SentimentAnalysisEnglishTest.class.getClassLoader();
    private static final int MODE_GET_T_L = 0; // SentimentAnalysis()
    private static final int MODE_GET_L = 1; // SentimentAnalysis()
    private static final int MODE_GET_ = 2; // SentimentAnalysis(t,l)
    private static Language language;
    private static Tokenizer tokenizer;

    @Before
    public void init() {
        language = new English();
        tokenizer = new TokenizerEnglish();
    }

    @Test
    public void testAmazonReviewSnippetsGTV() {
        testGroundTruth("amazonReviewSnippets_GroundTruth_vader.tsv");
    }

    @Test
    public void testMovieReviewSnippetsGTV() {
        testGroundTruth("movieReviewSnippets_GroundTruth_vader.tsv");
    }

    @Test
    public void testNytEditorialSnippetsGTV() {
        testGroundTruth("nytEditorialSnippets_GroundTruth_vader.tsv");
    }

    @Test
    public void testTweetsGTV() {
        testGroundTruth("tweets_GroundTruth_vader.tsv");
    }

    @Test
    public void testTweetsGTVModeGetTL() {
        testGroundTruth("tweets_GroundTruth_vader.tsv", language, tokenizer, MODE_GET_T_L);
    }

    @Test
    public void testTweetsGTVModeGetL() {
        testGroundTruth("tweets_GroundTruth_vader.tsv", language, tokenizer, MODE_GET_L);
    }

    @Test
    public void testGetAvailableLanguages() {
        SentimentAnalysis sa = new SentimentAnalysis();
        Assert.assertTrue("Fails to found 'en' (English)!", sa.getAvailableLanguages().contains("en"));
    }

    private void testGroundTruth(String file) {
        testGroundTruth(file, language, tokenizer, MODE_GET_);
    }

    private void testGroundTruth(String file, Language l, Tokenizer t, int mode) {
        InputStream is = LOADER.getResourceAsStream(file);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            SentimentAnalysis sa;
            switch (mode) {
                case MODE_GET_T_L:
                case MODE_GET_L:
                    sa = new SentimentAnalysis();
                    break;
                case MODE_GET_:
                default:
                    sa = new SentimentAnalysis(l, t);
                    break;
            }
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\t");
                float expectedNegativeScore = Float.parseFloat(data[1]);
                float expectedNeutralScore = Float.parseFloat(data[2]);
                float expectedPositiveScore = Float.parseFloat(data[3]);
                float expectedCompoundScore = Float.parseFloat(data[4]);
                String text = data[5];
                Map<String, Float> sp;
                switch (mode) {
                    case MODE_GET_T_L:
                        sp = sa.getSentimentAnalysis(text, l, t);
                        break;
                    case MODE_GET_L:
                        sp = sa.getSentimentAnalysis(text, "en");
                        break;
                    case MODE_GET_:
                    default:
                        sp = sa.getSentimentAnalysis(text);
                        break;
                }
                float actualNegativeScore = sp.get("negative");
                float actualPositiveScore = sp.get("positive");
                float actualNeutralScore = sp.get("neutral");
                float actualCompoundScore = sp.get("compound");
                Assert.assertFalse(
                        getErrorMessage(text, actualNegativeScore, expectedNegativeScore, "Negative Score"),
                        error(actualNegativeScore, expectedNegativeScore)
                );
                Assert.assertFalse(
                        getErrorMessage(text, actualPositiveScore, expectedPositiveScore, "Positive Score"),
                        error(actualPositiveScore, expectedPositiveScore)
                    );
                Assert.assertFalse(
                        getErrorMessage(text, actualNeutralScore, expectedNeutralScore, "Neutral Score"),
                        error(actualNeutralScore, expectedNeutralScore)
                );
                Assert.assertFalse(
                        getErrorMessage(text, actualCompoundScore, expectedCompoundScore, "Compound Score"),
                        error(actualCompoundScore, expectedCompoundScore)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getErrorMessage(String message, float actual, float expected, String type) {
        return String.format("Test String: %s => %s (actual = %f, expected = %f)", message, type, actual, expected);
    }

    private int noOfDecimalDigits(float value) {
        String text = Float.toString(Math.abs(value));
        return text.length() - text.indexOf('.') - 1;
    }

    /**
     * Due to Floating Point Precision errors results used to differ by 1
     * (e.g., 0.0345 from NLTK might be 0.0344 or 0.0346 when calculated in
     * Java). This is mainly due to rounding off errors. To handle this than the
     * difference between two values should not be greater than 1.0.
     *
     * <pre>
     * error(0.0345, 0.0344) => false
     * error(0.0345, 0.0346) => false
     * error(0.0345, 0.0348) => true
     * </pre>
     *
     * @param actual Actual error value.
     * @param expected Running error value.
     * @return true iff the difference between actual and expected is greater
     * than 1.0
     */
    private boolean error(float actual, float expected) {
        int maxPlaces = Math.max(noOfDecimalDigits(actual), noOfDecimalDigits(expected));
        return ((Math.abs(Math.abs(actual * maxPlaces) - Math.abs(expected * maxPlaces))) > 1.0);
    }
}
