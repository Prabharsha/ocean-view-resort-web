<!DOCTYPE html>
<html lang="en">
<head><jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/><title>Profile – Ocean View Resort</title></head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "My Profile"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content">
        <div class="row g-4">
            <div class="col-lg-4 animate-in"><div class="ov-card text-center"><div class="card-body-ov py-4">
                <div class="profile-avatar-lg mx-auto mb-3" id="profileAvatar">U</div>
                <h4 class="fw-bold" id="profileName">Loading...</h4>
                <p class="text-muted" id="profileRole">—</p>
                <hr/><div class="text-muted" style="font-size:.85rem" id="profileEmail">—</div><div class="text-muted" style="font-size:.85rem" id="profilePhone">—</div>
            </div></div></div>
            <div class="col-lg-8 animate-in delay-1"><div class="ov-card"><div class="card-header-ov"><h5><i class="fas fa-lock me-2 text-primary"></i>Change Password</h5></div><div class="card-body-ov">
                <form id="changePasswordForm" onsubmit="changePassword(event)">
                    <div class="mb-3"><label class="form-label fw-semibold">Current Password</label><input type="password" class="form-control ov-input" id="oldPassword" required/></div>
                    <div class="mb-3"><label class="form-label fw-semibold">New Password</label><input type="password" class="form-control ov-input" id="newPassword" required minlength="8"/></div>
                    <div class="mb-3"><label class="form-label fw-semibold">Confirm New Password</label><input type="password" class="form-control ov-input" id="confirmNewPassword" required/></div>
                    <button type="submit" class="btn btn-ocean"><i class="fas fa-key me-2"></i>Change Password</button>
                </form>
            </div></div></div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
document.addEventListener('DOMContentLoaded', () => {
    const user = OVAuth.getUser();
    if (user) {
        document.getElementById('profileAvatar').textContent = ((user.firstName||'')[0]||'').toUpperCase() + ((user.lastName||'')[0]||'').toUpperCase() || 'U';
        document.getElementById('profileName').textContent = (user.firstName || '') + ' ' + (user.lastName || '');
        document.getElementById('profileRole').textContent = user.role || '—';
        document.getElementById('profileEmail').textContent = user.email || '—';
        document.getElementById('profilePhone').textContent = user.phone || '—';
    }
});
async function changePassword(e) { e.preventDefault();
    if (document.getElementById('newPassword').value !== document.getElementById('confirmNewPassword').value) { showToast('Passwords do not match','error'); return; }
    try { const res = await api('/api/auth/change-password', { method: 'PUT', body: { oldPassword: document.getElementById('oldPassword').value, newPassword: document.getElementById('newPassword').value } });
        if (res?.ok) { showToast('Password changed!', 'success'); document.getElementById('changePasswordForm').reset(); } else { const err = await res?.json().catch(()=>({})); showToast(err.message || 'Failed', 'error'); }
    } catch(ex) { showToast('Network error', 'error'); }
}
</script>
</body>
</html>

