package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;


import java.time.Duration;

public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//input[@id='username']")
    private WebElement txt_username;

    @FindBy(xpath = "//input[@id='password']")
    private WebElement txt_password;

    @FindBy(xpath = "//input[@id='kc-login']")
    private WebElement clk_login;

    private WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

    public void Addusername(String u_name) {
        wait.until(ExpectedConditions.visibilityOf(txt_username));
        txt_username.clear();
        txt_username.sendKeys(u_name);
    }

    public void AddPassword(String u_password) {
        wait.until(ExpectedConditions.visibilityOf(txt_password));
        txt_password.clear();
        txt_password.sendKeys(u_password);
    }

    public void ClickLogin() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // wait for footer to move away
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class*='kc-info']")));
        } catch (Exception e) {
            // ignore if footer not found
        }

        // scroll
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", clk_login);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(clk_login)).click();
        } catch (Exception e) {
            // fallback to JS click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clk_login);
        }
    }

}
