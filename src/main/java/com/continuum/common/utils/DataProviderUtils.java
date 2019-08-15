package com.continuum.common.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import com.continuum.utils.EnvironmentPropertiesReader;

public class DataProviderUtils {
	private static EnvironmentPropertiesReader configProperty = EnvironmentPropertiesReader.getInstance();

	@DataProvider(parallel = true)
	public static Iterator<Object[]> realizeBVTDataProvider(ITestContext context) throws IOException {

		List<Object[]> dataToBeReturned = new ArrayList<Object[]>();
		List<String> browsers = null;
		List<String> platforms = null;
		List<String> browserVersions = null;
		List<String> deviceNames = null;
		String driverInitilizeInfo = null;
		String browser = null;
		String platform = null;
		String browserVersion = null;
		String deviceName = null;
		Iterator<String> browsersIt = null;
		Iterator<String> browserVersionsIt = null;
		Iterator<String> platformsIt = null;
		Iterator<String> deviceNameIt = null;

		// From local to sauce lab for browser test
		if (configProperty.hasProperty("runSauceLabFromLocal") && configProperty.getProperty("runSauceLabFromLocal").equalsIgnoreCase("true")) {
			browser = configProperty.hasProperty("browserName") ? configProperty.getProperty("browserName") : null;
			browserVersion = configProperty.hasProperty("browserVersion") ? configProperty.getProperty("browserVersion") : null;
			platform = configProperty.hasProperty("platform") ? configProperty.getProperty("platform") : null;

			browsers = Arrays.asList(browser.split("\\|"));
			browserVersions = Arrays.asList(browserVersion.split("\\|"));
			platforms = Arrays.asList(platform.split("\\|"));

			browsersIt = browsers.iterator();
			browserVersionsIt = browserVersions.iterator();
			platformsIt = platforms.iterator();

			// From local to sauce lab for device test
			if (configProperty.hasProperty("runUserAgentDeviceTest") && configProperty.getProperty("runUserAgentDeviceTest").equalsIgnoreCase("true")) {

				// handling parallel device test inputs
				deviceName = configProperty.hasProperty("deviceName") ? configProperty.getProperty("deviceName") : null;
				deviceNames = Arrays.asList(deviceName.split("\\|"));
				deviceNameIt = deviceNames.iterator();

				while (browsersIt.hasNext() && browserVersionsIt.hasNext() && platformsIt.hasNext() && deviceNameIt.hasNext()) {
					browser = browsersIt.next();
					browserVersion = browserVersionsIt.next();
					platform = platformsIt.next();
					deviceName = deviceNameIt.next();
					driverInitilizeInfo = browser + "&" + browserVersion + "&" + platform + "&" + deviceName;
					dataToBeReturned.add(new Object[] { driverInitilizeInfo });
				}
			} else {
				// handling parallel browser test inputs
				while (browsersIt.hasNext() && browserVersionsIt.hasNext() && platformsIt.hasNext()) {
					browser = browsersIt.next();
					browserVersion = browserVersionsIt.next();
					platform = platformsIt.next();
					driverInitilizeInfo = browser + "&" + browserVersion + "&" + platform;
					dataToBeReturned.add(new Object[] { driverInitilizeInfo });
				}
			}

		} else {
			// local to local test execution and also handling parallel support
			browsers = Arrays.asList(context.getCurrentXmlTest().getParameter("browserName").split(","));
			for (String b : browsers) {
				dataToBeReturned.add(new Object[] { b });
			}
		}
		return dataToBeReturned.iterator();
	}
}