<!DOCTYPE html>
<html lang="en">
<head><jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/><title>Bill Details – Ocean View Resort</title></head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "Bill Details"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content"><div id="billContent"><div class="text-center py-5"><div class="ov-spinner"></div></div></div></div>
</div>
<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
document.addEventListener('DOMContentLoaded', async () => {
    const id = window.location.pathname.split('/').pop();
    try { const res = await api('/api/bills/' + id); if (!res?.ok) { showToast('Bill not found','error'); return; } const b = await res.json();
        document.getElementById('billContent').innerHTML = `<div class="row justify-content-center animate-in"><div class="col-lg-8">
            <a href="${pageContext.request.contextPath}/bills" class="btn btn-outline-ocean btn-sm mb-3"><i class="fas fa-arrow-left me-1"></i> Back to Bills</a>
            <div class="ov-card"><div class="bill-header"><div class="d-flex justify-content-between align-items-start">
                <div><div class="hotel-name"><i class="fas fa-water me-2"></i>Ocean View Resort</div><div class="hotel-tagline">Room Reservation Invoice</div></div>
                <div class="text-end"><div style="font-size:.85rem;opacity:.7">Reservation</div><div class="fw-bold">${'${b.reservationNumber||"—"}'}</div></div></div></div>
            <div class="bill-body">
                <div class="row mb-4"><div class="col-sm-6"><h6 class="text-muted mb-1">Guest</h6><div class="fw-bold">${'${b.guestName||"—"}'}</div></div><div class="col-sm-6 text-sm-end"><h6 class="text-muted mb-1">Room</h6><div class="fw-bold">Room ${'${b.roomNumber||"—"}'} (${'${b.roomType||""}'})</div></div></div>
                <table class="ov-table mb-0"><thead><tr><th>Description</th><th class="text-end">Amount</th></tr></thead><tbody>
                    <tr><td>Room Rate (${'${b.numNights}'} nights × ${'${formatCurrency(b.roomRate)}'})</td><td class="text-end">${'${formatCurrency(b.subtotal)}'}</td></tr>
                    <tr><td>Tax (${'${b.taxRate}'}%)</td><td class="text-end">${'${formatCurrency(b.taxAmount)}'}</td></tr>
                    ${'${b.discountAmount > 0 ? "<tr><td>Discount</td><td class=\\"text-end text-success\\">- " + formatCurrency(b.discountAmount) + "</td></tr>" : ""}'}
                </tbody></table>
            </div>
            <div class="bill-footer"><div class="d-flex justify-content-between align-items-center"><div>Status: ${'${statusBadge(b.paymentStatus)}'}</div><div class="bill-total">${'${formatCurrency(b.totalAmount)}'}</div></div></div>
            </div><div class="d-flex justify-content-end gap-2 mt-3 no-print"><button class="btn btn-outline-ocean" onclick="window.print()"><i class="fas fa-print me-2"></i>Print</button></div>
        </div></div>`;
    } catch(ex) { document.getElementById('billContent').innerHTML = '<div class="alert alert-danger">Failed to load bill</div>'; }
});
</script>
</body>
</html>

