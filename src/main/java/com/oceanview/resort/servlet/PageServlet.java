package com.oceanview.resort.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * MVC Controller Servlet for serving JSP page views.
 * Replaces Spring Boot's PageController.
 *
 * <p>Maps browser-facing URL paths to their corresponding JSP templates
 * under /WEB-INF/jsp/. All data fetching happens client-side via AJAX calls
 * to the /api/* servlets — the JSP pages are static shells.</p>
 */
public class PageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();
        String pathInfo = req.getPathInfo();
        String jspPage;

        switch (path) {
            case "/login":      jspPage = "/WEB-INF/jsp/login.jsp"; break;
            case "/register":   jspPage = "/WEB-INF/jsp/register.jsp"; break;
            case "/dashboard":  jspPage = "/WEB-INF/jsp/dashboard.jsp"; break;
            case "/profile":    jspPage = "/WEB-INF/jsp/profile.jsp"; break;
            case "/help":       jspPage = "/WEB-INF/jsp/help.jsp"; break;

            case "/reservations":
                if (pathInfo == null || pathInfo.equals("/")) {
                    jspPage = "/WEB-INF/jsp/reservation/list.jsp";
                } else if (pathInfo.startsWith("/create")) {
                    jspPage = "/WEB-INF/jsp/reservation/create.jsp";
                } else if (pathInfo.startsWith("/view/")) {
                    jspPage = "/WEB-INF/jsp/reservation/view.jsp";
                } else if (pathInfo.startsWith("/update/")) {
                    jspPage = "/WEB-INF/jsp/reservation/update.jsp";
                } else {
                    jspPage = "/WEB-INF/jsp/reservation/list.jsp";
                }
                break;

            case "/rooms":
                if (pathInfo == null || pathInfo.equals("/")) {
                    jspPage = "/WEB-INF/jsp/room/list.jsp";
                } else {
                    jspPage = "/WEB-INF/jsp/room/detail.jsp";
                }
                break;

            case "/bills":
                if (pathInfo == null || pathInfo.equals("/")) {
                    jspPage = "/WEB-INF/jsp/bill/list.jsp";
                } else if (pathInfo.endsWith("/print")) {
                    jspPage = "/WEB-INF/jsp/bill/print.jsp";
                } else {
                    jspPage = "/WEB-INF/jsp/bill/detail.jsp";
                }
                break;

            case "/reports":
                if (pathInfo == null || pathInfo.equals("/")) {
                    jspPage = "/WEB-INF/jsp/report/dashboard.jsp";
                } else if (pathInfo.startsWith("/monthly")) {
                    jspPage = "/WEB-INF/jsp/report/monthly.jsp";
                } else if (pathInfo.startsWith("/weekly")) {
                    jspPage = "/WEB-INF/jsp/report/weekly.jsp";
                } else {
                    jspPage = "/WEB-INF/jsp/report/dashboard.jsp";
                }
                break;

            case "/customers":
                if (pathInfo == null || pathInfo.equals("/")) {
                    jspPage = "/WEB-INF/jsp/customer/list.jsp";
                } else {
                    jspPage = "/WEB-INF/jsp/customer/detail.jsp";
                }
                break;

            default:
                jspPage = "/WEB-INF/jsp/dashboard.jsp";
                break;
        }

        req.getRequestDispatcher(jspPage).forward(req, resp);
    }
}

