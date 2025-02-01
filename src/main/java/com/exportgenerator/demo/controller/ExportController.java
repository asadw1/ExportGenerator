package com.exportgenerator.demo.controller;

import com.exportgenerator.demo.services.serviceinterfaces.ExcelExportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
public class ExportController {

    private final ExcelExportService excelExportService;

    public ExportController(ExcelExportService excelExportService) {
        this.excelExportService = excelExportService;
    }

    @Operation(summary = "Export data to Excel", description = "Generates an Excel file containing 100,000 rows with the following columns: ID, Name, Value, 10 date columns (StartDate_A to StartDate_J), and PokemonName. The date columns contain today's date in UTC format, and the PokemonName column contains Pokémon names from Bulbasaur to Mew in a cyclical manner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated and downloaded the Excel file", content = @Content(schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/export")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        excelExportService.exportToExcel(response);
    }

    @GetMapping("/message")
    public Map<String, String> getMessage() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Export Controller");
        return response;
    }
}
