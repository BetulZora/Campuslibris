package com.SoloProject.pages;

import com.SoloProject.utility.BrowserUtil;
import com.SoloProject.utility.DB_Util;
import com.SoloProject.utility.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookPage extends BasePage {

    @FindBy(xpath = "//table/tbody/tr")
    public List<WebElement> allRows;

    @FindBy(xpath = "//input[@type='search']")
    public WebElement search;

    @FindBy(id = "book_categories")
    public WebElement mainCategoryElement;

    @FindBy(name = "name")
    public WebElement bookName;


    @FindBy(xpath = "(//input[@type='text'])[4]")
    public WebElement author;

    @FindBy(xpath = "//div[@class='portlet-title']//a")
    public WebElement addBook;

    @FindBy(xpath = "//button[@type='submit']")
    public WebElement saveChanges;

    @FindBy(xpath = "//div[@class='toast-message']")
    public WebElement toastMessage;

    @FindBy(name = "year")
    public WebElement year;

    @FindBy(name = "isbn")
    public WebElement isbn;

    @FindBy(id = "book_group_id")
    public WebElement categoryDropdown;


    @FindBy(id = "description")
    public WebElement description;



    public WebElement editBook(String book) {
        String xpath = "//td[3][.='" + book + "']/../td/a";
        return Driver.getDriver().findElement(By.xpath(xpath));
    }

    public WebElement borrowBook(String book) {
        String xpath = "//td[3][.='" + book + "']/../td/a";
        return Driver.getDriver().findElement(By.xpath(xpath));
    }

    public Map<String, String> getUIMap(String bookName){


        search.sendKeys(bookName+ Keys.ENTER);
        BrowserUtil.waitFor(2);
        List<WebElement> listText = new ArrayList<>();
        listText.addAll(Driver.getDriver().findElements(By.xpath("//tbody/tr/td")));
        System.out.println("bookelems.get(0) = " + listText.get(0).getText());
        listText.remove(listText.size()-1);
        listText.remove(0);


        String locator = "//th[@aria-controls='tbl_books']";
        List<WebElement> listHeader = new ArrayList<>();
        listHeader.addAll(Driver.getDriver().findElements(By.xpath(locator)));
        System.out.println("Header elems.get(0) = " + listHeader.get(0).getText());
        listHeader.remove(listHeader.size() - 1);



        Map<String, String> uIMap = new HashMap<>();

        for (int i = 0; i < listHeader.size(); i++) {

            uIMap.put(listHeader.get(i).getText(), listText.get(i).getText());
            System.out.println("added Key "+listHeader.get(i).getText());
            System.out.println("added Value "+listText.get(i).getText());


        }
        System.out.println("uIMap = " + uIMap);
        return uIMap;



    }



}
