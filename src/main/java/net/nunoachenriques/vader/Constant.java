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

/**
 * The VADER's constant values (e.g., NORMALIZE_SCORE_ALPHA_DEFAULT)
 * for configurations and other uses among the algorithm.
 *
 * @author Nuno A. C. Henriques [nunoachenriques.net]
 */
class Constant {

    // Private constructor to avoid instantiation.
    private Constant() {
        // Void!
    }

    // TODO check SentimentAnalysis for missing constants!

    static final float NORMALIZE_SCORE_ALPHA_DEFAULT = 15.0f;
    static final float ALL_CAPS_BOOSTER_SCORE = 0.733f;
    static final float N_SCALAR = -0.74f;
    static final float EXCLAMATION_BOOST = 0.292f;
    static final float QUESTION_BOOST_COUNT_3 = 0.18f;
    static final float QUESTION_BOOST = 0.96f;
}
