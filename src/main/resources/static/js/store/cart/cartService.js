import { getStoreId } from "../utils/storeUtils.js";

export function getCart() {
  return JSON.parse(localStorage.getItem(`cart_${getStoreId()}`)) || [];
}

export function saveCart(cart) {
  localStorage.setItem(`cart_${getStoreId()}`, JSON.stringify(cart));
}

export function clearCart() {
  localStorage.removeItem(`cart_${getStoreId()}`);
}
