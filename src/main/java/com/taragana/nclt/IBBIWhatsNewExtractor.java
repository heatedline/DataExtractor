package com.taragana.nclt;

import com.taragana.nclt.model.IBBINewContentData;
import com.taragana.nclt.utils.FileDownloadUtil;
import com.taragana.nclt.utils.OpenCSVUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;


/**
 * The main class where the entire operation of data extraction from Insolvency and Bankruptcy Board of India site will take place
 *
 * @Author Supratim
 */
public class IBBIWhatsNewExtractor extends SeleniumBase {

    private static final String IBBI_URL = "https://ibbi.gov.in/webfront/whatsnew.php";

    /**
     * The main method
     *
     * @param args java standard
     */
    public static void main(String[] args) {

        IBBIWhatsNewExtractor ibbiWhatsNewExtractor = new IBBIWhatsNewExtractor();
        ibbiWhatsNewExtractor.setupChromeDriver(true);
        try {
            ibbiWhatsNewExtractor.extractFromPage();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Extracting required data from the given homepage.
     */
    private void extractFromPage() throws Exception {

        driver.get(IBBI_URL);

        waitForElementToAppear(By.name("max_form_per_page"));

        Select dropMaxPerPage = new Select(driver.findElement(By.name("max_form_per_page")));
        dropMaxPerPage.selectByVisibleText("50");

        Thread.sleep(10000);

        //extractionJob(paginateCount, count);

        recursivePaginate();

        Thread.sleep(10000);
        System.out.println("Ending process....");
        shutdown();

    }

    /**
     * Method to implement the automation for Pagination in this particular required website
     */
    private void recursivePaginate() {

        JavascriptExecutor executor = (JavascriptExecutor) driver;

        new FluentWait<>(driver).withTimeout(300, TimeUnit.SECONDS).pollingEvery(5, TimeUnit.SECONDS)
                .ignoring(StaleElementReferenceException.class)
                .until((Function) arg0 -> {
                    /*waitForElementToAppear(By.className("LinkBL1"));
                    List<WebElement> paginationList = driver.findElements(By.className("LinkBL1"));
                    for (WebElement pagination : paginationList) {
                        if (isElementPresent(By.xpath("//*[@id=\"pagingCont\"]/a[" + paginateCount + "]"))) {
                            driver.findElement(By.xpath("//*[@id=\"pagingCont\"]/a[" + paginateCount + "]")).click();
                            try {
                                Thread.sleep(5000);
                                executor.executeScript("window.scrollTo(0, document.body.scrollHeight)");
                                extractionJob(paginateCount, count);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            break;
                        }

                        paginateCount++;

                        if (paginateCount >= 11) {
                            System.out.println(driver.findElement(By.xpath("//*[@id=\"pagingCont\"]/span[2]/b")).getText() + " .... " + count);
                            if (driver.findElement(By.xpath("//*[@id=\"pagingCont\"]/span[2]/b")).getText().equals(String.valueOf(11 + count))) {
                                paginateCount = 2;
                                count = count + 10;
                                recursivePaginate();
                            }
                        }
                    }*/

                    return true;
                });
    }

    /**
     * Method where the entire extraction of data is taking place along with creating respective CSV files with data, and also downloading required PDF files.
     *
     * @param paginateIndex
     * @param paginateSetCount
     * @throws Exception
     */
    private void extractionJob(int paginateIndex, int paginateSetCount) {

        JavascriptExecutor executor = (JavascriptExecutor) driver;

        waitForElementToAppear(By.className("col-sm-12"));
        waitForElementToAppear(By.className("catageries"));

        WebElement divCategories = driver.findElement(By.className("catageries"));

        List<WebElement> childAnchorList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(divCategories, By.tagName("a")));

        new FluentWait<>(driver).withTimeout(300, TimeUnit.SECONDS).pollingEvery(5, TimeUnit.SECONDS)
                .ignoring(StaleElementReferenceException.class)
                .until((Function) arg0 -> {

                    /*List<IBBINewContentData> ibbiNewContentDataList = new ArrayList<>();

                    for (WebElement childAnchor : childAnchorList) {
                        if (!childAnchor.getText().chars().allMatch(Character::isDigit)) {
                            IBBINewContentData ibbiNewContentData = new IBBINewContentData();
                            ibbiNewContentData.setId(String.valueOf(contentCount));
                            ibbiNewContentData.setContent(childAnchor.getText());

                            ibbiNewContentDataList.add(ibbiNewContentData);

                            System.out.println("Content " + contentCount + " added. List size: " + ibbiNewContentDataList.size());

                            contentCount++;
                        }
                    }

                    try {
                        System.out.println("Writing to CSV File.....");
                        OpenCSVUtil.writeToIBBINewContentsCSV("IBBIWhatsNew_File_" + paginateIndex, ibbiNewContentDataList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    for (WebElement childAnchor : childAnchorList) {
                        if (!childAnchor.getText().chars().allMatch(Character::isDigit)) {
                            executor.executeScript("arguments[0].click();", childAnchor);

                            if (!new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "IBBINewContent" + File.separator + paginateIndex).exists()) {
                                new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "IBBINewContent" + File.separator + paginateIndex).mkdirs();
                            }

                            String parentWindow = driver.getWindowHandle();
                            Set<String> handles = driver.getWindowHandles();
                            for (String windowHandle : handles) {
                                if (!windowHandle.equals(parentWindow)) {
                                    driver.switchTo().window(windowHandle);
                                    try {
                                        Thread.sleep(5000);
                                        if (driver.getCurrentUrl().contains("pdf")) {
                                            FileDownloadUtil.downloadFile(executor.executeScript("return document.URL;").toString(), System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "IBBINewContent" + File.separator + paginateIndex);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    driver.close(); //closing child window
                                    driver.switchTo().window(parentWindow); //cntrl to parent window
                                }
                            }
                        }
                    }*/

                    return true;
                });

    }



}
