<!DOCTYPE html>
<html lang="en">
<head><jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/><title>Customers – Ocean View Resort</title></head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "Manage Customers"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-2 animate-in"><div><h4 class="fw-bold mb-1">Customer Management</h4><p class="text-muted mb-0">View and manage registered customers</p></div></div>
        <div class="filter-bar animate-in delay-1"><div class="search-box"><i class="fas fa-search"></i><input type="text" class="form-control ov-input" id="custSearch" placeholder="Search customers..." oninput="loadCustomers()"/></div></div>
        <div class="ov-card animate-in delay-2"><div class="card-body-ov p-0"><div class="table-responsive"><table class="ov-table"><thead><tr><th>Name</th><th>Email</th><th>Phone</th><th>Loyalty Points</th><th>Actions</th></tr></thead><tbody id="custTable"><tr><td colspan="5" class="text-center py-4"><div class="ov-spinner"></div></td></tr></tbody></table></div></div></div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
document.addEventListener('DOMContentLoaded', loadCustomers);
async function loadCustomers() { const q = document.getElementById('custSearch')?.value || ''; try { const res = await api('/api/customers' + (q ? '?q=' + encodeURIComponent(q) : '')); if (res?.ok) { const customers = await res.json(); const tbody = document.getElementById('custTable');
    if (!customers.length) { tbody.innerHTML = '<tr><td colspan="5"><div class="empty-state"><i class="fas fa-users"></i><h5>No Customers</h5></div></td></tr>'; return; }
    tbody.innerHTML = customers.map(c => '<tr><td class="fw-semibold">' + c.firstName + ' ' + c.lastName + '</td><td>' + (c.email||'—') + '</td><td>' + (c.phone||'—') + '</td><td><i class="fas fa-star text-warning me-1"></i>' + (c.loyaltyPoints||0) + '</td><td><a href="${pageContext.request.contextPath}/customers/' + c.id + '" class="btn btn-sm btn-outline-primary"><i class="fas fa-eye"></i></a></td></tr>').join('');
}} catch(ex) { document.getElementById('custTable').innerHTML = '<tr><td colspan="5" class="text-danger text-center">Failed</td></tr>'; } }
</script>
</body>
</html>

