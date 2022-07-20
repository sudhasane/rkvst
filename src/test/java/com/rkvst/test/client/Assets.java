package com.rkvst.test.client;

import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public interface Assets {
    Response getAssets(String sessionToken, String host) throws JSONException, IOException;

    Response createAsset(String sessionToken, String body) throws JSONException, IOException;

    void createAssetToValidateSchema(String sessionToken) throws JSONException, IOException;

    void getAssetToValidateSchema(String sessionToken, String assetId) throws JSONException, IOException;


}
