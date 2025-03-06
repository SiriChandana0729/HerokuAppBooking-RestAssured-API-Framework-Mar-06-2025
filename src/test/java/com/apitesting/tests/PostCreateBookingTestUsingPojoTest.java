package com.apitesting.tests;

import com.apitesting.pojos.Booking;
import com.apitesting.pojos.BookingDates;
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

public class PostCreateBookingTestUsingPojoTest {
    int bookingId;

    @BeforeMethod
    public void setup() {
        RestAssured.baseURI ="https://restful-booker.herokuapp.com";
    }

    @Test
    public void createBookingTest() throws JsonProcessingException {
        //prepare the request body
        BookingDates bookingdates = new BookingDates("2025-01-01","2026-01-01");
        Booking bookingRequest = new Booking("Radha","Gundreddi","Dinner",20000,true,bookingdates);
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
                .body("booking.firstname", Matchers.equalTo("Radha"))
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




}
