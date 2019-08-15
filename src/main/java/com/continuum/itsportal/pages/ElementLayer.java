package com.continuum.itsportal.pages;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.continuum.common.utils.PortalUtils;
import com.continuum.utils.Log;

/**
 * ElementLayer page is used to verify each page elements.
 * 
 * We can declare and initialize this class on each page object classes
 */
public class ElementLayer {

	private final WebDriver driver;

	/**
	 * 
	 * Constructor class for ElementLayer, here we initializing the driver for
	 * page
	 * 
	 * @param driver
	 */
	public ElementLayer(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * To verify the current page element presence depends on expectedElements
	 * list value/fields
	 * 
	 * <p>
	 * if expected element present on this current page then adds list of
	 * value/fields to actualElement list and then comparing both the lists. if
	 * both the lists are equal it will return true, else false
	 * 
	 * @param expectedElements
	 *            - expected element list value
	 * @return statusToBeReturned - returns true if both the lists are equal,
	 *         else returns false
	 * @throws Exception
	 */
	public boolean verifyPageElements(List<String> expectedElements, Object obj) throws Exception {
		boolean statusToBeReturned = false;
		List<String> actual_elements = new ArrayList<String>();
		for (String expEle : expectedElements) {
			Field f = null;
			try {
				f = obj.getClass().getDeclaredField(expEle);
			} catch (NoSuchFieldException | SecurityException e1) {
				throw new Exception("No such a field present on this page, Please check the expected list values:: " + expEle);
			}
			WebElement element = null;
			try {
				element = ((WebElement) f.get(obj));
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				Log.exception(e1);
			}
			if (PortalUtils.waitForElement(driver, element)) {
				actual_elements.add(expEle);
			}
		}
		statusToBeReturned = PortalUtils.compareTwoList(expectedElements, actual_elements);
		return statusToBeReturned;
	}
	
	/**
	 * To verify the current page element checked/selected depends on expectedElements
	 * list value/fields
	 * 
	 * <p>
	 * if expected element checked/selected on this current page then adds list of
	 * value/fields to actualElement list and then comparing both the lists. if
	 * both the lists are equal it will return true, else false
	 * 
	 * @param expectedElements
	 *            - expected element list value
	 * @return  returns true if both the lists are equal,
	 *         else returns false
	 * @throws Exception
	 */
	public boolean verifyPageElementsChecked(List<String> expectedElements, Object obj) throws Exception {
		boolean statusToBeReturned = false;
		List<String> actual_elements = new ArrayList<String>();
		for (String expEle : expectedElements) {
			Field f = null;
			try {
				f = obj.getClass().getDeclaredField(expEle);
			} catch (NoSuchFieldException | SecurityException e1) {
				throw new Exception("No such a field present on this page, Please check the expected list values:: " + expEle);
			}
			WebElement element = null;
			try {
				element = ((WebElement) f.get(obj));
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				Log.exception(e1);
			}
			(new WebDriverWait(driver, 5).pollingEvery(250, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class).withMessage("Creat an Event mobel box did not open")).until(ExpectedConditions.visibilityOf(element));
			if (element.isSelected()) {
				actual_elements.add(expEle);
			}
		}
		statusToBeReturned = PortalUtils.compareTwoList(expectedElements, actual_elements);
		return statusToBeReturned;
	}

    /**
     * To verify the lack of presence of a page element depends on 
     * expectedElements list value/fields
     * 
     * if expected element is NOT present on this current page then adds list of
     * value/fields to actualElement list and then comparing both the lists. if
     * both the lists are equal it will return true, else false
     * 
     * @param expectedNotToSee - expected non-existing elements list value
     * @param obj 
     * @return statusToBeReturned - returns true if both the lists are equal,
     *         else returns false
     * @throws Exception
     */
    public boolean verifyPageElementsDoNotExist(List<String> expectedNotToSee, Object obj) throws Exception {
        boolean statusToBeReturned = false;
        List<String> nonexisting_elements = new ArrayList<String>();
        for (String expEle : expectedNotToSee) {
            Field f = null;
            WebElement element = null;
            try {
                f = obj.getClass().getDeclaredField(expEle);
                element = ((WebElement) f.get(obj));
            } catch (NoSuchFieldException | SecurityException e1) {
                throw new Exception("No such field present on this page, Please check the expected list values:: " + expEle);
            }
            if (!PortalUtils.waitForElement(driver, element)) {
                nonexisting_elements.add(expEle);
            }
        }
        statusToBeReturned = PortalUtils.compareTwoList(expectedNotToSee, nonexisting_elements);
        return statusToBeReturned;
    }

	
	/**
	 * To verify the current page list elements presence depends on
	 * expectedElements list value/fields
	 * 
	 * <p>
	 * if size of the list element is greater than zero and first element from
	 * expected list elements present on this current page then adds list of
	 * value/fields to actualElement list and then comparing both the lists. if
	 * both the lists are equal it will return true, else false
	 * 
	 * @param expectedElements
	 *            - expected element list value
	 * @return statusToBeReturned - returns true if both the lists are equal,
	 *         else returns false
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public boolean verifyPageListElements(List<String> expectedElements, Object obj) throws Exception {
		boolean statusToBeReturned = false;
		List<String> actual_elements = new ArrayList<String>();
		for (String expEle : expectedElements) {
			Field f = null;
			try {
				f = obj.getClass().getDeclaredField(expEle);
			} catch (NoSuchFieldException | SecurityException e1) {
				throw new Exception("No such a field present on this page, Please check the expected list values:: " + expEle);
			}
			List<WebElement> element = null;
			try {
				element = ((List<WebElement>) f.get(obj));
			} catch (ClassCastException | IllegalArgumentException | IllegalAccessException e1) {
				Log.exception(e1);
			}
			if (element.size()>0 && PortalUtils.waitForElement(driver, element.get(0))) {
				actual_elements.add(expEle);
			}
		}
		statusToBeReturned = PortalUtils.compareTwoList(expectedElements, actual_elements);
		return statusToBeReturned;
	}

}
