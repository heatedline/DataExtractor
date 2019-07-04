package com.taragana.nclt;

import com.taragana.nclt.model.IBBIOrder;
import com.taragana.nclt.utils.DBConnection;
import com.taragana.nclt.utils.DataExtractorUtils;
import com.taragana.nclt.utils.FileDownloadUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The main class where the entire operation of IBBI Order Extraction from IBBI web site.
 *
 * @Author Supratim
 */
public class IBBIOrderExtractor extends SeleniumBase {

    private static final String IBBI_ORDER_URL = "https://ibbi.gov.in/orders/ibbi";

    /**
     * The main method
     *
     * @param args java standard
     */
    public static void main(String[] args) {

        IBBIOrderExtractor ibbiOrderExtractor = new IBBIOrderExtractor();
        ibbiOrderExtractor.setupChromeDriver(false);
        try {
            ibbiOrderExtractor.operation();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Ending IBBIOrderExtractor....");
                ibbiOrderExtractor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Method for Extraction operation
     */
    private void operation() {

        driver.get(IBBI_ORDER_URL);

        String totalRecordsCount = driver.findElement(By.xpath("//*[@id=\"block-ibbi-content\"]/div/div/nav/p")).getText();
        System.out.println("There are " + totalRecordsCount + " for Orders.");

        extractFromPage();

        if(isElementPresent(By.className("pager")))
            recursivePaginate();

    }

    /**
     * Extracting required data from the given homepage.
     */
    private void extractFromPage() {

        JavascriptExecutor executor = (JavascriptExecutor) driver;

        waitForElementToAppear(By.className("views-element-container"));

        WebElement tableBody = driver.findElement(By.xpath("//*[@id=\"block-ibbi-content\"]/div/div/table/tbody"));

        List<WebElement> tableRowList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableBody, By.tagName("tr")));

        for(WebElement tableRow : tableRowList) {

            List<WebElement> tableDataList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableRow, By.tagName("td")));

            IBBIOrder ibbiOrder = new IBBIOrder();

            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
            try {
                java.util.Date dateOfOrder = formatter.parse(tableDataList.get(0).getText());
                ibbiOrder.setDateOfOrder(dateOfOrder);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ibbiOrder.setSubject(tableDataList.get(1).getText());

            List<WebElement> anchorPDFLink = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableDataList.get(1), By.tagName("a")));

            if (!new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "IBBIOrder").exists()) {
                new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "IBBIOrder").mkdirs();
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
                            FileDownloadUtil.downloadFile(executor.executeScript("return document.URL;").toString(), System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "IBBIOrder");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    driver.close(); //closing child window
                    driver.switchTo().window(parentWindow); //control to parent window
                }
            }

            ibbiOrder.setPdfFileName(FileDownloadUtil.FILE_NAME);
            ibbiOrder.setOrderRemarks(tableDataList.get(2).getText());

            int id = insertIntoIBBIOrder(ibbiOrder);

            System.out.println(String.format("A new record with id %d has been inserted.", id));

        }

    }

    /**
     * Method to implement the automation for Pagination in this particular required website
     */
    private void recursivePaginate() {

        JavascriptExecutor executor = (JavascriptExecutor) driver;

        waitForElementToAppear(By.className("pager"));

        List<WebElement> anchorListInPager = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(driver.findElement(By.className("pager")), By.tagName("a")));

        for(WebElement anchorElementInPager : anchorListInPager) {
            try {
                if(anchorElementInPager.getAttribute("rel").equalsIgnoreCase("next")) {
                    executor.executeScript("window.scrollTo(0, document.body.scrollHeight)");
                    Thread.sleep(5000);
                    anchorElementInPager.click();
                    extractFromPage();
                    Thread.sleep(5000);
                    recursivePaginate();
                }
            } catch (InterruptedException | StaleElementReferenceException e) {
                e.getStackTrace();
            }

        }

    }

    /**
     * Method to insert Model data into database using JDBC PreparedStatement
     * @param ibbiOrder Model data of IBBIOrder
     * @return id of the record inserted
     */
    private int insertIntoIBBIOrder(IBBIOrder ibbiOrder) {

        ResultSet rs = null;
        int id = 0;

        String sql = "INSERT INTO ibbi_order(dateOfOrder,subject,pdfFileName,orderRemarks) "
                + "VALUES(?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDate(1, new java.sql.Date(ibbiOrder.getDateOfOrder().getTime()));
            pstmt.setString(2, ibbiOrder.getSubject());
            pstmt.setString(3, ibbiOrder.getPdfFileName());
            pstmt.setString(4, ibbiOrder.getOrderRemarks());

            int rowAffected = pstmt.executeUpdate();
            if (rowAffected == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            insertIntoIBBIOrderErrorLog(ibbiOrder, ex.getErrorCode());
            DataExtractorUtils.printSQLException(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                DataExtractorUtils.printSQLException(e);
            }
        }

        return id;

    }

    /**
     * Method to insert Model data into database using JDBC PreparedStatement
     * @param ibbiOrder ibbiOrder Model data of IBBIOrder
     * @param errorCode ErrorCode of the SQLException occurred
     */
    private void insertIntoIBBIOrderErrorLog(IBBIOrder ibbiOrder, Integer errorCode) {

        ResultSet rs = null;
        //int id = 0;

        String sql = "INSERT INTO ibbi_order_error_log(dateOfOrder,subject,pdfFileName,orderRemarks,errorCode) "
                + "VALUES(?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDate(1, new java.sql.Date(ibbiOrder.getDateOfOrder().getTime()));
            pstmt.setString(2, ibbiOrder.getSubject());
            pstmt.setString(3, ibbiOrder.getPdfFileName());
            pstmt.setString(4, ibbiOrder.getOrderRemarks());
            pstmt.setInt(5, errorCode);

            int rowAffected = pstmt.executeUpdate();
            if (rowAffected == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    //id = rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            DataExtractorUtils.printSQLException(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                DataExtractorUtils.printSQLException(e);
            }
        }
    }



}
