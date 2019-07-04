package com.taragana.nclt;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SeleniumBase {

    protected static final Logger l = Logger.getGlobal();
    private static final int TIMEOUT = 180;
    private static final int POLLING = 1;
    private static boolean isSSLBypassed = false;

    static {
        bypassSSLSecurityChecks();
    }

    protected WebDriver driver;
    protected FluentWait wait;

    /**
     * Bypasses all SSL security checks. Call it first thing in public static void main(String[] args).
     * Use only when absolutely necessary with sites you trust over a ISP you trust because bypassing SSL security features will make your application vulnerable to man-in-the-middle-attacks.
     * Two valid use cases are:
     * - Internal sites with self-generated certificate
     * - Sites whose SSL/TLS certificate has problems and you cannot get the Site admins to rectify them in time
     *
     * @return whether the effort succeeded
     */
    private static boolean bypassSSLSecurityChecks() {
        if (isSSLBypassed) return true;
        System.setProperty("jsse.enableSNIExtension", "false");

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            isSSLBypassed = true;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            l.severe(e::getMessage);
        }
        return isSSLBypassed;
    }

    /**
     * Setup chromedriver. chromedriver should be in path.
     * @param headlessFlag flag check to maintain headless mode on/off for chromedriver.
     */
    protected void setupChromeDriver(boolean headlessFlag) {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\heatedline\\Documents\\ChromeDriver\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(headlessFlag);
        options.setAcceptInsecureCerts(true);
        options.addArguments("--log-level=SEVERE");
        //options.addArguments("start-maximized");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(1, TimeUnit.MINUTES);
        driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
        wait = new FluentWait(driver).withTimeout(Duration.ofSeconds(TIMEOUT)).pollingEvery(Duration.ofSeconds(POLLING)).ignoring(NoSuchFieldException.class);
    }

    /**
     * Method to wait for a certain WebElement to be visible in screen
     *
     * @param locator The locator to locate the element
     */
    protected void waitForElementToAppear(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Method to shutdown the ChromeDriver
     *
     * @throws Exception
     */
    public void shutdown() throws Exception {
        System.out.println("Shutting down......");
        driver.quit();
        driver = null;
        wait = null;
    }

    /**
     * Method to check if WebElement is present on the page or not and to avoid NoSuchElementException
     *
     * @param by The locator to locate the element
     * @return
     */
    protected boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
