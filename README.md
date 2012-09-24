yasgui: Yet another SPARQL GUI
======

I work on this tool as a pet-project, as I could not find any other SPARQL graphical user interface which fits all my requirements:
* Work on all endpoints (not just the CORS-enabled ones)
* Work on a linux distrubution (in this case it works cross-platform as long as there is a java server application)
* Easy-to-work user interface (i.e. prefix fetching, syntax highlighting/checking, storing queries)

Used services and libraries
-------
 * Thanks to Marijn Haverbeke for [CodeMirror][1] and the SPARQL highlight plugin
 * [SMARTGWT][2]
 * Thanks to the people from [FLINT SPARQL editor][3] and the javascript SPARQL parser they created using [peg.js][4]
 * [CKAN][5] dataset catalogue
 * [prefix.cc][6]





  [1]: http://codemirror.net/
  [2]: http://codemirror.net/
  [3]: https://github.com/TSO-Openup/FlintSparqlEditor
  [4]: http://pegjs.majda.cz/
  [5]: http://thedatahub.org/
  [6]: http://prefix.cc