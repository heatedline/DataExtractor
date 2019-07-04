package com.taragana.nclt.utils;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.taragana.nclt.model.IBBIAnnouncementData;
import com.taragana.nclt.model.IBBINewContentData;
import com.taragana.nclt.model.JudgementData;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * An Utility class which uses OpenCSV to write content into CSV files.
 *
 * @Author Supratim
 */
public class OpenCSVUtil {

    public static void writeToNCLTCSV(String csvFileName, String id, String caseNo, String status, String petitionerVsRespondent, String listingDate) throws IOException {
        try (
                Writer writer = Files.newBufferedWriter(Paths.get("./" + csvFileName + ".csv"));

                CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END)
        ) {
            String[] headerRecord = {"id", "caseNo", "status", "petitionerVsRespondent", "listingDate"};
            csvWriter.writeNext(headerRecord);

            csvWriter.writeNext(new String[]{id, caseNo, status, petitionerVsRespondent, listingDate});
        }
    }

    /**
     * Method to write to a CSV file from JavaBean using OpenCSV
     *
     * @param csvFileName            Mentioning the file name of the CSV file
     * @param ibbiNewContentDataList The List of the JavaBean that needs to be provided for data write
     * @throws IOException
     */
    public static void writeToIBBINewContentsCSV(String csvFileName, List<IBBINewContentData> ibbiNewContentDataList) throws IOException {

        try (
                Writer writer = Files.newBufferedWriter(Paths.get("./" + csvFileName + ".csv"));

                CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END)
        ) {
            // Create Mapping Strategy to arrange the
            // column name in order
            ColumnPositionMappingStrategy<IBBINewContentData> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(IBBINewContentData.class);

            String[] headerRecord = {"S No.", "New Content"};
            csvWriter.writeNext(headerRecord);

            // Arrange column name as provided in below array.
            String[] columns = new String[]{"id", "content"};
            mappingStrategy.setColumnMapping(columns);

            // Creating StatefulBeanToCsv object
            StatefulBeanToCsvBuilder<IBBINewContentData> builder = new StatefulBeanToCsvBuilder<>(writer);

            StatefulBeanToCsv<IBBINewContentData> beanWriter = builder.withMappingStrategy(mappingStrategy).build();

            // Write list to StatefulBeanToCsv object
            try {
                beanWriter.write(ibbiNewContentDataList);
            } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Method to write to a CSV file from JavaBean using OpenCSV
     *
     * @param csvFileName       Mentioning the file name of the CSV file
     * @param judgementDataList The List of the JavaBean that needs to be provided for data write
     * @throws IOException
     */
    public static void writeToNCLATCSV(String csvFileName, List<JudgementData> judgementDataList) throws IOException {
        try (
                Writer writer = Files.newBufferedWriter(Paths.get("./" + csvFileName + ".csv"));

                CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END)
        ) {
            // Create Mapping Strategy to arrange the
            // column name in order
            ColumnPositionMappingStrategy<JudgementData> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(JudgementData.class);

            String[] headerRecord = {"S No.", "Company Appeal (AT) No", "Date Of Order", "Party", "Section", "Court Name", "Order passed by", "Remark"};
            csvWriter.writeNext(headerRecord);

            // Arrange column name as provided in below array.
            String[] columns = new String[]{"id", "companyAppealNo", "dateOfOrder", "party", "section", "courtName", "orderPassedBy", "remark"};
            mappingStrategy.setColumnMapping(columns);

            // Creating StatefulBeanToCsv object
            StatefulBeanToCsvBuilder<JudgementData> builder = new StatefulBeanToCsvBuilder<>(writer);

            StatefulBeanToCsv<JudgementData> beanWriter = builder.withMappingStrategy(mappingStrategy).build();

            // Write list to StatefulBeanToCsv object
            try {
                beanWriter.write(judgementDataList);
            } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Method to write to a CSV file from JavaBean using OpenCSV
     *
     * @param csvFileName              Mentioning the file name of the CSV file
     * @param ibbiAnnouncementDataList The List of the JavaBean that needs to be provided for data write
     * @throws IOException
     */
    public static void writeToIBBIAnnouncementCSV(String csvFileName, List<IBBIAnnouncementData> ibbiAnnouncementDataList) throws IOException {
        try (
                Writer writer = Files.newBufferedWriter(Paths.get("./" + csvFileName + ".csv"));

                /*CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END)*/
        ) {
            // Create Mapping Strategy to arrange the
            // column name in order
            ColumnPositionMappingStrategy<IBBIAnnouncementData> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(IBBIAnnouncementData.class);

            // Arrange column name as provided in below array.
            String[] columns = new String[]{"id", "dateOfAnnouncement", "lastDateOfSubmission", "corporateDebtorName", "applicantName", "insolvencyProfessionalName", "insolvencyProfessionalAddress", "remarks"};
            mappingStrategy.setColumnMapping(columns);

            // Creating StatefulBeanToCsv object
            StatefulBeanToCsvBuilder<IBBIAnnouncementData> builder = new StatefulBeanToCsvBuilder<>(writer);

            StatefulBeanToCsv<IBBIAnnouncementData> beanWriter = builder.withMappingStrategy(mappingStrategy).build();

            // Write list to StatefulBeanToCsv object
            try {
                beanWriter.write(ibbiAnnouncementDataList);
            } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                e.printStackTrace();
            }

        }
    }

}