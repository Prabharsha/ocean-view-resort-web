<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/>
    <title>Dashboard – Ocean View Resort</title>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>

<div class="ov-main">
    <% request.setAttribute("pageTitle", "Dashboard"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>

    <div class="ov-content">
        <!-- Stat Cards -->
        <div class="row g-3 mb-4 animate-in">
            <div class="col-sm-6 col-lg-3">
                <div class="stat-card blue">
                    <div class="stat-label">Total Reservations</div>
                    <div class="stat-value" id="totalReservations">0</div>
                </div>
            </div>
            <div class="col-sm-6 col-lg-3">
                <div class="stat-card green">
                    <div class="stat-label">Available Rooms</div>
                    <div class="stat-value" id="availableRooms">0</div>
                </div>
            </div>
            <div class="col-sm-6 col-lg-3">
                <div class="stat-card gold">
                    <div class="stat-label">Check-Ins Today</div>
                    <div class="stat-value" id="checkInsToday">0</div>
                </div>
            </div>
            <div class="col-sm-6 col-lg-3">
                <div class="stat-card teal">
                    <div class="stat-label">Check-Outs Today</div>
                    <div class="stat-value" id="checkOutsToday">0</div>
                </div>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="ov-card mb-4 animate-in delay-1">
            <div class="card-header-ov"><h5><i class="fas fa-bolt me-2" style="color:var(--ov-accent)"></i>Quick Actions</h5></div>
            <div class="card-body-ov">
                <div class="d-flex flex-wrap gap-2">
                    <a href="${pageContext.request.contextPath}/reservations/create" class="btn btn-ocean"><i class="fas fa-plus-circle me-2"></i>New Reservation</a>
                    <a href="${pageContext.request.contextPath}/reservations" class="btn btn-outline-ocean"><i class="fas fa-calendar-alt me-2"></i>View Reservations</a>
                    <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-ocean"><i class="fas fa-door-open me-2"></i>Manage Rooms</a>
                    <a href="${pageContext.request.contextPath}/bills" class="btn btn-outline-ocean"><i class="fas fa-file-invoice-dollar me-2"></i>Bills</a>
                </div>
            </div>
        </div>

        <!-- Revenue Chart -->
        <div class="ov-card animate-in delay-2">
            <div class="card-header-ov"><h5><i class="fas fa-chart-line me-2 text-primary"></i>Monthly Overview</h5></div>
            <div class="card-body-ov">
                <div class="chart-container"><canvas id="revenueChart"></canvas></div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
document.addEventListener('DOMContentLoaded', async () => {
    try {
        const res = await api('/api/dashboard/stats');
        if (res?.ok) {
            const data = await res.json();
            document.getElementById('totalReservations').textContent = data.totalReservations || 0;
            document.getElementById('availableRooms').textContent = data.availableRooms || 0;
            document.getElementById('checkInsToday').textContent = data.checkInsToday || 0;
            document.getElementById('checkOutsToday').textContent = data.checkOutsToday || 0;
        }
    } catch (e) { console.error('Dashboard load error', e); }

    // Chart placeholder
    const ctx = document.getElementById('revenueChart');
    if (ctx && typeof Chart !== 'undefined') {
        new Chart(ctx, {
            type: 'line',
            data: { labels: ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'],
                    datasets: [{ label: 'Reservations', data: [5,8,12,7,10,15,18,14,11,9,13,16],
                        borderColor: '#0077b6', backgroundColor: 'rgba(0,119,182,.1)', fill: true, tension: .4 }]},
            options: { responsive: true, maintainAspectRatio: false }
        });
    }
});
</script>
</body>
</html>

