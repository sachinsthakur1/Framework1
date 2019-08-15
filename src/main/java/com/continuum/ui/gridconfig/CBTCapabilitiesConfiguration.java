package com.continuum.ui.gridconfig;

import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Class to set the DesiredCapabilties when working with crossbrowsertesting.com
 *
 */
public class CBTCapabilitiesConfiguration implements IGridCapabilitiesConfiguration {
	private String testName;
	private String build;
	private String browserApiName;
	private String osApiName;
	private String screenResolution;
	private boolean recordVideo = false;
	private boolean recordNetwork = false;
	private boolean recordSnapshot = true;

	public CBTCapabilitiesConfiguration(String testName, String build, String browserApiName, String osApiName, String screenResolution) {
		this.testName = testName;
		this.build = build;
		this.browserApiName = browserApiName;
		this.osApiName = osApiName;
		this.screenResolution = screenResolution;

	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public String getBrowserApiName() {
		return browserApiName;
	}

	public void setBrowserApiName(String browserApiName) {
		this.browserApiName = browserApiName;
	}

	public String getOsApiName() {
		return osApiName;
	}

	public void setOsApiName(String osApiName) {
		this.osApiName = osApiName;
	}

	public String getScreenResolution() {
		return screenResolution;
	}

	public void setScreenResolution(String screenResolution) {
		this.screenResolution = screenResolution;
	}

	public boolean isRecordVideo() {
		return recordVideo;
	}

	public void setRecordVideo(boolean recordVideo) {
		this.recordVideo = recordVideo;
	}

	public boolean isRecordNetwork() {
		return recordNetwork;
	}

	public void setRecordNetwork(boolean recordNetwork) {
		this.recordNetwork = recordNetwork;
	}

	public boolean isRecordSnapshot() {
		return recordSnapshot;
	}

	public void setRecordSnapshot(boolean recordSnapshot) {
		this.recordSnapshot = recordSnapshot;
	}

	/**
	 * To set the desired capabilities for a browser
	 *
	 */
	public DesiredCapabilities getDesiredCapabilities() {
		DesiredCapabilities caps = new DesiredCapabilities();

		caps.setCapability("name", getTestName());
		caps.setCapability("build", getBuild());
		caps.setCapability("browser_api_name", getBrowserApiName());
		caps.setCapability("os_api_name", getOsApiName());
		caps.setCapability("screen_resolution", getScreenResolution());
		caps.setCapability("record_video", String.valueOf(isRecordVideo()));
		caps.setCapability("record_network", String.valueOf(isRecordNetwork()));
		caps.setCapability("record_snapshot", String.valueOf(isRecordSnapshot()));

		return caps;
	}
}
