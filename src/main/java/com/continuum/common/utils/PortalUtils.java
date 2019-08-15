package com.continuum.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.continuum.utils.DataUtils;
import com.continuum.utils.EnvironmentPropertiesReader;
import com.continuum.utils.Log;
import com.continuum.utils.MobileEmulationUserAgentConfiguration;
import com.continuum.utils.StopWatch;
import com.continuum.utils.Utils;
import com.continuum.utils.WebDriverFactory;

/**
 * RealizeUtils class is used in all realize page classes and methods - Utilize
 * this page method for reusable purpose and avoid number of lines in the page
 * object waitforRealizePageLoad()
 *
 */
public class PortalUtils {

	// wait for realize spinner load, till progressDialog present, wait till
	// sideBar open, wait will modelDialogBox open, till success message
	// present..
	// static String cssSpinner =
	// "div[id*='Loading'][class='']>i[class*='icon-spinner'], [class*='loading'],[class*='Loading']:not([class*='hide']),[id*='Loading']:not([class*='hide']), li[class*='ng-animate'],div[id='progressDialog'],div:not([class*='withSidebarOpen']) ~ *[show-sidebar],[id='progressing'][aria-hidden='false']";
	static String cssSpinner = "div[id*='Loading'][class='']>i[class*='icon-spinner'], [class*='loading'],[class*='Loading']:not([class*='hide']),[id*='Loading']:not([class*='hide']), li[class*='ng-animate'],div[class='success-message'],[class*='success']:not([class*='hide']),div[id='progressDialog'],div:not([class*='withSidebarOpen']) ~ *[show-sidebar],[id='progressing'][aria-hidden='false']";

	private static By allSpinners = By.cssSelector(cssSpinner);
	private static EnvironmentPropertiesReader configProperty = EnvironmentPropertiesReader.getInstance();
	static AtomicBoolean lock = new AtomicBoolean(false);
	public static ExpectedCondition<Boolean> realizeLoad;
	public static int maxElementWait = 3;
	public static int minElementWait = 2;

	public static By errorMsg = By.cssSelector("div[class*='alert']:not([class*='hide']) div[class*='message-container']");
	public static By closeErrorMsg = By.cssSelector("div[class*='alert']:not([class*='hide']) a[class*='close']>i");
	public static By goBack = By.cssSelector("a[ng-click*='back'], a[ng-click*='Back']");

	static {

		realizeLoad = new ExpectedCondition<Boolean>() {
			public final Boolean apply(final WebDriver driver) {
				List<WebElement> spinners = driver.findElements(allSpinners);
				for (WebElement spinner : spinners) {
					try {
						if (spinner.isDisplayed()) {
							return false;
						}
					} catch (NoSuchElementException e) {
						e.printStackTrace();
					}
				}
				// To wait click events to trigger
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				spinners = driver.findElements(allSpinners);
				for (WebElement spinner : spinners) {
					try {
						if (spinner.isDisplayed()) {
							return false;
						}
					} catch (NoSuchElementException e) {
						e.printStackTrace();
					}
				}
				return true;
			}
		};

		maxElementWait = configProperty.getProperty("maxElementWait") != null ? Integer.valueOf(configProperty.getProperty("maxElementWait")) : maxElementWait;
		minElementWait = configProperty.getProperty("minElementWait") != null ? Integer.valueOf(configProperty.getProperty("minElementWait")) : minElementWait;
	}

	/**
	 * To wait for realize page load with global load wait time It will check
	 * all page elements getting load ex: spinners, ajax load, DOM element,
	 * frames load, dialog box and side bar
	 * 
	 * @param driver
	 */
	public static void waitForITSPortalPageLoad(final WebDriver driver) {
		
		String s="";
        while(!s.equals("complete")){
        s=(String) ((JavascriptExecutor) driver).executeScript("return document.readyState");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        }
		//waitForRealizePageLoad(driver, WebDriverFactory.maxPageLoadWait);
	}

	/**
	 * To wait for realize page load with global load wait time It will check
	 * all page elements getting load ex: spinners, ajax load, DOM element,
	 * frames load, dialog box and side bar
	 * 
	 * @param driver
	 * @param maxWait
	 */
	public static void waitForRealizePageLoad(final WebDriver driver, int maxWait) {
		long startTime = StopWatch.startTime();
		FluentWait<WebDriver> wait = new WebDriverWait(driver, maxWait).pollingEvery(500, TimeUnit.MILLISECONDS).ignoring(StaleElementReferenceException.class).withMessage("Page Load Timed Out");
		try {
			Utils.waitForPageLoad(driver, maxWait);
			wait.until(realizeLoad);
		} catch (TimeoutException e) {
			Utils.waitForPageLoad(driver, maxWait);
			wait.until(realizeLoad);
		}
		Log.event("Realize Page Load Wait: (Sync)", StopWatch.elapsedTime(startTime));

	} // waitForRealizePageLoad

