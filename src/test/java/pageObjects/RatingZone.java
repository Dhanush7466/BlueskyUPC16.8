package pageObjects;

import java.io.File;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Robust RatingZone page object (clean & compile-ready).
 */
public class RatingZone extends BasePage {

    public RatingZone(WebDriver driver) {
        super(driver);
    }

    // parent menu - used to expand submenu
    @FindBy(xpath = "(//a[normalize-space()='Rating Zone'])[1]")
    private WebElement Rating_zone;

    // stable id-based locator for Location Zone (if present)
    private By locationZoneBy = By.id("menu_1113");

    // fallback text locator
    private By locationZoneByText = By.xpath("//a[normalize-space()='Location Zone']");

    // loader/overlay selectors (adjust if your app uses different classes)
    private By loaderBy = By.cssSelector("div.loader1.loader--style3");
    private By genericOverlay = By.cssSelector(".overlay, .loading, .spinner, .global-loader, .blocking-overlay");

    // ------------------- PUBLIC MAIN METHODS ------------------- //

    /**
     * Clicks Rating Zone parent (safe click). Public so your test can call it.
     */
    public void clickRating_zone() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        waitForPageReady();
        waitUntilLoaderInvisible(wait);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(Rating_zone));
            new Actions(driver).moveToElement(Rating_zone).pause(Duration.ofMillis(150)).click().perform();
        } catch (Exception e) {
            // fallback JS click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", Rating_zone);
        }

        sleep(300);
    }

    /**
     * Tries to reveal submenu and click Location Zone safely.
     */
    public void clickLocation_zone() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        waitForPageReady();
        waitUntilLoaderInvisible(wait);

        // 1) reveal submenu
        tryRevealSubmenu(wait);

        // 2) find element
        WebElement locationEl = findLocationElement(wait);

        if (locationEl == null) {
            dumpAnchorsInfo("Location Zone");
            saveScreenshot("LocationZone_not_found.png");
            throw new RuntimeException("Location Zone element not found in DOM (id/text).");
        }

        // 3) prepare & click
        waitUntilLoaderInvisible(wait);
        waitForPageReady();
        safeScrollIntoView(locationEl);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(locationEl));
        } catch (Exception ignored) {}

        // prefer Actions if topmost, else JS fallback
        if (isElementTopMost(locationEl)) {
            try {
                new Actions(driver).moveToElement(locationEl).pause(Duration.ofMillis(120)).click().perform();
                return;
            } catch (Exception e) {
                // continue to JS
            }
        }

        // JS click with retries
        retryJsClick(locationEl, 4, 300);
    }

    // ------------------- helper methods ------------------- //

    private void tryRevealSubmenu(WebDriverWait wait) {
        // try hover
        try {
            new Actions(driver).moveToElement(Rating_zone).pause(Duration.ofMillis(150)).perform();
            sleep(200);
        } catch (Exception ignored) {}

        // try click
        try {
            wait.until(ExpectedConditions.elementToBeClickable(Rating_zone));
            try { Rating_zone.click(); } catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", Rating_zone); }
            sleep(300);
        } catch (Exception e) {
            // ignore
        }

        // try parent li click
        try {
            WebElement parentLi = Rating_zone.findElement(By.xpath("./ancestor::li[1]"));
            if (parentLi != null) {
                try { new Actions(driver).moveToElement(parentLi).pause(Duration.ofMillis(100)).click().perform(); } catch (Exception ex) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", parentLi); }
                sleep(250);
            }
        } catch (Exception ignored) {}

        // final last resort - force show submenu via JS on parent li
        try {
            String script =
                "var a = arguments[0];" +
                "var li = a.closest('li');" +
                "if(li){ var u = li.querySelector('ul'); if(u) { u.style.display='block'; u.style.visibility='visible'; } }";
            ((JavascriptExecutor) driver).executeScript(script, Rating_zone);
            sleep(200);
        } catch (Exception ignored) {}
    }

    private WebElement findLocationElement(WebDriverWait wait) {
        // id presence (may be hidden)
        try {
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locationZoneBy));
            if (el != null) return el;
        } catch (Exception ignored) {}

        // visible by text
        try {
            WebElement elText = wait.until(ExpectedConditions.visibilityOfElementLocated(locationZoneByText));
            if (elText != null) return elText;
        } catch (Exception ignored) {}

        // last resort: JS query for anchor text (case-insensitive)
        try {
            Long count = (Long) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('a')).filter(n => n.textContent && n.textContent.trim().toLowerCase().includes('location zone')).length;");
            if (count != null && count > 0) {
                Object obj = ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('a')).find(n => n.textContent && n.textContent.trim().toLowerCase().includes('location zone'));");
                if (obj instanceof WebElement) return (WebElement) obj;
            }
        } catch (Exception ignored) {}

        return null;
    }

    private boolean isElementTopMost(WebElement el) {
        try {
            String script =
                "var el = arguments[0]; var r = el.getBoundingClientRect();" +
                "var cx = (r.left + r.right) / 2, cy = (r.top + r.bottom) / 2;" +
                "if (cx < 0 || cy < 0 || cx > window.innerWidth || cy > window.innerHeight) return false;" +
                "var top = document.elementFromPoint(cx, cy);" +
                "while(top){ if(top === el) return true; top = top.parentElement; } return false;";
            Object res = ((JavascriptExecutor) driver).executeScript(script, el);
            return Boolean.TRUE.equals(res);
        } catch (StaleElementReferenceException sere) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void waitUntilLoaderInvisible(WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderBy));
        } catch (Exception e) {
            try {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(genericOverlay));
            } catch (Exception e2) {
                sleep(300);
            }
        }
    }

    private void waitForPageReady() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            shortWait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        } catch (Exception ignored) {}
    }

    private void retryJsClick(WebElement el, int attempts, long pauseMs) {
        for (int i = 0; i < attempts; i++) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                return;
            } catch (Exception e) {
                sleep(pauseMs);
            }
        }
        throw new RuntimeException("Failed to click element after JS retries");
    }

    private void dumpAnchorsInfo(String containsText) {
        try {
            Object result = ((JavascriptExecutor) driver).executeScript(
                "var nodes = Array.from(document.querySelectorAll('a'));" +
                "var matches = nodes.filter(n => n.textContent && n.textContent.trim().toLowerCase().includes(arguments[0].toLowerCase()));" +
                "return matches.map(m => ({text: m.textContent.trim(), id: m.id || '', href: m.getAttribute('href')||'', outer: m.outerHTML}));",
                containsText);
            log("Anchor dump: " + String.valueOf(result));
        } catch (Exception e) {
            log("Failed to dump anchors: " + e.getMessage());
        }
    }

    private void saveScreenshot(String fileName) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(System.getProperty("user.dir") + File.separator + fileName);
            FileHandler.copy(src, dest);
            log("Saved screenshot: " + dest.getAbsolutePath());
        } catch (Exception e) {
            log("Failed to save screenshot: " + e.getMessage());
        }
    }

    private void log(String msg) {
        System.out.println("[RatingZone] " + msg);
    }

    private void safeScrollIntoView(WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", el);
            sleep(150);
        } catch (Exception ignored) {}
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
