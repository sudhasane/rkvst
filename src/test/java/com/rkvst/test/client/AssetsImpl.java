package com.rkvst.test.client;

import com.rkvst.test.utility.TestUtils;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.io.IOException;

import static com.rkvst.test.utility.TestConstants.ASSETS_PATH;
import static com.rkvst.test.utility.TestConstants.TEST_DATA_DIR;
import static com.rkvst.test.utility.TestUtils.parseJSONFile;

@Component
public class AssetsImpl implements Assets {


    @Value("${rkvst.baseuri}")
    private String rkvstHost;

    @Override
    public Response getAssets(String sessionToken, String host) throws JSONException, IOException {
        return TestUtils.requestSpec(sessionToken)
                .baseUri(host)
                .when()
                .get()
                .then()
                .extract().response();
    }


    @Override
    public Response createAsset(String sessionToken, String body) throws JSONException, IOException {
        JSONObject requestBody = parseJSONFile(TEST_DATA_DIR + "/payloads/assets.json");
        return TestUtils.requestSpec(sessionToken)
                .baseUri(rkvstHost + ASSETS_PATH)
                .body(body)
                .contentType("application/json")
                .when().post()
                .then()
                .extract().response();
    }

    @Override
    public void createAssetToValidateSchema(String sessionToken) throws JSONException, IOException {
        JSONObject requestBody = parseJSONFile(TEST_DATA_DIR + "/payloads/assets.json");
        TestUtils.requestSpec(sessionToken)
                .baseUri(rkvstHost + ASSETS_PATH)
                .body(requestBody.toString())
                .contentType("application/json")
                .when().post()
                .then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("AuthJsonSchema.json"));

    }

    @Override
    public void getAssetToValidateSchema(String sessionToken, String assetId) throws JSONException, IOException {
        TestUtils.requestSpec(sessionToken)
                .baseUri(rkvstHost + ASSETS_PATH + assetId)
                .contentType("application/json")
                .when()
                .get()
                .then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("newAsset.json"));

    }


}
