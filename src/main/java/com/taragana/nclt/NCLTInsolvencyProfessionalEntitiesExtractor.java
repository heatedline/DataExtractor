package com.taragana.nclt;


import com.taragana.nclt.model.NCLTInsolvencyProfessionalEntites;
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
 * The main class where the entire operation of IPE Extraction takes place from IBBI(Under NCLT) web site.
 *
 * @Author Supratim
 */
public class NCLTInsolvencyProfessionalEntitiesExtractor extends SeleniumBase {

    private static final String NCLT_PROFESSIONAL_ENTITES_URL = "https://ibbi.gov.in/service-provider/professional-entities";

    /**
     * The main method
     * @param args java standard
     */
    public static void main(String[] args) {

        NCLTInsolvencyProfessionalEntitiesExtractor ncltInsolvencyProfessionalEntitiesExtractor = new NCLTInsolvencyProfessionalEntitiesExtractor();
        ncltInsolvencyProfessionalEntitiesExtractor.setupChromeDriver(true);
        try {
            ncltInsolvencyProfessionalEntitiesExtractor.extractFromPage();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Ending NCLTInsolvencyProfessionalEntitiesExtractor....");
                ncltInsolvencyProfessionalEntitiesExtractor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Method where the extraction is taking place from the respective URL
     */
    private void extractFromPage() {

        driver.get(NCLT_PROFESSIONAL_ENTITES_URL);

        waitForElementToAppear(By.className("views-element-container"));

        WebElement tableBody = driver.findElement(By.xpath("//*[@id=\"block-ibbi-content\"]/div/div/table/tbody"));

        List<WebElement> tableRowList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableBody, By.tagName("tr")));

        for(WebElement tableRow : tableRowList) {

            if(!tableRow.getAttribute("class").equals("t-row")) {
                List<WebElement> tableDataList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableRow, By.tagName("td")));

                NCLTInsolvencyProfessionalEntites ncltInsolvencyProfessionalEntites = new NCLTInsolvencyProfessionalEntites();

                ncltInsolvencyProfessionalEntites.setRecognitionNumber(tableDataList.get(1).getText());
                ncltInsolvencyProfessionalEntites.setIpeName(tableDataList.get(2).getText());
                ncltInsolvencyProfessionalEntites.setIpeConstitution(tableDataList.get(3).getText());

                if(!tableDataList.get(3).getText().contains("Derecog")) {
                    ncltInsolvencyProfessionalEntites.setIpeAddress(tableDataList.get(4).getText());
                    ncltInsolvencyProfessionalEntites.setIpePartnersName(tableDataList.get(5).getText());
                    ncltInsolvencyProfessionalEntites.setIpeContactDetails(tableDataList.get(6).getText());
                }

                int id = insertIntoNCLTIPE(ncltInsolvencyProfessionalEntites);

                System.out.println(String.format("A new record with id %d has been inserted.", id));

            }

        }

    }

    /**
     * Method to insert Model data into database using JDBC PreparedStatement
     * @param ncltInsolvencyProfessionalEntites Model data of NCLTInsolvencyProfessionalEntites
     * @return id of the record inserted
     */
    private int insertIntoNCLTIPE(NCLTInsolvencyProfessionalEntites ncltInsolvencyProfessionalEntites) {

        ResultSet rs = null;
        int id = 0;

        String sql = "INSERT INTO nclt_ipe(recognitionNumber,ipeName,ipeConstitution,ipeAddress,ipePartnerName,ipeContactDetails) "
                + "VALUES(?,?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ncltInsolvencyProfessionalEntites.getRecognitionNumber());
            pstmt.setString(2, ncltInsolvencyProfessionalEntites.getIpeName());
            pstmt.setString(3, ncltInsolvencyProfessionalEntites.getIpeConstitution());
            pstmt.setString(4, ncltInsolvencyProfessionalEntites.getIpeAddress());
            pstmt.setString(5, ncltInsolvencyProfessionalEntites.getIpePartnersName());
            pstmt.setString(6, ncltInsolvencyProfessionalEntites.getIpeContactDetails());

            int rowAffected = pstmt.executeUpdate();
            if (rowAffected == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            insertIntoNCLTIPEErrorLog(ncltInsolvencyProfessionalEntites, ex.getErrorCode());
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
     * @param ncltInsolvencyProfessionalEntites Model data of NCLTInsolvencyProfessionalEntites
     * @param errorCode ErrorCode of SQL Exception
     */
    private void insertIntoNCLTIPEErrorLog(NCLTInsolvencyProfessionalEntites ncltInsolvencyProfessionalEntites, Integer errorCode) {

        ResultSet rs = null;
        //int id = 0;

        String sql = "INSERT INTO nclt_ipe_error_log(recognitionNumber,ipeName,ipeConstitution,ipeAddress,ipePartnerName,ipeContactDetails,errorCode) "
                + "VALUES(?,?,?,?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ncltInsolvencyProfessionalEntites.getRecognitionNumber());
            pstmt.setString(2, ncltInsolvencyProfessionalEntites.getIpeName());
            pstmt.setString(3, ncltInsolvencyProfessionalEntites.getIpeConstitution());
            pstmt.setString(4, ncltInsolvencyProfessionalEntites.getIpeAddress());
            pstmt.setString(5, ncltInsolvencyProfessionalEntites.getIpePartnersName());
            pstmt.setString(6, ncltInsolvencyProfessionalEntites.getIpeContactDetails());
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
