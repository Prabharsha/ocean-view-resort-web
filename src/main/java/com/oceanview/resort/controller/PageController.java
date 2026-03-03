package com.oceanview.resort.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MVC controller for serving Thymeleaf page views.
 *
 * <p>Maps browser-facing URL paths to their corresponding Thymeleaf
 * templates under {@code classpath:/templates/}.</p>
 */
@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboardPage() {
        return "dashboard";
    }

    @GetMapping("/reservations")
    public String reservationListPage() {
        return "reservation/list";
    }

    @GetMapping("/reservations/create")
    public String createReservationPage() {
        return "reservation/create";
    }

    @GetMapping("/reservations/view/{id}")
    public String viewReservationPage() {
        return "reservation/view";
    }

    @GetMapping("/rooms")
    public String roomListPage() {
        return "room/list";
    }

    @GetMapping("/rooms/{id}")
    public String roomDetailPage() {
        return "room/detail";
    }

    @GetMapping("/bills")
    public String billListPage() {
        return "bill/list";
    }

    @GetMapping("/bills/{id}")
    public String billDetailPage() {
        return "bill/detail";
    }

    @GetMapping("/reports")
    public String reportsPage() {
        return "report/dashboard";
    }

    @GetMapping("/reports/monthly")
    public String monthlyReportPage() {
        return "report/monthly";
    }

    @GetMapping("/reports/weekly")
    public String weeklyReportPage() {
        return "report/weekly";
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "profile";
    }

    @GetMapping("/help")
    public String helpPage() {
        return "help";
    }

    @GetMapping("/reservations/update/{id}")
    public String updateReservationPage() {
        return "reservation/update";
    }

    @GetMapping("/bills/{id}/print")
    public String printBillPage() {
        return "bill/print";
    }

    @GetMapping("/customers")
    public String customerListPage() {
        return "customer/list";
    }

    @GetMapping("/customers/{id}")
    public String customerDetailPage() {
        return "customer/detail";
    }
}
