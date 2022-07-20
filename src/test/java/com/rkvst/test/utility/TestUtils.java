package com.rkvst.test.utility;

import io.restassured.RestAssured;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class TestUtils {

    public static JSONObject parseJSONFile(String filename) throws JSONException, IOException {

        String conFileName = new File(filename).getCanonicalPath();
        String content = new String(Files.readAllBytes(Paths.get(conFileName)));
        return new JSONObject(content);
    }


    public static JSONObject parseJsonFile(String filename) {
       JSONObject requestData = null;
        try {
            JSONParser parser = new JSONParser();
             requestData = (JSONObject) parser.parse(
                    new FileReader(filename));


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return requestData;
    }

    public static RequestSpecification requestSpec(String sessionToken) {
        RequestSpecification request = RestAssured.given()
                .header("Authorization", sessionToken);
        RestAssured.useRelaxedHTTPSValidation();
        return request.filters(new ErrorLoggingFilter());
    }

    public static String getSessionToken() throws JSONException, IOException {
        File tokenDir = new File("src/test/resources/");
        JSONObject requestBody = parseJSONFile(tokenDir + "/token/sessionToken.json");
        return requestBody.get("sessionToken").toString();
    }
}
