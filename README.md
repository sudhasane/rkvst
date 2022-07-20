To automate APIs, I used the Java8, Rest assured library, Jint5 and Gradle as build tool.

To run the test ./gradlew clean test

Reports can be found under $buildDir/reports/FunctionalIntegrationTest/index.html

#Following scenarios were automated
create event with valid session token to validate that the authorised access can create a record
Get all Assets and validated schema to verify retrieved data is in accordance with given schema
Get assets with correct and incorrect asset Id
Get assets with Invalid session token to validate any unauthorised access can retrieve data
Get events for valid and invalid assetId
Create event with valid asset id
Get public url of both assets and event

#Some observations
Providing incorrect path params to get assets giving incorrect error like JWT is not valid 
Empty request body for create asset is successful, some error might be thrown instead
In API document request body mandatory parameters were not mentioned
Post request is returning 200 instead of 201
Get asset with empty spaces giving "invalid GetAssetRequest.Uuid: value must be a valid UUID | caused by: invalid uuid format" ,  error message can be more relevant "provide correct assetID"
In Events Post request body "Safety Rating" is giving Invalid json expression due to spaces in between words of key name

#Problems
An unsupported encoding exception is thrown when automating get Publicurl for assets and events
Create event is not always successful for an existing asset, few times received error like task is not enabled for a given asset
