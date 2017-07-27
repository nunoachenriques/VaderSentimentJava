package net.nunoachenriques.vader.text;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * @author Animesh Pandey Created on 4/9/2016.
 * @author Nuno A. C. Henriques [nunoachenriques.net]
 */
class TokenizerLucene
        implements TokenizerInterface {

    /**
     * Tokenizes the input string removing white space. Text pre-processing with
     * Lucene white space tokenizer. A Lucene length filter is also applied to
     * remove the tokens of length of 1.
     *
     * @param inputString The input string to be pre-processed with Lucene white
     * space tokenizer.
     * @return {@code ArrayList<String>} of tokens of inputString.
     * @throws IOException on a file operation failure (e.g., reading properties).
     */
    @Override
    public ArrayList<String> defaultSplit(String inputString) throws IOException {
        StringReader reader = new StringReader(inputString);
        Tokenizer whiteSpaceTokenizer = new WhitespaceTokenizer();
        whiteSpaceTokenizer.setReader(reader);
        ArrayList<String> tokenizedString;
        try (TokenStream tokenStream = new LengthFilter(whiteSpaceTokenizer, 2, Integer.MAX_VALUE)) {
            final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            tokenizedString = new ArrayList<>();
            while (tokenStream.incrementToken()) {
                tokenizedString.add(charTermAttribute.toString());
            }
            tokenStream.end();
        }
        return tokenizedString;
    }

    /**
     * A tokenizer to remove punctuation of the text. Text pre-processing with
     * Lucene standard tokenizer to remove punctuation. A Lucene length filter
     * is also applied to remove the tokens of length of 1.
     *
     * @param inputString The input string to be pre-processed with Lucene
     * standard tokenizer to remove punctuation.
     * @return {@code ArrayList<String>} of tokens of inputString.
     * @throws IOException on a file operation failure (e.g., reading properties).
     */
    @Override
    public ArrayList<String> removePunctuation(String inputString) throws IOException {
        StringReader reader = new StringReader(inputString);
        StandardTokenizer removePunctuationTokenizer = new StandardTokenizer();
        removePunctuationTokenizer.setReader(reader);
        ArrayList<String> tokenizedString;
        try (TokenStream tokenStream = new LengthFilter(removePunctuationTokenizer, 2, Integer.MAX_VALUE)) {
            final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            tokenizedString = new ArrayList<>();
            while (tokenStream.incrementToken()) {
                tokenizedString.add(charTermAttribute.toString());
            }
            tokenStream.end();
        }
        return tokenizedString;
    }

}
