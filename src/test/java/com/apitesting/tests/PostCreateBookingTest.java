package com.apitesting.tests;

import com.apitesting.utils.JsonpathValidator;
import com.fasterxml.jackson.databind.util.JSONPObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class PostCreateBookingTest {
    int bookingId;

    @BeforeMethod
    public void setup() {
        RestAssured.baseURI ="https://restful-booker.herokuapp.com";
    }

    @Test
    public void createBookingTest() {
        //prepare the request body
        Map<String,Object>bookingData = new HashMap<>();
        Map<String,Object> bookingDates = new HashMap<>();
        bookingData.put("firstname","Siri");
        bookingData.put("lastname","Chandana");
        bookingData.put("totalprice",1500);
        bookingData.put("depositpaid",true);
        bookingData.put("additionalneeds","Breakfast");
        bookingData.put("bookingdates",bookingDates);
        bookingDates.put("checkin","2025-01-01");
        bookingDates.put("checkout","2026-01-01");


        Response response = RestAssured.given()
                      .contentType(ContentType.JSON)
                      .body(bookingData)
                      .baseUri("https://restful-booker.herokuapp.com/booking")
                .when()
                   .post()
                .then()
                .assertThat()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .body("booking.firstname", Matchers.equalTo("Siri"))
                .extract()
                .response();
        System.out.println("Create Response is");
        response.prettyPrint();
        //fetch the booking id
        bookingId = JsonpathValidator.read(response,"bookingid");
        System.out.println("Create response booking id:"+bookingId);
    }

    @Test
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



}
