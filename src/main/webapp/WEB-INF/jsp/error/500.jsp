<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/>
    <title>500 – Server Error</title>
</head>
<body style="background:var(--ov-light)">
<div class="d-flex align-items-center justify-content-center" style="min-height:100vh">
    <div class="text-center">
        <div style="font-size:6rem;color:var(--ov-danger);font-weight:800">500</div>
        <h3 class="fw-bold mt-2 mb-3">Internal Server Error</h3>
        <p class="text-muted mb-4">Something went wrong on our end. Please try again later.</p>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-ocean"><i class="fas fa-home me-2"></i>Back to Dashboard</a>
    </div>
</div>
</body>
</html>

