package com.continuum.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.xml.XmlTest;

import com.continuum.common.ui.BrowserType;
import com.continuum.ui.gridconfig.IGridCapabilitiesConfiguration;
import com.continuum.ui.gridconfig.SauceLabsCapabilitiesConfiguration;
import com.saucelabs.selenium.client.factory.SeleniumFactory;

/**
 * WebdriverFactory class used to get a web driver instance, depends on the user
 * requirement as driverHost, driverPort and browserName we adding the
 * desiredCapabilities and other static action initialized here and some methods
 * used to retrieve the Hub and node information. It also consists page wait
 * load for images/frames/document
 */

public class WebDriverFactory {
	private static Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);
	private static EnvironmentPropertiesReader configProperty = EnvironmentPropertiesReader.getInstance();
	private static MobileEmulationUserAgentConfiguration mobEmuUA = new MobileEmulationUserAgentConfiguration();

	static String driverHost;
	static String driverPort;
	static String browserName;
	static String deviceName;
	static URL hubURL;

	static DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
	static DesiredCapabilities firefoxCapabilities = DesiredCapabilities.firefox();
	static DesiredCapabilities chromeCapabilities = DesiredCapabilities.chrome();
	static DesiredCapabilities safariCapabilities = DesiredCapabilities.safari();
	static ChromeOptions opt = new ChromeOptions();
	static FirefoxProfile fp = new FirefoxProfile();
	public static ExpectedCondition<Boolean> documentLoad;
	public static ExpectedCondition<Boolean> framesLoad;
	public static ExpectedCondition<Boolean> imagesLoad;
	public static int maxPageLoadWait = 120;

	static {
		documentLoad = new ExpectedCondition<Boolean>() {
			public final Boolean apply(final WebDriver driver) {
				final JavascriptExecutor js = (JavascriptExecutor) driver;
				boolean docReadyState = false;
				try {
					docReadyState = (Boolean) js.executeScript("return (function() { if (document.readyState != 'complete') {  return false; } if (window.jQuery != null && window.jQuery != undefined && window.jQuery.active) { return false;} if (window.jQuery != null && window.jQuery != undefined && window.jQuery.ajax != null && window.jQuery.ajax != undefined && window.jQuery.ajax.active) {return false;}  if (window.angular != null && angular.element(document).injector() != null && angular.element(document).injector().get('$http').pendingRequests.length) return false; return true;})();");
				} catch (WebDriverException e) {
					docReadyState = true;
				}
				return docReadyState;
			}
		};

		imagesLoad = new ExpectedCondition<Boolean>() {
			public final Boolean apply(final WebDriver driver) {
				boolean docReadyState = true;
				try {
					JavascriptExecutor js;
					List<WebElement> images = driver.findElements(By.cssSelector("img[src]"));
					for (int i = 0; i < images.size(); i++) {
						try {
							js = (JavascriptExecutor) driver;
							docReadyState = docReadyState && (Boolean) js.executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", images.get(i));
							if (!docReadyState) {
								break;
							}
						} catch (StaleElementReferenceException e) {
							images = driver.findElements(By.cssSelector("img[src]"));
							i--;
							continue;
						} catch (WebDriverException e) {

							// setting the true value if any exception arise
							// Ex:: inside frame or switching to new windows or
							// switching to new frames
							docReadyState = true;
						}
					}
				} catch (WebDriverException e) {
					docReadyState = true;
				}
				return docReadyState;
			}
		};

		framesLoad = new ExpectedCondition<Boolean>() {
			public final Boolean apply(final WebDriver driver) {
				boolean docReadyState = true;
				try {
					JavascriptExecutor js;
					List<WebElement> frames = driver.findElements(By.cssSelector("iframe[style*='hidden']"));
					for (WebElement frame : frames) {
						try {
							driver.switchTo().defaultContent();
							driver.switchTo().frame(frame);
							js = (JavascriptExecutor) driver;
							docReadyState = docReadyState && (Boolean) js.executeScript("return (document.readyState==\"complete\")");
							driver.switchTo().defaultContent();
							if (!docReadyState) {
								break;
							}
						} catch (WebDriverException e) {
							docReadyState = true;
						}
					}
				} catch (WebDriverException e) {
					docReadyState = true;
				} finally {
					driver.switchTo().defaultContent();
				}
				return docReadyState;
			}
		};

		XmlTest test = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest();
		driverHost = System.getProperty("hubHost") != null ? System.getProperty("hubHost") : test.getParameter("deviceHost");
		driverPort = System.getProperty("hubPort") != null ? System.getProperty("hubPort") : test.getParameter("devicePort");

		maxPageLoadWait = configProperty.getProperty("maxPageLoadWait") != null ? Integer.valueOf(configProperty.getProperty("maxPageLoadWait")) : maxPageLoadWait;

		opt.addArguments("--ignore-certificate-errors");
		opt.addArguments("--disable-bundled-ppapi-flash");
		opt.addArguments("--disable-extensions");
		opt.addArguments("--disable-web-security");
		opt.addArguments("--always-authorize-plugins");
		opt.addArguments("--allow-running-insecure-content");
		opt.addArguments("--test-type");
		opt.addArguments("--enable-npapi");
		chromeCapabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true);

		try {
			hubURL = new URL("http://" + driverHost + ":" + driverPort + "/wd/hub");
		} catch (MalformedURLException e) {
			// e.printStackTrace();
		}

	}

	/**
	 * Method to get instance of web driver using default parameters
	 * 
	 * @return driver
	 */
	public static WebDriver get() {
		browserName = System.getProperty("browserName") != null ? System.getProperty("browserName") : Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("browserName").toLowerCase();
		return get(browserName);
	}

	/**
	 * get a new WebDriver instance with parameters. Just calls
	 * {@link #newWebDriverInstance(BrowserType, String, Proxy, IGridCapabilitiesConfiguration)}
	 * with null hub and grid options
	 * 
	 * @param browser
	 *            The browser you want to use
	 * @param proxy
	 *            a selenium proxy to use
	 * @return WebDriver instance
	 */
	public static WebDriver newWebDriverInstance(BrowserType browser, Proxy proxy) {
		return newWebDriverInstance(browser, null, proxy, null);
	}

	/**
	 * newWebDriverInstanceFromEnvironment is primarily for use with Jenkins.
	 * Currently the only environment it will setup correctly is saucelabs. It
	 * grabs the browser, os, screen res, etc. from the environment so you can
	 * run your tests with a configuration matrix.
	 * 
	 * required environment variables:
	 * 
	 * SELENIUM_DRIVER, SAUCE_USER_NAME, SAUCE_API_KEY
	 * 
	 * 
	 * @param testName
	 *            name of the test to pass to the grid
	 * @return remote WebDriver instance
	 */
	public static WebDriver newWebDriverInstanceFromEnvironment(String testName) {
		// right now we only support sauce labs

		if (System.getenv("SAUCE_USER_NAME") != null || System.getProperty("SAUCE_USER_NAME") != null) {
			SauceLabsCapabilitiesConfiguration caps = new SauceLabsCapabilitiesConfiguration(testName, System.getenv("BUILD_ID"));
			String screenResolution = configProperty.hasProperty("screenResolution") ? configProperty.getProperty("screenResolution") : null;
			String seleniumVersion = configProperty.hasProperty("seleniumVersion") ? configProperty.getProperty("seleniumVersion") : null;
			String iedriverVersion = configProperty.hasProperty("iedriverVersion") ? configProperty.getProperty("iedriverVersion") : null;
			String chromedriverVersion = configProperty.hasProperty("chromedriverVersion") ? configProperty.getProperty("chromedriverVersion") : null;
			String maxTestDuration = configProperty.hasProperty("maxTestDuration") ? configProperty.getProperty("maxTestDuration") : null;
			String commandTimeout = configProperty.hasProperty("commandTimeout") ? configProperty.getProperty("commandTimeout") : null;
			String idleTimeout = configProperty.hasProperty("idleTimeout") ? configProperty.getProperty("idleTimeout") : null;

			caps.setRecordSnapshot(false);

			// To setting the screen resolution
			if (screenResolution != null) {
				caps.setScreenResolution(screenResolution);
			}

			// To setting the selenium, ie and chrome driver version
			// capabilities
			if (seleniumVersion != null)
				caps.setSeleniumVersion(seleniumVersion);
			if (iedriverVersion != null)
				caps.setIeDriverVersion(iedriverVersion);
			if (chromedriverVersion != null)
				caps.setChromeDriverVersion(chromedriverVersion);

			// Timeout capabilities
			if (maxTestDuration != null)
				caps.setMaxTestDuration(maxTestDuration);
			if (commandTimeout != null)
				caps.setCommandTimeout(commandTimeout);
			if (idleTimeout != null)
				caps.setIdleTimeout(idleTimeout);

			WebDriver driver = null;
			String userAgent = null;
			// User agent capabilities
			if (System.getProperty("runUserAgentDeviceTest") != null) {
				if (System.getProperty("runUserAgentDeviceTest").equalsIgnoreCase("true")) {
					deviceName = System.getProperty("deviceName") != null ? System.getProperty("deviceName") : null;
					userAgent = mobEmuUA.getUserAgent(deviceName) != null ? mobEmuUA.getUserAgent(deviceName) : null;

					if (deviceName != null && userAgent != null) {
						//driver = SeleniumFactory.createWebDriver(caps.getUserAgentDesiredCapabilities(caps, deviceName, userAgent));
					} else {
						logger.error("Invalid mobile emulation configuration, check the parameters(deviceName) value: " + deviceName);
					}
				} else {
					logger.error("runUserAgentDeviceTest value has been set as false");
				}
			} else {
				//driver = SeleniumFactory.createWebDriver(caps.getDesiredCapabilities());
			}
			String saucelabsSessionId = (((RemoteWebDriver) driver).getSessionId()).toString();
			String sauceLink = "http://saucelabs.com/jobs/" + saucelabsSessionId + "?auth=" + newHMACMD5Digest(System.getenv("SAUCE_USER_NAME") + ":" + System.getenv("SAUCE_API_KEY"), saucelabsSessionId);
			logger.debug("Saucelab link for " + testName + ":: " + sauceLink);
			Log.addSauceJobUrlToReport(driver, sauceLink);
			return driver;
		} else {
			return null;
		}

	}

	/**
	 * generates an md5 HMAC digest based on the provided key and message.
	 * 
	 * @param keyString
	 *            Secret key
	 * @param msg
	 *            The message to be authenticated
	 * @return the digest
	 */
	public static String newHMACMD5Digest(String keyString, String msg) {
		String sEncodedString = null;
		try {
			SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacMD5");
			Mac mac = Mac.getInstance("HmacMD5");
			mac.init(key);

			byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));

			StringBuffer hash = new StringBuffer();

			for (int i = 0; i < bytes.length; i++) {
				String hex = Integer.toHexString(0xFF & bytes[i]);
				if (hex.length() == 1) {
					hash.append('0');
				}
				hash.append(hex);
			}
			sEncodedString = hash.toString();
		} catch (UnsupportedEncodingException e) {
		} catch (InvalidKeyException e) {
		} catch (NoSuchAlgorithmException e) {
		}
		return sEncodedString;
	}

	/**
	 * get a new WebDriver instance with parameters
	 * 
	 * @param browser
	 *            The browser you want to use
	 * @param gridCapabilitiesConfig
	 *            If using a selenium grid, platform to use with it
	 * @param sHubUrl
	 *            string representing the url of the hub you want to use.
	 *            requires platform to not be null
	 * @param proxy
	 *            a selenium proxy to use
	 * 
	 * @return a new WebDriver object or null if something failed
	 */
	public static WebDriver newWebDriverInstance(BrowserType browser, String sHubUrl, Proxy proxy, IGridCapabilitiesConfiguration gridCapabilitiesConfig) {
		if (browser == null) {
			logger.error("BrowserType is null.");
			return null;
		}
		logger.debug("BrowserType is [" + browser.toString() + "]");

		WebDriver webdriver = null;
		DesiredCapabilities capabilities = null;
		URL url = null;
		if (sHubUrl != null) {
			try {
				url = new URL(sHubUrl);
			} catch (MalformedURLException e) {
				logger.error(e.getMessage());
				return null;
			}
			if (gridCapabilitiesConfig == null) {
				logger.error("requested to use a selenium hub but no capabilties specified");
				return null;
			}
			// can bypass pretty everything else below. TODO don't make this so
			// hackish
			if (sHubUrl.contains("saucelabs.com") || sHubUrl.contains("browserstack.com")) {
				return new RemoteWebDriver(url, gridCapabilitiesConfig.getDesiredCapabilities());
			}
		}

		try {
			switch (browser) {
			case Chrome:
				String os = System.getProperty("os.name").toLowerCase();
				if (os.indexOf("win") >= 0) {
					URL driverSrc = WebDriverFactory.class.getResource("/drivers/win/chromedriver.exe");
					File dest = new File(System.getProperty("user.dir") + "/chromedriver.exe");
					org.apache.commons.io.FileUtils.copyURLToFile(driverSrc, dest);
					System.setProperty("webdriver.chrome.driver", dest.getAbsolutePath());
				} else {
					// TODO add other OSes and turn this into an actual
					// exception
					new RuntimeException("Your OS currently isn't supported. Please bug someone to implement support");
				}
				System.setProperty("webdriver.chrome.logfile", "chromedriver.log");
				// Using chrome options to get rid of some flag/banners during
				// test execution
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--test-type");
				options.addArguments("start-maximized");
				capabilities = DesiredCapabilities.chrome();
				capabilities.setCapability(ChromeOptions.CAPABILITY, options);
				if (proxy != null) {
					capabilities.setCapability(CapabilityType.PROXY, proxy);
				}
				if (hubURL != null) {
					webdriver = new RemoteWebDriver(url, capabilities);
				} else {
					webdriver = new ChromeDriver(capabilities);
				}
				break;
			case Firefox:
				capabilities = DesiredCapabilities.firefox();
				capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
				if (proxy != null) {
					capabilities.setCapability(CapabilityType.PROXY, proxy);
				}
				if (hubURL != null) {
					webdriver = new RemoteWebDriver(hubURL, capabilities);
				} else {
					webdriver = new FirefoxDriver(capabilities);
				}
				break;
			case IE:
				URL driverSrc = WebDriverFactory.class.getResource("/drivers/win/IEDriverServer.exe");
				File dest = new File(System.getProperty("user.dir") + "/IEDriverServer.exe");
				org.apache.commons.io.FileUtils.copyURLToFile(driverSrc, dest);
				capabilities = DesiredCapabilities.internetExplorer();
				capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, false);
				capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
				capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
				capabilities.setCapability("ignoreProtectedModeSettings", true);
				capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
				capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

				if (proxy != null) {
					capabilities.setCapability(CapabilityType.PROXY, proxy);
				}
				if (hubURL != null) {
					webdriver = new RemoteWebDriver(url, capabilities);
				} else {
					webdriver = new InternetExplorerDriver(capabilities);
				}
				// TODO add break back in once driver is added
				// break;
			case Safari:
			case PhantomJS:
			default:
				logger.error("invalid BrowserType or browser hasn't been implemented yet");

			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		if (webdriver != null) {
			// set implicit wait time
			webdriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		}
		return webdriver;
	}

	/**
	 * Webdriver to get the web driver with browser name and platform and
	 * setting the desired capabilities for browsers
	 */
	public static WebDriver get(String browserWithPlatform) {

		String browser = null;
		String platform = null;
		String browserVersion = null;

		String sauceUserName = null;
		String sauceAuthKey = null;
		WebDriver driver = null;
		String userAgent = null;
		long startTime = StopWatch.startTime();

		// Get invoking test name to pass on to Jenkins
		String callerMethodName = new Exception().getStackTrace()[1].getMethodName();
		String driverInitializeInfo[] = null;

		// Handling System property variable overridden on parallel execution
		// till web driver initialization part
		synchronized (System.class) {
			// From local to sauce lab for browser test
			if (configProperty.hasProperty("runSauceLabFromLocal") && configProperty.getProperty("runSauceLabFromLocal").equalsIgnoreCase("true")) {
				if (browserWithPlatform.contains("&")) {
					driverInitializeInfo = browserWithPlatform.split("&");
					browser = driverInitializeInfo[0];
					browser = BrowserType.fromConfiguration(browser).getConfiguration();
					browserVersion = driverInitializeInfo[1];
					platform = driverInitializeInfo[2];
				}

				sauceUserName = configProperty.hasProperty("sauceUserName") ? configProperty.getProperty("sauceUserName") : null;
				sauceAuthKey = configProperty.hasProperty("sauceAuthKey") ? configProperty.getProperty("sauceAuthKey") : null;

				System.setProperty("SELENIUM_DRIVER", "sauce-ondemand:?os=" + platform + "&browser=" + browser + "&browser-version=" + browserVersion + "&username=" + sauceUserName + "&access-key=" + sauceAuthKey);
				System.setProperty("SAUCE_USER_NAME", sauceUserName);
				System.setProperty("SAUCE_API_KEY", sauceAuthKey);

				// From local to sauce lab for device test
				if (configProperty.hasProperty("runUserAgentDeviceTest") && configProperty.getProperty("runUserAgentDeviceTest").equalsIgnoreCase("true")) {
					deviceName = driverInitializeInfo[3];
					System.setProperty("runUserAgentDeviceTest", "true");
					System.setProperty("deviceName", deviceName);
				}
			}

			// check for Jenkins override from Jenkins to sauce lab
			if (System.getProperty("SELENIUM_DRIVER") != null || System.getenv("SELENIUM_DRIVER") != null) {
				return newWebDriverInstanceFromEnvironment(callerMethodName);
			}
		}

		// To support local to local execution by grid configuration
		if (browserWithPlatform.contains("_")) {
			browser = browserWithPlatform.split("_")[0].toLowerCase().trim();
			platform = browserWithPlatform.split("_")[1].toUpperCase().trim();
		} else {
			platform = "ANY";
		}

		try {
			if ("chrome".equalsIgnoreCase(browser)) {
				if (configProperty.hasProperty("runUserAgentDeviceTest") && configProperty.getProperty("runUserAgentDeviceTest").equalsIgnoreCase("true")) {
					deviceName = configProperty.hasProperty("deviceName") ? configProperty.getProperty("deviceName") : null;
					userAgent = mobEmuUA.getUserAgent(deviceName);
					if (userAgent != null && deviceName != null) {
						driver = new RemoteWebDriver(hubURL, setChromeUserAgent(deviceName, userAgent));
					} else {
						logger.error("Given user agent configuration not yet implemented (or) check the parameters(deviceName) value in config.properties: " + deviceName);
					}
				} else {
					chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, opt);
					chromeCapabilities.setPlatform(Platform.fromString(platform));
					driver = new RemoteWebDriver(hubURL, chromeCapabilities);
				}
			} else if ("iexplorer".equalsIgnoreCase(browser)) {
				ieCapabilities.setCapability("enablePersistentHover", false);
				ieCapabilities.setCapability("ignoreZoomSetting", true);
				ieCapabilities.setCapability("nativeEvents", false);
				ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				ieCapabilities.setPlatform(Platform.fromString(platform));
				driver = new RemoteWebDriver(hubURL, ieCapabilities);
			} else if ("safari".equalsIgnoreCase(browser)) {
				driver = new RemoteWebDriver(hubURL, safariCapabilities);

			} else {
				synchronized (WebDriverFactory.class) {
					firefoxCapabilities.setCapability("unexpectedAlertBehaviour", "ignore");
					firefoxCapabilities.setPlatform(Platform.fromString(platform));
					driver = new RemoteWebDriver(hubURL, firefoxCapabilities);
				}
				driver.manage().timeouts().pageLoadTimeout(maxPageLoadWait, TimeUnit.SECONDS);
			}
			Assert.assertNotNull(driver, "Driver did not intialize...\n Please check if hub is running / configuration settings are corect.");

			if (!"ANDROID".equalsIgnoreCase(platform)) {
				driver.manage().window().maximize();
			}
		} catch (UnreachableBrowserException e) {
			e.printStackTrace();
			throw new SkipException("Hub is not started or down.");
		} catch (WebDriverException e) {

			try {
				if (driver != null) {
					driver.quit();
				}
			} catch (Exception e1) {
				e.printStackTrace();
			}

			if (e.getMessage().toLowerCase().contains("error forwarding the new session empty pool of vm for setup")) {
				throw new SkipException("Node is not started or down.");
			} else if (e.getMessage().toLowerCase().contains("error forwarding the new session empty pool of vm for setup") || e.getMessage().toLowerCase().contains("cannot get automation extension") || e.getMessage().toLowerCase().contains("chrome not reachable")) {
				Log.message("&emsp;<b> --- Re-tried as browser crashed </b>");
				try {
					driver.quit();
				} catch (WebDriverException e1) {
					e.printStackTrace();
				}
				driver = get();
			} else {
				throw e;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception encountered in getDriver Method." + e.getMessage().toString());
		} finally {
			// ************************************************************************************************************
			// * Update start time of the tests once free slot is assigned by
			// RemoteWebDriver - Just for reporting purpose
			// *************************************************************************************************************/
			try {
				Field f = Reporter.getCurrentTestResult().getClass().getDeclaredField("m_startMillis");
				f.setAccessible(true);
				f.setLong(Reporter.getCurrentTestResult(), Calendar.getInstance().getTime().getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Log.event("Driver::Get", StopWatch.elapsedTime(startTime));
		Log.addTestRunMachineInfo(driver);
		return driver;
	}

	/**
	 * Get the test session Node IP address,port when executing through Grid
	 */
	public static final String getTestSessionNodeIP(final WebDriver driver) throws Exception {

		XmlTest test = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest();
		driverHost = System.getProperty("hubHost") != null ? System.getProperty("hubHost") : test.getParameter("deviceHost");
		driverPort = test.getParameter("devicePort");
		HttpHost host = new HttpHost(driverHost, Integer.parseInt(driverPort));
		HttpClient client = HttpClientBuilder.create().build();
		URL testSessionApi = new URL("http://" + driverHost + ":" + driverPort + "/grid/api/testsession?session=" + ((RemoteWebDriver) driver).getSessionId());
		BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("POST", testSessionApi.toExternalForm());
		HttpResponse response = client.execute(host, r);
		JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
		String nodeIP = object.getString("proxyId").toLowerCase();
		nodeIP = nodeIP.replace("http://", "");
		nodeIP = nodeIP.replaceAll(":[0-9]{1,5}", "").trim();
		return nodeIP;
	}

	/**
	 * Get the test session Hub IP address,port when executing through Grid
	 */
	public static final String getHubSession(final WebDriver driver) throws Exception {

		XmlTest test = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest();
		driverHost = System.getProperty("hubHost") != null ? System.getProperty("hubHost") : test.getParameter("deviceHost");
		driverPort = test.getParameter("devicePort");
		HttpHost host = new HttpHost(driverHost, Integer.parseInt(driverPort));
		HttpClient client = HttpClientBuilder.create().build();
		URL testSessionApi = new URL("http://" + driverHost + ":" + driverPort + "/grid/api/testsession?session=" + ((RemoteWebDriver) driver).getSessionId());
		BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("POST", testSessionApi.toExternalForm());
		HttpResponse response = client.execute(host, r);
		JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
		String nodeIP = object.getString("proxyId").toLowerCase();
		nodeIP = nodeIP.replace("http://", "");
		nodeIP = nodeIP.replaceAll(":[0-9]{1,5}", "").trim();
		return nodeIP;
	}

	/**
	 * To storing chrome mobile emulation configurations(width, height,
	 * pixelRatio) and returning the capabilities
	 * 
	 * <p>
	 * if required feasible result then set andriodWidth, androidHeight,
	 * androidPixelRatio,iosWidth,androidHeight and iosPixelRatio values in the
	 * config.propeties
	 *
	 * @param userAgent
	 * @return chromeCapabilities
	 */
	public static DesiredCapabilities setChromeUserAgent(String deviceName, String userAgent) {
		Map<String, Object> deviceMetrics = new HashMap<String, Object>();
		Map<String, Object> mobileEmulation = new HashMap<String, Object>();

		int width = 0;
		int height = 0;
		Double pixRatio = null;

		width = Integer.valueOf(mobEmuUA.getDeviceWidth(deviceName));
		height = Integer.valueOf(mobEmuUA.getDeviceHeight(deviceName));
		pixRatio = Double.valueOf(mobEmuUA.getDevicePixelRatio(deviceName));

		deviceMetrics.put("width", width);
		deviceMetrics.put("height", height);
		deviceMetrics.put("pixelRatio", pixRatio);
		mobileEmulation.put("deviceMetrics", deviceMetrics);
		mobileEmulation.put("userAgent", userAgent);
		Log.event("mobileEmulation settings::==> " + mobileEmulation);
		opt.setExperimentalOption("mobileEmulation", mobileEmulation);
		chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, opt);
		return chromeCapabilities;
	}
}