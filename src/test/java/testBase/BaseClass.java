package testBase;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

public class BaseClass {
    public WebDriver driver;
    public Logger logger;
    public Properties p;

    @BeforeClass
    @Parameters({"os","browser"})
    public void Setup(String os, String br) throws IOException {
        FileReader file = new FileReader("./src/test/resources/config.properties");
        p = new Properties();
        p.load(file);

        logger = LogManager.getLogger(this.getClass());

        // Optional: use WebDriverManager to auto-manage chromedriver binary
        // WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless=new"); // only if you need headless
        // options.addArguments("--no-sandbox", "--disable-dev-shm-usage");

        switch (br.toLowerCase()) {
            case "chrome":
                driver = new ChromeDriver(options);
                break;
            case "firefox":
                // driver = new FirefoxDriver(); // add when needed
                throw new RuntimeException("Firefox not configured yet");
            default:
                throw new RuntimeException("Invalid Browser name: " + br);
        }

        // sensible timeouts
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30)); // page load
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));   // short implicit

        // navigate
        driver.get(p.getProperty("appurl"));
        driver.manage().window().maximize();
    }

    @AfterClass
    public void Exit() {
        if (driver != null) {
            driver.quit(); // close browser and chromedriver properly
        }
    }
}