	/**
	 * This function is to validate a error message on alert
	 * 
	 * @param driver
	 * @param errMsg
	 * @return boolean return true if error message as expected else return
	 *         false
	 * @throws Exception
	 */
	public static boolean validateErrorMsg(WebDriver driver, String errMsg) throws Exception {
		boolean status = false;
		final long startTime = StopWatch.startTime();
		try {
			if (waitForElement(driver, driver.findElement(errorMsg))) {
				if (driver.findElement(errorMsg).getText().contains(errMsg)) {
					driver.findElement(closeErrorMsg).click();
					status = true;
					Log.event("Error message is displayed:: " + errMsg, StopWatch.elapsedTime(startTime));
				} else {
					status = false;
				}
			}
		} catch (Exception e) {
			throw new Exception("Unable to find alert Message", e);
		}
		return status;
	}

	/**
	 * If alert message displayed this method will close it that
	 * 
	 * @param driver
	 * @throws Exception
	 */
	public static void closeAlertMsg(WebDriver driver) throws Exception {
		try {
			if (waitForElement(driver, driver.findElement(closeErrorMsg))) {
				driver.findElement(closeErrorMsg).click();
			}

		} catch (Exception e) {
			throw new Exception("Unable to close alert message", e);
		}

	}

	/**
	 * Click Go Back button
	 * 
	 * @param driver
	 * @throws Exception
	 */
	public static void goBack(WebDriver driver) throws Exception {
		try {
            Log.event("Clicking on Go Back button");
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0)");
			if (waitForElement(driver, driver.findElement(goBack))) {
				driver.findElement(goBack).click();
				Log.message("Clicked on Go Back button");
				PortalUtils.waitForITSPortalPageLoad(driver);
			}
		} catch (Exception e) {
			throw new Exception("Unable to click back button on the page navigator", e);
		}
	}

	/**
	 * To get matching text element from List of web elements
	 * 
	 * @param elements
	 * @param contenttext
	 * @return elementToBeReturned as WebElement
	 * @throws Exception
	 */
	public static WebElement getMachingTextElementFromList(List<WebElement> elements, String contenttext) throws Exception {
		WebElement elementToBeReturned = null;
		boolean found = false;
		if (elements.size() > 0) {
			for (WebElement element : elements) {
				if (element.getText().trim().replaceAll("\\s+", " ").equals(contenttext)) {
					elementToBeReturned = element;
					found = true;
					break;
				}
			}
			if (!found) {
				throw new Exception("Didn't find the correct text(" + contenttext + ")..! on the page");
			}
		} else {
			throw new Exception("Unable to find list element...!");
		}
		return elementToBeReturned;
	}

	/**
	 * To wait for the specific element on the page
	 * 
	 * @param driver
	 * @param element
	 * @return boolean - return true if element is present else return false
	 */
	public static boolean waitForElement(WebDriver driver, WebElement element) {
		return waitForElement(driver, element, maxElementWait);
	}

	/**
	 * To wait for the specific element on the page
	 * 
	 * @param driver
	 * @param element
	 * @return boolean - return true if element is present else return false
	 */
	public static boolean waitForElement(WebDriver driver, WebElement element, int maxWait) {
		boolean statusOfElementToBeReturned = false;
		long startTime = StopWatch.startTime();
		WebDriverWait wait = new WebDriverWait(driver, maxWait);
		try {
			WebElement waitElement = wait.until(ExpectedConditions.visibilityOf(element));
			if (waitElement.isDisplayed() && waitElement.isEnabled()) {
				statusOfElementToBeReturned = true;
				Log.event("Element is displayed:: " + element.toString());
			}
		} catch (Exception e) {
			statusOfElementToBeReturned = false;
			Log.event("Unable to find a element after " + StopWatch.elapsedTime(startTime) + " sec ==> " + element.toString());
		}
		return statusOfElementToBeReturned;
	}

	/**
	 * Wait until element disappears in the page
	 * 
	 * @param driver
	 *            driver instance
	 * @param element
	 *            webelement to be disaapear
	 * @return true if element is not appearing in the page
	 */
	public static boolean waitUntilElementDisappear(WebDriver driver, final WebElement element) {
		final boolean isNotDisplayed;

		WebDriverWait wait = (WebDriverWait) new WebDriverWait(driver, WebDriverFactory.maxPageLoadWait);
		isNotDisplayed = wait.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver webDriver) {
				boolean isPresent = false;
				try {
					if (element.isDisplayed()) {
						isPresent = false;
						Log.event("Element " + element.toString() + ", is still visible in page");
					}
				} catch (Exception e) {
					isPresent = true;
					Log.event("Element " + element.toString() + ", is not displayed in page ");
					return isPresent;
				}
				return isPresent;
			}
		});
		return isNotDisplayed;
	}

	/**
	 * Waits for element to visible
	 * 
	 * @param driver
	 * @param element
	 * @param timeSeconds
	 * @throws Exception
	 */
	public static void waitForElementVisible(final WebDriver driver, WebElement element, int timeSeconds) throws Exception {
		try {

			WebDriverWait wait = new WebDriverWait(driver, timeSeconds);
			wait.until(ExpectedConditions.visibilityOf(element));

		} catch (TimeoutException te) {
			Log.exception(te);
		}
	}

	/**
	 * To scroll into particular element
	 * 
	 * @param driver
	 * @param element
	 */
	public static void scrollIntoView(final WebDriver driver, WebElement element) {
		try {
			// ((JavascriptExecutor)
			// driver).executeScript("window.scrollTo(5,250)");
			// waitForRealizePageLoad(driver);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
			waitForITSPortalPageLoad(driver);
		} catch (Exception e) {
			Log.event("Moved to element..");
		}
	}

	/**
	 * To get/pick the credentials depends on platform and browsers, depends on
	 * that picking data from excel using the test case id and sheet name
	 * 
	 * @param testcaseId
	 * @param sheetName
	 * @param driver
	 * @return
	 */
	public static HashMap<String, String> getTestData(String testcaseId, String sheetName, final WebDriver driver) {
		HashMap<String, String> dataToBeReturned = null;
		HashMap<String, String> credentials = null;

		// get the os and browser details(name and version)
		String browser = ((RemoteWebDriver) driver).getCapabilities().getBrowserName();
		String version = ((RemoteWebDriver) driver).getCapabilities().getVersion().split("\\.")[0];
		Platform platform = ((RemoteWebDriver) driver).getCapabilities().getPlatform();
		int osVersion = platform.getMajorVersion();
		String osDetails = platform.toString() + String.valueOf(osVersion);
		osDetails = osDetails.replaceAll(" ", "_").toLowerCase();
		String browserDetails = browser + version;
		String osAndBrowserInfo = osDetails + "_" + browserDetails;

		int noOfSuiteCount = 0;
		String tempSheetName = null;
		String configSheetName = null;
		String msuitePackageName = null;
		tempSheetName = sheetName;

		// fetching test data sheet names depends on package name
		if (sheetName.contains("smoke")) {
			// No of suite going to execute on parallel
			noOfSuiteCount = 7;
			configSheetName = "Smoke";
		} else if (sheetName.contains("regression")) {
			noOfSuiteCount = 2;
			configSheetName = "Regression";
		}

		// To get the package and class name
		msuitePackageName = sheetName.split("\\.")[(sheetName.split("\\.").length) - 2];
		sheetName = tempSheetName.split("\\.")[(tempSheetName.split("\\.").length) - 1];

		String browserAndSuiteWisecredentialsheetName = "BrowserCredentials";

		// Get the test data from test case sheet
		dataToBeReturned = DataUtils.testDatabyID(testcaseId, sheetName, configSheetName);

		// Check for any existing teacher & Student credentials in the test,
		// else will take the credentials from BrowserCredentials sheet
		if (dataToBeReturned.containsKey("T_USERNAME") && !dataToBeReturned.get("T_USERNAME").trim().isEmpty() && !dataToBeReturned.get("T_PASSWORD").trim().isEmpty()) {
			if (dataToBeReturned.containsKey("STUD_USERNAME") && !dataToBeReturned.get("STUD_USERNAME").trim().isEmpty() && !dataToBeReturned.get("STUD_PASSWORD").trim().isEmpty()) {
				dataToBeReturned.put("BROWSER_NAME", osAndBrowserInfo);
			} else {
				// dataToBeReturned.put("BROWSER_NAME", osAndBrowserInfo);
				credentials = getCredentailsDependsOnCombo(noOfSuiteCount, platform, version, browser, sheetName, configSheetName, msuitePackageName, osAndBrowserInfo, browserAndSuiteWisecredentialsheetName, driver);
				credentials.remove("T_USERNAME");
				credentials.remove("T_PASSWORD");
				dataToBeReturned.put("STUD_USERNAME", credentials.get("STUD_USERNAME"));
				dataToBeReturned.put("STUD_PASSWORD", credentials.get("STUD_PASSWORD"));
			}
		} else if (dataToBeReturned.containsKey("STUD_USERNAME") && !dataToBeReturned.get("STUD_USERNAME").trim().isEmpty() && !dataToBeReturned.get("STUD_PASSWORD").trim().isEmpty()) {
			// dataToBeReturned.put("BROWSER_NAME", osAndBrowserInfo);
			credentials = getCredentailsDependsOnCombo(noOfSuiteCount, platform, version, browser, sheetName, configSheetName, msuitePackageName, osAndBrowserInfo, browserAndSuiteWisecredentialsheetName, driver);
			credentials.remove("STUD_USERNAME");
			credentials.remove("STUD_PASSWORD");
			dataToBeReturned.put("T_USERNAME", credentials.get("T_USERNAME"));
			dataToBeReturned.put("T_PASSWORD", credentials.get("T_PASSWORD"));

		} else {
			credentials = getCredentailsDependsOnCombo(noOfSuiteCount, platform, version, browser, sheetName, configSheetName, msuitePackageName, osAndBrowserInfo, browserAndSuiteWisecredentialsheetName, driver);
		}
		Log.event("credentials data:: " + credentials);
		Log.event("Test data:: " + dataToBeReturned);
		if (credentials != null) {
			dataToBeReturned.putAll(credentials);
		}
		Log.event("All input data:: " + dataToBeReturned);
		return dataToBeReturned;
	}

	/**
	 * To get the credentials depends on parameters value
	 * 
	 * @param noOfSuiteCount
	 * @param platform
	 * @param version
	 * @param browser
	 * @param sheetName
	 *            - test cases sheet name
	 * @param configSheetName
	 *            - config sheet name
	 * @param msuitePackageName
	 *            - Mega suite name
	 * @param osAndBrowserInfo
	 *            - OS and browser details
	 * @param browserAndSuiteWisecredentialsheetName
	 * @param driver
	 * @return credentials - depends on parameter input return the credentials
	 */
	public static HashMap<String, String> getCredentailsDependsOnCombo(int noOfSuiteCount, Platform platform, String version, String browser, String sheetName, String configSheetName, String msuitePackageName, String osAndBrowserInfo, String browserAndSuiteWisecredentialsheetName, final WebDriver driver) {
		HashMap<String, String> credentials = null;
		String tag = null;
		String deviceName = Utils.getDeviceName(driver);
		if (StringUtils.containsIgnoreCase(platform.toString(), "WIN") || StringUtils.containsIgnoreCase(platform.toString(), "VISTA") || StringUtils.containsIgnoreCase(platform.family().toString(), "WIN")) {
			if (browser.matches(".*chrome.*")) {
				if (!deviceName.contains("browser")) {
					tag = setTagForDeviceCombo(deviceName);
					osAndBrowserInfo = deviceName;
				} else {
					tag = "win_chrome";
				}

				for (int suite = 1; suite <= noOfSuiteCount; suite++) {
					if (sheetName.contains("Suite" + suite) && configSheetName.equals("Smoke")) {
						credentials = DataUtils.testDatabyID(tag + "_suite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
					} else if (msuitePackageName.contains("MSuite" + suite) && configSheetName.equals("Regression")) {
						credentials = DataUtils.testDatabyID(tag + "_msuite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
					}
				}
				credentials.put("BROWSER_NAME", osAndBrowserInfo);
			} else if (browser.matches(".*firefox.*")) {

				for (int suite = 1; suite <= noOfSuiteCount; suite++) {
					if (sheetName.contains("Suite" + suite) && configSheetName.equals("Smoke")) {
						credentials = DataUtils.testDatabyID("win_firefox_suite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
					} else if (msuitePackageName.contains("MSuite" + suite) && configSheetName.equals("Regression")) {
						credentials = DataUtils.testDatabyID("win_firefox_msuite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
					}
				}
				credentials.put("BROWSER_NAME", osAndBrowserInfo);
			} else if (browser.matches(".*explore.*") && version.contains("10")) {
				for (int suite = 1; suite <= noOfSuiteCount; suite++) {
					if (sheetName.contains("Suite" + suite) && configSheetName.equals("Smoke")) {
						credentials = DataUtils.testDatabyID("win_ie10_suite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
					} else if (msuitePackageName.contains("MSuite" + suite) && configSheetName.equals("Regression")) {
						credentials = DataUtils.testDatabyID("win_ie10_msuite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
					}
				}
				credentials.put("BROWSER_NAME", osAndBrowserInfo);
			} else if (browser.matches(".*explore.*") && version.contains("11")) {

				for (int suite = 1; suite <= noOfSuiteCount; suite++) {
					if (sheetName.contains("Suite" + suite) && configSheetName.equals("Smoke")) {
						credentials = DataUtils.testDatabyID("win_ie11_suite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
					} else if (msuitePackageName.contains("MSuite" + suite) && configSheetName.equals("Regression")) {
						credentials = DataUtils.testDatabyID("win_ie11_msuite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
					}
				}
				credentials.put("BROWSER_NAME", osAndBrowserInfo);
			}

		} else if (StringUtils.containsIgnoreCase(platform.toString(), "MAC")) {

			if (browser.matches(".*chrome.*")) {
				if (!deviceName.contains("browser")) {
					tag = setTagForDeviceCombo(deviceName);
					osAndBrowserInfo = deviceName;
				} else {
					tag = "mac_chrome";
				}
				for (int suite = 1; suite <= noOfSuiteCount; suite++) {
					if (sheetName.contains("Suite" + suite) && configSheetName.equals("Smoke")) {
						credentials = DataUtils.testDatabyID(tag + "_suite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
					} else if (msuitePackageName.contains("MSuite" + suite) && configSheetName.equals("Regression")) {
						credentials = DataUtils.testDatabyID(tag + "_msuite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
					}
				}
				credentials.put("BROWSER_NAME", osAndBrowserInfo);
			} else if (browser.matches(".*firefox.*")) {
				for (int suite = 1; suite <= noOfSuiteCount; suite++) {
					if (sheetName.contains("Suite" + suite) && configSheetName.equals("Smoke")) {
						credentials = DataUtils.testDatabyID("mac_firefox_suite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
					} else if (msuitePackageName.contains("MSuite" + suite) && configSheetName.equals("Regression")) {
						credentials = DataUtils.testDatabyID("mac_firefox_msuite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
					}
				}
				credentials.put("BROWSER_NAME", osAndBrowserInfo);
			} else if (browser.matches(".*safari.*")) {

				for (int suite = 1; suite <= noOfSuiteCount; suite++) {
					if (sheetName.contains("Suite" + suite) && configSheetName.equals("Smoke")) {
						credentials = DataUtils.testDatabyID("mac_safari_suite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
					} else if (msuitePackageName.contains("MSuite" + suite) && configSheetName.equals("Regression")) {
						credentials = DataUtils.testDatabyID("mac_safari_msuite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
					}
				}
				credentials.put("BROWSER_NAME", osAndBrowserInfo);
			}
			/* To execute in devices - Android and iOS */
		} else if (StringUtils.containsIgnoreCase(platform.toString(), "ANDROID") || StringUtils.containsIgnoreCase(browser.toString(), "android")) {
			for (int suite = 1; suite <= noOfSuiteCount; suite++) {
				if (sheetName.contains("Suite" + suite)) {
					credentials = DataUtils.testDatabyID("android_chrome_suite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
				}
			}
			credentials.put("BROWSER_NAME", osAndBrowserInfo);

		} else if (StringUtils.containsIgnoreCase(platform.toString(), "iOS") || StringUtils.containsIgnoreCase(browser.toString(), "safari")) {
			for (int suite = 1; suite <= noOfSuiteCount; suite++) {
				if (sheetName.contains("Suite" + suite)) {
					credentials = DataUtils.testDatabyID("ios_safari_suite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
				}
			}
			credentials.put("BROWSER_NAME", osAndBrowserInfo);
		} else {
			for (int suite = 1; suite <= noOfSuiteCount; suite++) {
				if (sheetName.contains("Suite" + suite) && configSheetName.equals("Smoke")) {
					credentials = DataUtils.testDatabyID(tag + "_suite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
				} else if (msuitePackageName.contains("MSuite" + suite) && configSheetName.equals("Regression")) {
					credentials = DataUtils.testDatabyID(tag + "_msuite" + suite, browserAndSuiteWisecredentialsheetName, configSheetName);
				}
			}
			Log.fail("No credentails match with given platform and browsers.. please check the credentails..!", driver);
		}
		return credentials;
	}

	/**
	 * Switching between tabs or windows in a browser
	 * 
	 * @param driver
	 */
	public static void switchToNewWindow(WebDriver driver) {
		String winHandle = driver.getWindowHandle();
		for (String index : driver.getWindowHandles()) {
			if (!index.equals(winHandle)) {
				driver.switchTo().window(index);
				break;
			}
		}
		if (!((RemoteWebDriver) driver).getCapabilities().getBrowserName().matches(".*safari.*")) {
			((JavascriptExecutor) driver).executeScript("if(window.screen){window.moveTo(0, 0); window.resizeTo(window.screen.availWidth, window.screen.availHeight);};");
		}
	}

	/**
	 * To compare two array list values,then print unique list value and print
	 * missed list value
	 * 
	 * @param expectedElements
	 *            - expected element list
	 * @param actualElements
	 *            - actual element list
	 * @return statusToBeReturned - returns true if both the lists are equal,
	 *         else returns false
	 */
	public static boolean compareTwoList(List<String> expectedElements, List<String> actualElements) {
		boolean statusToBeReturned = false;
		List<String> uniqueList = new ArrayList<String>();
		List<String> missedList = new ArrayList<String>();
		for (String item : expectedElements) {
			if (actualElements.contains(item)) {
				uniqueList.add(item);
			} else {
				missedList.add(item);
			}
		}
		Collections.sort(expectedElements);
		Collections.sort(actualElements);
		if (expectedElements.equals(actualElements)) {
			Log.event("All elements checked on this page:: " + uniqueList);
			statusToBeReturned = true;
		} else {
			Log.event("Missing element on this page:: " + missedList);
			statusToBeReturned = false;
		}
		return statusToBeReturned;
	}

	/**
	 * Verifies if all the values in the boolean array are true or not
	 * 
	 * @param array
	 *            of boolean values
	 * @return overall status of the booleans in the array
	 */
	public static boolean isAllTrue(ArrayList<Boolean> array) {
		for (boolean b : array)
			if (!b)
				return false;
		return true;
	}

	/**
	 * To verify student name if or not trim issue found(to handle student name
	 * trim issue in sauce lab)
	 * 
	 * @param expectedStudentName
	 *            - expected student name
	 * @param actualStudentName
	 *            - actual student name in UI
	 * @return boolean status-return true if both the names are equal.
	 */
	public static boolean verifyStudentName(String expectedName, String actualName) {
		boolean status = false;
		int expectedLength = expectedName.length();
		int actualLength = actualName.length();
		Log.event("expectedlength :: " + expectedLength + " and actual length ::" + actualLength);
		if ((expectedLength - actualLength) == 0 && expectedName.equals(actualName)) {
			status = true;
			Log.event("Student name in both expected ::" + expectedName + " and actual ::" + actualName + " is verified");
		} else if ((expectedLength - actualLength) == 1) {
			expectedName = expectedName.substring(0, expectedName.length() - 1);
			if (expectedName.equals(actualName)) {
				status = true;
				Log.event("Student name after truncation is verified");
			} else {
				status = false;
				Log.event("Student name in both expected ::" + expectedName + " and actual ::" + actualName + " is not verified");
			}
		}
		return status;
	}

	/**
	 * To get inputslist string value depends on nthvalue count
	 * 
	 * @param inputslist
	 *            - contains more than one value and its separated with pipe(|)
	 *            symbol
	 * @param nthvalue
	 *            - which nthvalue wants to get
	 * @return dataToBeReturned - depends on nthvalue value data will be return
	 *         from nthvalue string
	 * @throws Exception
	 */
	public static String getValueFromStringArray(String inputslist, int nthvalue) throws Exception {
		String dataToBeReturned = null;
		try {
			if (inputslist.contains("|")) {
				dataToBeReturned = inputslist.split("\\|")[nthvalue - 1];
			} else {
				throw new Exception("Invalid input, seems its doesn't seperated with pipe(|) )");
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new Exception("Invalid input, check the parameter )");
		}
		return dataToBeReturned;
	}

	/**
	 * To get value out of a string that is separated by given character
	 * 
	 * @param splitChar
	 *            - the given character string to split on
	 * @param inputslist
	 *            - contains multiple values separated by given character
	 * @param nth
	 *            - which item in the list to get
	 * @return dataToBeReturned
	 * @throws Exception
	 */
	public static String getValueFromStringArray(String splitChar, String inputslist, int nth) throws Exception {
		String dataToBeReturned = null;
		try {
			if (inputslist.contains(splitChar)) {
				dataToBeReturned = inputslist.split(Pattern.quote(splitChar))[nth - 1];
			} else {
				throw new Exception("Invalid input, seems it isn't separated by " + splitChar);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new Exception("Invalid input, check the parameter.");
		}
		return dataToBeReturned;
	}

	/**
	 * To set the tag name for device combos to fetch credentials
	 * 
	 * @param deviceName
	 *            - combo name
	 * @return tag - return the tag name for device
	 */
	public static String setTagForDeviceCombo(String deviceName) {
		String tag = null;
		if (MobileEmulationUserAgentConfiguration.SM_GALAXY_TAB4_AND4_4_2_LANDSCAPE.equals(deviceName)) {
			tag = "galaxy_and_4_4_ls";

		} else if (MobileEmulationUserAgentConfiguration.SM_GALAXY_TAB4_AND4_4_2_PORTRAIT.equals(deviceName)) {
			tag = "galaxy_and_4_4_pr";

		} else if (MobileEmulationUserAgentConfiguration.SM_GALAXY_TAB3_AND4_2_2_LANDSCAPE.equals(deviceName)) {
			tag = "galaxy_and_4_2_ls";

		} else if (MobileEmulationUserAgentConfiguration.SM_GALAXY_TAB3_AND4_2_2_PORTRAIT.equals(deviceName)) {
			tag = "galaxy_and_4_2_ls";

		} else if (MobileEmulationUserAgentConfiguration.APPLE_IPAD4_IOS8_LANDSCAPE.equals(deviceName)) {
			tag = "ipad_ios8_ls";

		} else if (MobileEmulationUserAgentConfiguration.APPLE_IPAD4_IOS8_PORTRAIT.equals(deviceName)) {
			tag = "ipad_ios8_pr";

		} else if (MobileEmulationUserAgentConfiguration.APPLE_IPAD4_IOS7_LANDSCAPE.equals(deviceName)) {
			tag = "ipad_ios7_ls";

		} else if (MobileEmulationUserAgentConfiguration.APPLE_IPAD4_IOS7_PORTRAIT.equals(deviceName)) {
			tag = "ipad_ios7_pr";
		}
		return tag;
	}

	/**
	 * To compare two HashMap values,then print unique list value and print
	 * missed list value
	 * 
	 * @param expectedElements
	 *            - expected element list
	 * @param actualElements
	 *            - actual element list
	 * @return statusToBeReturned - returns true if both the lists are equal,
	 *         else returns false
	 */
	public static boolean compareTwoHashMap(Map<String, String> expectedList, Map<String, String> actualList) {
		List<String> missedkey = new ArrayList<String>();
		HashMap<String, String> missedvalue = new HashMap<String, String>();
		try {
			for (String k : expectedList.keySet()) {
				if (!(actualList.get(k).equals(expectedList.get(k)))) {
					missedvalue.put(k, actualList.get(k));
					Log.event("Missed Values:: " + missedvalue);
					return false;
				}
			}
			for (String y : actualList.keySet()) {
				if (!expectedList.containsKey(y)) {
					missedkey.add(y);
					Log.event("Missed keys:: " + missedkey);
					return false;
				}
			}
		} catch (NullPointerException np) {
			return false;
		}
		return true;
	}


	// TODO find instances of this and next method in Realize Regression project
	// to remove them
	/**
	 * To convert color of an element from rgba to hex
	 * 
	 * @param color
	 * @return String of hex value
	 */
	public static String convertColorFromRgbaToHex(String color) {
		String[] hexValue = color.replace("rgba(", "").replace(")", "").split(",");

		int hexValue1 = Integer.parseInt(hexValue[0]);
		hexValue[1] = hexValue[1].trim();
		int hexValue2 = Integer.parseInt(hexValue[1]);
		hexValue[2] = hexValue[2].trim();
		int hexValue3 = Integer.parseInt(hexValue[2]);

		String actualColor = String.format("#%02x%02x%02x", hexValue1, hexValue2, hexValue3);

		return actualColor;
	}

	/**
	 * To check background color of given element
	 * 
	 * @param elementToCheck
	 *            - WebElement that we are checking
	 * @param desiredColor
	 *            - hex value of a color
	 * @return boolean value
	 * @throws Exception
	 */
	public static boolean checkBackgroundColor(WebElement elementToCheck, String desiredColor) throws Exception {
		boolean flag = false;
		try {
			String color = elementToCheck.getCssValue("background-color");
			String actualColor = convertColorFromRgbaToHex(color);
			flag = actualColor.equalsIgnoreCase(desiredColor);
		} catch (NoSuchElementException e) {
		}
		return flag;
	}

    /**
     * To Pick username based on the current browser
     * @param username -  list of username separated by | symbol
     * @param driver
     * @return String - username based on the current browser
     * @throws Exception 
     */
    public static String pickUserName(String username, WebDriver driver) throws Exception{
		String dataToBeReturned = null;
		if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().matches(".*chrome.*")) {
			dataToBeReturned = getValueFromStringArray(username, 1);
		} else if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().matches(".*explore.*")) {
			dataToBeReturned = getValueFromStringArray(username, 2);
		} else if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().matches(".*safari.*")) {
			dataToBeReturned = getValueFromStringArray(username, 3);
		} else if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().matches(".*firefox.*")) {
			dataToBeReturned = getValueFromStringArray(username, 4);
		}
		return dataToBeReturned;          
 	}

    /**
     * To change date format from MMDDYYYY to MMDDYY
     * @param date
     * 			date in format MMDDYYYY
     * 
     * @return date in format MMDDYY
     * @throws Exception 
     */
    public static String changeDateFormatToMMDDYY(String dateMMDDYYYY) throws Exception{
        
  	  Date actualDate = null;
  	 
  	  SimpleDateFormat yy = new SimpleDateFormat("MM/dd/yy");
  	  SimpleDateFormat yyyy = new SimpleDateFormat("MM/dd/yyyy");

  	  try {
  	   actualDate = yyyy.parse(dateMMDDYYYY);
  	  } catch (ParseException pe) {
  		throw new Exception("Invalid date, check the parameter.");
  	  }
  	  
  	  return yy.format(actualDate);

  	 }

    /**
     * To verify a list is in alphabetical order
     * @param listToCheck
     * @return boolean
     */
    public static boolean verifyListInAlphabeticalOrder(List<WebElement> listToCheck) {
        boolean status = false;
        List<String> ActualList = new ArrayList<String>();
        List<String> Sortedlist = new ArrayList<String>();
        for (WebElement element : listToCheck) {
            ActualList.add(element.getText());
            Sortedlist.add(element.getText());
        }
        Collections.sort(Sortedlist);
        if (ActualList.equals(Sortedlist)) {
            status = true;
            Log.message("List is in alphabetical order: " + Sortedlist);
        } else {
            status = false;
        }
        return status;
    }

    /**
     * To verify the contents of a tool tip
     * @param tooltipToCheck - the WebElement of the tool tip
     * @param expectedContents - the String contents of what should appear
     * @param driver 
     * @return boolean - return true if tool tip message is equivalent to
     *         expectedContents, else return false
     * @throws InterruptedException
     */

    public static boolean verifyToolTipContents(WebElement tooltipToCheck, String expectedContents, WebDriver driver) throws InterruptedException {
        boolean status = false;
        String actualContents = null;

        if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().matches(".*safari.*")) {
            String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover', true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(mouseOverScript, tooltipToCheck);
            
        } else {
            Actions ToolTip = new Actions(driver);
            ToolTip.moveToElement(tooltipToCheck).perform();
        }
        Thread.sleep(2000);
        actualContents = tooltipToCheck.getAttribute("popover").trim();
        Log.message("Mouseover tool tip:: \"" + actualContents + "\"");
        status = expectedContents.equalsIgnoreCase(actualContents) ? true : false;       
        return status;
    }

    /**
     * Verify the css property for an element
     * @param element - WebElement for which to verify the css property
     * @param cssProperty - the css property name to verify
     * @param actualValue - the actual css value of the element
     * @return boolean  
     */
    public static boolean verifyCssPropertyForElement(WebElement element, String cssProperty, String actualValue){
		boolean result = false;
		
		String actualClassProperty = element.getCssValue(cssProperty);
		
		if(actualClassProperty.contains(actualValue)){
				result = true;
		}
		return result;
	}
    
    /**
     * Get random number 
     * @return String, random number
     */
    public static String getRandomNumber()
    {
    	Random random = new Random(System.nanoTime() % 100000);
    	int randomInt = random.nextInt(1000000000);
    	return String.valueOf(randomInt);
    }
    
    /**
     * Wait for the numberOfWindowsToBe the given numberOfWindows
     * @param numberOfWindows
     * @return
     */
    public static ExpectedCondition<Boolean> numberOfWindowsToBe(final int numberOfWindows) {
        return new ExpectedCondition<Boolean>() {
          @Override
          public Boolean apply(WebDriver driver) {
                    driver.getWindowHandles();
            return driver.getWindowHandles().size() == numberOfWindows;
          }
        };
    }
    
    /**
     * Verify contents of a WebElement equals a passed in string variable
     * @param textToVerify - expected text
     * @param elementToVerify - element to verify the text of
     * @return true if text on screen matches passed variable contents
     */
    public static boolean verifyWebElementTextEquals(WebElement elementToVerify, String textToVerify) {
        boolean status = false;
        if (elementToVerify.getText().trim().replaceAll("\\s+", " ").equals(textToVerify)) {
            status = true;
        }
        return status;
    }

    /**
     * Verify contents of a WebElement equals ignoring case a passed in string variable
     * @param textToVerify - expected text
     * @param elementToVerify - element to verify the text of
     * @return true if text on screen equals ignoring case passed variable contents
     */
    public static boolean verifyWebElementTextEqualsIgnoreCase(WebElement elementToVerify, String textToVerify) {
        boolean status = false;
        if (elementToVerify.getText().trim().replaceAll("\\s+", " ").equalsIgnoreCase(textToVerify.trim())) {
            status = true;
        }
        return status;
    }
}