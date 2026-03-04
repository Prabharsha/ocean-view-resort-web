package com.oceanview.resort.config;

import com.oceanview.resort.dao.*;
import com.oceanview.resort.service.*;
import com.oceanview.resort.util.BillCalculator;
import com.oceanview.resort.util.ReservationNumberGenerator;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application lifecycle listener that initialises core components on startup.
 *
 * <p><b>Design Pattern: Factory / Singleton</b></p>
 * <p>Acts as a factory for creating and wiring service and DAO singletons.
 * All components are stored in the {@link ServletContext} for access by servlets.</p>
 */
public class AppContextListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("═══ Ocean View Resort – Starting Application ═══");
        ServletContext ctx = sce.getServletContext();

        // ── 1. Database ──
        String dbUrl = ctx.getInitParameter("db.url");
        String dbUser = ctx.getInitParameter("db.username");
        String dbPass = ctx.getInitParameter("db.password");
        DatabaseManager.init(dbUrl, dbUser, dbPass);
        log.info("Database connection pool initialised");

        // ── 2. DAOs (Data Access Objects) ── (Repository Pattern)
        UserDAO userDAO = new UserDAO();
        CustomerDAO customerDAO = new CustomerDAO();
        RoomDAO roomDAO = new RoomDAO();
        ReservationDAO reservationDAO = new ReservationDAO();
        BillDAO billDAO = new BillDAO();
        PaymentDAO paymentDAO = new PaymentDAO();

        ctx.setAttribute("userDAO", userDAO);
        ctx.setAttribute("customerDAO", customerDAO);
        ctx.setAttribute("roomDAO", roomDAO);
        ctx.setAttribute("reservationDAO", reservationDAO);
        ctx.setAttribute("billDAO", billDAO);
        ctx.setAttribute("paymentDAO", paymentDAO);

        // ── 3. Utility Singletons ──
        BillCalculator billCalculator = new BillCalculator();
        ReservationNumberGenerator resNumGen = new ReservationNumberGenerator(reservationDAO);

        ctx.setAttribute("billCalculator", billCalculator);
        ctx.setAttribute("resNumGen", resNumGen);

        // ── 4. Services (Service Layer Pattern) ──
        UserService userService = new UserService(userDAO, customerDAO);
        RoomService roomService = new RoomService(roomDAO, reservationDAO);
        ReservationService reservationService = new ReservationService(
                reservationDAO, roomDAO, userDAO, customerDAO, resNumGen);
        BillService billService = new BillService(
                billDAO, reservationDAO, paymentDAO, roomDAO, billCalculator);
        ReportService reportService = new ReportService(reservationDAO, roomDAO, billDAO);
        CustomerService customerService = new CustomerService(customerDAO, userDAO);

        ctx.setAttribute("userService", userService);
        ctx.setAttribute("roomService", roomService);
        ctx.setAttribute("reservationService", reservationService);
        ctx.setAttribute("billService", billService);
        ctx.setAttribute("reportService", reportService);
        ctx.setAttribute("customerService", customerService);

        // ── 5. JWT secret ──
        ctx.setAttribute("jwtSecret", ctx.getInitParameter("jwt.secret"));
        ctx.setAttribute("jwtExpiration", Long.parseLong(ctx.getInitParameter("jwt.expiration")));

        log.info("═══ Ocean View Resort – Application Started Successfully ═══");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("═══ Ocean View Resort – Shutting Down ═══");
        DatabaseManager.getInstance().shutdown();
        log.info("═══ Ocean View Resort – Shutdown Complete ═══");
    }
}

