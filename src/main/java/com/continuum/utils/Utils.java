package com.continuum.utils;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

/**
 * Util class consists wait for page load,page load with user defined max time
 * and is used globally in all classes and methods
 *
 */
public class Utils {
	private static EnvironmentPropertiesReader configProperty = EnvironmentPropertiesReader.getInstance();
	static MobileEmulationUserAgentConfiguration mobEUA = new MobileEmulationUserAgentConfiguration();

	/**
	 * waitForPageLoad waits for the page load with default page load wait time
	 *
	 */
	public static void waitForPageLoad(final WebDriver driver) {
		waitForPageLoad(driver, WebDriverFactory.maxPageLoadWait);
	}

	/**
	 * waitForPageLoad waits for the page load with custom page load wait time
	 *
	 */
	public static void waitForPageLoad(final WebDriver driver, int maxWait) {
		long startTime = StopWatch.startTime();
		FluentWait<WebDriver> wait = new WebDriverWait(driver, maxWait).pollingEvery(500, TimeUnit.MILLISECONDS).ignoring(StaleElementReferenceException.class).withMessage("Page Load Timed Out");
		try {

			wait.until(WebDriverFactory.documentLoad);
			wait.until(WebDriverFactory.imagesLoad);
			wait.until(WebDriverFactory.framesLoad);
			String title = driver.getTitle().toLowerCase();
			String url = driver.getCurrentUrl().toLowerCase();
			Log.event("Page URL:: " + url);

			if ("the page cannot be found".equalsIgnoreCase(title) || title.contains("is not available") || url.contains("/error/") || url.toLowerCase().contains("/errorpage/")) {
				Assert.fail("Site is down. [Title: " + title + ", URL:" + url + "]");
			}
		} catch (TimeoutException e) {
			driver.navigate().refresh();
			wait.until(WebDriverFactory.documentLoad);
			wait.until(WebDriverFactory.imagesLoad);
			wait.until(WebDriverFactory.framesLoad);
		}
		Log.event("Page Load Wait: (Sync)", StopWatch.elapsedTime(startTime));

	} // waitForPageLoad

	/**
	 * To get the device name
	 * 
	 * <p>
	 * if test run on sauce lab device then return device name or valid message,
	 * otherwise check local device execution then return device name or valid
	 * message
	 * 
	 * @param driver
	 *            - corresponding web driver instance
	 * @return dataToBeReturned - device name or valid message
	 */
	public static String getDeviceName(final WebDriver driver) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String dataToBeReturned = null;
		boolean checkExecutionOnSauce = false;
		boolean checkDeviceExecution = false;
		checkExecutionOnSauce = (System.getProperty("SELENIUM_DRIVER") != null || System.getenv("SELENIUM_DRIVER") != null) ? true : false;

		if (checkExecutionOnSauce) {
			checkDeviceExecution = ((System.getProperty("runUserAgentDeviceTest") != null) && (System.getProperty("runUserAgentDeviceTest").equalsIgnoreCase("true"))) ? true : false;
			if (checkDeviceExecution) {
				String userAgentString = (String) (js.executeScript("return navigator.userAgent"));
				long pixelRatio = (Long) js.executeScript("return window.devicePixelRatio");
				long width = (Long) js.executeScript("return screen.width");
				long height = (Long) js.executeScript("return screen.height");

				dataToBeReturned = mobEUA.getDeviceNameFromMobileEmulation(userAgentString, Long.toString(pixelRatio), Long.toString(width), Long.toString(height));
				// dataToBeReturned = (System.getProperty("deviceName") != null)
				// ? System.getProperty("deviceName") : null;
			} else {
				dataToBeReturned = "sauce browser test: no device";
			}
		} else {
			checkDeviceExecution = (configProperty.hasProperty("runUserAgentDeviceTest") && (configProperty.getProperty("runUserAgentDeviceTest").equalsIgnoreCase("true"))) ? true : false;
			if (checkDeviceExecution) {
				dataToBeReturned = configProperty.hasProperty("deviceName") ? configProperty.getProperty("deviceName") : null;
			} else {
				dataToBeReturned = "local browser test: no device";
			}
		}
		return dataToBeReturned;
	}

}
