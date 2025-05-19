import { getCart, saveCart } from "./cartService.js";
import { getStoreId } from "../utils/storeUtils.js";
import { showErrorModal } from "../modals/errorModal.js";

export function updateCartCount() {
  const cart = getCart();
  const uniqueNames = new Set(cart.map(item => item.name));
  document.getElementById("cart-count").textContent = uniqueNames.size;
}

export function setupCartIcon() {
  const cartIcon = document.getElementById("cart-icon");
  cartIcon.addEventListener("click", () => {
    renderCartModal();
    const modalElement = document.getElementById("cartModal");
    if (modalElement) new bootstrap.Modal(modalElement).show();
  });
}

export function renderCartModal() {
  const cart = getCart();
  const cartItemsList = document.getElementById("cart-items");
  cartItemsList.innerHTML = "";

  if (cart.length === 0) {
    cartItemsList.innerHTML = '<li class="list-group-item text-center">Количката е празна</li>';
    return;
  }

  let total = 0;

  cart.forEach(item => {
    const li = document.createElement("li");
    li.className = "list-group-item d-flex justify-content-between align-items-center";
    li.innerHTML = `
      <div><strong>${item.name}</strong> – ${item.quantity} бр. – ${item.unitPrice.toFixed(2)} лв.</div>
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-sm btn-outline-secondary btn-decrease" data-name="${item.name}">−</button>
        <button class="btn btn-sm btn-outline-secondary btn-increase" data-name="${item.name}">+</button>
        <button class="btn btn-danger btn-sm btn-remove" data-name="${item.name}">🗑</button>
      </div>`;
    cartItemsList.appendChild(li);
    total += item.unitPrice * item.quantity;
  });

  const totalDiv = document.createElement("div");
  totalDiv.className = "text-end fw-bold mt-3";
  totalDiv.textContent = `Обща сума: ${total.toFixed(2)} лв.`;
  cartItemsList.appendChild(totalDiv);

  setupCartButtons(cartItemsList);
}

function setupCartButtons(container) {
  container.querySelectorAll(".btn-decrease").forEach(btn =>
    btn.addEventListener("click", () => updateQuantity(btn.dataset.name, -1)));

  container.querySelectorAll(".btn-increase").forEach(btn =>
    btn.addEventListener("click", () => updateQuantity(btn.dataset.name, 1)));

  container.querySelectorAll(".btn-remove").forEach(btn =>
    btn.addEventListener("click", () => {
      let cart = getCart();
      cart = cart.filter(p => p.name !== btn.dataset.name);
      saveCart(cart);
      renderCartModal();
      updateCartCount();
    }));
}

async function updateQuantity(name, delta) {
  const cart = getCart();
  const item = cart.find(p => p.name === name);
  if (!item) return;

  const newQuantity = item.quantity + delta;
  if (newQuantity < 1) return;

  try {
    const res = await fetch(`/store/${getStoreId()}/products/checkQuantity?productName=${encodeURIComponent(name)}&quantity=${newQuantity}`, { method: "POST" });
    if (!res.ok) return;
    const data = await res.json();

    if (newQuantity <= data.availableQuantity) {
      item.quantity = newQuantity;
      saveCart(cart);
      renderCartModal();
      updateCartCount();
    }
  } catch (e) {
    console.error("Грешка при обновяване на количество:", e);
  }
}
