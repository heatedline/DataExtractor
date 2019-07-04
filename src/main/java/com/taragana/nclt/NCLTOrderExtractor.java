package com.taragana.nclt;

import com.taragana.nclt.model.NCLTOrder;
import com.taragana.nclt.utils.DBConnection;
import com.taragana.nclt.utils.Data;
import com.taragana.nclt.utils.FileDownloadUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
 * The main class where the entire operation of Orders Extraction takes place from NCLT web site.
 *
 * @Author Supratim
 */
public class NCLTOrderExtractor extends SeleniumBase {

    private static final  String NCLT_ORDER_URL = "https://nclt.gov.in/exposed-order-judgements-page";

    /**
     * The main method
     *
     * @param args java standard
     */
    public static void main(String[] args) {

        NCLTOrderExtractor ncltOrderExtractor = new NCLTOrderExtractor();
        ncltOrderExtractor.setupChromeDriver(true);
        String startDate = System.getProperty("fromDate");
        String endDate = System.getProperty("toDate");

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        try {
            ncltOrderExtractor.extractFromPage(formatter.parse(startDate), formatter.parse(endDate));
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

        driver.get(NCLT_ORDER_URL);

        waitForElementToAppear(By.name("field_bench_target_id"));

        Select drpBench = new Select(driver.findElement(By.name("field_bench_target_id")));
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

        By startDate = By.id("edit-field-search-date-value-1-min-datepicker-popup-0");
        waitForElementToAppear(startDate);

        WebElement startDateElement = driver.findElement(startDate);
        startDateElement.clear();
        startDateElement.sendKeys(fromDateInput);

        By endDate = By.id("edit-field-search-date-value-1-max-datepicker-popup-0");
        waitForElementToAppear(endDate);

        WebElement endDateElement = driver.findElement(endDate);
        endDateElement.clear();
        endDateElement.sendKeys(toDateInput);

        waitForElementToAppear(By.id("front-Compare-button-4"));
        driver.findElement(By.id("front-Compare-button-4")).click();

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

        JavascriptExecutor executor = (JavascriptExecutor) driver;

        waitForElementToAppear(By.className("view-content"));

        WebElement tableBody = driver.findElement(By.xpath("//*[@id=\"block-system-main\"]/div/div/div[1]/table/tbody"));

        List<WebElement> tableRowList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableBody, By.tagName("tr")));

