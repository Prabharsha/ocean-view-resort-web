<!DOCTYPE html>
<html lang="en">
<head><jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/><title>Bills & Payments – Ocean View Resort</title></head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "Bills & Payments"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-2 animate-in"><div><h4 class="fw-bold mb-1">Bills &amp; Payments</h4><p class="text-muted mb-0">Manage invoices and payment records</p></div></div>
        <div class="ov-card animate-in delay-1"><div class="card-body-ov p-0"><div class="table-responsive"><table class="ov-table"><thead><tr><th>Bill ID</th><th>Reservation</th><th>Guest</th><th>Room</th><th>Total</th><th>Status</th><th>Actions</th></tr></thead><tbody id="billTable"><tr><td colspan="7" class="text-center py-4"><div class="ov-spinner"></div></td></tr></tbody></table></div></div></div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
document.addEventListener('DOMContentLoaded', async () => {
    try { const res = await api('/api/bills'); if (res?.ok) { const bills = await res.json(); const tbody = document.getElementById('billTable');
        if (!bills.length) { tbody.innerHTML = '<tr><td colspan="7"><div class="empty-state"><i class="fas fa-file-invoice"></i><h5>No Bills Yet</h5></div></td></tr>'; return; }
        tbody.innerHTML = bills.map(b => '<tr><td class="fw-semibold">' + (b.id?.substring(0,8)||'—') + '</td><td><a href="${pageContext.request.contextPath}/reservations/view/' + (b.reservationId||'') + '">' + (b.reservationNumber||'—') + '</a></td><td>' + (b.guestName||'—') + '</td><td>' + (b.roomNumber||'—') + '</td><td class="fw-bold">' + formatCurrency(b.totalAmount) + '</td><td>' + statusBadge(b.paymentStatus) + '</td><td><a href="${pageContext.request.contextPath}/bills/' + b.id + '" class="btn btn-sm btn-outline-primary"><i class="fas fa-eye"></i></a></td></tr>').join('');
    }} catch(ex) { document.getElementById('billTable').innerHTML = '<tr><td colspan="7" class="text-danger text-center">Failed to load bills</td></tr>'; }
});
</script>
</body>
</html>

