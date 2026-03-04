<%-- Ocean View Resort – Sidebar Fragment --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<% String ctx = request.getContextPath(); %>

<nav class="ov-sidebar" id="sidebar">
    <!-- Brand -->
    <div class="sidebar-brand">
        <i class="fas fa-water"></i>
        <div class="brand-text">
            Ocean View Resort
            <small>Management System</small>
        </div>
    </div>

    <!-- Navigation -->
    <div class="flex-grow-1 py-2">
        <div class="nav-section">Main</div>
        <a href="<%=ctx%>/dashboard" class="nav-link">
            <i class="fas fa-th-large"></i> Dashboard
        </a>

        <div class="nav-section">Reservations</div>
        <a href="<%=ctx%>/reservations" class="nav-link">
            <i class="fas fa-calendar-alt"></i> All Reservations
        </a>
        <a href="<%=ctx%>/reservations/create" class="nav-link">
            <i class="fas fa-plus-circle"></i> New Reservation
        </a>

        <div class="nav-section">Customers</div>
        <a href="<%=ctx%>/customers" class="nav-link">
            <i class="fas fa-users"></i> Manage Customers
        </a>

        <div class="nav-section">Rooms</div>
        <a href="<%=ctx%>/rooms" class="nav-link">
            <i class="fas fa-door-open"></i> Room Management
        </a>

        <div class="nav-section">Billing</div>
        <a href="<%=ctx%>/bills" class="nav-link">
            <i class="fas fa-file-invoice-dollar"></i> Bills &amp; Payments
        </a>

        <div class="nav-section" data-role="MANAGER">Reports</div>
        <a href="<%=ctx%>/reports" class="nav-link" data-role="MANAGER">
            <i class="fas fa-chart-bar"></i> Reports
        </a>

        <div class="nav-section">Support</div>
        <a href="<%=ctx%>/help" class="nav-link">
            <i class="fas fa-question-circle"></i> Help &amp; FAQ
        </a>
    </div>

    <!-- User Footer -->
    <div class="sidebar-footer">
        <div class="user-info">
            <div class="user-avatar">U</div>
            <div class="user-details">
                <div class="user-name">Guest</div>
                <div class="user-role">&mdash;</div>
            </div>
        </div>
        <a href="#" onclick="OVAuth.clear(); window.location.href=CTX+'/login';"
           class="nav-link mt-2" style="color:rgba(255,255,255,.5)">
            <i class="fas fa-sign-out-alt"></i> Sign Out
        </a>
    </div>
</nav>

