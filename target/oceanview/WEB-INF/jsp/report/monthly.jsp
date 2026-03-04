<!DOCTYPE html>
<html lang="en">
<head><jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/><title>Monthly Report – Ocean View Resort</title></head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "Monthly Report"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content"><a href="${pageContext.request.contextPath}/reports" class="btn btn-outline-ocean btn-sm mb-3"><i class="fas fa-arrow-left me-1"></i>Back to Reports</a><div id="reportContent"><div class="text-center py-5"><div class="ov-spinner"></div></div></div></div>
</div>
<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
document.addEventListener('DOMContentLoaded', async () => {
    const now = new Date(); const year = now.getFullYear(); const month = now.getMonth() + 1;
    try { const res = await api('/api/reports/monthly?year=' + year + '&month=' + month); if (res?.ok) { const d = await res.json(); document.getElementById('reportContent').innerHTML = '<div class="ov-card animate-in"><div class="card-header-ov"><h5>' + d.period + ' Report</h5></div><div class="card-body-ov"><div class="row g-3"><div class="col-sm-6 col-lg-3"><div class="stat-card blue"><div class="stat-label">Total</div><div class="stat-value">' + d.totalReservations + '</div></div></div><div class="col-sm-6 col-lg-3"><div class="stat-card green"><div class="stat-label">Confirmed</div><div class="stat-value">' + d.confirmedReservations + '</div></div></div><div class="col-sm-6 col-lg-3"><div class="stat-card teal"><div class="stat-label">Completed</div><div class="stat-value">' + d.completedReservations + '</div></div></div><div class="col-sm-6 col-lg-3"><div class="stat-card danger"><div class="stat-label">Cancelled</div><div class="stat-value">' + d.cancelledReservations + '</div></div></div></div></div></div>'; } } catch(ex) { document.getElementById('reportContent').innerHTML = '<div class="alert alert-danger">Failed to load report</div>'; }
});
</script>
</body>
</html>