        for(WebElement tableRow : tableRowList) {

            List<WebElement> tableDataList = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableRow, By.tagName("td")));

            String[] caseNoArr = tableDataList.get(1).getText().split("\n");
            String caseN0 = caseNoArr[0];
            String status = caseNoArr[1].replaceAll("\\[", "").replaceAll("\\]", "");

            NCLTOrder ncltOrder = new NCLTOrder();
            ncltOrder.setCaseNo(caseN0);
            ncltOrder.setStatus(status);
            ncltOrder.setPetitionerVsRespondent(tableDataList.get(2).getText());
            ncltOrder.setListingDate(tableDataList.get(3).getText());

            int id = insertIntoNCLTOrder(ncltOrder);

            System.out.println(String.format("A new record with id %d has been inserted in nclt_order.", id));

            List<WebElement> buttonElements = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableDataList.get(1), By.tagName("button")));

            WebElement btnParentElement = (WebElement) executor.executeScript("return arguments[0].parentNode;", buttonElements.get(0));

            operation(btnParentElement.getAttribute("href"));

        }

    }

    private void operation(String btnOnClickUrl) throws Exception {

        WebDriver driver = new ChromeDriver(new ChromeOptions().setHeadless(true));

        driver.get(btnOnClickUrl);

        WebElement finalOrderDetailTableBody = driver.findElement(By.xpath("//*[@id=\"block-system-main\"]/div/div/div[1]/div/div[2]/table/tbody"));

        WebElement interimOrderDetailTableBody = driver.findElement(By.xpath("//*[@id=\"block-system-main\"]/div/div/div[2]/table/tbody"));

        List<WebElement> tableRowListFinalOrder = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(finalOrderDetailTableBody, By.tagName("tr")));

        for(WebElement tableRowFinalOrder : tableRowListFinalOrder) {
            List<WebElement> tableDataListFinalOrder = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableRowFinalOrder, By.tagName("td")));

            if(!tableDataListFinalOrder.get(0).getText().equalsIgnoreCase("no result found")) {
                String title = tableDataListFinalOrder.get(0).getText();
                String description = tableDataListFinalOrder.get(1).getText();

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    java.util.Date dateOfJudgement = formatter.parse(tableDataListFinalOrder.get(2).getText());

                    List<WebElement> anchorPDFLink = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableDataListFinalOrder.get(3), By.tagName("a")));

                    if (!new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLTFinalOrder").exists()) {
                        new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLTFinalOrder").mkdirs();
                    }

                    FileDownloadUtil.downloadFile(anchorPDFLink.get(0).getAttribute("href"), System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLTFinalOrder");

                    int id = insertIntoNCLTFinalOrder(title, description, dateOfJudgement, FileDownloadUtil.FILE_NAME);

                    System.out.println(String.format("A new record with id %d has been inserted in nclt_final_order.", id));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(tableDataListFinalOrder.get(0).getText());
            }
        }

        List<WebElement> tableRowListInterimOrder = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(interimOrderDetailTableBody, By.tagName("tr")));

        for(WebElement tableRowInterimOrder : tableRowListInterimOrder) {
            List<WebElement> tableDataListInterimOrder = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableRowInterimOrder, By.tagName("td")));
            String caseNo = tableDataListInterimOrder.get(1).getText();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            try {
                java.util.Date dateOfOrder = formatter.parse(tableDataListInterimOrder.get(2).getText());

                List<WebElement> anchorPDFLink = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableDataListInterimOrder.get(3), By.tagName("a")));

                if (!new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLTInterimOrder").exists()) {
                    new File(System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLTInterimOrder").mkdirs();
                }

                FileDownloadUtil.downloadFile(anchorPDFLink.get(0).getAttribute("href"), System.getProperty("user.dir") + File.separator + "Docs" + File.separator + "NCLTInterimOrder");

                int id = insertIntoNCLTInterimOrder(caseNo, dateOfOrder, FileDownloadUtil.FILE_NAME);

                System.out.println(String.format("A new record with id %d has been inserted in nclt_interim_order.", id));

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        Thread.sleep(5000);

        driver.quit();

    }

    /**
     * Method to insert required data to database using JDBC for NCLT Final Order
     * @param inputTitle
     * @param inputDescription
     * @param inputDateOfJudgement
     * @param inputPdfFileName
     * @return id of the record inserted
     */
    private int insertIntoNCLTFinalOrder(String inputTitle, String inputDescription, java.util.Date inputDateOfJudgement, String inputPdfFileName) {

        ResultSet rs = null;
        int id = 0;

        String sql = "INSERT INTO nclt_final_order(title,description,dateOfJudgement,pdfFileName) "
                + "VALUES(?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, inputTitle);
            pstmt.setString(2, inputDescription);
            pstmt.setDate(3, new java.sql.Date(inputDateOfJudgement.getTime()));
            pstmt.setString(4, inputPdfFileName);

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

    /**
     * Method to insert required data to database using JDBC for NCLT Interim Order
     * @param inputCaseNo
     * @param inputDateOfOrder
     * @param inputPdfFileName
     * @return id of the record inserted
     */
    private int insertIntoNCLTInterimOrder(String inputCaseNo, java.util.Date inputDateOfOrder, String inputPdfFileName) {

        ResultSet rs = null;
        int id = 0;

        String sql = "INSERT INTO nclt_interim_order(caseNo,dateOfOrder,pdfFileName) "
                + "VALUES(?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, inputCaseNo);
            pstmt.setDate(2, new java.sql.Date(inputDateOfOrder.getTime()));
            pstmt.setString(3, inputPdfFileName);

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

    /**
     * Method to insert Model data into database using JDBC PreparedStatement
     *
     * @param ncltOrder Model data of NCLTOrder
     * @return id of the record inserted
     */
    private int insertIntoNCLTOrder(NCLTOrder ncltOrder) {

        ResultSet rs = null;
        int id = 0;

        String sql = "INSERT INTO nclt_order(caseNo,status,petitionerVsRespondent,listingDate) "
                + "VALUES(?,?,?,?)";
        try (Connection dbConn = DBConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(dbConn).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ncltOrder.getCaseNo());
            pstmt.setString(2, ncltOrder.getStatus());
            pstmt.setString(3, ncltOrder.getPetitionerVsRespondent());
            pstmt.setString(4, ncltOrder.getListingDate());

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
