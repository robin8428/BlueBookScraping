package main;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import main.browser.Browser;
import main.sites.BlueBook;
import main.sites.KlarnaSso;
import main.util.Date;
import main.util.Properties;


public class Launcher {

	private static final Logger LOG = LogManager.getLogger();

	public static void main(String[] args) {
		String year = "2020";
		if (args != null && args.length == 1) {
			year = args[0];
		}

		FirefoxOptions options = new FirefoxOptions();
		options.setHeadless(true);
		setDriverPath();
		WebDriver driver = new FirefoxDriver(options);
		Properties prop = new Properties();

		Browser browser = new Browser(driver);
		KlarnaSso sso = new KlarnaSso(browser);
		BlueBook blueBook = new BlueBook(browser);

		try {
			boolean success = sso.login(prop.getProperty("username"), prop.getProperty("password"));
			if (!success) {
				throw new IllegalStateException("login failed due to invalid credentials");
			}

			boolean otpSuccess = false;
			while (!otpSuccess) {
				String otp = JOptionPane.showInputDialog("please enter authenticator code for sso.klarna.net");
				otpSuccess = sso.submitOtp(otp);
			}

			sso.openBluebook();

			browser.switchTab(1);
			blueBook.toTimesheets();
			Date overhours = blueBook.calculateOverhours(year);

			LOG.info("total overhours for year " + year + ": " + overhours.getDays() + " working days, " + overhours.getHours() + " hours, " + overhours.getMinutes() + " minutes");
		} finally {
			driver.quit();
		}
	}


	private static String setDriverPath() {
		String path;
		if (System.getProperty("os.name").startsWith("Windows")) {
			path = "drivers/geckodriver.exe";
		} else {
			path = "drivers/geckodriver";
		}
		System.setProperty("webdriver.gecko.driver", path);
		return path;
	}
}
