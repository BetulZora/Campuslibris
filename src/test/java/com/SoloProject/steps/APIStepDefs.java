package com.SoloProject.steps;

import com.SoloProject.utility.ConfigurationReader;
import com.SoloProject.utility.LibraryAPI_Util;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class APIStepDefs {

    RequestSpecification givenPart;
    Response response;
    ValidatableResponse vResp;
    String pathParameter="";
    String endPoint;


    /**
     * US 01 RELATED STEPS
     *
     */
    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String userType) {
        givenPart = given().log().uri()
               .header("x-library-token",LibraryAPI_Util.getToken(userType));
    }
    @Given("Accept header is {string}")
    public void accept_header_is(String contentType) {
        givenPart.accept(contentType);
    }

    @And("Path param is {string}")
    public void pathParamIs(String pathParam) {
        pathParameter = pathParam;
        givenPart.pathParam("id", pathParameter);
    }
    @When("I send GET request to {string} endpoint")
    public void i_send_get_request_to_endpoint(String endpoint) {
        response = givenPart.when().get(ConfigurationReader.getProperty("library.baseUri")+endpoint).prettyPeek();
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
        vResp.body(path, everyItem(notNullValue()));
    }



    @And("{string} field should be same with path param")
    public void fieldShouldBeSameWithPathParam(String pathParam) {
        vResp.body(pathParam, is(equalTo(pathParameter)));
    }

    @And("following fields should not be null")
    public void followingFieldsShouldNotBeNull(List<String> fields) {
        for (String field : fields) {
            vResp.body(field, is(notNullValue()));
        }
    }
}
