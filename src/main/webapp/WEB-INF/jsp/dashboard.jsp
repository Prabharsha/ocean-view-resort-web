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

        <!-- Welcome Banner -->
        <div class="ov-card mb-4 animate-in" style="background:linear-gradient(135deg,var(--ov-dark),var(--ov-primary-dark));color:white;border:none">
            <div class="card-body-ov d-flex flex-wrap align-items-center justify-content-between gap-3">
                <div>
                    <h4 class="fw-bold mb-1">Welcome back, <span id="welcomeName">User</span>! 👋</h4>
                    <p class="mb-0 opacity-75">Here's what's happening at Ocean View Resort today.</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/reservations/create" class="btn btn-gold">
                        <i class="fas fa-plus me-1"></i> New Reservation
                    </a>
                </div>
            </div>
        </div>

        <!-- Stat Cards Row -->
        <div class="row g-3 mb-4">
            <div class="col-sm-6 col-xl-3 animate-in delay-1">
                <div class="stat-card blue">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <div class="stat-label">Total Reservations</div>
                            <div class="stat-value" id="statReservations">—</div>
                            <div class="stat-trend" id="statResTrend" style="color:var(--ov-gray)">total reservations</div>
                        </div>
                        <div class="stat-icon"><i class="fas fa-calendar-check"></i></div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3 animate-in delay-2">
                <div class="stat-card green">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <div class="stat-label">Available Rooms</div>
                            <div class="stat-value" id="statRooms">—</div>
                            <div class="stat-trend" style="color:var(--ov-gray)">out of <span id="statTotalRooms">0</span> total</div>
                        </div>
                        <div class="stat-icon"><i class="fas fa-door-open"></i></div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3 animate-in delay-3">
                <div class="stat-card gold">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <div class="stat-label">Today's Check-ins</div>
                            <div class="stat-value" id="statCheckins">—</div>
                            <div class="stat-trend" style="color:var(--ov-gray)"><span id="statCheckouts">0</span> check-outs</div>
                        </div>
                        <div class="stat-icon"><i class="fas fa-sign-in-alt"></i></div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3 animate-in delay-4" data-role="MANAGER,STAFF">
                <div class="stat-card teal">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <div class="stat-label">Monthly Revenue</div>
                            <div class="stat-value" id="statRevenue">—</div>
                            <div class="stat-trend" id="statRevTrend" style="color:var(--ov-gray)">paid revenue total</div>
                        </div>
                        <div class="stat-icon"><i class="fas fa-rupee-sign"></i></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Charts Row -->
        <div class="row g-3 mb-4">
            <div class="col-lg-8 animate-in delay-2">
                <div class="ov-card">
                    <div class="card-header-ov">
                        <h5><i class="fas fa-chart-line me-2 text-primary"></i>Revenue Overview</h5>
                        <select class="form-select form-select-sm" style="width:auto" id="revenueRange">
                            <option value="6">Last 6 Months</option>
                            <option value="12" selected>Last 12 Months</option>
                        </select>
                    </div>
                    <div class="card-body-ov">
                        <div class="chart-container"><canvas id="revenueChart"></canvas></div>
                    </div>
                </div>
            </div>
            <div class="col-lg-4 animate-in delay-3">
                <div class="ov-card">
                    <div class="card-header-ov">
                        <h5><i class="fas fa-chart-pie me-2" style="color:var(--ov-accent)"></i>Room Occupancy</h5>
                    </div>
                    <div class="card-body-ov">
                        <div class="chart-container" style="height:260px"><canvas id="occupancyChart"></canvas></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent Reservations & Quick Actions -->
        <div class="row g-3">
            <div class="col-lg-8 animate-in delay-3">
                <div class="ov-card">
                    <div class="card-header-ov">
                        <h5><i class="fas fa-clock me-2" style="color:var(--ov-secondary)"></i>Recent Reservations</h5>
                        <a href="${pageContext.request.contextPath}/reservations" class="btn btn-sm btn-outline-ocean">View All</a>
                    </div>
                    <div class="card-body-ov p-0">
                        <div class="table-responsive">
                            <table class="ov-table">
                                <thead>
                                    <tr>
                                        <th>Reservation #</th>
                                        <th>Guest</th>
                                        <th>Room</th>
                                        <th>Check-in</th>
                                        <th>Status</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody id="recentReservations">
                                    <tr>
                                        <td colspan="6" class="text-center py-4 text-muted">
                                            <div class="ov-spinner"></div><br/>Loading...
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-lg-4 animate-in delay-4">
                <div class="ov-card">
                    <div class="card-header-ov">
                        <h5><i class="fas fa-bolt me-2" style="color:var(--ov-warning)"></i>Quick Actions</h5>
                    </div>
                    <div class="card-body-ov">
                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/reservations/create" class="btn btn-ocean text-start">
                                <i class="fas fa-plus-circle me-2"></i> New Reservation
                            </a>
                            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-ocean text-start">
                                <i class="fas fa-search me-2"></i> Check Room Availability
                            </a>
                            <a href="${pageContext.request.contextPath}/bills" class="btn btn-outline-ocean text-start" data-role="MANAGER,STAFF">
                                <i class="fas fa-file-invoice-dollar me-2"></i> Manage Bills
                            </a>
                            <a href="${pageContext.request.contextPath}/reports" class="btn btn-outline-ocean text-start" data-role="MANAGER">
                                <i class="fas fa-chart-bar me-2"></i> View Reports
                            </a>
                            <a href="${pageContext.request.contextPath}/help" class="btn btn-outline-ocean text-start">
                                <i class="fas fa-question-circle me-2"></i> Help &amp; FAQ
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Today's Activity -->
                <div class="ov-card mt-3">
                    <div class="card-header-ov">
                        <h5><i class="fas fa-stream me-2" style="color:var(--ov-success)"></i>Today's Activity</h5>
                    </div>
                    <div class="card-body-ov" id="todayActivity">
                        <div class="d-flex align-items-start gap-3 mb-3">
                            <div class="rounded-circle d-flex align-items-center justify-content-center"
                                 style="width:36px;height:36px;min-width:36px;background:rgba(0,119,182,.1)">
                                <i class="fas fa-sign-in-alt text-primary" style="font-size:.85rem"></i>
                            </div>
                            <div>
                                <div class="fw-semibold" style="font-size:.88rem">Check-ins expected</div>
                                <div class="text-muted" style="font-size:.8rem" id="actCheckins">Loading...</div>
                            </div>
                        </div>
                        <div class="d-flex align-items-start gap-3 mb-3">
                            <div class="rounded-circle d-flex align-items-center justify-content-center"
                                 style="width:36px;height:36px;min-width:36px;background:rgba(45,159,78,.1)">
                                <i class="fas fa-sign-out-alt text-success" style="font-size:.85rem"></i>
                            </div>
                            <div>
                                <div class="fw-semibold" style="font-size:.88rem">Check-outs expected</div>
                                <div class="text-muted" style="font-size:.8rem" id="actCheckouts">Loading...</div>
                            </div>
                        </div>
                        <div class="d-flex align-items-start gap-3">
                            <div class="rounded-circle d-flex align-items-center justify-content-center"
                                 style="width:36px;height:36px;min-width:36px;background:rgba(201,162,39,.12)">
                                <i class="fas fa-concierge-bell" style="font-size:.85rem;color:var(--ov-accent)"></i>
                            </div>
                            <div>
                                <div class="fw-semibold" style="font-size:.88rem">Pending reservations</div>
                                <div class="text-muted" style="font-size:.8rem" id="actPending">Loading...</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div><!-- /.ov-content -->
