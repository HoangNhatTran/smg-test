import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class WikipediaTest {
    WebDriver driver;

    @BeforeSuite
    void setUp() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--start-maximized");
        driver = new ChromeDriver(chromeOptions);
        driver.get("https://en.wikipedia.org/wiki/Main_Page");
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
    }

    @DataProvider(name = "test1")
    public static Object[] primeNumbers() {
        String dataFile = "/testdata/searchData.csv";
        ArrayList<String> searchTerm = new ArrayList<>();
        try (InputStream inputStream = WikipediaTest.class.getResourceAsStream(dataFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                searchTerm.add(line);
            }
            return searchTerm.toArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(dataProvider = "test1")
    void searchPagesContainSpecificText(String searchTerm) {
        // Read data file


        // Enter the search input
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(By.name("search"))).sendKeys(searchTerm);
        //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        //element.sendKeys(searchTerm);


        // Find and assert "Search for pages containing <searchTerm>" option
        WebElement parentElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cdx-typeahead-search-menu-0")));
        ;
        WebElement searchBySpecificText = parentElement.findElement(By.cssSelector("li:last-child"));
        Assert.assertTrue(searchBySpecificText.getText().contains(searchTerm));

        // Click on the "Search for pages containing <searchTerm>" option
        searchBySpecificText.click();

        // Assert the URL and search field
        String encodedSearchTerm = URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);
        String expectedUrl = "https://en.wikipedia.org/w/index.php?fulltext=1&search=" + encodedSearchTerm + "&title=Special%3ASearch&ns0=1";
        Assert.assertEquals(driver.getCurrentUrl(), expectedUrl);
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id='firstHeading']")).getText(), "Search results");
        WebElement searchfield = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[title='Search Wikipedia']")));
        Assert.assertEquals(searchfield.getAttribute("value"), searchTerm);


    }


    @Test
    void testClearButton() {
        //input search text
        driver.findElement(By.name("search")).sendKeys("abc");
        //select "Search for pages containing <searchTerm>" option
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cdx-typeahead-search-menu-0")));
        WebElement parentElement = driver.findElement(By.cssSelector("#cdx-typeahead-search-menu-0"));
        WebElement searchBySpecificText = parentElement.findElement(By.cssSelector("li:last-child"));
        searchBySpecificText.click();
        //find and select clear button
        WebElement element1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchText")));
        driver.findElement(By.cssSelector("div#searchText span:last-child")).click();
        Assert.assertFalse(driver.findElement(By.cssSelector("div#searchText span:last-child")).isDisplayed());
        Assert.assertEquals(driver.findElement(By.cssSelector("input[title='Search Wikipedia']")).getAttribute("value"), "");
    }

    @AfterSuite
    void closeBrowser() {
        driver.quit();
    }
}