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
import net.nunoachenriques.vader.text.Properties;
import net.nunoachenriques.vader.text.Tokenizer;
import net.nunoachenriques.vader.text.TokenizerEnglish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>The SentimentAnalysis class is the main class for VADER Sentiment Analysis.
 * Since version 2.0 is multi-language ready and Android ready! Use cases:</p>
 *
 * <h2>I. Several samples, same language.</h2>
 * <pre>
 * ...
 * <code>
 * SentimentAnalysis sa = new SentimentAnalysis(new TokenizerEnglish(), new English());
 * Map<String,Float> sp;
 * String s1 = "VADER is smart, handsome, and funny!";
 * String s2 = "VADER sometimes fails too as everyone else!";
 *
 * sp = sa.getSentimentAnalysis(s1);
 * System.out.println(s1 + " *** " + sp.toString());
 * sp = sa.getSentimentAnalysis(s2);
 * System.out.println(s2 + " *** " + sp.toString());
 * </code>
 * ...
 * </pre>
 *
 * <h2>II. Several samples, multiple languages.</h2>
 * <pre>
 * ...
 * <code>
 * SentimentAnalysis sa = new SentimentAnalysis();
 * Map<String,Float> sp;
 * String s1 = "VADER is smart, handsome, and funny!";
 * String s2 = "VADER por vezes falha como todas as pessoas!";
 *
 * sp = sa.getSentimentAnalysis(s1, new TokenizerEnglish(), new English());
 * System.out.println(s1 + " *** " + sp.toString());
 * sp = sa.getSentimentAnalysis(s2, new TokenizerPortuguese(), new Portuguese());
 * System.out.println(s2 + " *** " + sp.toString());
 * </code>
 * ...
 * </pre>
 *
 * @author Nuno A. C. Henriques [nunoachenriques.net]
 * @see <a href="http://comp.social.gatech.edu/papers/icwsm14.vader.hutto.pdf" target="_blank">VADER: A Parsimonious Rule-based Model for Sentiment Analysis of Social Media Text</a>
 */
public class SentimentAnalysis {

    // Available languages (e.g., "en").
    // TODO automatic detection of all languages?!
    private static final List<String> LANGUAGES = Collections.singletonList("en");

    private String text;
    private Language language;
    private Tokenizer tokenizer;
    private Properties textProperties;

    /**
     * Default constructor with all parameters {@code null}.
     */
    @SuppressWarnings("WeakerAccess")
    public SentimentAnalysis() {
        text = null;
        language = null;
        tokenizer = null;
        textProperties = null;
    }

    /**
     * Sets two required parameters: tokenizer and language. All the others
     * initialize as {@code null}.
     *
     * @param l The text {@link Language}
     *          (e.g., {@link net.nunoachenriques.vader.lexicon.English}).
     * @param t The text {@link Tokenizer} to be used
     *          (e.g., {@link net.nunoachenriques.vader.text.TokenizerEnglish}).
     */
    @SuppressWarnings("WeakerAccess")
    public SentimentAnalysis(Language l, Tokenizer t) {
        text = null;
        language = l;
        tokenizer = t;
        textProperties = null;
    }

    /**
     * Does the sentiment analysis of the given text sample and returns
     * the polarity values.
     *
     * @param s Text sample to analyse.
     * @return The list of positive, neutral, negative, and compound name-value
     * pairs.
     */
    public Map<String, Float> getSentimentAnalysis(String s) {
        text = s;
        textProperties = new Properties(s, language, tokenizer);
        return getPolarity();
    }

    /**
     * Does the sentiment analysis of the given text sample and returns
     * the polarity values.
     *
     * @param s Text sample to analyse.
     * @param l Language of the text sample to analyse.
     * @return The list of positive, neutral, negative, and compound name-value
     * pairs.
     */
    public Map<String, Float> getSentimentAnalysis(String s, String l) {
        switch (l) {
            case "en":
                language = new English();
                tokenizer = new TokenizerEnglish();
                break;
        }
        return getSentimentAnalysis(s);
    }

