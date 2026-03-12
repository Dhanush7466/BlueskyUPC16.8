package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DashboardPage extends BasePage{
	
	public DashboardPage(WebDriver driver)
	{
		super(driver);
	}
	
	@FindBy(xpath="//a[normalize-space()='Dashboard']")
	private WebElement Dashboard_screen;
	
	public void ClickDashbord()
	{
	
		Dashboard_screen.click();
	}

}
