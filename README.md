# 🌊 Ocean View Resort – Room Reservation System (Java EE Edition)

> **CIS6003 Advanced Programming – Cardiff Metropolitan University / ICBT**
>
> A production-quality Room Reservation System built with **Java EE (Servlets, JSP, JDBC)** — no frameworks.
> Converted from the original Spring Boot + Thymeleaf version while keeping identical UI and flows.

---

## 📋 Technology Stack

| Layer          | Technology                          |
|----------------|-------------------------------------|
| **Language**   | Java 17                             |
| **Web Server** | Apache Tomcat 10.1+ (Jakarta EE 10) |
| **Servlets**   | Jakarta Servlet 6.0                 |
| **Views**      | JSP + JSTL                          |
| **Database**   | MySQL 8.x via JDBC                  |
| **Conn Pool**  | HikariCP                            |
| **Password**   | BCrypt (favre lib)                   |
| **Auth**       | Custom JWT (HMAC-SHA256)             |
| **JSON**       | Gson                                |
| **Logging**    | SLF4J + Logback                     |
| **Frontend**   | HTML5, CSS3, Bootstrap 5.3, JS (vanilla) |
| **Build**      | Apache Maven                         |

---

## 🏗️ Architecture & Design Patterns

| Pattern                | Where Applied                                      |
|------------------------|----------------------------------------------------|
| **MVC**                | Servlets (Controller) → JSP (View) → Model (POJO)  |
| **DAO Pattern**        | `UserDAO`, `RoomDAO`, `ReservationDAO`, `BillDAO`   |
| **Service Layer**      | `UserService`, `ReservationService`, `BillService`  |
| **Singleton**          | `DatabaseManager`, `ReservationNumberGenerator`     |
| **Factory**            | `AppContextListener` wiring all components          |
| **Filter Chain**       | `AuthFilter`, `EncodingFilter`, `CorsFilter`        |
| **Strategy**           | `BillCalculator` for calculation strategies          |

---

## 📁 Project Structure

```
ocean-view-resort-ee/
├── pom.xml
├── src/main/java/com/oceanview/resort/
│   ├── config/         → AppContextListener, DatabaseManager
│   ├── dao/            → UserDAO, CustomerDAO, RoomDAO, ReservationDAO, BillDAO, PaymentDAO
│   ├── filter/         → AuthFilter, EncodingFilter, CorsFilter
│   ├── model/          → User, Customer, Room, Reservation, Bill, Payment
│   │   └── enums/      → UserRole, RoomType, ReservationStatus, PaymentMethod
│   ├── service/        → UserService, RoomService, ReservationService, BillService, etc.
│   ├── servlet/        → PageServlet, BaseApiServlet
│   │   └── api/        → AuthServlet, ReservationApiServlet, RoomApiServlet, BillApiServlet, etc.
│   └── util/           → BillCalculator, JwtUtil, JsonUtil, PasswordUtil, ReservationNumberGenerator
├── src/main/resources/
│   ├── db/             → schema.sql, data.sql
│   └── logback.xml
└── src/main/webapp/
    ├── index.jsp
    ├── static/css/     → style.css
    ├── static/js/      → app.js
    └── WEB-INF/
        ├── web.xml
        └── jsp/
            ├── fragments/  → head.jsp, sidebar.jsp, topbar.jsp, scripts.jsp
            ├── login.jsp, register.jsp, dashboard.jsp, profile.jsp, help.jsp
            ├── reservation/ → list.jsp, create.jsp, view.jsp, update.jsp
            ├── room/       → list.jsp, detail.jsp
            ├── bill/       → list.jsp, detail.jsp, print.jsp
            ├── customer/   → list.jsp, detail.jsp
            ├── report/     → dashboard.jsp, monthly.jsp, weekly.jsp
            └── error/      → 404.jsp, 500.jsp
```

---

## 🚀 Setup & Deployment

### Prerequisites
- **Java 17** (JDK)
- **Apache Tomcat 10.1+** (Jakarta EE 10)
- **MySQL 8.x**
- **Apache Maven 3.8+**

### 1. Database Setup
```sql
-- Run schema.sql first, then data.sql
mysql -u root -p < src/main/resources/db/schema.sql
mysql -u root -p < src/main/resources/db/data.sql
```

### 2. Configure Database Connection
Edit `src/main/webapp/WEB-INF/web.xml` — update the context parameters:
```xml
<context-param>
    <param-name>db.url</param-name>
    <param-value>jdbc:mysql://localhost:3306/ocean_view_resort?useSSL=false&amp;serverTimezone=Asia/Colombo&amp;allowPublicKeyRetrieval=true</param-value>
</context-param>
<context-param>
    <param-name>db.username</param-name>
    <param-value>root</param-value>
</context-param>
<context-param>
    <param-name>db.password</param-name>
    <param-value>root</param-value>
</context-param>
```

### 3. Build
```bash
mvn clean package
```

### 4. Deploy
Copy the generated `target/oceanview.war` to your Tomcat `webapps/` directory and start Tomcat.

Or use Maven Tomcat plugin / IDE integration.

### 5. Access
Open your browser: **http://localhost:8080/oceanview/login**

### Demo Credentials
| Role       | Username   | Password       |
|------------|------------|----------------|
| Manager    | pansilu    | Manager@123    |
| Staff      | staff1     | Staff@123      |
| Customer   | customer1  | Customer@123   |

---

## 🔑 Key Features

- ✅ **User Authentication** – JWT-based login with role-based access (Manager, Staff, Customer)
- ✅ **Reservation Management** – Create, confirm, check-in, check-out, cancel
- ✅ **Room Management** – CRUD operations, availability search by date range
- ✅ **Bill Generation** – Automatic calculation with tax, discounts, payments
- ✅ **Customer Management** – Registration, search, loyalty points
- ✅ **Reports** – Monthly/weekly reports, occupancy rates
- ✅ **Responsive UI** – Same ocean-themed UI as original Spring Boot version
- ✅ **Multi-step Wizard** – 3-step reservation creation flow
- ✅ **Print Support** – Print-friendly bill views

---

## 🆚 Spring Boot vs Java EE Comparison

| Feature              | Spring Boot Version         | Java EE Version (this)       |
|----------------------|-----------------------------|-----------------------------|
| Web Framework        | Spring MVC + Thymeleaf      | Jakarta Servlets + JSP      |
| Data Access          | Spring Data JPA             | Plain JDBC + DAO Pattern    |
| Security             | Spring Security + JWT       | Custom JWT + AuthFilter     |
| DI / IoC             | Spring Container            | Manual wiring (Listener)    |
| Config               | application.properties      | web.xml context-params      |
| Templates            | Thymeleaf fragments         | JSP includes                |
| Build Output         | Executable JAR              | WAR (for Tomcat)            |
| Packaging            | Embedded Tomcat             | External Tomcat 10.1+       |

---

*Built for CIS6003 Advanced Programming — Cardiff Metropolitan University / ICBT*

