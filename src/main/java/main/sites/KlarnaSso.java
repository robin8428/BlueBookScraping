package main.sites;

import static main.browser.filters.AttributeFilter.classWord;
import static main.browser.filters.AttributeFilter.hrefAttr;
import static main.browser.filters.AttributeFilter.idAttr;
import static main.browser.filters.AttributeFilter.or;
import static main.browser.filters.StringOperation.contains;

import main.browser.Browser;
import main.browser.elements.BaseElement;
import main.browser.filters.AttributeFilter;


public class KlarnaSso {

	private final Browser browser;

	public KlarnaSso(Browser browser) {
		this.browser = browser;
	}


	public boolean login(String username, String password) {
		browser.loadUrl("https://sso.klarna.net/");
		BaseElement usernameInput = browser.findElement(idAttr("okta-signin-username"));
		usernameInput.type(username);
		BaseElement passwordInput = browser.findElement(idAttr("okta-signin-password"));
		passwordInput.type(password);
		browser.findElement(idAttr("okta-signin-submit")).click();

		return isSuccess(otpFilter());
	}


	public boolean submitOtp(String otp) {
		BaseElement otpInput = browser.findElement(otpFilter());
		otpInput.type(otp);
		browser.findElement(classWord("button-primary")).click();

		return isSuccess(blueBookButtonFilter());
	}


	public void openBluebook() {
		browser.findElement(blueBookButtonFilter()).click();
	}


	private boolean isSuccess(AttributeFilter alternativeFilter) {
		return !browser.findElement(or(alternativeFilter, errorFilter())).attribute("class").contains("error");
	}


	private AttributeFilter blueBookButtonFilter() {
		return hrefAttr(contains("klarnabankab_bluebook"));
	}


	private AttributeFilter otpFilter() {
		return idAttr("input10");
	}


	private AttributeFilter errorFilter() {
		return classWord("okta-form-infobox-error");
	}
}
