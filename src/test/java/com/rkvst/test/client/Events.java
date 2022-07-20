package com.rkvst.test.client;

import io.restassured.response.Response;
import org.json.JSONException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public interface Events {

    Response createEvent(String sessionToken, String assetId, String requestBody) throws JSONException, IOException;
}
