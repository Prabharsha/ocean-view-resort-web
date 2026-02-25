/**
 * ═══════════════════════════════════════════════════════
 * Ocean View Resort – Application JavaScript
 * ═══════════════════════════════════════════════════════
 */

/* ── Token Management ────────────────────────────── */
const OVAuth = {
    getToken()        { return localStorage.getItem('ov_access_token'); },
    getRefresh()      { return localStorage.getItem('ov_refresh_token'); },
    getUser()         { const u = localStorage.getItem('ov_user'); return u ? JSON.parse(u) : null; },
    getRole()         { const u = this.getUser(); return u ? u.role : null; },

    save(data) {
        if (data.accessToken)  localStorage.setItem('ov_access_token', data.accessToken);
        if (data.refreshToken) localStorage.setItem('ov_refresh_token', data.refreshToken);
        if (data.user)         localStorage.setItem('ov_user', JSON.stringify(data.user));
    },

    clear() {
        localStorage.removeItem('ov_access_token');
        localStorage.removeItem('ov_refresh_token');
        localStorage.removeItem('ov_user');
    },

    isLoggedIn() { return !!this.getToken(); },

    headers() {
        const h = { 'Content-Type': 'application/json' };
        const t = this.getToken();
        if (t) h['Authorization'] = 'Bearer ' + t;
        return h;
    }
};

/* ── Context Path Helper ─────────────────────────── */
const CTX = document.querySelector('meta[name="ctx"]')?.content || '/oceanview';

/* ── API Fetch Wrapper ───────────────────────────── */
async function api(endpoint, options = {}) {
    const url = CTX + endpoint;
    const config = {
        headers: OVAuth.headers(),
        ...options
    };
    if (config.body && typeof config.body === 'object' && !(config.body instanceof FormData)) {
        config.body = JSON.stringify(config.body);
    }

    let res = await fetch(url, config);

    // Auto-refresh on 401
    if (res.status === 401 && OVAuth.getRefresh()) {
        const refreshRes = await fetch(CTX + '/api/auth/refresh', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken: OVAuth.getRefresh() })
        });
        if (refreshRes.ok) {
            const data = await refreshRes.json();
            OVAuth.save(data);
            config.headers = OVAuth.headers();
            res = await fetch(url, config);
        } else {
            OVAuth.clear();
            window.location.href = CTX + '/login';
            return;
        }
    }

    if (res.status === 401 || res.status === 403) {
        OVAuth.clear();
        window.location.href = CTX + '/login';
        return;
    }

    return res;
}

/* ── Toast Notifications ─────────────────────────── */
function showToast(message, type = 'success') {
    const existing = document.querySelector('.ov-toast');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.className = `ov-toast ${type}`;
    toast.innerHTML = `
        <div class="d-flex align-items-center gap-2">
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : type === 'warning' ? 'exclamation-triangle' : 'info-circle'}"></i>
            <span>${message}</span>
            <button onclick="this.closest('.ov-toast').remove()" class="btn-close btn-close-white ms-auto" style="font-size:.6rem"></button>
        </div>`;
    document.body.appendChild(toast);

    requestAnimationFrame(() => toast.classList.add('show'));
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 350);
    }, 4000);
}

/* ── Loading Overlay ─────────────────────────────── */
function showLoading(message = 'Loading...') {
    if (document.getElementById('ov-loading')) return;
    const overlay = document.createElement('div');
    overlay.id = 'ov-loading';
    overlay.className = 'loading-overlay';
    overlay.innerHTML = `<div class="ov-spinner"></div><span>${message}</span>`;
    document.body.appendChild(overlay);
}

function hideLoading() {
    const el = document.getElementById('ov-loading');
    if (el) el.remove();
}

/* ── Format Helpers ──────────────────────────────── */
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-GB', { style: 'currency', currency: 'GBP' }).format(amount || 0);
}

function formatDate(dateStr) {
    if (!dateStr) return '—';
    return new Intl.DateTimeFormat('en-GB', { year: 'numeric', month: 'short', day: 'numeric' }).format(new Date(dateStr));
}

function formatDateTime(dateStr) {
    if (!dateStr) return '—';
    return new Intl.DateTimeFormat('en-GB', {
        year: 'numeric', month: 'short', day: 'numeric',
        hour: '2-digit', minute: '2-digit'
    }).format(new Date(dateStr));
}