</div><!-- /.ov-main -->

<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>

<script>
let revenueChartObj  = null;
let occupancyChartObj = null;

document.addEventListener('DOMContentLoaded', async () => {
    const user = OVAuth.getUser();
    if (user) {
        document.getElementById('welcomeName').textContent = user.firstName || user.username || 'User';
    }
    await loadDashboardData();
    document.getElementById('revenueRange').addEventListener('change', async () => {
        await loadRevenueChart(parseInt(document.getElementById('revenueRange').value));
    });
});

function fmtLKR(v) {
    const n = Number(v) || 0;
    if (n >= 1000000) return 'Rs.' + (n / 1000000).toFixed(1) + 'M';
    if (n >= 1000)    return 'Rs.' + (n / 1000).toFixed(0) + 'k';
    return 'Rs.' + n.toFixed(0);
}

async function loadRevenueChart(months) {
    const ctx = document.getElementById('revenueChart');
    if (!ctx) return;

    let labels = [], revenueData = [], occupancyData = [];
    try {
        const res = await api('/api/dashboard/revenue?months=' + months);
        if (res?.ok) {
            const data = await res.json();
            labels        = data.labels    || [];
            revenueData   = data.revenue   || [];
            occupancyData = data.occupancy || [];
        }
    } catch (e) { console.warn('Revenue chart error:', e); }

    if (revenueChartObj) revenueChartObj.destroy();
    revenueChartObj = new Chart(ctx, {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                label: 'Revenue (LKR)',
                data: revenueData,
                backgroundColor: 'rgba(0,119,182,.7)',
                borderRadius: 6,
                borderSkipped: false
            },{
                label: 'Occupancy %',
                data: occupancyData,
                type: 'line',
                borderColor: '#c9a227',
                backgroundColor: 'rgba(201,162,39,.1)',
                fill: true,
                tension: .4,
                yAxisID: 'y1',
                pointBackgroundColor: '#c9a227',
                pointRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { intersect: false, mode: 'index' },
            plugins: {
                legend: { position: 'top', labels: { usePointStyle: true, padding: 15 } },
                tooltip: {
                    callbacks: {
                        label: ctx => ctx.dataset.yAxisID === 'y1'
                            ? ctx.dataset.label + ': ' + ctx.parsed.y + '%'
                            : ctx.dataset.label + ': ' + formatCurrency(ctx.parsed.y)
                    }
                }
            },
            scales: {
                y:  { beginAtZero: true, ticks: { callback: v => fmtLKR(v) }, grid: { color: 'rgba(0,0,0,.05)' } },
                y1: { position: 'right', beginAtZero: true, max: 100,
                      ticks: { callback: v => v + '%' }, grid: { display: false } },
                x:  { grid: { display: false } }
            }
        }
    });
}

