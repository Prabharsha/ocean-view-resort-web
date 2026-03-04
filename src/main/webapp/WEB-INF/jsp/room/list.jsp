<!DOCTYPE html>
<html lang="en">
<head><jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/><title>Room Management – Ocean View Resort</title></head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "Room Management"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-2 animate-in">
            <div><h4 class="fw-bold mb-1">Room Management</h4><p class="text-muted mb-0">Manage hotel rooms and availability</p></div>
            <button class="btn btn-ocean" data-bs-toggle="modal" data-bs-target="#addRoomModal" data-role="MANAGER,STAFF"><i class="fas fa-plus-circle me-2"></i>Add Room</button>
        </div>
        <div class="filter-bar animate-in delay-1">
            <div class="search-box"><i class="fas fa-search"></i><input type="text" class="form-control ov-input" id="roomSearch" placeholder="Search rooms..." oninput="filterRooms()"/></div>
            <select class="form-select ov-input" style="width:auto" id="typeFilter" onchange="filterRooms()"><option value="">All Types</option><option value="STANDARD">Standard</option><option value="DELUXE">Deluxe</option><option value="SUITE">Suite</option><option value="PENTHOUSE">Penthouse</option></select>
        </div>
        <div class="row g-3" id="roomGrid"><div class="col-12 text-center py-5"><div class="ov-spinner"></div><br/>Loading rooms...</div></div>
    </div>
</div>

<!-- Add Room Modal -->
<div class="modal fade" id="addRoomModal" tabindex="-1"><div class="modal-dialog modal-dialog-centered"><div class="modal-content">
    <div class="modal-header" style="background:var(--ov-primary);color:#fff"><h5 class="modal-title"><i class="fas fa-plus-circle me-2"></i>Add New Room</h5><button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button></div>
    <div class="modal-body"><form id="addRoomForm" onsubmit="addRoom(event)">
        <div class="row g-3">
            <div class="col-6"><label class="form-label">Room Number *</label><input type="text" class="form-control ov-input" id="newRoomNumber" required/></div>
            <div class="col-6"><label class="form-label">Type *</label><select class="form-select ov-input" id="newRoomType" required><option value="STANDARD">Standard</option><option value="DELUXE">Deluxe</option><option value="SUITE">Suite</option><option value="PENTHOUSE">Penthouse</option></select></div>
            <div class="col-6"><label class="form-label">Floor *</label><input type="number" class="form-control ov-input" id="newFloor" min="1" required/></div>
            <div class="col-6"><label class="form-label">Capacity *</label><input type="number" class="form-control ov-input" id="newCapacity" min="1" required/></div>
            <div class="col-12"><label class="form-label">Rate Per Night (LKR) *</label><input type="number" class="form-control ov-input" id="newRate" step="0.01" required/></div>
            <div class="col-12"><label class="form-label">Description</label><textarea class="form-control ov-input" id="newDesc" rows="2"></textarea></div>
        </div>
    </form></div>
    <div class="modal-footer"><button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button><button type="submit" form="addRoomForm" class="btn btn-ocean">Add Room</button></div>
</div></div></div>

<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
let allRooms = [];
document.addEventListener('DOMContentLoaded', loadRooms);
async function loadRooms() { try { const res = await api('/api/rooms'); if (res?.ok) { allRooms = await res.json(); filterRooms(); } } catch(ex) { document.getElementById('roomGrid').innerHTML = '<div class="col-12 text-center text-danger py-4">Failed to load rooms</div>'; } }
function filterRooms() { const s = document.getElementById('roomSearch').value.toLowerCase(), t = document.getElementById('typeFilter').value; const filtered = allRooms.filter(r => { const matchSearch = !s || (r.roomNumber||'').toLowerCase().includes(s) || (r.description||'').toLowerCase().includes(s); return matchSearch && (!t || r.roomType === t); }); renderRooms(filtered); }
function renderRooms(rooms) { const grid = document.getElementById('roomGrid'); if (!rooms.length) { grid.innerHTML = '<div class="col-12"><div class="empty-state"><i class="fas fa-door-closed"></i><h5>No Rooms Found</h5></div></div>'; return; }
    grid.innerHTML = rooms.map(r => '<div class="col-sm-6 col-lg-4 col-xl-3"><div class="room-card"><div class="room-img"><i class="fas fa-' + (r.roomType==='SUITE'?'crown':r.roomType==='PENTHOUSE'?'star':r.roomType==='DELUXE'?'gem':'bed') + '"></i><div class="room-price">' + formatCurrency(r.ratePerNight) + '/night</div></div><div class="room-body"><h5>Room ' + r.roomNumber + '</h5><div>' + roomTypeBadge(r.roomType) + ' ' + (r.available ? '<span class="badge-status badge-confirmed">Available</span>' : '<span class="badge-status badge-cancelled">Occupied</span>') + '</div><div class="room-features"><span><i class="fas fa-users"></i> ' + r.capacity + ' guests</span><span><i class="fas fa-layer-group"></i> Floor ' + r.floorNumber + '</span></div><p class="text-muted mt-2" style="font-size:.82rem">' + (r.description||'') + '</p></div></div></div>').join(''); }
async function addRoom(e) { e.preventDefault(); const res = await api('/api/rooms', { method: 'POST', body: { roomNumber: document.getElementById('newRoomNumber').value, roomType: document.getElementById('newRoomType').value, floorNumber: parseInt(document.getElementById('newFloor').value), capacity: parseInt(document.getElementById('newCapacity').value), ratePerNight: parseFloat(document.getElementById('newRate').value), description: document.getElementById('newDesc').value, available: true } }); if (res?.ok) { showToast('Room added!', 'success'); bootstrap.Modal.getInstance(document.getElementById('addRoomModal'))?.hide(); loadRooms(); } else { const err = await res?.json().catch(()=>({})); showToast(err.message||'Failed', 'error'); } }
</script>
</body>
</html>

