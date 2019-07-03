# API Docs - v5.0.3

!!! Info "Tested Siddhi Core version: *<a target="_blank" href="http://siddhi.io/en/v5.0/docs/query-guide/">5.0.0</a>*"
    It could also support other Siddhi Core minor versions.

## Regex

### find *<a target="_blank" href="http://siddhi.io/en/v5.0/docs/query-guide/#function">(Function)</a>*
<p style="word-wrap: break-word">Finds the subsequence that matches the given regex pattern.</p>
<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>

```
<BOOL> regex:find(<STRING> regex, <STRING> input.sequence, <INT> starting.index)
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">regex</td>
        <td style="vertical-align: top; word-wrap: break-word">A regular expression that is matched to a sequence in order to find the subsequence of the same. For example, <code>\d\d(.*)WSO2</code>.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">input.sequence</td>
        <td style="vertical-align: top; word-wrap: break-word">The input sequence to be matched with the regular expression. For example, <code>21 products are produced by WSO2</code>.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">starting.index</td>
        <td style="vertical-align: top; word-wrap: break-word">The starting index of the input sequence from where the input sequence ismatched with the given regex pattern.For example, <code>10</code>.</td>
        <td style="vertical-align: top">0</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
regex:find('\d\d(.*)WSO2', '21 products are produced by WSO2 currently')
```
<p style="word-wrap: break-word">This method attempts to find the subsequence of the input.sequence that matches the regex pattern, <code>\d\d(.*)WSO2</code>. It returns <code>true</code> as a subsequence exists.</p>

<span id="example-2" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 2</span>
```
regex:find('\d\d(.*)WSO2', '21 products are produced by WSO2.', 4)
```
<p style="word-wrap: break-word">This method attempts to find the subsequence of the input.sequence that matches the regex pattern, <code>\d\d(.*)WSO2</code> starting from index <code>4</code>. It returns 'false' as subsequence does not exists.</p>

### group *<a target="_blank" href="http://siddhi.io/en/v5.0/docs/query-guide/#function">(Function)</a>*
<p style="word-wrap: break-word">Returns the subsequence captured by the given group during the regex match operation.</p>
<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>

```
<STRING> regex:group(<STRING> regex, <STRING> input.sequence, <INT> group.id)
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">regex</td>
        <td style="vertical-align: top; word-wrap: break-word">A regular expression. For example, <code>\d\d(.*)WSO2.</code></td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">input.sequence</td>
        <td style="vertical-align: top; word-wrap: break-word">The input sequence to be matched with the regular expression. For example, 2<code>1 products are produced by WSO2</code>.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">group.id</td>
        <td style="vertical-align: top; word-wrap: break-word">The given group id of the regex expression. For example, <code>2</code>.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
regex:group('\d\d(.*)(WSO2.*)(WSO2.*)', '21 products are produced within 10 years by WSO2 currently by WSO2 employees', 3)
```
<p style="word-wrap: break-word">Function returns 'WSO2 employees', the subsequence captured by the  groupID 3 according to the regex pattern, <code>\d\d(.*)(WSO2.*)(WSO2.*)</code>.</p>

### lookingAt *<a target="_blank" href="http://siddhi.io/en/v5.0/docs/query-guide/#function">(Function)</a>*
<p style="word-wrap: break-word">Match the input.sequence from the beginning against the regex pattern, and unlike <code>regex:matches() it does not require that the entire input.sequence be matched.</code></p>
<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>

```
<BOOL> regex:lookingAt(<STRING> regex, <STRING> input.sequence)
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">regex</td>
        <td style="vertical-align: top; word-wrap: break-word">A regular expression. For example, <code>\d\d(.*)WSO2</code>.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">input.sequence</td>
        <td style="vertical-align: top; word-wrap: break-word">The input sequence to be matched with the regular expression. For example, <code>21 products are produced by WSO2</code>.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
regex:lookingAt('\d\d(.*)(WSO2.*)', '21 products are produced by WSO2 currently in Sri Lanka')
```
<p style="word-wrap: break-word">Function matches the input.sequence against the regex pattern, <code>\d\d(.*)(WSO2.*)</code> from the beginning, and as it matches it returns <code>true</code>.</p>

<span id="example-2" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 2</span>
```
regex:lookingAt('WSO2(.*)middleware(.*)', 'sample test string and WSO2 is situated in trace and it's a middleware company')
```
<p style="word-wrap: break-word">Function matches the input.sequence against the regex pattern, <code>WSO2(.*)middleware(.*)</code> from the beginning, and as it does not match it returns <code>false</code>.</p>

### matches *<a target="_blank" href="http://siddhi.io/en/v5.0/docs/query-guide/#function">(Function)</a>*
<p style="word-wrap: break-word">Matches the entire input.sequence against the regex pattern.</p>
<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>

```
<BOOL> regex:matches(<STRING> regex, <STRING> input.sequence)
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">regex</td>
        <td style="vertical-align: top; word-wrap: break-word">A regular expression. For example, <code>\d\d(.*)WSO2</code>.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">input.sequence</td>
        <td style="vertical-align: top; word-wrap: break-word">The input sequence to be matched with the regular expression. For example, <code>21 products are produced by WSO2</code>.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
regex:matches('WSO2(.*)middleware(.*)', 'WSO2 is situated in trace and its a middleware company')
```
<p style="word-wrap: break-word">Function matches the entire input.sequence against <code>WSO2(.*)middleware(.*)</code> regex pattern, and as it matches it returns `true`.</p>

<span id="example-2" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 2</span>
```
regex:matches('WSO2(.*)middleware', 'WSO2 is situated in trace and its a middleware company')
```
<p style="word-wrap: break-word">Function matches the entire input.sequence against <code>WSO2(.*)middleware</code> regex pattern. As it does not match it returns `false`.</p>

