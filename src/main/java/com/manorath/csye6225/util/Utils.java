package com.manorath.csye6225.util;

import com.manorath.csye6225.model.User;
import net.minidev.json.JSONObject;

import java.util.Base64;

public class Utils {

    private final static String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$";

    public static String[] decode(String header) {
        assert header.substring(0, 6).equals("Basic");
        String basicAuthEncoded = header.substring(6);
        String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
        final String[] credentialValues = basicAuthAsString.split(":", 2);
        return  credentialValues;
    }

    // Create Json Object
    public static String toJsonString(User u) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.appendField("id",u.getId());
        jsonObject.appendField("email_address",u.getEmail());
        jsonObject.appendField("first_name",u.getFirstName());
        jsonObject.appendField("last_name",u.getLastName());
        jsonObject.appendField("account_created",u.getAccountCreated().toString());
        jsonObject.appendField("account_updated",u.getAccountUpdated().toString());

        return jsonObject.toJSONString();
    }

    public static String toJsonStringTest(User u) {
        JSONObject jsonObject = new JSONObject();


        jsonObject.appendField("email_address",u.getEmail());
        jsonObject.appendField("first_name",u.getFirstName());
        jsonObject.appendField("last_name",u.getLastName());
        jsonObject.appendField("password",u.getPassword());

        return jsonObject.toJSONString();
    }

    public static boolean checkPassword(String password) {
        return password.matches(PASSWORD_REGEX);
    }
}
