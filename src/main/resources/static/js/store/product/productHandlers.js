import { getCart, saveCart } from "../cart/cartService.js";
import { updateCartCount } from "../cart/cartUI.js";
import { showErrorModal } from "../modals/errorModal.js";
import { getStoreId } from "../utils/storeUtils.js";

export function setupProductHandlers() {
  const cards = document.querySelectorAll(".product-card");

  if (cards.length >= 4) {
    document.querySelector(".product-grid")?.classList.add("has-four-or-more");
  }

  cards.forEach(card => {
    const input = card.querySelector(".quantity-input");
    const btnInc = card.querySelector(".quantity-increase");
    const btnDec = card.querySelector(".quantity-decrease");
    const addBtn = card.querySelector(".btn-cart");

    btnInc?.addEventListener("click", () => input.value = parseInt(input.value) + 1);
    btnDec?.addEventListener("click", () => input.value = Math.max(1, parseInt(input.value) - 1));
    input?.addEventListener("input", () => {
      if (isNaN(parseInt(input.value)) || parseInt(input.value) < 1) input.value = 1;
    });

    addBtn?.addEventListener("click", async () => {
      const quantity = parseInt(input.value);
      const productId = card.getAttribute("data-product-id");
      const name = card.querySelector(".card-title")?.textContent.trim();
      const price = parseFloat(card.querySelector(".card-price")?.textContent.replace("лв.", "").trim());
      const cart = getCart();
      const existing = cart.find(p => p.productId === productId);
      const total = quantity + (existing?.quantity || 0);

      try {
        const res = await fetch(`/store/${getStoreId()}/products/checkQuantity?productName=${encodeURIComponent(name)}&quantity=${total}`, {
          method: "POST"
        });
        if (!res.ok) {
          const err = await res.text();
          showErrorModal(err);
          return;
        }

        if (existing) existing.quantity += quantity;
        else cart.push({ productId, name, quantity, unitPrice: price });

        saveCart(cart);
        updateCartCount();
      } catch (err) {
        showErrorModal("Грешка при проверка на количеството.");
        console.error(err);
      }
    });
  });
}
