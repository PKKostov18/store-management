const assignModal = document.getElementById('assignCashierModal');

assignModal?.addEventListener('show.bs.modal', function (event) {
  const button = event.relatedTarget;
  const registerId = button.getAttribute('data-register-id');
  const form = document.getElementById('assignCashierForm');

  document.getElementById('cashRegisterId').value = registerId;
  form.action = '/cashRegister/' + registerId + '/assign';
});

document.addEventListener("DOMContentLoaded", function () {
  const cartIcon = document.getElementById("cart-icon");
  const cartCount = document.getElementById("cart-count");
  const cartItemsList = document.getElementById("cart-items");

  function getCart() {
    const storeId = getStoreId();
    return JSON.parse(localStorage.getItem(`cart_${storeId}`)) || [];
  }

  function saveCart(cart) {
    const storeId = getStoreId();
    localStorage.setItem(`cart_${storeId}`, JSON.stringify(cart));
  }

  function updateCartCount() {
    const cart = getCart();
    const uniqueNames = new Set(cart.map(item => item.name));
    cartCount.textContent = uniqueNames.size;
  }

  function showErrorModal(message) {
    const errorModalBody = document.getElementById('errorModalBody');
    errorModalBody.textContent = message;

    const errorModalElement = document.getElementById('errorModal');
    const errorModal = new bootstrap.Modal(errorModalElement);
    errorModal.show();
  }

  function getStoreId() {
    return document.body.getAttribute("data-store-id");
  }

  function renderCartModal() {
    const cart = getCart();
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
        <div>
          <strong>${item.name}</strong> – ${item.quantity} бр. – ${item.unitPrice.toFixed(2)} лв.
        </div>
        <div class="d-flex align-items-center gap-2">
          <button class="btn btn-sm btn-outline-secondary btn-decrease" data-name="${item.name}">−</button>
          <button class="btn btn-sm btn-outline-secondary btn-increase" data-name="${item.name}">+</button>
          <button class="btn btn-danger btn-sm btn-remove" data-name="${item.name}">🗑</button>
        </div>
      `;
      cartItemsList.appendChild(li);
      total += item.unitPrice * item.quantity;
    });

    const totalDiv = document.createElement("div");
    totalDiv.className = "text-end fw-bold mt-3";
    totalDiv.textContent = `Обща сума: ${total.toFixed(2)} лв.`;
    cartItemsList.appendChild(totalDiv);

    // Добавяне на събития
    cartItemsList.querySelectorAll(".btn-decrease").forEach(button => {
      button.addEventListener("click", () => {
        const cart = getCart();
        const item = cart.find(p => p.name === button.dataset.name);
        if (item) {
          item.quantity = Math.max(1, item.quantity - 1);
          saveCart(cart);
          renderCartModal();
          updateCartCount();
        }
      });
    });

cartItemsList.querySelectorAll(".btn-increase").forEach(button => {
  button.addEventListener("click", async () => {
    const cart = getCart();
    const productName = button.dataset.name;
    const storeId = getStoreId();

    const item = cart.find(p => p.name === productName);
    if (!item) return;

    const nextQuantity = item.quantity + 1;

    try {
      const url = `/store/${storeId}/products/checkQuantity?productName=${encodeURIComponent(productName)}&quantity=${nextQuantity}`;

      const response = await fetch(url, {
        method: "POST"
      });

      if (!response.ok) {
        // Логваме за отстраняване на грешки, но не правим нищо повече
        console.log("Грешка при проверка на наличността:", response.status);
        return;
      }

      const data = await response.json();

      // Предполага се, че data.availableQuantity е числото с наличното количество
      if (nextQuantity <= data.availableQuantity) {
        item.quantity = nextQuantity;
        saveCart(cart);
        renderCartModal();
        updateCartCount();
      } else {
        // Не правим нищо, просто не увеличаваме
        console.log(`Няма достатъчно наличност за ${productName}: има ${data.availableQuantity}, искаш ${nextQuantity}`);
      }
    } catch (error) {
      console.error("Грешка при заявката за наличност:", error);
    }
  });
});




    cartItemsList.querySelectorAll(".btn-remove").forEach(button => {
      button.addEventListener("click", () => {
        let cart = getCart();
        cart = cart.filter(p => p.name !== button.dataset.name);
        saveCart(cart);
        renderCartModal();
        updateCartCount();
      });
    });
  }

  cartIcon.addEventListener("click", function () {
    renderCartModal();
    const modalElement = document.getElementById("cartModal");
    if (modalElement) {
      const modal = new bootstrap.Modal(modalElement);
      modal.show();
    } else {
      console.error("Модалът не е намерен!");
    }
  });

  const cards = document.querySelectorAll(".product-card");
  cards.forEach(card => {
    const btnIncrease = card.querySelector(".quantity-increase");
    const btnDecrease = card.querySelector(".quantity-decrease");
    const input = card.querySelector(".quantity-input");
    const addToCartBtn = card.querySelector(".btn-cart");

    btnIncrease?.addEventListener("click", () => {
      input.value = parseInt(input.value) + 1;
    });

    btnDecrease?.addEventListener("click", () => {
      const value = parseInt(input.value);
      if (value > 1) {
        input.value = value - 1;
      }
    });

    input?.addEventListener("input", () => {
      const value = parseInt(input.value);
      if (isNaN(value) || value < 1) {
        input.value = 1;
      }
    });

    addToCartBtn?.addEventListener("click", async () => {
      const productName = card.querySelector(".card-title").textContent.trim();
      const quantity = parseInt(input.value);
      const unitPrice = parseFloat(card.querySelector(".card-price").textContent.replace("лв.", "").trim());
      const storeId = getStoreId();

      const cart = getCart();
      const existing = cart.find(item => item.name === productName);
      const totalRequested = quantity + (existing ? existing.quantity : 0);

      try {
        const response = await fetch(`/store/${storeId}/products/checkQuantity?productName=${encodeURIComponent(productName)}&quantity=${totalRequested}`, {
          method: "POST",
        });

        if (!response.ok) {
          const errorText = await response.text();
          showErrorModal(errorText);
          return;
        }

        if (existing) {
          existing.quantity += quantity;
        } else {
          cart.push({ name: productName, quantity, unitPrice });
        }

        saveCart(cart);
        updateCartCount();
      } catch (error) {
        showErrorModal("Възникна грешка при проверка на количеството.");
        console.error(error);
      }
    });


  });

  updateCartCount();
});
