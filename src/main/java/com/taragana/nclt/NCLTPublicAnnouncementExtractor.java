package com.taragana.nclt;

import com.taragana.nclt.model.NCLTPublicAnnouncement;
import com.taragana.nclt.utils.DBConnection;
import com.taragana.nclt.utils.FileDownloadUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The main class where the entire operation of Public Announcements Extraction takes place from IBBI(Under NCLT) web site.
 *
 * @Author Supratim
 */
public class NCLTPublicAnnouncementExtractor extends SeleniumBase {

    private static final String NCLT_PUBLIC_ANNOUNCEMENT_URL = "https://ibbi.gov.in/public-announcement";

    /**
     * The main method
     *
     * @param args java standard
     */
    public static void main(String[] args) {

        NCLTPublicAnnouncementExtractor ncltPublicAnnouncementExtractor = new NCLTPublicAnnouncementExtractor();
        ncltPublicAnnouncementExtractor.setupChromeDriver(false);
        String lastDate = System.getProperty("toDate");
        //String lastDate = "01/03/2019";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        try {
            ncltPublicAnnouncementExtractor.extractFromPage(formatter.parse(lastDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracting required data from the given homepage.
     * @param lastDate Input lastDateOfSubmission for showing results accordingly
     */
    private void extractFromPage(java.util.Date lastDate) throws Exception {

        driver.get(NCLT_PUBLIC_ANNOUNCEMENT_URL);

        List<String> announcementList = new ArrayList<>();

        waitForElementToAppear(By.name("ann"));

        Select dropAnnouncement = new Select(driver.findElement(By.name("ann")));
        List<WebElement> announcementDropDownList = new ArrayList<>(dropAnnouncement.getOptions());
        for(WebElement we : announcementDropDownList) {
            if(!we.getText().equalsIgnoreCase("select")) {
                announcementList.add(we.getText());
            }
        }

        Calendar c1 = Calendar.getInstance();
        c1.setTime(lastDate);

        String lastDateInput;

        if((c1.get(Calendar.MONTH)+1) < 10) {
            if(c1.get(Calendar.DAY_OF_MONTH) < 10) {
                lastDateInput = c1.get(Calendar.YEAR) + "/0" + (c1.get(Calendar.MONTH)+1) + "/0" + c1.get(Calendar.DAY_OF_MONTH);
            } else {
                lastDateInput = c1.get(Calendar.YEAR) + "/0" + (c1.get(Calendar.MONTH)+1) + "/" + c1.get(Calendar.DAY_OF_MONTH);
            }
        } else {
            if(c1.get(Calendar.DAY_OF_MONTH) < 10) {
                lastDateInput =  c1.get(Calendar.YEAR) + "/" + (c1.get(Calendar.MONTH)+1) + "/0" + c1.get(Calendar.DAY_OF_MONTH);
            } else {
                lastDateInput = c1.get(Calendar.YEAR) + "/" + (c1.get(Calendar.MONTH)+1) + "/" + c1.get(Calendar.DAY_OF_MONTH);
            }
        }

        System.out.println("lastDateInput: " + lastDateInput);

        WebElement dateElement = driver.findElement(By.name("date"));
        dateElement.sendKeys(lastDateInput);

        for(String announcementDesc : announcementList) {
            operation(announcementDesc);
        }

        System.out.println("Ending process....");
        shutdown();
    }

    /**
     * The method where we are doing the functional operations to extract data
     *
     * @param announcementDesc Input announcement description with which the list will be shown
     * @throws Exception
     */
    private void operation(String announcementDesc) throws Exception {

        System.out.print("Processing " + announcementDesc + " data....");

        waitForElementToAppear(By.name("ann"));

        Select dropAnnouncement = new Select(driver.findElement(By.name("ann")));
        dropAnnouncement.selectByVisibleText(announcementDesc);

        WebElement applyButton = driver.findElement(By.xpath("//*[@id=\"myFormId\"]/div/div[4]/div/input"));
        applyButton.click();

        if(isElementPresent(By.xpath("//*[@id=\"block-ibbi-content\"]/div/div/nav/p"))) {
            WebElement totalRecordsElement = driver.findElement(By.xpath("//*[@id=\"block-ibbi-content\"]/div/div/nav/p"));
            System.out.println("There are total " + totalRecordsElement + " records for " + announcementDesc);
        }

        extractionJob(announcementDesc);

        if(isElementPresent(By.className("pager")))
            recursivePaginate(announcementDesc);

    }


    /**
     * Method to implement the automation for Pagination in this particular required website
     * @param announcementDesc Input announcement description with which the list will be shown
     * @throws Exception
     */
    private void recursivePaginate(String announcementDesc) throws Exception {

        JavascriptExecutor executor = (JavascriptExecutor) driver;

        waitForElementToAppear(By.className("pager"));

        List<WebElement> anchorListInPager = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(driver.findElement(By.className("pagination")), By.tagName("a")));

        for(WebElement anchorElementInPager : anchorListInPager) {
            if(anchorElementInPager.getAttribute("rel").equalsIgnoreCase("next")) {
                executor.executeScript("window.scrollTo(0, document.body.scrollHeight)");
                Thread.sleep(5000);
                anchorElementInPager.click();
                extractionJob(announcementDesc);
                Thread.sleep(5000);
                recursivePaginate(announcementDesc);
            }
        }

    }

    /**
     * Method where the entire extraction of data is taking place.
     * @param announcementDesc Input announcement description with which the list will be shown
     * @throws Exception
     */
    private void extractionJob(String announcementDesc) throws Exception {

        JavascriptExecutor executor = (JavascriptExecutor) driver;

        waitForElementToAppear(By.className("table-responsive"));

        WebElement tableBody = driver.findElement(By.xpath("//*[@id=\"block-ibbi-content\"]/div/div/div[2]/table/tbody"));

        List<WebElement> tableRowList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableBody, By.tagName("tr")));

        for(WebElement tableRow : tableRowList) {

            List<WebElement> tableDataList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableRow, By.tagName("td")));

