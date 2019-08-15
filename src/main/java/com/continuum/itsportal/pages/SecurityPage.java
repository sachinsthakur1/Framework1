package com.continuum.itsportal.pages;


import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.continuum.common.utils.PortalUtils;
import com.continuum.utils.Log;

/**
 * HomePage consists realize component like Programs, Classes, Data, Centers and
 * Grades Realizer teacher and student can perform their action in this class
 * and view NBC subscription
 * 
 * @see <a href=
 *      "http://helpdev.pearsoncmg.com/realize/instructor/en/Help.htm#Instructor/home_page.htm%3FTocPath%3D_____3"
 *      >Home Page </a> for more info about Home page
 * 
 */
public class SecurityPage extends LoadableComponent<SecurityPage> {

	private final WebDriver driver;
	public TopNavBar topnav;
	public ElementLayer uielement;
	private boolean isPageLoaded;

	@FindBy(css = "._JHPuAOWQkve8QAU-6AsZ:nth-child(3)")
	WebElement lnkActivation;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_btnAddGroupedOrderTop:enabled")
	WebElement btnAddGroupedOrder;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_btnAddGroupedOrderTop:enabled")
	WebElement btnAddOrderAndReset;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_cboClient_Arrow")
	WebElement arrwClientDropdown;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_cboClient_DropDown")
	WebElement lstClientDropdown;
	
	//@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_cboBusinessType")
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_ctl00_ContentPlaceHolder1_ucOrder_cboBusinessTypePanel")
	WebElement arrwBusinessTypeDropdown;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_cboBusinessType")
	WebElement lstBusinessTypeDropdown;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_cboGoodsType_Arrow")
	WebElement arrwGoodsTypeDropdown;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_cboGoodsType_DropDown")
	WebElement lstGoodsTypeDropdown;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_txtLoadNumber")
	WebElement txtLoadNumber;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_txtDeliveryOrderNumber")
	WebElement txtOrderNumber;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_cboService_Arrow")
	WebElement arrwService;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_cboService_DropDown")
	WebElement lstService;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_rntxtPallets")
	WebElement txtPallets;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_rntxtPalletSpaces")
	WebElement txtSpaces;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_rntxtWeight")
	WebElement txtWeight;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_rntxtCartons")
	WebElement txtCases;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_cboPalletType_Arrow")
	WebElement arrwPalletType;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_cboPalletType_DropDown")
	WebElement lstPalletType;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_txtTrafficNotes")
	WebElement txtTrafficNote;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_ucCollectionPoint_cboPoint_Arrow")
	WebElement arrwCollectFromAddress;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_ucCollectionPoint_cboPoint_DropDown")
	WebElement lstCollectFromAddress;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_ucDeliveryPoint_cboPoint_Arrow")
	WebElement arrwDeliverToAddress;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_ucDeliveryPoint_cboPoint_DropDown")
	WebElement lstDeliverToAddress;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_rntOrderRate")
	WebElement txtRevenueRate;
	
	@FindBy(css = "#OrderID")
	WebElement txtFldOrderID;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_ucCollectionPoint_pnlFullAddress")
	WebElement txtFldCollectFromAddress;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_ucDeliveryPoint_pnlFullAddress")
	WebElement txtFldDeliverToAddress;
	
	@FindBy(css = "#ctl00_ContentPlaceHolder1_ucOrder_lblMissingDocumentsAlert")
	WebElement msgAlertClient;
	
	@FindBy(css = "#btnBookingForm")
	WebElement btnBookingFormOC;
	
	@FindBy(css = "#btnPodLabel")
	WebElement btnPodLabelOC;
	
	@FindBy(css = "#btnCreatePIL")
	WebElement btnCreatePILOC;
	
	@FindBy(css = "#btnCreateDeliveryNote")
	WebElement btnCreateDeliveryNoteOC;
	
	@FindBy(css = "[value='Close Window']")
	WebElement btnCloseWindowOC;
	
	@FindBy(css = "input#ctl00_ContentPlaceHolder1_ucOrder_chkCreateJob")
	WebElement chkbxCreateRun;
	
	@FindBy(css = ".buttonbar #ctl00_ContentPlaceHolder1_ucOrder_vsSubmit ul li")
	List<WebElement> lstErrorMsgs;
	
	
	/**
	 * constructor of the class
	 * 
	 * @param driver
	 */
	public SecurityPage(WebDriver driver) {

		this.driver = driver;
		ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, PortalUtils.minElementWait);
		PageFactory.initElements(finder, this);
	}

	@Override
	protected void isLoaded() {
		if (!isPageLoaded) {
			Assert.fail();
		}
		if (isPageLoaded && lnkActivation == null) {
			Log.fail("Add order page is not loaded", driver);
		}

		// Reinitialize globalNav on each load
		topnav = new TopNavBar(driver);
		uielement = new ElementLayer(driver);
	}

	@Override
	protected void load() {

		isPageLoaded = true;
		PortalUtils.waitForElement(driver, lnkActivation, 100);
	}
	
	public boolean verifyActivationLink() {
		Log.event("Verifying student name if 'Activate and Configure Offerings' link is visible");
		return PortalUtils.verifyWebElementTextEquals(lnkActivation, "Activate and Configure Offerings");
	}
	
	public boolean verifyDeactivationLink() {
		Log.event("Verifying student name if 'Deactivate and Configure Offerings' link is visible");
		return PortalUtils.verifyWebElementTextEquals(lnkActivation, "Deactivate and Configure Offerings");
	}
}