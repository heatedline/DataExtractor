package com.taragana.nclt;

import com.taragana.nclt.model.IBBIAnnouncementData;
import com.taragana.nclt.utils.Data;
import com.taragana.nclt.utils.FileDownloadUtil;
import com.taragana.nclt.utils.OpenCSVUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * The main class where the entire operation of data extraction from Insolvency and Bankruptcy Board of India site will take place
 *
 * @Author Supratim
 */
public class IBBIExtractor extends SeleniumBase {

    private static final String IBBI_URL = "https://ibbi.gov.in/webfront/public_announcement.php";

    private static int announcementCount = 1;
    private static int count = 0;
    private static int tableRowCount = 1;
    private static int paginateCount = 1;


    /**
     * The main method
     *
     * @param args java standard
     */
    public static void main(String[] args) {


        IBBIExtractor ibbiExtractor = new IBBIExtractor();
        try {
            ibbiExtractor.extractFromPage();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Extracting required data from the given homepage.
     */
    private void extractFromPage() throws Exception {

        driver.get(IBBI_URL);

        for (String announcement : Data.announcementList) {
            operation(announcement, announcementCount);
            driver.navigate().refresh();
            announcementCount++;
        }

        Thread.sleep(10000);
        System.out.println("Ending process....");
        shutdown();

    }

    /**
     * The method where we are doing the functional operations to extract data
     *
     * @param announcement      Input announcement with which the list will be shown
     * @param announcementCount Distinguishing between same files with announcementCount
     */
    private void operation(String announcement, int announcementCount) throws Exception {

        waitForElementToAppear(By.name("section"));

        Select dropAnnouncement = new Select(driver.findElement(By.name("section")));
        dropAnnouncement.selectByVisibleText(announcement);

        List<WebElement> buttonElements = driver.findElements(By.tagName("button"));
        for (WebElement buttonElement : buttonElements) {
            if (buttonElement.getText().equals("Submit")) {
                buttonElement.click();
                break;
            }
        }

        waitForElementToAppear(By.name("max_form_per_page"));

        Select dropMaxPerPage = new Select(driver.findElement(By.name("max_form_per_page")));
        dropMaxPerPage.selectByVisibleText("50");

        Thread.sleep(10000);

        extractionJob(paginateCount, count);

        recursivePaginate();

    }

    /**
     * Method to implement the automation for Pagination in this particular required website
     */
    private void recursivePaginate() {

        JavascriptExecutor executor = (JavascriptExecutor) driver;

        new FluentWait<>(driver).withTimeout(300, TimeUnit.SECONDS).pollingEvery(5, TimeUnit.SECONDS)
                .ignoring(StaleElementReferenceException.class)
                .until((Function) arg0 -> {
                    waitForElementToAppear(By.className("LinkBL1"));
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
                    }

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
    private void extractionJob(int paginateIndex, int paginateSetCount) throws Exception {

        JavascriptExecutor executor = (JavascriptExecutor) driver;

        waitForElementToAppear(By.id("mainContainer"));

        WebElement tableBody = driver.findElement(By.xpath("//*[@id=\"mainContainer\"]/table/tbody"));

        List<WebElement> tableRowList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableBody, By.tagName("tr")));

        System.out.println("TableRowList size: " + tableRowList.size());

        List<WebElement> pdfImageIconList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableBody, By.tagName("img")));

        new FluentWait<>(driver).withTimeout(300, TimeUnit.SECONDS).pollingEvery(5, TimeUnit.SECONDS)
                .ignoring(StaleElementReferenceException.class)
                .until((Function) arg0 -> {

                    List<IBBIAnnouncementData> ibbiAnnouncementDataList = new ArrayList<>();

                    for (WebElement tableRow : tableRowList) {

                        if (tableRow.isDisplayed()) {
                            IBBIAnnouncementData ibbiAnnouncementData = new IBBIAnnouncementData();
                            ibbiAnnouncementData.setId(driver.findElement(By.xpath("//*[@id=\"mainContainer\"]/table/tbody/tr[" + tableRowCount + "]/td[1]")).getText());
                            ibbiAnnouncementData.setDateOfAnnouncement(driver.findElement(By.xpath("//*[@id=\"mainContainer\"]/table/tbody/tr[" + tableRowCount + "]/td[2]")).getText());
                            ibbiAnnouncementData.setLastDateOfSubmission(driver.findElement(By.xpath("//*[@id=\"mainContainer\"]/table/tbody/tr[" + tableRowCount + "]/td[3]")).getText());
                            ibbiAnnouncementData.setCorporateDebtorName(driver.findElement(By.xpath("//*[@id=\"mainContainer\"]/table/tbody/tr[" + tableRowCount + "]/td[4]")).getText());
                            ibbiAnnouncementData.setApplicantName(driver.findElement(By.xpath("//*[@id=\"mainContainer\"]/table/tbody/tr[" + tableRowCount + "]/td[5]")).getText());
                            ibbiAnnouncementData.setInsolvencyProfessionalName(driver.findElement(By.xpath("//*[@id=\"mainContainer\"]/table/tbody/tr[" + tableRowCount + "]/td[6]")).getText());
                            ibbiAnnouncementData.setInsolvencyProfessionalAddress(driver.findElement(By.xpath("//*[@id=\"mainContainer\"]/table/tbody/tr[" + tableRowCount + "]/td[7]")).getText());
                            ibbiAnnouncementData.setRemarks(driver.findElement(By.xpath("//*[@id=\"mainContainer\"]/table/tbody/tr[" + tableRowCount + "]/td[9]")).getText());

                            ibbiAnnouncementDataList.add(ibbiAnnouncementData);

                            System.out.println("Row " + tableRowCount + " added. Model list size: " + ibbiAnnouncementDataList.size());

                        }

                        tableRowCount++;
                    }

                    try {
                        System.out.println("Writing to CSV File.....");
                        OpenCSVUtil.writeToIBBIAnnouncementCSV("IBBIMain_File_" + announcementCount + "_" + paginateIndex + "_" + paginateSetCount, ibbiAnnouncementDataList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    for (WebElement pdfImageIcon : pdfImageIconList) {

                        WebElement parentElement = (WebElement) executor.executeScript("return arguments[0].parentNode;", pdfImageIcon);
                        executor.executeScript("arguments[0].click();", parentElement);

                        if (!new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + announcementCount).exists()) {
                            new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + announcementCount).mkdirs();
                        }

                        String parentWindow = driver.getWindowHandle();
                        Set<String> handles = driver.getWindowHandles();
                        for (String windowHandle : handles) {
                            if (!windowHandle.equals(parentWindow)) {
                                driver.switchTo().window(windowHandle);
                                try {
                                    Thread.sleep(5000);
                                    if (driver.getCurrentUrl().contains("pdf")) {
                                        FileDownloadUtil.downloadFile(executor.executeScript("return document.URL;").toString(), System.getProperty("user.dir") + File.separator + "Docs" + File.separator + announcementCount);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                driver.close(); //closing child window
                                driver.switchTo().window(parentWindow); //cntrl to parent window
                            }
                        }
                    }

                    return true;
                });

        tableRowCount = 1;

    }


}
