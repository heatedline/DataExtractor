package com.taragana.nclt;

import com.taragana.nclt.model.NCLTInsolvencyProfessionalAgencies;
import com.taragana.nclt.utils.DBConnection;
import com.taragana.nclt.utils.DataExtractorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.*;
import java.util.List;
import java.util.Objects;

/**
 * The main class where the entire operation of IPA Extraction takes place from IBBI(Under NCLT) web site.
 *
 * @Author Supratim
 */
public class NCLTInsolvencyProfessionalAgenciesExtractor extends SeleniumBase {

    private static final String NCLT_IPA_URL = "https://ibbi.gov.in/service-provider/professional-agencies";

    /**
     * The main method
     * @param args java standard
     */
    public static void main(String[] args) {

        NCLTInsolvencyProfessionalAgenciesExtractor ncltipaExtractor = new NCLTInsolvencyProfessionalAgenciesExtractor();
        ncltipaExtractor.setupChromeDriver(true);
        try {
          ncltipaExtractor.extractFromPage();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Ending NCLTInsolvencyProfessionalAgenciesExtractor....");
                ncltipaExtractor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Method where the extraction is taking place from the respective URL
     */
    private void extractFromPage() {

        driver.get(NCLT_IPA_URL);

        waitForElementToAppear(By.className("views-element-container"));

        WebElement tableBody = driver.findElement(By.xpath("//*[@id=\"block-ibbi-content\"]/div/div/table/tbody"));

        List<WebElement> tableRowList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableBody, By.tagName("tr")));

        for(WebElement tableRow : tableRowList) {

            List<WebElement> tableDataList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableRow, By.tagName("td")));

            NCLTInsolvencyProfessionalAgencies ncltInsolvencyProfessionalAgencies = new NCLTInsolvencyProfessionalAgencies();
            ncltInsolvencyProfessionalAgencies.setIpaRegistrationNumber(tableDataList.get(1).getText());
            ncltInsolvencyProfessionalAgencies.setIpaName(tableDataList.get(2).getText());
            ncltInsolvencyProfessionalAgencies.setIpaAddress(tableDataList.get(3).getText());
            ncltInsolvencyProfessionalAgencies.setIpaWebsite(tableDataList.get(4).getText());
            ncltInsolvencyProfessionalAgencies.setIpaChiefExecutiveName(tableDataList.get(5).getText());
            ncltInsolvencyProfessionalAgencies.setIpaContactDetails(tableDataList.get(6).getText());

            int id = insertIntoNCLTIPA(ncltInsolvencyProfessionalAgencies);

            System.out.println(String.format("A new record with id %d has been inserted.", id));

        }

    }

    /**
     * Method to insert Model data into database using JDBC PreparedStatement
     * @param ncltInsolvencyProfessionalAgencies Model data of NCLTInsolvencyProfessionalAgencies
     * @return id of the record inserted
     */
    private int insertIntoNCLTIPA(NCLTInsolvencyProfessionalAgencies ncltInsolvencyProfessionalAgencies) {

        ResultSet rs = null;
        int id = 0;

        String sql = "INSERT INTO nclt_ipa(ipaRegistrationNumber,ipaName,ipaAddress,ipaWebsite,ipaChiefExecutiveName,ipaContactDetails) "
                + "VALUES(?,?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ncltInsolvencyProfessionalAgencies.getIpaRegistrationNumber());
            pstmt.setString(2, ncltInsolvencyProfessionalAgencies.getIpaName());
            pstmt.setString(3, ncltInsolvencyProfessionalAgencies.getIpaAddress());
            pstmt.setString(4, ncltInsolvencyProfessionalAgencies.getIpaWebsite());
            pstmt.setString(5, ncltInsolvencyProfessionalAgencies.getIpaChiefExecutiveName());
            pstmt.setString(6, ncltInsolvencyProfessionalAgencies.getIpaContactDetails());

            int rowAffected = pstmt.executeUpdate();
            if (rowAffected == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            insertIntoNCLTIPAErrorLog(ncltInsolvencyProfessionalAgencies, ex.getErrorCode());
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
     * @param ncltInsolvencyProfessionalAgencies Model data of NCLTInsolvencyProfessionalAgencies
     * @param errorCode Error Code of the SQL Exception
     */
    private void insertIntoNCLTIPAErrorLog(NCLTInsolvencyProfessionalAgencies ncltInsolvencyProfessionalAgencies, Integer errorCode) {

        ResultSet rs = null;
        //int id = 0;

        String sql = "INSERT INTO nclt_ipa_error_log(ipaRegistrationNumber,ipaName,ipaAddress,ipaWebsite,ipaChiefExecutiveName,ipaContactDetails,errorCode) "
                + "VALUES(?,?,?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ncltInsolvencyProfessionalAgencies.getIpaRegistrationNumber());
            pstmt.setString(2, ncltInsolvencyProfessionalAgencies.getIpaName());
            pstmt.setString(3, ncltInsolvencyProfessionalAgencies.getIpaAddress());
            pstmt.setString(4, ncltInsolvencyProfessionalAgencies.getIpaWebsite());
            pstmt.setString(5, ncltInsolvencyProfessionalAgencies.getIpaChiefExecutiveName());
            pstmt.setString(6, ncltInsolvencyProfessionalAgencies.getIpaContactDetails());
            pstmt.setInt(7, errorCode);

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
