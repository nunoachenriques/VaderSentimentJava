package com.vader;

import com.vader.analyzer.TextProperties;
import com.vader.util.Utils;
import java.io.IOException;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The SentimentAnalyzer class is the main class for VADER Sentiment Analysis.
 *
 * @author Animesh Pandey
 *         Created on 4/11/2016.
 * @see <a href="http://comp.social.gatech.edu/papers/icwsm14.vader.hutto.pdf">VADER:
 * A Parsimonious Rule-based Model for Sentiment Analysis of Social Media Text</a>
 */
public class SentimentAnalyzer {
    private static Logger logger = Logger.getLogger(SentimentAnalyzer.class);

    private String text;
    private TextProperties textProperties;
    private HashMap<String, Float> polarity;

    /**
     * Default constructor without arguments. Use case:
     * <pre>
     * ...
     * <code>
     * String s = "VADER is smart, handsome, and funny!";
     * System.out.println(s);
     * SentimentAnalyzer sa = new SentimentAnalyzer();
     * sa.setText(s);
     * System.out.println(sa.getSentimentPolarity().toString());
     * </code>
     * ...
     * </pre>
     */
    public SentimentAnalyzer() {
        text = null;
        textProperties = null;
        polarity = null;
    }

    /**
     * One-argument constructor with text set. Use case:
     * <pre>
     * ...
     * <code>
     * String s = "VADER is smart, handsome, and funny!";
     * System.out.println(s);
     * SentimentAnalyzer sa = new SentimentAnalyzer(s);
     * System.out.println(sa.getSentimentPolarity().toString());
     * </code>
     * ...
     * </pre>
     * @param s The text sample intended for sentiment analysis.
     * @throws IOException
     */
    public SentimentAnalyzer(String s) throws IOException {
        setText(s);
        polarity = null;
    }

    /**
     * Sets the text sample intended for sentiment analysis and does
     * all the text properties preprocessing.
     * @param s The text sample.
     * @throws IOException
     */
    public void setText(String s) throws IOException {
        text = s;
        textProperties = new TextProperties(s);
    }

    /**
     * Gets the current text sample intended for sentiment analysis.
     * @return The text sample.
     */
    public String getText() {
        return text;
    }

    /**
     * Does the sentiment analysis of the current text sample, sets
     * and returns the polarity values.
     * @deprecated As of release 1.1, replaced by {@link #getSentimentPolarity()}
     */
    @Deprecated public void analyse() {
        polarity = getSentiment();
    }

    /**
     * Does the sentiment analysis of the current text sample, sets
     * and returns the polarity values.
     * @return The list of positive, neutral, negative, and compound name-value pairs.
     */
    public HashMap<String, Float> getSentimentPolarity() {
        polarity = getSentiment();
        return polarity;
    }

    /**
     * Gets the polarity of the current text sample sentiment analysis.
     * @return The list of positive, neutral, negative, and compound name-value pairs.
     * @see #getSentimentPolarity()
     */
    public HashMap<String, Float> getPolarity() {
        return polarity;
    }

    private float valenceModifier(String precedingWord, float currentValence) {
        float scalar = 0.0f;
        String precedingWordLower = precedingWord.toLowerCase();
        if (Utils.BOOSTER_DICTIONARY.containsKey(precedingWordLower)) {
            scalar = Utils.BOOSTER_DICTIONARY.get(precedingWordLower);
            if (currentValence < 0.0)
                scalar *= -1.0;
            if (Utils.isUpper(precedingWord) && textProperties.isCapDIff())
                scalar = (currentValence > 0.0) ? scalar + Utils.ALL_CAPS_BOOSTER_SCORE : scalar - Utils.ALL_CAPS_BOOSTER_SCORE;
        }
        return scalar;
    }

    private int pythonIndexToJavaIndex(int pythonIndex) {
        return textProperties.getWordsAndEmoticons().size() - Math.abs(pythonIndex);
    }

