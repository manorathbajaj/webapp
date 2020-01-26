package com.manorath.csye6225.controllers;

import com.manorath.csye6225.controller.UserController;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserTest {

    @Autowired
    UserController userController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() {
        assertThat(userController).isNotNull();
    }
    
    @Test
    public void checkGoodRequest() {
        given().auth().preemptive().basic("bajajsss.m@husky.neu.edu","Manorath96!").
                when().
                        get("http://localhost:8080/v1/user/self")
                .then()
                .assertThat()
                .statusCode(500);
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
