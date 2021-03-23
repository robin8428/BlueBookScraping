package main.sites;

import static main.browser.filters.AttributeFilter.and;
import static main.browser.filters.AttributeFilter.classWord;
import static main.browser.filters.AttributeFilter.idAttr;
import static main.browser.filters.AttributeFilter.name;
import static main.browser.filters.AttributeFilter.text;
import static main.browser.filters.StringOperation.contains;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import main.browser.Browser;
import main.browser.elements.BaseElement;
import main.util.Date;


public class BlueBook {

	private static final Logger LOG = LogManager.getLogger();

	private final Browser browser;

	private int oldestIndex;
	private int newestIndex;

	public BlueBook(Browser browser) {
		this.browser = browser;
		oldestIndex = -1;
		newestIndex = -1;
	}


	public void toTimesheets() {
		browser.setTimeout(60);
		browser.findElement(idAttr("wx-header__title"));
		browser.setTimeout(10);
		browser.findElement(idAttr("category_tabs")).findElement(name("a", text(contains("Timesheets")))).simulatedClick();
		browser.findElement(and(classWord("long-label"), text(contains("Approved")))).click();
	}


	private void findOldestAndNewestIndex(String year) {
		List<BaseElement> sheetContainers = getSheetContainers();

		for (int i = 0; i < sheetContainers.size(); i++) {
			BaseElement e = sheetContainers.get(i);
			String sheetDate = e.findElements(name("td", classWord("ng-binding"))).get(0).text();

			if (sheetDate.contains(year)) {
				if (newestIndex < 0) {
					newestIndex = i;
				}
				oldestIndex = i;
			}
		}
		LOG.debug("newest index: " + newestIndex);
		LOG.debug("oldest index: " + oldestIndex);
		LOG.debug("total elements: " + sheetContainers.size());
	}


	public Date calculateOverhours(String year) {
		String yearBefore = Integer.toString(Integer.parseInt(year) - 1);
		findOldestAndNewestIndex(year);

		Date totalDate = new Date();

		new BaseElement(browser.findElement(By.xpath(".//div[contains(@ng-show,'processItem.data.approved.length')]/div/div[" + (newestIndex + 1) + "]//button")), browser).simulatedClick();
		String currentLabel = year;

		while (currentLabel.contains(year)) {
			String hourString = browser.findElement(classWord("wx-timesheet-overview-details__summary-hours")).nonBlankText();
			Date sheetDate = parseDateString(hourString);
			totalDate.addDate(sheetDate);
			currentLabel = browser.findElement(classWord("wx-timesheet-navigation-control__label")).nonBlankText();

			LOG.trace("[" + currentLabel + "]: " + hourString);

			BaseElement prevButton = browser.findElement(idAttr("timesheet-navigation-button-previous"));

			if (currentLabel.contains(yearBefore) || prevButton == null) {
				break;
			}
			prevButton.click();
		}

		return totalDate;
	}


	private List<BaseElement> getSheetContainers() {
		return browser.findElements(classWord("objectives-table"));
	}


	private Date parseDateString(String hourString) {
		hourString = hourString.replaceAll("\\s", "");
		int hours = Integer.parseInt(hourString.replaceAll("(\\d+)\\D+\\d+\\D+", "$1"));
		int minutes = Integer.parseInt(hourString.replaceAll("\\d+\\D+(\\d+)\\D+", "$1"));

		return new Date(0, hours, minutes);
	}
}
