package jsonPathUsage;

import java.io.IOException;

import org.json.JSONException;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class JsonPathUsageTogetDataFromResp{
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
		JsonPath jsP= JsonPath.from(responseJson.body().asString());
		System.out.println(jsP.get());
		System.out.println(jsP.getString("booking.firstname"));
		System.out.println(jsP.getObject("booking.bookingdates", BookingDates.class));
		System.out.println(jsP.getMap("booking.bookingdates"));
	}
}
