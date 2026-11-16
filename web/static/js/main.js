// FitZone Client Side Scripts

document.addEventListener('DOMContentLoaded', function () {
    // Auto-dismiss alerts after 4 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 4000);
    });

    console.log("FitZone Gym Management Portal Loaded.");
});
