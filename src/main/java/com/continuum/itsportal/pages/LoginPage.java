package com.continuum.itsportal.pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.continuum.common.utils.PortalUtils;
import com.continuum.utils.Log;
import com.continuum.utils.StopWatch;

/**
 * Login page consists login as a partner with the their credentials
 * (username, password) If partner credentials are correct then they
 * can navigate the portal homepage.
 *
 */
public class LoginPage extends LoadableComponent<LoginPage> {

	private final WebDriver driver;
	private String itsPortalUrl;
	private boolean isPageLoaded;
	
	public final String loginErrorMessage = "The username or password is incorrect. Try again.";

	@FindBy(css = "#idToken1")
	WebElement txtUserName;

	@FindBy(css = "#idToken2")
	WebElement txtPassWord;

	@FindBy(css = "#loginButton_0")
	WebElement btnSignIn;
	

	/**
	 * Constructor class for Login page Here we initializing the driver for page
	 * factory objects. For ajax element waiting time has added while
	 * initialization
	 * 
	 * @param driver
	 * @param url
	 */
	public LoginPage(WebDriver driver, String url) {

		this.driver = driver;
		itsPortalUrl = url;
		PageFactory.initElements(driver, this);
	}

	@Override
	protected void isLoaded() {

		if (!isPageLoaded) {
			Assert.fail();
		}
        
		if (isPageLoaded && !driver.getCurrentUrl().toLowerCase().contains("login")) {
			Log.fail("ITSPortal Login Page did not open up. Site might be down.", driver);
		}
	}

	@Override
	protected void load() {

		isPageLoaded = true;
		driver.get(itsPortalUrl);
		PortalUtils.waitForITSPortalPageLoad(driver);
	}

	/**
	 * Login to ITSPortal
	 * 
	 * @param username
	 *            as string
	 * @param password
	 *            as string
	 * @param screenShot 
	 *        to capture screenShot 
	 * @return HomePage if enter the correct credential for Portal
	 */
	public HomePage loginToITSPortal(String username, String password, boolean screenShot) {
		Log.message("Launched ITSPortal site:: " + itsPortalUrl);
		Log.event("Login to the ITSPortal");
		enterUserName(username);
		enterPassword(password);		
		clickBtnSignIn();
		PortalUtils.waitForITSPortalPageLoad(driver);
		Log.message("Logged into ITS Portal as (" + username + "/" + password + ")", driver, screenShot);		
		return new HomePage(driver).get();
	}

	/**
	 * Enter user name
	 * 
	 * @param userName as string
	 */
	public void enterUserName(String userName) {
		(new WebDriverWait(driver, 30).pollingEvery(200, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class, StaleElementReferenceException.class).withMessage("Unable to find username text box")).until(ExpectedConditions.visibilityOf(txtUserName));
		txtUserName.clear();
		txtUserName.sendKeys(userName);
		Log.event("Entered the UserName: " + userName);
	}

	/**
	 * Enter password
	 * 
	 * @param pwd as string
	 */
	public void enterPassword(String pwd) {
		(new WebDriverWait(driver, 30).pollingEvery(200, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class, StaleElementReferenceException.class).withMessage("Unable to find password text box")).until(ExpectedConditions.visibilityOf(txtPassWord));
		txtPassWord.clear();
		txtPassWord.sendKeys(pwd);
		Log.event("Entered the Password: " + pwd);

	}

	/**
	 * Click signIn button on login page
	 * 
	 */
	public void clickBtnSignIn() {

		final long startTime = StopWatch.startTime();
		(new WebDriverWait(driver, 30).pollingEvery(200, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class, StaleElementReferenceException.class).withMessage("Unable to click signIn button on login page")).until(ExpectedConditions.elementToBeClickable(btnSignIn)).click();
		PortalUtils.waitForITSPortalPageLoad(driver);
		Log.event("Clicked signIn button on login page", StopWatch.elapsedTime(startTime));
	}
}