<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/>
    <title>Reservations – Ocean View Resort</title>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "Reservations"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-2 animate-in">
            <div><h4 class="fw-bold mb-1">All Reservations</h4><p class="text-muted mb-0">Manage and track all reservations</p></div>
            <a href="${pageContext.request.contextPath}/reservations/create" class="btn btn-ocean"><i class="fas fa-plus-circle me-2"></i> New Reservation</a>
        </div>
        <div class="row g-3 mb-4 animate-in delay-1">
            <div class="col-sm-6 col-lg-3"><div class="stat-card blue"><div class="stat-label">Total</div><div class="stat-value" id="totalCount">0</div></div></div>
            <div class="col-sm-6 col-lg-3"><div class="stat-card gold"><div class="stat-label">Pending</div><div class="stat-value" id="pendingCount">0</div></div></div>
            <div class="col-sm-6 col-lg-3"><div class="stat-card green"><div class="stat-label">Checked In</div><div class="stat-value" id="checkedInCount">0</div></div></div>
            <div class="col-sm-6 col-lg-3"><div class="stat-card danger"><div class="stat-label">Cancelled</div><div class="stat-value" id="cancelledCount">0</div></div></div>
        </div>
        <div class="ov-card mb-4 animate-in delay-2">
            <div class="card-body-ov">
                <div class="filter-bar mb-0">
                    <div class="search-box"><i class="fas fa-search"></i><input type="text" class="form-control ov-input" id="searchInput" placeholder="Search by reservation #, guest name..." oninput="filterReservations()"/></div>
                    <select class="form-select ov-input" style="width:auto" id="statusFilter" onchange="filterReservations()">
                        <option value="">All Statuses</option><option value="PENDING">Pending</option><option value="CONFIRMED">Confirmed</option><option value="CHECKED_IN">Checked In</option><option value="CHECKED_OUT">Checked Out</option><option value="CANCELLED">Cancelled</option>
                    </select>
                    <select class="form-select ov-input" style="width:auto" id="sortSelect" onchange="filterReservations()">
                        <option value="newest">Newest First</option><option value="oldest">Oldest First</option><option value="checkin">Check-in Date</option>
                    </select>
                </div>
            </div>
        </div>
        <div class="ov-card animate-in delay-3">
            <div class="card-body-ov p-0">
                <div class="table-responsive">
                    <table class="ov-table"><thead><tr><th>Reservation #</th><th>Guest</th><th>Room</th><th>Check-in</th><th>Check-out</th><th>Guests</th><th>Status</th><th>Actions</th></tr></thead>
                        <tbody id="reservationTable"><tr><td colspan="8" class="text-center py-4"><div class="ov-spinner"></div><br/>Loading reservations...</td></tr></tbody>
                    </table>
                </div>
            </div>
        </div>
        <div class="mt-3" id="pagination"></div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
let allReservations = [];
let currentPage = 0;
const PAGE_SIZE = 10;
document.addEventListener('DOMContentLoaded', loadReservations);

async function loadReservations() {
    try {
        const res = await api('/api/reservations');
        if (res?.ok) { const data = await res.json(); allReservations = Array.isArray(data) ? data : (data.content || []); updateCounts(); filterReservations(); }
    } catch (ex) { document.getElementById('reservationTable').innerHTML = '<tr><td colspan="8" class="text-center text-danger py-4">Failed to load reservations</td></tr>'; }
}

function updateCounts() {
    document.getElementById('totalCount').textContent = allReservations.length;
    document.getElementById('pendingCount').textContent = allReservations.filter(r => r.status === 'PENDING').length;
    document.getElementById('checkedInCount').textContent = allReservations.filter(r => r.status === 'CHECKED_IN').length;
    document.getElementById('cancelledCount').textContent = allReservations.filter(r => r.status === 'CANCELLED').length;
}

function filterReservations() {
    const search = document.getElementById('searchInput').value.toLowerCase();
    const status = document.getElementById('statusFilter').value;
    const sort = document.getElementById('sortSelect').value;
    let filtered = allReservations.filter(r => {
        const matchSearch = !search || (r.reservationNumber || '').toLowerCase().includes(search) || (r.guestName || '').toLowerCase().includes(search) || (r.customerName || '').toLowerCase().includes(search);
        return matchSearch && (!status || r.status === status);
    });
    if (sort === 'newest') filtered.sort((a, b) => new Date(b.createdAt || b.checkInDate) - new Date(a.createdAt || a.checkInDate));
    else if (sort === 'oldest') filtered.sort((a, b) => new Date(a.createdAt || a.checkInDate) - new Date(b.createdAt || b.checkInDate));
    else if (sort === 'checkin') filtered.sort((a, b) => new Date(a.checkInDate) - new Date(b.checkInDate));
    currentPage = 0; renderPage(filtered);
}

function renderPage(data) {
    const start = currentPage * PAGE_SIZE;
    const page = data.slice(start, start + PAGE_SIZE);
    const tbody = document.getElementById('reservationTable');
    if (page.length === 0) { tbody.innerHTML = '<tr><td colspan="8"><div class="empty-state"><i class="fas fa-calendar-times"></i><h5>No Reservations Found</h5><p>Try adjusting your filters.</p></div></td></tr>'; }
    else {
        tbody.innerHTML = page.map(r => `<tr>
            <td><a href="${pageContext.request.contextPath}/reservations/view/${'${r.id}'}" class="fw-semibold text-primary">${'${r.reservationNumber || "—"}'}</a></td>
            <td>${'${r.guestName || r.customerName || "Guest"}'}</td>
            <td>${'${r.roomNumber || r.roomId || "—"}'}</td>
            <td>${'${formatDate(r.checkInDate)}'}</td>
            <td>${'${formatDate(r.checkOutDate)}'}</td>
            <td><i class="fas fa-user me-1 text-muted"></i>${'${r.numGuests || 1}'}</td>
            <td>${'${statusBadge(r.status)}'}</td>
            <td><div class="btn-group btn-group-sm">
                <a href="${pageContext.request.contextPath}/reservations/view/${'${r.id}'}" class="btn btn-outline-primary" title="View"><i class="fas fa-eye"></i></a>
                ${'${r.status === "CONFIRMED" ? \'<button class="btn btn-outline-success" title="Check In" onclick="checkin(\\\'\'+ r.reservationNumber +\'\\\')"><i class="fas fa-sign-in-alt"></i></button>\' : ""}'}
                ${'${(r.status === "PENDING" || r.status === "CONFIRMED") ? \'<button class="btn btn-outline-danger" title="Cancel" onclick="cancelRes(\\\'\'+ r.id +\'\\\')"><i class="fas fa-times"></i></button>\' : ""}'}
            </div></td></tr>`).join('');
    }
    const totalPages = Math.ceil(data.length / PAGE_SIZE);
    buildPagination('pagination', currentPage, totalPages, p => { currentPage = p; renderPage(data); });
}

async function checkin(resNum) {
    if (!await confirmAction('Confirm check-in for ' + resNum + '?')) return;
    const res = await api('/api/reservations/' + resNum + '/checkin', { method: 'PUT' });
    if (res?.ok) { showToast('Checked in successfully', 'success'); loadReservations(); } else showToast('Check-in failed', 'error');
}

async function cancelRes(id) {
    if (!await confirmAction('Cancel this reservation?')) return;
    const res = await api('/api/reservations/' + id + '/cancel', { method: 'PUT' });
    if (res?.ok) { showToast('Reservation cancelled', 'success'); loadReservations(); } else showToast('Cancellation failed', 'error');
}
</script>
</body>
</html>

