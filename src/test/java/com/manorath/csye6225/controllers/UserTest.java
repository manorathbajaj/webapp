package com.manorath.csye6225.controllers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class UserTest {

    @Test
    public void checkGoodRequest() {
        given().auth().preemptive().basic("bajaj.m@husky.neu.edu","Manorath96!").
                when().
                        get("http://localhost:8080/v1/user/self")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void postCheckBadRequest() {

            RestAssured.baseURI = "http://localhost:8080";
            String data = "{\n" +
                "\t\"email\":\"bajaj.m@husky.neu.edu\",\n" +
                "\t\"firstName\": \"Manorath\",\n" +
                "\t\"lastName\" : \"Bajaj\",\n" +
                "\t\"password\" : \"Manorath96!\"\n" +
                "}";

        Response r = given()
                .contentType("application/json").
                        body(data).
                        when().
                        post("/v1/user");
        r.then().statusCode(400);
    }
}