/* ── Status Badge HTML ───────────────────────────── */
function statusBadge(status) {
    const map = {
        'PENDING':     'badge-pending',
        'CONFIRMED':   'badge-confirmed',
        'CHECKED_IN':  'badge-checked-in',
        'CHECKED_OUT': 'badge-checked-out',
        'CANCELLED':   'badge-cancelled',
        'COMPLETED':   'badge-completed',
        'PAID':        'badge-paid',
        'UNPAID':      'badge-unpaid',
        'PARTIAL':     'badge-partial'
    };
    const cls = map[status] || 'badge-pending';
    const label = (status || 'UNKNOWN').replace(/_/g, ' ');
    return `<span class="badge-status ${cls}">${label}</span>`;
}

/* ── Room Type Badge ─────────────────────────────── */
function roomTypeBadge(type) {
    const map = {
        'STANDARD':     'badge-room-standard',
        'DELUXE':       'badge-room-deluxe',
        'SUITE':        'badge-room-suite',
        'PRESIDENTIAL': 'badge-room-presidential',
        'FAMILY':       'badge-room-family'
    };
    const cls = map[type] || 'badge-room-standard';
    return `<span class="badge-status ${cls}">${type || 'STANDARD'}</span>`;
}

/* ── Sidebar Active Link ─────────────────────────── */
function initSidebar() {
    const path = window.location.pathname.replace(CTX, '');
    document.querySelectorAll('.ov-sidebar .nav-link').forEach(link => {
        const href = link.getAttribute('href')?.replace(CTX, '') || '';
        if ((href === '/' && path === '/') ||
            (href === '/dashboard' && (path === '/' || path === '/dashboard')) ||
            (href !== '/' && href !== '/dashboard' && path.startsWith(href))) {
            link.classList.add('active');
        }
    });

    // Mobile toggle
    const toggle = document.querySelector('.sidebar-toggle');
    const sidebar = document.querySelector('.ov-sidebar');
    const overlay = document.querySelector('.sidebar-overlay');
    if (toggle && sidebar) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('show');
            overlay?.classList.toggle('show');
        });
        overlay?.addEventListener('click', () => {
            sidebar.classList.remove('show');
            overlay.classList.remove('show');
        });
    }

    // User info in sidebar
    updateUserUI();
}

/* ── Update UI with User Info ────────────────────── */
function updateUserUI() {
    const user = OVAuth.getUser();
    if (!user) return;

    const nameEl = document.querySelector('.user-name');
    const roleEl = document.querySelector('.user-role');
    const avatarEl = document.querySelector('.user-avatar');
    const initials = (user.firstName?.[0] || '') + (user.lastName?.[0] || user.username?.[0] || '');

    if (nameEl) nameEl.textContent = user.firstName ? `${user.firstName} ${user.lastName || ''}` : user.username;
    if (roleEl) roleEl.textContent = user.role || 'USER';
    if (avatarEl) avatarEl.textContent = initials.toUpperCase() || 'U';

    // Role-based visibility
    const role = (user.role || '').toUpperCase();
    document.querySelectorAll('[data-role]').forEach(el => {
        const allowed = el.dataset.role.split(',').map(r => r.trim().toUpperCase());
        el.style.display = allowed.includes(role) ? '' : 'none';
    });
}

/* ── Pagination Builder ──────────────────────────── */
function buildPagination(containerId, currentPage, totalPages, onPageClick) {
    const container = document.getElementById(containerId);
    if (!container || totalPages <= 1) { if (container) container.innerHTML = ''; return; }

    let html = '<nav><ul class="pagination ov-pagination justify-content-center mb-0">';
    html += `<li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">&laquo;</a></li>`;

    const start = Math.max(0, currentPage - 2);
    const end   = Math.min(totalPages - 1, currentPage + 2);

    for (let i = start; i <= end; i++) {
        html += `<li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i + 1}</a></li>`;
    }

    html += `<li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">&raquo;</a></li>`;
    html += '</ul></nav>';
    container.innerHTML = html;

    container.querySelectorAll('.page-link').forEach(link => {
        link.addEventListener('click', e => {
            e.preventDefault();
            const page = parseInt(link.dataset.page);
            if (page >= 0 && page < totalPages) onPageClick(page);
        });
    });
}

/* ── Confirm Dialog ──────────────────────────────── */
function confirmAction(message) {
    return new Promise(resolve => {
        if (confirm(message)) resolve(true);
        else resolve(false);
    });
}

/* ── DOMContentLoaded ────────────────────────────── */
document.addEventListener('DOMContentLoaded', () => {
    initSidebar();

    // Auto-redirect if not logged in (except auth pages)
    const path = window.location.pathname.replace(CTX, '');
    const publicPaths = ['/login', '/register', '/error', ''];
    if (!OVAuth.isLoggedIn() && !publicPaths.includes(path)) {
        window.location.href = CTX + '/login';
    }
});
