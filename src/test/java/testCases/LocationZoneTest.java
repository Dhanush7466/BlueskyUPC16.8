package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.LoginPage;
import testBase.BaseClass;

public class LocationZoneTest extends BaseClass{

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



	
}