    private float checkForNever(float currentValence, int startI, int i, int closeTokenIndex) {
        ArrayList<String> wordsAndEmoticons = textProperties.getWordsAndEmoticons();

        if (startI == 0) {
            if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(i - 1))))) {
                currentValence *= Utils.N_SCALAR;
            }
        }

        if (startI == 1) {
            String wordAtDistanceTwoLeft = wordsAndEmoticons.get(i - 2);
            String wordAtDistanceOneLeft = wordsAndEmoticons.get(i - 1);
            if ((wordAtDistanceTwoLeft.equals("never")) && (wordAtDistanceOneLeft.equals("so") || (wordAtDistanceOneLeft.equals("this")))) {
                currentValence *= 1.5f;
            } else if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(closeTokenIndex))))) {
                currentValence *= Utils.N_SCALAR;
            }
        }

        if (startI == 2) {
            String wordAtDistanceThreeLeft = wordsAndEmoticons.get(i - 3);
            String wordAtDistanceTwoLeft = wordsAndEmoticons.get(i - 2);
            String wordAtDistanceOneLeft = wordsAndEmoticons.get(i - 1);
            if ((wordAtDistanceThreeLeft.equals("never")) &&
                    (wordAtDistanceTwoLeft.equals("so") || wordAtDistanceTwoLeft.equals("this")) ||
                    (wordAtDistanceOneLeft.equals("so") || wordAtDistanceOneLeft.equals("this"))) {
                currentValence *= 1.25f;
            } else if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(closeTokenIndex))))) {
                currentValence *= Utils.N_SCALAR;
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

        ArrayList<String> leftGramSequences = new ArrayList<String>() {{
            add(leftBiGramFromCurrent);
            add(leftTriGramFromCurrent);
            add(leftBiGramFromOnePrevious);
            add(leftTriGramFromOnePrevious);
            add(leftBiGramFromTwoPrevious);
        }};

        if (logger.isDebugEnabled())
            logger.debug("Grams: " + leftGramSequences);

        for (String leftGramSequence : leftGramSequences) {
            if (Utils.SENTIMENT_LADEN_IDIOMS.containsKey(leftGramSequence)) {
                currentValence = Utils.SENTIMENT_LADEN_IDIOMS.get(leftGramSequence);
                break;
            }
        }

        if (wordsAndEmoticons.size() - 1 > i) {
            final String rightBiGramFromCurrent = String.format("%s %s", wordsAndEmoticons.get(i), wordsAndEmoticons.get(i + 1));
            if (Utils.SENTIMENT_LADEN_IDIOMS.containsKey(rightBiGramFromCurrent))
                currentValence = Utils.SENTIMENT_LADEN_IDIOMS.get(rightBiGramFromCurrent);
        }
        if (wordsAndEmoticons.size() - 1 > i + 1) {
            final String rightTriGramFromCurrent = String.format("%s %s %s", wordsAndEmoticons.get(i), wordsAndEmoticons.get(i + 1), wordsAndEmoticons.get(i + 2));
            if (Utils.SENTIMENT_LADEN_IDIOMS.containsKey(rightTriGramFromCurrent))
                currentValence = Utils.SENTIMENT_LADEN_IDIOMS.get(rightTriGramFromCurrent);
        }

        if (Utils.BOOSTER_DICTIONARY.containsKey(leftBiGramFromTwoPrevious) || Utils.BOOSTER_DICTIONARY.containsKey(leftBiGramFromOnePrevious))
            currentValence += Utils.DAMPENER_WORD_DECREMENT;

        return currentValence;
    }

    private HashMap<String, Float> getSentiment() {
        ArrayList<Float> sentiments = new ArrayList<>();
        ArrayList<String> wordsAndEmoticons = textProperties.getWordsAndEmoticons();

        for (String item : wordsAndEmoticons) {
            float currentValence = 0.0f;
            int i = wordsAndEmoticons.indexOf(item);

            logger.debug("Current token, \"" + item + "\" with index, i = " + i);
            logger.debug("Sentiment State before \"kind of\" processing: " + sentiments);

            if (i < wordsAndEmoticons.size() - 1 &&
                    item.toLowerCase().equals("kind") &&
                    wordsAndEmoticons.get(i + 1).toLowerCase().equals("of") ||
                    Utils.BOOSTER_DICTIONARY.containsKey(item.toLowerCase())) {
                sentiments.add(currentValence);
                continue;
            }

            logger.debug("Sentiment State after \"kind of\" processing: " + sentiments);
            logger.debug(String.format("Current Valence is %f for \"%s\"", currentValence, item));

            String currentItemLower = item.toLowerCase();
            if (Utils.WORD_VALENCE_DICTIONARY.containsKey(currentItemLower)) {
                currentValence = Utils.WORD_VALENCE_DICTIONARY.get(currentItemLower);

                logger.debug(Utils.isUpper(item));
                logger.debug(textProperties.isCapDIff());
                logger.debug((Utils.isUpper(item) && textProperties.isCapDIff()) + "\t" + item + "\t" + textProperties.isCapDIff());

                if (Utils.isUpper(item) && textProperties.isCapDIff()) {
                    currentValence = (currentValence > 0.0) ? currentValence + Utils.ALL_CAPS_BOOSTER_SCORE : currentValence - Utils.ALL_CAPS_BOOSTER_SCORE;
                }

                logger.debug(String.format("Current Valence post all CAPS checks: %f", currentValence));

                int startI = 0;
                float gramBasedValence = 0.0f;
                while (startI < 3) {
                    int closeTokenIndex = i - (startI + 1);
                    if (closeTokenIndex < 0)
                        closeTokenIndex = pythonIndexToJavaIndex(closeTokenIndex);

                    if ((i > startI) && !Utils.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticons.get(closeTokenIndex).toLowerCase())) {

                        logger.debug(String.format("Current Valence pre gramBasedValence: %f", currentValence));
                        gramBasedValence = valenceModifier(wordsAndEmoticons.get(closeTokenIndex), currentValence);
                        logger.debug(String.format("Current Valence post gramBasedValence: %f", currentValence));

                        if (startI == 1 && gramBasedValence != 0.0f)
                            gramBasedValence *= 0.95f;
                        if (startI == 2 && gramBasedValence != 0.0f)
                            gramBasedValence *= 0.9f;
                        currentValence += gramBasedValence;
                        logger.debug(String.format("Current Valence post gramBasedValence and distance boosting: %f", currentValence));

                        currentValence = checkForNever(currentValence, startI, i, closeTokenIndex);
                        logger.debug(String.format("Current Valence post \"never\" check: %f", currentValence));

                        if (startI == 2) {
                            currentValence = checkForIdioms(currentValence, i);
                            logger.debug(String.format("Current Valence post Idiom check: %f", currentValence));
                        }
                    }
                    startI++;
                }

                if (i > 1 && !Utils.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticons.get(i - 1).toLowerCase()) && wordsAndEmoticons.get(i - 1).toLowerCase().equals("least")) {
                    if (!(wordsAndEmoticons.get(i - 2).toLowerCase().equals("at") || wordsAndEmoticons.get(i - 2).toLowerCase().equals("very")))
                        currentValence *= Utils.N_SCALAR;
                } else if (i > 0 && !Utils.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticons.get(i - 1).toLowerCase()) && wordsAndEmoticons.get(i - 1).equals("least")) {
                    currentValence *= Utils.N_SCALAR;
                }
            }

            sentiments.add(currentValence);
        }

        if (logger.isDebugEnabled())
            logger.debug("Sentiment state after first pass through tokens: " + sentiments);

        sentiments = checkConjunctionBut(wordsAndEmoticons, sentiments);

        if (logger.isDebugEnabled())
            logger.debug("Sentiment state after checking conjunctions: " + sentiments);

        return polarityScores(sentiments);
    }

    private ArrayList<Float> siftSentimentScores(ArrayList<Float> currentSentimentState) {
        float positiveSentimentScore = 0.0f;
        float negativeSentimentScore = 0.0f;
        int neutralSentimentCount = 0;
        for (Float valence : currentSentimentState) {
            if (valence > 0.0f)
                positiveSentimentScore = positiveSentimentScore + valence + 1.0f;
            else if (valence < 0.0f)
                negativeSentimentScore = negativeSentimentScore + valence - 1.0f;
            else
                neutralSentimentCount += 1;
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
            for (Float valence : currentSentimentState)
                totalValence += valence;

            logger.debug("Total valence: " + totalValence);

            float punctuationAmplifier = boostByPunctuation();
            if (totalValence > 0.0f)
                totalValence += boostByPunctuation();
            else if (totalValence < 0.0f)
                totalValence -= boostByPunctuation();

            logger.debug("Total valence after boost/damp by punctuation: " + totalValence);

            float compoundPolarity = normalizeScore(totalValence);

            logger.debug("Final token-wise sentiment state: " + currentSentimentState);

            ArrayList<Float> siftedScores = siftSentimentScores(currentSentimentState);
            float positiveSentimentScore = siftedScores.get(0);
            float negativeSentimentScore = siftedScores.get(1);
            int neutralSentimentCount = Math.round(siftedScores.get(2));

            logger.debug(String.format("Post Sift Sentiment Scores: %s %s %s", positiveSentimentScore, negativeSentimentScore, neutralSentimentCount));

            if (positiveSentimentScore > Math.abs(negativeSentimentScore))
                positiveSentimentScore += punctuationAmplifier;
            else if (positiveSentimentScore < Math.abs(negativeSentimentScore))
                negativeSentimentScore -= punctuationAmplifier;

            float normalizationFactor = positiveSentimentScore + Math.abs(negativeSentimentScore)
                    + neutralSentimentCount;

            logger.debug("Normalization Factor: " + normalizationFactor);

            logger.debug(String.format("Pre-Normalized Scores: %s %s %s %s",
                    Math.abs(positiveSentimentScore),
                    Math.abs(negativeSentimentScore),
                    Math.abs(neutralSentimentCount),
                    compoundPolarity
            ));

            logger.debug(String.format("Pre-Round Scores: %s %s %s %s",
                    Math.abs(positiveSentimentScore / normalizationFactor),
                    Math.abs(negativeSentimentScore / normalizationFactor),
                    Math.abs(neutralSentimentCount / normalizationFactor),
                    compoundPolarity
            ));

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
        int exclamationCount = StringUtils.countMatches(text, "!");
        return Math.min(exclamationCount, 4) * Utils.EXCLAMATION_BOOST;
    }

    private float boostByQuestionMark() {
        int questionMarkCount = StringUtils.countMatches(text, "?");
        float questionMarkAmplifier = 0.0f;
        if (questionMarkCount > 1)
            questionMarkAmplifier = (questionMarkCount <= 3) ? questionMarkCount * Utils.QUESTION_BOOST_COUNT_3 : Utils.QUESTION_BOOST;
        return questionMarkAmplifier;
    }

    private ArrayList<Float> checkConjunctionBut(ArrayList<String> inputTokens, ArrayList<Float> currentSentimentState) {
        if (inputTokens.contains("but") || inputTokens.contains("BUT")) {
            int index = inputTokens.indexOf("but");
            if (index == -1)
                index = inputTokens.indexOf("BUT");
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
            if (index > 0 && tokenList.get(index - 1).equals("at"))
                return true;
        }
        return false;
    }

    private boolean hasContraction(ArrayList<String> tokenList) {
        for (String s : tokenList) {
            if (s.endsWith("n't"))
                return true;
        }
        return false;
    }

    private boolean hasNegativeWord(ArrayList<String> tokenList, ArrayList<String> newNegWords) {
        for (String newNegWord : newNegWords) {
            if (tokenList.contains(newNegWord))
                return true;
        }
        return false;
    }

    private boolean isNegative(ArrayList<String> tokenList, ArrayList<String> newNegWords, boolean checkContractions) {
        newNegWords.addAll(Utils.NEGATIVE_WORDS);
        boolean result = hasNegativeWord(tokenList, newNegWords) || hasAtLeast(tokenList);
        if (checkContractions)
            return result;
        return result || hasContraction(tokenList);
    }

    private boolean isNegative(ArrayList<String> tokenList, ArrayList<String> newNegWords) {
        newNegWords.addAll(Utils.NEGATIVE_WORDS);
        return hasNegativeWord(tokenList, newNegWords) || hasAtLeast(tokenList) || hasContraction(tokenList);
    }

    private boolean isNegative(ArrayList<String> tokenList) {
        return hasNegativeWord(tokenList, Utils.NEGATIVE_WORDS) || hasAtLeast(tokenList) || hasContraction(tokenList);
    }

    private float normalizeScore(float score, float alpha) {
        double normalizedScore = score / Math.sqrt((score * score) + alpha);
        return (float) normalizedScore;
    }

    private float normalizeScore(float score) {
        double normalizedScore = score / Math.sqrt((score * score) + 15.0f);
        return (float) normalizedScore;
    }

    private static float roundDecimal(float currentValue, int roundTo) {
        float n = (float) Math.pow(10.0, (double) roundTo);
        float number = Math.round(currentValue * n);
        return number / n;
    }
}
