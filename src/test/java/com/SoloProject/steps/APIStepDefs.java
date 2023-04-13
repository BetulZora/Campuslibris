package com.SoloProject.steps;

import com.SoloProject.utility.ConfigurationReader;
import com.SoloProject.utility.LibraryAPI_Util;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class APIStepDefs {

    RequestSpecification givenPart;
    Response response;
    ValidatableResponse vResp;
    String pathParameter="";


    /**
     * US 01 RELATED STEPS
     *
     */
    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String userType) {
        givenPart = given().log().all()
               .header("x-library-token",LibraryAPI_Util.getToken(userType));
    }
    @Given("I logged Library api with credentials {string} and {string}")
    public void i_logged_library_api_with_credentials_and(String string, String string2) {
        /**TODO: I have to write this
         *
         */
    }
    @Given("Accept header is {string}")
    public void accept_header_is(String contentType) {
        givenPart.accept(contentType);
    }

    @Given("Request Content Type header is {string}")
    public void request_content_type_header_is(String contentTypeHeader) {
        givenPart.contentType(contentTypeHeader);

    }

    @Given("Path param is {string}")
    public void pathParamIs(String pathParam) {
        pathParameter = pathParam;
        givenPart.pathParam("id", pathParameter);
    }

    @Given("I send token information as request body")
    public void i_send_token_information_as_request_body() {
        /**TODO: I have to write this
         *
         */
    }

    @Given("I create a random {string} as request body")
    public void i_create_a_random_as_request_body(String bookOrUser) {
        Map<String,Object> map = new LinkedHashMap<>();

        if(bookOrUser.equals("book")){
            map = LibraryAPI_Util.getRandomBookMap();
        } else if(bookOrUser.equals("user")){
            map = LibraryAPI_Util.getRandomUserMap();
        } else {
            System.out.println("#####################################\n" +
                    "WRONG TYPE CHOSEN CHOOSE BOOK OR USER");
        }

        givenPart.formParams(map);
        System.out.println("map created");


    }
    @When("I send GET request to {string} endpoint")
    public void i_send_get_request_to_endpoint(String endpoint) {
        response = givenPart.when().get(ConfigurationReader.getProperty("library.baseUri")+endpoint).prettyPeek();
        vResp = response.then();
    }
    @When("I send POST request to {string} endpoint")
    public void i_send_post_request_to_endpoint(String endPoint) {
        response = givenPart.when().post(ConfigurationReader.getProperty("library.baseUri")+endPoint).prettyPeek();
        vResp = response.then();

    }
    @Then("status code should be {int}")
    public void status_code_should_be(Integer statusCode) {
        vResp.statusCode(statusCode);
    }
    @Then("Response Content type is {string}")
    public void response_content_type_is(String contentType) {
        vResp.contentType(contentType);
    }
    @Then("{string} field should not be null")
    public void field_should_not_be_null(String path) {
        vResp.body(path, is(notNullValue()));
    }



    @Then("{string} field should be same with path param")
    public void fieldShouldBeSameWithPathParam(String pathParam) {
        vResp.body(pathParam, is(equalTo(pathParameter)));
    }

    @Then("following fields should not be null")
    public void followingFieldsShouldNotBeNull(List<String> fields) {
        for (String field : fields) {
            vResp.body(field, is(notNullValue()));
        }
    }

    @Then("the field value for {string} path should be equal to {string}")
    public void the_field_value_for_path_should_be_equal_to(String key, String value) {
        vResp.body(key,is(equalTo(value)));

    }









}
