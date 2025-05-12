const assignModal = document.getElementById('assignCashierModal');

assignModal.addEventListener('show.bs.modal', function (event) {
    const button = event.relatedTarget;
    const registerId = button.getAttribute('data-register-id');
    const cashierSelect = document.getElementById('cashierId');
    const form = document.getElementById('assignCashierForm');

    document.getElementById('cashRegisterId').value = registerId;
    form.action = '/cashRegister/' + registerId + '/assign';
});

document.addEventListener("DOMContentLoaded", function () {
    const productGrid = document.querySelector(".product-grid");
    const products = productGrid.querySelectorAll(".product-card");

    if (products.length >= 4) {
        productGrid.classList.add("has-four-or-more");
    }
});