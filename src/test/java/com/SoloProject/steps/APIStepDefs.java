package com.SoloProject.steps;

import com.SoloProject.pages.BookPage;
import com.SoloProject.pages.LoginPage;
import com.SoloProject.utility.*;
import com.github.javafaker.Job;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.internal.common.assertion.Assertion;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class APIStepDefs {

    RequestSpecification givenPart;
    Response response;
    ValidatableResponse vResp;
    String pathParameter="";
    BookPage bookPage;
    String acceptContentType;


    /**
     * US 01 RELATED STEPS
     *
     */
    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String userType) {
        givenPart = given().log().all()
               .header("x-library-token",LibraryAPI_Util.getToken(userType));
    }
    String feature5token;
    @Given("I logged Library api with credentials {string} and {string}")
    public void i_logged_library_api_with_credentials_and(String email, String password) {

        feature5token = LibraryAPI_Util.getToken(email, password);
        givenPart = given().log().all()
                .header("x-library-token",feature5token);

    }
    @Given("Accept header is {string}")
    public void accept_header_is(String contentType) {
        givenPart.accept(contentType);
        acceptContentType = contentType;
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
        givenPart.body(("token="+feature5token));
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



    @Then("UI, Database and API created book information must match")
    public void ui_database_and_api_created_book_information_must_match() {

        // -------------------------- compare UI to API-----------------------------
        bookPage = new BookPage();
        String book_id = response.jsonPath().getString("book_id");

        Response resp = given().log().all()
                .header("x-library-token",LibraryAPI_Util.getToken("librarian"))
                .accept(acceptContentType)
                .pathParam("id",book_id)
                .when().get(ConfigurationReader.getProperty("library.baseUri")+"/get_book_by_id/{id}").prettyPeek();


        String bookName=resp.jsonPath().getString("name");
        System.out.println("bookName = " + bookName);
        Map<String, String> uiDetails = bookPage.getUIMap(bookName);

        ValidatableResponse toConfirm = resp.then();

        toConfirm.body("isbn", is(uiDetails.get("ISBN")));
        toConfirm.body("name", is(uiDetails.get("Name")));
        toConfirm.body("author", is(uiDetails.get("Author")));
        toConfirm.body("year", is(uiDetails.get("Year")));

        //book_category_id=18
        JsonPath jp = toConfirm.extract().jsonPath();
        int bookCategoryNum = jp.getInt("book_category_id");
        System.out.println("bookCategoryNum = " + bookCategoryNum);



        String bookCategoryAPI;
        DB_Util.runQuery("select name from book_categories where id="+bookCategoryNum);
        bookCategoryAPI = DB_Util.getFirstRowFirstColumn();
        System.out.println("bookCategoryAPI = " + bookCategoryAPI);

        assertEquals(bookCategoryAPI,uiDetails.get("Category"));

        // -------------------------- compare DB to API-----------------------------
        String Generalquery = "select name, isbn, author, year from books " +
                "where name = '"+bookName+"' order by id desc";
        DB_Util.runQuery(Generalquery);
        Map<String,Object> dBMap = DB_Util.getRowMap(1);
        System.out.println("dBMap = " + dBMap);

        String categoriesQuery = "select name from book_categories where id = (select book_category_id from books " +
                "where name = '"+bookName+"' order by id desc)";
        DB_Util.runQuery(categoriesQuery);
        Map<String,Object> catMap = DB_Util.getRowMap(1);
        System.out.println("catMap = " + catMap);

        toConfirm.body("isbn", is(dBMap.get("isbn")));
        toConfirm.body("name", is(dBMap.get("name")));
        toConfirm.body("author", is(dBMap.get("author")));
        toConfirm.body("year", is(dBMap.get("year")));

        assertEquals(bookCategoryAPI,catMap.get("name"));

    }

    @Then("created user information should match with Database")
    public void created_user_information_should_match_with_database() {
        JsonPath jp = vResp.extract().jsonPath();
        int newUserIDvResp = jp.getInt("user_id");

        //"user_id": "8211"
        String query = "select id, " +
                "full_name, " +
                "email, " +
                "password, " +
                "user_group_id, " +
                "status, " +
                "start_date, " +
                "end_date, " +
                "address " +
                "from users " +
                "where id="+newUserIDvResp+";";

        System.out.println("query = " + query);
        DB_Util.runQuery(query);

        System.out.println("-------------------------------------------newUserDBMap");
        Map<String,Object> newUserDBMap = DB_Util.getAllRowAsListOfMap().get(0);
        System.out.println("newUserDBMap = " + newUserDBMap);

        String newUserIDString = newUserIDvResp+"";
        System.out.println("-------------------------------------------JP");
        JsonPath jp2 = given().log().all()
                .header("x-library-token",LibraryAPI_Util.getToken("librarian"))
                .accept(acceptContentType)
                .pathParam("id",newUserIDvResp)
                .when().get(ConfigurationReader.getProperty("library.baseUri")+"/get_user_by_id/{id}").prettyPeek()
                .then()
                .body("id", equalTo(newUserIDString))
                .body("full_name", is(newUserDBMap.get("full_name")))
                .body("email", is(newUserDBMap.get("email")))
                .body("password", is(newUserDBMap.get("password")))
                .body("user_group_id", is(newUserDBMap.get("user_group_id")))
                .body("status", is(newUserDBMap.get("status")))
                .body("start_date", is(newUserDBMap.get("start_date")))
                .body("end_date", is(newUserDBMap.get("end_date")))
                .body("address", is(newUserDBMap.get("address")))
                .extract().jsonPath();
        System.out.println("-------------------------------------------Setting");

        newUserPassword="libraryUser";
        newUserEmail = (String) newUserDBMap.get("email");
        System.out.println("newUserDBMap.get(\"email\") = " + newUserDBMap.get("email"));
        newUserName = (String) newUserDBMap.get("full_name");

        System.out.println("newUserDBMap.get(\"full_name\") = " + newUserDBMap.get("full_name"));

    }

    public String newUserPassword;
    public String newUserEmail;
    public String newUserName;


    @Then("created user should be able to login Library UI")
    public void created_user_should_be_able_to_login_library_ui() {
        bookPage = new BookPage();
        LoginPage loginPage = new LoginPage();
        System.out.println("email blah = " + newUserEmail);
        System.out.println("password blah= " + newUserPassword);

        loginPage.login(newUserEmail,newUserPassword);
        BrowserUtil.waitFor(2);
        assertEquals(true,Driver.getDriver().getCurrentUrl().contains("#dashboard"));
    }

    @Then("created user name should appear in Dashboard Page")
    public void created_user_name_should_appear_in_dashboard_page() {
        bookPage = new BookPage();
        String UIname = bookPage.accountHolderName.getText();
        String createdName = newUserName;
        System.out.println("UIname = " + UIname);
        System.out.println("createdName = " + createdName);
        assertEquals(UIname,createdName);


    }





}
