package testCases;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pageObjects.LoginPage;
import pageObjects.RatingZone;
import testBase.BaseClass;
import org.testng.annotations.Test;

public class AddLocationZoneTC extends BaseClass{
	
	@Test
	public void TestUPCLogin() {
	    logger.info("*********Login Test case started***********");
	    LoginPage lp = new LoginPage(driver);
	    try {
	        lp.Addusername("mary.gonsalves@6dtech.co.in");
	        lp.AddPassword("Admin@123");
	        lp.ClickLogin();
	        logger.info("*********Logged in and logged out*********");
	    } catch (Exception e) {
	        // Log full exception and fail the test including cause
	        logger.error("Login test failed with exception:", e);
	        Assert.fail("Login test failed: " + e.toString(), e);
	    } finally {
	        logger.info("******Finished Test Execution*********");
	    }
	}
	
	@Test(dependsOnMethods="testCases.UPCLogin.TestUPCLogin")
	public void TestAddLocationZone()
	{
		logger.info("********Rating Zone is going to be clicked****");
		RatingZone rz=new RatingZone(driver);
		rz.clickRating_zone();
		rz.clickLocation_zone();
	}
		

}
