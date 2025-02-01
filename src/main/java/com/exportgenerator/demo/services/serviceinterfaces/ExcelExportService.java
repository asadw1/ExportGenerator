package com.exportgenerator.demo.services.serviceinterfaces;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ExcelExportService {
    void exportToExcel(HttpServletResponse response) throws IOException;
}
