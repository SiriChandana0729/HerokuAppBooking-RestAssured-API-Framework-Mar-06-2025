package com.apitesting.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

public class GetBookingIdsTest {

    @BeforeMethod
    public void setup() {
        RestAssured.baseURI ="https://restful-booker.herokuapp.com";
    }

    @Test
    public void getAllBookingsTest() {

        Response response = RestAssured.given()
                      .contentType(ContentType.JSON)

                .when()
                   .get("/booking")
                .then()
                .assertThat()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .extract()
                .response();
        Assert.assertTrue(response.getBody().asString().contains("bookingid"));



    }

    @Test
    public void getAllBookingByIDTest() {

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)

                .when()
                .get("/booking/1")
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
