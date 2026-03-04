<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/>
    <title>404 – Not Found</title>
</head>
<body style="background:var(--ov-light)">
<div class="d-flex align-items-center justify-content-center" style="min-height:100vh">
    <div class="text-center">
        <div style="font-size:6rem;color:var(--ov-primary);font-weight:800">404</div>
        <h3 class="fw-bold mt-2 mb-3">Page Not Found</h3>
        <p class="text-muted mb-4">The page you're looking for doesn't exist or has been moved.</p>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-ocean"><i class="fas fa-home me-2"></i>Back to Dashboard</a>
    </div>
</div>
</body>
</html>

