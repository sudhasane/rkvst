package com.rkvst.test.functionalTests;

import com.rkvst.test.TestConfig;
import com.rkvst.test.client.Assets;
import com.rkvst.test.client.Events;
import com.rkvst.test.utility.TestConstants;
import com.rkvst.test.utility.TestData;
import com.rkvst.test.utility.TestUtils;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URLEncoder;

import static com.rkvst.test.utility.TestConstants.*;
import static com.rkvst.test.utility.TestUtils.parseJSONFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = TestConfig.class)
public class AssetsTest {

    @Value("${rkvst.baseuri}")
    private String rkvstHost;
    Assets assets;

    Events events;
    TestData testData;

    @Autowired
    public AssetsTest(Assets assets, TestData testData, Events events) {
        this.assets = assets;
        this.testData = testData;
        this.events = events;
    }


    @Test
    void givenUrl_whenJsonResponseConformsToSchema_thenCorrect() throws JSONException, IOException {
        testData.setAssetId(getAssetId());
        assets.getAssetToValidateSchema(TestUtils.getSessionToken(), "/" + testData.getAssetId());
    }

    @Test
    void verifyAssetCreation() throws JSONException, IOException {
        JSONObject requestBody = parseJSONFile(TEST_DATA_DIR + "/payloads/assets.json");
        Response response = assets.createAsset(TestUtils.getSessionToken(), requestBody.toString());
        assertEquals(HttpStatus.SC_OK, response.statusCode());
        assertEquals("PENDING", response.path("confirmation_status"));

    }

