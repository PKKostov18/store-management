export function closeAllModals() {
  document.querySelectorAll(".modal.show").forEach(modalEl => {
    bootstrap.Modal.getInstance(modalEl)?.hide();
  });
}
