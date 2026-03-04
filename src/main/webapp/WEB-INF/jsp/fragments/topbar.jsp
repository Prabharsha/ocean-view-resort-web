<%-- Ocean View Resort – Topbar Fragment --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<% String ctx = request.getContextPath(); %>

<header class="ov-topbar">
    <button class="sidebar-toggle" onclick="document.getElementById('sidebar').classList.toggle('show');document.getElementById('sidebarOverlay').classList.toggle('show')">
        <i class="fas fa-bars"></i>
    </button>
    <h4 class="page-title mb-0"><%= request.getAttribute("pageTitle") != null ? request.getAttribute("pageTitle") : "Dashboard" %></h4>
    <div class="topbar-actions">
        <button class="btn-icon" title="Notifications">
            <i class="fas fa-bell"></i>
            <span class="notification-badge" id="notifCount" style="display:none">0</span>
        </button>
        <a href="<%=ctx%>/profile" class="btn-icon" title="Profile">
            <i class="fas fa-user"></i>
        </a>
    </div>
</header>

