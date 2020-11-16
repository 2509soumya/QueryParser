package com.self.queryparsers.refactor.utils;

public class ParserRegex {
	
	public static final String entity_regex="(?i)([\\w]+[.])?([\\w]+)";
	public static final String literal_regex="(([\'][^\']*[\'])|([\"][^\"]*[\"])|([\\d]+))";
	public static final String arg_colalias="(?i)(([\\s]+AS[\\s]+[\\w]+)|([\\s]+[\\w]+)|([\\s]+[\"][\\w]+[\"])|([\\s]+[\'][\\w]+[\']))?";
	public static final String arg_select_pattern="(?i)([\\s]*[(]?SELECT[\\s]+)";
	public static final String func_pattern="([\\w]+)[(]";
	public static final String func_entitypattern=entity_regex;
	public static final String func_literalpattern=literal_regex;
	public static final String func_argumentDelimiter="(?i)[\\s]*(([,])|[)])";
	public static final String casewhen_regex="(?i)([\\s]*case[\\s]+when[\\s+])";
	public static final String when_regex="(?i)(when[\\s+])";
	public static final String expr_codeblock="([\\s]*)([(][^()]*[)])";
	public static final String select_pattern="(?i)([\\s]*SELECT[\\s]+)";
	public static final String argumentDelimiter="(?i)[\\s]*(([,])|(FROM))";
	public static final String arithmeticop_regex="[*+-]|[/%]";
	public static final String bitwiseop_regex="[&|^]";
	public static final String comparisonop_regex="(?i)(==|=|<>|>=|<=|>|<|ANY|BETWEEN|EXISTS|IN|LIKE|NOT|IS NOT|IS)";
	public static final String joiningop_regex="(?i)(AND|OR)[\\s]+";
	public static final String tableentity_regex="(?i)(([\\w]+[.])|([\\w-]+[.][\\w]+[.]))?([\\w]+)";
	public static final String joinregex="(?i)((INNER JOIN)|(OUTER JOIN)|(LEFT OUTER JOIN)|(RIGHT OUTER JOIN)|(LEFT JOIN)|(RIGHT JOIN))";
	public static final String keywordregex="(?i)(//bCURRENT_DATE//b|//bNOT NULL//b|//bNULL//b|[*])";
	public static final String aggregateregex="(?i)((GROUP BY)|(ORDER BY))";
	public static final String aggregateProjection="(?i)((DISTINCT)|(TOP[\\s]+[0-9]+))[\\s]+";

}
