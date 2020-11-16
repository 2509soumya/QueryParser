package com.self.queryparsers.refactor.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import com.self.queryparsers.refactor.SelectionParser;

public class CaseWhenThenParser {
	
	public JSONArray parse() throws Exception {
		String query=SelectionParser.query;
		JSONArray casewhenexpr=new JSONArray();
		System.out.println("Begin query: "+query);
		
		ExpressionParser expr_parser=new ExpressionParser();
		ConditionParser condtn_parser=new ConditionParser();
		
		if(query.matches(ParserRegex.casewhen_regex+"(.*)")) {
			
			query=query.replaceFirst("(?i)case[\\s]+", "");
			System.out.println("After case query: "+query);

			while(query.matches(ParserRegex.when_regex+"(.*)")) {
				JSONObject caseobj=new JSONObject();
				query=query.replaceFirst(ParserRegex.when_regex, "");				
				System.out.println("After when query: "+query);
				JSONArray conditionexpr_obj=new JSONArray();
				conditionexpr_obj=condtn_parser.parse(conditionexpr_obj);
				System.out.println("After condition  query: "+query);
				caseobj.put("when", conditionexpr_obj);
				query=query.trim();
				if(query.matches("(?i)(then)[\\s]+"+"(.*)")){
					query=query.replaceFirst("(?i)(then)[\\s]+","");
				}
				
				System.out.println("After then  query: "+query);				
				JSONArray thenexpr_obj=new JSONArray();
				thenexpr_obj=expr_parser.parse(thenexpr_obj);				
				caseobj.put("then", thenexpr_obj);
				casewhenexpr.put(caseobj);
				query=query.trim();
				}
			
			query=query.trim();
			if(query.matches("(?i)(else)[\\s]+"+"(.*)")){
				query=query.replaceFirst("(?i)(else)[\\s]+","");
				query=query.trim();
				System.out.println("After else  query: "+query);
				JSONObject elseobj=new JSONObject();
				JSONArray elseexpr_obj=new JSONArray();
				elseexpr_obj=expr_parser.parse(elseexpr_obj);
				elseobj.put("else", elseexpr_obj);
				casewhenexpr.put(elseobj);
			}else {
				throw new Exception("Parsed until : "+query);
			}
			
			query=query.trim();
			if(query.matches("(?i)(end)"+"(.*)")){
				query=query.replaceFirst("(?i)(end)","");
			}else {
				throw new Exception("Parsed until : "+query);
			}
		}
		return casewhenexpr;
	}

}
