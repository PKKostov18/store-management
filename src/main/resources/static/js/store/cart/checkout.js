import { getCart, clearCart } from "./cartService.js";
import { closeAllModals } from "../modals/modalManager.js";
import { showCashierSelectionModal } from "../modals/cashierModal.js";
import { updateCartCount, renderCartModal } from "./cartUI.js";
import { showErrorModal } from "../modals/errorModal.js";
import { showSuccessModal } from "../modals/successModal.js";

let pendingPaymentHasMoney = null;

export function setupCheckoutButton() {
  document.getElementById("checkout-btn").addEventListener("click", () => {
    closeAllModals();
    showCashierSelectionModal();
  });

  document.querySelector("#confirmCheckoutModal .btn-success").addEventListener("click", () => setHasMoney(true));
  document.querySelector("#confirmCheckoutModal .btn-danger").addEventListener("click", closeAllModals);
}

function setHasMoney(hasMoney) {
  closeAllModals();
  if (!hasMoney) return;

  const cashierId = localStorage.getItem("selectedCashierId");
  if (!cashierId) {
    pendingPaymentHasMoney = hasMoney;
    showCashierSelectionModal();
    return;
  }

  performCheckout(cashierId);
}

function performCheckout(cashierId) {
  const cart = getCart();
  if (cart.length === 0) {
    showErrorModal("Количката е празна.");
    return;
  }

  const payload = cart.map(item => ({
    productId: item.productId,
    quantity: item.quantity,
    sellingPrice: item.unitPrice
  }));

  fetch(`/receipt/checkout?cashierId=${cashierId}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
    .then(res => {
      if (!res.ok) throw new Error("Плащането не беше успешно.");
      return res.json();
    })
    .then(data => {
      clearCart();
      updateCartCount();
      bootstrap.Modal.getInstance(document.getElementById('confirmCheckoutModal'))?.hide();
      showSuccessModal(`✅ Успешно плащане. Сума: ${data.totalAmount.toFixed(2)} лв.`);
      renderCartModal();
    })
    .catch(err => showErrorModal(err.message || "Възникна проблем при плащането."));
}