    /**
     * Does the sentiment analysis of the given text sample, using the specified
     * tokenizer, the specified language parameters, and returns the polarity
     * values.
     *
     * @param s Text sample to analyse.
     * @param l The text {@link Language} (e.g., {@link English}).
     * @param t The text {@link Tokenizer} to be used (e.g., {@link TokenizerEnglish}).
     * @return The list of positive, neutral, negative, and compound name-value
     * pairs.
     */
    public Map<String, Float> getSentimentAnalysis(String s, Language l, Tokenizer t) {
        tokenizer = t;
        language = l;
        return getSentimentAnalysis(s);
    }

    /**
     * Gets the languages available for the sentiment analysis process.
     *
     * @return List of available languages in ISO 639-1 or 639-3 language code
     * (e.g., en).
     */
    public List<String> getAvailableLanguages() {
        return LANGUAGES;
    }

    // TODO hardcoded values (0.95f, 0.9f) to Constant?!
    private Map<String, Float> getPolarity() {
        List<Float> sentiments = new ArrayList<>();
        List<String> wordsAndEmoticons = textProperties.getWordsAndEmoticons();
        final Map<String, Float> boosterDictionary = language.getBoosterDictionary();
        final Map<String, Float> valenceDictionary = language.getWordValenceDictionary();

        for (String item : wordsAndEmoticons) {
            float currentValence = 0.0f;
            int i = wordsAndEmoticons.indexOf(item);

            // TODO English language dependent!
            if (i < wordsAndEmoticons.size() - 1
                    && item.toLowerCase().equals("kind")
                    && wordsAndEmoticons.get(i + 1).toLowerCase().equals("of")
                    || boosterDictionary.containsKey(item.toLowerCase())) {
                sentiments.add(currentValence);
                continue;
            }

            String currentItemLower = item.toLowerCase();
            if (valenceDictionary.containsKey(currentItemLower)) {
                currentValence = valenceDictionary.get(currentItemLower);
                if (language.isUpper(item) && textProperties.isCapDifferential()) {
                    currentValence = (currentValence > 0.0) ? currentValence + Constant.ALL_CAPS_BOOSTER_SCORE : currentValence - Constant.ALL_CAPS_BOOSTER_SCORE;
                }
                int startI = 0;
                float gramBasedValence;
                while (startI < 3) {
                    int closeTokenIndex = i - (startI + 1);
                    if (closeTokenIndex < 0) {
                        closeTokenIndex = pythonIndexToJavaIndex(closeTokenIndex);
                    }
                    if ((i > startI) && !valenceDictionary.containsKey(wordsAndEmoticons.get(closeTokenIndex).toLowerCase())) {
                        gramBasedValence = valenceModifier(wordsAndEmoticons.get(closeTokenIndex), currentValence);
                        if (startI == 1 && gramBasedValence != 0.0f) {
                            gramBasedValence *= 0.95f;
                        }
                        if (startI == 2 && gramBasedValence != 0.0f) {
                            gramBasedValence *= 0.9f;
                        }
                        currentValence += gramBasedValence;
                        currentValence = checkForNever(currentValence, startI, i, closeTokenIndex);
                        if (startI == 2) {
                            currentValence = checkForIdioms(currentValence, i);
                        }
                    }
                    startI++;
                }
                if (i > 1 && !valenceDictionary.containsKey(wordsAndEmoticons.get(i - 1).toLowerCase()) && wordsAndEmoticons.get(i - 1).toLowerCase().equals("least")) {
                    if (!(wordsAndEmoticons.get(i - 2).toLowerCase().equals("at") || wordsAndEmoticons.get(i - 2).toLowerCase().equals("very"))) {
                        currentValence *= Constant.N_SCALAR;
                    }
                } else if (i > 0 && !valenceDictionary.containsKey(wordsAndEmoticons.get(i - 1).toLowerCase()) && wordsAndEmoticons.get(i - 1).equals("least")) {
                    currentValence *= Constant.N_SCALAR;
                }
            }
            sentiments.add(currentValence);
        }
        sentiments = checkConjunctionBut(wordsAndEmoticons, sentiments);
        return polarityScores(sentiments);
    }

