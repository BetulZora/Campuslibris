package com.SoloProject.steps;

import com.SoloProject.pages.BookPage;
import com.SoloProject.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UIStepDefs {

    LoginPage loginPage = new LoginPage();
    BookPage bookPage = new BookPage();

    @Given("I logged in Library UI as {string}")
    public void i_logged_in_library_ui_as(String user) {
        loginPage.login(user);

    }
    @Given("I navigate to {string} page")
    public void i_navigate_to_page(String page) {
        bookPage.navigateModule(page);
    }






}