    @Test
    @Disabled
    //empty request body is giving 200 and creating asset
    void verifyAssetCreationWithEmptyRequestBody() throws JSONException, IOException {
        JSONObject requestBody = parseJSONFile(TEST_DATA_DIR + "/payloads/emptyassets.json");
        Response response = assets.createAsset(TestUtils.getSessionToken(), requestBody.toString());
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.statusCode());


    }

    @Test
    void verifyGetAssets() throws JSONException, IOException {
        Response response = assets.getAssets(TestUtils.getSessionToken(), rkvstHost + ASSETS_PATH);
        assertEquals(HttpStatus.SC_OK, response.statusCode());
        assertTrue(response.jsonPath().getList("assets").size() >= 0);


    }

    @Test
    void verifyGetAssetsWithOutSessionToken() throws JSONException, IOException {
        Response response = assets.getAssets("", rkvstHost + ASSETS_PATH);
        assertEquals(HttpStatus.SC_FORBIDDEN, response.statusCode());
        assertEquals(INVALID_TOKEN_MESSAGE, response.path("message"));
    }

    @Test
    void verifyGetAssetsWithInvalidSessionToken() throws JSONException, IOException {
        Response response = assets.getAssets(TestConstants.INVALID_SESSION_TOKEN, rkvstHost + ASSETS_PATH);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
    }

    @Test
    void verifyGetAssetsWithInvalidHostUrl() throws JSONException, IOException {
        Response response = assets.getAssets(TestUtils.getSessionToken(), rkvstHost + ASSETS_PATH + "ss");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.statusCode());
    }


    @Test
    void verifyGetAssetsWithAssetId() throws JSONException, IOException {
        testData.setAssetId(getAssetId());
        Response response = assets.getAssets(TestUtils.getSessionToken(), rkvstHost + "/" + testData.getAssetId());
        assertEquals(HttpStatus.SC_OK, response.statusCode());
    }


    @Test
    @Disabled
    void verifyGetPublicURIOfAsset() throws JSONException, IOException {
        Response assetResponse = assets.getAssets(TestUtils.getSessionToken(), rkvstHost + ASSETS_PATH);
        testData.setAssetId(assetResponse.path("assets[0].identity").toString());
        Response response = assets.getAssets(TestUtils.getSessionToken(), rkvstHost + "/" + testData.getAssetId() + URLEncoder.encode(PUBLIC_URI_PATH, "UTF-8"));
        if (assetResponse.path("assets[0].public").equals(false)) {
            assertEquals(HttpStatus.SC_NOT_FOUND, response.statusCode());
            assertEquals("asset requested is not public", response.path("message"));
        } else if (assetResponse.path("assets[0].public").equals(true)) {
            assertEquals(HttpStatus.SC_OK, response.statusCode());
        }

    }

    @Test
    void verifyGetAssetsWithInvalidAssetId() throws JSONException, IOException {
        Response response = assets.getAssets(TestUtils.getSessionToken(), rkvstHost + ASSETS_PATH + INVALID_ASSET_ID);
        assertEquals(HttpStatus.SC_NOT_FOUND, response.statusCode());
    }

    @Test
    @Disabled
    void verifyGetPublicURIOfEvent() throws JSONException, IOException {
        testData.setAssetId(getAssetId());
        Response response = assets.getAssets(TestUtils.getSessionToken(), rkvstHost + "/" + testData.getAssetId() + "/" + PUBLIC_URI_PATH);
        assertEquals(HttpStatus.SC_OK, response.statusCode());
    }

    @Test
    void verifyGetEvents() throws JSONException, IOException {
        testData.setAssetId(getAssetId());
        Response response = assets.getAssets(TestUtils.getSessionToken(), rkvstHost + "/" + testData.getAssetId() + EVENTS_PATH);
        assertEquals(HttpStatus.SC_OK, response.statusCode());
        assertEquals(testData.getAssetId(), response.path("events[0].asset_identity").toString());
    }

    @Test
    void verifyCreateEvent() throws JSONException, IOException {
        JSONObject eventsRequestBody = parseJSONFile(TEST_DATA_DIR + "/payloads/events.json");
        String assetIdentity = getAssetId();
        Response eventsResponse = events.createEvent(TestUtils.getSessionToken(), assetIdentity, eventsRequestBody.toString());
        assertEquals(HttpStatus.SC_OK, eventsResponse.statusCode());
        assertEquals(assetIdentity, eventsResponse.path(ASSET_IDENTITY).toString());
        assertAll("Event creation Assertions", () -> {
            assertEquals("RecordEvidence", eventsRequestBody.get(BEHAVIOUR));
            assertNotNull(eventsRequestBody.get(TIME_STAMP));
            assertEquals(eventsRequestBody.getJSONObject(EVENT_ATTRIBUTE).get(DISPLAY_TYPE), eventsResponse.path("event_attributes.arc_display_type"));
            assertEquals(eventsRequestBody.getJSONObject(PRINCIPAL_DECLARED).get(EMAIL), eventsResponse.path("principal_declared.email"));

        });

    }

    @Test
    void verifyCreateEventWithEmptyRequestBody() throws JSONException, IOException {
        testData.setAssetId(getAssetId());
        JSONObject requestBody = parseJSONFile(TEST_DATA_DIR + "/payloads/emptyevents.json");
        Response eventsResponse = events.createEvent(TestUtils.getSessionToken(), testData.getAssetId(), requestBody.toString());
        assertEquals(HttpStatus.SC_BAD_REQUEST, eventsResponse.statusCode());
        assertEquals(INVALID_JSON_MESSAGE, eventsResponse.path("error"));

    }

    @Test
    void verifyGetEventsWithInvalidAssetId() throws JSONException, IOException {
        Response response = assets.getAssets(TestUtils.getSessionToken(), rkvstHost + ASSETS_PATH + INVALID_ASSET_ID + EVENTS_PATH);
        assertEquals(HttpStatus.SC_NOT_FOUND, response.statusCode());
    }


    private String getAssetId() throws JSONException, IOException {
        Response assetResponse = assets.getAssets(TestUtils.getSessionToken(), rkvstHost + ASSETS_PATH);
        return assetResponse.path("assets[1].identity").toString();
    }

}
