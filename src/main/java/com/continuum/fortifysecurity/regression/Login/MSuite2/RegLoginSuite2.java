package com.continuum.fortifysecurity.regression.Login.MSuite2;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.continuum.common.utils.DataProviderUtils;
import com.continuum.common.utils.PortalUtils;
import com.continuum.itsportal.pages.HomePage;
import com.continuum.itsportal.pages.LoginPage;
import com.continuum.utils.EmailReport;
import com.continuum.utils.Log;
import com.continuum.utils.WebDriverFactory;


/**
 * This class for realizer assignment tests for suite one
 *
 */
@Listeners(EmailReport.class)
public class RegLoginSuite2 {

	final String remediationEnrichmentText = "Additional Practice for ";
	private String webSite;

	@BeforeTest
	public void init(ITestContext context) {
		webSite = (System.getProperty("webSite") != null ? System.getProperty("webSite") : context.getCurrentXmlTest().getParameter("webSite")).toLowerCase();
	}

	@Test(description = "O365 opt-in with business premium P2", dataProviderClass = DataProviderUtils.class, dataProvider = "realizeBVTDataProvider", priority = 0)
	public void tc001RegLoginSuite2(String browser) throws Exception {
		final WebDriver driver = WebDriverFactory.get(browser);
		// Loading the test data from excel using the test case id
		HashMap<String, String> testData = PortalUtils.getTestData("C11247662", RegLoginSuite2.class.getName(), driver);
		browser = testData.get("BROWSER_NAME").toString();
		String itsLoginID = testData.get("ITS_USERNAME").toString();
		String itsPassword = testData.get("ITS_PASSWORD").toString();
		Log.testCaseInfo("Verify partner opt-in with azure premium P2 account <small><b><i>[" + browser + "]</b></i></small>");
		try {
			LoginPage loginPage = new LoginPage(driver, webSite).get();
			HomePage homePage = loginPage.loginToITSPortal(itsLoginID, itsPassword, false);	
			Log.testCaseResult();
		} catch (Exception e) {
			Log.exception(e, driver);
		} finally {
			Log.endTestCase();
			driver.quit();
		}
	}	
	
	@Test(description = "O365 opt-in with Business Premium account", dataProviderClass = DataProviderUtils.class, dataProvider = "realizeBVTDataProvider", priority = 0)
	public void tc002RegLoginSuite2(String browser) throws Exception {
		final WebDriver driver = WebDriverFactory.get(browser);
		// Loading the test data from excel using the test case id
		HashMap<String, String> testData = PortalUtils.getTestData("C11247663", RegLoginSuite2.class.getName(), driver);
		browser = testData.get("BROWSER_NAME").toString();
		String itsLoginID = testData.get("ITS_USERNAME").toString();
		String itsPassword = testData.get("ITS_PASSWORD").toString();
		Log.testCaseInfo("Verify partner opt-in with Business Premium account <small><b><i>[" + browser + "]</b></i></small>");
		try {
			LoginPage loginPage = new LoginPage(driver, webSite).get();
			HomePage homePage = loginPage.loginToITSPortal(itsLoginID, itsPassword, false);	
			Log.testCaseResult();
		} catch (Exception e) {
			Log.exception(e, driver);
		} finally {
			Log.endTestCase();
			driver.quit();
		}
	}
}
