<!DOCTYPE html>
<html lang="en">
<head><jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/><title>Room Details – Ocean View Resort</title></head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "Room Details"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content"><div id="roomContent"><div class="text-center py-5"><div class="ov-spinner"></div></div></div></div>
</div>
<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
document.addEventListener('DOMContentLoaded', async () => {
    const id = window.location.pathname.split('/').pop();
    try { const res = await api('/api/rooms/' + id); if (res?.ok) { const r = await res.json(); document.getElementById('roomContent').innerHTML = '<div class="animate-in"><a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-ocean btn-sm mb-3"><i class="fas fa-arrow-left me-1"></i> Back</a><div class="ov-card"><div class="room-img" style="height:250px"><i class="fas fa-bed" style="font-size:4rem"></i><div class="room-price">' + formatCurrency(r.ratePerNight) + '/night</div></div><div class="card-body-ov"><h3 class="fw-bold">Room ' + r.roomNumber + '</h3>' + roomTypeBadge(r.roomType) + ' ' + (r.available ? '<span class="badge-status badge-confirmed ms-2">Available</span>' : '<span class="badge-status badge-cancelled ms-2">Occupied</span>') + '<div class="row g-3 mt-3"><div class="col-sm-4"><div class="text-muted">Capacity</div><div class="fw-bold"><i class="fas fa-users text-primary me-1"></i>' + r.capacity + ' guests</div></div><div class="col-sm-4"><div class="text-muted">Floor</div><div class="fw-bold"><i class="fas fa-layer-group text-primary me-1"></i>Floor ' + r.floorNumber + '</div></div><div class="col-sm-4"><div class="text-muted">Status</div><div class="fw-bold">' + (r.available ? 'Available' : 'Occupied') + '</div></div></div>' + (r.description ? '<hr/><h5 class="fw-bold">Description</h5><p>' + r.description + '</p>' : '') + '</div></div></div>'; } } catch(ex) { document.getElementById('roomContent').innerHTML = '<div class="alert alert-danger">Failed to load room details</div>'; }
});
</script>
</body>
</html>

