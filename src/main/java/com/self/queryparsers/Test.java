package com.self.queryparsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class Test {	

	
	public static void main(String[] args) {
		String teradatadatatypes="(ARRAY(?![\\w]+)|VARRAY(?![\\w]+)|BYTE(?![\\w]+)|VARBYTE(?![\\w]+)|BLOB(?![\\w]+)|CHAR[(][\\d]*[)](?![\\w]+)|VARCHAR(?![\\w]+)|CLOB(?![\\w]+)|AVRO(?![\\w]+)|DATE(?![\\w]+)|TIME(?![\\w]+)|TIMESTAMP(?![\\w]+)|INTERVAL YEAR(?![\\w]+)|INTERVAL YEAR TO MONTH(?![\\w]+)|INTERVAL MONTH(?![\\w]+)|INTERVAL DAY(?![\\w]+)|INTERVAL DAY TO HOUR(?![\\w]+)|INTERVAL DAY TO MINUTE(?![\\w]+)|INTERVAL DAY TO SECOND(?![\\w]+)|INTERVAL HOUR(?![\\w]+)|INTERVAL HOUR TO MINUTE(?![\\w]+)|INTERVAL HOUR TO SECOND(?![\\w]+)|INTERVAL MINUTE(?![\\w]+)|INTERVAL MINUTE TO SECOND(?![\\w]+)|INTERVAL SECOND(?![\\w]+)|JSON(?![\\w]+)|BYTEINT(?![\\w]+)|SMALLINT(?![\\w]+)|INTEGER(?![\\w]+)|BIGINT(?![\\w]+)|DECIMAL([(][^()]*[)])|NUMERIC(?![\\w]+)|NUMBER(?![\\w]+)|PERIOD(?![\\w]+)|XML(?![\\w]+))";
		String formatdatatyperegex="^(?i)[(]("+teradatadatatypes+"),"+"[\\s]*FORMAT[\\s]+([\'][^\']+[\'])"+"[\\s]*[)]";	
		String query="(DATE, FORMAT 'MM/DD/YYYY')";
        if(query.matches(formatdatatyperegex+"(.*)")) {
	        Pattern p=Pattern.compile(formatdatatyperegex);
			Matcher m=p.matcher(query);
			if(m.find()) {
				System.out.println(m.group(4));
				System.out.println(m.group(2));
			}
		}
		
	}

}
