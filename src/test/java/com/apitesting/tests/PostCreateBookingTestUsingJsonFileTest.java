package com.apitesting.tests;

import com.apitesting.pojos.Booking;
import com.apitesting.pojos.BookingDates;
import com.apitesting.utils.JsonpathValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PostCreateBookingTestUsingJsonFileTest {
    int bookingId;

    @BeforeMethod
    public void setup() {
        RestAssured.baseURI ="https://restful-booker.herokuapp.com";
    }

    @Test(dataProvider = "getTestData")
    public void createBookingTest(Map<String,Object> bookingData) throws JsonProcessingException {

        if(bookingData==null || bookingData.isEmpty()) {
            throw new RuntimeException("Test data is null or empty");
        }
        //extract the fields
        String firstname = (String) bookingData.get("firstname");
        String lastname = (String) bookingData.get("lastname");
        String additionalneeds = (String) bookingData.get("additionalneeds");
        int totalprice = (int)bookingData.get("totalprice");
        boolean depositpaid = (boolean)bookingData.get("depositpaid");


        //extract booking dates
        Map<String,String>bookingDatesMap = (Map<String, String>) bookingData.get("bookingdates");
        String checkin = bookingDatesMap!=null ? bookingDatesMap.get("checkin"):null;
        String checkout = bookingDatesMap!=null ? bookingDatesMap.get("checkout"):null;

        //validate required fields
        if (firstname == null || lastname == null || checkin == null || checkout == null) {

            throw new RuntimeException("Missing required fields in the test data");
        }

        //log the extracted data
        System.out.printf("Firstname: %s, Lastname: %s, Total Price: %d, Deposit paid: %b \n",firstname,lastname,totalprice,depositpaid);
        System.out.printf("checkin Date: %s, checkout Date: %s\n",checkin,checkout);

        //prepare the request body
        BookingDates bookingdates = new BookingDates(checkin,checkout);
        Booking bookingRequest = new Booking(firstname,lastname,additionalneeds,totalprice,depositpaid,bookingdates);
        //serialize the java object request body into json
        ObjectMapper objectmapper = new ObjectMapper();
        String requestBody = objectmapper.writerWithDefaultPrettyPrinter().writeValueAsString(bookingRequest);
        //send POST Request
        Response response = RestAssured.given()
                      .contentType(ContentType.JSON)
                      .body(requestBody)

                .when()
                   .post("/booking")
                .then()
                .assertThat()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .body("booking.firstname", Matchers.equalTo(firstname))
                .extract()
                .response();
        System.out.println("Create Response is");
        response.prettyPrint();
        //fetch the booking id
        //bookingId = JsonpathValidator.read(response,"bookingid");
        //an other way to above line
        bookingId = response.jsonPath().getInt("bookingid");
        System.out.printf("booking id: %d%n",+bookingId);
    }

    @Test(dependsOnMethods = "createBookingTest")
    public void getBookingByIDTest() {

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .baseUri("https://restful-booker.herokuapp.com")
                .pathParam("bookingId",bookingId)
                .when()
                .get("/booking/{bookingId}")
                .then()
                .assertThat()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .extract()
                .response();
        System.out.println("Response body data");
        response.prettyPrint();

    }

    @DataProvider
    public Object[] getTestData() {
        try{
            String jsonData = FileUtils.readFileToString(new File(System.getProperty("user.dir")+"/src/test/resources/data.json"), StandardCharsets.UTF_8);
           JSONArray jsonArray =  JsonPath.read(jsonData,"$");
           return jsonArray.toArray();

        }catch(IOException e){
            throw new RuntimeException("Error reading test data from Json file");
        }
    }


}
