package net.nunoachenriques.vader.text;

import java.io.IOException;
import java.util.List;

/**
 * @author Animesh Pandey Created on 4/16/2016.
 * @author Nuno A. C. Henriques [nunoachenriques.net]
 */
interface TokenizerInterface {

    List<String> defaultSplit(String inputString) throws IOException;

    List<String> removePunctuation(String inputString) throws IOException;
}
