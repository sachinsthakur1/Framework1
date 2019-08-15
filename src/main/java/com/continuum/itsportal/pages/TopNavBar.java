package com.continuum.itsportal.pages;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.continuum.common.utils.PortalUtils;
import com.continuum.utils.Log;

/**
 * Handling top menu bar on proteo enterprise pages, navigating to relevant page by tab in
 * the menu bar at the top of the proteo enterprise pages.
 */
public class TopNavBar {

	private final WebDriver driver;
	public ElementLayer uielement;
	
	@FindBy(css = "ul.ulTopMainNavigation > #ctl00_ctl00_mnuSecurity")
	WebElement lnkSecurityTab;
	
	@FindBy(css = ".rmRootGroup.rmHorizontal .rmExpanded")
	WebElement menuDropdown;
	
	@FindBy(xpath = "//ul [@class = 'rmRootGroup rmHorizontal']/li/a [@class='rmLink rmRootLink rmSelected rmExpanded']/..//li[@class != 'rmItem rmSeparator']/a")
	List<WebElement> menuDropdownOptions;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_btnSubmitTop")
	WebElement btnAddOrder;
	
	@FindBy(css = ".buttonClassSmall[value='Search']")
	WebElement btnSearch;
	
	/**
	 * 
	 * Constructor class for TopNavBar Here we initializing the driver for page
	 * factory objects and specific wait time for Ajax element
	 * 
	 * @param driver
	 */
	public TopNavBar(WebDriver driver) {
		this.driver = driver;
		ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, PortalUtils.minElementWait);
		PageFactory.initElements(finder, this);
		uielement = new ElementLayer(driver);
	}
	
	public SecurityPage navigateToSecurityTab(boolean screenShot) {
		Log.event("Clicking on Security tab.");
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0)");		
		lnkSecurityTab.click();
		PortalUtils.waitForITSPortalPageLoad(driver);
		//PortalUtils.waitForElement(driver, menuDropdown, 1);
		Log.message("Navigated to 'Security' page", driver, screenShot);
		return new SecurityPage(driver).get();
	}
}
