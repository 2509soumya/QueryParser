package com.self.queryparsers.refactor;

import org.json.JSONArray;

import com.self.queryparsers.refactor.utils.ConditionParser;

public class PredicatesParser {
	
	public JSONArray parse() throws Exception{
		String query=SelectionParser.query;
		query=query.trim();
		JSONArray conditionexpr_obj=new JSONArray();
		ConditionParser condtn_parser=new ConditionParser();
		
		System.out.println("Inside predicate parser :"+query);
		if(query.matches("(?i)(WHERE)"+"(.*)")) {
			query=query.replaceFirst("(?i)(WHERE)", "");
			query=query.trim();
			conditionexpr_obj=condtn_parser.parse(conditionexpr_obj);
		}
		return conditionexpr_obj;
	}

}
