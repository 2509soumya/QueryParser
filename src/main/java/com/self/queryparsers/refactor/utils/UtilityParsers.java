package com.self.queryparsers.refactor.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

import com.self.queryparsers.refactor.SelectionParser;

public class UtilityParsers {
	
	public static JSONObject keywordParser() throws Exception {
		String query=SelectionParser.query;
		query=query.trim();
		JSONObject obj_keyword=new JSONObject();
		Pattern key_pattern=Pattern.compile(ParserRegex.keywordregex);
		Matcher key_matcher=key_pattern.matcher(query);
		if (key_matcher.find()) {
			String key=key_matcher.group();
			query=query.replaceFirst(ParserRegex.keywordregex, "");
			obj_keyword.put("key", key);
			return obj_keyword;
		}else {
			throw new Exception("Keyword matching failed");
		}
	}
	
	public static JSONObject literalfuncParser() throws Exception{
		String query=SelectionParser.query;
		JSONObject column_object=new JSONObject();	
		Pattern entitypattern=Pattern.compile(ParserRegex.func_literalpattern);
		Matcher matcher=entitypattern.matcher(query);
		if (matcher.find()) {
				column_object.put("literalvalue", matcher.group());
		}else {
			throw new Exception("Not a expression literal");
		}
		query=query.replaceFirst(ParserRegex.func_literalpattern, "");
		return column_object;
	}
	
	public static JSONObject entityfuncParser() throws Exception{
		String query=SelectionParser.query;
		JSONObject column_object=new JSONObject();	
		Pattern entitypattern=Pattern.compile(ParserRegex.func_entitypattern);
		Matcher matcher=entitypattern.matcher(query);
		if (matcher.find()) {
			System.out.println(matcher.group());
			if(matcher.group(1)!=null) {
				column_object.put("enityalias", matcher.group(1).replaceFirst("[.]", ""));
			}
			column_object.put("colname", matcher.group(2));
		}else {
			throw new Exception("Not a entity selection type");
		}
		query=query.replaceFirst(ParserRegex.func_entitypattern, "");
		return column_object;
	}
	

}