function renderOccupancyChart(occupied, available, maintenance) {
    const ctx = document.getElementById('occupancyChart');
    if (!ctx) return;
    if (occupancyChartObj) occupancyChartObj.destroy();
    occupancyChartObj = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Occupied', 'Available', 'Maintenance'],
            datasets: [{
                data: [occupied, available, maintenance],
                backgroundColor: ['#0077b6', '#2d9f4e', '#f0ad4e'],
                borderWidth: 0,
                hoverOffset: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '70%',
            plugins: {
                legend: { position: 'bottom', labels: { usePointStyle: true, padding: 12 } }
            }
        }
    });
}

async function loadDashboardData() {
    try {
        const metricsRes = await api('/api/dashboard/metrics');
        if (metricsRes?.ok) {
            const m = await metricsRes.json();

            // Room stats
            const total    = m.totalRooms      || 0;
            const avail    = m.availableRooms   || 0;
            const occupied = m.occupiedRooms    || 0;
            const maintenance = Math.max(0, total - avail - occupied);

            document.getElementById('statRooms').textContent      = avail;
            document.getElementById('statTotalRooms').textContent = total;
            renderOccupancyChart(occupied, avail, maintenance);

            // Check-in / check-out stats
            document.getElementById('statCheckins').textContent  = m.todayCheckIns  ?? 0;
            document.getElementById('statCheckouts').textContent = m.todayCheckOuts ?? 0;
            document.getElementById('actCheckins').textContent   = (m.todayCheckIns  ?? 0) + ' guests arriving today';
            document.getElementById('actCheckouts').textContent  = (m.todayCheckOuts ?? 0) + ' guests departing today';

            // Revenue
            const rev = Number(m.totalPaidRevenue) || 0;
            document.getElementById('statRevenue').textContent  = formatCurrency(rev);
            document.getElementById('statRevTrend').textContent = 'paid revenue total';

            // Pending count from status breakdown
            const statusMap = m.reservationsByStatus || {};
            const pending   = statusMap['PENDING'] || 0;
            document.getElementById('actPending').textContent = pending + ' awaiting confirmation';

            // Total reservations
            const allCount = Object.values(statusMap).reduce((s, c) => s + c, 0);
            document.getElementById('statReservations').textContent = allCount;
            document.getElementById('statResTrend').textContent     = 'total reservations';

            // Recent reservations table
            const recentList = m.recentReservations || [];
            const tbody = document.getElementById('recentReservations');
            if (recentList.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6"><div class="empty-state py-4"><i class="fas fa-calendar-times"></i><h5>No Reservations</h5><p>No reservations found yet.</p></div></td></tr>';
            } else {
                tbody.innerHTML = recentList.map(r => `
                    <tr>
                        <td><span class="fw-semibold text-primary">${r.reservationNumber || '—'}</span></td>
                        <td>${r.customerName || '—'}</td>
                        <td>${r.roomNumber   || '—'}</td>
                        <td>${formatDate(r.checkInDate)}</td>
                        <td>${statusBadge(r.status)}</td>
                        <td><a href="${CTX}/reservations/view/${r.id}" class="btn btn-sm btn-outline-ocean">View</a></td>
                    </tr>`).join('');
            }
        } else {
            // Fallback: basic stats endpoint
            const statsRes = await api('/api/dashboard/stats');
            if (statsRes?.ok) {
                const d = await statsRes.json();
                document.getElementById('statRooms').textContent      = d.availableRooms    || 0;
                document.getElementById('statTotalRooms').textContent = d.totalRooms         || 0;
                document.getElementById('statCheckins').textContent   = d.checkInsToday      || 0;
                document.getElementById('statCheckouts').textContent  = d.checkOutsToday     || 0;
                document.getElementById('statReservations').textContent = d.totalReservations || 0;
                document.getElementById('actPending').textContent = (d.pendingCount || 0) + ' awaiting confirmation';
                renderOccupancyChart(
                    (d.totalRooms || 0) - (d.availableRooms || 0),
                    d.availableRooms || 0, 0);
            }
        }

        // Revenue chart
        await loadRevenueChart(parseInt(document.getElementById('revenueRange').value) || 12);

    } catch (ex) {
        console.error('Dashboard load error:', ex);
    }
}
</script>
</body>
</html>

