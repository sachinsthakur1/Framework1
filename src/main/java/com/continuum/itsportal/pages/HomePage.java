package com.continuum.itsportal.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.testng.Assert;

import com.continuum.common.utils.PortalUtils;
import com.continuum.utils.Log;

/**
 * HomePage consists ITSPortal component like Dashboard, Ticket, Report, Security
 * 
 */
public class HomePage extends LoadableComponent<HomePage> {

	private final WebDriver driver;
	public TopNavBar topnav;
	public ElementLayer uielement;
	private boolean isPageLoaded;
	public String createClasstitle = "Create classes";
	public String createAssignmenttitle = "Create assignments";
	public final String noNBCrssFeeds = "Hmm, looks like we couldn't get the latest news feed. Try again later.";

	@FindBy(css = ".masterpagelite_headerLogoImg")
	WebElement imgHome;
	
	
	/**
	 * constructor of the class
	 * 
	 * @param driver
	 */
	public HomePage(WebDriver driver) {
		this.driver = driver;
		ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, PortalUtils.minElementWait);
		PageFactory.initElements(finder, this);
	}

	@Override
	protected void isLoaded() {
		if (!isPageLoaded) {
			Assert.fail();
		}
		
		
		String curl = driver.getCurrentUrl().toLowerCase();
		if (isPageLoaded && curl.contains("Setup")) {
			Log.fail("ITSPortal Login Page did not open up. Site might be down.", driver);
		}

		// Reinitialize globalNav on each load
		topnav = new TopNavBar(driver);
		uielement = new ElementLayer(driver);
	}

	@Override
	protected void load() {

		isPageLoaded = true;
		PortalUtils.waitForITSPortalPageLoad(driver);
	}
}