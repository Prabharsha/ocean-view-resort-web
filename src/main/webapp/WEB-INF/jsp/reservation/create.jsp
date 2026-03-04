<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/>
    <title>New Reservation – Ocean View Resort</title>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "New Reservation"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content">
        <div class="ov-card animate-in"><div class="card-body-ov">
            <!-- Wizard Steps -->
            <div class="wizard-steps">
                <div class="wizard-step active" id="ws1"><div class="step-circle">1</div><div class="step-label">Guest Info</div></div>
                <div class="wizard-step" id="ws2"><div class="step-circle">2</div><div class="step-label">Room Selection</div></div>
                <div class="wizard-step" id="ws3"><div class="step-circle">3</div><div class="step-label">Confirmation</div></div>
            </div>
            <form id="reservationForm" onsubmit="submitReservation(event)">
                <!-- Step 1 -->
                <div class="wizard-panel active" id="panel1">
                    <h5 class="fw-bold mb-3"><i class="fas fa-user-circle me-2 text-primary"></i>Guest Information &amp; Dates</h5>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Customer <span class="text-danger">*</span></label>
                            <div id="customerSearchWrap" class="position-relative">
                                <div class="input-group"><span class="input-group-text bg-white border-end-0"><i class="fas fa-search text-muted" id="searchIcon"></i></span>
                                <input type="text" class="form-control ov-input border-start-0" id="customerSearch" autocomplete="off" placeholder="Search by name, email or phone..." oninput="onSearchInput(this.value)"/></div>
                                <div id="customerDropdown" style="display:none;position:absolute;top:100%;left:0;right:0;z-index:1050;background:#fff;border:1px solid #dee2e6;border-radius:0 0 8px 8px;box-shadow:0 6px 20px rgba(0,0,0,.12);max-height:280px;overflow-y:auto"></div>
                            </div>
                            <div id="selectedCustomerCard" class="mt-2 p-2 rounded-3 d-flex align-items-center gap-2" style="display:none!important;background:var(--ov-sand)">
                                <div id="scAvatar" style="width:38px;height:38px;border-radius:50%;background:var(--ov-primary);color:#fff;font-size:.85rem;display:flex;align-items:center;justify-content:center;flex-shrink:0;font-weight:600"></div>
                                <div class="flex-grow-1 overflow-hidden"><div class="fw-semibold text-truncate" id="scName"></div><div class="text-muted text-truncate" style="font-size:.76rem" id="scMeta"></div></div>
                                <button type="button" class="btn btn-sm btn-outline-secondary flex-shrink-0" onclick="clearCustomer()" title="Change customer"><i class="fas fa-times"></i></button>
                            </div>
                            <input type="hidden" id="customerId"/>
                            <div class="form-text">Customer not registered? <a href="#" data-bs-toggle="modal" data-bs-target="#registerCustomerModal" style="color:var(--ov-primary);font-weight:500">Register here <i class="fas fa-user-plus" style="font-size:.75rem"></i></a></div>
                        </div>
                        <div class="col-md-6"><label class="form-label fw-semibold">Number of Guests</label><input type="number" class="form-control ov-input" id="numberOfGuests" min="1" max="10" value="1" required/></div>
                        <div class="col-md-6"><label class="form-label fw-semibold">Check-in Date</label><input type="date" class="form-control ov-input" id="checkInDate" required onchange="checkAvailability()"/></div>
                        <div class="col-md-6"><label class="form-label fw-semibold">Check-out Date</label><input type="date" class="form-control ov-input" id="checkOutDate" required onchange="checkAvailability()"/></div>
                        <div class="col-12"><label class="form-label fw-semibold">Special Requests</label><textarea class="form-control ov-input" id="specialRequests" rows="3" placeholder="Any special requirements..."></textarea></div>
                    </div>
                    <div class="d-flex justify-content-end mt-4"><button type="button" class="btn btn-ocean" onclick="goToStep(2)">Next: Select Room <i class="fas fa-arrow-right ms-2"></i></button></div>
                </div>
                <!-- Step 2 -->
                <div class="wizard-panel" id="panel2">
                    <h5 class="fw-bold mb-3"><i class="fas fa-bed me-2 text-primary"></i>Select a Room</h5>
                    <div class="filter-bar"><select class="form-select ov-input" style="width:auto" id="roomTypeFilter" onchange="filterRooms()"><option value="">All Room Types</option><option value="STANDARD">Standard</option><option value="DELUXE">Deluxe</option><option value="SUITE">Suite</option><option value="PENTHOUSE">Penthouse</option></select></div>
                    <div class="row g-3" id="availableRooms"><div class="col-12 text-center py-4 text-muted"><i class="fas fa-info-circle me-2"></i>Select check-in and check-out dates first</div></div>
                    <input type="hidden" id="selectedRoomId"/>
                    <div class="d-flex justify-content-between mt-4"><button type="button" class="btn btn-outline-ocean" onclick="goToStep(1)"><i class="fas fa-arrow-left me-2"></i> Back</button><button type="button" class="btn btn-ocean" onclick="goToStep(3)" id="toStep3Btn" disabled>Next: Confirm <i class="fas fa-arrow-right ms-2"></i></button></div>
                </div>
                <!-- Step 3 -->
                <div class="wizard-panel" id="panel3">
                    <h5 class="fw-bold mb-3"><i class="fas fa-clipboard-check me-2 text-primary"></i>Booking Summary</h5>
                    <div class="row g-3">
                        <div class="col-md-6"><div class="p-3 rounded-3" style="background:var(--ov-sand)"><h6 class="fw-bold mb-3">Guest Details</h6><div class="mb-2"><span class="text-muted">Customer:</span> <strong id="confCustomer"></strong></div><div class="mb-2"><span class="text-muted">Guests:</span> <strong id="confGuests"></strong></div><div><span class="text-muted">Special Requests:</span> <strong id="confRequests">—</strong></div></div></div>
                        <div class="col-md-6"><div class="p-3 rounded-3" style="background:var(--ov-light)"><h6 class="fw-bold mb-3">Stay Details</h6><div class="mb-2"><span class="text-muted">Room:</span> <strong id="confRoom"></strong></div><div class="mb-2"><span class="text-muted">Check-in:</span> <strong id="confCheckIn"></strong></div><div class="mb-2"><span class="text-muted">Check-out:</span> <strong id="confCheckOut"></strong></div><div class="mb-2"><span class="text-muted">Nights:</span> <strong id="confNights"></strong></div><hr/><div class="d-flex justify-content-between"><span class="fw-bold" style="font-size:1.1rem">Estimated Total</span><span class="fw-bold text-primary" style="font-size:1.2rem" id="confTotal">LKR 0.00</span></div></div></div>
                    </div>
                    <div class="d-flex justify-content-between mt-4"><button type="button" class="btn btn-outline-ocean" onclick="goToStep(2)"><i class="fas fa-arrow-left me-2"></i> Back</button><button type="submit" class="btn btn-ocean px-4" id="confirmBtn"><i class="fas fa-check-circle me-2"></i> Confirm Booking</button></div>
                </div>
            </form>
        </div></div>
    </div>
