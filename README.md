siddhi-execution-regex
======================================

The **siddhi-execution-regex extension** is an extension to <a target="_blank" href="https://wso2.github
.io/siddhi">Siddhi</a> that provides basic RegEx execution capabilities.

Find some useful links below:

* <a target="_blank" href="https://github.com/wso2-extensions/siddhi-execution-regex">Source code</a>
* <a target="_blank" href="https://github.com/wso2-extensions/siddhi-execution-regex/releases">Releases</a>
* <a target="_blank" href="https://github.com/wso2-extensions/siddhi-execution-regex/issues">Issue tracker</a>

## Latest API Docs 

Latest API Docs is <a target="_blank" href="https://wso2-extensions.github.io/siddhi-execution-regex/api/4.0.2-SNAPSHOT">4.0.2-SNAPSHOT</a>.

## How to use 

**Using the extension in <a target="_blank" href="https://github.com/wso2/product-sp">WSO2 Stream Processor</a>**

* You can use this extension in the latest <a target="_blank" href="https://github.com/wso2/product-sp/releases">WSO2 Stream Processor</a> that is a part of <a target="_blank" href="http://wso2.com/analytics?utm_source=gitanalytics&utm_campaign=gitanalytics_Jul17">WSO2 Analytics</a> offering, with editor, debugger and simulation support. 

* This extension is shipped by default with WSO2 Stream Processor, if you wish to use an alternative version of this extension you can replace the component <a target="_blank" href="https://github.com/wso2-extensions/siddhi-execution-regex/releases">jar</a> that can be found in the `<STREAM_PROCESSOR_HOME>/lib` directory.

**Using the extension as a <a target="_blank" href="https://wso2.github.io/siddhi/documentation/running-as-a-java-library">java library</a>**

* This extension can be added as a maven dependency along with other Siddhi dependencies to your project.

```
     <dependency>
        <groupId>org.wso2.extension.siddhi.execution.regex</groupId>
        <artifactId>siddhi-execution-regex</artifactId>
        <version>x.x.x</version>
     </dependency>
```

## Jenkins Build Status

---

|  Branch | Build Status |
| :------ |:------------ | 
| master  | [![Build Status](https://wso2.org/jenkins/job/siddhi/job/siddhi-execution-regex/badge/icon)](https://wso2.org/jenkins/job/siddhi/job/siddhi-execution-regex/) |

---

## Features

* <a target="_blank" href="https://wso2-extensions.github.io/siddhi-execution-regex/api/4.0.2-SNAPSHOT/#matches-function">matches</a> *(<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#functions">Function</a>)*<br><div style="padding-left: 1em;"><p>This method attempts to match the entire 'inputSequence' against the 'regex' pattern.</p></div>
* <a target="_blank" href="https://wso2-extensions.github.io/siddhi-execution-regex/api/4.0.2-SNAPSHOT/#find-function">find</a> *(<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#functions">Function</a>)*<br><div style="padding-left: 1em;"><p>These methods attempts to find the next sub-sequence of the 'inputSequence' that matches the 'regex' pattern.</p></div>
* <a target="_blank" href="https://wso2-extensions.github.io/siddhi-execution-regex/api/4.0.2-SNAPSHOT/#group-function">group</a> *(<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#functions">Function</a>)*<br><div style="padding-left: 1em;"><p>This method returns the input sub-sequence captured by the given group during the previous match operation.</p></div>
* <a target="_blank" href="https://wso2-extensions.github.io/siddhi-execution-regex/api/4.0.2-SNAPSHOT/#lookingat-function">lookingAt</a> *(<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#functions">Function</a>)*<br><div style="padding-left: 1em;"><p>This method attempts to match the 'inputSequence', starting at the beginning, against the 'regex' pattern.</p></div>

## How to Contribute
 
  * Please report issues at <a target="_blank" href="https://github.com/wso2-extensions/siddhi-execution-regex/issues">GitHub Issue Tracker</a>.
  
  * Send your contributions as pull requests to <a target="_blank" href="https://github.com/wso2-extensions/siddhi-execution-regex/tree/master">master branch</a>. 
 
## Contact us 

 * Post your questions with the <a target="_blank" href="http://stackoverflow.com/search?q=siddhi">"Siddhi"</a> tag in <a target="_blank" href="http://stackoverflow.com/search?q=siddhi">Stackoverflow</a>. 
 
 * Siddhi developers can be contacted via the mailing lists:
 
    Developers List   : [dev@wso2.org](mailto:dev@wso2.org)
    
    Architecture List : [architecture@wso2.org](mailto:architecture@wso2.org)
 
## Support 

* We are committed to ensuring support for this extension in production. Our unique approach ensures that all support leverages our open development methodology and is provided by the very same engineers who build the technology. 

* For more details and to take advantage of this unique opportunity contact us via <a target="_blank" href="http://wso2.com/support?utm_source=gitanalytics&utm_campaign=gitanalytics_Jul17">http://wso2.com/support/</a>. 

