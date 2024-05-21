import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.skyscreamer.jsonassert.JSONAssert;


import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestingClass {
    //access to page contents
    GooglePage gp = new GooglePage();

    //helper method to click an element if displayed
    public void waitForElementAndClick(WebDriver driver, WebElement element) {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.visibilityOf(element));
        if (element.isDisplayed()) {
            element.click();
        }
    }

    @Test
    public void webTest() throws InterruptedException, IOException {
        //opening the driver, maximizing the window
        WebDriver wb = new ChromeDriver();
        wb.get("https://www.google.com");
        wb.manage().window().maximize();

        //discarding cookies popup
        waitForElementAndClick(wb, wb.findElement(gp.getDiscardButton()));

        //searching for automation
        wb.findElement(gp.getTextAreaElement()).sendKeys("automation");
        wb.findElement(gp.getTextAreaElement()).sendKeys(Keys.RETURN);

        //going to Wiki article
        waitForElementAndClick(wb, wb.findElement(gp.getWikiButton()));

        //first automatic process, both as a requirement and a specific date in Wiki article are not properly defined
        //there is no direct correlation with the requirement and the page contents

        File scrFile = ((TakesScreenshot)wb).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(scrFile, new File("c:\\tmp\\wiki_screenshot.png"));

        wb.close();
    }


    @Test
    public void apiTest() {
        String body = "{\n" +
                "  \"id\": 0,\n" +
                "  \"username\": \"usr1\",\n" +
                "  \"firstName\": \"Test\",\n" +
                "  \"lastName\": \"User\",\n" +
                "  \"email\": \"test@mail.com\",\n" +
                "  \"password\": \"Test123\",\n" +
                "  \"phone\": \"48312645978\",\n" +
                "  \"userStatus\": 0\n" +
                "}";

        String responseBody = "{\n" +
                "  \"id\": 9223372036854755752,\n" +
                "  \"username\": \"usr1\",\n" +
                "  \"firstName\": \"Test\",\n" +
                "  \"lastName\": \"User\",\n" +
                "  \"email\": \"test@mail.com\",\n" +
                "  \"password\": \"Test123\",\n" +
                "  \"phone\": \"48312645978\",\n" +
                "  \"userStatus\": 0\n" +
                "}";

        //testing if user is made
        given().body(body).header("Content-Type", "application/json").when().post("https://petstore.swagger.io/v2/user").then().statusCode(200);

        //looking for that specific user as actual state
        String actualResponseBody = when().get("https://petstore.swagger.io/v2/user/usr1").asString();

        JSONAssert.assertEquals(responseBody, actualResponseBody, true);
    };

    public Map<String, String> returnPetList(JsonPath inputJson){

        ArrayList<Long> idList = new ArrayList<>(inputJson.getList("id"));
        ArrayList<String> nameList = new ArrayList<>(inputJson.getList("name"));

        Map<String, String> soldPetsMap = new HashMap<>();

        if (idList.size() == nameList.size()) {
            for (int i = 0; i < idList.size(); i++) {
                soldPetsMap.put(String.valueOf(idList.get(i)), nameList.get(i));
            }
        } else {
            System.out.println("Lists have different sizes. Cannot create soldPetsMap.");
        }
        return soldPetsMap;
    }

    @Test
    public void petStatusTest(){
        JsonPath petList = when().get("https://petstore.swagger.io/v2/pet/findByStatus?status=sold").jsonPath();
        System.out.println(returnPetList(petList));
        new PetIteration(returnPetList(petList));

    }

    public class PetIteration {
        PetIteration(Map<String, String> inputMap){
            Map<String, Integer> petNamesOccurrences = new HashMap<>();
            List<String> petNames = new ArrayList<>(inputMap.values());

            for (String petName : petNames) {
                petNamesOccurrences.put(petName, petNamesOccurrences.getOrDefault(petName, 0) + 1);
            }

            System.out.println(petNamesOccurrences);
        }
    }



}
