<!DOCTYPE html>
<html lang="en">
<head><jsp:include page="/WEB-INF/jsp/fragments/head.jsp"/><title>Help & FAQ – Ocean View Resort</title></head>
<body>
<jsp:include page="/WEB-INF/jsp/fragments/sidebar.jsp"/>
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<div class="ov-main">
    <% request.setAttribute("pageTitle", "Help & FAQ"); %>
    <jsp:include page="/WEB-INF/jsp/fragments/topbar.jsp"/>
    <div class="ov-content">
        <div class="row justify-content-center"><div class="col-lg-10">
            <div class="text-center mb-4 animate-in"><h3 class="fw-bold">Help &amp; Frequently Asked Questions</h3><p class="text-muted">Find answers to common questions about using the Ocean View Resort system</p></div>
            <div class="ov-accordion accordion animate-in delay-1" id="faqAccordion">
                <div class="accordion-item"><h2 class="accordion-header"><button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#faq1">How do I create a new reservation?</button></h2><div id="faq1" class="accordion-collapse collapse show" data-bs-parent="#faqAccordion"><div class="accordion-body">Navigate to <strong>Reservations &gt; New Reservation</strong> from the sidebar. Follow the 3-step wizard: select a customer, choose an available room for your dates, and confirm the booking.</div></div></div>
                <div class="accordion-item"><h2 class="accordion-header"><button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#faq2">How do check-in and check-out work?</button></h2><div id="faq2" class="accordion-collapse collapse" data-bs-parent="#faqAccordion"><div class="accordion-body">Open the reservation details and click the <strong>Check In</strong> or <strong>Check Out</strong> button. The status will update automatically. A bill is generated upon check-out.</div></div></div>
                <div class="accordion-item"><h2 class="accordion-header"><button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#faq3">How do I generate a bill?</button></h2><div id="faq3" class="accordion-collapse collapse" data-bs-parent="#faqAccordion"><div class="accordion-body">Bills are automatically generated when a reservation is checked out. You can also manually generate a bill from the reservation details page. View all bills in <strong>Bills &amp; Payments</strong>.</div></div></div>
                <div class="accordion-item"><h2 class="accordion-header"><button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#faq4">Who can access the Reports section?</button></h2><div id="faq4" class="accordion-collapse collapse" data-bs-parent="#faqAccordion"><div class="accordion-body">Only users with the <strong>Manager</strong> role can access Reports. Staff and customers won't see the Reports link in the sidebar.</div></div></div>
                <div class="accordion-item"><h2 class="accordion-header"><button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#faq5">How do I change my password?</button></h2><div id="faq5" class="accordion-collapse collapse" data-bs-parent="#faqAccordion"><div class="accordion-body">Click the <strong>Profile</strong> icon in the top-right corner of the page. In your profile page, use the <strong>Change Password</strong> form.</div></div></div>
            </div>
            <div class="ov-card mt-4 animate-in delay-2"><div class="card-body-ov text-center"><h5 class="fw-bold mb-2"><i class="fas fa-headset me-2 text-primary"></i>Need More Help?</h5><p class="text-muted mb-3">Contact the Ocean View Resort IT support team</p><div class="d-flex justify-content-center gap-3 flex-wrap"><span class="text-muted"><i class="fas fa-envelope me-1"></i>support@oceanview.lk</span><span class="text-muted"><i class="fas fa-phone me-1"></i>+94 77 123 4567</span></div></div></div>
        </div></div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/fragments/scripts.jsp"/>
</body>
</html>

