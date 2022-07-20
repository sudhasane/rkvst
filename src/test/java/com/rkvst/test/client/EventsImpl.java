package com.rkvst.test.client;

import com.rkvst.test.utility.TestUtils;
import io.restassured.response.Response;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;

import static com.rkvst.test.utility.TestConstants.EVENTS_PATH;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EventsImpl implements Events {

    @Value("${rkvst.baseuri}")
    private String rkvstHost;

    @Override
    public Response createEvent(String sessionToken, String assetId, String requestBody) throws JSONException, IOException {
        return TestUtils.requestSpec(sessionToken)
                .baseUri(rkvstHost + "/" + assetId + EVENTS_PATH)
                .body(requestBody)
                .contentType("application/json")
                .when().post()
                .then()
                .extract().response();
    }

}
