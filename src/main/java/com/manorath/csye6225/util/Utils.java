package com.manorath.csye6225.util;

import com.amazonaws.services.s3.AmazonS3;
import com.manorath.csye6225.model.Bill;
import com.manorath.csye6225.model.User;
import net.minidev.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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


    public static String getMD5(byte[] data) throws NoSuchAlgorithmException
    {
        MessageDigest messageDigest=MessageDigest.getInstance("MD5");

        byte[] digest = messageDigest.digest(data);

        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {

            sb.append(Integer.toHexString((int) (b & 0xff)));

        }
        return sb.toString();
    }
    // get due dates
    public static int getDiff(Date date) {
        Date current = new Date();
        long millisecondDiff = date.getTime() - current.getTime();
        if(millisecondDiff > 0)
            return (int)TimeUnit.DAYS.convert(millisecondDiff,TimeUnit.MILLISECONDS);
        else
            return Integer.MAX_VALUE;
    }
}
