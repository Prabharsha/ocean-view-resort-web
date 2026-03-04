<!DOCTYPE html>
<html lang="en">
<head><jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/><title>Reports – Ocean View Resort</title></head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "Reports"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content">
        <h4 class="fw-bold mb-4 animate-in">Reports Dashboard</h4>
        <div class="row g-4 mb-4 animate-in delay-1">
            <div class="col-sm-6 col-lg-3"><div class="stat-card blue"><div class="stat-label">Total Rooms</div><div class="stat-value" id="totalRooms">0</div></div></div>
            <div class="col-sm-6 col-lg-3"><div class="stat-card green"><div class="stat-label">Available</div><div class="stat-value" id="avail">0</div></div></div>
            <div class="col-sm-6 col-lg-3"><div class="stat-card gold"><div class="stat-label">Occupancy Rate</div><div class="stat-value" id="occRate">0%</div></div></div>
            <div class="col-sm-6 col-lg-3"><div class="stat-card teal"><div class="stat-label">Pending</div><div class="stat-value" id="pendCount">0</div></div></div>
        </div>
        <div class="row g-4 animate-in delay-2">
            <div class="col-lg-8"><div class="ov-card"><div class="card-header-ov"><h5><i class="fas fa-chart-bar me-2 text-primary"></i>Monthly Reservations</h5></div><div class="card-body-ov"><div class="chart-container"><canvas id="monthlyChart"></canvas></div></div></div></div>
            <div class="col-lg-4"><div class="ov-card"><div class="card-header-ov"><h5><i class="fas fa-chart-pie me-2 text-primary"></i>Room Types</h5></div><div class="card-body-ov"><div class="chart-container"><canvas id="roomTypeChart"></canvas></div></div></div></div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
document.addEventListener('DOMContentLoaded', async () => {
    try { const res = await api('/api/reports/occupancy'); if (res?.ok) { const d = await res.json(); document.getElementById('totalRooms').textContent = d.totalRooms||0; document.getElementById('avail').textContent = d.availableRooms||0; document.getElementById('occRate').textContent = d.occupancyRate||'0%'; } } catch(ex) {}
    try { const res = await api('/api/reports/metrics'); if (res?.ok) { const d = await res.json(); document.getElementById('pendCount').textContent = d.pendingCount||0; } } catch(ex) {}
    if (typeof Chart !== 'undefined') {
        new Chart(document.getElementById('monthlyChart'), { type: 'bar', data: { labels: ['Jan','Feb','Mar','Apr','May','Jun'], datasets: [{ label: 'Reservations', data: [12,8,15,10,7,18], backgroundColor: 'rgba(0,119,182,0.7)' }] }, options: { responsive: true, maintainAspectRatio: false } });
        new Chart(document.getElementById('roomTypeChart'), { type: 'doughnut', data: { labels: ['Standard','Deluxe','Suite','Penthouse'], datasets: [{ data: [4,4,2,2], backgroundColor: ['#0077b6','#dc3545','#e65100','#6a1b9a'] }] }, options: { responsive: true, maintainAspectRatio: false } });
    }
});
</script>
</body>
</html>

