package net.nunoachenriques.vader;

import net.nunoachenriques.vader.lexicon.English;
import net.nunoachenriques.vader.text.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * The SentimentAnalysis class is the main class for VADER Sentiment Analysis.
 * Use cases:
 * <h2>I Step-by-step individual operations.</h2>
 * <pre>
 * ...
 * {@code
 * SentimentAnalysis sa = new SentimentAnalysis();
 * Map<String,Float> sp;
 * String s1 = "VADER is smart, handsome, and funny!";
 * String s2 = "VADER sometimes fails too as everyone else!";
 * ...
 * sa.setText(s1);
 * sp = sa.getPolarity();
 * System.out.println(sa.getText() + " *** " + sp.toString());
 * ...
 * System.out.println(sa.getText());
 * System.out.println(sa.getPolarity());
 * ...
 * sa.setText(s2);
 * sp = sa.getPolarity();
 * System.out.println(sa.getText() + " *** " + sp.toString());
 * ...
 * System.out.println(sa.getText());
 * System.out.println(sa.getPolarity());
 * }
 * ...
 * </pre>
 * <h2>II One-step all-in-one operation.</h2>
 * <pre>
 * ...
 * {@code
 * SentimentAnalysis sa = new SentimentAnalysis();
 * Map<String,Float> sp;
 * String s1 = "VADER is smart, handsome, and funny!";
 * String s2 = "VADER sometimes fails too as everyone else!";
 * ...
 * sp = sa.getSentimentAnalysis(s1);
 * System.out.println(sa.getText() + " *** " + sp.toString());
 * sp = sa.getSentimentAnalysis(s2);
 * System.out.println(sa.getText() + " *** " + sp.toString());
 * }
 * ...
 * </pre>
 *
 * @author Animesh Pandey Created on 4/11/2016.
 * @author Nuno A. C. Henriques [nunoachenriques.net]
 * @see
 * <a href="http://comp.social.gatech.edu/papers/icwsm14.vader.hutto.pdf">VADER:
 * A Parsimonious Rule-based Model for Sentiment Analysis of Social Media
 * Text</a>
 */
public class SentimentAnalysis {

    private static final Logger LOGGER = LogManager.getLogger(SentimentAnalysis.class);
    private static final float NORMALIZE_SCORE_ALPHA_DEFAULT = 15.0f;

    private String text;
    private Properties textProperties;
    private HashMap<String, Float> polarity;

    /**
     * Default constructor without arguments.
     */
    public SentimentAnalysis() {
        text = null;
        textProperties = null;
        polarity = null;
    }

    /**
     * Sets the text sample intended for sentiment analysis and does all the
     * text properties processing. Polarity is reset, i.e., {@code null}.
     *
     * @param s The text sample.
     * @throws IOException on a file operation failure (e.g., reading properties).
     */
    public void setText(String s) throws IOException {
        text = s;
        textProperties = new Properties(s);
        polarity = null;
    }

    /**
     * Gets the current text sample intended for sentiment analysis.
     *
     * @return The text sample.
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the polarity of the current text sample sentiment analysis. If the
     * analysis is not done yet, i.e., polarity is {@code null} and the text is
     * not {@code null}, then does the sentiment analysis before returning.
     *
     * @return The list of positive, neutral, negative, and compound name-value
     * pairs.
     */
    public HashMap<String, Float> getPolarity() {
        if (polarity == null && text != null) {
            polarity = getSentiment();
        }
        return polarity;
    }

    /**
     * Does the sentiment analysis of the given text sample, sets and returns
     * the polarity values.
     *
     * @param s Text sample to analyse.
     * @return The list of positive, neutral, negative, and compound name-value
     * pairs.
     * @throws IOException on a file operation failure (e.g., reading properties).
     */
    public final HashMap<String, Float> getSentimentAnalysis(String s) throws IOException {
        text = s;
        textProperties = new Properties(s);
        polarity = getSentiment();
        return polarity;
    }

