package com.manorath.csye6225.controllers;

import com.manorath.csye6225.controller.UserController;
import com.manorath.csye6225.model.Bill;
import com.manorath.csye6225.model.PaymentStatus;
import com.manorath.csye6225.model.User;
import com.manorath.csye6225.repository.BillRepository;
import com.manorath.csye6225.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import javax.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class Tests {

    @Autowired
    UserController userController;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BillRepository billRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() {
        assertThat(userController).isNotNull();
    }

    @Test
    public void createUserTest() {
        User u = new User("abc@xyz.com","Manorath96!","abc","efg");
        u.setId("123");
        User x =userRepository.save(u);
        assertThat(u.getFirstName()).isEqualTo(x.getFirstName());
        userRepository.deleteById("123");
    }

    @Test
    public void createBill() {
        Bill b = new Bill();
        b.setCreatedTs(new Date());
        b.setOwnerID("123");
        b.setUpdatedTs(new Date());
        b.setBillDate(new Date());
        b.setAmountDue(new BigDecimal(123.2));
        b.setDueDate(new Date());
        b.setPaymentStatus(PaymentStatus.no_payment_required);
        List l = new ArrayList<String>();
        l.add("test");
        b.setCategories(l);
        b.setVendor("test");

        Bill x = billRepository.save(b);
        assertThat(x.getOwnerID()).isEqualTo(b.getOwnerID());
        billRepository.delete(x);
    }
/*

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
 */
}
