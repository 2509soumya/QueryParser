package com.self.queryparsers.refactor.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.self.queryparsers.refactor.SelectionParser;

public class ConditionParser{

	public JSONArray parse(JSONArray conditionobj) throws Exception {
		String query=SelectionParser.query;
		
		SelectionParser sel_parser=new SelectionParser();
		
			query=query.trim();
			JSONArray exprobj=new JSONArray();		
			JSONObject condition_operandobject=new JSONObject();	
			condition_operandobject.put("expr_type", "operand");
			
			ExpressionParser parser=new ExpressionParser();
			
			condition_operandobject.put("expression", parser.parse(exprobj));
			query=query.trim();			
			
			JSONObject condition_operator_object=new JSONObject();
			Pattern oprtr_pattern=Pattern.compile("^"+ParserRegex.comparisonop_regex+"(.*)");
			Matcher oprtr_matcher=oprtr_pattern.matcher(query);
			String operator="";
			if (oprtr_matcher.find()) {
				operator=oprtr_matcher.group(1);
				condition_operator_object.put("expr_type", "operator");
				condition_operator_object.put("operator",operator);
				query=query.replaceFirst(ParserRegex.comparisonop_regex, "");
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
				   
				   if(query.matches(ParserRegex.arg_select_pattern+"(.*)")) {
						//nested select
						arg_obj.put(sel_parser.parse());
					}else {
						   query=","+query;
						   char separating_character=',';
						   while(separating_character==',') {
							   query=query.replaceFirst("[,]", "");
							   query=query.trim();
							   System.out.println("Query to expression parser: "+query);
							   JSONArray exprlitobj=new JSONArray();
							   arg_obj.put(parser.parse(exprlitobj));
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
				condition_operand2object.put("expression", parser.parse(expr1obj));
			}

			
			conditionobj.put(condition_operandobject);
			conditionobj.put(condition_operator_object);
			conditionobj.put(condition_operand2object);

			query=query.trim();
			
			   System.out.println("Query at this point: "+ query);

			   
			if(query.matches("^"+ParserRegex.joiningop_regex+"(.*)")) {
				Pattern joiningoprtr_pattern=Pattern.compile(ParserRegex.joiningop_regex);
				Matcher joiningoprtr_matcher=joiningoprtr_pattern.matcher(query);
				JSONObject joincondition_operator_object=new JSONObject();
						
				if (joiningoprtr_matcher.find()) {
					System.out.println("Inside joining matcher");
					String joining_operator=joiningoprtr_matcher.group(1);
					joincondition_operator_object.put("expr_type", "operator");
					joincondition_operator_object.put("operator",joining_operator);
					
					conditionobj.put(joincondition_operator_object);
					
					query=query.replaceFirst(ParserRegex.joiningop_regex, "");
					return parse(conditionobj);
				}else {
					return conditionobj;
				}
			}
			else {
				return conditionobj;
			}
		}
	
}
