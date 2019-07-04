package com.taragana.nclt;


import com.taragana.nclt.model.NCLTJudgement;
import com.taragana.nclt.utils.DBConnection;
import com.taragana.nclt.utils.Data;
import com.taragana.nclt.utils.FileDownloadUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * The main class where the entire operation of Judgements Extraction takes place from NCLT web site.
 *
 * @Author Supratim
 */
public class NCLTJudgementsExtractor extends SeleniumBase {

    private static final String NCLT_JUDGEMEMTS_URL = "https://nclt.gov.in/exposed-judgement-date-wise-page";

    /**
     * The main method
     *
     * @param args java standard
     */
    public static void main(String[] args) {

        NCLTJudgementsExtractor ncltJudgementsExtractor = new NCLTJudgementsExtractor();
        ncltJudgementsExtractor.setupChromeDriver(true);
        String startDate = System.getProperty("fromDate");
        String endDate = System.getProperty("toDate");

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        try {
            ncltJudgementsExtractor.extractFromPage(formatter.parse(startDate), formatter.parse(endDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Extracting required data from the given homepage.
     * @param startDateParam Start Date Parameter
     * @param endDateParam End Date Parameter
     */
    private void extractFromPage(java.util.Date startDateParam, java.util.Date endDateParam) throws Exception {

        driver.get(NCLT_JUDGEMEMTS_URL);

        waitForElementToAppear(By.id("edit-field-bench-target-id"));

        Select drpBench = new Select(driver.findElement(By.id("edit-field-bench-target-id")));
        drpBench.selectByVisibleText(Data.PRINCIPAL_BENCH);

        Calendar c1 = Calendar.getInstance();
        c1.setTime(startDateParam);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(endDateParam);

        String fromDateInput;
        String toDateInput;

        String fromYearInput = String.valueOf(c1.get(Calendar.YEAR)).substring(String.valueOf(c1.get(Calendar.YEAR)).length() - 2);
        String toYearInput = String.valueOf(c2.get(Calendar.YEAR)).substring(String.valueOf(c2.get(Calendar.YEAR)).length() - 2);

        if((c1.get(Calendar.MONTH)+1) < 10) {
            if(c1.get(Calendar.DAY_OF_MONTH) < 10) {
                fromDateInput = "0" + c1.get(Calendar.DAY_OF_MONTH) + "/0" + (c1.get(Calendar.MONTH)+1) + "/" + fromYearInput;
            } else {
                fromDateInput = c1.get(Calendar.DAY_OF_MONTH) + "/0" + (c1.get(Calendar.MONTH)+1) + "/" + fromYearInput;
            }
        } else {
            if(c1.get(Calendar.DAY_OF_MONTH) < 10) {
                fromDateInput =  "0" + c1.get(Calendar.DAY_OF_MONTH) + "/" + (c1.get(Calendar.MONTH)+1) + "/" + fromYearInput;
            } else {
                fromDateInput = c1.get(Calendar.DAY_OF_MONTH) + "/" + (c1.get(Calendar.MONTH)+1) + "/" + fromYearInput;
            }
        }

        if((c2.get(Calendar.MONTH)+1) < 10) {
            if(c2.get(Calendar.DAY_OF_MONTH) < 10) {
                toDateInput = "0" + c2.get(Calendar.DAY_OF_MONTH) + "/0" + (c2.get(Calendar.MONTH)+1) + "/" + toYearInput;
            } else {
                toDateInput = c2.get(Calendar.DAY_OF_MONTH) + "/0" + (c2.get(Calendar.MONTH)+1) + "/" + toYearInput;
            }
        } else {
            if(c2.get(Calendar.DAY_OF_MONTH) < 10) {
                toDateInput =  "0" + c2.get(Calendar.DAY_OF_MONTH) + "/" + (c2.get(Calendar.MONTH)+1) + "/" + toYearInput;
            } else {
                toDateInput = c2.get(Calendar.DAY_OF_MONTH) + "/" + (c2.get(Calendar.MONTH)+1) + "/" + toYearInput;
            }
        }

        System.out.println("fromDateInput: "  + fromDateInput);
        System.out.println("toDateInput: "  + toDateInput);

        By startDate = By.id("edit-field-search-date-value-min-datepicker-popup-0");
        waitForElementToAppear(startDate);

        WebElement startDateElement = driver.findElement(startDate);
        startDateElement.clear();
        startDateElement.sendKeys(fromDateInput);

        By endDate = By.id("edit-field-search-date-value-max-datepicker-popup-0");
        waitForElementToAppear(endDate);

        WebElement endDateElement = driver.findElement(endDate);
        endDateElement.clear();
        endDateElement.sendKeys(toDateInput);

        waitForElementToAppear(By.id("front-Compare-button-new-1"));
        driver.findElement(By.id("front-Compare-button-new-1")).click();

        Thread.sleep(10000);

        extractionJob();

        recursivePaginate();

        shutdown();

    }

    /**
     * Method to implement the automation for Pagination in this particular required website
     * @throws Exception
     */
    private void recursivePaginate() throws Exception {

        waitForElementToAppear(By.className("pager"));

        By nextPager = By.className("pager-next");
        if (isElementPresent(nextPager)) {
            waitForElementToAppear(nextPager);
            WebElement pagerNextElement = driver.findElement(nextPager);
            pagerNextElement.click();
            extractionJob();
            recursivePaginate();
        } else {
            System.out.println("No Pagination in current page.");
        }

    }

    /**
     * Method where the entire extraction of data is taking place.
     * @throws Exception
     */
    private void extractionJob() throws Exception {

        waitForElementToAppear(By.className("view-content"));

        WebElement tableBody = driver.findElement(By.xpath("//*[@id=\"block-system-main\"]/div/div/div[1]/table/tbody"));

        List<WebElement> tableRowList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableBody, By.tagName("tr")));

        for(WebElement tableRow : tableRowList) {

            List<WebElement> tableDataList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableRow, By.tagName("td")));

            NCLTJudgement ncltJudgement = new NCLTJudgement();

            ncltJudgement.setCaseNo(tableDataList.get(1).getText());
            ncltJudgement.setPetitionerName(tableDataList.get(2).getText());

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            try {
                java.util.Date judgementDate = formatter.parse(tableDataList.get(3).getText());
                ncltJudgement.setJudgementDate(judgementDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            List<WebElement> anchorPDFLink = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableDataList.get(4), By.tagName("a")));
            System.out.println(anchorPDFLink.get(0).getAttribute("href"));
            if (!new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLTJudgements").exists()) {
                new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLTJudgements").mkdirs();
            }

            FileDownloadUtil.downloadFile(anchorPDFLink.get(0).getAttribute("href"), System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLTJudgements");

            String[] pdfDetailsArr = tableDataList.get(4).getText().split(",");

            ncltJudgement.setPdfFileName(FileDownloadUtil.FILE_NAME);
            ncltJudgement.setPdfFileSize(pdfDetailsArr[0]);
            ncltJudgement.setPdfFileLanguage(pdfDetailsArr[1]);

            int id = insertIntoNCLTJudgement(ncltJudgement);

            System.out.println(String.format("A new record with id %d has been inserted in nclt_judgement.", id));

        }

    }

    /**
     * Method to insert Model data into database using JDBC PreparedStatement
     *
     * @param ncltJudgement Model data of NCLTJudgement
     * @return id of the record inserted
     */
    private int insertIntoNCLTJudgement(NCLTJudgement ncltJudgement) {

        ResultSet rs = null;
        int id = 0;

        String sql = "INSERT INTO nclt_judgement(caseNo,petitionerName,judgementDate,pdfFileName,pdfFileSize,pdfFileLanguage) "
                + "VALUES(?,?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ncltJudgement.getCaseNo());
            pstmt.setString(2, ncltJudgement.getPetitionerName());
            pstmt.setDate(3, new java.sql.Date(ncltJudgement.getJudgementDate().getTime()));
            pstmt.setString(4, ncltJudgement.getPdfFileName());
            pstmt.setString(5, ncltJudgement.getPdfFileSize());
            pstmt.setString(6, ncltJudgement.getPdfFileLanguage());

            int rowAffected = pstmt.executeUpdate();
            if (rowAffected == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            insertIntoNCLTJudgementErrorLog(ncltJudgement);
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

    private void insertIntoNCLTJudgementErrorLog(NCLTJudgement ncltJudgement) {

        ResultSet rs = null;

        String sql = "INSERT INTO nclt_judgement_error_log(caseNo,petitionerName,judgementDate,pdfFileName,pdfFileSize,pdfFileLanguage) "
                + "VALUES(?,?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ncltJudgement.getCaseNo());
            pstmt.setString(2, ncltJudgement.getPetitionerName());
            pstmt.setDate(3, new java.sql.Date(ncltJudgement.getJudgementDate().getTime()));
            pstmt.setString(4, ncltJudgement.getPdfFileName());
            pstmt.setString(5, ncltJudgement.getPdfFileSize());
            pstmt.setString(6, ncltJudgement.getPdfFileLanguage());

            int rowAffected = pstmt.executeUpdate();
            if (rowAffected == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    //id = rs.getInt(1);
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

    }


}
