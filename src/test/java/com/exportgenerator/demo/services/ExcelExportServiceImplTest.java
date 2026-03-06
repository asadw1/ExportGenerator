package com.exportgenerator.demo.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExcelExportServiceImplTest {

    @InjectMocks
    private ExcelExportServiceImpl excelExportService;

    @BeforeEach
    public void setUp() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getResourceAsStream("/pokemon_names.json")) {
            List<String> pokemonNames = objectMapper.readValue(inputStream, new TypeReference<List<String>>() {
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
