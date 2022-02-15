Siddhi Execution Regex
======================================

  [![Jenkins Build Status](https://wso2.org/jenkins/job/siddhi/job/siddhi-execution-regex/badge/icon)](https://wso2.org/jenkins/job/siddhi/job/siddhi-execution-regex/)
  [![GitHub Release](https://img.shields.io/github/release/siddhi-io/siddhi-execution-regex.svg)](https://github.com/siddhi-io/siddhi-execution-regex/releases)
  [![GitHub Release Date](https://img.shields.io/github/release-date/siddhi-io/siddhi-execution-regex.svg)](https://github.com/siddhi-io/siddhi-execution-regex/releases)
  [![GitHub Open Issues](https://img.shields.io/github/issues-raw/siddhi-io/siddhi-execution-regex.svg)](https://github.com/siddhi-io/siddhi-execution-regex/issues)
  [![GitHub Last Commit](https://img.shields.io/github/last-commit/siddhi-io/siddhi-execution-regex.svg)](https://github.com/siddhi-io/siddhi-execution-regex/commits/master)
  [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

The **siddhi-execution-regex extension** is a <a target="_blank" href="https://siddhi.io/">Siddhi</a> extension that provides basic RegEx execution capabilities such as find, match, etc.

For information on <a target="_blank" href="https://siddhi.io/">Siddhi</a> and it's features refer <a target="_blank" href="https://siddhi.io/redirect/docs.html">Siddhi Documentation</a>. 

## Download

* Versions 5.x and above with group id `io.siddhi.extension.*` from <a target="_blank" href="https://mvnrepository.com/artifact/io.siddhi.extension.execution.regex/siddhi-execution-regex/">here</a>.
* Versions 4.x and lower with group id `org.wso2.extension.siddhi.*` from <a target="_blank" href="https://mvnrepository.com/artifact/org.wso2.extension.siddhi.execution.regex/siddhi-execution-regex">here</a>.

## Latest API Docs 

Latest API Docs is <a target="_blank" href="https://siddhi-io.github.io/siddhi-execution-regex/api/5.0.7">5.0.7</a>.

## Features

* <a target="_blank" href="https://siddhi-io.github.io/siddhi-execution-regex/api/5.0.7/#find-function">find</a> *(<a target="_blank" href="http://siddhi.io/en/v5.1/docs/query-guide/#function">Function</a>)*<br> <div style="padding-left: 1em;"><p><p style="word-wrap: break-word;margin: 0;">Finds the subsequence that matches the given regex pattern.</p></p></div>
* <a target="_blank" href="https://siddhi-io.github.io/siddhi-execution-regex/api/5.0.7/#group-function">group</a> *(<a target="_blank" href="http://siddhi.io/en/v5.1/docs/query-guide/#function">Function</a>)*<br> <div style="padding-left: 1em;"><p><p style="word-wrap: break-word;margin: 0;">Returns the subsequence captured by the given group during the regex match operation.</p></p></div>
* <a target="_blank" href="https://siddhi-io.github.io/siddhi-execution-regex/api/5.0.7/#lookingat-function">lookingAt</a> *(<a target="_blank" href="http://siddhi.io/en/v5.1/docs/query-guide/#function">Function</a>)*<br> <div style="padding-left: 1em;"><p><p style="word-wrap: break-word;margin: 0;">Matches the input.sequence from the beginning against the regex pattern, and unlike <code>regex:matches() it does not require that the entire input.sequence be matched.</code></p></p></div>
* <a target="_blank" href="https://siddhi-io.github.io/siddhi-execution-regex/api/5.0.7/#matches-function">matches</a> *(<a target="_blank" href="http://siddhi.io/en/v5.1/docs/query-guide/#function">Function</a>)*<br> <div style="padding-left: 1em;"><p><p style="word-wrap: break-word;margin: 0;">Matches the entire input.sequence against the regex pattern.</p></p></div>

## Dependencies 

There are no other dependencies needed for this extension. 

## Installation

For installing this extension on various siddhi execution environments refer Siddhi documentation section on <a target="_blank" href="https://siddhi.io/redirect/add-extensions.html">adding extensions</a>.

## Support and Contribution

* We encourage users to ask questions and get support via <a target="_blank" href="https://stackoverflow.com/questions/tagged/siddhi">StackOverflow</a>, make sure to add the `siddhi` tag to the issue for better response.

* If you find any issues related to the extension please report them on <a target="_blank" href="https://github.com/siddhi-io/siddhi-execution-regex/issues">the issue tracker</a>.

* For production support and other contribution related information refer <a target="_blank" href="https://siddhi.io/community/">Siddhi Community</a> documentation.
