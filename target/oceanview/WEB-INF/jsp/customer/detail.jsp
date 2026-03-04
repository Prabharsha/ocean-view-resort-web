<!DOCTYPE html>
<html lang="en">
<head><jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/><title>Customer Details – Ocean View Resort</title></head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "Customer Details"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content"><div id="custContent"><div class="text-center py-5"><div class="ov-spinner"></div></div></div></div>
</div>
<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
document.addEventListener('DOMContentLoaded', async () => {
    const id = window.location.pathname.split('/').pop();
    try { const res = await api('/api/customers/' + id); if (res?.ok) { const c = await res.json();
        document.getElementById('custContent').innerHTML = '<div class="animate-in"><a href="${pageContext.request.contextPath}/customers" class="btn btn-outline-ocean btn-sm mb-3"><i class="fas fa-arrow-left me-1"></i> Back</a><div class="ov-card"><div class="card-body-ov"><div class="d-flex align-items-center gap-3 mb-4"><div class="profile-avatar-lg">' + ((c.firstName||'')[0]||'').toUpperCase() + ((c.lastName||'')[0]||'').toUpperCase() + '</div><div><h4 class="fw-bold mb-0">' + c.firstName + ' ' + c.lastName + '</h4><p class="text-muted mb-0">' + (c.email||'') + '</p></div></div><div class="row g-3"><div class="col-sm-4"><div class="text-muted">Phone</div><div class="fw-bold">' + (c.phone||'—') + '</div></div><div class="col-sm-4"><div class="text-muted">Address</div><div class="fw-bold">' + (c.address||'—') + '</div></div><div class="col-sm-4"><div class="text-muted">Loyalty Points</div><div class="fw-bold"><i class="fas fa-star text-warning me-1"></i>' + (c.loyaltyPoints||0) + '</div></div></div></div></div></div>';
    }} catch(ex) { document.getElementById('custContent').innerHTML = '<div class="alert alert-danger">Failed to load customer</div>'; }
});
</script>
</body>
</html>

