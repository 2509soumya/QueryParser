package com.self.queryparsers.refactor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.self.queryparsers.refactor.utils.ExpressionParser;
import com.self.queryparsers.refactor.utils.FunctionsParser;
import com.self.queryparsers.refactor.utils.ParserRegex;

public class ProjectionsParser {
	
	public JSONArray parse() throws Exception {
		String query=SelectionParser.query;
		char separatingchar=',';
		JSONArray colsArray=new JSONArray();
		while(separatingchar==',') {
			JSONObject column_object=new JSONObject();
			query=query.trim();
			System.out.println(query);
			column_object=expressionArgumentParser();	
			query=query.trim();
			separatingchar=query.charAt(0);
			System.out.println(separatingchar);
			System.out.println(query);
			if(separatingchar==',') {
				query=query.substring(1, query.length());
			}
			colsArray.put(column_object);
		}
		return colsArray;
	}
	
	public static JSONObject expressionArgumentParser() throws Exception{
		String query=SelectionParser.query;
		JSONArray exprobj=new JSONArray();
		JSONObject columndef_object=new JSONObject();
		
		boolean openingbrac=false;
		if(query.startsWith("(")){
			System.out.println("replacing opening paran");
			query=query.replaceFirst("[(]", "");
			openingbrac=true;
		}
		
		ExpressionParser expr_parser=new ExpressionParser();
		exprobj=expr_parser.parse(exprobj);
		FunctionsParser func_parser=new FunctionsParser();
		
		if(query.matches("[\\s]*(([(]FORMAT)|([(][\\w]+[(]))"+"(.*)")) {
			JSONArray castexprobj=new JSONArray();
			JSONObject columnobj=new JSONObject();
			JSONObject columndefobj=new JSONObject();
			columndefobj.put("func_name","CAST");
			JSONObject finalexpr=new JSONObject();
			finalexpr.put("expression", exprobj);
			//expression obj
		    query=query.trim();
		    String formatregex="[(][\\s]*(FORMAT)[\\s]+(([\'][^\']*[\'])|([\"][^\"]*[\"]))[\\s]*[)]";
			Pattern format_pattern=Pattern.compile("^"+formatregex);
			Matcher format_matcher=format_pattern.matcher(query);
			if (format_matcher.find()) {
				System.out.println(format_matcher.group(2));
				finalexpr.put("format", format_matcher.group(2));
				query=query.replaceFirst(formatregex, "");
			}else {
				System.out.println("No match!!");
			}
			query=query.trim();
			
			if(query.charAt(0)=='(') {
				query=query.substring(1, query.length());
			}
			
			if(query.matches(func_parser.parse() + "(.*)")) {
				JSONObject dttype_obj=func_parser.parse();
				finalexpr.put("datatype", dttype_obj);
			}else if(query.matches("(?)DATE" + "(.*)")) {
				JSONObject dttype_obj=new JSONObject();
				dttype_obj.put("keyword", "DATE");
				finalexpr.put("datatype", dttype_obj);
				query=query.replaceFirst("^(?i)DATE", "");
			}else {
				throw new Exception("Cast datatype not recognized");
			}
			
			query=query.trim();
			if(query.charAt(0)==')') {
				query=query.substring(1, query.length());
			}
			JSONArray finalarr=new JSONArray();
			finalarr.put(finalexpr);
			columndefobj.put("arguments", finalarr);
			
			columnobj.put("def",columndefobj);
			columnobj.put("expr_type","operand");
			columnobj.put("type","function");
			castexprobj.put(columnobj);
			columndef_object.put("expression",castexprobj);
		}else {
			columndef_object.put("expression",exprobj);
		}
		
		query=query.trim();
		
		if(openingbrac && query.charAt(0)==')') {
			query=query.replaceFirst("[)]", "");
		}
		
		query=query.trim();
	
		if(query.matches("(?i)(FROM)"+"(.*)")) {
			System.out.println("from clause started");
		}else {
			query=" "+query;
			System.out.println("Query after expression parsing: "+query);
			Pattern colalias_pattern=Pattern.compile("^"+ParserRegex.arg_colalias);
			Matcher alias_matcher=colalias_pattern.matcher(query);
			if (alias_matcher.find()) {
				String alias=alias_matcher.group().replaceFirst("^(?i)[\\s]*AS[\\s]*", "").trim();
				System.out.println("Matched alias: "+alias);
				
				if(alias.length()>0 && alias.charAt(0)=='\'' && alias.charAt(alias.length()-1)=='\'') {
					alias=alias.substring(1, alias.length()-1);
				}else if(alias.length()>0 && alias.charAt(0)=='\"' && alias.charAt(alias.length()-1)=='\"') {
					alias=alias.substring(1, alias.length()-1);
				}
				
				columndef_object.put("colalias", alias);
			}else {
				throw new Exception("Alias Matching exception for function literal");
			}
			query=query.replaceFirst(ParserRegex.arg_colalias, "");
		}
		return columndef_object;
	}

}