</div>

<!-- Register Customer Modal -->
<div class="modal fade" id="registerCustomerModal" tabindex="-1" aria-hidden="true"><div class="modal-dialog modal-lg modal-dialog-centered"><div class="modal-content">
    <div class="modal-header" style="background:var(--ov-primary);color:#fff"><h5 class="modal-title"><i class="fas fa-user-plus me-2"></i>Register New Customer</h5><button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button></div>
    <div class="modal-body">
        <form id="registerForm" onsubmit="registerNewCustomer(event)">
            <div class="row g-3">
                <div class="col-md-6"><label class="form-label fw-semibold">First Name *</label><input type="text" class="form-control ov-input" id="rc_firstName" required/></div>
                <div class="col-md-6"><label class="form-label fw-semibold">Last Name *</label><input type="text" class="form-control ov-input" id="rc_lastName" required/></div>
                <div class="col-md-6"><label class="form-label fw-semibold">Email *</label><input type="email" class="form-control ov-input" id="rc_email" required/></div>
                <div class="col-md-6"><label class="form-label fw-semibold">Phone</label><input type="text" class="form-control ov-input" id="rc_phone"/></div>
                <div class="col-12"><label class="form-label fw-semibold">Address</label><textarea class="form-control ov-input" id="rc_address" rows="2"></textarea></div>
            </div>
        </form>
    </div>
    <div class="modal-footer"><button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button><button type="submit" form="registerForm" class="btn btn-ocean" id="regSaveBtn"><i class="fas fa-user-check me-2"></i>Register &amp; Select</button></div>
</div></div></div>

<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
let allRooms = [], selectedRoom = null, selectedCustomerObj = null, searchDebounceTimer = null;
document.addEventListener('DOMContentLoaded', () => {
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('checkInDate').min = today;
    document.getElementById('checkOutDate').min = today;
    document.addEventListener('click', e => { if (!e.target.closest('#customerSearchWrap')) document.getElementById('customerDropdown').style.display = 'none'; });
});

