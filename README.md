# VADER Sentiment Analysis in Java

VADER (Valence Aware Dictionary and sEntiment Reasoner) is a lexicon
and rule-based sentiment analysis tool that is _specifically attuned
to sentiments expressed in social media_.

This is a fork with **API and package names breaking changes** of the
[Java port by Animesh Pandey](https://github.com/apanimesh061/VaderSentimentJava)
of the
[NLTK VADER sentiment analysis module](http://www.nltk.org/api/nltk.sentiment.html#module-nltk.sentiment.vader)
written in Python and optimized from the original.

 - The [NLTK](http://www.nltk.org/_modules/nltk/sentiment/vader.html)
   Python source code.
 - The [Original](https://github.com/cjhutto/vaderSentiment) Python
   source code by the paper's author C.J. Hutto.

## Repository

https://github.com/nunoachenriques/vader-sentiment-analysis

## Citation

If you use either the data set or any of the VADER sentiment analysis
tools (VADER sentiment lexicon or Python code for rule-based sentiment
analysis engine) in your research, please cite the original paper:

Hutto, C. J., & Gilbert, E. (2014). VADER: A Parsimonious Rule-based Model for
Sentiment Analysis of Social Media Text. In Proceedings of the Eighth
International AAAI Conference on Weblogs and Social Media (pp. 216–225).
Ann Arbor, Michigan, USA.

Retrieved from http://comp.social.gatech.edu/papers/icwsm14.vader.hutto.pdf

## Install

### From release

https://github.com/nunoachenriques/vader-sentiment-analysis/releases

### From source code

1. Get the code from the repository (clone or download).

2. Change to the package root directory and `./gradlew installDist`.
   Notice: remember to change `gradle.properties` accordingly to your
   JDK home for 1.7 version compatibility.

3. The JAR packages will be in `build/install/vader-sentiment-analysis`
   directory.

## Testing

The tests from the original Java port are validated against the ground truth of
the original Python (NLTK) implementation. The algorithm running is still the
original implementation from Hutto & Gilbert in Python and ported to Java by
Animesh Pandey.

```shell
./gradlew test
```

## Use case example

As a Java library it will easily integrates with a bit of coding.

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

SentimentAnalysis sa = new SentimentAnalysis();

for (String sentence : sentences) {
    System.out.println(sentence);
    System.out.println(sa.getSentimentAnalysis(sentence).toString());
}
...
```

## Documentation

### From release

https://github.com/nunoachenriques/vader-sentiment-analysis/releases

### From source code

1. Get the code from the repository (clone or download).

2. Change to the package root directory and `./gradlew javadoc`.
   Notice: remember to change `gradle.properties` accordingly to your
   JDK home for 1.7 version compatibility.

3. The docs will be in `build/docs/javadoc` directory.

## License

Copyright 2017 Nuno A. C. Henriques [http://nunoachenriques.net/]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[1]: http://www.apache.org/licenses/LICENSE-2.0.html
