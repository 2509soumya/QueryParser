package com.self.queryparsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class QueryParser {
	
	public static String entity_regex="(?i)([\\w]+[.])?([\\w]+|[*])";
	public static String literal_regex="(([\'][^\']*[\'])|([\"][^\"]*[\"])|([\\d]+))";
	public static String arg_colalias="(?i)(([\\s]+AS[\\s]+[\\w]+)|([\\s]+[\\w]+)|([\\s]+[\"][\\w]+[\"])|([\\s]+[\'][\\w]+[\']))?";
	public static String arg_select_pattern="(?i)([\\s]*[(]?SELECT[\\s]+)";
	public static String func_pattern="([\\w]+)[(]";
	public static String func_entitypattern=entity_regex;
	public static String func_literalpattern=literal_regex;
	public static String func_argumentDelimiter="(?i)[\\s]*(([,])|[)])";
	public static String casewhen_regex="(?i)([\\s]*case[\\s]+when[\\s+])";
	public static String when_regex="(?i)(when[\\s+])";
	public static String expr_codeblock="([\\s]*)([(][^()]*[)])";
	public static String select_pattern="(?i)([\\s]*SELECT[\\s]+)";
	public static String argumentDelimiter="(?i)[\\s]*(([,])|(FROM))";
	public static String arithmeticop_regex="[*+-]|[/%]";
	public static String bitwiseop_regex="[&|^]";
	public static String comparisonop_regex="(?i)(==|=|<>|>=|<=|>|<|ANY|BETWEEN|EXISTS|IN|LIKE|NOT|IS NOT|IS)";
	public static String joiningop_regex="(?i)(AND|OR)[\\s]+";
	public static String tableentity_regex="(?i)(([\\w]+[.])|([\\w-]+[.][\\w]+[.]))?([\\w]+)";
	public static String joinregex="(?i)((INNER JOIN)|(OUTER JOIN)|(LEFT OUTER JOIN)|(RIGHT OUTER JOIN)|(LEFT JOIN)|(RIGHT JOIN))";
	public static String keywordregex="(?i)(//bCURRENT_DATE//b|//bNOT NULL//b|//bNULL//b|[*])";
	public static String aggregateregex="(?i)((GROUP BY)|(ORDER BY))";
	public static String unionregex="(?i)((UNION)|(UNION ALL))";
	public static String aggregateProjection="(?i)((DISTINCT)|(TOP[\\s]+[0-9]+))[\\s]+";

	//public static String query="SELECT sales.* FROM product LEFT OUTER JOIN sales ON product_key = sales_product_key WHERE quantity > 10 AND product_name LIKE 'French%'";
	
	public String query;
	
	public QueryParser(String query) {
		this.query=query;
	}
	
	public static void main(String[] args) throws Exception {
		//Moving cursor implementation
		  QueryParser parser=new QueryParser("");
		  parser.parse();
	}
	
	public JSONObject parse() {
		JSONObject responseobject=new JSONObject();
		JSONArray finaljsonarr=new JSONArray();
		try {
	    	query=query.trim();
	    	finaljsonarr=selectWithUnionParser();
			System.out.println("Final Json : "+finaljsonarr.toString());
			System.out.println("Remaining query : "+query);
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

		query=query.trim();
		if(query.length()>0) {
			System.out.println("Query Parsing failed !!!");
			responseobject.put("message", "Query Parsing failed !!!");
			responseobject.put("parseduntil", query);
		}else {
			System.out.println("Query Parsing succeeded !!!");
			responseobject.put("parsedobject", finaljsonarr);
			responseobject.put("message", "Successfully parsed query");
		}
		return responseobject;
	}
	
	
	/*
    public static String query="ITEM_COST  (FORMAT '-ZZ,ZZZ,ZZZ,ZZ9.9999999') (CHAR(23)) AS hbo";

	public static void main(String[] args1) throws Exception {
		//Moving cursor implementation
		query=query.trim();
		JSONObject column_object=new JSONObject();
		JSONObject operand_object=new JSONObject();
		operand_object.put("type", "function");
		operand_object.put("expr_type", "operand");
		
		JSONObject  args=new JSONObject();
		args.put("arguments",miscellaneousParser());
		args.put("func_name","CAST");
		
		operand_object.put("def", args);
		JSONArray exprobject=new JSONArray();
		exprobject.put(operand_object);
		column_object.put("expression", exprobject);
		if(query.matches("(?i)(FROM)[\\s]+"+"(.*)")) {
			System.out.println("from clause started");
		}else {
			query=" "+query;
			System.out.println("Query after expression parsing:"+query);
			Pattern colalias_pattern=Pattern.compile(arg_colalias);
			Matcher alias_matcher=colalias_pattern.matcher(query);
			if (alias_matcher.find()) {
				String alias=alias_matcher.group().replaceFirst("^(?i)[\\s]*AS[\\s]*", "").trim();
				System.out.println("Matched alias: "+alias);
				
				if(alias.length()>0 && alias.charAt(0)=='\'' && alias.charAt(alias.length()-1)=='\'') {
					alias=alias.substring(1, alias.length()-1);
				}else if(alias.length()>0 && alias.charAt(0)=='\"' && alias.charAt(alias.length()-1)=='\"') {
					alias=alias.substring(1, alias.length()-1);
				}
				
				column_object.put("colalias", alias);
			}else {
				throw new Exception("Alias Matching exception for function literal");
			}
			query=query.replaceFirst(arg_colalias, "");
		}
		System.out.println(column_object);
		System.out.println("Remaning query: "+query);
		
	}*/
	
	public JSONArray selectWithUnionParser() throws Exception{
		JSONArray finaljsonarr=new JSONArray();
    	query=query.trim();
    	
    	JSONObject selectionobj=new JSONObject();	    	
    	selectionobj.put("selectionobject", selectParser());
    	
    	finaljsonarr.put(selectionobj);

		while(query.matches("^"+unionregex+ "(.*)")) {
			Pattern union_pattern=Pattern.compile("^"+unionregex+"(.*)");
			Matcher union_matcher=union_pattern.matcher(query);
			if (union_matcher.find()) {
				String operator=union_matcher.group(1);
				
				JSONObject unionobj=new JSONObject();
				unionobj.put("unionkey",operator);
				
				finaljsonarr.put(unionobj);
				query=query.replaceFirst("^"+unionregex, "");
			}
	    	JSONObject selectionobj1=new JSONObject();	    	
	    	selectionobj1.put("selectionobject", selectParser());
	    	finaljsonarr.put(selectionobj1);
	    	
			query=query.trim();
		}
		return finaljsonarr;
	}
	
	
	public JSONObject selectParser() throws Exception{
		System.out.println("query: "+query);
		JSONObject selecttree=new JSONObject();	
		if(query.matches(select_pattern + "(.*)")) {
			System.out.println("selection expression");
			query=query.replaceFirst(select_pattern, "");
			System.out.println(query);
			//aggregation projection
			JSONArray aggprojobj=new JSONArray();
			aggprojobj=aggregateProjection(aggprojobj);
			
			//extract columns
			JSONArray colsArray=projectionParser();
			//extract table conditions
			JSONArray tabsArray=tablejoinParser();
			//extract filter conditions if any
			JSONArray predicateArray=predicateParser();
			//extract agg conditions if any
			JSONArray aggobj=new JSONArray();
			JSONObject groupobj=aggregateParser("(?i)(GROUP BY)");
			if(!groupobj.isEmpty()) {
				aggobj.put(groupobj);
			}
			JSONObject orderobj=aggregateParser("(?i)(ORDER BY)");
			if(!orderobj.isEmpty()) {
				aggobj.put(orderobj);
			}
			selecttree.put("columnaggregate", aggprojobj);
			selecttree.put("columns", colsArray);
			selecttree.put("tables", tabsArray);
			selecttree.put("predicates", predicateArray);
			selecttree.put("aggregates", aggobj);
			System.out.println("Final remaining query: "+query);
			
		}else {
			throw new Exception("Not a search expression");
		}
		
		System.out.println(selecttree.toString());
		return selecttree;
	}
	
	public JSONObject parseTableentity() throws Exception {
		JSONObject entity_object=new JSONObject();
		entity_object.put("type", "table");
		
		JSONObject table_object=new JSONObject();
		Pattern tableentitypattern=Pattern.compile(tableentity_regex);
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
		query=query.replaceFirst(tableentity_regex, "");
		table_object.put("dbname", dbname);
		table_object.put("schemaname", schemaname);
		table_object.put("tablename", tablename);
		entity_object.put("meta", table_object);
		
		System.out.println("query-"+query);
		return entity_object;
	}
	
	public JSONObject tableParser() throws Exception {
		query=query.trim();
		JSONObject tabobj=new JSONObject();
		if(query.matches(tableentity_regex+ "(.*)")) {
			tabobj.put("type", "tableentity");
			tabobj.put("entity", parseTableentity());
		}else if(query.matches(arg_select_pattern+ "(.*)")) {
			//nested select
			boolean isParan=false;
			if(query.charAt(0)=='(') {
				query=query.substring(1, query.length());
				isParan=true;
			}
			tabobj.put("type", "selectentity");
			tabobj.put("entity", selectWithUnionParser());
			
			if(isParan && query.charAt(0)==')') {
				query=query.substring(1, query.length());
			}
		}else {
			throw new Exception("table nature could not be infered");
		}
		
		query=query.trim();
		if(query.matches("(?i)((WHERE[\\s]+)|(ON[\\s]+)|"+joinregex+")"+"(.*)")) {
			System.out.println("where clause started");
		}else {
			query=" "+query;
			System.out.println("Query after expression parsing: "+query);
			Pattern tabalias_pattern=Pattern.compile("^"+arg_colalias);
			Matcher alias_matcher=tabalias_pattern.matcher(query);
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
			query=query.replaceFirst(arg_colalias, "");
		}
		
		
		return tabobj;
	}
	
	public JSONArray tablejoinParser() throws Exception{
		query=query.trim();
		
		System.out.println("Table join parser- query : "+query);
		
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
		while(query.matches(joinregex+"(.*)")) {
			JSONObject joinblock=new JSONObject();
			JSONObject joinobj=new JSONObject();
			Pattern join_pattern=Pattern.compile("^"+joinregex+"(.*)");
			Matcher join_matcher=join_pattern.matcher(query);
			if (join_matcher.find()) {
				String operator=join_matcher.group(1);
				joinobj.put("joinkey",operator);
				query=query.replaceFirst(joinregex, "");
			}
			
			joinblock.put("KEY",joinobj);
			joinblock.put("FROM",tableParser());
			//conditional parsing
			
			query=query.trim();
			if(query.matches("(?i)(ON)"+"(.*)")) {
				query=query.replaceFirst("(?i)(ON)", "");
				query=query.trim();
				JSONArray conditionexpr_obj=new JSONArray();
				conditionexpr_obj=conditionParser(conditionexpr_obj);
				joinblock.put("ON",conditionexpr_obj);
			}
			selection_object.put(joinblock);
		}
		
		return selection_object;
	}
	
	
	public JSONArray predicateParser() throws Exception{
		query=query.trim();
		JSONArray conditionexpr_obj=new JSONArray();
		
		System.out.println("Inside predicate parser :"+query);
		if(query.matches("(?i)(WHERE)"+"(.*)")) {
			query=query.replaceFirst("(?i)(WHERE)", "");
			query=query.trim();
			conditionexpr_obj=conditionParser(conditionexpr_obj);
		}
		return conditionexpr_obj;
	}
	
	public JSONArray aggregateProjection(JSONArray aggproj_arrobj) {
		query=query.trim();
		JSONObject aggprojobj=new JSONObject();
		Pattern aggproj_pattern=Pattern.compile("^"+aggregateProjection);
		Matcher aggproj_matcher=aggproj_pattern.matcher(query);
		if(aggproj_matcher.find()) {
			query=query.replaceFirst("^"+aggregateProjection, "");
			String aggprojfunc=aggproj_matcher.group().trim();
			aggprojobj.put("aggproj", aggprojfunc);
			aggproj_arrobj.put(aggprojobj);
			return aggregateProjection(aggproj_arrobj);
		}else {
			return aggproj_arrobj;
		}
	}
	
	
	public JSONObject aggregateParser(String aggfuncregex) throws Exception{
		query=query.trim();
		
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
				agg_arr.put(expressionParser(agg_arrobj));
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
	
	public JSONArray projectionParser() throws Exception{
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
	
	
	public JSONObject expressionArgumentParser() throws Exception{
		JSONArray exprobj=new JSONArray();
		JSONObject columndef_object=new JSONObject();
		
		boolean openingbrac=false;
		if(query.startsWith("(")){
			System.out.println("replacing opening paran");
			query=query.replaceFirst("[(]", "");
			openingbrac=true;
		}
		
		exprobj=expressionParser(exprobj);
		
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
			
			if(query.matches(func_pattern + "(.*)")) {
				JSONObject dttype_obj=funcParser("nested");
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
			Pattern colalias_pattern=Pattern.compile("^"+arg_colalias);
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
			query=query.replaceFirst(arg_colalias, "");
		}
		return columndef_object;
	}
	
	public JSONObject keywordParser() throws Exception {
		query=query.trim();
		JSONObject obj_keyword=new JSONObject();
		Pattern key_pattern=Pattern.compile(keywordregex);
		Matcher key_matcher=key_pattern.matcher(query);
		if (key_matcher.find()) {
			String key=key_matcher.group();
			query=query.replaceFirst(keywordregex, "");
			obj_keyword.put("key", key);
			return obj_keyword;
		}else {
			throw new Exception("Keyword matching failed");
		}
	}
	
	
	public JSONArray expressionParser(JSONArray expr_object) throws Exception{
		query=query.trim();
		
		System.out.println("Query for expression parser:"+query);
		
		String operator_regex="[\\s]*("+arithmeticop_regex+"|"+bitwiseop_regex+")";		
		JSONObject operand_object=new JSONObject();
		if(query.matches(keywordregex+ "(.*)")) {
			System.out.println("expr: keyword");
			operand_object.put("type", "keyword");
			operand_object.put("def", keywordParser());
		}
		else if(query.matches(func_pattern + "(.*)")){
			System.out.println("expr: function");
			operand_object.put("type", "function");
			operand_object.put("def", funcParser("nested"));
		}
		else if(query.matches(expr_codeblock+ "(.*)")) {
			System.out.println("Matched nested expr block: "+query);
			query=query.replaceFirst("[(]", "");
			JSONArray childexprobj=new JSONArray();
			operand_object.put("type", "expression");
			operand_object.put("def", expressionParser(childexprobj));
			if(query.charAt(0)==')') {
				query=query.replaceFirst("[)]", "");
			}else {
				throw new Exception("Failed while parsing nested expression");
			}
		}
		else if(query.matches(casewhen_regex+"(.*)")) {
			System.out.println("expr: case when column");
			operand_object.put("type", "case_when");
			operand_object.put("def", caseWhenParser());
		}
		else if(query.matches(arg_select_pattern+"(.*)")) {
			System.out.println("expr: select column");
			//nested select
			boolean isParan=false;
			if(query.charAt(0)=='(') {
				query=query.substring(1, query.length());
				isParan=true;
			}
			operand_object.put("type", "selectentity");
			operand_object.put("def", selectWithUnionParser());
			
			if(isParan && query.charAt(0)==')') {
				query=query.substring(1, query.length());
			}
		}
		else if(query.matches(func_literalpattern +"(.*)")){
			System.out.println("expr: literal");
			operand_object.put("type", "literal");
			operand_object.put("def", literalfuncParser());
		}
		else if(query.matches(func_entitypattern +"(.*)")) {
			System.out.println("expr: entity column");
			operand_object.put("type", "entity");
			operand_object.put("def", entityfuncParser());
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
			return expressionParser(expr_object);
		}else {
			return expr_object;
		}
	}
	
	
	public JSONArray conditionParser(JSONArray conditionobj) throws Exception{
		query=query.trim();
		JSONArray exprobj=new JSONArray();		
		JSONObject condition_operandobject=new JSONObject();	
		condition_operandobject.put("expr_type", "operand");
		condition_operandobject.put("expression", expressionParser(exprobj));
		
		query=query.trim();
		
		
		JSONObject condition_operator_object=new JSONObject();
		Pattern oprtr_pattern=Pattern.compile("^"+comparisonop_regex+"(.*)");
		Matcher oprtr_matcher=oprtr_pattern.matcher(query);
		String operator="";
		if (oprtr_matcher.find()) {
			operator=oprtr_matcher.group(1);
			condition_operator_object.put("expr_type", "operator");
			condition_operator_object.put("operator",operator);
			query=query.replaceFirst(comparisonop_regex, "");
		}else {
			throw new Exception("Invalid conditional expression");
		}
		
		query=query.trim();
		
		JSONObject condition_operand2object=new JSONObject();
		//right operand parsing
		if(operator.equalsIgnoreCase("in")) {	
		   if(query.startsWith("(")) {
			   query=query.replaceFirst("[(]", "");
			   JSONArray arg_obj=new JSONArray();
			   
			   if(query.matches(arg_select_pattern+"(.*)")) {
					//nested select
					arg_obj.put(selectWithUnionParser());
				}else {
					   query=","+query;
					   char separating_character=',';
					   while(separating_character==',') {
						   query=query.replaceFirst("[,]", "");
						   query=query.trim();
						   System.out.println("Query to expression parser: "+query);
						   JSONArray exprlitobj=new JSONArray();
						   arg_obj.put(expressionParser(exprlitobj));
						   separating_character=query.charAt(0);
					   }
				}
			   
			   if(query.startsWith(")")) {
				   query=query.replaceFirst("[)]", "");
			   }else {
				   throw new Exception("like operator issue");
			   }
			   
			   condition_operand2object.put("type", "selectentity");
			   condition_operand2object.put("expr_type", "operand");
			   condition_operand2object.put("expression", arg_obj);
		   }else {
			   throw new Exception("in operator issue");
		   }
		}else {
			JSONArray expr1obj=new JSONArray();		
			condition_operand2object.put("expr_type", "operand");
			condition_operand2object.put("expression", expressionParser(expr1obj));
		}

		
		conditionobj.put(condition_operandobject);
		conditionobj.put(condition_operator_object);
		conditionobj.put(condition_operand2object);

		query=query.trim();
		
		   System.out.println("Query at this point: "+ query);

		   
		if(query.matches("^"+joiningop_regex+"(.*)")) {
			Pattern joiningoprtr_pattern=Pattern.compile(joiningop_regex);
			Matcher joiningoprtr_matcher=joiningoprtr_pattern.matcher(query);
			JSONObject joincondition_operator_object=new JSONObject();
					
			if (joiningoprtr_matcher.find()) {
				System.out.println("Inside joining matcher");
				String joining_operator=joiningoprtr_matcher.group(1);
				joincondition_operator_object.put("expr_type", "operator");
				joincondition_operator_object.put("operator",joining_operator);
				
				conditionobj.put(joincondition_operator_object);
				
				query=query.replaceFirst(joiningop_regex, "");
				return conditionParser(conditionobj);
			}else {
				return conditionobj;
			}
		}
		else {
			return conditionobj;
		}
	}
	
	
	public JSONObject caseWhenargumentParser() throws Exception{
		query=query.trim();
		JSONObject finalexpr=new JSONObject();
		finalexpr.put("expr", caseWhenParser());
		finalexpr.put("type", "case-when");

		System.out.println("Before alias matching "+query);
		
		Pattern colalias_pattern=Pattern.compile(arg_colalias);
		Matcher alias_matcher=colalias_pattern.matcher(query);
		if (alias_matcher.find()) {
			finalexpr.put("colalias", alias_matcher.group().replaceFirst("(?i)[\\s]*AS[\\s]*", ""));
		}else {
			throw new Exception("Alias Matching exception for function literal");
		}
		query=query.replaceFirst(arg_colalias, "");
		System.out.println("Final query: "+query);
		
		return finalexpr;
	}
	
	public JSONArray caseWhenParser() throws Exception {
		JSONArray casewhenexpr=new JSONArray();
		System.out.println("Begin query: "+query);
		if(query.matches(casewhen_regex+"(.*)")) {
			
			query=query.replaceFirst("(?i)case[\\s]+", "");
			System.out.println("After case query: "+query);

			while(query.matches(when_regex+"(.*)")) {
				JSONObject caseobj=new JSONObject();
				query=query.replaceFirst(when_regex, "");				
				System.out.println("After when query: "+query);
				JSONArray conditionexpr_obj=new JSONArray();
				conditionexpr_obj=conditionParser(conditionexpr_obj);
				System.out.println("After condition  query: "+query);
				caseobj.put("when", conditionexpr_obj);
				query=query.trim();
				if(query.matches("(?i)(then)[\\s]+"+"(.*)")){
					query=query.replaceFirst("(?i)(then)[\\s]+","");
				}
				
				System.out.println("After then  query: "+query);				
				JSONArray thenexpr_obj=new JSONArray();
				thenexpr_obj=expressionParser(thenexpr_obj);				
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
				elseexpr_obj=expressionParser(elseexpr_obj);
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
	
	
	
	public JSONArray castParser() throws Exception{
		JSONObject finalexpr=new JSONObject();
		query=query.replaceFirst("^(?i)(CAST[(])", "");
		JSONArray exprobj=new JSONArray();
		expressionParser(exprobj);
		finalexpr.put("expression", exprobj);
		
		System.out.println("Outside Expression Parser in cast:"+query);
		query=query.replaceFirst("^(?i)[\\s]*AS[\\s]+", "");
		
		if(query.matches(func_pattern + "(.*)")) {
			JSONObject dttype_obj=funcParser("nested");
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
	
	
	public JSONObject funcParser(String type) throws Exception{
		JSONObject column_object=new JSONObject();	
		Pattern expr_funcpattern=Pattern.compile(func_pattern);
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
		JSONArray argsArray=new JSONArray();
		char separatingchar=',';
		while(separatingchar==',') {
			JSONArray arg_object=new JSONArray();
			query=query.trim();
			arg_object=expressionParser(arg_object);
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
	
	
	public JSONObject literalfuncParser() throws Exception{
		JSONObject column_object=new JSONObject();	
		Pattern entitypattern=Pattern.compile(func_literalpattern);
		Matcher matcher=entitypattern.matcher(query);
		if (matcher.find()) {
				column_object.put("literalvalue", matcher.group());
		}else {
			throw new Exception("Not a expression literal");
		}
		query=query.replaceFirst(func_literalpattern, "");
		return column_object;
	}
	
	public JSONObject entityfuncParser() throws Exception{
		JSONObject column_object=new JSONObject();	
		Pattern entitypattern=Pattern.compile(func_entitypattern);
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
		query=query.replaceFirst(func_entitypattern, "");
		return column_object;
	}

}
