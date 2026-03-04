<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/>
    <title>Register – Ocean View Resort</title>
</head>
<body>
<div class="auth-wrapper">
    <div class="auth-left d-none d-md-flex">
        <div class="wave-icon">&#x1F3D6;&#xFE0F;</div>
        <h1>Join Us</h1>
        <p class="tagline">Create your account and start your luxury experience</p>
        <div class="mt-3 p-3 rounded-3 text-start" style="background:rgba(255,255,255,.1);max-width:340px">
            <div class="d-flex align-items-center gap-2 mb-2"><i class="fas fa-check-circle"></i> <span style="font-size:.9rem">Instant online booking</span></div>
            <div class="d-flex align-items-center gap-2 mb-2"><i class="fas fa-check-circle"></i> <span style="font-size:.9rem">Exclusive member discounts</span></div>
            <div class="d-flex align-items-center gap-2 mb-2"><i class="fas fa-check-circle"></i> <span style="font-size:.9rem">Loyalty points program</span></div>
            <div class="d-flex align-items-center gap-2"><i class="fas fa-check-circle"></i> <span style="font-size:.9rem">24/7 concierge support</span></div>
        </div>
    </div>

    <div class="auth-right">
        <div class="auth-card">
            <div class="brand-badge"><i class="fas fa-water"></i> OCEAN VIEW RESORT</div>
            <h2>Create Account</h2>
            <p class="subtitle">Fill in your details to get started</p>

            <div id="registerError" class="alert alert-danger d-none" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i><span id="registerErrorText"></span>
            </div>
            <div id="registerSuccess" class="alert alert-success d-none" role="alert">
                <i class="fas fa-check-circle me-2"></i>
                Account created successfully! <a href="${pageContext.request.contextPath}/login" class="fw-bold">Sign in now</a>
            </div>

            <form id="registerForm" onsubmit="handleRegister(event)">
                <div class="row g-2">
                    <div class="col-6"><div class="form-floating"><input type="text" class="form-control" id="firstName" placeholder="First Name" required/><label for="firstName">First Name</label></div></div>
                    <div class="col-6"><div class="form-floating"><input type="text" class="form-control" id="lastName" placeholder="Last Name" required/><label for="lastName">Last Name</label></div></div>
                </div>
                <div class="form-floating mt-3"><input type="email" class="form-control" id="email" placeholder="Email" required/><label for="email"><i class="fas fa-envelope me-2"></i>Email Address</label></div>
                <div class="form-floating mt-3"><input type="tel" class="form-control" id="phone" placeholder="Phone"/><label for="phone"><i class="fas fa-phone me-2"></i>Phone Number</label></div>
                <div class="form-floating mt-3"><input type="text" class="form-control" id="regUsername" placeholder="Username" required/><label for="regUsername"><i class="fas fa-user me-2"></i>Username</label></div>
                <div class="form-floating mt-3"><input type="password" class="form-control" id="regPassword" placeholder="Password" required minlength="8"/><label for="regPassword"><i class="fas fa-lock me-2"></i>Password (min 8 chars)</label></div>
                <div class="form-floating mt-3"><input type="password" class="form-control" id="confirmPassword" placeholder="Confirm Password" required/><label for="confirmPassword"><i class="fas fa-lock me-2"></i>Confirm Password</label></div>
                <div class="form-check mt-3"><input class="form-check-input" type="checkbox" id="terms" required/><label class="form-check-label" for="terms" style="font-size:.85rem">I agree to the <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a></label></div>
                <button type="submit" class="btn btn-ocean w-100 py-3 mt-3" id="registerBtn">
                    <span class="btn-text">Create Account</span>
                    <span class="btn-loading d-none"><span class="spinner-border spinner-border-sm me-2"></span>Creating...</span>
                </button>
            </form>
            <div class="text-center mt-4">
                <span style="color:var(--ov-gray);font-size:.9rem">Already have an account?</span>
                <a href="${pageContext.request.contextPath}/login" class="ms-1 fw-semibold">Sign In</a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
<script>
async function handleRegister(e) {
    e.preventDefault();
    const btn = document.getElementById('registerBtn');
    const errorBox = document.getElementById('registerError');
    const successBox = document.getElementById('registerSuccess');
    errorBox.classList.add('d-none'); successBox.classList.add('d-none');

    const pass = document.getElementById('regPassword').value;
    if (pass !== document.getElementById('confirmPassword').value) {
        errorBox.classList.remove('d-none');
        document.getElementById('registerErrorText').textContent = 'Passwords do not match';
        return;
    }

    btn.querySelector('.btn-text').classList.add('d-none');
    btn.querySelector('.btn-loading').classList.remove('d-none');
    btn.disabled = true;

    try {
        const res = await fetch(CTX + '/api/auth/register', {
            method: 'POST', headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                firstName: document.getElementById('firstName').value,
                lastName: document.getElementById('lastName').value,
                email: document.getElementById('email').value,
                phone: document.getElementById('phone').value,
                username: document.getElementById('regUsername').value,
                password: pass
            })
        });
        if (res.ok) { successBox.classList.remove('d-none'); document.getElementById('registerForm').reset(); }
        else { const err = await res.json().catch(()=>({})); errorBox.classList.remove('d-none'); document.getElementById('registerErrorText').textContent = err.message || 'Registration failed.'; }
    } catch (ex) { errorBox.classList.remove('d-none'); document.getElementById('registerErrorText').textContent = 'Network error.'; }
    finally { btn.querySelector('.btn-text').classList.remove('d-none'); btn.querySelector('.btn-loading').classList.add('d-none'); btn.disabled = false; }
}
</script>
</body>
</html>

