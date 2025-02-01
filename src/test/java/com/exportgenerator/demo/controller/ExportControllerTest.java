package com.exportgenerator.demo.controller;

import com.exportgenerator.demo.services.serviceinterfaces.ExcelExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExportController.class)
public class ExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExcelExportService excelExportService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetMessage() throws Exception {
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Export Controller");

        mockMvc.perform(get("/api/message"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\": \"Export Controller\"}"));
    }

    @Test
    public void testExportToExcel() throws Exception {
        mockMvc.perform(get("/api/export"))
                .andExpect(status().isOk());
    }
}
