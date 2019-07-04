package com.taragana.nclt;

import com.taragana.nclt.model.NCLATTentativeList;
import com.taragana.nclt.utils.DBConnection;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * @Author Supratim
 * Extraction of Tentative List from the NCLAT web site.
 */
public class NCLATTentativeListExtractor extends SeleniumBase {

    private static final String NCLAT_TENTATIVE_LIST_URL = "https://nclat.nic.in/?page_id=347";

    /**
     * The main method
     *
     * @param args java standard
     */
    public static void main(String[] args) {

        NCLATTentativeListExtractor nclatTentativeListExtractor = new NCLATTentativeListExtractor();
        nclatTentativeListExtractor.setupChromeDriver(true);
        String fromDate = System.getProperty("fromDate");
        String toDate = System.getProperty("toDate");

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        try {
            nclatTentativeListExtractor.extractFromPage(formatter.parse(fromDate), formatter.parse(toDate));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Ending process....");
                nclatTentativeListExtractor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Extracting required data from the given homepage.
     */
    private void extractFromPage(java.util.Date fromDate, java.util.Date toDate) {

        driver.get(NCLAT_TENTATIVE_LIST_URL);

        waitForElementToAppear(By.id("from_date"));
        waitForElementToAppear(By.id("to_date"));

        Calendar c1 = Calendar.getInstance();
        c1.setTime(fromDate);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(toDate);

        String fromDateInput;
        String toDateInput;

        if((c1.get(Calendar.MONTH)+1) < 10) {
            if(c1.get(Calendar.DAY_OF_MONTH) < 10) {
                fromDateInput = "0" + (c1.get(Calendar.MONTH)+1) + "/0" + c1.get(Calendar.DAY_OF_MONTH) + "/" + c1.get(Calendar.YEAR);
            } else {
                fromDateInput = "0" + (c1.get(Calendar.MONTH)+1) + "/" + c1.get(Calendar.DAY_OF_MONTH) + "/" + c1.get(Calendar.YEAR);
            }
        } else {
            if(c1.get(Calendar.DAY_OF_MONTH) < 10) {
                fromDateInput = c1.get(Calendar.MONTH)+1 + "/0" + c1.get(Calendar.DAY_OF_MONTH) + "/" + c1.get(Calendar.YEAR);
            } else {
                fromDateInput = c1.get(Calendar.MONTH)+1 + "/" + c1.get(Calendar.DAY_OF_MONTH) + "/" + c1.get(Calendar.YEAR);
            }
        }

        if((c2.get(Calendar.MONTH)+1) < 10) {
            if(c2.get(Calendar.DAY_OF_MONTH) < 10) {
                toDateInput = "0" + (c2.get(Calendar.MONTH)+1) + "/0" + c2.get(Calendar.DAY_OF_MONTH) + "/" + c2.get(Calendar.YEAR);
            } else {
                toDateInput = "0" + (c2.get(Calendar.MONTH)+1) + "/" + c2.get(Calendar.DAY_OF_MONTH) + "/" + c2.get(Calendar.YEAR);
            }
        } else {
            if(c2.get(Calendar.DAY_OF_MONTH) < 10) {
                toDateInput = c2.get(Calendar.MONTH)+1 + "/0" + c2.get(Calendar.DAY_OF_MONTH) + "/" + c2.get(Calendar.YEAR);
            } else {
                toDateInput = c2.get(Calendar.MONTH)+1 + "/" + c2.get(Calendar.DAY_OF_MONTH) + "/" + c2.get(Calendar.YEAR);
            }
        }

        System.out.println("fromDateInput: "  + fromDateInput);
        System.out.println("toDateInput: "  + toDateInput);

        driver.findElement(By.id("from_date")).sendKeys(fromDateInput);
        driver.findElement(By.id("to_date")).sendKeys(toDateInput);

        waitForElementToAppear(By.id("filter"));

        driver.findElement(By.id("filter")).click();

        waitForElementToAppear(By.id("order_table"));

        WebElement tableBody = driver.findElement(By.xpath("//*[@id=\"order_table\"]/div/div/table/tbody"));

        List<WebElement> tableRowList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableBody, By.tagName("tr")));

        for (WebElement tableRow : tableRowList) {

            List<WebElement> tableDataList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableRow, By.tagName("td")));

            NCLATTentativeList nclatTentativeList = new NCLATTentativeList();
            nclatTentativeList.setCaseNo(tableDataList.get(0).getText());
            nclatTentativeList.setPartyName(tableDataList.get(1).getText());
            nclatTentativeList.setSection(tableDataList.get(2).getText());

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            try {
                java.util.Date date = formatter.parse(tableDataList.get(3).getText());
                nclatTentativeList.setDate(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            nclatTentativeList.setRemark(tableDataList.get(4).getText());

            int id = insertNCLATTentativeList(nclatTentativeList);

            System.out.println(String.format("A new record with id %d has been inserted.", id));

        }
    }

    /**
     * Method to insert Model data into database using JDBC PreparedStatement
     *
     * @param tentativeList Model data of NCLATTentativeList
     * @return id of the record inserted
     */
    private int insertNCLATTentativeList(NCLATTentativeList tentativeList) {

        ResultSet rs = null;
        int id = 0;

        String sql = "INSERT INTO nclat_daily_tentative_cause_list(caseNo,partyName,section,date,remark) "
                + "VALUES(?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tentativeList.getCaseNo());
            pstmt.setString(2, tentativeList.getPartyName());
            pstmt.setString(3, tentativeList.getSection());
            pstmt.setDate(4, new java.sql.Date(tentativeList.getDate().getTime()));
            pstmt.setString(5, tentativeList.getRemark());

            int rowAffected = pstmt.executeUpdate();
            if (rowAffected == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            insertNCLATTentativeErrorLogList(tentativeList);
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

    /**
     * Method to insert Model data into database using JDBC PreparedStatement
     * @param tentativeList tentativeList Model data of NCLATTentativeList
     */
    private void insertNCLATTentativeErrorLogList(NCLATTentativeList tentativeList) {

        ResultSet rs = null;

        String sql = "INSERT INTO nclat_daily_tentative_cause_list_error_log(caseNo,partyName,section,date,remark) "
                + "VALUES(?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tentativeList.getCaseNo());
            pstmt.setString(2, tentativeList.getPartyName());
            pstmt.setString(3, tentativeList.getSection());
            pstmt.setDate(4, new java.sql.Date(tentativeList.getDate().getTime()));
            pstmt.setString(5, tentativeList.getRemark());

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
