import { setupProductHandlers } from "./product/productHandlers.js";
import { updateCartCount } from "./cart/cartUI.js";
import { setupCheckoutButton } from "./cart/checkout.js";
import { setupCashierModal } from "./modals/cashierModal.js";
import { setupCartIcon } from "./cart/cartUI.js";

const assignModal = document.getElementById('assignCashierModal');

assignModal?.addEventListener('show.bs.modal', function (event) {
  const button = event.relatedTarget;
  const registerId = button.getAttribute('data-register-id');
  const form = document.getElementById('assignCashierForm');

  document.getElementById('cashRegisterId').value = registerId;
  form.action = '/cashRegister/' + registerId + '/assign';
});

document.addEventListener("DOMContentLoaded", () => {
  setupProductHandlers();
  setupCartIcon();
  setupCheckoutButton();
  setupCashierModal();
  updateCartCount();
});