    private float valenceModifier(String precedingWord, float currentValence) {
        float scalar = 0.0f;
        String precedingWordLower = precedingWord.toLowerCase();
        if (English.BOOSTER_DICTIONARY.containsKey(precedingWordLower)) {
            scalar = English.BOOSTER_DICTIONARY.get(precedingWordLower);
            if (currentValence < 0.0) {
                scalar *= -1.0;
            }
            if (English.isUpper(precedingWord) && textProperties.isCapDIff()) {
                scalar = (currentValence > 0.0) ? scalar + English.ALL_CAPS_BOOSTER_SCORE : scalar - English.ALL_CAPS_BOOSTER_SCORE;
            }
        }
        return scalar;
    }

    private int pythonIndexToJavaIndex(int pythonIndex) {
        return textProperties.getWordsAndEmoticons().size() - Math.abs(pythonIndex);
    }

    private float checkForNever(float currentValence, int startI, int i, int closeTokenIndex) {
        ArrayList<String> wordsAndEmoticons = textProperties.getWordsAndEmoticons();

        if (startI == 0) {
            if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(i - 1))), English.NEGATIVE_WORDS)) {
                currentValence *= English.N_SCALAR;
            }
        }

        if (startI == 1) {
            String wordAtDistanceTwoLeft = wordsAndEmoticons.get(i - 2);
            String wordAtDistanceOneLeft = wordsAndEmoticons.get(i - 1);
            if ((wordAtDistanceTwoLeft.equals("never")) && (wordAtDistanceOneLeft.equals("so") || (wordAtDistanceOneLeft.equals("this")))) {
                currentValence *= 1.5f;
            } else if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(closeTokenIndex))), English.NEGATIVE_WORDS)) {
                currentValence *= English.N_SCALAR;
            }
        }

        if (startI == 2) {
            String wordAtDistanceThreeLeft = wordsAndEmoticons.get(i - 3);
            String wordAtDistanceTwoLeft = wordsAndEmoticons.get(i - 2);
            String wordAtDistanceOneLeft = wordsAndEmoticons.get(i - 1);
            if ((wordAtDistanceThreeLeft.equals("never"))
                    && (wordAtDistanceTwoLeft.equals("so") || wordAtDistanceTwoLeft.equals("this"))
                    || (wordAtDistanceOneLeft.equals("so") || wordAtDistanceOneLeft.equals("this"))) {
                currentValence *= 1.25f;
            } else if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(closeTokenIndex))), English.NEGATIVE_WORDS)) {
                currentValence *= English.N_SCALAR;
            }
        }

        return currentValence;
    }

    private float checkForIdioms(float currentValence, int i) {
        ArrayList<String> wordsAndEmoticons = textProperties.getWordsAndEmoticons();
        final String leftBiGramFromCurrent = String.format("%s %s", wordsAndEmoticons.get(i - 1), wordsAndEmoticons.get(i));
        final String leftTriGramFromCurrent = String.format("%s %s %s", wordsAndEmoticons.get(i - 2), wordsAndEmoticons.get(i - 1), wordsAndEmoticons.get(i));
        final String leftBiGramFromOnePrevious = String.format("%s %s", wordsAndEmoticons.get(i - 2), wordsAndEmoticons.get(i - 1));
        final String leftTriGramFromOnePrevious = String.format("%s %s %s", wordsAndEmoticons.get(i - 3), wordsAndEmoticons.get(i - 2), wordsAndEmoticons.get(i - 1));
        final String leftBiGramFromTwoPrevious = String.format("%s %s", wordsAndEmoticons.get(i - 3), wordsAndEmoticons.get(i - 2));

        ArrayList<String> leftGramSequences = new ArrayList<String>() {
            {
                add(leftBiGramFromCurrent);
                add(leftTriGramFromCurrent);
                add(leftBiGramFromOnePrevious);
                add(leftTriGramFromOnePrevious);
                add(leftBiGramFromTwoPrevious);
            }
        };

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Grams: " + leftGramSequences);
        }

        for (String leftGramSequence : leftGramSequences) {
            if (English.SENTIMENT_LADEN_IDIOMS.containsKey(leftGramSequence)) {
                currentValence = English.SENTIMENT_LADEN_IDIOMS.get(leftGramSequence);
                break;
            }
        }

        if (wordsAndEmoticons.size() - 1 > i) {
            final String rightBiGramFromCurrent = String.format("%s %s", wordsAndEmoticons.get(i), wordsAndEmoticons.get(i + 1));
            if (English.SENTIMENT_LADEN_IDIOMS.containsKey(rightBiGramFromCurrent)) {
                currentValence = English.SENTIMENT_LADEN_IDIOMS.get(rightBiGramFromCurrent);
            }
        }
        if (wordsAndEmoticons.size() - 1 > i + 1) {
            final String rightTriGramFromCurrent = String.format("%s %s %s", wordsAndEmoticons.get(i), wordsAndEmoticons.get(i + 1), wordsAndEmoticons.get(i + 2));
            if (English.SENTIMENT_LADEN_IDIOMS.containsKey(rightTriGramFromCurrent)) {
                currentValence = English.SENTIMENT_LADEN_IDIOMS.get(rightTriGramFromCurrent);
            }
        }

        if (English.BOOSTER_DICTIONARY.containsKey(leftBiGramFromTwoPrevious) || English.BOOSTER_DICTIONARY.containsKey(leftBiGramFromOnePrevious)) {
            currentValence += English.DAMPENER_WORD_DECREMENT;
        }

        return currentValence;
    }

    private HashMap<String, Float> getSentiment() {
        ArrayList<Float> sentiments = new ArrayList<>();
        ArrayList<String> wordsAndEmoticons = textProperties.getWordsAndEmoticons();

        for (String item : wordsAndEmoticons) {
            float currentValence = 0.0f;
            int i = wordsAndEmoticons.indexOf(item);

            LOGGER.debug("Current token, \"" + item + "\" with index, i = " + i);
            LOGGER.debug("Sentiment State before \"kind of\" processing: " + sentiments);

            if (i < wordsAndEmoticons.size() - 1
                    && item.toLowerCase().equals("kind")
                    && wordsAndEmoticons.get(i + 1).toLowerCase().equals("of")
                    || English.BOOSTER_DICTIONARY.containsKey(item.toLowerCase())) {
                sentiments.add(currentValence);
                continue;
            }

            LOGGER.debug("Sentiment State after \"kind of\" processing: " + sentiments);
            LOGGER.debug(String.format("Current Valence is %f for \"%s\"", currentValence, item));

            String currentItemLower = item.toLowerCase();
            if (English.WORD_VALENCE_DICTIONARY.containsKey(currentItemLower)) {
                currentValence = English.WORD_VALENCE_DICTIONARY.get(currentItemLower);

                LOGGER.debug(English.isUpper(item));
                LOGGER.debug(textProperties.isCapDIff());
                LOGGER.debug((English.isUpper(item) && textProperties.isCapDIff()) + "\t" + item + "\t" + textProperties.isCapDIff());

                if (English.isUpper(item) && textProperties.isCapDIff()) {
                    currentValence = (currentValence > 0.0) ? currentValence + English.ALL_CAPS_BOOSTER_SCORE : currentValence - English.ALL_CAPS_BOOSTER_SCORE;
                }

                LOGGER.debug(String.format("Current Valence post all CAPS checks: %f", currentValence));

                int startI = 0;
                float gramBasedValence;
                while (startI < 3) {
                    int closeTokenIndex = i - (startI + 1);
                    if (closeTokenIndex < 0) {
                        closeTokenIndex = pythonIndexToJavaIndex(closeTokenIndex);
                    }

                    if ((i > startI) && !English.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticons.get(closeTokenIndex).toLowerCase())) {

                        LOGGER.debug(String.format("Current Valence pre gramBasedValence: %f", currentValence));
                        gramBasedValence = valenceModifier(wordsAndEmoticons.get(closeTokenIndex), currentValence);
                        LOGGER.debug(String.format("Current Valence post gramBasedValence: %f", currentValence));

                        if (startI == 1 && gramBasedValence != 0.0f) {
                            gramBasedValence *= 0.95f;
                        }
                        if (startI == 2 && gramBasedValence != 0.0f) {
                            gramBasedValence *= 0.9f;
                        }
                        currentValence += gramBasedValence;
                        LOGGER.debug(String.format("Current Valence post gramBasedValence and distance boosting: %f", currentValence));

                        currentValence = checkForNever(currentValence, startI, i, closeTokenIndex);
                        LOGGER.debug(String.format("Current Valence post \"never\" check: %f", currentValence));

                        if (startI == 2) {
                            currentValence = checkForIdioms(currentValence, i);
                            LOGGER.debug(String.format("Current Valence post Idiom check: %f", currentValence));
                        }
                    }
                    startI++;
                }

                if (i > 1 && !English.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticons.get(i - 1).toLowerCase()) && wordsAndEmoticons.get(i - 1).toLowerCase().equals("least")) {
                    if (!(wordsAndEmoticons.get(i - 2).toLowerCase().equals("at") || wordsAndEmoticons.get(i - 2).toLowerCase().equals("very"))) {
                        currentValence *= English.N_SCALAR;
                    }
                } else if (i > 0 && !English.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticons.get(i - 1).toLowerCase()) && wordsAndEmoticons.get(i - 1).equals("least")) {
                    currentValence *= English.N_SCALAR;
                }
            }

            sentiments.add(currentValence);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sentiment state after first pass through tokens: " + sentiments);
        }

        sentiments = checkConjunctionBut(wordsAndEmoticons, sentiments);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sentiment state after checking conjunctions: " + sentiments);
        }

        return polarityScores(sentiments);
    }

    private ArrayList<Float> siftSentimentScores(ArrayList<Float> currentSentimentState) {
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

    private HashMap<String, Float> polarityScores(ArrayList<Float> currentSentimentState) {
        if (!currentSentimentState.isEmpty()) {
            float totalValence = 0.0f;
            for (Float valence : currentSentimentState) {
                totalValence += valence;
            }

            LOGGER.debug("Total valence: " + totalValence);

            float punctuationAmplifier = boostByPunctuation();
            if (totalValence > 0.0f) {
                totalValence += boostByPunctuation();
            } else if (totalValence < 0.0f) {
                totalValence -= boostByPunctuation();
            }

            LOGGER.debug("Total valence after boost/damp by punctuation: " + totalValence);

            float compoundPolarity = normalizeScore(totalValence);

            LOGGER.debug("Final token-wise sentiment state: " + currentSentimentState);

            ArrayList<Float> siftedScores = siftSentimentScores(currentSentimentState);
            float positiveSentimentScore = siftedScores.get(0);
            float negativeSentimentScore = siftedScores.get(1);
            int neutralSentimentCount = Math.round(siftedScores.get(2));

            LOGGER.debug(String.format("Post Sift Sentiment Scores: %s %s %s", positiveSentimentScore, negativeSentimentScore, neutralSentimentCount));

            if (positiveSentimentScore > Math.abs(negativeSentimentScore)) {
                positiveSentimentScore += punctuationAmplifier;
            } else if (positiveSentimentScore < Math.abs(negativeSentimentScore)) {
                negativeSentimentScore -= punctuationAmplifier;
            }

            float normalizationFactor = positiveSentimentScore + Math.abs(negativeSentimentScore)
                    + neutralSentimentCount;

            LOGGER.debug("Normalization Factor: " + normalizationFactor);

            LOGGER.debug(String.format("Pre-Normalized Scores: %s %s %s %s",
                    Math.abs(positiveSentimentScore),
                    Math.abs(negativeSentimentScore),
                    Math.abs(neutralSentimentCount),
                    compoundPolarity
            ));

            LOGGER.debug(String.format("Pre-Round Scores: %s %s %s %s",
                    Math.abs(positiveSentimentScore / normalizationFactor),
                    Math.abs(negativeSentimentScore / normalizationFactor),
                    Math.abs(neutralSentimentCount / normalizationFactor),
                    compoundPolarity
            ));

            final float normalizedPositivePolarity = roundDecimal(Math.abs(positiveSentimentScore / normalizationFactor), 3);
            final float normalizedNegativePolarity = roundDecimal(Math.abs(negativeSentimentScore / normalizationFactor), 3);
            final float normalizedNeutralPolarity = roundDecimal(Math.abs(neutralSentimentCount / normalizationFactor), 3);
            final float normalizedCompoundPolarity = roundDecimal(compoundPolarity, 4);

            return new HashMap<String, Float>() {
                {
                    put("compound", normalizedCompoundPolarity);
                    put("positive", normalizedPositivePolarity);
                    put("negative", normalizedNegativePolarity);
                    put("neutral", normalizedNeutralPolarity);
                }
            };

        } else {
            return new HashMap<String, Float>() {
                {
                    put("compound", 0.0f);
                    put("positive", 0.0f);
                    put("negative", 0.0f);
                    put("neutral", 0.0f);
                }
            };
        }
    }

    private float boostByPunctuation() {
        return boostByExclamation() + boostByQuestionMark();
    }

    private float boostByExclamation() {
        int exclamationCount = StringUtils.countMatches(text, "!");
        return Math.min(exclamationCount, 4) * English.EXCLAMATION_BOOST;
    }

    private float boostByQuestionMark() {
        int questionMarkCount = StringUtils.countMatches(text, "?");
        float questionMarkAmplifier = 0.0f;
        if (questionMarkCount > 1) {
            questionMarkAmplifier = (questionMarkCount <= 3) ? questionMarkCount * English.QUESTION_BOOST_COUNT_3 : English.QUESTION_BOOST;
        }
        return questionMarkAmplifier;
    }

    private ArrayList<Float> checkConjunctionBut(ArrayList<String> inputTokens, ArrayList<Float> currentSentimentState) {
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

    private boolean hasAtLeast(ArrayList<String> tokenList) {
        if (tokenList.contains("least")) {
            int index = tokenList.indexOf("least");
            if (index > 0 && tokenList.get(index - 1).equals("at")) {
                return true;
            }
        }
        return false;
    }

    private boolean hasContraction(ArrayList<String> tokenList) {
        for (String s : tokenList) {
            if (s.endsWith("n't")) {
                return true;
            }
        }
        return false;
    }

    private boolean hasNegativeWord(ArrayList<String> tokenList, ArrayList<String> newNegWords) {
        for (String newNegWord : newNegWords) {
            if (tokenList.contains(newNegWord)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNegative(ArrayList<String> tokenList, ArrayList<String> newNegWords, boolean checkContractions) {
        //newNegWords.addAll(English.NEGATIVE_WORDS);
        boolean result = hasNegativeWord(tokenList, newNegWords) || hasAtLeast(tokenList);
        if (checkContractions) {
            return result;
        }
        return result || hasContraction(tokenList);
    }

    private boolean isNegative(ArrayList<String> tokenList, ArrayList<String> newNegWords) {
        return isNegative(tokenList, newNegWords, false);
        //return hasNegativeWord(tokenList, English.NEGATIVE_WORDS) || hasAtLeast(tokenList) || hasContraction(tokenList);
    }

    private float normalizeScore(float score, float alpha) {
        double normalizedScore = score / Math.sqrt((score * score) + alpha);
        return (float) normalizedScore;
    }

    private float normalizeScore(float score) {
        return normalizeScore(score, NORMALIZE_SCORE_ALPHA_DEFAULT);
    }

    private static float roundDecimal(float currentValue, int roundTo) {
        float n = (float) Math.pow(10.0, (double) roundTo);
        float number = Math.round(currentValue * n);
        return number / n;
    }
}
