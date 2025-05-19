import { closeAllModals } from "./modalManager.js";
import { showErrorModal } from "./errorModal.js";

export function setupCashierModal() {
  document.getElementById("confirmSelectedCashierBtn").addEventListener("click", () => {
    const select = document.getElementById("selectAssignedCashier");
    const cashierId = select.value;
    if (!cashierId) return;

    localStorage.setItem("selectedCashierId", cashierId);
    bootstrap.Modal.getInstance(document.getElementById("selectCashierModal")).hide();

    const confirmModal = new bootstrap.Modal(document.getElementById("confirmCheckoutModal"));
    closeAllModals();
    confirmModal.show();
  });
}

export function showCashierSelectionModal() {
  closeAllModals();
  const select = document.getElementById("selectAssignedCashier");
  select.innerHTML = '<option value="" disabled selected hidden>Изберете касиер...</option>';

  const spans = document.querySelectorAll(".register-card.register-active span[data-cashier-id]");
  if (spans.length === 0) {
    showErrorModal("Няма активни касиери на каса.");
    return;
  }

  const unique = new Map();
  spans.forEach(span => {
    const id = span.getAttribute("data-cashier-id");
    const name = span.textContent.replace("Касиер: ", "").trim();
    if (!unique.has(id)) unique.set(id, name);
  });

  unique.forEach((name, id) => {
    const opt = document.createElement("option");
    opt.value = id;
    opt.textContent = name;
    select.appendChild(opt);
  });

  new bootstrap.Modal(document.getElementById("selectCashierModal")).show();
}
