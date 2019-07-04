package com.taragana.nclt;

import com.taragana.nclt.model.NCLATOrder;
import com.taragana.nclt.utils.DBConnection;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The main class where the entire operation of Orders data extraction from NCLAT website is done
 *
 * @Author Supratim
 */
public class NCLATOrdersExtractor extends SeleniumBase {

    private static final String NCLAT_ORDERS_URL = "https://nclat.nic.in/?page_id=125";

    /**
     * The main method
     *
     * @param args java standard
     */
    public static void main(String[] args) {

        NCLATOrdersExtractor nclatOrdersExtractor = new NCLATOrdersExtractor();
        nclatOrdersExtractor.setupChromeDriver(true);
        try {
            nclatOrdersExtractor.extractFromPage();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Ending process....");
                nclatOrdersExtractor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Extracting required data from the given homepage.
     */
    private void extractFromPage() throws Exception {

        driver.get(NCLAT_ORDERS_URL);

        List<String> yearList = new ArrayList<>();

        waitForElementToAppear(By.name("month"));

        Select dropYear = new Select(driver.findElement(By.name("month")));
        List<WebElement> yearDropDownList = new ArrayList<>(dropYear.getOptions());
        yearDropDownList.sort((WebElement a, WebElement b) -> b.getText().compareTo(a.getText()));
        for(WebElement we : yearDropDownList) {
            if(!we.getText().equalsIgnoreCase("select year")) {
                yearList.add(we.getText());
            }
        }

        for(String year : yearList) {
            operation(year);
            driver.navigate().refresh();
        }
    }

    /**
     * The method where we are doing the functional operations to extract data
     *
     * @param year Input year with which the list will be shown
     * @throws Exception
     */
    private void operation(String year) throws Exception {

        System.out.print("Processing " + year + " data....");

        waitForElementToAppear(By.name("month"));

        Select dropYear = new Select(driver.findElement(By.name("month")));
        dropYear.selectByVisibleText(year);

        waitForElementToAppear(By.id("cpfilter"));

        WebElement searchButton = driver.findElement(By.id("cpfilter"));
        searchButton.click();

        Thread.sleep(10000);

        waitForElementToAppear(By.id("order_table"));

        WebElement tableBody = driver.findElement(By.xpath("//*[@id=\"order_table\"]/div/div[2]/table/tbody"));

        List<WebElement> tableRowList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableBody, By.tagName("tr")));

        for (WebElement tableRow : tableRowList) {

            List<WebElement> tableDataList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableRow, By.tagName("td")));

            NCLATOrder nclatOrder = new NCLATOrder();
            nclatOrder.setCompanyAppealNo(tableDataList.get(0).getText());

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            try {
                java.util.Date date = formatter.parse(tableDataList.get(1).getText());
                nclatOrder.setDateOfOrder(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            nclatOrder.setParty(tableDataList.get(2).getText());
            nclatOrder.setSection(tableDataList.get(3).getText());
            nclatOrder.setCourtName(tableDataList.get(4).getText());
            nclatOrder.setOrderPassedBy(tableDataList.get(5).getText());

            List<WebElement> anchorPDFLink = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableDataList.get(6), By.tagName("a")));
            System.out.println(anchorPDFLink.get(0).getAttribute("href"));
            if (!new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLATOrdersList").exists()) {
                new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLATOrdersList").mkdirs();
            }

            FileDownloadUtil.downloadFile(anchorPDFLink.get(0).getAttribute("href"), System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLATOrdersList");

            nclatOrder.setPdfFileName(FileDownloadUtil.FILE_NAME);
            nclatOrder.setPdfFileSize(tableDataList.get(6).getText());
            nclatOrder.setRemark(tableDataList.get(7).getText());

            int id = insertNCLATOrdersList(nclatOrder);

            System.out.println(String.format("A new record with id %d has been inserted.", id));

        }
    }

    /**
     * Method to insert Model data into database using JDBC PreparedStatement
     *
     * @param nclatOrder Model data of NCLATJudgement
     * @return id of the record inserted
     */
    private int insertNCLATOrdersList(NCLATOrder nclatOrder) {

        ResultSet rs = null;
        int id = 0;

        String sql = "INSERT INTO nclat_order(companyAppealNo,dateOfOrder,party,section,courtName,orderPassedBy,pdfFileName,pdfFileSize,remark) "
                + "VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, nclatOrder.getCompanyAppealNo());
            pstmt.setDate(2, new java.sql.Date(nclatOrder.getDateOfOrder().getTime()));
            pstmt.setString(3, nclatOrder.getParty());
            pstmt.setString(4, nclatOrder.getSection());
            pstmt.setString(5, nclatOrder.getCourtName());
            pstmt.setString(6, nclatOrder.getOrderPassedBy());
            pstmt.setString(7, nclatOrder.getPdfFileName());
            pstmt.setString(8, nclatOrder.getPdfFileSize());
            pstmt.setString(9, nclatOrder.getRemark());

            int rowAffected = pstmt.executeUpdate();
            if (rowAffected == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            insertNCLATOrdersErrorLogList(nclatOrder);
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

    private void insertNCLATOrdersErrorLogList(NCLATOrder nclatOrder) {

        ResultSet rs = null;

        String sql = "INSERT INTO nclat_order_error_log(companyAppealNo,dateOfOrder,party,section,courtName,orderPassedBy,pdfFileName,pdfFileSize,remark) "
                + "VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, nclatOrder.getCompanyAppealNo());
            pstmt.setDate(2, new java.sql.Date(nclatOrder.getDateOfOrder().getTime()));
            pstmt.setString(3, nclatOrder.getParty());
            pstmt.setString(4, nclatOrder.getSection());
            pstmt.setString(5, nclatOrder.getCourtName());
            pstmt.setString(6, nclatOrder.getOrderPassedBy());
            pstmt.setString(7, nclatOrder.getPdfFileName());
            pstmt.setString(8, nclatOrder.getPdfFileSize());
            pstmt.setString(9, nclatOrder.getRemark());

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
