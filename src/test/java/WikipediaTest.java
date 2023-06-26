import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import org.monte.media.FormatKeys;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.monte.media.Format;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class WikipediaTest {
    WebDriver driver;
    ScreenRecorder recorder;
    @BeforeSuite
    void setUp() throws IOException, AWTException {
        GraphicsConfiguration gconfig = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        recorder = new ScreenRecorder(gconfig,
                new Format(MediaTypeKey, FormatKeys.MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, FormatKeys.MediaType.VIDEO, EncodingKey,
                        ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                        QualityKey, 1.0f,
                        KeyFrameIntervalKey, 15 * 60),
                new Format(MediaTypeKey, FormatKeys.MediaType.VIDEO,
                        EncodingKey,"black", FrameRateKey, Rational.valueOf(30)), null);
        recorder.start();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--start-maximized");
        driver = new ChromeDriver(chromeOptions);
        driver.get("https://en.wikipedia.org/wiki/Main_Page");
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
    }

    @DataProvider(name = "test1")
    public static Object[] searchTerm() {
        String dataFile = "/testdata/searchData.csv";
        ArrayList<String> testData = new ArrayList<>();
        try (InputStream inputStream = WikipediaTest.class.getResourceAsStream(dataFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                testData.add(line);
            }
            return testData.toArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(dataProvider = "test1")
    void searchPagesContainSpecificText(String searchTerm) {

        // Enter the search input
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.ignoring(StaleElementReferenceException.class)
                .until((WebDriver d) -> {
                    d.findElement(By.name("search")).sendKeys(searchTerm);
                    return true;
                });


        // Find and assert "Search for pages containing <searchTerm>" option
        wait.ignoring(StaleElementReferenceException.class)
                .until((WebDriver d) -> {
                    d.findElement(By.id("cdx-typeahead-search-menu-0"));
                    return true;
                });
        WebElement parentElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cdx-typeahead-search-menu-0")));
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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.ignoring(StaleElementReferenceException.class)
                .until((WebDriver d) -> {
                    d.findElement(By.name("search")).sendKeys("abc");
                    return true;
                });
        //select "Search for pages containing <searchTerm>" option
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cdx-typeahead-search-menu-0")));
        WebElement parentElement = driver.findElement(By.cssSelector("#cdx-typeahead-search-menu-0"));
        WebElement searchBySpecificText = parentElement.findElement(By.cssSelector("li:last-child"));
        searchBySpecificText.click();
        //find and select clear button
        WebElement clearButton= wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div#searchText span:last-child")));
        clearButton.click();
        // assert that cleat button and input text is cleared
        Assert.assertFalse(clearButton.isDisplayed());
        Assert.assertEquals(driver.findElement(By.cssSelector("input[title='Search Wikipedia']")).getAttribute("value"), "");
    }

    @AfterSuite
    void closeBrowser() throws IOException {
        driver.quit();
        recorder.stop();
    }
}
