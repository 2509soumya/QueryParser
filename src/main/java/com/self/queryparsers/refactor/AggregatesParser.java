package com.self.queryparsers.refactor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.self.queryparsers.refactor.utils.ExpressionParser;

public class AggregatesParser {
		
	public JSONObject parse(String aggfuncregex) throws Exception{
		String query=SelectionParser.query;
		query=query.trim();
		
		ExpressionParser expr_parser=new ExpressionParser();
		System.out.println("Query at aggregate parser: "+query);
		
		JSONObject agg_obj=new JSONObject();
		JSONArray agg_arr=new JSONArray();
		Pattern agg_pattern=Pattern.compile("^"+aggfuncregex);
		Matcher agg_matcher=agg_pattern.matcher(query);
		if (agg_matcher.find()) {
			String aggfunc=agg_matcher.group();
			aggfunc=aggfunc.trim();
			query=query.replaceFirst(aggfuncregex, "");
			System.out.println("func: "+aggfunc);
			System.out.println("after func: "+query);
			agg_obj.put("func",aggfunc);
			char separating_char=',';
			while(separating_char==',') {
				query=query.substring(1, query.length());
				JSONArray agg_arrobj=new JSONArray();
				agg_arr.put(expr_parser.parse(agg_arrobj));
				query=query.trim();
				if(query.length()>0) {
					separating_char=query.charAt(0);
				}else {
					break;
				}
				System.out.println("separating char:"+separating_char);
			}
			agg_obj.put("arguments", agg_arr);
			
			query=query.trim();
			if(aggfunc.equalsIgnoreCase("order by")) {
				System.out.println("Agg func attribute order by:"+query);
				System.out.println(query.matches("(?i)((ASC)|(DESC))"+"(.*)"));
				if(query.matches("(?i)((ASC)|(DESC))"+"(.*)")) {
					Pattern aggfunc_pattern=Pattern.compile("(?i)((ASC)|(DESC))");
					Matcher aggfunc_matcher=aggfunc_pattern.matcher(query);
					if(aggfunc_matcher.find()) {
						String aggfunc1=aggfunc_matcher.group();
						agg_obj.put("funcattr", aggfunc1);
						query=query.replaceFirst("(?i)((ASC)|(DESC))", "");
					}
				}
			}
		}
		return agg_obj;
	}

}
