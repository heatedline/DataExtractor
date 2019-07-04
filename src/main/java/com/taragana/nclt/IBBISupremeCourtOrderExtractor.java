package com.taragana.nclt;

import com.taragana.nclt.model.IBBISupremeCourtOrder;
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
 * The main class where the entire operation of Supreme Court Order Extraction from IBBI web site.
 *
 * @Author Supratim
 */
public class IBBISupremeCourtOrderExtractor extends SeleniumBase {

    private static final String IBBI_SUPREME_COURT_ORDER_URL = "https://ibbi.gov.in/orders/supreme-court";

    /**
     * The main method
     *
     * @param args java standard
     */
    public static void main(String[] args) {

        IBBISupremeCourtOrderExtractor ibbiSupremeCourtOrderExtractor = new IBBISupremeCourtOrderExtractor();
        ibbiSupremeCourtOrderExtractor.setupChromeDriver(false);
        try {
            ibbiSupremeCourtOrderExtractor.operation();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Ending IBBISupremeCourtOrderExtractor....");
                ibbiSupremeCourtOrderExtractor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Method for Extraction operation
     */
    private void operation() {

        driver.get(IBBI_SUPREME_COURT_ORDER_URL);

        String totalRecordsCount = driver.findElement(By.xpath("//*[@id=\"block-ibbi-content\"]/div/nav/p")).getText();
        System.out.println("There are " + totalRecordsCount + " for Supreme court.");

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

        WebElement tableBody = driver.findElement(By.xpath("//*[@id=\"block-ibbi-content\"]/div/table/tbody"));

        List<WebElement> tableRowList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableBody, By.tagName("tr")));

        for(WebElement tableRow : tableRowList) {

            List<WebElement> tableDataList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableRow, By.tagName("td")));

            IBBISupremeCourtOrder ibbiSupremeCourtOrder = new IBBISupremeCourtOrder();

            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
            try {
                java.util.Date dateOfOrder = formatter.parse(tableDataList.get(0).getText());
                ibbiSupremeCourtOrder.setDateOfOrder(dateOfOrder);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ibbiSupremeCourtOrder.setSubject(tableDataList.get(1).getText());

            List<WebElement> anchorPDFLink = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableDataList.get(1), By.tagName("a")));

            if (!new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "IBBISupremeCourtOrder").exists()) {
                new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "IBBISupremeCourtOrder").mkdirs();
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
                            FileDownloadUtil.downloadFile(executor.executeScript("return document.URL;").toString(), System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "IBBISupremeCourtOrder");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    driver.close(); //closing child window
                    driver.switchTo().window(parentWindow); //cntrl to parent window
                }
            }

            ibbiSupremeCourtOrder.setPdfFileName(FileDownloadUtil.FILE_NAME);
            ibbiSupremeCourtOrder.setOrdersRemarks(tableDataList.get(2).getText());

            int id = insertIntoIBBISupremeCourtOrder(ibbiSupremeCourtOrder);

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
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to insert Model data into database using JDBC PreparedStatement
     * @param ibbiSupremeCourtOrder Model data of IBBISupremeCourtOrder
     * @return id of the record inserted
     */
    private int insertIntoIBBISupremeCourtOrder(IBBISupremeCourtOrder ibbiSupremeCourtOrder) {

        ResultSet rs = null;
        int id = 0;

        String sql = "INSERT INTO ibbi_supreme_court_order(dateOfOrder,subject,pdfFileName,orderRemarks) "
                + "VALUES(?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDate(1, new java.sql.Date(ibbiSupremeCourtOrder.getDateOfOrder().getTime()));
            pstmt.setString(2, ibbiSupremeCourtOrder.getSubject());
            pstmt.setString(3, ibbiSupremeCourtOrder.getPdfFileName());
            pstmt.setString(4, ibbiSupremeCourtOrder.getOrdersRemarks());

            int rowAffected = pstmt.executeUpdate();
            if (rowAffected == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            insertIntoIBBISupremeCourtOrderErrorLog(ibbiSupremeCourtOrder, ex.getErrorCode());
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
     * @param ibbiSupremeCourtOrder ibbiSupremeCourtOrder Model data of IBBISupremeCourtOrder
     * @param errorCode ErrorCode of the SQLException occurred
     */
    private void insertIntoIBBISupremeCourtOrderErrorLog(IBBISupremeCourtOrder ibbiSupremeCourtOrder, Integer errorCode) {

        ResultSet rs = null;
        //int id = 0;

        String sql = "INSERT INTO ibbi_supreme_court_order_error_log(dateOfOrder,subject,pdfFileName,orderRemarks,errorCode) "
                + "VALUES(?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDate(1, new java.sql.Date(ibbiSupremeCourtOrder.getDateOfOrder().getTime()));
            pstmt.setString(2, ibbiSupremeCourtOrder.getSubject());
            pstmt.setString(3, ibbiSupremeCourtOrder.getPdfFileName());
            pstmt.setString(4, ibbiSupremeCourtOrder.getOrdersRemarks());
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
