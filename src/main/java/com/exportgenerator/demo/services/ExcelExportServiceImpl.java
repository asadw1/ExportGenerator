package com.exportgenerator.demo.services;

import com.exportgenerator.demo.services.serviceinterfaces.ExcelExportService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for exporting data to Excel.
 */
@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    static final List<String> POKEMON_NAMES = new ArrayList<>();

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = ExcelExportServiceImpl.class.getResourceAsStream("/pokemon_names.json")) {
            POKEMON_NAMES.addAll(objectMapper.readValue(inputStream, new TypeReference<List<String>>() {
            }));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Pokémon names", e);
        }
    }

    public ExcelExportServiceImpl() {
        // Empty constructor
    }

    /**
     * Getter for POKEMON_NAMES.
     *
     * @return the list of Pokémon names
     */
    static List<String> getPokemonNames() {
        return POKEMON_NAMES;
    }

    /**
     * Exports data to an Excel file and writes it to the HTTP response.
     *
     * @param response the HTTP response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void exportToExcel(HttpServletResponse response) throws IOException {
        String fileName = "data_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                + ".xlsx";

        Workbook workbook = new SXSSFWorkbook();

        int totalRows = 100000;
        int rowsPerSheet = totalRows / 5;
        String[] sheetNames = { "Data_Sheet1", "Data_Sheet2", "Data_Sheet3", "Data_Sheet4", "Data_Sheet5" };

        buildExport(workbook, rowsPerSheet, sheetNames);

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * Builds the Excel export by creating sheets and populating rows.
     *
     * @param workbook     the workbook
     * @param rowsPerSheet the number of rows per sheet
     * @param sheetNames   the names of the sheets
     */
    void buildExport(Workbook workbook, int rowsPerSheet, String[] sheetNames) {
        for (int sheetIndex = 0; sheetIndex < 5; sheetIndex++) {
            Sheet sheet = workbook.createSheet(sheetNames[sheetIndex]);
            createHeaderRow(sheet);

            LocalDateTime utcNow = LocalDateTime.now(ZoneOffset.UTC);

            for (int i = 1; i <= rowsPerSheet; i++) {
                int rowIndex = i + (sheetIndex * rowsPerSheet);
                Row row = sheet.createRow(i);
                populateRow(row, rowIndex, utcNow);
            }
        }
    }

    /**
     * Creates the header row in the given sheet.
     *
     * @param sheet the sheet where the header row will be created
     */
    void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Value");

        for (int i = 0; i < 10; i++) {
            headerRow.createCell(3 + i).setCellValue("StartDate_" + (char) ('A' + i));
        }

        headerRow.createCell(13).setCellValue("PokemonName");

        for (int i = 0; i < 4; i++) {
            headerRow.createCell(14 + i).setCellValue("Email_" + (i + 1));
        }

        headerRow.createCell(18).setCellValue("Country");
        headerRow.createCell(19).setCellValue("City");
        headerRow.createCell(20).setCellValue("State");
        headerRow.createCell(21).setCellValue("Zipcode");

        for (int i = 0; i < 8; i++) {
            headerRow.createCell(22 + i).setCellValue("Misc_" + (i + 1));
        }
    }

    /**
     * Populates a row with the given data.
     *
     * @param row      the row to populate
     * @param rowIndex the index of the row
     * @param utcNow   the current date and time in UTC
     */
    void populateRow(Row row, int rowIndex, LocalDateTime utcNow) {
        row.createCell(0).setCellValue(rowIndex);
        row.createCell(1).setCellValue("Name " + rowIndex);
        row.createCell(2).setCellValue("Value " + rowIndex);

        for (int j = 0; j < 10; j++) {
            row.createCell(3 + j).setCellValue(utcNow.format(DateTimeFormatter.ISO_DATE));
        }

        row.createCell(13).setCellValue(POKEMON_NAMES.get((rowIndex - 1) % POKEMON_NAMES.size()));

        for (int j = 0; j < 4; j++) {
            row.createCell(14 + j).setCellValue("email" + rowIndex + "_" + (j + 1) + "@example.com");
        }

        row.createCell(18).setCellValue("Country " + rowIndex);
        row.createCell(19).setCellValue("City " + rowIndex);
        row.createCell(20).setCellValue("State " + rowIndex);
        row.createCell(21).setCellValue("Zipcode " + rowIndex);

        for (int j = 0; j < 8; j++) {
            row.createCell(22 + j).setCellValue("Misc " + rowIndex + "_" + (j + 1));
        }
    }
}
