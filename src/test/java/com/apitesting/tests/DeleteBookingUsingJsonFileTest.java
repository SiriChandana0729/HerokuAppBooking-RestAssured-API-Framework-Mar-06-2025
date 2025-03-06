package com.apitesting.tests;

import com.apitesting.utils.JsonpathValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DeleteBookingUsingJsonFileTest {
    private int bookingId;




    //@BeforeMethod
   // public void setup() {
   //     RestAssured.baseURI ="https://restful-booker.herokuapp.com";
  //  }

    @Test
    public void createDeleteBookingTest() throws JsonProcessingException {
        try {
            String postApiRequestBody = FileUtils.readFileToString(new File(System.getProperty("user.dir") + "/src/test/resources/singlebooking.json"), StandardCharsets.UTF_8);
            String tokenApiRequestBody = FileUtils.readFileToString(new File(System.getProperty("user.dir") + "/src/test/resources/tokendata.json"), StandardCharsets.UTF_8);
            String patchApiRequestBody = FileUtils.readFileToString(new File(System.getProperty("user.dir") + "/src/test/resources/patchTestData.json"), StandardCharsets.UTF_8);
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

        //STEP 3: Delete Booking


        Response deleteresponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Content-Type","application/json")
                .header("Content-Type","application/json")
                .header("cookie","token="+token)
                .header("Authorization","Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .baseUri("https://restful-booker.herokuapp.com/booking")
                .when()
                .delete("/{bookingId}",bookingId)
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .response();
        System.out.println("Delete Booking Response body data");
            deleteresponse.prettyPrint();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
