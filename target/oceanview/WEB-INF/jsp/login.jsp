<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/>
    <title>Login – Ocean View Resort</title>
</head>
<body>
<div class="auth-wrapper">
    <div class="auth-left d-none d-md-flex">
        <div class="wave-icon">&#x1F30A;</div>
        <h1>Ocean View Resort</h1>
        <p class="tagline">Where the ocean meets luxury</p>
        <div class="d-flex gap-4 mt-3" style="opacity:.8">
            <div class="text-center"><i class="fas fa-bed fa-lg mb-1"></i><div style="font-size:.8rem">120+ Rooms</div></div>
            <div class="text-center"><i class="fas fa-utensils fa-lg mb-1"></i><div style="font-size:.8rem">Fine Dining</div></div>
            <div class="text-center"><i class="fas fa-spa fa-lg mb-1"></i><div style="font-size:.8rem">Luxury Spa</div></div>
            <div class="text-center"><i class="fas fa-umbrella-beach fa-lg mb-1"></i><div style="font-size:.8rem">Private Beach</div></div>
        </div>
    </div>

    <div class="auth-right">
        <div class="auth-card">
            <div class="brand-badge"><i class="fas fa-water"></i> OCEAN VIEW RESORT</div>
            <h2>Welcome back</h2>
            <p class="subtitle">Sign in to manage reservations &amp; services</p>

            <div id="loginError" class="alert alert-danger d-none" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i>
                <span id="loginErrorText">Invalid credentials</span>
            </div>

            <form id="loginForm" onsubmit="handleLogin(event)">
                <div class="form-floating">
                    <input type="text" class="form-control" id="username" placeholder="Username" required autofocus/>
                    <label for="username"><i class="fas fa-user me-2"></i>Username</label>
                </div>
                <div class="form-floating">
                    <input type="password" class="form-control" id="password" placeholder="Password" required/>
                    <label for="password"><i class="fas fa-lock me-2"></i>Password</label>
                </div>
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="remember"/>
                        <label class="form-check-label" for="remember" style="font-size:.88rem">Remember me</label>
                    </div>
                    <a href="#" style="font-size:.88rem">Forgot password?</a>
                </div>
                <button type="submit" class="btn btn-ocean w-100 py-3" id="loginBtn">
                    <span class="btn-text">Sign In</span>
                    <span class="btn-loading d-none">
                        <span class="spinner-border spinner-border-sm me-2"></span>Signing in...
                    </span>
                </button>
            </form>

            <div class="text-center mt-4">
                <span style="color:var(--ov-gray);font-size:.9rem">Don't have an account?</span>
                <a href="${pageContext.request.contextPath}/register" class="ms-1 fw-semibold">Create Account</a>
            </div>

            <div class="mt-4 p-3 rounded-3" style="background:var(--ov-sand);font-size:.82rem">
                <div class="fw-bold mb-2"><i class="fas fa-info-circle me-1"></i> Demo Credentials</div>
                <div class="row g-2">
                    <div class="col-6"><button class="btn btn-sm btn-outline-secondary w-100" onclick="fillDemo('pansilu','Manager@123')"><i class="fas fa-user-tie me-1"></i> Manager</button></div>
                    <div class="col-6"><button class="btn btn-sm btn-outline-secondary w-100" onclick="fillDemo('staff1','Staff@123')"><i class="fas fa-user-cog me-1"></i> Staff</button></div>
                    <div class="col-6"><button class="btn btn-sm btn-outline-secondary w-100" onclick="fillDemo('customer1','Customer@123')"><i class="fas fa-user me-1"></i> Customer</button></div>
                    <div class="col-6"><button class="btn btn-sm btn-outline-secondary w-100" onclick="fillDemo('staff2','Staff@123')"><i class="fas fa-user-cog me-1"></i> Staff 2</button></div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>

<script>
function fillDemo(user, pass) {
    document.getElementById('username').value = user;
    document.getElementById('password').value = pass;
}

async function handleLogin(e) {
    e.preventDefault();
    const btn = document.getElementById('loginBtn');
    const errorBox = document.getElementById('loginError');
    errorBox.classList.add('d-none');
    btn.querySelector('.btn-text').classList.add('d-none');
    btn.querySelector('.btn-loading').classList.remove('d-none');
    btn.disabled = true;

    try {
        const res = await fetch(CTX + '/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                username: document.getElementById('username').value,
                password: document.getElementById('password').value
            })
        });

        if (res.ok) {
            const data = await res.json();
            const u = data.user || {};
            OVAuth.save({
                accessToken: data.accessToken,
                refreshToken: data.refreshToken,
                user: {
                    userId: u.id || '', username: u.username || '',
                    firstName: u.firstName || '', lastName: u.lastName || '',
                    email: u.email || '', phone: u.phone || '', role: u.role || ''
                }
            });
            showToast('Login successful! Redirecting...', 'success');
            setTimeout(() => window.location.href = CTX + '/dashboard', 600);
        } else {
            const err = await res.json().catch(() => ({}));
            errorBox.classList.remove('d-none');
            document.getElementById('loginErrorText').textContent = err.message || 'Invalid username or password';
        }
    } catch (ex) {
        errorBox.classList.remove('d-none');
        document.getElementById('loginErrorText').textContent = 'Network error. Please try again.';
    } finally {
        btn.querySelector('.btn-text').classList.remove('d-none');
        btn.querySelector('.btn-loading').classList.add('d-none');
        btn.disabled = false;
    }
}
</script>
</body>
</html>

