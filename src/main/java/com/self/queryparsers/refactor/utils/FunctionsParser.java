package com.self.queryparsers.refactor.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.self.queryparsers.refactor.SelectionParser;

public class FunctionsParser {

	public JSONObject parse() throws Exception {
		String query=SelectionParser.query;
		JSONObject column_object=new JSONObject();	
		Pattern expr_funcpattern=Pattern.compile(ParserRegex.func_pattern);
		Matcher matcher=expr_funcpattern.matcher(query);
		String funcname="";
		int end_idx=-1;
		if (matcher.find()) {
			System.out.println("Funcname: "+matcher.group(1));
			funcname=matcher.group(1);
			column_object.put("func_name",matcher.group(1));
			end_idx=matcher.end();
		}else {
			throw new Exception("Not a function selection type");
		}
		System.out.println(query.substring(end_idx, query.length()));
		query=query.substring(end_idx, query.length()).trim();
		
		if(query.indexOf(')')!=0) {
			JSONArray argumentsArray=new JSONArray();
			if(funcname.equalsIgnoreCase("cast")) {
				argumentsArray=castParser();
			}else {
				argumentsArray=func_argumentParser(funcname);
			}
			column_object.put("arguments", argumentsArray);
		}else {
			query=query.substring(1, query.length());
		}
		query=query.trim();
		return column_object;
	}
	
	public JSONArray func_argumentParser(String functionname) throws Exception{
		String query=SelectionParser.query;
		JSONArray argsArray=new JSONArray();
		char separatingchar=',';
		while(separatingchar==',') {
			JSONArray arg_object=new JSONArray();
			query=query.trim();
			ExpressionParser parser=new ExpressionParser();
			arg_object=parser.parse(arg_object);
			query=query.trim();
			separatingchar=query.charAt(0);
			if(separatingchar==',') {
				query=query.substring(1, query.length());
			}
			argsArray.put(arg_object);
		}
		if(query.indexOf(')')==0) {
			query=query.substring(1, query.length());
		}else {
			System.out.println("Remaining query :"+query);
			throw new Exception("Function Arguments not parsed successfully: "+functionname);
		}
		return argsArray;
	}
	
	public JSONArray castParser() throws Exception{
		String query=SelectionParser.query;
		
		JSONObject finalexpr=new JSONObject();
		query=query.replaceFirst("^(?i)(CAST[(])", "");
		JSONArray exprobj=new JSONArray();
		
		ExpressionParser parser=new ExpressionParser();
		parser.parse(exprobj);
		
		finalexpr.put("expression", exprobj);
		
		System.out.println("Outside Expression Parser in cast:"+query);
		query=query.replaceFirst("^(?i)[\\s]*AS[\\s]+", "");
		
		if(query.matches(ParserRegex.func_pattern + "(.*)")) {
			JSONObject dttype_obj=parse();
			finalexpr.put("datatype", dttype_obj);
		}else if(query.matches("(?)DATE" + "(.*)")) {
			JSONObject dttype_obj=new JSONObject();
			dttype_obj.put("keyword", "DATE");
			finalexpr.put("datatype", dttype_obj);
			query=query.replaceFirst("^(?i)DATE", "");
		}else {
			throw new Exception("Cast datatype not recognized");
		}
		
		query=query.replaceFirst("[)]", "");
        System.out.println("Before alias matching "+query);
		JSONArray finalarr=new JSONArray();
		finalarr.put(finalexpr);
		return finalarr;
	}

}
