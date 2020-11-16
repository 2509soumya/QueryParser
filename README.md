# QueryParser


This Parser has been developed with a vision such that it takes a selection query of any language and is able to parse and convert it
to a tree (represented by a jsonobject).

The core logic of the parser works by using a moving cursor implementation where parser looks ahead and identfies the nature of the 
succeeding block ,it then consumes the block by patternmatching aka regex matchers , and forms a structured json object 
which can easily be interpreted by any program.

The uses of such a parser are manifold..

1 > It helps you extract informative blocks from queries (the tables involved in the query etc).
2 > JSON output of the parser can be used by another adapter/library , which converts it to a query of another language.Thus the 
output generated by the parser would act as an intermediate state for language conversion tools.
3 > Syntax checking of queries.

Usage :
========
Replace the query variable in "QueryParser.java" with any "teradata query" and run the class(currently code only supports teradata queries)

The output will have the json object generated by the parser.