    private float valenceModifier(String precedingWord, float currentValence) {
        float scalar = 0.0f;
        final Map<String, Float> boosterDictionary = language.getBoosterDictionary();
        String precedingWordLower = precedingWord.toLowerCase();
        if (boosterDictionary.containsKey(precedingWordLower)) {
            scalar = boosterDictionary.get(precedingWordLower);
            if (currentValence < 0.0) {
                scalar *= -1.0;
            }
            if (language.isUpper(precedingWord) && textProperties.isCapDifferential()) {
                scalar = (currentValence > 0.0) ? scalar + Constant.ALL_CAPS_BOOSTER_SCORE : scalar - Constant.ALL_CAPS_BOOSTER_SCORE;
            }
        }
        return scalar;
    }

    private int pythonIndexToJavaIndex(int pythonIndex) {
        return textProperties.getWordsAndEmoticons().size() - Math.abs(pythonIndex);
    }

    // TODO hardcoded values (1.5f, 1.25f) to Constant?!
    private float checkForNever(float currentValence, int startI, int i, int closeTokenIndex) {
        List<String> wordsAndEmoticons = textProperties.getWordsAndEmoticons();
        final List<String> negativeWords = language.getNegativeWords();
        if (startI == 0) {
            if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(i - 1))), negativeWords)) {
                currentValence *= Constant.N_SCALAR;
            }
        }
        if (startI == 1) {
            String wordAtDistanceTwoLeft = wordsAndEmoticons.get(i - 2);
            String wordAtDistanceOneLeft = wordsAndEmoticons.get(i - 1);

            // TODO English language dependent!
            if ((wordAtDistanceTwoLeft.equals("never"))
                    && (wordAtDistanceOneLeft.equals("so")
                    || (wordAtDistanceOneLeft.equals("this")))) {

                currentValence *= 1.5f;
            } else if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(closeTokenIndex))), negativeWords)) {
                currentValence *= Constant.N_SCALAR;
            }
        }
        if (startI == 2) {
            String wordAtDistanceThreeLeft = wordsAndEmoticons.get(i - 3);
            String wordAtDistanceTwoLeft = wordsAndEmoticons.get(i - 2);
            String wordAtDistanceOneLeft = wordsAndEmoticons.get(i - 1);

            // TODO English language dependent!
            if ((wordAtDistanceThreeLeft.equals("never"))
                    && (wordAtDistanceTwoLeft.equals("so") || wordAtDistanceTwoLeft.equals("this"))
                    || (wordAtDistanceOneLeft.equals("so") || wordAtDistanceOneLeft.equals("this"))) {

                currentValence *= 1.25f;
            } else if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(closeTokenIndex))), negativeWords)) {
                currentValence *= Constant.N_SCALAR;
            }
        }
        return currentValence;
    }

    private float checkForIdioms(float currentValence, int i) {
        List<String> wordsAndEmoticons = textProperties.getWordsAndEmoticons();
        final String leftBiGramFromCurrent = String.format("%s %s", wordsAndEmoticons.get(i - 1), wordsAndEmoticons.get(i));
        final String leftTriGramFromCurrent = String.format("%s %s %s", wordsAndEmoticons.get(i - 2), wordsAndEmoticons.get(i - 1), wordsAndEmoticons.get(i));
        final String leftBiGramFromOnePrevious = String.format("%s %s", wordsAndEmoticons.get(i - 2), wordsAndEmoticons.get(i - 1));
        final String leftTriGramFromOnePrevious = String.format("%s %s %s", wordsAndEmoticons.get(i - 3), wordsAndEmoticons.get(i - 2), wordsAndEmoticons.get(i - 1));
        final String leftBiGramFromTwoPrevious = String.format("%s %s", wordsAndEmoticons.get(i - 3), wordsAndEmoticons.get(i - 2));
        final Map<String, Float> boosterDictionary = language.getBoosterDictionary();
        final Map<String, Float> sentimentLadenIdioms = language.getSentimentLadenIdioms();

        List<String> leftGramSequences = new ArrayList<String>() {{
            add(leftBiGramFromCurrent);
            add(leftTriGramFromCurrent);
            add(leftBiGramFromOnePrevious);
            add(leftTriGramFromOnePrevious);
            add(leftBiGramFromTwoPrevious);
        }};

        for (String leftGramSequence : leftGramSequences) {
            if (sentimentLadenIdioms.containsKey(leftGramSequence)) {
                currentValence = sentimentLadenIdioms.get(leftGramSequence);
                break;
            }
        }
        if (wordsAndEmoticons.size() - 1 > i) {
            final String rightBiGramFromCurrent = String.format("%s %s", wordsAndEmoticons.get(i), wordsAndEmoticons.get(i + 1));
            if (sentimentLadenIdioms.containsKey(rightBiGramFromCurrent)) {
                currentValence = sentimentLadenIdioms.get(rightBiGramFromCurrent);
            }
        }
        if (wordsAndEmoticons.size() - 1 > i + 1) {
            final String rightTriGramFromCurrent = String.format("%s %s %s", wordsAndEmoticons.get(i), wordsAndEmoticons.get(i + 1), wordsAndEmoticons.get(i + 2));
            if (sentimentLadenIdioms.containsKey(rightTriGramFromCurrent)) {
                currentValence = sentimentLadenIdioms.get(rightTriGramFromCurrent);
            }
        }
        if (boosterDictionary.containsKey(leftBiGramFromTwoPrevious) || boosterDictionary.containsKey(leftBiGramFromOnePrevious)) {
            currentValence += -0.293f; // TODO review Language and English.DAMPENER_WORD_DECREMENT;
        }
        return currentValence;
    }

    private List<Float> siftSentimentScores(List<Float> currentSentimentState) {
        float positiveSentimentScore = 0.0f;
        float negativeSentimentScore = 0.0f;
        int neutralSentimentCount = 0;
        for (Float valence : currentSentimentState) {
            if (valence > 0.0f) {
                positiveSentimentScore = positiveSentimentScore + valence + 1.0f;
            } else if (valence < 0.0f) {
                negativeSentimentScore = negativeSentimentScore + valence - 1.0f;
            } else {
                neutralSentimentCount += 1;
            }
        }
        return new ArrayList<>(Arrays.asList(
                positiveSentimentScore,
                negativeSentimentScore,
                (float) neutralSentimentCount)
        );
    }

    // TODO hardcoded values (4) to Constant?!
    private Map<String, Float> polarityScores(List<Float> currentSentimentState) {
        if (!currentSentimentState.isEmpty()) {
            float totalValence = 0.0f;
            for (Float valence : currentSentimentState) {
                totalValence += valence;
            }
            float punctuationAmplifier = boostByPunctuation();
            if (totalValence > 0.0f) {
                totalValence += boostByPunctuation();
            } else if (totalValence < 0.0f) {
                totalValence -= boostByPunctuation();
            }
            float compoundPolarity = normalizeScore(totalValence, Constant.NORMALIZE_SCORE_ALPHA_DEFAULT);
            List<Float> siftedScores = siftSentimentScores(currentSentimentState);
            float positiveSentimentScore = siftedScores.get(0);
            float negativeSentimentScore = siftedScores.get(1);
            int neutralSentimentCount = Math.round(siftedScores.get(2));
            if (positiveSentimentScore > Math.abs(negativeSentimentScore)) {
                positiveSentimentScore += punctuationAmplifier;
            } else if (positiveSentimentScore < Math.abs(negativeSentimentScore)) {
                negativeSentimentScore -= punctuationAmplifier;
            }
            float normalizationFactor = positiveSentimentScore
                    + Math.abs(negativeSentimentScore)
                    + neutralSentimentCount;
            final float normalizedPositivePolarity = roundDecimal(Math.abs(positiveSentimentScore / normalizationFactor), 3);
            final float normalizedNegativePolarity = roundDecimal(Math.abs(negativeSentimentScore / normalizationFactor), 3);
            final float normalizedNeutralPolarity = roundDecimal(Math.abs(neutralSentimentCount / normalizationFactor), 3);
            final float normalizedCompoundPolarity = roundDecimal(compoundPolarity, 4);
            return new HashMap<String, Float>() {{
                put("compound", normalizedCompoundPolarity);
                put("positive", normalizedPositivePolarity);
                put("negative", normalizedNegativePolarity);
                put("neutral", normalizedNeutralPolarity);
            }};
        } else {
            return new HashMap<String, Float>() {{
                put("compound", 0.0f);
                put("positive", 0.0f);
                put("negative", 0.0f);
                put("neutral", 0.0f);
            }};
        }
    }

    private float boostByPunctuation() {
        return boostByExclamation() + boostByQuestionMark();
    }

    private float boostByExclamation() {
        int exclamationCount = Properties.countLetter(text, "!");
        return Math.min(exclamationCount, 4) * Constant.EXCLAMATION_BOOST;
    }

    private float boostByQuestionMark() {
        float questionMarkAmplifier = 0.0f;
        int questionMarkCount = Properties.countLetter(text, "?");
        if (questionMarkCount > 1) {
            questionMarkAmplifier =
                    (questionMarkCount <= 3)
                            ? questionMarkCount * Constant.QUESTION_BOOST_COUNT_3
                            : Constant.QUESTION_BOOST;
        }
        return questionMarkAmplifier;
    }

    // TODO hardcoded values (0.5f, 1.5f) to Constant?!
    private List<Float> checkConjunctionBut(List<String> inputTokens, List<Float> currentSentimentState) {

        // TODO English language dependent!
        if (inputTokens.contains("but") || inputTokens.contains("BUT")) {
            int index = inputTokens.indexOf("but");
            if (index == -1) {
                index = inputTokens.indexOf("BUT");
            }

            for (Float valence : currentSentimentState) {
                int currentValenceIndex = currentSentimentState.indexOf(valence);
                if (currentValenceIndex < index) {
                    currentSentimentState.set(currentValenceIndex, valence * 0.5f);
                } else if (currentValenceIndex > index) {
                    currentSentimentState.set(currentValenceIndex, valence * 1.5f);
                }
            }
        }
        return currentSentimentState;
    }

    private boolean hasAtLeast(List<String> tokenList) {

        // TODO English language dependent!
        if (tokenList.contains("least")) {
            int index = tokenList.indexOf("least");
            return index > 0 && tokenList.get(index - 1).equals("at");
        }
        return false;
    }

    private boolean hasContraction(List<String> tokenList) {
        for (String s : tokenList) {

            // TODO English language dependent!
            if (s.endsWith("n't")) {
                return true;
            }
        }
        return false;
    }

    private boolean hasNegativeWord(List<String> tokenList, List<String> newNegWords) {
        for (String newNegWord : newNegWords) {
            if (tokenList.contains(newNegWord)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNegative(List<String> tokenList, List<String> newNegWords) {
        return hasNegativeWord(tokenList, newNegWords)
                || hasAtLeast(tokenList)
                || hasContraction(tokenList);
    }

    private float normalizeScore(float score, float alpha) {
        return (float) (score / Math.sqrt((score * score) + alpha));
    }

    private static float roundDecimal(float currentValue, int roundTo) {
        float n = (float) Math.pow(10.0, (double) roundTo);
        float number = Math.round(currentValue * n);
        return number / n;
    }
}
