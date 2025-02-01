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

    // Package-private getter for POKEMON_NAMES
    static List<String> getPokemonNames() {
        return POKEMON_NAMES;
    }

    @Override
    public void exportToExcel(HttpServletResponse response) throws IOException {
        String fileName = "data_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                + ".xlsx";

        Workbook workbook = new SXSSFWorkbook();

        int totalRows = 100000;
        int rowsPerSheet = totalRows / 5;
        String[] sheetNames = { "Data_Sheet1", "Data_Sheet2", "Data_Sheet3", "Data_Sheet4", "Data_Sheet5" };

        for (int sheetIndex = 0; sheetIndex < 5; sheetIndex++) {
            Sheet sheet = workbook.createSheet(sheetNames[sheetIndex]);
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Value");

            // Add additional date columns
            for (int i = 0; i < 10; i++) {
                headerRow.createCell(3 + i).setCellValue("StartDate_" + (char) ('A' + i));
            }

            // Add PokemonName column
            headerRow.createCell(13).setCellValue("PokemonName");

            // Add email address columns
            for (int i = 0; i < 4; i++) {
                headerRow.createCell(14 + i).setCellValue("Email_" + (i + 1));
            }

            // Add location columns
            headerRow.createCell(18).setCellValue("Country");
            headerRow.createCell(19).setCellValue("City");
            headerRow.createCell(20).setCellValue("State");
            headerRow.createCell(21).setCellValue("Zipcode");

            // Add misc columns
            for (int i = 0; i < 8; i++) {
                headerRow.createCell(22 + i).setCellValue("Misc_" + (i + 1));
            }

            LocalDateTime utcNow = LocalDateTime.now(ZoneOffset.UTC);
            for (int i = 1; i <= rowsPerSheet; i++) {
                int rowIndex = i + (sheetIndex * rowsPerSheet);
                Row row = sheet.createRow(i);
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

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
