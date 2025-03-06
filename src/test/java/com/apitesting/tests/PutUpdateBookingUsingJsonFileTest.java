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
import java.util.Map;

public class PutUpdateBookingUsingJsonFileTest {
    private int bookingId;
    private String tokenApiRequestBody;
    private String putApiRequestBody;


    //@BeforeMethod
   // public void setup() {
   //     RestAssured.baseURI ="https://restful-booker.herokuapp.com";
  //  }

    @Test
    public void createBookingTest() throws JsonProcessingException {
        try {
            String postApiRequestBody = FileUtils.readFileToString(new File(System.getProperty("user.dir") + "/src/test/resources/singlebooking.json"), StandardCharsets.UTF_8);
            tokenApiRequestBody = FileUtils.readFileToString(new File(System.getProperty("user.dir") + "/src/test/resources/tokendata.json"), StandardCharsets.UTF_8);
            putApiRequestBody = FileUtils.readFileToString(new File(System.getProperty("user.dir") + "/src/test/resources/updatebookingdata.json"), StandardCharsets.UTF_8);
            //1. SEND POST REQUEST
            Response response = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(postApiRequestBody)
                    .baseUri("https://restful-booker.herokuapp.com")

                    .when()
                    .post("/booking")
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .statusLine("HTTP/1.1 200 OK")
                    .extract()
                    .response();
            System.out.println("Create Response is");
            response.prettyPrint();
            //fetch the booking id
            //bookingId = JsonpathValidator.read(response,"bookingid");
            //an other way to above line
            bookingId = response.jsonPath().getInt("bookingid");
            System.out.printf("booking id: %d%n", +bookingId);



        //STEP 2: TOKEN GENERATION
        Response tokenResponse = RestAssured.given()
                      .contentType(ContentType.JSON)
                      .body(tokenApiRequestBody)
                      .baseUri("https://restful-booker.herokuapp.com/auth")
                .when()
                   .post()
                .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .response();
        String token = JsonpathValidator.read(tokenResponse,"token");

        //UPDATE THE REQUEST BY ID USING PUT CALL


        Response putresponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(putApiRequestBody)
                .header("cookie","token="+token)
                .baseUri("https://restful-booker.herokuapp.com/booking")
                .when()
                .put("/{bookingId}",bookingId)
                .then()
                .assertThat()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .extract()
                .response();
        System.out.println("Put Booking Response body data");
        putresponse.prettyPrint();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
