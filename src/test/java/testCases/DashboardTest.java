package testCases;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;


import pageObjects.DashboardPage;
import testBase.BaseClass;
public class DashboardTest extends BaseClass{

@Test(dependsOnMethods="testCases.UPCLogin.TestUPCLogin")
public void ClickDashbord() {
    // assume 'driver' and 'wait' are available in the class (or create local ones)
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

    // preferred locator(s)
    By byId = By.id("menu_1085");
    By byLink = By.linkText("Dashboard");
    By byCss = By.cssSelector("a[page*='Dashboard']");

    WebElement menuElement = null;

    // Try id first, then fallbacks
    try {
        menuElement = wait.until(ExpectedConditions.presenceOfElementLocated(byId));
    } catch (Exception e1) {
        try {
            menuElement = wait.until(ExpectedConditions.presenceOfElementLocated(byLink));
        } catch (Exception e2) {
            try {
                menuElement = wait.until(ExpectedConditions.presenceOfElementLocated(byCss));
            } catch (Exception e3) {
                // Not found by any locator - throw informative error
                throw new RuntimeException("Dashboard element not present (checked id/linkText/css).");
            }
        }
    }

    // Wait until visible and clickable
    wait.until(ExpectedConditions.visibilityOf(menuElement));
    wait.until(ExpectedConditions.elementToBeClickable(menuElement));

    // Scroll into view to avoid overlays or footer covering it
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", menuElement);

    // Try a human-like click first (Actions), then fallback to JS click
    try {
        new Actions(driver).moveToElement(menuElement).pause(Duration.ofMillis(150)).click().perform();
    } catch (Exception ex) {
        // fallback to JS click if Actions fails
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuElement);
    }
}


}
