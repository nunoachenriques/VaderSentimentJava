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

/**
 * markdowntohtml.js
 *
 * Converts a Markdown text file to an HTML one.
 * Depends: Showdown [https://github.com/showdownjs/showdown] JavaScript library v1.8.6
 * Depends: jjs (Java 8)
 * Arguments: arguments[0] = Markdown file name.
 * Use case: jjs -scripting showdown.min.js markdowntohtml.js -- README.md > README.html
 * @author Nuno A. C. Henriques [nunoachenriques.net]
 */

var converter = new showdown.Converter();
converter.setFlavor('github');
converter.setOption('simpleLineBreaks', false);
converter.setOption('openLinksInNewWindow', true);
converter.setOption('taskLists', true);
print(converter.makeHtml(readFully(arguments[0])));
