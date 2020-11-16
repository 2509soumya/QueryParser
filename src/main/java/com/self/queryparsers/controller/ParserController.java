package com.self.queryparsers.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.self.queryparsers.QueryParser;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping(value = "teradataparser/", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParserController {

	
	@RequestMapping(value = "parse", method = RequestMethod.POST)
	public ResponseEntity<String>  generateQuery(@RequestBody String query){
	  System.out.println("Formulating parsed json ..");
	  QueryParser parser=new QueryParser(query);
	  JSONObject response=parser.parse();
	  return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
}
