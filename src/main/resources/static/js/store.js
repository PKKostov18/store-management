const assignModal = document.getElementById('assignCashierModal');

assignModal?.addEventListener('show.bs.modal', function (event) {
  const button = event.relatedTarget;
  const registerId = button.getAttribute('data-register-id');
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

  const cartIcon = document.getElementById("cart-icon");
  const cartCount = document.getElementById("cart-count");
  const cartItemsList = document.getElementById("cart-items");

  function getCart() {
    const rawCart = localStorage.getItem("cart");
    try {
      const parsed = JSON.parse(rawCart);
      return Array.isArray(parsed) ? parsed : [];
    } catch {
      return [];
    }
  }

  function saveCart(cart) {
    localStorage.setItem("cart", JSON.stringify(cart));
  }

  function updateCartCount() {
    const cart = getCart();
    const uniqueNames = new Set(cart.map(item => item.name));
    cartCount.textContent = uniqueNames.size;
  }

  function removeFromCart(index) {
    const cart = getCart();
    cart.splice(index, 1);
    saveCart(cart);
    updateCartCount();
    renderCartModal();
  }

  function renderCartModal() {
    const cart = getCart();
    cartItemsList.innerHTML = "";

    if (cart.length === 0) {
      cartItemsList.innerHTML = '<li class="list-group-item text-center">Количката е празна</li>';
      return;
    }

    let total = 0;

    cart.forEach((item, index) => {
      const li = document.createElement("li");
      li.className = "list-group-item d-flex justify-content-between align-items-center";
      li.innerHTML = `
        <div>
          <strong>${item.name}</strong> – ${item.quantity} бр. – ${item.unitPrice.toFixed(2)} лв.
        </div>
        <div class="d-flex align-items-center gap-2">
          <button class="btn btn-sm btn-outline-secondary btn-decrease" data-index="${index}">−</button>
          <button class="btn btn-sm btn-outline-secondary btn-increase" data-index="${index}">+</button>
          <button class="btn btn-danger btn-sm btn-remove" data-index="${index}">🗑</button>
        </div>
      `;
      cartItemsList.appendChild(li);
      total += item.unitPrice * item.quantity;
    });

    // Обща сума
    const totalDiv = document.createElement("div");
    totalDiv.className = "text-end fw-bold mt-3";
    totalDiv.textContent = `Обща сума: ${total.toFixed(2)} лв.`;
    cartItemsList.appendChild(totalDiv);

    // Бутоните +/−/🗑
    cartItemsList.querySelectorAll(".btn-decrease").forEach(button => {
      button.addEventListener("click", function () {
        const index = this.getAttribute("data-index");
        const cart = getCart();
        if (cart[index].quantity > 1) {
          cart[index].quantity--;
          saveCart(cart);
          renderCartModal();
          updateCartCount();
        }
      });
    });

    cartItemsList.querySelectorAll(".btn-increase").forEach(button => {
      button.addEventListener("click", function () {
        const index = this.getAttribute("data-index");
        const cart = getCart();
        cart[index].quantity++;
        saveCart(cart);
        renderCartModal();
        updateCartCount();
      });
    });

    cartItemsList.querySelectorAll(".btn-remove").forEach(button => {
      button.addEventListener("click", function () {
        const index = this.getAttribute("data-index");
        removeFromCart(index);
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

    addToCartBtn?.addEventListener("click", () => {
      const productName = card.querySelector(".card-title").textContent.trim();
      const quantity = parseInt(input.value);
      const priceText = card.querySelector(".card-price").textContent.trim(); // напр. "5.99 лв."
      const unitPrice = parseFloat(priceText.replace("лв.", "").trim());

      if (quantity >= 1 && !isNaN(unitPrice)) {
        const cart = getCart();
        cart.push({
          name: productName,
          quantity,
          unitPrice,
          totalPrice: +(unitPrice * quantity).toFixed(2)
        });
        saveCart(cart);
        updateCartCount();
      }
    });
  });

  updateCartCount();
});
