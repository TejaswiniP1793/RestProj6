package getAllValuesUsingJsonNode;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
public class GetAllValuesFromNestedJsonUsingJsonNode {
	@Test
	public void getAllValuesFromNestedJSON() throws JsonProcessingException, IOException, JSONException {
		ObjectMapper objectMapper = new ObjectMapper();
		
		// Creating Node that maps to JSON Object structures in JSON content
		ObjectNode bookingDetails = objectMapper.createObjectNode();
		
		// It is similar to map put method. put method is overloaded to accept different types of data
		// String as field value
		bookingDetails.put("firstname", "Jim");
		bookingDetails.put("lastname", "Brown");
		// integer as field value
		bookingDetails.put("totalprice", 111);
		// boolean as field value
		bookingDetails.put("depositpaid", true);
		bookingDetails.put("additionalneeds", "Breakfast");
		// Duplicate field name. Will override value
		bookingDetails.put("additionalneeds", "Lunch");
		
		// Since requirement is to create a nested JSON Object
		ObjectNode bookingDateDetails = objectMapper.createObjectNode();
		bookingDateDetails.put("checkin", "2021-07-01");
		bookingDateDetails.put("checkout", "2021-07-01");
		
		// Since 2.4 , put(String fieldName, JsonNode value) is deprecated. So use either set(String fieldName, JsonNode value) or replace(String fieldName, JsonNode value)
		bookingDetails.set("bookingdates", bookingDateDetails);
		
		
		//GIVEN
		Response responseJson=RestAssured
		   .given().headers("someHeader","SomeValue")
			  .baseUri("https://restful-booker.herokuapp.com/booking")
			  .contentType(ContentType.JSON)
			  // Pass JSON pay load directly
			  .body(bookingDetails)
			  .log()
			  .all()
		// WHEN
		   .when()
			   .post();
		Headers headers=responseJson.getHeaders();
		System.out.println("========================================");
		for(Header h:headers) {
			System.out.print(h.getName()+": ");
			System.out.println(h.getValue());
		}
		System.out.println("========================================");
		JSONObject jsonObj=new JSONObject(responseJson.asString());
		JsonNode jsonTree = objectMapper.readTree(jsonObj.toString());
		System.out.println(jsonObj.toString());
		System.out.println("----------------------------------------");
		System.out.println(jsonTree.get("booking").get("firstname").asText());
		System.out.println(jsonTree.path("booking").path("totalprice").asText());
		System.out.println(jsonTree.at("/bookingid"));
		Iterator<String> it= jsonTree.fieldNames();
		
		while(it.hasNext()) {
			String key=it.next();
			Object value = jsonTree.get(key);
			if(value instanceof TextNode) {
				System.out.print(key+":");
				System.out.println(jsonTree.get(key));
			}
			else if(value instanceof ObjectNode) {
				System.out.println(key+":");
				Iterator<String> it1  = ((ObjectNode) value).fieldNames();
				while(it1.hasNext()) {
					
					String key1=it1.next();
					System.out.print(key1+":");
					System.out.println(((ObjectNode) value).get(key1));
				}
			}
		}
		System.out.println("===========================================");
		//JSON schema validation of response when schema in src/test/resources
		//responseJson.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema.json"));
		 //  JSON schema validation of response when schema not in src/test/resources
		responseJson.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(new File("C:\\Users\\Stellar User\\Documents\\Automation\\WorkSpace\\RestProj6\\schema.json")));
	}
}
