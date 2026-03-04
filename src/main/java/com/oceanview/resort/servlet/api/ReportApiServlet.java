package com.oceanview.resort.servlet.api;

import com.oceanview.resort.service.ReportService;
import com.oceanview.resort.servlet.BaseApiServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

/**
 * REST servlet for report generation.
 */
public class ReportApiServlet extends BaseApiServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ReportService service = getService(req, "reportService");
        String pathParam = getPathParam(req);

        try {
            if (pathParam == null || pathParam.equals("monthly")) {
                int year = Integer.parseInt(req.getParameter("year") != null ?
                        req.getParameter("year") : String.valueOf(LocalDate.now().getYear()));
                int month = Integer.parseInt(req.getParameter("month") != null ?
                        req.getParameter("month") : String.valueOf(LocalDate.now().getMonthValue()));
                sendJson(resp, service.generateMonthlyReport(year, month));
            } else if (pathParam.equals("weekly")) {
                int year = Integer.parseInt(req.getParameter("year") != null ?
                        req.getParameter("year") : String.valueOf(LocalDate.now().getYear()));
                int week = Integer.parseInt(req.getParameter("week") != null ?
                        req.getParameter("week") : "1");
                sendJson(resp, service.generateWeeklyReport(year, week));
            } else if (pathParam.equals("occupancy")) {
                sendJson(resp, service.generateOccupancyReport());
            } else if (pathParam.equals("revenue")) {
                sendJson(resp, service.generateRevenueReport());
            } else if (pathParam.equals("metrics")) {
                sendJson(resp, service.generateAdvancedMetrics());
            } else {
                sendError(resp, 404, "Unknown report type");
            }
        } catch (Exception e) {
            sendError(resp, 500, e.getMessage());
        }
    }
}

