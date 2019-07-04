package com.taragana.nclt;

import com.taragana.nclt.model.NCLATJudgement;
import com.taragana.nclt.utils.DBConnection;
import com.taragana.nclt.utils.Data;
import com.taragana.nclt.utils.FileDownloadUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The main class where the entire operation of Judgements data extraction from NCLAT website is done
 *
 * @Author Supratim
 */
public class NCLATJudgementsExtractor extends SeleniumBase {


    private static final String NCLAT_JUDGEMENTS_URL = "https://nclat.nic.in/?page_id=123";

    /**
     * The main method
     *
     * @param args java standard
     */
    public static void main(String[] args) {

        NCLATJudgementsExtractor nclatJudgementsExtractor = new NCLATJudgementsExtractor();
        nclatJudgementsExtractor.setupChromeDriver(true);
        try {
            nclatJudgementsExtractor.extractFromPage();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Ending process....");
                nclatJudgementsExtractor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Extracting required data from the given homepage.
     */
    private void extractFromPage() throws Exception {

        driver.get(NCLAT_JUDGEMENTS_URL);

        Data.yearList.sort(Collections.reverseOrder());

        for (String year : Data.yearList) {
            System.out.println(year);
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

        waitForElementToAppear(By.name("month"));

        Select dropYear = new Select(driver.findElement(By.name("month")));
        dropYear.selectByVisibleText(year);

        waitForElementToAppear(By.id("cpfilter"));

        WebElement searchButton = driver.findElement(By.id("cpfilter"));
        searchButton.click();

        Thread.sleep(5000);

        waitForElementToAppear(By.id("order_table"));

        WebElement tableBody = driver.findElement(By.xpath("//*[@id=\"order_table\"]/div/div[2]/table/tbody"));

        List<WebElement> tableRowList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableBody, By.tagName("tr")));

        for (WebElement tableRow : tableRowList) {

            List<WebElement> tableDataList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableRow, By.tagName("td")));

            NCLATJudgement nclatJudgement = new NCLATJudgement();
            nclatJudgement.setCompanyAppealNo(tableDataList.get(0).getText());

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            try {
                java.util.Date date = formatter.parse(tableDataList.get(1).getText());
                nclatJudgement.setDateOfOrder(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            nclatJudgement.setParty(tableDataList.get(2).getText());
            nclatJudgement.setSection(tableDataList.get(3).getText());
            nclatJudgement.setCourtName(tableDataList.get(4).getText());
            nclatJudgement.setOrderPassedBy(tableDataList.get(5).getText());

            List<WebElement> anchorPDFLink = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableDataList.get(6), By.tagName("a")));
            System.out.println(anchorPDFLink.get(0).getAttribute("href"));
            if (!new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLATJudgementsList").exists()) {
                new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLATJudgementsList").mkdirs();
            }

            FileDownloadUtil.downloadFile(anchorPDFLink.get(0).getAttribute("href"), System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLATJudgementsList");

            nclatJudgement.setPdfFileName(FileDownloadUtil.FILE_NAME);
            nclatJudgement.setPdfFileSize(tableDataList.get(6).getText());
            nclatJudgement.setRemark(tableDataList.get(7).getText());

            int id = insertNCLATJudgementList(nclatJudgement);

            System.out.println(String.format("A new record with id %d has been inserted.", id));

        }

    }

    /**
     * Method to insert Model data into database using JDBC PreparedStatement
     *
     * @param judgementList Model data of NCLATJudgement
     * @return id of the record inserted
     */
    private int insertNCLATJudgementList(NCLATJudgement judgementList) {

        ResultSet rs = null;
        int id = 0;

        String sql = "INSERT INTO nclat_judgement(companyAppealNo,dateOfOrder,party,section,courtName,orderPassedBy,pdfFileName,pdfFileSize,remark) "
                + "VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, judgementList.getCompanyAppealNo());
            pstmt.setDate(2, new java.sql.Date(judgementList.getDateOfOrder().getTime()));
            pstmt.setString(3, judgementList.getParty());
            pstmt.setString(4, judgementList.getSection());
            pstmt.setString(5, judgementList.getCourtName());
            pstmt.setString(6, judgementList.getOrderPassedBy());
            pstmt.setString(7, judgementList.getPdfFileName());
            pstmt.setString(8, judgementList.getPdfFileSize());
            pstmt.setString(9, judgementList.getRemark());

            int rowAffected = pstmt.executeUpdate();
            if (rowAffected == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            insertNCLATJudgementErrorLogList(judgementList);
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

    private void insertNCLATJudgementErrorLogList(NCLATJudgement judgementList) {

        ResultSet rs = null;

        String sql = "INSERT INTO nclat_judgement_error_log(companyAppealNo,dateOfOrder,party,section,courtName,orderPassedBy,pdfFileName,pdfFileSize,remark) "
                + "VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, judgementList.getCompanyAppealNo());
            pstmt.setDate(2, new java.sql.Date(judgementList.getDateOfOrder().getTime()));
            pstmt.setString(3, judgementList.getParty());
            pstmt.setString(4, judgementList.getSection());
            pstmt.setString(5, judgementList.getCourtName());
            pstmt.setString(6, judgementList.getOrderPassedBy());
            pstmt.setString(7, judgementList.getPdfFileName());
            pstmt.setString(8, judgementList.getPdfFileSize());
            pstmt.setString(9, judgementList.getRemark());

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
