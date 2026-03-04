<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/>
    <title>View Reservation – Ocean View Resort</title>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "Reservation Details"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content"><div id="resContent"><div class="text-center py-5"><div class="ov-spinner"></div><br/>Loading reservation...</div></div></div>
</div>
<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
document.addEventListener('DOMContentLoaded', loadReservation);
async function loadReservation() {
    const id = window.location.pathname.split('/').pop();
    try { const res = await api('/api/reservations/' + id); if (!res?.ok) { showToast('Reservation not found','error'); return; } renderReservation(await res.json()); } catch(ex) { document.getElementById('resContent').innerHTML = '<div class="alert alert-danger">Failed to load reservation</div>'; }
}
function renderReservation(r) {
    const nights = r.checkInDate && r.checkOutDate ? Math.ceil((new Date(r.checkOutDate) - new Date(r.checkInDate)) / 86400000) : 0;
    document.getElementById('resContent').innerHTML = `
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-2 animate-in">
            <div class="d-flex align-items-center gap-3"><a href="${pageContext.request.contextPath}/reservations" class="btn btn-outline-ocean btn-sm"><i class="fas fa-arrow-left me-1"></i> Back</a><div><h4 class="fw-bold mb-0">${'${r.reservationNumber || "RES-" + r.id}'}</h4><span class="text-muted">Created ${'${formatDateTime(r.createdAt || r.checkInDate)}'}</span></div></div>
            <div class="d-flex gap-2 flex-wrap">
                ${'${r.status === "PENDING" ? \'<button class="btn btn-outline-success btn-sm" onclick="confirmRes(\\\'\'+ r.reservationNumber +\'\\\')"><i class="fas fa-check me-1"></i>Confirm</button>\' : ""}'}
                ${'${r.status === "CONFIRMED" ? \'<button class="btn btn-success btn-sm" onclick="checkinRes(\\\'\'+ r.reservationNumber +\'\\\')"><i class="fas fa-sign-in-alt me-1"></i>Check In</button>\' : ""}'}
                ${'${r.status === "CHECKED_IN" ? \'<button class="btn btn-warning btn-sm" onclick="checkoutRes(\\\'\'+ r.reservationNumber +\'\\\')"><i class="fas fa-sign-out-alt me-1"></i>Check Out</button>\' : ""}'}
                ${'${["PENDING","CONFIRMED"].includes(r.status) ? \'<button class="btn btn-outline-danger btn-sm" onclick="cancelResView(\\\'\'+ r.id +\'\\\')"><i class="fas fa-times me-1"></i>Cancel</button>\' : ""}'}
                <button class="btn btn-outline-ocean btn-sm" onclick="window.print()"><i class="fas fa-print me-1"></i>Print</button>
            </div>
        </div>
        <div class="row g-4">
            <div class="col-lg-8 animate-in delay-1"><div class="ov-card"><div class="card-header-ov"><h5><i class="fas fa-info-circle me-2 text-primary"></i>Reservation Details</h5>${'${statusBadge(r.status)}'}</div>
                <div class="card-body-ov"><div class="row g-3">
                    <div class="col-sm-6"><div class="text-muted mb-1" style="font-size:.82rem">RESERVATION NUMBER</div><div class="fw-bold">${'${r.reservationNumber || "—"}'}</div></div>
                    <div class="col-sm-6"><div class="text-muted mb-1" style="font-size:.82rem">ROOM</div><div class="fw-bold">${'${r.roomNumber || "Room " + (r.roomId || "—")}'}</div></div>
                    <div class="col-sm-6"><div class="text-muted mb-1" style="font-size:.82rem">CHECK-IN DATE</div><div class="fw-bold"><i class="fas fa-calendar-check text-success me-1"></i>${'${formatDate(r.checkInDate)}'}</div></div>
                    <div class="col-sm-6"><div class="text-muted mb-1" style="font-size:.82rem">CHECK-OUT DATE</div><div class="fw-bold"><i class="fas fa-calendar-times text-danger me-1"></i>${'${formatDate(r.checkOutDate)}'}</div></div>
                    <div class="col-sm-6"><div class="text-muted mb-1" style="font-size:.82rem">NUMBER OF NIGHTS</div><div class="fw-bold">${'${nights}'} night${'${nights !== 1 ? "s" : ""}'}</div></div>
                    <div class="col-sm-6"><div class="text-muted mb-1" style="font-size:.82rem">GUESTS</div><div class="fw-bold"><i class="fas fa-users me-1 text-primary"></i>${'${r.numGuests || 1}'}</div></div>
                </div>${'${r.specialRequests ? \'<hr/><div class="text-muted mb-1" style="font-size:.82rem">SPECIAL REQUESTS</div><div class="p-3 rounded-3" style="background:var(--ov-sand)">\' + r.specialRequests + "</div>" : ""}'}</div>
            </div>
            <div class="ov-card mt-3"><div class="card-header-ov"><h5><i class="fas fa-file-invoice-dollar me-2" style="color:var(--ov-accent)"></i>Bill Summary</h5>${'${r.billId ? \'<a href="${pageContext.request.contextPath}/bills/\' + r.billId + \'" class="btn btn-sm btn-gold">View Full Bill</a>\' : ""}'}</div>
                <div class="card-body-ov" id="billSection"><div class="text-muted text-center py-3">${'${r.billId ? "Loading bill..." : "No bill generated yet"}'}</div></div>
            </div></div>
            <div class="col-lg-4 animate-in delay-2"><div class="ov-card"><div class="card-header-ov"><h5><i class="fas fa-user me-2 text-primary"></i>Guest</h5></div>
                <div class="card-body-ov text-center"><div class="profile-avatar-lg mx-auto mb-3">${'${(r.customerName?.[0] || r.guestName?.[0] || "G").toUpperCase()}'}</div><h5 class="fw-bold">${'${r.customerName || r.guestName || "Guest"}'}</h5><p class="text-muted">Customer ID: ${'${r.customerId || "—"}'}</p></div>
            </div></div>
        </div>`;
    if (r.billId) loadBillSummary(r.billId);
}
async function loadBillSummary(billId) { try { const res = await api('/api/bills/' + billId); if (res?.ok) { const b = await res.json(); document.getElementById('billSection').innerHTML = '<div class="d-flex justify-content-between mb-2"><span class="text-muted">Room Rate / Night</span><strong>' + formatCurrency(b.roomRate) + '</strong></div><div class="d-flex justify-content-between mb-2"><span class="text-muted">Nights</span><strong>' + b.numNights + '</strong></div><hr class="my-2"/><div class="d-flex justify-content-between"><span class="fw-bold">Total</span><span class="fw-bold text-primary" style="font-size:1.2rem">' + formatCurrency(b.totalAmount) + '</span></div><div class="mt-2">' + statusBadge(b.paymentStatus) + '</div>'; } } catch(ex) { document.getElementById('billSection').innerHTML = '<div class="text-muted text-center py-3">Could not load bill</div>'; } }
async function confirmRes(num) { const res = await api('/api/reservations/' + num + '/confirm', { method: 'PUT' }); if (res?.ok) { showToast('Confirmed', 'success'); loadReservation(); } else showToast('Failed', 'error'); }
async function checkinRes(num) { const res = await api('/api/reservations/' + num + '/checkin', { method: 'PUT' }); if (res?.ok) { showToast('Checked in', 'success'); loadReservation(); } else showToast('Failed', 'error'); }
async function checkoutRes(num) { if (!await confirmAction('Confirm check-out?')) return; const res = await api('/api/reservations/' + num + '/checkout', { method: 'PUT' }); if (res?.ok) { showToast('Checked out', 'success'); loadReservation(); } else showToast('Failed', 'error'); }
async function cancelResView(id) { if (!await confirmAction('Cancel this reservation?')) return; const res = await api('/api/reservations/' + id + '/cancel', { method: 'PUT' }); if (res?.ok) { showToast('Cancelled', 'success'); loadReservation(); } else showToast('Failed', 'error'); }
</script>
</body>
</html>