function goToStep(step) {
    if (step === 2) { if (!document.getElementById('customerId').value) { showToast('Please select a customer', 'warning'); return; } const ci = document.getElementById('checkInDate').value, co = document.getElementById('checkOutDate').value; if (!ci || !co) { showToast('Please fill in dates', 'warning'); return; } if (new Date(co) <= new Date(ci)) { showToast('Check-out must be after check-in', 'warning'); return; } }
    if (step === 3 && !document.getElementById('selectedRoomId').value) { showToast('Please select a room', 'warning'); return; }
    for (let i = 1; i <= 3; i++) { document.getElementById('panel' + i).classList.toggle('active', i === step); const ws = document.getElementById('ws' + i); ws.classList.remove('active', 'completed'); if (i < step) ws.classList.add('completed'); if (i === step) ws.classList.add('active'); }
    if (step === 3) populateConfirmation();
}

function onSearchInput(val) { clearTimeout(searchDebounceTimer); if (!val || val.length < 2) { document.getElementById('customerDropdown').style.display = 'none'; return; } searchDebounceTimer = setTimeout(() => fetchCustomers(val), 320); }
async function fetchCustomers(q) { const icon = document.getElementById('searchIcon'); icon.className = 'fas fa-spinner fa-spin text-muted'; const res = await api('/api/customers?q=' + encodeURIComponent(q)); icon.className = 'fas fa-search text-muted'; if (!res?.ok) return; renderDropdown(await res.json()); }
function renderDropdown(list) { const dd = document.getElementById('customerDropdown'); if (!list.length) { dd.innerHTML = '<div class="px-3 py-3 text-center" style="font-size:.87rem;color:#666">No customers found.</div>'; } else { dd.innerHTML = list.map(c => '<div class="d-flex align-items-center gap-2 px-3 py-2" style="cursor:pointer;border-bottom:1px solid #f3f3f3" onmouseenter="this.style.background=\'#f0f7ff\'" onmouseleave="this.style.background=\'\'" onclick=\'pickCustomer(' + JSON.stringify(c).replace(/'/g, "\\'") + ')\'><div style="width:36px;height:36px;border-radius:50%;background:var(--ov-primary);color:#fff;font-size:.8rem;font-weight:600;display:flex;align-items:center;justify-content:center">' + ((c.firstName||'')[0]||'') + ((c.lastName||'')[0]||'') + '</div><div class="flex-grow-1 overflow-hidden"><div class="fw-semibold text-truncate" style="font-size:.9rem">' + c.firstName + ' ' + c.lastName + '</div><div class="text-muted text-truncate" style="font-size:.76rem">' + c.email + '</div></div></div>').join(''); } dd.style.display = 'block'; }

function pickCustomer(c) { selectedCustomerObj = c; document.getElementById('customerId').value = c.id; document.getElementById('customerDropdown').style.display = 'none'; document.getElementById('scAvatar').textContent = ((c.firstName||'')[0]||'').toUpperCase()+((c.lastName||'')[0]||'').toUpperCase(); document.getElementById('scName').textContent = c.firstName + ' ' + c.lastName; document.getElementById('scMeta').textContent = c.email; document.getElementById('selectedCustomerCard').style.cssText = 'display:flex!important'; document.getElementById('customerSearchWrap').style.display = 'none'; }
function clearCustomer() { selectedCustomerObj = null; document.getElementById('customerId').value = ''; document.getElementById('selectedCustomerCard').style.cssText = 'display:none!important'; document.getElementById('customerSearchWrap').style.display = ''; document.getElementById('customerSearch').focus(); }

async function registerNewCustomer(e) { e.preventDefault(); const btn = document.getElementById('regSaveBtn'); btn.disabled = true; btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Registering...'; const res = await api('/api/customers', { method: 'POST', body: { firstName: document.getElementById('rc_firstName').value, lastName: document.getElementById('rc_lastName').value, email: document.getElementById('rc_email').value, phone: document.getElementById('rc_phone').value, address: document.getElementById('rc_address').value } }); if (res?.ok) { const newC = await res.json(); showToast('Customer registered!', 'success'); bootstrap.Modal.getInstance(document.getElementById('registerCustomerModal'))?.hide(); document.getElementById('registerForm').reset(); pickCustomer(newC); } else { const err = await res?.json().catch(()=>({})); showToast(err.message || 'Registration failed', 'error'); } btn.disabled = false; btn.innerHTML = '<i class="fas fa-user-check me-2"></i>Register & Select'; }

async function checkAvailability() { const ci = document.getElementById('checkInDate').value, co = document.getElementById('checkOutDate').value; if (!ci || !co || new Date(co) <= new Date(ci)) return; const container = document.getElementById('availableRooms'); container.innerHTML = '<div class="col-12 text-center py-4"><div class="ov-spinner"></div><br/>Checking...</div>'; try { const res = await api('/api/rooms/available?checkIn=' + ci + '&checkOut=' + co); if (res?.ok) { allRooms = await res.json(); renderRooms(allRooms); } } catch(ex) { container.innerHTML = '<div class="col-12 text-center py-4 text-danger">Failed to load rooms</div>'; } }
function filterRooms() { const type = document.getElementById('roomTypeFilter').value; renderRooms(type ? allRooms.filter(r => r.roomType === type) : allRooms); }
function renderRooms(rooms) { const container = document.getElementById('availableRooms'); if (!rooms.length) { container.innerHTML = '<div class="col-12"><div class="empty-state"><i class="fas fa-bed"></i><h5>No Rooms Available</h5><p>Try different dates.</p></div></div>'; return; } container.innerHTML = rooms.map(r => '<div class="col-md-6 col-lg-4"><div class="room-card" style="cursor:pointer" onclick="selectRoom(\'' + r.id + '\', this)"><div class="room-img"><i class="fas fa-' + (r.roomType==='SUITE'?'crown':r.roomType==='PENTHOUSE'?'star':r.roomType==='DELUXE'?'gem':'bed') + '"></i><div class="room-price">' + formatCurrency(r.ratePerNight) + '/night</div></div><div class="room-body"><h5>Room ' + (r.roomNumber||r.id) + '</h5><div>' + roomTypeBadge(r.roomType) + '</div><div class="room-features"><span><i class="fas fa-users"></i> ' + (r.capacity||2) + ' guests</span><span><i class="fas fa-layer-group"></i> Floor ' + (r.floorNumber||'-') + '</span></div><div class="mt-2 text-muted" style="font-size:.82rem">' + (r.description||'Comfortable room') + '</div></div></div></div>').join(''); }
function selectRoom(id, el) { selectedRoom = allRooms.find(r => r.id === id); document.getElementById('selectedRoomId').value = id; document.getElementById('toStep3Btn').disabled = false; document.querySelectorAll('.room-card').forEach(c => c.classList.remove('border','border-primary','border-2')); el.classList.add('border','border-primary','border-2'); showToast('Room ' + (selectedRoom?.roomNumber||id) + ' selected', 'info'); }

function populateConfirmation() { const ci = document.getElementById('checkInDate').value, co = document.getElementById('checkOutDate').value, nights = Math.ceil((new Date(co) - new Date(ci)) / 86400000), rate = selectedRoom?.ratePerNight || 0; document.getElementById('confCustomer').textContent = selectedCustomerObj ? (selectedCustomerObj.firstName + ' ' + selectedCustomerObj.lastName) : ''; document.getElementById('confGuests').textContent = document.getElementById('numberOfGuests').value; document.getElementById('confRequests').textContent = document.getElementById('specialRequests').value || '—'; document.getElementById('confRoom').textContent = 'Room ' + (selectedRoom?.roomNumber||'') + ' (' + (selectedRoom?.roomType||'') + ')'; document.getElementById('confCheckIn').textContent = formatDate(ci); document.getElementById('confCheckOut').textContent = formatDate(co); document.getElementById('confNights').textContent = nights; document.getElementById('confTotal').textContent = formatCurrency(nights * rate); }

async function submitReservation(e) { e.preventDefault(); const btn = document.getElementById('confirmBtn'); btn.disabled = true; btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Booking...'; try { const res = await api('/api/reservations', { method: 'POST', body: { customerId: document.getElementById('customerId').value, roomId: document.getElementById('selectedRoomId').value, checkInDate: document.getElementById('checkInDate').value, checkOutDate: document.getElementById('checkOutDate').value, numGuests: parseInt(document.getElementById('numberOfGuests').value), specialRequests: document.getElementById('specialRequests').value } }); if (res?.ok) { const data = await res.json(); showToast('Reservation created!', 'success'); setTimeout(() => window.location.href = CTX + '/reservations/view/' + (data.id || data.reservationNumber), 800); } else { const err = await res.json().catch(()=>({})); showToast(err.message || 'Failed to create', 'error'); btn.disabled = false; btn.innerHTML = '<i class="fas fa-check-circle me-2"></i> Confirm Booking'; } } catch(ex) { showToast('Network error', 'error'); btn.disabled = false; btn.innerHTML = '<i class="fas fa-check-circle me-2"></i> Confirm Booking'; } }
</script>
</body>
</html>

