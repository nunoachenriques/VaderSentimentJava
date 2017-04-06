# VADER: sentiment analysis in Java

VADER (Valence Aware Dictionary and sEntiment Reasoner) is a lexicon
and rule-based sentiment analysis tool that is _specifically attuned
to sentiments expressed in social media_.

This is a fork of the
[Java port by Animesh Pandey](https://github.com/apanimesh061/VaderSentimentJava)
of the
[NLTK VADER sentiment analysis module](http://www.nltk.org/api/nltk.sentiment.html#module-nltk.sentiment.vader)
originally written in Python.

 - The [Original](https://github.com/cjhutto/vaderSentiment) Python
   source code by the paper's author C.J. Hutto.
 - The [NLTK](http://www.nltk.org/_modules/nltk/sentiment/vader.html)
   Python source code.

## Citation

If you use either the dataset or any of the VADER sentiment analysis
tools (VADER sentiment lexicon or Python code for rule-based sentiment
analysis engine) in your research, please cite the original paper. For
example:

**Hutto, C.J. & Gilbert, E.E. (2014). VADER: A Parsimonious Rule-based
  Model for Sentiment Analysis of Social Media Text. Eighth
  International Conference on Weblogs and Social Media (ICWSM-14). Ann
  Arbor, MI, June 2014.**

## Testing

The tests from the original Java port are validated against the ground
truth of the original Python (NLTK) implementation.

```shell
gradle test
```

## Java code example

```java
...
ArrayList<String> sentences = new ArrayList<String>() {{
    add("VADER is smart, handsome, and funny.");
    add("VADER is smart, handsome, and funny!");
    add("VADER is very smart, handsome, and funny.");
    add("VADER is VERY SMART, handsome, and FUNNY.");
    add("VADER is VERY SMART, handsome, and FUNNY!!!");
    add("VADER is VERY SMART, really handsome, and INCREDIBLY FUNNY!!!");
    add("The book was good.");
    add("The book was kind of good.");
    add("The plot was good, but the characters are uncompelling and the dialog is not great.");
    add("A really bad, horrible book.");
    add("At least it isn't a horrible book.");
    add(":) and :D");
    add("");
    add("Today sux");
    add("Today sux!");
    add("Today SUX!");
    add("Today kinda sux! But I'll get by, lol");
}};

SentimentAnalyzer sa = new SentimentAnalyzer();

for (String sentence : sentences) {
    System.out.println(sentence);
    System.out.println(sa.getSentimentAnalysis(sentence).toString());
}
...
```
