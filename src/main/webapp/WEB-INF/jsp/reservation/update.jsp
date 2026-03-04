<!DOCTYPE html>
<html lang="en">
<head><jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/><title>Update Reservation – Ocean View Resort</title></head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "Update Reservation"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content"><div class="row justify-content-center"><div class="col-lg-8">
        <div class="d-flex align-items-center gap-3 mb-4 animate-in"><a href="javascript:history.back()" class="btn btn-outline-ocean btn-sm"><i class="fas fa-arrow-left me-1"></i> Back</a><h4 class="fw-bold mb-0">Update Reservation</h4></div>
        <div class="ov-card animate-in delay-1"><div class="card-body-ov">
            <form id="updateForm" onsubmit="updateReservation(event)">
                <div class="row g-3">
                    <div class="col-md-6"><label class="form-label fw-semibold">Check-in Date</label><input type="date" class="form-control ov-input" id="updCheckIn" required/></div>
                    <div class="col-md-6"><label class="form-label fw-semibold">Check-out Date</label><input type="date" class="form-control ov-input" id="updCheckOut" required/></div>
                    <div class="col-md-6"><label class="form-label fw-semibold">Number of Guests</label><input type="number" class="form-control ov-input" id="updGuests" min="1" max="10" required/></div>
                    <div class="col-md-6"><label class="form-label fw-semibold">Room ID</label><input type="text" class="form-control ov-input" id="updRoomId" required/></div>
                    <div class="col-12"><label class="form-label fw-semibold">Special Requests</label><textarea class="form-control ov-input" id="updRequests" rows="3"></textarea></div>
                </div>
                <div class="d-flex justify-content-end gap-2 mt-4"><a href="javascript:history.back()" class="btn btn-outline-secondary">Cancel</a><button type="submit" class="btn btn-ocean" id="updateBtn"><i class="fas fa-save me-2"></i>Save Changes</button></div>
            </form>
        </div></div>
    </div></div></div>
</div>
<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
const resId = window.location.pathname.split('/').filter(Boolean).pop();
document.addEventListener('DOMContentLoaded', async () => { try { const res = await api('/api/reservations/' + resId); if (res?.ok) { const r = await res.json(); document.getElementById('updCheckIn').value = r.checkInDate; document.getElementById('updCheckOut').value = r.checkOutDate; document.getElementById('updGuests').value = r.numGuests || 1; document.getElementById('updRoomId').value = r.roomId; document.getElementById('updRequests').value = r.specialRequests || ''; } } catch(ex) { showToast('Failed to load', 'error'); } });
async function updateReservation(e) { e.preventDefault(); const btn = document.getElementById('updateBtn'); btn.disabled = true; btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Saving...'; try { const res = await api('/api/reservations/' + resId + '?status=PENDING', { method: 'PUT' }); if (res?.ok) { showToast('Updated!', 'success'); setTimeout(() => window.location.href = CTX + '/reservations/view/' + resId, 800); } else { showToast('Update failed', 'error'); } } catch(ex) { showToast('Network error', 'error'); } finally { btn.disabled = false; btn.innerHTML = '<i class="fas fa-save me-2"></i>Save Changes'; } }
</script>
</body>
</html>

