package com.exportgenerator.demo.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExcelExportServiceImplTest {

    @InjectMocks
    private ExcelExportServiceImpl excelExportService;

    private List<String> pokemonNames;

    @BeforeEach
    public void setUp() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getResourceAsStream("/pokemon_names.json")) {
            pokemonNames = objectMapper.readValue(inputStream, new TypeReference<List<String>>() {
            });
            ExcelExportServiceImpl.getPokemonNames().clear();
            ExcelExportServiceImpl.getPokemonNames().addAll(pokemonNames);
        }
    }

    @Test
    public void testExportToExcel() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(byteArrayOutputStream));

        excelExportService.exportToExcel(response);

        verify(response, times(1)).getOutputStream();
        Workbook workbook = new SXSSFWorkbook();
        assertNotNull(workbook);
    }

    @Test
    public void testBuildExport() {
        Workbook workbook = new SXSSFWorkbook();
        int rowsPerSheet = 20000;
        String[] sheetNames = { "Data_Sheet1", "Data_Sheet2", "Data_Sheet3", "Data_Sheet4", "Data_Sheet5" };

        excelExportService.buildExport(workbook, rowsPerSheet, sheetNames);

        assertEquals(5, workbook.getNumberOfSheets());

        for (int sheetIndex = 0; sheetIndex < 5; sheetIndex++) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            assertNotNull(sheet);
            assertEquals(sheetNames[sheetIndex], sheet.getSheetName());

            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow);
            assertEquals("ID", headerRow.getCell(0).getStringCellValue());
            assertEquals("Name", headerRow.getCell(1).getStringCellValue());
            assertEquals("Value", headerRow.getCell(2).getStringCellValue());

            for (int rowIndex = 1; rowIndex <= rowsPerSheet; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                assertNotNull(row);
                assertEquals(rowIndex + (sheetIndex * rowsPerSheet), (int) row.getCell(0).getNumericCellValue());
                assertEquals("Name " + (rowIndex + (sheetIndex * rowsPerSheet)), row.getCell(1).getStringCellValue());
                assertEquals("Value " + (rowIndex + (sheetIndex * rowsPerSheet)), row.getCell(2).getStringCellValue());
            }
        }
    }

    @Test
    public void testCreateHeaderRow() {
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("Test_Sheet");

        excelExportService.createHeaderRow(sheet);

        Row headerRow = sheet.getRow(0);
        assertNotNull(headerRow);
        assertEquals("ID", headerRow.getCell(0).getStringCellValue());
        assertEquals("Name", headerRow.getCell(1).getStringCellValue());
        assertEquals("Value", headerRow.getCell(2).getStringCellValue());

        for (int i = 0; i < 10; i++) {
            assertEquals("StartDate_" + (char) ('A' + i), headerRow.getCell(3 + i).getStringCellValue());
        }

        assertEquals("PokemonName", headerRow.getCell(13).getStringCellValue());

        for (int i = 0; i < 4; i++) {
            assertEquals("Email_" + (i + 1), headerRow.getCell(14 + i).getStringCellValue());
        }

        assertEquals("Country", headerRow.getCell(18).getStringCellValue());
        assertEquals("City", headerRow.getCell(19).getStringCellValue());
        assertEquals("State", headerRow.getCell(20).getStringCellValue());
        assertEquals("Zipcode", headerRow.getCell(21).getStringCellValue());

        for (int i = 0; i < 8; i++) {
            assertEquals("Misc_" + (i + 1), headerRow.getCell(22 + i).getStringCellValue());
        }
    }

    @Test
    public void testPopulateRow() {
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("Test_Sheet");
        Row row = sheet.createRow(1);
        LocalDateTime utcNow = LocalDateTime.now(ZoneOffset.UTC);

        int rowIndex = 1;
        excelExportService.populateRow(row, rowIndex, utcNow);

        assertEquals(rowIndex, (int) row.getCell(0).getNumericCellValue());
        assertEquals("Name " + rowIndex, row.getCell(1).getStringCellValue());
        assertEquals("Value " + rowIndex, row.getCell(2).getStringCellValue());

        for (int j = 0; j < 10; j++) {
            assertEquals(utcNow.format(DateTimeFormatter.ISO_DATE), row.getCell(3 + j).getStringCellValue());
        }

        assertEquals(pokemonNames.get((rowIndex - 1) % pokemonNames.size()), row.getCell(13).getStringCellValue());

        for (int j = 0; j < 4; j++) {
            assertEquals("email" + rowIndex + "_" + (j + 1) + "@example.com", row.getCell(14 + j).getStringCellValue());
        }

        assertEquals("Country " + rowIndex, row.getCell(18).getStringCellValue());
        assertEquals("City " + rowIndex, row.getCell(19).getStringCellValue());
        assertEquals("State " + rowIndex, row.getCell(20).getStringCellValue());
        assertEquals("Zipcode " + rowIndex, row.getCell(21).getStringCellValue());

        for (int j = 0; j < 8; j++) {
            assertEquals("Misc " + rowIndex + "_" + (j + 1), row.getCell(22 + j).getStringCellValue());
        }
    }

    // Helper class to mock ServletOutputStream
    private static class DelegatingServletOutputStream extends jakarta.servlet.ServletOutputStream {
        private final ByteArrayOutputStream outputStream;

        public DelegatingServletOutputStream(ByteArrayOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void write(int b) {
            outputStream.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
            // No-op
        }
    }
}
