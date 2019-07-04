package com.taragana.nclt;

import com.taragana.nclt.model.NCLTRegisteredIP;
import com.taragana.nclt.utils.DBConnection;
import com.taragana.nclt.utils.DataExtractorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

/**
 * The main class where the entire operation of Registered IP Extraction takes place from IBBI(Under NCLT) web site.
 *
 * @Author Supratim
 */
public class NCLTRegisteredIPExtractor extends SeleniumBase {

    private static final String NCLT_REGISTERED_IP_REGULATION_7_WITH_9_URL = "https://ibbi.gov.in/ips-register/view-ip/1";
    private static final String NCLT_REGISTERED_IP_REGULATION_7_WITH_5_URL = "https://ibbi.gov.in/ips-register/view-ip/2";

    /**
     * The main method
     * @param args java standard
     */
    public static void main(String[] args) {

        NCLTRegisteredIPExtractor ncltRegisteredIPExtractor = new NCLTRegisteredIPExtractor();
        ncltRegisteredIPExtractor.setupChromeDriver(true);

        try {
            ncltRegisteredIPExtractor.operation("9");
            ncltRegisteredIPExtractor.operation("5");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Ending NCLTRegisteredIPExtractor....");
                ncltRegisteredIPExtractor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * The method where we are doing the functional operations to extract data
     * @param regulation Passing the regulation for which Registered IP data will be extracted
     */
    private void operation(String regulation) {

        if(regulation.contains("9")) {
            driver.get(NCLT_REGISTERED_IP_REGULATION_7_WITH_9_URL);
        } else {
            if(regulation.contains("5")) {
                driver.get(NCLT_REGISTERED_IP_REGULATION_7_WITH_5_URL);
            }
        }

        String totalRecordsCount = driver.findElement(By.xpath("//*[@id=\"block-ibbi-content\"]/div/div/nav[1]/p")).getText();
        System.out.println("There are " + totalRecordsCount + " for regulation " + regulation);

        extractFromPage(regulation);

        recursivePaginate(regulation);

    }

    /**
     * Extracting required data from the given homepage.
     * @param regulation Passing the regulation for which Registered IP data will be extracted
     */
    private void extractFromPage(String regulation) {

            waitForElementToAppear(By.className("views-element-container"));

            WebElement tableBody = driver.findElement(By.xpath("//*[@id=\"block-ibbi-content\"]/div/div/table/tbody"));

            List<WebElement> tableRowList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableBody, By.tagName("tr")));

            for(WebElement tableRow : tableRowList) {

                List<WebElement> tableDataList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableRow, By.tagName("td")));

                NCLTRegisteredIP ncltRegisteredIP = new NCLTRegisteredIP();

                ncltRegisteredIP.setRegulation(regulation);
                ncltRegisteredIP.setRegistrationNumber(tableDataList.get(1).getText());
                ncltRegisteredIP.setIpName(tableDataList.get(2).getText());
                ncltRegisteredIP.setIpAddress(tableDataList.get(3).getText());
                if(!tableDataList.get(3).getText().contains("wef")) {
                    ncltRegisteredIP.setIpEmail(tableDataList.get(4).getText());
                    ncltRegisteredIP.setEnrolledWithIPAName(tableDataList.get(5).getText());

                    SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
                    try {
                        java.util.Date registrationDate = formatter.parse(tableDataList.get(6).getText());
                        ncltRegisteredIP.setRegistrationDate(registrationDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    ncltRegisteredIP.setRemarks(tableDataList.get(7).getText());
                }

                int id = insertIntoNCLTRegisteredIP(ncltRegisteredIP);

                System.out.println(String.format("A new record with id %d has been inserted.", id));

            }

    }

    /**
     * Method to implement the automation for Pagination in this particular required website
     * @param regulation Passing the regulation for which Registered IP data will be shown
     */
    private void recursivePaginate(String regulation) {

        JavascriptExecutor executor = (JavascriptExecutor) driver;

        waitForElementToAppear(By.className("pager"));

        List<WebElement> anchorListInPager = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(driver.findElement(By.className("pagination")), By.tagName("a")));

        for(WebElement anchorElementInPager : anchorListInPager) {
            try {
                if(anchorElementInPager.getAttribute("rel").equalsIgnoreCase("next")) {
                    executor.executeScript("window.scrollTo(0, document.body.scrollHeight)");
                    Thread.sleep(5000);
                    anchorElementInPager.click();
                    extractFromPage(regulation);
                    Thread.sleep(5000);
                    recursivePaginate(regulation);
                }
            } catch (InterruptedException | StaleElementReferenceException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * Method to insert Model data into database using JDBC PreparedStatement
     * @param ncltRegisteredIP Model data of NCLTRegisteredIP
     * @return id of the record inserted
     */
    private int insertIntoNCLTRegisteredIP(NCLTRegisteredIP ncltRegisteredIP) {

        ResultSet rs = null;
        int id = 0;

        String sql = "INSERT INTO nclt_registered_ip(regulation,registrationNumber,ipName,ipAddress,ipEmail,enrolledWithIPAName,registrationDate,remarks) "
                + "VALUES(?,?,?,?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ncltRegisteredIP.getRegulation());
            pstmt.setString(2, ncltRegisteredIP.getRegistrationNumber());
            pstmt.setString(3, ncltRegisteredIP.getIpName());
            pstmt.setString(4, ncltRegisteredIP.getIpAddress());
            pstmt.setString(5, ncltRegisteredIP.getIpEmail());
            pstmt.setString(6, ncltRegisteredIP.getEnrolledWithIPAName());
            if(ncltRegisteredIP.getRegistrationDate() != null) {
                pstmt.setDate(7, new java.sql.Date(ncltRegisteredIP.getRegistrationDate().getTime()));
            } else {
                pstmt.setNull(7, Types.DATE);
            }

            pstmt.setString(8, ncltRegisteredIP.getRemarks());

            int rowAffected = pstmt.executeUpdate();
            if (rowAffected == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            insertIntoNCLTRegisteredIP(ncltRegisteredIP, ex.getErrorCode());
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
     * @param ncltRegisteredIP Model data of NCLTRegisteredIP
     * @param errorCode ErrorCode of the SQL Exception
     */
    private void insertIntoNCLTRegisteredIP(NCLTRegisteredIP ncltRegisteredIP, Integer errorCode) {

        ResultSet rs = null;
        //int id = 0;

        String sql = "INSERT INTO nclt_registered_ip_error_log(regulation,registrationNumber,ipName,ipAddress,ipEmail,enrolledWithIPAName,registrationDate,remarks,errorCode) "
                + "VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ncltRegisteredIP.getRegulation());
            pstmt.setString(2, ncltRegisteredIP.getRegistrationNumber());
            pstmt.setString(3, ncltRegisteredIP.getIpName());
            pstmt.setString(4, ncltRegisteredIP.getIpAddress());
            pstmt.setString(5, ncltRegisteredIP.getIpEmail());
            pstmt.setString(6, ncltRegisteredIP.getEnrolledWithIPAName());
            pstmt.setDate(7, new java.sql.Date(ncltRegisteredIP.getRegistrationDate().getTime()));
            pstmt.setString(8, ncltRegisteredIP.getRemarks());
            pstmt.setInt(9, errorCode);

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
