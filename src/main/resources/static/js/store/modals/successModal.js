export function showSuccessModal(message) {
  const modalBody = document.getElementById("successModalBody");
  modalBody.textContent = message;

  const modalElement = new bootstrap.Modal(document.getElementById("successModal"));
  modalElement.show();

  setTimeout(() => {
    modalElement.hide();
  }, 2000);
}