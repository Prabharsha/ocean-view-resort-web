<!DOCTYPE html>
<html lang="en">
<head><jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/><title>Weekly Report – Ocean View Resort</title></head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "Weekly Report"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content"><a href="${pageContext.request.contextPath}/reports" class="btn btn-outline-ocean btn-sm mb-3"><i class="fas fa-arrow-left me-1"></i>Back to Reports</a><div id="reportContent"><div class="text-center py-5"><div class="ov-spinner"></div></div></div></div>
</div>
<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
document.addEventListener('DOMContentLoaded', async () => {
    try { const res = await api('/api/reports/weekly?year=2026&week=10'); if (res?.ok) { const d = await res.json(); document.getElementById('reportContent').innerHTML = '<div class="ov-card animate-in"><div class="card-header-ov"><h5>Week ' + d.week + ', ' + d.year + '</h5></div><div class="card-body-ov"><p><strong>Period:</strong> ' + (d.periodStart||'') + ' to ' + (d.periodEnd||'') + '</p><p><strong>Total Reservations:</strong> ' + d.totalReservations + '</p></div></div>'; } } catch(ex) { document.getElementById('reportContent').innerHTML = '<div class="alert alert-danger">Failed to load report</div>'; }
});
</script>
</body>
</html>

