package com.self.queryparsers.refactor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.self.queryparsers.refactor.utils.ParserRegex;

public class ColumnAggregateParser {
	
	
	public JSONArray parse(JSONArray aggproj_arrobj) {
		String query=SelectionParser.query;
		query=query.trim();
		JSONObject aggprojobj=new JSONObject();
		Pattern aggproj_pattern=Pattern.compile("^"+ParserRegex.aggregateProjection);
		Matcher aggproj_matcher=aggproj_pattern.matcher(query);
		if(aggproj_matcher.find()) {
			query=query.replaceFirst("^"+ParserRegex.aggregateProjection, "");
			String aggprojfunc=aggproj_matcher.group().trim();
			aggprojobj.put("aggproj", aggprojfunc);
			aggproj_arrobj.put(aggprojobj);
			return parse(aggproj_arrobj);
		}else {
			return aggproj_arrobj;
		}
	}

}
