package com.self.queryparsers.refactor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.self.queryparsers.refactor.utils.ConditionParser;
import com.self.queryparsers.refactor.utils.ParserRegex;

public class TableJoinParser {

	
	public JSONArray parse() throws JSONException, Exception {
		String query=SelectionParser.query;
        query=query.trim();
		System.out.println("Table join parser- query : "+query);
		
		
		ConditionParser condtn_parser=new ConditionParser();
		
		//parse driving table
		JSONArray selection_object=new JSONArray();
		//parse for table
		if(query.matches("(?i)(FROM)"+"(.*)")) {
			query=query.replaceFirst("(?i)(FROM)", "");
		}else {
			throw new Exception("Query FROM clause is not where expected");
		}
		
		System.out.println("query state : " + query);
		
		JSONObject tabobj=new JSONObject();
		tabobj.put("type", "drivingtable");
		tabobj.put("def", tableParser());
		
		selection_object.put(tabobj);
		
		query=query.trim();
		while(query.matches(ParserRegex.joinregex+"(.*)")) {
			JSONObject joinblock=new JSONObject();
			JSONObject joinobj=new JSONObject();
			Pattern join_pattern=Pattern.compile("^"+ParserRegex.joinregex+"(.*)");
			Matcher join_matcher=join_pattern.matcher(query);
			if (join_matcher.find()) {
				String operator=join_matcher.group(1);
				joinobj.put("joinkey",operator);
				query=query.replaceFirst(ParserRegex.joinregex, "");
			}
			
			joinblock.put("KEY",joinobj);
			joinblock.put("FROM",tableParser());
			//conditional parsing
			
			query=query.trim();
			if(query.matches("(?i)(ON)"+"(.*)")) {
				query=query.replaceFirst("(?i)(ON)", "");
				query=query.trim();
				JSONArray conditionexpr_obj=new JSONArray();
				conditionexpr_obj=condtn_parser.parse(conditionexpr_obj);
				joinblock.put("ON",conditionexpr_obj);
			}
			selection_object.put(joinblock);
		}
		
		return selection_object;
	}
	
	public JSONObject tableParser() throws Exception {
		String query=SelectionParser.query;
		query=query.trim();
		
		SelectionParser sel_parser=new SelectionParser();
		
		JSONObject tabobj=new JSONObject();
		if(query.matches(ParserRegex.tableentity_regex+ "(.*)")) {
			tabobj.put("type", "tableentity");
			tabobj.put("entity", parseTableentity());
		}else if(query.matches(ParserRegex.arg_select_pattern+ "(.*)")) {
			//nested select
			boolean isParan=false;
			if(query.charAt(0)=='(') {
				query=query.substring(1, query.length());
				isParan=true;
			}
			tabobj.put("type", "selectentity");
			tabobj.put("entity", sel_parser.parse());
			if(isParan && query.charAt(0)==')') {
				query=query.substring(1, query.length());
			}
		}else {
			throw new Exception("table nature could not be infered");
		}
		
		query=query.trim();
		if(query.matches("(?i)((WHERE)|"+ParserRegex.joinregex+")"+"(.*)")) {
			System.out.println("where clause started");
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
				
				tabobj.put("tablealias", alias);
			}else {
				throw new Exception("Alias Matching exception for function literal");
			}
			query=query.replaceFirst(ParserRegex.arg_colalias, "");
		}
		
		
		return tabobj;
	}
	
	public JSONObject parseTableentity() throws Exception {
		JSONObject entity_object=new JSONObject();
		String query=SelectionParser.query;
		entity_object.put("type", "table");
		
		JSONObject table_object=new JSONObject();
		Pattern tableentitypattern=Pattern.compile(ParserRegex.tableentity_regex);
		Matcher matcher=tableentitypattern.matcher(query);
		
		String dbname=null;
		String schemaname=null;
		String tablename=null;
		
		if (matcher.find()) {
			String entityname=matcher.group();
			
			System.out.println("Entity name: "+entityname);
			entityname=entityname.trim();
			String[] tabarray=entityname.split("[.]");
			if(tabarray.length==3) {
				dbname=tabarray[0];
				schemaname=tabarray[1];
				tablename=tabarray[2];
			}else if(tabarray.length==2) {
				schemaname=tabarray[0];
				tablename=tabarray[1];
			}else {
				tablename=entityname;
			}
		}
		query=query.replaceFirst(ParserRegex.tableentity_regex, "");
		table_object.put("dbname", dbname);
		table_object.put("schemaname", schemaname);
		table_object.put("tablename", tablename);
		entity_object.put("meta", table_object);
		
		System.out.println("query-"+query);
		return entity_object;
	}
}