            NCLTPublicAnnouncement ncltPublicAnnouncement = new NCLTPublicAnnouncement();

            ncltPublicAnnouncement.setAnnouncementDescription(announcementDesc);

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            try {
                java.util.Date dateOfAnnouncement = formatter.parse(tableDataList.get(0).getText());
                java.util.Date lastDateOfSubmission = formatter.parse(tableDataList.get(1).getText());
                ncltPublicAnnouncement.setDateOfAnnouncement(dateOfAnnouncement);
                ncltPublicAnnouncement.setLastDateOfSubmission(lastDateOfSubmission);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ncltPublicAnnouncement.setCorporateDebtorName(tableDataList.get(2).getText());
            ncltPublicAnnouncement.setApplicantName(tableDataList.get(3).getText());
            ncltPublicAnnouncement.setInsolvencyProfessionalName(tableDataList.get(4).getText());
            ncltPublicAnnouncement.setInsolvencyProfessionalAddress(tableDataList.get(5).getText());

            List<WebElement> anchorPDFLink = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableDataList.get(6), By.tagName("a")));

            if (!new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLTPublicAnnouncement").exists()) {
                new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLTPublicAnnouncement").mkdirs();
            }

            executor.executeScript("arguments[0].click();", anchorPDFLink.get(0));

            //for proper working of this run in setHeadless(false) mode of ChromeDriver
            String parentWindow = driver.getWindowHandle();
            Set<String> handles = driver.getWindowHandles();
            for (String windowHandle : handles) {
                if (!windowHandle.equals(parentWindow)) {
                    driver.switchTo().window(windowHandle);
                    try {
                        Thread.sleep(5000);
                        if (driver.getCurrentUrl().contains("pdf")) {
                            System.out.println(driver.getCurrentUrl());
                            FileDownloadUtil.downloadFile(executor.executeScript("return document.URL;").toString(), System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLTPublicAnnouncement");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    driver.close(); //closing child window
                    driver.switchTo().window(parentWindow); //cntrl to parent window
                }
            }

            ncltPublicAnnouncement.setPdfFileName(FileDownloadUtil.FILE_NAME);
            ncltPublicAnnouncement.setRemark(tableDataList.get(7).getText());

            int id = insertNCLATPublicAnnouncementList(ncltPublicAnnouncement);

            System.out.println(String.format("A new record with id %d has been inserted.", id));

        }

    }

    /**
     * Method to insert Model data into database using JDBC PreparedStatement
     *
     * @param ncltPublicAnnouncement Model data of NCLTPublicAnnouncement
     * @return id of the record inserted
     */
    private int insertNCLATPublicAnnouncementList(NCLTPublicAnnouncement ncltPublicAnnouncement) {

        ResultSet rs = null;
        int id = 0;

        String sql = "INSERT INTO nclt_announcement(announcementDesc,dateOfAnnouncement,lastDateOfSubmission,corporateDebtorName,applicantName,insolvencyProfessionalName,insolvencyProfessionalAddress,pdfFileName,remark) "
                + "VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ncltPublicAnnouncement.getAnnouncementDescription());
            pstmt.setDate(2, new java.sql.Date(ncltPublicAnnouncement.getDateOfAnnouncement().getTime()));
            pstmt.setDate(3, new java.sql.Date(ncltPublicAnnouncement.getLastDateOfSubmission().getTime()));
            pstmt.setString(4, ncltPublicAnnouncement.getCorporateDebtorName());
            pstmt.setString(5, ncltPublicAnnouncement.getApplicantName());
            pstmt.setString(6, ncltPublicAnnouncement.getInsolvencyProfessionalName());
            pstmt.setString(7, ncltPublicAnnouncement.getInsolvencyProfessionalAddress());
            pstmt.setString(8, ncltPublicAnnouncement.getPdfFileName());
            pstmt.setString(9, ncltPublicAnnouncement.getRemark());

            int rowAffected = pstmt.executeUpdate();
            if (rowAffected == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return id;

    }



}
