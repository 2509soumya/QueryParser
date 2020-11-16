package com.self.queryparsers.refactor.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.self.queryparsers.refactor.SelectionParser;

public class ExpressionParser{

	public JSONArray parse(JSONArray expr_object) throws JSONException, Exception {
				
		String query=SelectionParser.query;
		query=query.trim();
		
		System.out.println("Query for expression parser:"+query);
		
		String operator_regex="[\\s]*("+ParserRegex.arithmeticop_regex+"|"+ParserRegex.bitwiseop_regex+")";		
		JSONObject operand_object=new JSONObject();
		if(query.matches(ParserRegex.keywordregex+ "(.*)")) {
			System.out.println("expr: keyword");
			operand_object.put("type", "keyword");
			operand_object.put("def", UtilityParsers.keywordParser());
		}
		else if(query.matches(ParserRegex.func_pattern + "(.*)")){
			System.out.println("expr: function");
			FunctionsParser parser=new FunctionsParser();
			operand_object.put("type", "function");
			operand_object.put("def", parser.parse());
		}
		else if(query.matches(ParserRegex.expr_codeblock+ "(.*)")) {
			System.out.println("Matched nested expr block: "+query);
			query=query.replaceFirst("[(]", "");
			JSONArray childexprobj=new JSONArray();
			operand_object.put("type", "expression");
			operand_object.put("def", parse(childexprobj));
			if(query.charAt(0)==')') {
				query=query.replaceFirst("[)]", "");
			}else {
				throw new Exception("Failed while parsing nested expression");
			}
		}
		else if(query.matches(ParserRegex.casewhen_regex+"(.*)")) {
			System.out.println("expr: case when column");
			operand_object.put("type", "case_when");
			CaseWhenThenParser casewhenparser=new CaseWhenThenParser();
			operand_object.put("def", casewhenparser.parse());
		}
		else if(query.matches(ParserRegex.arg_select_pattern+"(.*)")) {
			System.out.println("expr: select column");
			//nested select
			boolean isParan=false;
			if(query.charAt(0)=='(') {
				query=query.substring(1, query.length());
				isParan=true;
			}
			operand_object.put("type", "selectentity");
			SelectionParser sel_parser=new SelectionParser();
			operand_object.put("def", sel_parser.parse());
			
			if(isParan && query.charAt(0)==')') {
				query=query.substring(1, query.length());
			}
		}
		else if(query.matches(ParserRegex.func_literalpattern +"(.*)")){
			System.out.println("expr: literal");
			operand_object.put("type", "literal");
			operand_object.put("def", UtilityParsers.literalfuncParser());
		}
		else if(query.matches(ParserRegex.func_entitypattern +"(.*)")) {
			System.out.println("expr: entity column");
			operand_object.put("type", "entity");
			operand_object.put("def", UtilityParsers.entityfuncParser());
		}else {
			System.out.println("Else in expression parser: "+query);
			throw new Exception("No expr match");
		}
		
		operand_object.put("expr_type", "operand");

		expr_object.put(operand_object);		
		System.out.println(query);
		System.out.println(expr_object.toString());
		
		JSONObject operator_object=new JSONObject();
		Pattern op_pattern=Pattern.compile("^"+operator_regex+"(.*)");
		Matcher op_matcher=op_pattern.matcher(query);
		if (op_matcher.find()) {
			String operator=op_matcher.group(1);
			operator_object.put("expr_type", "operator");
			operator_object.put("operator",operator);
			expr_object.put(operator_object);
			query=query.replaceFirst(operator_regex, "");
			return parse(expr_object);
		}else {
			return expr_object;
		}
	}

}
